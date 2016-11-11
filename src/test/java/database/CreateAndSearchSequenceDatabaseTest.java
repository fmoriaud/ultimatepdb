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
public class CreateAndSearchSequenceDatabaseTest {

    /*
        void createDatabase();
    void updateDatabaseKeepingFourLetterCodeEntries();
    void updateDatabaseAndOverride();
    String returnSequenceInDbifFourLetterCodeAndChainfoundInDatabase(String fourLetterCode, String chainName);

    */

    @Ignore
    @Test
    public void testCreateDatabase() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        CreateAndSearchSequenceDatabase createAndSearchSequenceDatabase = new CreateAndSearchSequenceDatabase(algoParameters);
        createAndSearchSequenceDatabase.createDatabase();

        // Read an entry from it
        String sequence1di9 = createAndSearchSequenceDatabase.returnSequenceInDbifFourLetterCodeAndChainfoundInDatabase("1DI9", "A");

        // Check sequence length
        assertNotNull(sequence1di9);
        assertEquals((sequence1di9.length() / 3), 348);

        createAndSearchSequenceDatabase.shutdownDb();

        /*
        From resources 11 November 2016
        uniqueFourLetterCode count = 28
        total entries count = 117
        */
    }


    @Ignore
    @Test
    public void testCreateDatabaseFromBigPDBFolder() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();
        Path pathToPDBFolder = Paths.get("//Users//Fabrice//Documents//pdb");
        Path pathToChemCompFolderFolder = Paths.get("//Users//Fabrice//Documents//chemcomp");
        algoParameters.setPATH_TO_REMEDIATED_PDB_MMCIF_FOLDER(pathToPDBFolder.toFile().toString());
        algoParameters.setPATH_TO_CHEMCOMP_FOLDER(pathToChemCompFolderFolder.toFile().toString());

        CreateAndSearchSequenceDatabase createAndSearchSequenceDatabase = new CreateAndSearchSequenceDatabase(algoParameters);
        createAndSearchSequenceDatabase.createDatabase();

        // Read an entry from it
        String sequence1di9 = createAndSearchSequenceDatabase.returnSequenceInDbifFourLetterCodeAndChainfoundInDatabase("1DI9", "A");

        // Check sequence length
        assertNotNull(sequence1di9);
        assertEquals((sequence1di9.length() / 3), 348);

        createAndSearchSequenceDatabase.shutdownDb();

        /*
        From resources 11 November 2016
        uniqueFourLetterCode count = 28
        total entries count = 117
        */
    }
}

