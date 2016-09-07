package myjmol;

import convertformat.AdapterBioJavaStructure;
import io.BiojavaReaderTest;
import io.Tools;
import org.biojava.bio.structure.Structure;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.CommandLineTools;
import protocols.ParsingConfigFileException;
import structure.EnumMyReaderBiojava;
import structure.ExceptionInMyStructurePackage;
import structure.MyStructureIfc;
import structure.ReadingStructurefileException;
import ultiJmol.UltiJMol;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 31/08/16.
 */
public class MyJmolTest {


    // method to be tested
// ultiJmol.viewerForUlti.areHydrogenAdded()


    @Test
    public void testOpenStringInlineV3000Jmol() throws ParsingConfigFileException, IOException, ReadingStructurefileException, ExceptionInMyStructurePackage {

        URL url = BiojavaReaderTest.class.getClassLoader().getResource("3nir.cif.gz");
        Structure mmcifStructure = mmcifStructure = Tools.getStructure(url);

        URL urlUltimate = BiojavaReaderTest.class.getClassLoader().getResource("ultimate.xml");
        AlgoParameters algoParameters = CommandLineTools.generateModifiedAlgoParameters(urlUltimate.getPath(), EnumMyReaderBiojava.BioJava_MMCIFF);

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc myStructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure, EnumMyReaderBiojava.BioJava_MMCIFF);

        UltiJMol ultiJmol = new UltiJMol();
        String myStructureV3000 = myStructure.toV3000();
        ultiJmol.jmolviewerForUlti.openStringInline(myStructureV3000);

        System.out.println();


    }
}