package exifdata.exif.imagedata;

import com.drew.metadata.Directory;
import com.drew.metadata.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ExifData {

    Logger l = LoggerFactory.getLogger( ExifData.class );

    public static final int DATE_TIME_TAKEN_JPEG = 306;

    public static final int DATE_TIME_TAKEN_IMG = 36868;

    Map<Integer, String> tags = new HashMap<>();

    /**
     * for getting the tag value depending on file-type
     *
     * @param currentFile the file currently processed
     * @param tags        the exif-information
     * @return the tag-value for creating the new file-name
     */
    public abstract String getSpecificTagValue( File currentFile, Map<Integer, String> tags );


    public String createNewFileName( String tagValue, String absolutePath ) {

        String ending = absolutePath.substring( absolutePath.lastIndexOf( '.' ) );
        String path;
        if( absolutePath.lastIndexOf( "/" ) > 1 ) {

            path = absolutePath.substring( 0, absolutePath.lastIndexOf( "/" ) + 1 );
        }
        else {
            path = absolutePath.substring( 0, absolutePath.lastIndexOf( "\\" ) + 1 );
            path = path.replace( "\\", "/" );
        }

        // build new name
        String newFileName = tagValue.replace( ":", "" );
        newFileName = newFileName.replace( " ", "_" );
        newFileName += ending;
        newFileName = path + newFileName;

        File newFile = new File( newFileName );
        if( newFile.exists() ) {
            newFileName = renameFileWithNumber( newFileName );
        }

        this.l.info( "    new file-name: '{}'", newFileName );
        return newFileName;
    }


    protected String getTagValue( Integer... tagIDs ) {

        for( Integer tagID : tagIDs ) {
            if( this.tags.get( tagID ) != null ) {
                return this.tags.get( tagID );
            }
        }

        return "";
    }

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

    public String renameFileWithNumber( String filenameForRenaming ) {

        String filename = filenameForRenaming.substring( filenameForRenaming.lastIndexOf( "." ) - 15 );
        String path = filenameForRenaming.replace( filename, "" );
        String ending = filename.substring( filename.lastIndexOf( '.' ) );
        filename = filename.replace( ending, "" );

        // file already renamed
        if( filename.indexOf( '-' ) >= 0 ) {

            Integer integer = Integer.valueOf( filename.substring( filename.lastIndexOf( '-' ) + 1 ) );
            String substring = filename.substring( 0, filename.lastIndexOf( '-' ) );
            filename = substring + integer + 1;

            filename = path + filename + ending;
            if( new File( filename ).exists() ) {
                renameFileWithNumber( filename );
            }
        }
        // first renaming
        else {
            filename += "-1";
        }

        return path + filename + ending;
    }
}
