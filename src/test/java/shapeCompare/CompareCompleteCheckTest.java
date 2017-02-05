/*
Author:
      Fabrice Moriaud <fmoriaud@ultimatepdb.org>

  Copyright (c) 2016 Fabrice Moriaud

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
  */
package shapeCompare;

import hits.Hit;
import io.Tools;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;
import protocols.ShapeContainerDefined;
import protocols.ShapecontainerDefinedBySegmentOfChain;
import protocols.ShapecontainerDefinedByWholeChain;
import shape.ShapeContainerIfc;
import shapeBuilder.ShapeBuildingException;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class CompareCompleteCheckTest {


    /**
     * test using a segment of chain. Should work as I use the original structurelocal which has deleted atoms
     */
    @Test
    public void completeCheckAutoCompareSegmentOfChain() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        int initialCount = algoParameters.ultiJMolBuffer.getSize();

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
        assertTrue(countHitDeletedBecauseOfHitLigandClashesInQuery == 0);

        int countHitDeletedBecauseOfPercentageIncreaseCompleteCheck = compareCompleteCheck.getCountHitDeletedBecauseOfPercentageIncreaseCompleteCheck();
        assertTrue(countHitDeletedBecauseOfPercentageIncreaseCompleteCheck == 4);

        int hitCount = results.size();
        assertTrue(hitCount == 16);

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


    /**
     * test using a chain.
     *
     * @throws IOException
     * @throws ParsingConfigFileException
     */
    @Test
    public void completeCheckAutoCompareChain() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        int initialCount = algoParameters.ultiJMolBuffer.getSize();

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
        assertTrue(countHitDeletedBecauseOfHitLigandClashesInQuery == 1);

        int countHitDeletedBecauseOfPercentageIncreaseCompleteCheck = compareCompleteCheck.getCountHitDeletedBecauseOfPercentageIncreaseCompleteCheck();
        assertTrue(countHitDeletedBecauseOfPercentageIncreaseCompleteCheck == 11);

        int hitCount = results.size();
        assertTrue(hitCount == 4);

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


    @Test
    public void completeCheckInterfamilyHit() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        int initialCount = algoParameters.ultiJMolBuffer.getSize();

        algoParameters.setFRACTION_NEEDED_ON_QUERY(0.75f);
        char[] fourLetterCodeQuery = "1be9".toCharArray();
        char[] chainIdQuery = "B".toCharArray();

        ShapeContainerDefined shapecontainerDefinedQuery = new ShapecontainerDefinedByWholeChain(fourLetterCodeQuery, chainIdQuery, algoParameters);
        ShapeContainerIfc queryShape = null;
        try {
            queryShape = shapecontainerDefinedQuery.getShapecontainer();
        } catch (ShapeBuildingException e) {
            assertTrue(false);
        }

        // TODO need another one
        char[] fourLetterCodeTarget = "5cp7".toCharArray();
        char[] chainIdTarget = "H".toCharArray();
        int startingRankId = 104;
        int peptideLength = 5;
        ShapeContainerDefined shapecontainerDefinedTarget = new ShapecontainerDefinedBySegmentOfChain(fourLetterCodeTarget, chainIdTarget, startingRankId, peptideLength, algoParameters);
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
        assertTrue(countHitDeletedBecauseOfHitLigandClashesInQuery == 0);

        int countHitDeletedBecauseOfPercentageIncreaseCompleteCheck = compareCompleteCheck.getCountHitDeletedBecauseOfPercentageIncreaseCompleteCheck();
        assertTrue(countHitDeletedBecauseOfPercentageIncreaseCompleteCheck == 1);

        int hitCount = results.size();
        assertTrue(hitCount == 1);

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


    @Test
    public void completeCheckCompareVsItSeflWasAWeirdCoverageResult() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        int initialCount = algoParameters.ultiJMolBuffer.getSize();

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
