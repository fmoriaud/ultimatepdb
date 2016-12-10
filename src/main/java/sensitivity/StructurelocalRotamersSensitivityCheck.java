package sensitivity;

import hits.Hit;
import mystructure.MyStructureIfc;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;
import protocols.ProtocolTools;
import protocols.ShapeContainerDefined;
import protocols.ShapecontainerDefinedByWholeChain;
import scorePairing.ScorePairingTools;
import shape.ShapeContainerIfc;
import shapeBuilder.ShapeBuildingException;
import shapeCompare.CompareCompleteCheck;
import shapeCompare.NullResultFromAComparisonException;
import shapeCompare.ResultsFromEvaluateCost;

import java.util.List;

/**
 * Created by Fabrice on 04.12.16.
 */
public class StructurelocalRotamersSensitivityCheck {


    public StructurelocalRotamersSensitivityCheck() {


    }


    public void compute() throws ParsingConfigFileException {

        AlgoParameters algoParameters = ProtocolTools.prepareAlgoParameters();
        // build the query
        String queryFourLetterCode = "1be9";
        String chainId = "B";

        ShapeContainerDefined shapeContainerbuilderQuery = new ShapecontainerDefinedByWholeChain(queryFourLetterCode.toCharArray(), chainId.toCharArray(), algoParameters);
        ShapeContainerIfc queryShape = null;
        try {
            queryShape = shapeContainerbuilderQuery.getShapecontainer();
        } catch (ShapeBuildingException e) {
            e.printStackTrace();
            System.exit(0);
        }

        ShapeContainerDefined shapeContainerbuilderTarget = new ShapecontainerDefinedByWholeChain(queryFourLetterCode.toCharArray(), chainId.toCharArray(), algoParameters);
        ShapeContainerIfc targetShape = null;
        try {
            targetShape = shapeContainerbuilderTarget.getShapecontainer();
        } catch (ShapeBuildingException e) {
            e.printStackTrace();
            System.exit(0);
        }

        CompareCompleteCheck compareCompleteCheck = new CompareCompleteCheck(queryShape, targetShape, algoParameters);
        List<Hit> hits = null;
        try {
            hits = compareCompleteCheck.computeResults();
        } catch (NullResultFromAComparisonException e) {
            e.printStackTrace();
        }
        Hit bestHit = hits.get(0);
        ResultsFromEvaluateCost result = bestHit.getResultsFromEvaluateCost();

        MyStructureIfc originalStructureLocal = queryShape.getMyStructureUsedToComputeShape();



        ResultsFromEvaluateCost resultCompleteCheck = ScorePairingTools.score(queryShape, targetShape, result, algoParameters);



    }
}
