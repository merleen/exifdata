package exifdata.exif.imagedata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;

public class ExifDataJpg extends ExifData {

    Logger l = LoggerFactory.getLogger( ExifDataJpg.class );

    @Override
    public String getSpecificTagValue( File currentFile, Map<Integer, String> tags ) {

        String tagValue = getTagValue( ExifData.DATE_TIME_TAKEN_JPEG, ExifData.DATE_TIME_TAKEN_IMG );

        // found no tag-information
        if( tagValue.isEmpty() ) {
            this.l.debug( "    no tag-value found, return current file-name" );
            return currentFile.getAbsolutePath();
        }

        this.l.debug( "    tag-value: '{}'", tagValue );

        return tagValue;
    }
}
