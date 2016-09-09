package myjmol;

import convertformat.AdapterBioJavaStructure;
import io.BiojavaReaderTest;
import io.Tools;
import org.biojava.bio.structure.Structure;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.CommandLineTools;
import protocols.ParsingConfigFileException;
import mystructure.EnumMyReaderBiojava;
import mystructure.ExceptionInMyStructurePackage;
import mystructure.MyStructureIfc;
import mystructure.ReadingStructurefileException;
import ultiJmol.UltiJMol;

import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 31/08/16.
 */
public class MyJmolTest {


    // method to be tested
// ultiJmol.viewerForUlti.areHydrogenAdded()


    @Test
    public void testOpenStringInlineV3000Jmol() throws ParsingConfigFileException, IOException, ReadingStructurefileException, ExceptionInMyStructurePackage {

        URL url = BiojavaReaderTest.class.getClassLoader().getResource("1di9.cif.gz");
        Structure mmcifStructure = mmcifStructure = Tools.getStructure(url);

        URL urlUltimate = BiojavaReaderTest.class.getClassLoader().getResource("ultimate.xml");
        AlgoParameters algoParameters = CommandLineTools.generateModifiedAlgoParameters(urlUltimate.getPath(), EnumMyReaderBiojava.BioJava_MMCIFF);

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc myStructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure, EnumMyReaderBiojava.BioJava_MMCIFF);

        UltiJMol ultiJmol = new UltiJMol();
        String myStructureV3000 = myStructure.toV3000();
        try {
            Thread.sleep(4 * 1000);
        } catch (InterruptedException e) {

        }
        assertTrue(myStructureV3000.contains("M  V30 COUNTS 2798 2863 0 0 0"));
        assertTrue(myStructureV3000.contains("M  V30 1 N 19.12 41.85 25.992  0"));
        assertTrue(myStructureV3000.contains("M  V30 2863 1 2798 2797"));
        assertTrue(myStructureV3000.contains("M  END"));

        ultiJmol.jmolviewerForUlti.openStringInline(myStructureV3000);
        try {
            Thread.sleep(4 * 1000);
        } catch (InterruptedException e) {

        }
        String readV3000 = ultiJmol.viewerForUlti.getModelExtract("*", true, false, "V3000");
        assertTrue(readV3000.contains("M  V30 COUNTS 2798 2863 0 0 0"));
        assertTrue(readV3000.contains("M  V30 1 N     19.12000     41.85000     25.99200 0"));
        assertTrue(myStructureV3000.contains("M  V30 2863 1 2798 2797"));
        assertTrue(myStructureV3000.contains("M  END"));

        try {
            Thread.sleep(2 * 1000);
        } catch (InterruptedException e) {

        }
        ultiJmol.jmolviewerForUlti.evalString("zap");
        try {
            Thread.sleep(4 * 1000);
        } catch (InterruptedException e) {

        }
        // that throws an excepion. Don't know how to fix it
        ultiJmol.jmolviewerForUlti.dispose();
    }
}