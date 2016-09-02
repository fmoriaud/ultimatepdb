package myjmol;

import convertformat.AdapterBioJavaStructure;
import io.ExceptionInIOPackage;
import io.IOTools;
import io.ContentOfReadMmCifFileReadFromResourcesTest;
import org.biojava.nbio.structure.Structure;
import org.junit.Test;
import parameters.AlgoParameters;
import structure.ExceptionInMyStructurePackage;
import structure.MyStructureIfc;
import structure.ReadingStructurefileException;
import ultiJmol.UltiJMol;

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
    public void testOpenJmol() {

        URL url = ContentOfReadMmCifFileReadFromResourcesTest.class.getClassLoader().getResource("1di9.cif.gz");

        Path path = null;
        try {
            path = Paths.get(url.toURI());
        } catch (URISyntaxException e1) {
            assertTrue(false);
        }
        Structure cifStructure = null;
        try {
            cifStructure = IOTools.readMMCIFFile(path);
        } catch (ExceptionInIOPackage e) {
            assertTrue(false);
        }

        AlgoParameters algoParameters = new AlgoParameters();
        AdapterBioJavaStructure adapter = new AdapterBioJavaStructure(algoParameters);

        MyStructureIfc myStructure = null;
        try {
            myStructure = adapter.convertStructureToMyStructure(cifStructure, algoParameters);
        } catch (ReadingStructurefileException e) {
            e.printStackTrace();
        } catch (ExceptionInMyStructurePackage exceptionInMyStructurePackage) {
            exceptionInMyStructurePackage.printStackTrace();
        }
        UltiJMol ultiJmol = new UltiJMol();

        ultiJmol.jmolviewerForUlti.openStringInline(myStructure.toV3000());

        System.out.println();


    }
}