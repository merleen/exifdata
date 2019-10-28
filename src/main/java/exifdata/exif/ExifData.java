package exifdata.exif;

import com.drew.metadata.Directory;
import com.drew.metadata.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExifData {

    private final Logger l = LoggerFactory.getLogger( ExifData.class );

    public static final int DATE_TIME_TAKEN_JPEG = 306;

    public static final int DATE_TIME_IMAGE_CREATED = 3;

    public static final int DATE_TIME_TAKEN_IMG = 36868;

    private final Map<Integer, String> tags = new HashMap<>();

    private static final Map<String, String> months = new HashMap<>();

    private final SimpleDateFormat FORMAT = new SimpleDateFormat( "yyyyMMdd HHmmss" );

    static {
        months.put( "Jan", "01" );
        months.put( "Feb", "02" );
        months.put( "Mär", "03" );
        months.put( "Apr", "04" );
        months.put( "Mai", "05" );
        months.put( "Jun", "06" );
        months.put( "Jul", "07" );
        months.put( "Aug", "08" );
        months.put( "Sep", "09" );
        months.put( "Okt", "10" );
        months.put( "Nov", "11" );
        months.put( "Dez", "12" );
    }

    /**
     * for getting the tag value
     *
     * @param currentFile the file currently processed
     * @param tags        the exif-information
     * @return the tag-value for creating the new file-name
     */
    public String getSpecificTagValue( File currentFile, Map<Integer, String> tags ) throws IOException,
            ParseException {

        // found no tag-information
        if( tags.isEmpty() ) {
            return currentFile.getAbsolutePath();
        }
        else {
            return getOldest( Collections.list( Collections.enumeration( tags.values() ) ) );
        }
    }


    public String createNewFileName( String tagValue, String absolutePath ) {

        String ending = absolutePath.substring( absolutePath.lastIndexOf( '.' ) );
        String path = ExifFiles.getActiveDir();

        // build new name
        String newFileName = tagValue.replace( ":", "" );
        newFileName = newFileName.replace( " ", "_" );
        newFileName += ending;
        newFileName = path + newFileName;

        File newFile = new File( newFileName );
        if( newFile.exists() ) {
            newFileName = renameFileWithNumber( newFileName );
        }

        this.l.info( "    new: '{}'", newFileName );
        return newFileName;
    }


//    List<String> getTagValue( Integer... tagIDs ) {
//
//        List<String> list = new ArrayList<>();
//
//        for( Integer tagID : tagIDs ) {
//            if( this.tags.get( tagID ) != null ) {
//                list.add( this.tags.get( tagID ) );
//            }
//        }
//
//        return list;
//    }

    public Map<Integer, String> getTagsForCurrentFile( Iterable<Directory> directories, Integer... requiredTags ) {

        List<Integer> requiredTagsList = Arrays.asList( requiredTags );

        for( Directory directory : directories ) {

            for( Tag tag : directory.getTags() ) {

                if( requiredTagsList.contains( tag.getTagType() ) ) {
                    this.tags.put( tag.getTagType(), tag.getDescription() );
                }
            }
        }

        return this.tags;
    }

    private String renameFileWithNumber( String filenameForRenaming ) {

        String path = ExifFiles.getActiveDir();

        String filename =
                filenameForRenaming.replace( path, "" );
        String ending = filename.substring( filename.lastIndexOf( '.' ) );
        filename = filename.replace( ending, "" );

        // file already renamed
        if( filename.indexOf( '-' ) >= 14 ) {

            Integer integer = Integer.valueOf( filename.substring( filename.lastIndexOf( '-' ) + 1 ) );
            String datetimestamp = filename.substring( 0, filename.lastIndexOf( '-' ) );
            filename = datetimestamp + "-" + ( integer + 1 );
        }
        // first renaming
        else {
            filename += "-1";
        }

        String completeFilename = path + filename + ending;

        if( new File( completeFilename ).exists() ) {
            return renameFileWithNumber( completeFilename );
        }

        return completeFilename;
    }

    /**
     * looks for the oldest date and formats it in proper format
     *
     * @param tagValue
     * @return
     * @throws IOException
     * @throws ParseException
     */
    private String getOldest( List<String> tagValue ) throws IOException, ParseException {

        List<Date> dates = new ArrayList<>();

        for( String tv : tagValue ) {
            // 2017:08:13 11:29:56
            if( tv.startsWith( "2" ) ) {

                dates.add( this.FORMAT.parse( tv.replace( ":", "" ) ) );
            }
            // Di Nov 07 17:53:27 +01:00 2017
            else {
                String[] split = tv.split( " " );

                String m = months.get( split[ 1 ] );
                if( m == null ) {
                    throw new IOException( "could not get number for month: " + split[ 1 ] );
                }

                dates.add( this.FORMAT.parse( split[ 5 ] + m + split[ 2 ] + " " + split[ 3 ].replace( ":", "" ) ) );
            }
        }

        return this.FORMAT.format( Collections.min( dates ) ).replace( " ", "_" );
    }
}
