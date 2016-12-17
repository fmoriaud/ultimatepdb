package scorePairing;

import java.util.Map;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import math.ProcrustesAnalysisIfc;
import parameters.AlgoParameters;
import pointWithProperties.Enum.PropertyName;
import pointWithProperties.PointWithPropertiesIfc;
import shapeCompare.PairingAndNullSpaces;

public class ScorePairingWithStaticMethods {


    public static ResultFromScorePairing computeCost(PairingAndNullSpaces currentNewPairingAndNewNullSpaces, Map<Integer, PointWithPropertiesIfc> queryShape, Map<Integer, PointWithPropertiesIfc> hitShape, AlgoParameters algoParameters) {

        int countOfPairedPoint = currentNewPairingAndNewNullSpaces.getPairing().size();

        double[][] matrixPointsModelDouble = new double[3][countOfPairedPoint];
        double[][] matrixPointsCandidateDouble = new double[3][countOfPairedPoint];

        RealMatrix matrixPointsModel = new Array2DRowRealMatrix(matrixPointsModelDouble);
        RealMatrix matrixPointsCandidate = new Array2DRowRealMatrix(matrixPointsCandidateDouble);

        int currentPoint = 0;

        RealVector sumVector1 = new ArrayRealVector(new double[3]);
        RealVector sumVector2 = new ArrayRealVector(new double[3]);

        sumVector1.setEntry(0, 0.0);
        sumVector1.setEntry(1, 0.0);
        sumVector1.setEntry(2, 0.0);
        sumVector2.setEntry(0, 0.0);
        sumVector2.setEntry(1, 0.0);
        sumVector2.setEntry(2, 0.0);


        for (Map.Entry<Integer, Integer> entry : currentNewPairingAndNewNullSpaces.getPairing().entrySet()) {

            Integer point1id = Integer.valueOf(entry.getKey().intValue());
            Integer point2id = Integer.valueOf(entry.getValue().intValue());

            // the 2 points could be defined as static be static
            PointWithPropertiesIfc point1 = queryShape.get(point1id);
            PointWithPropertiesIfc point2 = hitShape.get(point2id);

            // matrix for procrustes
            matrixPointsModel.setEntry(0, currentPoint, point1.getCoords().getCoords()[0]);
            matrixPointsModel.setEntry(1, currentPoint, point1.getCoords().getCoords()[1]);
            matrixPointsModel.setEntry(2, currentPoint, point1.getCoords().getCoords()[2]);

            matrixPointsCandidate.setEntry(0, currentPoint, point2.getCoords().getCoords()[0]);
            matrixPointsCandidate.setEntry(1, currentPoint, point2.getCoords().getCoords()[1]);
            matrixPointsCandidate.setEntry(2, currentPoint, point2.getCoords().getCoords()[2]);

            // barycenter computation
            sumVector1.addToEntry(0, point1.getCoords().getCoords()[0]);
            sumVector1.addToEntry(1, point1.getCoords().getCoords()[1]);
            sumVector1.addToEntry(2, point1.getCoords().getCoords()[2]);

            sumVector2.addToEntry(0, point2.getCoords().getCoords()[0]);
            sumVector2.addToEntry(1, point2.getCoords().getCoords()[1]);
            sumVector2.addToEntry(2, point2.getCoords().getCoords()[2]);
            currentPoint += 1;

        }

        RealVector barycenterVector1 = sumVector1.mapDivide((double) currentPoint);
        RealVector barycenterVector2 = sumVector2.mapDivide((double) currentPoint);


        RealVector translationVectorToTranslateShape2ToOrigin = barycenterVector2.mapMultiply(-1.0);
        RealVector translationVectorToTranslateShape2ToShape1 = barycenterVector1.subtract(barycenterVector2);

        translateBarycenterListOfPointToOrigin(matrixPointsModel, barycenterVector1);
        translateBarycenterListOfPointToOrigin(matrixPointsCandidate, barycenterVector2);

        ProcrustesAnalysisIfc procrustesAnalysis = null;
        try {
            procrustesAnalysis = algoParameters.procrustesAnalysisBuffer.get();
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        procrustesAnalysis.run(matrixPointsModel, matrixPointsCandidate);

        RealMatrix rotationMatrixToRotateShape2ToShape1 = procrustesAnalysis.getRotationMatrix();

        try {
            algoParameters.procrustesAnalysisBuffer.put(procrustesAnalysis);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // not used: we dont really care how accurate is the overlap of points in pairs as it is within expected range because of the
        // fact that I extend hits with a distance criteria
        double distanceResidual = procrustesAnalysis.getResidual();
        // no need to use hit coverage as already used as a filter

        // no need to use the distance to outside as it is used as a filter

        // handling of probability to have a small contribution of if
        float toleranceElectronProbability = 0.2f;

        // use only properties for cost
        double costCharge = 0.0;
        double costHydrophobicity = 0.0;
        double costHbondDonnor = 0.0;
        double costHbondAcceptor = 0.0;
        double costDehydron = 0.0;
        double costAromaticRing = 0.0;
        double costProbabilityDiff = 0.0;

        for (Map.Entry<Integer, Integer> entry : currentNewPairingAndNewNullSpaces.getPairing().entrySet()) {

            Integer idFromMap1 = entry.getKey();
            Integer idFromMap2 = entry.getValue();
            PointWithPropertiesIfc point1 = queryShape.get(idFromMap1);
            PointWithPropertiesIfc point2 = hitShape.get(idFromMap2);

            float probabilityOfPoint1 = point1.getElectronProbability();
            float probabilityOfPoint2 = point2.getElectronProbability();
            float probabilityDiff = Math.abs(probabilityOfPoint1 - probabilityOfPoint2);
            if (probabilityDiff < toleranceElectronProbability) {
                probabilityDiff = 0.0f;
            } else {
                probabilityDiff -= toleranceElectronProbability;
            }
            costProbabilityDiff += probabilityDiff;


            // handle multi properties

            // Hydrophobe and Aromatic
            Float hydrophobicityOfPointFromMap1 = point1.get(PropertyName.Hydrophobicity);
            Float hydrophobicityOfPointFromMap2 = point2.get(PropertyName.Hydrophobicity);
            Float aromaticRingOfPointFromMap1 = point1.get(PropertyName.AromaticRing);
            Float aromaticRingOfPointFromMap2 = point2.get(PropertyName.AromaticRing);


            // if aromatic it is hydrophobe as well by definition
            if (aromaticRingOfPointFromMap1 != null && hydrophobicityOfPointFromMap1 == null){
                System.out.println(aromaticRingOfPointFromMap1 != null && hydrophobicityOfPointFromMap1 == null);
            }
            if (aromaticRingOfPointFromMap2 != null && hydrophobicityOfPointFromMap2 == null){
                System.out.println(aromaticRingOfPointFromMap2 != null && hydrophobicityOfPointFromMap2 == null);
            }
            // if one hydrophobe and not the other one
            // then cost hydrophobe
            if (hydrophobicityOfPointFromMap1 != null && hydrophobicityOfPointFromMap2 == null) {
                costHydrophobicity += returnCost(hydrophobicityOfPointFromMap1, hydrophobicityOfPointFromMap2);
            }
            if (hydrophobicityOfPointFromMap2 != null && hydrophobicityOfPointFromMap1 == null) {
                costHydrophobicity += returnCost(hydrophobicityOfPointFromMap1, hydrophobicityOfPointFromMap2);
            }
            // so a difference of hydrophobe to aromatic is neglected !!!

            Float chargeOfPointFromMap1 = point1.get(PropertyName.FormalCharge);
            Float chargeOfPointFromMap2 = point2.get(PropertyName.FormalCharge);
            costCharge += returnCost(chargeOfPointFromMap1, chargeOfPointFromMap2);

            Float hBondDonnorOfPointFromMap1 = point1.get(PropertyName.HbondDonnor);
            Float hBondDonnorOfPointFromMap2 = point2.get(PropertyName.HbondDonnor);
            Float hBondAcceptorOfPointFromMap1 = point1.get(PropertyName.HbondAcceptor);
            Float hBondAcceptorOfPointFromMap2 = point2.get(PropertyName.HbondAcceptor);

            costHbondDonnor += returnCost(hBondDonnorOfPointFromMap1, hBondDonnorOfPointFromMap2);
            costHbondAcceptor += returnCost(hBondAcceptorOfPointFromMap1, hBondAcceptorOfPointFromMap2);

            Float dehydronOfPointFromMap1 = point1.get(PropertyName.Dehydron);
            Float dehydronOfPointFromMap2 = point2.get(PropertyName.Dehydron);
            costDehydron += returnCost(dehydronOfPointFromMap1, dehydronOfPointFromMap2);

        }

        int size = currentNewPairingAndNewNullSpaces.getPairing().size();

        costCharge = costCharge / size;
        costHbondDonnor = costHbondDonnor / size;
        costHbondAcceptor = costHbondAcceptor / size;
        costDehydron = costDehydron / size;
        costHydrophobicity = costHydrophobicity / size;
        costAromaticRing = costAromaticRing / size;
        costProbabilityDiff = costProbabilityDiff / size;

        double costOnPairs =
                algoParameters.getWEIGHT_DIFFERENCE_IN_CHARGES_BETWEEN_PAIRED_POINTS() * costCharge +
                        algoParameters.getWEIGHT_DIFFERENCE_IN_HYDROPHOBICITY_BETWEEN_PAIRED_POINTS() * costHydrophobicity +
                        algoParameters.getWEIGHT_HBOND_DONNOR() * costHbondDonnor +
                        algoParameters.getWEIGHT_HBOND_ACCEPTOR() * costHbondAcceptor +
                        algoParameters.getWEIGHT_DEHYDRON() * costDehydron +
                        algoParameters.getWEIGHT_DIFFERENCE_AROMATICRING() * costAromaticRing +
                        algoParameters.getWEIGHT_DIFFERENCE_IN_PROBABILITIES_IN_PAIRED_POINTS() * costProbabilityDiff;

        //double costOnUnpairedPoints = computeRatioFromUnpairedPointsHitToAllPointsHits(currentNewPairingAndNewNullSpaces, hitShape);
        //double newFinalCost = costOnPairs + algoParameters.getWEIGHT_UNPAIRED_POINT_IN_SMALLEST_MAP() * costOnUnpairedPoints;

        double sumWeight = algoParameters.getWEIGHT_DIFFERENCE_IN_CHARGES_BETWEEN_PAIRED_POINTS() +
                algoParameters.getWEIGHT_DIFFERENCE_IN_HYDROPHOBICITY_BETWEEN_PAIRED_POINTS() +
                algoParameters.getWEIGHT_HBOND_DONNOR() +
                algoParameters.getWEIGHT_HBOND_ACCEPTOR() +
                algoParameters.getWEIGHT_DEHYDRON() +
                algoParameters.getWEIGHT_DIFFERENCE_AROMATICRING() +
                algoParameters.getWEIGHT_DIFFERENCE_IN_PROBABILITIES_IN_PAIRED_POINTS();

        double finalCost = costOnPairs / sumWeight;

        ResultFromScorePairing resultFromScorePairing = new ResultFromScorePairing(finalCost,
                rotationMatrixToRotateShape2ToShape1, translationVectorToTranslateShape2ToShape1,
                translationVectorToTranslateShape2ToOrigin, distanceResidual);
        return resultFromScorePairing;


    }


    private static float returnCost(Float value1, Float value2) {

        if (value1 != null && value2 == null) {
            return (float) Math.abs(value1);
        } else {
            if (value1 == null && value2 != null) {
                return (float) Math.abs(value2);
            } else {
                if (value1 != null && value2 != null) {
                    return (float) Math.abs(value1 - value2);
                }
            }
        }
        return 0.0f;
    }


    public static void translateBarycenterListOfPointToOrigin(RealMatrix matrix, RealVector barycenter) {

        int countOfPoint = matrix.getColumnDimension();

        for (int i = 0; i < countOfPoint; i++) {
            matrix.addToEntry(0, i, -1.0 * barycenter.getEntry(0));
            matrix.addToEntry(1, i, -1.0 * barycenter.getEntry(1));
            matrix.addToEntry(2, i, -1.0 * barycenter.getEntry(2));
        }
    }
}
