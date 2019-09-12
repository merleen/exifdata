package exifdata;

import exifdata.exif.files.ExifFiles;
import exifdata.exif.renamer.ExifRenamer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExifDataApp {

    private static final Logger l = LoggerFactory.getLogger( ExifDataApp.class );

    public static void main( String[] args ) {

        if( args.length != 1 ) {
            throw new IllegalArgumentException( "args length = 1 required, the path to the directory to be read" );
        }

        String activeDir = args[ 0 ];
        // just work with \
        if( activeDir.indexOf( '/' ) > -1 ) {
            activeDir = activeDir.replace( '/', '\\' );
        }
        if( !activeDir.endsWith( "\\" ) ) {
            activeDir += "\\";
        }

        ExifFiles.setActiveDir( activeDir );

        l.info( "read files" );
        ExifFiles exifFiles = new ExifFiles();
        exifFiles.processFiles();
        exifFiles.createOutputDir();

        l.info( "rename all files" );
        ExifRenamer.processRenamingForAllFileTypes( exifFiles.getAllFilesInDir(), exifFiles.getFoundFileTypes() );
    }
}
