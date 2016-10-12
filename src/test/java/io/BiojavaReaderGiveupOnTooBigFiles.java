package io;

import org.biojava.nbio.structure.Structure;
import org.junit.Test;
import protocols.ParsingConfigFileException;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 12/10/16.
 */
public class BiojavaReaderGiveupOnTooBigFiles {


    @Test
    public void testReadFromPDBFolderProtein() throws ParsingConfigFileException {

        String fourLetterCode = "3j3y";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        boolean exceptionThrown = false;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            exceptionThrown = true;
            assertTrue(e.getMessage().contains("File too big to be handled. Size ="));
        }
        assertTrue(exceptionThrown);

    }
}
