package io;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Created by Fabrice on 19/09/16.
 */
public class IOTools {

    public static Map<String, List<Path>> indexPDBFileInFolder(String pathToDividedPDBFolder) {

        Map<String, List<Path>> indexPDBFileInFolder = null;
        try {
            FileListingVisitorForCifGzFiles fileListingVisitor = new FileListingVisitorForCifGzFiles(pathToDividedPDBFolder);
            indexPDBFileInFolder = fileListingVisitor.getIndexFiles();

        } catch (IOException e) {
            System.out.println("FAILURE: in makeAListOfInputPDBFilesRecursivelyFromInputControllerFolder");
            //e.printStackTrace();
        }
        return indexPDBFileInFolder;
    }
}
