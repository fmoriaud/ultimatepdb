package multithread;

import convertformat.AdapterBioJavaStructure;
import convertformat.ExceptionInConvertFormat;
import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import hits.Hit;
import io.BiojavaReader;
import io.ExceptionInIOPackage;
import io.Tools;
import mystructure.EnumMyReaderBiojava;
import mystructure.ExceptionInMyStructurePackage;
import mystructure.MyStructureIfc;
import mystructure.ReadingStructurefileException;
import org.biojava.nbio.structure.Structure;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.CommandLineException;
import protocols.ParsingConfigFileException;
import protocols.ShapeContainerFactory;
import shape.ShapeContainerIfc;
import shapeBuilder.EnumShapeReductor;
import shapeBuilder.ShapeBuildingException;
import shapeCompare.ComparatorShapeContainerQueryVsAnyShapeContainer;
import shapeCompare.NullResultFromAComparisonException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 02/10/16.
 */
public class CompareOneOnlyCallableTest {

    @Test
    public void testCompareTwoKinaseLigandShape() throws ExceptionInScoringUsingBioJavaJMolGUI, ReadingStructurefileException, ExceptionInMyStructurePackage, CommandLineException, ParsingConfigFileException, ShapeBuildingException, IOException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        int initialCount = algoParameters.ultiJMolBuffer.getSize();

        String fourLetterCode1di9 = "1di9";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure1di9 = null;
        try {
            mmcifStructure1di9 = reader.readFromPDBFolder(fourLetterCode1di9, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }
        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc myStructure1di9 = null;
        try {
            myStructure1di9 = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure1di9, EnumMyReaderBiojava.BioJava_MMCIFF);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
            assertTrue(false);
        }

        String fourLetterCode1a9u = "1a9u";
        BiojavaReader reader2 = new BiojavaReader();
        Structure mmcifStructure1a9u = null;
        try {
            mmcifStructure1a9u = reader2.readFromPDBFolder(fourLetterCode1a9u, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }
        AdapterBioJavaStructure adapterBioJavaStructure2 = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc myStructure1a9u = null;
        try {
            myStructure1a9u = adapterBioJavaStructure2.getMyStructureAndSkipHydrogens(mmcifStructure1a9u, EnumMyReaderBiojava.BioJava_MMCIFF);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
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

        List<Hit> listBestHitForEachAndEverySeed = null;
        CompareOneOnlyCallable callable = new CompareOneOnlyCallable(shapeMSQ, shapeSB2, algoParameters);
        FutureTask<List<Hit>> future = new FutureTask(callable);
        future.run();
        try {
            listBestHitForEachAndEverySeed = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        assertTrue(listBestHitForEachAndEverySeed.size() == 6);
        float coverageTopHit = listBestHitForEachAndEverySeed.get(0).getResultsFromEvaluateCost().getRatioPairedPointInQuery();
        assertEquals(coverageTopHit, 0.653, 0.001);
        double costTopHit = listBestHitForEachAndEverySeed.get(0).getResultsFromEvaluateCost().getCost();
        assertEquals(costTopHit, 0.0436, 0.0001);


        /*
        if (listBestHitForEachAndEverySeed.size() == 6) {

            float coverageLastHit = listBestHitForEachAndEverySeed.get(5).getResultsFromEvaluateCost().getRatioPairedPointInQuery();
            assertEquals(coverageLastHit, 0.692, 0.001);
            double costLastHit = listBestHitForEachAndEverySeed.get(8).getResultsFromEvaluateCost().getCost();
            assertEquals(costLastHit, 0.0904, 0.001);
        }
*/
        int finalCount = algoParameters.ultiJMolBuffer.getSize();
        assertTrue(finalCount == initialCount);
        try {
            for (int i = 0; i < initialCount; i++) {
                algoParameters.ultiJMolBuffer.get().frame.dispose();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 0);

    }

}
