package shapebuilder;

import convertformat.AdapterBioJavaStructure;
import io.BiojavaReader;
import io.BiojavaReaderFromPathToMmcifFileTest;
import io.BiojavaReaderIfc;
import io.Tools;
import mystructure.EnumMyReaderBiojava;
import mystructure.ExceptionInMyStructurePackage;
import mystructure.MyStructureIfc;
import mystructure.ReadingStructurefileException;
import org.biojava.nbio.structure.Structure;
import org.junit.Ignore;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;
import shape.ShapeContainerIfc;
import shapeBuilder.ShapeBuilderConstructorIfc;
import shapeBuilder.ShapeBuilderConstructorWholeChain;
import shapeBuilder.ShapeBuildingException;
import shapeBuilder.StructureLocalToBuildShapeIfc;

import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertTrue;


/**
 * Created by Fabrice on 11/09/16.
 */
public class ShapeBuilderConstructorWholeChainTest {

    @Test
    public void testShapeBuilderConstructorWholeChain() throws IOException, ParsingConfigFileException {

        char[] chainId = "C".toCharArray();
        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();

        URL url = BiojavaReaderFromPathToMmcifFileTest.class.getClassLoader().getResource("2yjd.cif.gz");
        Structure mmcifStructure = null;
        try {
            mmcifStructure = Tools.getStructure(url);
        } catch (IOException e) {
            assertTrue(false);
        }
        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure, EnumMyReaderBiojava.BioJava_MMCIFF);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException e) {
            assertTrue(false);
        }

        ShapeBuilderConstructorIfc shapeBuilder = new ShapeBuilderConstructorWholeChain(mystructure, chainId, algoParameters);
        ShapeContainerIfc shape = null;
        try {
            shape = shapeBuilder.getShapeContainer();
        } catch (ShapeBuildingException e) {
            assertTrue(false);
        }
        // don't know if it is good, it is as it is now.
        assertTrue(shape.getShape().getSize() == 656);
        assertTrue(shape.getMiniShape().size() == 61);
    }
}
