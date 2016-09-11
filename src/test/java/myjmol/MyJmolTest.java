package myjmol;

import convertformat.AdapterBioJavaStructure;
import io.BiojavaReaderFromPathToMmcifFileTest;
import io.Tools;
import org.biojava.nbio.structure.Structure;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;
import mystructure.EnumMyReaderBiojava;
import mystructure.ExceptionInMyStructurePackage;
import mystructure.MyStructureIfc;
import mystructure.ReadingStructurefileException;
import ultiJmol1462.MyJmol1462;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 31/08/16.
 */
public class MyJmolTest {


    // method to be tested
// ultiJmol.viewerForUlti.areHydrogenAdded()

    private String pathToPDBFolder;

    @Rule
    public TemporaryFolder testPDBFolder = new TemporaryFolder();

    @Before
    public void createPath() {

        try {
            File file = testPDBFolder.newFile("empty");
            pathToPDBFolder = file.getParentFile().getAbsolutePath();
        } catch (IOException e) {

        }
    }

    @Test
    public void testOpenStringInlineV3000Jmol() throws ParsingConfigFileException, IOException, ReadingStructurefileException, ExceptionInMyStructurePackage {

        URL url = BiojavaReaderFromPathToMmcifFileTest.class.getClassLoader().getResource("1di9.cif.gz");
        Structure mmcifStructure = mmcifStructure = Tools.getStructure(url, pathToPDBFolder);

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc myStructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure, EnumMyReaderBiojava.BioJava_MMCIFF);

        MyJmol1462 ultiJmol = new MyJmol1462();
        String myStructureV3000 = myStructure.toV3000();
        try {
            Thread.sleep(4 * 1000);
        } catch (InterruptedException e) {

        }
        assertTrue(myStructureV3000.contains("M  V30 COUNTS 2798 2863 0 0 0"));
        assertTrue(myStructureV3000.contains("M  V30 1 N 19.12 41.85 25.992  0"));
        assertTrue(myStructureV3000.contains("M  V30 2863 1 2798 2797"));
        assertTrue(myStructureV3000.contains("M  END"));

        ultiJmol.openStringInline(myStructureV3000);
        try {
            Thread.sleep(4 * 1000);
        } catch (InterruptedException e) {

        }
        String readV3000 = ultiJmol.jmolPanel.getViewer().getData("*", "V3000");
        assertTrue(readV3000.contains("M  V30 COUNTS 2798 2863 0 0 0"));
        assertTrue(readV3000.contains("M  V30 1 N     19.12000     41.85000     25.99200 0"));
        assertTrue(myStructureV3000.contains("M  V30 2863 1 2798 2797"));
        assertTrue(myStructureV3000.contains("M  END"));

        try {
            Thread.sleep(2 * 1000);
        } catch (InterruptedException e) {

        }
        ultiJmol.evalString("zap");
        try {
            Thread.sleep(4 * 1000);
        } catch (InterruptedException e) {

        }
        // that throws an excepion. Don't know how to fix it
        ultiJmol.frame.dispose();
    }
}