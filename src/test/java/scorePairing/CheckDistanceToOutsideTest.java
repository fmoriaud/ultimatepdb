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
package scorePairing;

import io.Tools;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;
import protocols.ShapeContainerDefined;
import protocols.ShapecontainerDefinedByHetatm;
import shape.ShapeContainerIfc;
import shapeBuilder.ShapeBuildingException;
import shapeCompare.CompareTools;
import shapeCompare.ResultsFromEvaluateCost;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class CheckDistanceToOutsideTest {

    @Test
    public void testDistanceToOutsideGoodSuperpositionForAGoodBurriedPocketSuperpositionCae() throws IOException, ParsingConfigFileException, ShapeBuildingException {


        boolean visualInspection = true;
        boolean debug = true;

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        algoParameters.setFRACTION_NEEDED_ON_QUERY(0.75f);

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

        List<ResultsFromEvaluateCost> resultsExtendedPairing = CompareTools.compare(shapeContainer1bmk, shapeContainer1a9u, algoParameters);

        int count = 0;
        double averagePercentageDifferentSignTrue = 0;
        double averagePercentageDifferentSignFalse = 0;
        int countTrue = 0;
        int countFalse = 0;

        // determined by visual inspection of the superposition
        List<Boolean> expectedResult = new ArrayList<>();
        expectedResult.add(true);
        expectedResult.add(false);
        expectedResult.add(false);
        expectedResult.add(true); // 3
        expectedResult.add(false);
        expectedResult.add(false);
        expectedResult.add(false);
        expectedResult.add(true);
        expectedResult.add(true);
        expectedResult.add(true);
        expectedResult.add(false);
        expectedResult.add(false); // 11 true // not clear
        expectedResult.add(false);
        expectedResult.add(false);
        expectedResult.add(true); // 14 true
        expectedResult.add(false);
        expectedResult.add(false); // 16 true
        expectedResult.add(false);
        expectedResult.add(false); // ??
        expectedResult.add(true); // 19 true

        int countOk = 0;
        double maxTrue = 0.0;
        double minFalse = 100.0;

        int countFN = 0;
        int countFP = 0;
        List<Integer> listFP = new ArrayList<>();

        for (int i = 0; i < resultsExtendedPairing.size(); i++) {

            ResultsFromEvaluateCost result = resultsExtendedPairing.get(i);
            CheckDistanceToOutside checkDistanceToOutside = new CheckDistanceToOutside(result.getPairingAndNullSpaces(), shapeContainer1bmk, shapeContainer1a9u);
            boolean isDistanceOK = checkDistanceToOutside.isDistanceOk();
            int countCasesDifferentSign = checkDistanceToOutside.getCountCasesDifferentSign();
            int countConsideredCases = checkDistanceToOutside.getCountConsideredCases();

            float percentageDifferentSign = 100 * (float) countCasesDifferentSign / countConsideredCases;

            if (debug == true) {
                System.out.println(isDistanceOK + " must be " + " " + expectedResult.get(i) + " " + countCasesDifferentSign + " " + countConsideredCases + " " + percentageDifferentSign + " %");

            }

            if (expectedResult.get(i) == true && isDistanceOK == false) {
                countFN += 1;
            }
            if (expectedResult.get(i) == false && isDistanceOK == true) {
                countFP += 1;
                listFP.add(i);
            }

            if (isDistanceOK == expectedResult.get(i)) {
                countOk += 1;
            }

            if (expectedResult.get(i) == true) {
                if (percentageDifferentSign > maxTrue) {
                    maxTrue = percentageDifferentSign;
                }
            } else {
                if (percentageDifferentSign < minFalse) {
                    minFalse = percentageDifferentSign;
                }
            }
            if (isDistanceOK == true) {
                averagePercentageDifferentSignTrue += percentageDifferentSign;
                countTrue += 1;

            } else {
                averagePercentageDifferentSignFalse += percentageDifferentSign;
                countFalse += 1;

            }
            if (visualInspection == true) {
                String fileNameHit = "testDistanceToOutsideGoodSuperpositionHit" + isDistanceOK + count;
                shapeContainer1a9u.exportRotatedShapeColoredToPDBFile(fileNameHit, algoParameters, result);
            }

            count += 1;
            if (count == 20) {
                break;
            }
        }
        if (visualInspection == true) {
            String fileNameQuery = "testDistanceToOutsideGoodSuperpositionQuery";
            shapeContainer1bmk.exportShapeColoredToPDBFile(fileNameQuery, algoParameters);
        }

        System.out.println("For info: countFN = " + countFN + " countFP = " + countFP + " maxTrue = " + maxTrue + " minFalse = " + minFalse);

        // Must be as we dont want to loose potential good hits
        assertTrue(countFN == 0);
        assertTrue(countFP == 1);

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
