package exifdata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileReader {

    private static Logger l = LoggerFactory.getLogger( FileReader.class );

    public static List<String> getFileTypesDistinct( List<File> filesInDir ) {

        List<String> foundFileTypes = new ArrayList<>();

        for( File file : filesInDir ) {
            String name = file.getName();
            String filetype = name.substring( name.lastIndexOf( "." ) + 1 );
            if( !foundFileTypes.contains( filetype ) ) {
                foundFileTypes.add( filetype );
            }
        }

        l.info( "  found file types: {}", foundFileTypes.toString() );

        return foundFileTypes;
    }

    public static List<File> readAllFilesInDir( String dir ) {

        File directory = new File( dir );
        List<File> files = Arrays.asList( directory.listFiles() );

        l.info( "  found {} files", files.size() );

        return files;
    }
}
