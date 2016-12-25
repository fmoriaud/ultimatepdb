package io;

import convertformat.AdapterBioJavaStructure;
import convertformat.ExceptionInConvertFormat;
import mystructure.EnumMyReaderBiojava;
import mystructure.ExceptionInMyStructurePackage;
import mystructure.MyStructureIfc;
import mystructure.ReadingStructurefileException;
import org.biojava.nbio.structure.Structure;
import parameters.AlgoParameters;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Created by Fabrice on 19/09/16.
 */
public class IOTools {

    /**
     *
     * @param algoParameters
     * @param fourLetterCode that must be lower case
     * @return Can return null
     */
    public static MyStructureIfc getMyStructureIfc(AlgoParameters algoParameters, char[] fourLetterCode) {
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            // TODO change to char[]
            mmcifStructure = reader.readFromPDBFolder(String.valueOf(fourLetterCode), algoParameters.getPATH_TO_REMEDIATED_PDB_MMCIF_FOLDER(), algoParameters.getPATH_TO_CHEMCOMP_FOLDER());
        } catch (IOException | ExceptionInIOPackage e) {
           return null;
        }

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
            return null;
        }
        return mystructure;
    }



    public static Map<String, List<Path>> indexPDBFileInFolder(String pathToDividedPDBFolder) {

        Map<String, List<Path>> indexPDBFileInFolder = null;
        try {
            FileListingVisitorForPDBCifGzFiles fileListingVisitor = new FileListingVisitorForPDBCifGzFiles(pathToDividedPDBFolder);
            indexPDBFileInFolder = fileListingVisitor.getIndexFiles();

        } catch (IOException e) {
            System.out.println("FAILURE: in makeAListOfInputPDBFilesRecursivelyFromInputControllerFolder");
            //e.printStackTrace();
        }
        return indexPDBFileInFolder;
    }


    public static Map<String, List<Path>> indexChemcompFileInFolder(String pathToDividedPDBFolder) {

        Map<String, List<Path>> indexPDBFileInFolder = null;
        try {
            FileListingVisitorForChemcompCifGzFiles fileListingVisitor = new FileListingVisitorForChemcompCifGzFiles(pathToDividedPDBFolder);
            indexPDBFileInFolder = fileListingVisitor.getIndexFiles();

        } catch (IOException e) {
            System.out.println("FAILURE: in makeAListOfInputPDBFilesRecursivelyFromInputControllerFolder");
            //e.printStackTrace();
        }
        return indexPDBFileInFolder;
    }
}
