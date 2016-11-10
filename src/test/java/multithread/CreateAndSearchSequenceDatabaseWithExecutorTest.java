package multithread;

import database.CreateAndSearchSequenceDatabase;
import database.CreateAndSearchSequenceDatabaseWithExecutor;
import io.Tools;
import org.junit.Ignore;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Fabrice on 06/11/16.
 */
public class CreateAndSearchSequenceDatabaseWithExecutorTest {

    @Ignore
    @Test
    public void testUpdateDbMultithreaded() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();
        algoParameters.setSHAPE_COMPARISON_THREAD_COUNT(6);

        CreateAndSearchSequenceDatabaseWithExecutor createAndSearchSequenceDatabaseWithExecutor = new CreateAndSearchSequenceDatabaseWithExecutor(algoParameters);
        createAndSearchSequenceDatabaseWithExecutor.createDatabase();

        // Read an entry from it
        String sequence1di9 = createAndSearchSequenceDatabaseWithExecutor.returnSequenceInDbifFourLetterCodeAndChainfoundInDatabase("1DI9", "A");

        // Check sequence length
        assertNotNull(sequence1di9);
        assertEquals((sequence1di9.length() / 3), 348);

        createAndSearchSequenceDatabaseWithExecutor.shutdownDb();
    }


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
        CreateAndSearchSequenceDatabaseWithExecutor createAndSearchSequenceDatabaseWithExecutor = new CreateAndSearchSequenceDatabaseWithExecutor(algoParameters);
        createAndSearchSequenceDatabaseWithExecutor.createDatabase();

        // Read an entry from it
        String sequence1di9 = createAndSearchSequenceDatabaseWithExecutor.returnSequenceInDbifFourLetterCodeAndChainfoundInDatabase("1DI9", "A");

        // Check sequence length
        assertNotNull(sequence1di9);
        assertEquals((sequence1di9.length() / 3), 348);

        createAndSearchSequenceDatabaseWithExecutor.shutdownDb();
    }



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
        CreateAndSearchSequenceDatabaseWithExecutor createAndSearchSequenceDatabaseWithExecutor = new CreateAndSearchSequenceDatabaseWithExecutor(algoParameters);
        createAndSearchSequenceDatabaseWithExecutor.updateDatabaseKeepingFourLetterCodeEntries();

        // Read an entry from it
        String sequence1di9 = createAndSearchSequenceDatabaseWithExecutor.returnSequenceInDbifFourLetterCodeAndChainfoundInDatabase("1DI9", "A");

        // Check sequence length
        assertNotNull(sequence1di9);
        assertEquals((sequence1di9.length() / 3), 348);

        createAndSearchSequenceDatabaseWithExecutor.shutdownDb();
    }
}
