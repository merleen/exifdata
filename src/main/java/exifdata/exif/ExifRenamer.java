package exifdata.exif;

import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.mp4.Mp4MetadataReader;
import com.drew.metadata.Metadata;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExifRenamer {

    private final Logger l = LoggerFactory.getLogger( ExifRenamer.class );

    private List<File> filesInDir = null;

    ExifData exifData = new ExifData();

    public ExifRenamer( List<File> filesInDir ) {

        this.filesInDir = filesInDir;
    }

    public ExifRenamer() {

    }

    public void renameFiles( List<File> filesByType, String fileType, Integer... requiredTypes ) throws ParseException {

        for( File currentFile : filesByType ) {

            this.l.info( "================" );
            this.l.info( "  file: {}", currentFile.getName() );

            try {
                Metadata metadata = getMetadataByType( fileType, currentFile );

                if( metadata == null ) {
                    System.exit( -1 );
                }

                String newFileName = "";

                if( currentFile.getName().startsWith( "IMG-" ) ) {
                    newFileName = this.exifData.createNewFileName(
                            currentFile.getName().substring( 4, currentFile.getName().lastIndexOf( "." ) )
                            , currentFile.getAbsolutePath() );
                }
                else {
                    Map<Integer, String> tags = this.exifData.getTagsForCurrentFile( metadata.getDirectories(), requiredTypes );

                    String specificTagValue = this.exifData.getSpecificTagValue( currentFile, tags );
                    newFileName = this.exifData.createNewFileName( specificTagValue, currentFile.getAbsolutePath() );
                }
                FileUtils.moveFile( currentFile, FileUtils.getFile( newFileName ) );
                Thread.sleep( 300 );
            }
            catch( IOException | InterruptedException e ) {
                this.l.error( e.getMessage(), e );
            }

            this.l.info( "================" );
        }
    }

    private Metadata getMetadataByType( String fileType, File currentFile ) {

        try {
            if( "jpeg".equals( fileType ) || "jpg".equalsIgnoreCase( fileType ) ) {
                return JpegMetadataReader.readMetadata( currentFile );
            }
            else if( "mp4".equals( fileType ) ) {
                return Mp4MetadataReader.readMetadata( currentFile );
            }
            else if( "3gp".equals( fileType ) ) {
                return Mp4MetadataReader.readMetadata( currentFile );
            }
            else if( "mov".equals( fileType ) ) {
                return Mp4MetadataReader.readMetadata( currentFile );
            }
            else {
                throw new IOException( "unknown filetype: " + fileType );
            }
        }
        catch( ImageProcessingException | IOException e ) {
            this.l.error( e.getMessage(), e );
        }
        return null;
    }

    public void processRenamingForAllFileTypes( List<File> filesInDir, List<String> foundFileTypes ) throws ParseException {

        ExifRenamer exifRenamer = new ExifRenamer( filesInDir );

        for( String fileType : foundFileTypes ) {

            this.l.info( "process renaming for {}", fileType );

            if( "jpeg".equalsIgnoreCase( fileType ) || "jpg".equalsIgnoreCase( fileType ) ) {

                List<File> filesByType = exifRenamer.getFilesByType( "jpeg", "jpg" );

                // rename files by type
                exifRenamer.renameFiles( filesByType
                        , fileType
                        , ExifData.DATE_TIME_TAKEN_JPEG
                        , ExifData.DATE_TIME_TAKEN_IMG
                        , ExifData.DATE_TIME_IMAGE_CREATED );
            }
            else if( "mp4".equalsIgnoreCase( fileType ) ) {

                List<File> filesByType = exifRenamer.getFilesByType( "mp4" );

                exifRenamer.renameFiles( filesByType
                        , fileType
                        , ExifData.DATE_TIME_TAKEN_JPEG
                        , ExifData.DATE_TIME_TAKEN_IMG
                        , ExifData.DATE_TIME_IMAGE_CREATED );
            }
            else if( "3gp".equalsIgnoreCase( fileType ) ) {

                List<File> filesByType = exifRenamer.getFilesByType( "3gp" );

                exifRenamer.renameFiles( filesByType
                        , fileType
                        , ExifData.DATE_TIME_TAKEN_JPEG
                        , ExifData.DATE_TIME_TAKEN_IMG
                        , ExifData.DATE_TIME_IMAGE_CREATED );
            }
            else if( "mov".equalsIgnoreCase( fileType ) ) {

                List<File> filesByType = exifRenamer.getFilesByType( "mov" );

                exifRenamer.renameFiles( filesByType
                        , fileType
                        , ExifData.DATE_TIME_TAKEN_JPEG
                        , ExifData.DATE_TIME_TAKEN_IMG
                        , ExifData.DATE_TIME_IMAGE_CREATED );
            }
        }
    }


    private List<File> getFilesByType( String... types ) {

        List<File> filesByType = new ArrayList<>();

        for( String type : types ) {

            filesByType.addAll(
                    this.filesInDir.stream()
                            .filter( f ->
                                    f.getName().substring( f.getName().lastIndexOf( '.' ) + 1 ).equalsIgnoreCase( type ) )
                            .collect( Collectors.toList() )
            );
        }

        return filesByType;
    }
}
