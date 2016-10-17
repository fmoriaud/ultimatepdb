package shapebuilder;

import convertformat.AdapterBioJavaStructure;
import convertformat.ExceptionInConvertFormat;
import io.BiojavaReader;
import io.ExceptionInIOPackage;
import io.Tools;
import mystructure.*;
import org.biojava.nbio.structure.Structure;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;
import protocols.ShapeContainerFactory;
import shape.ShapeContainerIfc;
import shape.ShapeContainerWithPeptide;
import shapeBuilder.EnumShapeReductor;
import shapeBuilder.ShapeBuilderConstructorIfc;
import shapeBuilder.ShapeBuildingException;
import shapeBuilder.StructureLocalToBuildShapeSegmentOfShape;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 11/09/16.
 */
public class ShapeBuilderConstructorSegmentOfChainTest {

    @Test
    public void testShapeBuilderConstructor() throws IOException, ParsingConfigFileException {

        char[] chainId = "C".toCharArray();
        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 1);

        String fourLetterCode = "2yjd";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure, EnumMyReaderBiojava.BioJava_MMCIFF);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
            assertTrue(false);
        }

        int startingRankId = 3;
        int peptideLength = 4;
        ShapeContainerIfc shape = null;
        try {
            shape = ShapeContainerFactory.getShapeAroundASegmentOfChainUsingStartingMyMonomerPositionInChain(EnumShapeReductor.CLUSTERING, mystructure, algoParameters, chainId, startingRankId, peptideLength);
        } catch (ShapeBuildingException e) {
            e.printStackTrace();
        }


        // don't know if it is good, it is as it is now.
        // especially because ACE and NH2 were moved...
        assertTrue(shape.getShape().getSize() == 813);
        assertTrue(shape.getMiniShape().size() == 71);

        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 1);
        try {
            algoParameters.ultiJMolBuffer.get().frame.dispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 0);


        // Check ligand peptide bonds
        ShapeContainerWithPeptide shapeContainerWithPeptide = (ShapeContainerWithPeptide) shape;
        MyChainIfc ligand = shapeContainerWithPeptide.getPeptide();

        // Check peptide bonds
        MyAtomIfc n1 = ligand.getMyMonomers()[1].getMyAtomFromMyAtomName("N".toCharArray());
        MyAtomIfc c0 = ligand.getMyMonomers()[0].getMyAtomFromMyAtomName("C".toCharArray());
        boolean foundPeptideBond = false;
        for (MyBondIfc bond : n1.getBonds()) {
            if (bond.getBondedAtom() == c0) {
                foundPeptideBond = true;
            }
        }
        assertTrue(foundPeptideBond);
    }
}
