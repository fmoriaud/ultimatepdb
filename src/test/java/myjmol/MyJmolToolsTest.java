package myjmol;

import convertformat.AdapterBioJavaStructure;
import io.BiojavaReaderFromPathToMmcifFileTest;
import io.Tools;
import mystructure.*;
import org.biojava.nbio.structure.Structure;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;
import shapeBuilder.ShapeBuildingException;
import ultiJmol1462.MyJmolTools;

import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 11/09/16.
 */
public class MyJmolToolsTest {

    @Test
    public void testProtonateStructure() throws ParsingConfigFileException, IOException, ReadingStructurefileException, ExceptionInMyStructurePackage {

        URL url = BiojavaReaderFromPathToMmcifFileTest.class.getClassLoader().getResource("2n0u.cif.gz");
        Structure mmcifStructure = mmcifStructure = Tools.getStructure(url);

        URL urlUltimate = BiojavaReaderFromPathToMmcifFileTest.class.getClassLoader().getResource("ultimate.xml");
        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure, EnumMyReaderBiojava.BioJava_MMCIFF);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException e) {
            assertTrue(false);
        }

        MyStructureIfc protonatedStructure = null;
        try {
            protonatedStructure = MyJmolTools.protonateStructure(mystructure, algoParameters);
        } catch (ShapeBuildingException e) {
            assertTrue(false);
        }

        MyChainIfc chainBeforeProtonation = mystructure.getAminoMyChain("A".toCharArray());
        MyMonomerIfc gln23BeforeProtonation = chainBeforeProtonation.getMyMonomerFromResidueId(23);
        assertTrue(gln23BeforeProtonation.getMyAtoms().length == 9);

        MyChainIfc chainAfterProtonation = protonatedStructure.getAminoMyChain("A".toCharArray());
        MyMonomerIfc gln23AfterProtonation = chainAfterProtonation.getMyMonomerFromResidueId(23);
        assertTrue(gln23AfterProtonation.getMyAtoms().length == 18);
    }
}
