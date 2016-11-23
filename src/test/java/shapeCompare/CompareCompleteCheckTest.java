package shapeCompare;

import hits.Hit;
import io.Tools;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;
import protocols.ShapeContainerDefined;
import protocols.ShapecontainerDefinedBySegmentOfChain;
import shape.ShapeContainerIfc;
import shapeBuilder.ShapeBuildingException;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 15/11/16.
 */
public class CompareCompleteCheckTest {


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

        CompareCompleteCheck compareCompleteCheck = new CompareCompleteCheck(shapecontainerDefinedQuery.getMyStructure(), queryShape, targetShape, algoParameters);
        List<Hit> results = null;
        try {
            results = compareCompleteCheck.computeResults();
        } catch (NullResultFromAComparisonException e) {
            e.printStackTrace();
        }

        System.out.println("Found " + results.size() + " hits");
    }
}
