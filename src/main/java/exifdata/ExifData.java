package exifdata;

import exifdata.exifrenamer.ExifRenamer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class ExifData {

    private static Logger l = LoggerFactory.getLogger( ExifData.class );

    public static void main( String[] args ) {

        if( args.length != 1 ) {
            throw new IllegalArgumentException( "args length = 1 required, the path to the directory to be read" );
        }

        // read all files in dir
        l.info( "read all files in {}", args[0] );
        List<File> filesInDir = FileReader.readAllFilesInDir( args[ 0 ] );

        // all available file-types distinct
        l.info( "get file-types distinct" );
        List<String> foundFileTypes = FileReader.getFileTypesDistinct(filesInDir);

        l.info( "rename all files" );
        ExifRenamer.processRenamingForAllFileTypes(filesInDir, foundFileTypes);
    }
}
