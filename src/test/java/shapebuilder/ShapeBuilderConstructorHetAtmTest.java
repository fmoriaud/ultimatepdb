package shapebuilder;

import convertformat.AdapterBioJavaStructure;
import io.BiojavaReaderFromPathToMmcifFileTest;
import io.Tools;
import mystructure.EnumMyReaderBiojava;
import mystructure.ExceptionInMyStructurePackage;
import mystructure.MyStructureIfc;
import mystructure.ReadingStructurefileException;
import org.biojava.nbio.structure.Structure;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;
import shape.ShapeContainerIfc;
import shapeBuilder.ShapeBuilderConstructorHetAtm;
import shapeBuilder.ShapeBuilderConstructorIfc;
import shapeBuilder.ShapeBuilderConstructorSegmentOfChain;
import shapeBuilder.ShapeBuildingException;

import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 13/09/16.
 */
public class ShapeBuilderConstructorHetAtmTest {

    @Test
    public void testShapeBuilderConstructor() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();

        URL url = BiojavaReaderFromPathToMmcifFileTest.class.getClassLoader().getResource("1di9.cif.gz");
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

        char[] hetatmLigandThreeLetterCode = "MSQ".toCharArray();
        int occurenceId = 1;
        ShapeBuilderConstructorIfc shapeBuilder = new ShapeBuilderConstructorHetAtm(mystructure, hetatmLigandThreeLetterCode, occurenceId, algoParameters);
        ShapeContainerIfc shape = null;
        try {
            shape = shapeBuilder.getShapeContainer();
        } catch (
                ShapeBuildingException e) {
            assertTrue(false);
        }
        // don't know if it is good, it is as it is now.
        System.out.println(shape.getShape().getSize());
        System.out.println(shape.getMiniShape().size());
        assertTrue(shape.getShape().getSize() == 795);
        assertTrue(shape.getMiniShape().size() == 45 );//|| shape.getMiniShape().size() == 44);
    }
}
