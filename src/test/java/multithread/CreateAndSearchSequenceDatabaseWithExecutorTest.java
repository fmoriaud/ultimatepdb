package multithread;

import database.CreateAndSearchSequenceDatabaseWithExecutor;
import database.SequenceTools;
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
    public void testUpdateDbMultithreadedTest() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();
        algoParameters.setSHAPE_COMPARISON_THREAD_COUNT(6);

        CreateAndSearchSequenceDatabaseWithExecutor createAndSearchSequenceDatabaseWithExecutor = new CreateAndSearchSequenceDatabaseWithExecutor(algoParameters, Tools.testTableName);
        createAndSearchSequenceDatabaseWithExecutor.createDatabase();

        // Read an entry from it
        String sequence1di9 = createAndSearchSequenceDatabaseWithExecutor.returnSequenceInDbifFourLetterCodeAndChainfoundInDatabase("1DI9", "A");

        // Check sequence length
        assertNotNull(sequence1di9);
        assertEquals((sequence1di9.length() / 3), 348);

        createAndSearchSequenceDatabaseWithExecutor.shutdownDb();
    }
}
