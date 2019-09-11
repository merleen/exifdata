package exifdata.exif.files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExifFiles {

    private static final Logger l = LoggerFactory.getLogger( ExifFiles.class );

    private List<File> allFilesInDir;

    private List<String> foundFileTypes;

    /**
     * collect file-types by file-name-ending
     *
     * @param filesInDir all files in the required directory
     * @return a list with distinct file-types
     */
    public List<String> getFileTypesDistinct( List<File> filesInDir ) {

        List<String> foundFileTypes = new ArrayList<>();

        for( File file : filesInDir ) {
            if( file.isFile() ) {

                String name = file.getName();
                String filetype = name.substring( name.lastIndexOf( "." ) + 1 );
                if( !foundFileTypes.contains( filetype ) ) {
                    foundFileTypes.add( filetype );
                }
            }
        }

        l.info( "  found file types: {}", foundFileTypes.toString() );

        return foundFileTypes;
    }

    /**
     * reads all files in given directory
     *
     * @param dir the directory to be read
     * @return all files from directory
     */
    public List<File> readAllFilesInDir( String dir ) {

        File directory = new File( dir );
        List<File> files = Arrays.asList( directory.listFiles() );

        l.info( "  found {} files", files.size() );

        return files;
    }

    /**
     * processes all exif-files in the given directory
     *
     * @param activeDir the directory to be processed for exif-files
     */
    public void processFiles( String activeDir ) {

        // read all files in dir
        l.info( "read all files from {}", activeDir );
        this.allFilesInDir = readAllFilesInDir( activeDir );

        // all available file-types distinct
        l.info( "get file-types distinct" );
        this.foundFileTypes = getFileTypesDistinct( this.allFilesInDir );
    }

    public List<File> getAllFilesInDir() {

        return this.allFilesInDir;
    }

    public List<String> getFoundFileTypes() {

        return this.foundFileTypes;
    }
}
