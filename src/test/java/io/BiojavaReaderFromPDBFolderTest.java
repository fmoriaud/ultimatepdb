package io;

import mystructure.TestTools;
import org.biojava.nbio.structure.Structure;
import org.junit.Test;
import protocols.ParsingConfigFileException;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 11/09/16.
 */
public class BiojavaReaderFromPDBFolderTest {


    @Test
    public void testReadFromPDBFolderProtein() throws ParsingConfigFileException {

        // Assuming test folder is created already
        // TODO create it here in test folder and copy resources files to right 2 letter sub directories
        String fourLetterCode = "1di9";

        BiojavaReader reader = new BiojavaReader();

        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFrom(fourLetterCode, TestTools.testPDBFolder, TestTools.testChemcompFolder);
        } catch (IOException e) {
            assertTrue(false);
        }
        assertTrue(Tools.isGood1di9(mmcifStructure));
    }

}
