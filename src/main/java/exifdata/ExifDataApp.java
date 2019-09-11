package exifdata;

import exifdata.exif.files.ExifFiles;
import exifdata.exif.renamer.ExifRenamer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class ExifDataApp {

    private static final Logger l = LoggerFactory.getLogger( ExifDataApp.class );

    public static void main( String[] args ) {

        if( args.length != 1 ) {
            throw new IllegalArgumentException( "args length = 1 required, the path to the directory to be read" );
        }

        String activeDir = args[ 0 ];
        if( !activeDir.endsWith( "/" ) ) {
            activeDir += "/";
        }

        l.info( "read files" );
        ExifFiles exifFiles = new ExifFiles();
        exifFiles.processFiles( activeDir );

        String outputDirName = activeDir + "output/";
        l.info( "create output-directory {}", outputDirName );
        File outputDir = new File( outputDirName );
        outputDir.mkdir();
        ExifRenamer.setOutputDir( outputDir );

        l.info( "rename all files" );
        ExifRenamer.processRenamingForAllFileTypes( exifFiles.getAllFilesInDir(), exifFiles.getFoundFileTypes() );
    }
}
