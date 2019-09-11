package exifdata.exifrenamer;


import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ExifRenamerJpeg extends ExifRenamer {

    private static Logger l = LoggerFactory.getLogger( ExifRenamerJpeg.class );


    public void renameFiles( List<File> filesByType ) {

        // We are only interested in handling
        Iterable<JpegSegmentMetadataReader> readers = Arrays.asList(new ExifReader());

        for( File file : filesByType ) {

            l.info( "================" );
            l.info( "  file: {}", file.getName() );

            try {

                Metadata metadata = JpegMetadataReader.readMetadata( file, readers );

                Map<Integer, String> tags = getTags( metadata.getDirectories(), ExifRenamer.DATE_TIME_TAKEN_JPEG );
            }
            catch( JpegProcessingException e ) {

                l.error( e.getMessage(), e );
            }
            catch( IOException e ) {
                l.error( e.getMessage(), e );
            }

            l.info( "================" );
        }
    }
}
