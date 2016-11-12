package protocols;

import database.CreateAndSearchSequenceDatabase;
import database.CreateAndSearchSequenceDatabaseWithExecutor;
import database.SequenceTools;
import parameters.AlgoParameters;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Fabrice on 11/11/16.
 */
public class SequenceDatabaseBuildMultiThread {

    // -------------------------------------------------------------------
    // Public && Interface method
    // -------------------------------------------------------------------
    public static void main(String[] args) throws ParsingConfigFileException {

        AlgoParameters algoParameters = ProtocolTools.prepareAlgoParameters();
        algoParameters.setSHAPE_COMPARISON_THREAD_COUNT(6);
        CreateAndSearchSequenceDatabaseWithExecutor createAndSearchSequenceDatabaseWithExecutor = new CreateAndSearchSequenceDatabaseWithExecutor(algoParameters, SequenceTools.tableName);
        createAndSearchSequenceDatabaseWithExecutor.createDatabase();

        createAndSearchSequenceDatabaseWithExecutor.shutdownDb();
    }

}
/*
    @Ignore
    @Test
    public void testBuildDatabaseFromBigFolder() throws IOException, ParsingConfigFileException {


        // Create Sequence DB
        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();
        algoParameters.setSHAPE_COMPARISON_THREAD_COUNT(4);
        Path pathToPDBFolder = Paths.get("//Users//Fabrice//Documents//pdb");
        Path pathToChemCompFolderFolder = Paths.get("//Users//Fabrice//Documents//chemcomp");
        algoParameters.setPATH_TO_REMEDIATED_PDB_MMCIF_FOLDER(pathToPDBFolder.toFile().toString());
        algoParameters.setPATH_TO_CHEMCOMP_FOLDER(pathToChemCompFolderFolder.toFile().toString());
        CreateAndSearchSequenceDatabaseWithExecutor createAndSearchSequenceDatabaseWithExecutor = new CreateAndSearchSequenceDatabaseWithExecutor(algoParameters, SequenceTools.tableName);
        createAndSearchSequenceDatabaseWithExecutor.createDatabase();

        // Read an entry from it
        String sequence1di9 = createAndSearchSequenceDatabaseWithExecutor.returnSequenceInDbifFourLetterCodeAndChainfoundInDatabase("1DI9", "A");

        // Check sequence length
        assertNotNull(sequence1di9);
        assertEquals((sequence1di9.length() / 3), 348);

        createAndSearchSequenceDatabaseWithExecutor.shutdownDb();
    }

*/
/*
    @Ignore
    @Test
    public void testUpdateDatabaseFromBigFolder() throws IOException, ParsingConfigFileException {


        // Create Sequence DB
        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();
        algoParameters.setSHAPE_COMPARISON_THREAD_COUNT(4);
        Path pathToPDBFolder = Paths.get("//Users//Fabrice//Documents//pdb");
        Path pathToChemCompFolderFolder = Paths.get("//Users//Fabrice//Documents//chemcomp");
        algoParameters.setPATH_TO_REMEDIATED_PDB_MMCIF_FOLDER(pathToPDBFolder.toFile().toString());
        algoParameters.setPATH_TO_CHEMCOMP_FOLDER(pathToChemCompFolderFolder.toFile().toString());
        CreateAndSearchSequenceDatabaseWithExecutor createAndSearchSequenceDatabaseWithExecutor = new CreateAndSearchSequenceDatabaseWithExecutor(algoParameters, SequenceTools.tableName);
        createAndSearchSequenceDatabaseWithExecutor.updateDatabaseKeepingFourLetterCodeEntries();

        // Read an entry from it
        String sequence1di9 = createAndSearchSequenceDatabaseWithExecutor.returnSequenceInDbifFourLetterCodeAndChainfoundInDatabase("1DI9", "A");

        // Check sequence length
        assertNotNull(sequence1di9);
        assertEquals((sequence1di9.length() / 3), 348);

        createAndSearchSequenceDatabaseWithExecutor.shutdownDb();
    }
*/