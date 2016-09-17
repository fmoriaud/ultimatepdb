package convertformat;

import io.BiojavaReaderFromPathToMmcifFileTest;
import io.Tools;
import mystructure.*;
import org.biojava.nbio.structure.Group;
import org.biojava.nbio.structure.GroupType;
import org.biojava.nbio.structure.Structure;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 17/09/16.
 */
public class AdapterBiojavaStructureVariousCheckCovalentHetatmInsertion {

    // PTR is L-Peptide so already integrated by Biojava

    @Test
    public void testconvertStructureToMyStructureProteinHasNeighborsByBondAndDistance() throws ParsingConfigFileException, IOException {

        URL url = BiojavaReaderFromPathToMmcifFileTest.class.getClassLoader().getResource("2mrk.cif.gz");
        Structure mmcifStructure = mmcifStructure = Tools.getStructure(url);

        URL urlUltimate = BiojavaReaderFromPathToMmcifFileTest.class.getClassLoader().getResource("ultimate.xml");
        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure, EnumMyReaderBiojava.BioJava_MMCIFF);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException e) {
            assertTrue(false);
        }

        Group mmcifPTR = mmcifStructure.getChain(1).getAtomGroup(3);
        assertTrue(mmcifPTR.getPDBName().equals("PTR"));
        GroupType type = mmcifPTR.getType();
        assertTrue(type == GroupType.AMINOACID);
        MyMonomerIfc myStructurePTR = mystructure.getAminoMyChain("B".toCharArray()).getMyMonomerByRank(3);
        assertTrue(Arrays.equals(myStructurePTR.getThreeLetterCode(), "PTR".toCharArray()));
    }

}
