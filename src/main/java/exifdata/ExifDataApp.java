package exifdata;

import exifdata.exif.ExifFiles;
import exifdata.exif.ExifRenamer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;

public class ExifDataApp {

    private static final Logger l = LoggerFactory.getLogger( ExifDataApp.class );

    public static void main( String[] args ) throws ParseException {

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
        exifFiles.preProcessFiles();

        l.info( "rename all files" );
        ExifRenamer exifRenamer = new ExifRenamer();
        exifRenamer.processRenamingForAllFileTypes( exifFiles.getAllFilesInDir(), exifFiles.getFoundFileTypes() );
    }
}
