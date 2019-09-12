package exifdata.exif.imagedata;

import com.drew.metadata.Directory;
import com.drew.metadata.Tag;
import exifdata.exif.files.ExifFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ExifData {

    private final Logger l = LoggerFactory.getLogger( ExifData.class );

    public static final int DATE_TIME_TAKEN_JPEG = 306;

    public static final int DATE_TIME_TAKEN_IMG = 36868;

    private final Map<Integer, String> tags = new HashMap<>();

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

        this.l.info( "    new file-name: '{}'", newFileName );
        return newFileName;
    }


    String getTagValue( Integer... tagIDs ) {

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
}
