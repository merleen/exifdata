package exifdata.exif;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExifFiles {

    private static final Logger l = LoggerFactory.getLogger( ExifFiles.class );

    private File outputDir;

    private static String activeDir;

    private List<File> allFilesInDir;

    private List<String> foundFileTypes = new ArrayList<>();

    /**
     * collect file-types by file-name-ending
     *
     * @return a list with distinct file-types
     */
    private List<String> getFileTypesDistinct() {

        for( File file : this.allFilesInDir ) {
            if( file.isFile() ) {

                String name = file.getName();
                String filetype = name.substring( name.lastIndexOf( '.' ) + 1 );
                if( !this.foundFileTypes.contains( filetype ) ) {
                    this.foundFileTypes.add( filetype );
                }
            }
        }

        l.info( "  found file types: {}", this.foundFileTypes );

        return this.foundFileTypes;
    }

    /**
     * reads all files in given directory
     *
     * @param dir the directory to be read
     * @return all files from directory
     */
    private List<File> readAllFilesInDir( String dir ) {

        File directory = new File( dir );
        List<File> files = Arrays.asList( directory.listFiles() );

        l.info( "  found {} files", files.size() );

        return files;
    }

    /**
     * processes all exif-files in the given directory</br>
     * Note: this does not contain renaming, only collect files and types
     */
    public void preProcessFiles() {

        // read all files in dir
        l.info( "read all files from {}", activeDir );
        this.allFilesInDir = readAllFilesInDir( activeDir );

        // all available file-types distinct
        l.info( "get file-types distinct" );
        this.foundFileTypes = getFileTypesDistinct();
    }

    public List<File> getAllFilesInDir() {

        return this.allFilesInDir;
    }

    public List<String> getFoundFileTypes() {

        return this.foundFileTypes;
    }

    public void createOutputDir() {

        String outputDirName = activeDir + "output\\";
        l.info( "  create output-directory {}", outputDirName );
        this.outputDir = new File( outputDirName );
        if( !this.outputDir.exists() && !( this.outputDir.mkdir() ) ) {
            throw new IllegalArgumentException( "could not create output-dir: " + outputDirName );
        }
    }

    public File getOutputDir() {

        return this.outputDir;
    }

    public static String getActiveDir() {

        return activeDir;
    }

    public static void setActiveDir( String activeDir ) {

        ExifFiles.activeDir = activeDir;
    }
}
