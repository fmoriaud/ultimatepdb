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

    private ShapeContainerIfc queryShape;
    private ShapeContainerIfc hitShape;

    private int countCasesDifferentSign = 0;
    private int countConsideredCases = 0;
    private boolean distanceOk = false;


    public CheckDistanceToOutside(PairingAndNullSpaces pairingAndNullSpaces, ShapeContainerIfc queryShape, ShapeContainerIfc hitShape) {

        this.queryShape = queryShape;
        this.hitShape = hitShape;
        this.distanceOk = checkDistanceToOutside(pairingAndNullSpaces, queryShape, hitShape);
    }


    /**
     * Detect if the overlap leads to a ligand on the other side
     * For each paired point 1, take each paired point 2 (not closeby),
     * if in query the paired point 1 is closer to ligand than paired point 2, then it should be the same for the target
     * So same sign is good.
     * @param pairingAndNullSpaces
     * @param queryShape
     * @param hitShape
     * @return
     */
    private boolean checkDistanceToOutside(PairingAndNullSpaces pairingAndNullSpaces, ShapeContainerIfc queryShape, ShapeContainerIfc hitShape) {


        PairingAndNullSpaces currentNewPairingAndNewNullSpaces = pairingAndNullSpaces;
        // This regression should detect very firmly if the hit ligand is on the same side as the query ligand
        //SimpleRegression regression = new SimpleRegression();

        for (Map.Entry<Integer, Integer> entry : currentNewPairingAndNewNullSpaces.getPairing().entrySet()) {

            Integer idFromMap1a = entry.getKey();
            Integer idFromMap2a = entry.getValue();
            PointWithPropertiesIfc point1a = queryShape.get(idFromMap1a);
            PointWithPropertiesIfc point2a = hitShape.get(idFromMap2a);
            float distanceToOutsideOfPoint1Query = point1a.getDistanceToLigand();
            float distanceToOutsideOfPoint1Target = point2a.getDistanceToLigand();

            for (Map.Entry<Integer, Integer> entry2 : currentNewPairingAndNewNullSpaces.getPairing().entrySet()) {

                if (entry.getKey().equals(entry2.getKey()) && entry.getValue().equals(entry2.getValue())) {
                    continue;
                }

                Integer idFromMap1b = entry2.getKey();
                Integer idFromMap2b = entry2.getValue();
                PointWithPropertiesIfc point1b = queryShape.get(idFromMap1b);
                PointWithPropertiesIfc point2b = hitShape.get(idFromMap2b);
                if (point2b == null){
                    System.out.println();
                }
                float distanceToOutsideOfPoint2Query = point1b.getDistanceToLigand();
                float distanceToOutsideOfPoint2Target = point2b.getDistanceToLigand();

                float deltaQuery = distanceToOutsideOfPoint1Query - distanceToOutsideOfPoint2Query;
                float deltaHit = distanceToOutsideOfPoint1Target - distanceToOutsideOfPoint2Target;

                float distBetweenQueryPoints = ToolsMath.computeDistance(point1a.getCoords().getCoords(), point1b.getCoords().getCoords());
                if (distBetweenQueryPoints < 2.0f || distBetweenQueryPoints > 5f) {
                    // I consider that the dist to ligand difference cannot be reliable if shape points are too close or too far
                    // It is a local check of upside down hit
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
        if (percentageDifferentSign > 0.51) {
            //System.out.println("hit deleted");
            return false;
        }
        return true;
    }


    /**
     * For Junit tests
     *
     * @return
     */
    public int getCountCasesDifferentSign() {
        return countCasesDifferentSign;
    }

    /**
     * For Junit tests
     *
     * @return
     */
    public int getCountConsideredCases() {
        return countConsideredCases;
    }


    public boolean isDistanceOk() {
        return distanceOk;
    }

}
