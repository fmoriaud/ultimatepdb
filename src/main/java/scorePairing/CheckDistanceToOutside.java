package scorePairing;

import math.ToolsMath;
import pointWithProperties.PointWithPropertiesIfc;
import shape.ShapeContainerIfc;
import shapeCompare.PairingAndNullSpaces;
import shapeCompare.ResultsFromEvaluateCost;

import java.util.Map;

/**
 * Created by Fabrice on 24/11/16.
 */
public class CheckDistanceToOutside {

    private ResultsFromEvaluateCost result;
    private ShapeContainerIfc queryShape;
    private ShapeContainerIfc hitShape;


    private boolean distanceOk = false;


    public CheckDistanceToOutside(ResultsFromEvaluateCost result, ShapeContainerIfc queryShape, ShapeContainerIfc hitShape) {

        this.result = result;
        this.queryShape = queryShape;
        this.hitShape = hitShape;
        this.distanceOk = checkDistanceToOutside(result, queryShape, hitShape);
    }


    private static boolean checkDistanceToOutside(ResultsFromEvaluateCost result, ShapeContainerIfc queryShape, ShapeContainerIfc hitShape) {


        PairingAndNullSpaces currentNewPairingAndNewNullSpaces = result.getPairingAndNullSpaces();
        // This regression should detect very firmly if the hit ligand is on the same side as the query ligand
        //SimpleRegression regression = new SimpleRegression();
        int countCasesDifferentSign = 0;
        int countConsideredCases = 0;

        for (Map.Entry<Integer, Integer> entry : currentNewPairingAndNewNullSpaces.getPairing().entrySet()) {

            Integer idFromMap1a = entry.getKey();
            Integer idFromMap2a = entry.getValue();
            PointWithPropertiesIfc point1a = queryShape.get(idFromMap1a);
            PointWithPropertiesIfc point2a = hitShape.get(idFromMap2a);
            float distanceToOutsideOfPoint1a = point1a.getDistanceToLigand();
            float distanceToOutsideOfPoint2a = point2a.getDistanceToLigand();

            for (Map.Entry<Integer, Integer> entry2 : currentNewPairingAndNewNullSpaces.getPairing().entrySet()) {

                if (entry.getKey().equals(entry2.getKey()) && entry.getValue().equals(entry2.getValue())) {
                    continue;
                }

                Integer idFromMap1b = entry2.getKey();
                Integer idFromMap2b = entry2.getValue();
                PointWithPropertiesIfc point1b = queryShape.get(idFromMap1b);
                PointWithPropertiesIfc point2b = hitShape.get(idFromMap2b);
                float distanceToOutsideOfPoint1b = point1b.getDistanceToLigand();
                float distanceToOutsideOfPoint2b = point2b.getDistanceToLigand();

                float deltaQuery = distanceToOutsideOfPoint1a - distanceToOutsideOfPoint1b;
                float deltaHit = distanceToOutsideOfPoint2a - distanceToOutsideOfPoint2b;

                float distBetweenQueryPoints = ToolsMath.computeDistance(point1a.getCoords().getCoords(), point1b.getCoords().getCoords());
                if (distBetweenQueryPoints < 3.0f) { // I consider that the dist to ligand difference cannot be reliable if shape points are too close
                    continue;
                }

                countConsideredCases += 1;
                if (deltaQuery > 0) {
                    if (deltaHit < 0) {
                        countCasesDifferentSign += 1;
                    }
                } else {
                    if (deltaHit > 0) {
                        countCasesDifferentSign += 1;
                    }
                }

                //System.out.println(deltaQuery + "  " + deltaHit );
                //regression.addData(deltaQuery, deltaHit);
            }
        }

        float percentageDifferentSign = (float) countCasesDifferentSign / countConsideredCases;
        //System.out.println("percentageDifferentSign = " + percentageDifferentSign + " countConsideredCases =  " + countConsideredCases);

        //System.out.println("percentageDifferentSign = " + percentageDifferentSign);
        if (percentageDifferentSign > 0.4) {
            //System.out.println("hit deleted");
            return false;
        }
        return true;
    }


    public boolean isDistanceOk() {
        return distanceOk;
    }

}
