package shapeCompare;

import hits.Hit;
import io.Tools;
import org.junit.Ignore;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.*;
import shape.ShapeContainerIfc;
import shapeBuilder.ShapeBuildingException;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 15/11/16.
 */
public class CompareCompleteCheckTest {


    /**
     * test using a segment of chain. Should work as I use the original structurelocal which has deleted atoms
     */
    @Test
    public void completeCheckAutoCompareSegmentOfChain() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();

        char[] fourLetterCode = "1be9".toCharArray();
        char[] chainId = "B".toCharArray();
        int startingRankId = 1;
        int peptideLength = 3;

        ShapeContainerDefined shapecontainerDefinedQuery = new ShapecontainerDefinedBySegmentOfChain(fourLetterCode, chainId, startingRankId, peptideLength, algoParameters);
        ShapeContainerIfc queryShape = null;
        try {
            queryShape = shapecontainerDefinedQuery.getShapecontainer();
        } catch (ShapeBuildingException e) {
            assertTrue(false);
        }

        ShapeContainerDefined shapecontainerDefinedTarget = new ShapecontainerDefinedBySegmentOfChain(fourLetterCode, chainId, startingRankId, peptideLength, algoParameters);

        ShapeContainerIfc targetShape = null;
        try {
            targetShape = shapecontainerDefinedTarget.getShapecontainer();
        } catch (ShapeBuildingException e) {
            assertTrue(false);
        }

        CompareCompleteCheck compareCompleteCheck = new CompareCompleteCheck(queryShape, targetShape, algoParameters);
        List<Hit> results = null;
        try {
            results = compareCompleteCheck.computeResults();
        } catch (NullResultFromAComparisonException e) {
            e.printStackTrace();
        }

        int countHitDeletedBecauseOforiginalCost = compareCompleteCheck.getCountHitDeletedBecauseOforiginalCost();
        assertTrue(countHitDeletedBecauseOforiginalCost == 0);

        int countHitDeletedBecauseOfHitLigandClashesInQuery = compareCompleteCheck.getCountHitDeletedBecauseOfHitLigandClashesInQuery();
        assertTrue(countHitDeletedBecauseOfHitLigandClashesInQuery == 10);

        int countHitDeletedBecauseOfPercentageIncreaseCompleteCheck = compareCompleteCheck.getCountHitDeletedBecauseOfPercentageIncreaseCompleteCheck();
        assertTrue(countHitDeletedBecauseOfPercentageIncreaseCompleteCheck == 16);

        int hitCount = results.size();
        assertTrue(hitCount == 34);
    }


    /**
     * test using a chain.
     *
     * @throws IOException
     * @throws ParsingConfigFileException
     */
    @Test
    public void completeCheckAutoCompareChain() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();

        char[] fourLetterCode = "1be9".toCharArray();
        char[] chainId = "B".toCharArray();

        ShapeContainerDefined shapecontainerDefinedQuery = new ShapecontainerDefinedByWholeChain(fourLetterCode, chainId, algoParameters);
        ShapeContainerIfc queryShape = null;
        try {
            queryShape = shapecontainerDefinedQuery.getShapecontainer();
        } catch (ShapeBuildingException e) {
            assertTrue(false);
        }

        ShapeContainerDefined shapecontainerDefinedTarget = new ShapecontainerDefinedByWholeChain(fourLetterCode, chainId, algoParameters);
        ShapeContainerIfc targetShape = null;
        try {
            targetShape = shapecontainerDefinedTarget.getShapecontainer();
        } catch (ShapeBuildingException e) {
            assertTrue(false);
        }

        CompareCompleteCheck compareCompleteCheck = new CompareCompleteCheck(queryShape, targetShape, algoParameters);
        List<Hit> results = null;
        try {
            results = compareCompleteCheck.computeResults();
        } catch (NullResultFromAComparisonException e) {
            e.printStackTrace();
        }

        int countHitDeletedBecauseOforiginalCost = compareCompleteCheck.getCountHitDeletedBecauseOforiginalCost();
        assertTrue(countHitDeletedBecauseOforiginalCost == 0);

        int countHitDeletedBecauseOfHitLigandClashesInQuery = compareCompleteCheck.getCountHitDeletedBecauseOfHitLigandClashesInQuery();
        assertTrue(countHitDeletedBecauseOfHitLigandClashesInQuery == 8);

        int countHitDeletedBecauseOfPercentageIncreaseCompleteCheck = compareCompleteCheck.getCountHitDeletedBecauseOfPercentageIncreaseCompleteCheck();
        assertTrue(countHitDeletedBecauseOfPercentageIncreaseCompleteCheck == 29);

        int hitCount = results.size();
        assertTrue(hitCount == 5);
    }


    @Test
    public void completeCheckInterfamilyHit() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();

        char[] fourLetterCodeQuery = "1be9".toCharArray();
        char[] chainIdQuery = "B".toCharArray();

        ShapeContainerDefined shapecontainerDefinedQuery = new ShapecontainerDefinedByWholeChain(fourLetterCodeQuery, chainIdQuery, algoParameters);
        ShapeContainerIfc queryShape = null;
        try {
            queryShape = shapecontainerDefinedQuery.getShapecontainer();
        } catch (ShapeBuildingException e) {
            assertTrue(false);
        }

        char[] fourLetterCodeTarget = "1a3l".toCharArray();
        char[] chainIdTarget = "H".toCharArray();
        int startingRankId = 110;
        int peptideLength = 5;
        ShapeContainerDefined shapecontainerDefinedTarget = new ShapecontainerDefinedBySegmentOfChain(fourLetterCodeTarget, chainIdTarget, startingRankId, peptideLength, algoParameters);
        ShapeContainerIfc targetShape = null;
        try {
            targetShape = shapecontainerDefinedTarget.getShapecontainer();
        } catch (ShapeBuildingException e) {
            assertTrue(false);
        }

        /*
        &&&&&& Comparing starts 1A3L
                &&&&&& Comparing ends 1A3L found 85 hits to minimize
                &&&&&& Minimizing 1A3L rankId = 0
        PDB = 1A3L chain id = H index = 110 sequence = [GLN, GLY, THR, SER, VAL] cost = 0.10354941739683103 minishape size = 67 shape size = 1166
        RatioPairedPointToHitPoints = 0.83 CountOfLongDistanceChange = 0 InteractionEFinal = 10.956764221191406 RmsdLigand = 0.8019495606422424 LigandStrainedEnergy = 78.31669616699219
        all convergence reached true RmsdBackbone = 1.4588981 Rank = 0
                */

        CompareCompleteCheck compareCompleteCheck = new CompareCompleteCheck(queryShape, targetShape, algoParameters);
        List<Hit> results = null;
        try {
            results = compareCompleteCheck.computeResults();
        } catch (NullResultFromAComparisonException e) {
            e.printStackTrace();
        }

        int countHitDeletedBecauseOforiginalCost = compareCompleteCheck.getCountHitDeletedBecauseOforiginalCost();
        assertTrue(countHitDeletedBecauseOforiginalCost == 0);

        int countHitDeletedBecauseOfHitLigandClashesInQuery = compareCompleteCheck.getCountHitDeletedBecauseOfHitLigandClashesInQuery();
        assertTrue(countHitDeletedBecauseOfHitLigandClashesInQuery == 80);

        int countHitDeletedBecauseOfPercentageIncreaseCompleteCheck = compareCompleteCheck.getCountHitDeletedBecauseOfPercentageIncreaseCompleteCheck();
        assertTrue(countHitDeletedBecauseOfPercentageIncreaseCompleteCheck == 14);

        int hitCount = results.size();
        assertTrue(hitCount == 20);
    }


    @Test
    public void completeCheckCompareVsItSeflWasAWeirdCoverageResult() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();

        String queryFourLetterCode = "1be9";
        String peptideChainId = "B";

        ShapeContainerDefined shapeContainerbuilder = new ShapecontainerDefinedByWholeChain(queryFourLetterCode.toCharArray(), peptideChainId.toCharArray(), algoParameters);
        ShapeContainerIfc queryShape = null;
        try {
            queryShape = shapeContainerbuilder.getShapecontainer();
        } catch (ShapeBuildingException e) {
            e.printStackTrace();
        }

        ShapeContainerDefined shapeContainerDefinedTarget = new ShapecontainerDefinedBySegmentOfChain(queryFourLetterCode.toCharArray(), peptideChainId.toCharArray(), 0, 5, algoParameters);
        ShapeContainerIfc targetShape = null;
        try {
            targetShape = shapeContainerDefinedTarget.getShapecontainer();
        } catch (ShapeBuildingException e) {
            e.printStackTrace();
        }


        CompareCompleteCheck compareCompleteCheck = new CompareCompleteCheck(queryShape, targetShape, algoParameters);
        List<Hit> results = null;
        try {
            results = compareCompleteCheck.computeResults();
        } catch (NullResultFromAComparisonException e) {
            e.printStackTrace();
        }

        Hit result = results.get(0);
        double percentageIncreaseCompleteCheck = result.getPercentageIncreaseCompleteCheck();

        System.out.println("percentageIncreaseCompleteCheck = " + percentageIncreaseCompleteCheck);
    }
}
