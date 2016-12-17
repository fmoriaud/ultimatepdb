package scorePairing;

import hits.Hit;
import io.Tools;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.linear.AbstractRealMatrix;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;
import protocols.ShapeContainerDefined;
import protocols.ShapecontainerDefinedByHetatm;
import shape.ShapeContainerIfc;
import shapeBuilder.ShapeBuildingException;
import shapeCompare.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 06.12.16.
 */
public class CheckDistanceToOutsideTest {

    @Test
    public void testDistanceToOutsideGoodSuperpositionForAGoodBurriedPocketSuperpositionCae() throws IOException, ParsingConfigFileException, ShapeBuildingException {


        boolean visualInspection = true;
        boolean debug = true;

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

        List<ResultsFromEvaluateCost> resultsExtendedPairing = CompareTools.compare(shapeContainer1bmk, shapeContainer1a9u, algoParameters);

        int count = 0;
        double averagePercentageDifferentSignTrue = 0;
        double averagePercentageDifferentSignFalse = 0;
        int countTrue = 0;
        int countFalse = 0;

        // determined by visual inspection of the superposition
        List<Boolean> expectedResult = new ArrayList<>();
        expectedResult.add(true); //0
        expectedResult.add(true);
        expectedResult.add(true);
        expectedResult.add(false); // 3
        expectedResult.add(false);
        expectedResult.add(false);
        expectedResult.add(false);
        expectedResult.add(false);
        expectedResult.add(false);
        expectedResult.add(false);
        expectedResult.add(false);
        expectedResult.add(true); // 11 true // not clear
        expectedResult.add(false);
        expectedResult.add(false);
        expectedResult.add(true); // 14 true
        expectedResult.add(false); // 15 true
        expectedResult.add(true); // 16 true
        expectedResult.add(true);
        expectedResult.add(true);
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

        // The code could be improved, currently we have 3 FP
        assertTrue(countFP == 2);
        // Check which one to detect changes
        assertTrue(listFP.get(0) == 4);
        assertTrue(listFP.get(1) == 6);
    }
}
