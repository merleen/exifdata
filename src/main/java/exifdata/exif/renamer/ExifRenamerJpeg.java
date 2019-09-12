package exifdata.exif.renamer;


import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifReader;
import exifdata.exif.imagedata.ExifData;
import exifdata.exif.imagedata.ExifDataJpg;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ExifRenamerJpeg extends ExifRenamer {

    private final Logger l = LoggerFactory.getLogger( ExifRenamerJpeg.class );

    // We are only interested in handling
    private final Iterable<JpegSegmentMetadataReader> readers = Arrays.asList( new ExifReader() );


    ExifDataJpg exifDataJpg = new ExifDataJpg();

    public void renameFiles( List<File> filesByType ) {

        for( File currentFile : filesByType ) {

            this.l.info( "================" );
            this.l.info( "  file: {}", currentFile.getName() );

            try {
                Metadata metadata = JpegMetadataReader.readMetadata( currentFile, this.readers );


                Map<Integer, String> tags = this.exifDataJpg.getTagsForCurrentFile(
                        metadata.getDirectories()
                        , ExifData.DATE_TIME_TAKEN_JPEG, ExifData.DATE_TIME_TAKEN_IMG );

                String specificTagValue = this.exifDataJpg.getSpecificTagValue( currentFile, tags );
                String newFileName = this.exifDataJpg.createNewFileName( specificTagValue, currentFile.getAbsolutePath() );

                FileUtils.moveFile( currentFile, FileUtils.getFile( newFileName ) );

                String s = "";
            }
            catch( JpegProcessingException | IOException e ) {
                this.l.error( e.getMessage(), e );
            }

            this.l.info( "================" );
        }
    }
}
