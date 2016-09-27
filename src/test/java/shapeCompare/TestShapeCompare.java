package shapeCompare;

import java.io.IOException;
import java.util.List;

import convertformat.AdapterBioJavaStructure;
import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import hits.Hit;
import io.BiojavaReader;
import io.Tools;
import mystructure.EnumMyReaderBiojava;
import org.biojava.nbio.structure.Structure;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.CommandLineException;
import protocols.ParsingConfigFileException;
import protocols.ShapeContainerFactory;
import shape.ShapeContainerIfc;
import shapeBuilder.EnumShapeReductor;
import shapeBuilder.ShapeBuilderConstructorIfc;
import shapeBuilder.ShapeBuildingException;
import mystructure.ExceptionInMyStructurePackage;
import mystructure.MyStructureIfc;
import mystructure.ReadingStructurefileException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class TestShapeCompare {

    @Test
    public void testCompareTwoKinaseLigandShape() throws ExceptionInScoringUsingBioJavaJMolGUI, ReadingStructurefileException, ExceptionInMyStructurePackage, CommandLineException, ParsingConfigFileException, ShapeBuildingException, IOException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 1);

        String fourLetterCode1di9 = "1di9";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure1di9 = null;
        try {
            mmcifStructure1di9 = reader.readFromPDBFolder(fourLetterCode1di9, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException e) {
            assertTrue(false);
        }
        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc myStructure1di9 = null;
        try {
            myStructure1di9 = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure1di9, EnumMyReaderBiojava.BioJava_MMCIFF);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException e) {
            assertTrue(false);
        }

        String fourLetterCode1a9u = "1a9u";
        BiojavaReader reader2 = new BiojavaReader();
        Structure mmcifStructure1a9u = null;
        try {
            mmcifStructure1a9u = reader2.readFromPDBFolder(fourLetterCode1a9u, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException e) {
            assertTrue(false);
        }
        AdapterBioJavaStructure adapterBioJavaStructure2 = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc myStructure1a9u = null;
        try {
            myStructure1a9u = adapterBioJavaStructure2.getMyStructureAndSkipHydrogens(mmcifStructure1a9u, EnumMyReaderBiojava.BioJava_MMCIFF);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException e) {
            assertTrue(false);
        }


        char[] hetatmLigandSB2 = "SB2".toCharArray();
        int occurenceId = 1;
        ShapeContainerIfc shapeSB2 = null;
        try {
            shapeSB2 = ShapeContainerFactory.getShapeAroundAHetAtomLigand(EnumShapeReductor.CLUSTERING, myStructure1a9u, algoParameters, hetatmLigandSB2, occurenceId);
        } catch (ShapeBuildingException e) {
            e.printStackTrace();
        }

        char[] hetatmLigandMSQ = "MSQ".toCharArray();
        ShapeContainerIfc shapeMSQ = null;
        try {
            shapeMSQ = ShapeContainerFactory.getShapeAroundAHetAtomLigand(EnumShapeReductor.CLUSTERING, myStructure1di9, algoParameters, hetatmLigandMSQ, occurenceId);
        } catch (ShapeBuildingException e) {
            e.printStackTrace();
        }

        ComparatorShapeContainerQueryVsAnyShapeContainer comparatorShape = new ComparatorShapeContainerQueryVsAnyShapeContainer(shapeMSQ, shapeSB2, algoParameters);
        List<Hit> listBestHitForEachAndEverySeed = null;
        try {
            listBestHitForEachAndEverySeed = comparatorShape.computeResults();

        } catch (NullResultFromAComparisonException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // dont know if good but it is like this currently
        // It is not reproducible
        //assertTrue(listBestHitForEachAndEverySeed.size() == 16 || listBestHitForEachAndEverySeed.size() == 19 || listBestHitForEachAndEverySeed.size() == 20);
        assertTrue(listBestHitForEachAndEverySeed.size() == 20);
        float coverageTopHit = listBestHitForEachAndEverySeed.get(0).getResultsFromEvaluateCost().getCoverage();
        assertEquals(coverageTopHit, 0.679, 0.001);
        double costTopHit = listBestHitForEachAndEverySeed.get(0).getResultsFromEvaluateCost().getCost();
        assertEquals(costTopHit, 0.0386, 0.0001);

       /* if (listBestHitForEachAndEverySeed.size() == 19) {

            float coverageLastHit = listBestHitForEachAndEverySeed.get(18).getResultsFromEvaluateCost().getCoverage();
            assertEquals(coverageLastHit, 0.665, 0.001);
            double costLastHit = listBestHitForEachAndEverySeed.get(18).getResultsFromEvaluateCost().getCost();
            assertEquals(costLastHit, 0.140, 0.001);
        }
        if (listBestHitForEachAndEverySeed.size() == 16) {

            float coverageLastHit = listBestHitForEachAndEverySeed.get(15).getResultsFromEvaluateCost().getCoverage();
            assertEquals(coverageLastHit, 0.665, 0.001);
            double costLastHit = listBestHitForEachAndEverySeed.get(15).getResultsFromEvaluateCost().getCost();
            assertEquals(costLastHit, 0.140, 0.001);
        }
*/
        if (listBestHitForEachAndEverySeed.size() == 20) {

            float coverageLastHit = listBestHitForEachAndEverySeed.get(19).getResultsFromEvaluateCost().getCoverage();
            assertEquals(coverageLastHit, 0.665, 0.001);
            double costLastHit = listBestHitForEachAndEverySeed.get(19).getResultsFromEvaluateCost().getCost();
            assertEquals(costLastHit, 0.139, 0.001);
        }

        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 1);
        try {
            algoParameters.ultiJMolBuffer.get().frame.dispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 0);

    }

}
