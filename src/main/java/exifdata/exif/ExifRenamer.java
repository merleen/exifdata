package exifdata.exif;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifReader;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExifRenamer {

    private final Logger l = LoggerFactory.getLogger( ExifRenamer.class );

    ExifData exifData = new ExifData();

    public void renameFiles( List<File> filesByType, Integer... requiredTypes ) throws ParseException {

        for( File currentFile : filesByType ) {

            this.l.info( "================" );
            this.l.info( "  file: {}", currentFile.getName() );

            try {
                Metadata metadata = JpegMetadataReader.readMetadata( currentFile, this.readers );

                Map<Integer, String> tags = this.exifData.getTagsForCurrentFile( metadata.getDirectories(), requiredTypes );

                String specificTagValue = this.exifData.getSpecificTagValue( currentFile, tags );
                String newFileName = this.exifData.createNewFileName( specificTagValue, currentFile.getAbsolutePath() );

                FileUtils.moveFile( currentFile, FileUtils.getFile( newFileName ) );
            }
            catch( JpegProcessingException | IOException e ) {
                this.l.error( e.getMessage(), e );
            }

            this.l.info( "================" );
        }
    }

    // We are only interested in handling
    final Iterable<JpegSegmentMetadataReader> readers = Collections.singletonList( new ExifReader() );

    public void processRenamingForAllFileTypes( List<File> filesInDir, List<String> foundFileTypes ) throws ParseException {

        for( String fileType : foundFileTypes ) {

            this.l.info( "process renaming for {}", fileType );

            if( "jpeg".equalsIgnoreCase( fileType ) || "jpg".equalsIgnoreCase( fileType ) ) {

                // get renamer by type
                ExifRenamer exifRenamer = new ExifRenamer();
                List<File> filesByType = exifRenamer.getFilesByType( filesInDir, "jpeg", "jpg" );

                // rename files by type
                exifRenamer.renameFiles( filesByType
                        , ExifData.DATE_TIME_TAKEN_JPEG, ExifData.DATE_TIME_TAKEN_IMG, ExifData.DATE_TIME_CREATED );
            }
            else if( "mp4".equalsIgnoreCase( fileType ) ) {

            }
        }
    }


    private List<File> getFilesByType( List<File> fileList, String... types ) {

        List<File> filesByType = new ArrayList<>();

        for( String type : types ) {

            filesByType.addAll(
                    fileList.stream()
                            .filter( f ->
                                    f.getName().substring( f.getName().lastIndexOf( '.' ) + 1 ).equalsIgnoreCase( type ) )
                            .collect( Collectors.toList() )
            );
        }

        return filesByType;
    }
}
