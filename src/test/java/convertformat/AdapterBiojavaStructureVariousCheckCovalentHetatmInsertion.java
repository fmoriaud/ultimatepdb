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
    public void testconvertStructureToMyStructureWithPTRcovalentLigand() throws ParsingConfigFileException, IOException {

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



    @Test
    public void testconvertStructureToMyStructureWithORGcovalentLigand() throws ParsingConfigFileException, IOException {

        URL url = BiojavaReaderFromPathToMmcifFileTest.class.getClassLoader().getResource("3kw9.cif.gz");
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

        // ORG is integrated with cutoff bond distance 2.0 but not at 1.8.
        // I think it is better to integrate covalent ligand of any kind.
        // The problem is just if I want to build a query from the covalent one.
        // But is is maybe better to skip those ones as anyway they are not binding only with soft interaction and
        // potential covalent binding is out of the scope of ultimatepdb.
        // So that is nice that it is integrated to I put 2.0 A
        Group mmcifORG = mmcifStructure.getChain(0).getAtomGroup(215);
        assertTrue(mmcifORG.getPDBName().equals("ORG"));
        GroupType type = mmcifORG.getType();
        assertTrue(type == GroupType.HETATM);
        MyMonomerIfc myStructureORG = mystructure.getAminoMyChain("A".toCharArray()).getMyMonomerByRank(215);
        assertTrue(Arrays.equals(myStructureORG.getThreeLetterCode(), "ORG".toCharArray()));
    }

}
