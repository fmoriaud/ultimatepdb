package database;

import io.IOTools;
import io.Tools;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Fabrice on 08/11/16.
 */
public class CurrentDbGetInfos {


    @Test
    public void getContentInfos() throws IOException, ParsingConfigFileException {


        Connection connexion = DatabaseTools.getConnection();

        Statement stmt = null;
        try {
            stmt = connexion.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String findEntry = "SELECT * from sequence";

        ResultSet resultFindEntry = null;
        try {
            resultFindEntry = stmt.executeQuery(findEntry);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Set<String> uniqueFourLetterCode = new HashSet<>();
        int entriesCount = 0;
        try {
            while(resultFindEntry.next()){

                String fourLetterCode = resultFindEntry.getString(1);
                uniqueFourLetterCode.add(fourLetterCode);

                entriesCount += 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("uniqueFourLetterCode count = " + uniqueFourLetterCode.size());
        System.out.println("total entries count = " + entriesCount);

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();
        Path pathToPDBFolder = Paths.get("//Users//Fabrice//Documents//pdb");
        Path pathToChemCompFolderFolder = Paths.get("//Users//Fabrice//Documents//chemcomp");
        algoParameters.setPATH_TO_REMEDIATED_PDB_MMCIF_FOLDER(pathToPDBFolder.toFile().toString());
        algoParameters.setPATH_TO_CHEMCOMP_FOLDER(pathToChemCompFolderFolder.toFile().toString());
        Map<String, List<Path>> indexPDBFileInFolder = IOTools.indexPDBFileInFolder(algoParameters.getPATH_TO_REMEDIATED_PDB_MMCIF_FOLDER());

        System.out.println("PDB Four Letter codes in files count = " + indexPDBFileInFolder.size());

        // TODO Add test to see how many of intra chains of length 5 ?
        // But that wont incude if invalid residue with not all atoms ?

        DatabaseTools.shutdown();

        // TODO Add test so to check if Sequence DB was not changed
    }
}
