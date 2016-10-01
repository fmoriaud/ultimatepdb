package database;

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
 * Created by Fabrice on 29/09/16.
 */
public class UpdateSequenceDatabaseTest {


    @Test
    public void testBuildDatabaseFromTestFolder() throws IOException, ParsingConfigFileException {


        // Create Sequence DB
        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();
        Path pathToPDBFolder = Paths.get(algoParameters.getPATH_TO_REMEDIATED_PDB_MMCIF_FOLDER());
        Path pathToChemCompFolderFolder = Paths.get(algoParameters.getPATH_TO_CHEMCOMP_FOLDER());
        CreateAndSearchSequenceDatabase updateSequenceDatabase = new CreateAndSearchSequenceDatabase();
        updateSequenceDatabase.buildDatabase(pathToPDBFolder, pathToChemCompFolderFolder, algoParameters);

        // Read an entry from it
        String sequence1di9 = updateSequenceDatabase.returnSequenceInDbifFourLetterCodeAndChainfoundInDatabase("1DI9", "A");

        // Check sequence length
        assertNotNull(sequence1di9);
        assertEquals((sequence1di9.length() / 3), 348);

        updateSequenceDatabase.shutdownDb();
    }



    @Ignore
    @Test
    public void testBuildDatabaseFromBigFolder() throws IOException, ParsingConfigFileException {


        // Create Sequence DB
        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();
        Path pathToPDBFolder = Paths.get("//Users//Fabrice//Documents//pdb");
        Path pathToChemCompFolderFolder = Paths.get("//Users//Fabrice//Documents//chemcomp");
        CreateAndSearchSequenceDatabase updateSequenceDatabase = new CreateAndSearchSequenceDatabase();
        updateSequenceDatabase.buildDatabase(pathToPDBFolder, pathToChemCompFolderFolder, algoParameters);

        // Read an entry from it
        String sequence1di9 = updateSequenceDatabase.returnSequenceInDbifFourLetterCodeAndChainfoundInDatabase("1DI9", "A");

        // Check sequence length
        assertNotNull(sequence1di9);
        assertEquals((sequence1di9.length() / 3), 348);

        updateSequenceDatabase.shutdownDb();
    }
}
