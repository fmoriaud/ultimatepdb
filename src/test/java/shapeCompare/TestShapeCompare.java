package shapeCompare;

import java.io.IOException;
import java.util.List;

import convertformat.AdapterBioJavaStructure;
import convertformat.ExceptionInConvertFormat;
import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import hits.Hit;
import io.BiojavaReader;
import io.ExceptionInIOPackage;
import io.Tools;
import mystructure.EnumMyReaderBiojava;
import org.biojava.nbio.structure.Structure;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.*;
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

    /**
     * Use ShapeContainerDefined fro both Shape.
     * @throws ExceptionInScoringUsingBioJavaJMolGUI
     * @throws ReadingStructurefileException
     * @throws ExceptionInMyStructurePackage
     * @throws CommandLineException
     * @throws ParsingConfigFileException
     * @throws ShapeBuildingException
     * @throws IOException
     */
    @Test
    public void testCompareTwoKinaseLigandShape() throws ExceptionInScoringUsingBioJavaJMolGUI, ReadingStructurefileException, ExceptionInMyStructurePackage, CommandLineException, ParsingConfigFileException, ShapeBuildingException, IOException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        int initialCount = algoParameters.ultiJMolBuffer.getSize();

        char[] fourLetterCode1bmk = "1bmk".toCharArray();
        char[] hetAtomsLigandId1bmk = "SB5".toCharArray();
        int occurrenceId = 1;
        ShapeContainerDefined shapeContainerDefined1bmk = new ShapecontainerDefinedByHetatm(fourLetterCode1bmk, algoParameters, hetAtomsLigandId1bmk, occurrenceId);
        ShapeContainerIfc shapeContainer1bmk = shapeContainerDefined1bmk.getShapecontainer();

        char[] fourLetterCode1a9u = "1a9u".toCharArray();
        char[] hetAtomsLigandId1a9u = "SB2".toCharArray();


        ShapeContainerDefined shapeContainerDefined1a9u = new ShapecontainerDefinedByHetatm(fourLetterCode1a9u, algoParameters, hetAtomsLigandId1a9u, occurrenceId);
        ShapeContainerIfc shapeContainer1a9u = shapeContainerDefined1a9u.getShapecontainer();

        ComparatorShapeContainerQueryVsAnyShapeContainer comparatorShape = new ComparatorShapeContainerQueryVsAnyShapeContainer(shapeContainer1bmk, shapeContainer1a9u, algoParameters);
        List<Hit> listBestHitForEachAndEverySeed = null;
        try {
            listBestHitForEachAndEverySeed = comparatorShape.computeResults();

        } catch (NullResultFromAComparisonException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        assertTrue(listBestHitForEachAndEverySeed.size() == 1);
        float coverageTopHit = listBestHitForEachAndEverySeed.get(0).getResultsFromEvaluateCost().getRatioPairedPointInQuery();
        assertEquals(coverageTopHit, 0.657, 0.001);
        double costTopHit = listBestHitForEachAndEverySeed.get(0).getResultsFromEvaluateCost().getCost();
        assertEquals(costTopHit, 0.068, 0.001);


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
