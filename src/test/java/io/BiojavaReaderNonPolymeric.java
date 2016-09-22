package io;

import org.biojava.nbio.structure.Chain;
import org.biojava.nbio.structure.Group;
import org.biojava.nbio.structure.GroupType;
import org.biojava.nbio.structure.Structure;
import org.junit.Test;
import protocols.ParsingConfigFileException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 15/09/16.
 */
public class BiojavaReaderNonPolymeric {

    @Test
    public void testReadFromResourcesProtein() throws ParsingConfigFileException {

        String fourLetterCode = "2yjd";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException e) {
            assertTrue(false);
        }

        // Check the current situation Biojava 4.2.4
        // Four chains which is fine
        // Non Polymeric groups are found as HETATM Groups

        List<Chain> chains = mmcifStructure.getChains();
        assertTrue(chains.size() == 4);
        Chain peptideA = chains.get(2);
        List<Group> peptideAAminoAcids = peptideA.getAtomGroups(GroupType.AMINOACID);
        assertTrue(peptideAAminoAcids.size() == 11);
        List<Group> peptideAHetatm = peptideA.getAtomGroups(GroupType.HETATM);
        assertTrue(peptideAHetatm.size() == 7);
        Chain peptideB = chains.get(3);
        List<Group> peptideBAminoAcids = peptideB.getAtomGroups(GroupType.AMINOACID);
        assertTrue(peptideBAminoAcids.size() == 11);
        List<Group> peptideBHetatm = peptideB.getAtomGroups(GroupType.HETATM);
        assertTrue(peptideBHetatm.size() == 2);
        System.out.println();
    }
}
