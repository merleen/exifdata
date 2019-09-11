package exifdata.exifrenamer;

import com.drew.metadata.Directory;
import com.drew.metadata.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class ExifRenamer {

    private static Logger l = LoggerFactory.getLogger( ExifRenamer.class );

    public static final int DATE_TIME_TAKEN_JPEG = 306;

    public abstract void renameFiles( List<File> filesByType );

    public static Map<Integer,String> getTags( Iterable<Directory> directories, Integer... requiredTags ) {

        Map<Integer, String> foundTags = new HashMap<>();

        List<Integer> requiredTagsList = Arrays.asList( requiredTags );

        for( Directory directory : directories ) {

            for( Tag tag : directory.getTags() ) {

                if( requiredTagsList.contains( tag.getTagType() ) ) {
                    foundTags.put( tag.getTagType(), tag.getDescription() );
                }
            }
        }

        return foundTags;
    }

    public static void processRenamingForAllFileTypes( List<File> filesInDir, List<String> foundFileTypes ) {

        for( String fileType : foundFileTypes ) {

            l.info( "process renaming for {}", fileType );

            if( "jpeg".equalsIgnoreCase( fileType ) || "jpg".equalsIgnoreCase( fileType ) ) {

                // get renamer by type
                ExifRenamer renamerJpeg = new ExifRenamerJpeg();
                List<File> filesByType = renamerJpeg.getFilesByType(filesInDir, "jpeg", "jpg");

                // rename files by type
                renamerJpeg.renameFiles(filesByType);
            }
        }
    }


    public List<File> getFilesByType( List<File> fileList, String... types ) {

        List<File> filesByType = new ArrayList();

        for( String type : types ) {

            filesByType.addAll(
                fileList.stream()
                    .filter( f ->
                        f.getName().substring( f.getName().lastIndexOf( "." ) + 1 ).equalsIgnoreCase( type ) )
                    .collect( Collectors.toList() )
            );
        }

        return filesByType;
    }
}
