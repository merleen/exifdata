package exifdata.exif.renamer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ExifRenamer {

    private static final Logger l = LoggerFactory.getLogger( ExifRenamer.class );

    protected static File outputDir;


    public abstract void renameFiles( List<File> filesByType );


    public static void setOutputDir( File outputDir ) {

        ExifRenamer.outputDir = outputDir;
    }

    public static void processRenamingForAllFileTypes( List<File> filesInDir, List<String> foundFileTypes ) {

        for( String fileType : foundFileTypes ) {

            l.info( "process renaming for {}", fileType );

            if( "jpeg".equalsIgnoreCase( fileType ) || "jpg".equalsIgnoreCase( fileType ) ) {

                // get renamer by type
                ExifRenamer renamerJpeg = new ExifRenamerJpeg();
                List<File> filesByType = renamerJpeg.getFilesByType( filesInDir, "jpeg", "jpg" );

                // rename files by type
                renamerJpeg.renameFiles( filesByType );
            }
        }
    }


    public List<File> getFilesByType( List<File> fileList, String... types ) {

        List<File> filesByType = new ArrayList();

        for( String type : types ) {

            filesByType.addAll(
                    fileList.stream()
                            .filter( f ->
                                    f.getName().substring( f.getName().lastIndexOf( '.' ) + 1 ).equalsIgnoreCase( type ) )
                            .collect( Collectors.toList() )
            );
        }

        return filesByType;
    }
}
