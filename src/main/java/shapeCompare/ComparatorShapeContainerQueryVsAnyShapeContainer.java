package shapeCompare;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

import fingerprint.CannotCompareDistributionException;
import fingerprint.DistributionComparisonTools;
import hits.Hit;
import math.ToolsMath;
import multithread.ExtendPairingRecursiveTask;
import multithread.FindMatchingTriangleRecursiveTask;
import parameters.AlgoParameters;
import pointWithProperties.PointWithPropertiesIfc;
import scorePairing.ScorePairing;
import shape.ShapeContainerIfc;
import shapeReduction.TriangleInteger;

public class ComparatorShapeContainerQueryVsAnyShapeContainer {
    //------------------------
    // Constant
    //------------------------
    public boolean debug = false;
    private List<Hit> emptyList = new ArrayList<>();


    //------------------------
    // Class variables
    //------------------------
    ShapeContainerIfc shapeContainerQuery;
    ShapeContainerIfc shapeContainerAnyShape;
    AlgoParameters algoParameters;


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public ComparatorShapeContainerQueryVsAnyShapeContainer(ShapeContainerIfc shapeContainerQuery, ShapeContainerIfc shapeContainerAnyShape, AlgoParameters algoParameters) {

        this.shapeContainerQuery = shapeContainerQuery;
        this.shapeContainerAnyShape = shapeContainerAnyShape;
        this.algoParameters = algoParameters;
    }


    // -------------------------------------------------------------------
    // Public && Interface method
    // -------------------------------------------------------------------
    public List<Hit> computeResults() throws NullResultFromAComparisonException {

        try {
            float tanimotoHistogramProperties = DistributionComparisonTools.computeSubDistributionTanimoto(shapeContainerQuery.getHistogramStrikingProperties(), shapeContainerAnyShape.getHistogramStrikingProperties());
            float tanimotoHistogramProperties2 = DistributionComparisonTools.computeSubDistributionTanimoto(shapeContainerQuery.getHistogramD2(), shapeContainerAnyShape.getHistogramD2());
            //float distance = DistributionComparisonTools.computeDistance(shapeContainerQuery.getHistogramStrikingProperties(), shapeContainerAnyShape.getHistogramStrikingProperties());
            //float distance2 = DistributionComparisonTools.computeDistance(shapeContainerQuery.getHistogramD2(), shapeContainerAnyShape.getHistogramD2());

            System.out.println("getHistogramStrikingProperties getHistogramD2");
            System.out.println("fingerprint = " + tanimotoHistogramProperties + "  " + tanimotoHistogramProperties2);
            //System.out.println("distance = " + distance + "  " + distance2);
            if (tanimotoHistogramProperties < 0.4 || tanimotoHistogramProperties2 < 0.6) {
                //List<Hit> emptyHitList = new ArrayList<>();
                System.out.println("comparison skipped because of Fingerprint ");
                //return emptyList;
            }

        } catch (CannotCompareDistributionException e2) {
            e2.printStackTrace();

        }

        List<ResultsFromEvaluateCost> resultsPairingTriangleSeed = CompareTools.compareShapesBasedOnTriangles(shapeContainerQuery, shapeContainerAnyShape, algoParameters);

        List<PairingAndNullSpaces> listExtendedPair = getExtendedPairingAndNullSpaces(resultsPairingTriangleSeed);

        ScorePairing scorePairingBasedOnShape = new ScorePairing(shapeContainerQuery.getShape(), shapeContainerAnyShape.getShape(), algoParameters);
        List<ResultsFromEvaluateCost> resultsExtendedPairing = scorePairingBasedOnShape.getCostOfaListOfPairing(listExtendedPair);


        // Remove hit where hit is not enough matching query based on covergage
        //int before = resultsExtendedPairing.size();
        Iterator<ResultsFromEvaluateCost> it = resultsExtendedPairing.iterator();
        while (it.hasNext()) {

            ResultsFromEvaluateCost nextResult = it.next();
            float fractionNeededOnHit = algoParameters.getFRACTION_NEEDED_ON_QUERY();
            float ratioPairedPointInQuery = nextResult.getRatioPairedPointInQuery();

            boolean isDistanceToOutsideOk = checkDistanceToOutside(nextResult, shapeContainerQuery, shapeContainerAnyShape);

            if (ratioPairedPointInQuery < fractionNeededOnHit || isDistanceToOutsideOk == false) {
                it.remove();
                continue;
            }
        }

        List<Hit> hitsExtendedPairing = PairingTools.generateHitsListFromResultList(resultsExtendedPairing, shapeContainerAnyShape, shapeContainerQuery, algoParameters);
        Collections.sort(hitsExtendedPairing, new PairingTools.LowestCostHitComparator());

//		System.out.println("hits : ");
//		for (Hit hit: hitsExtendedPairing){
//			System.out.println(hit.getResultsFromEvaluateCost().getCoverage());
//		}
        if (hitsExtendedPairing.size() > 0) {
            System.out.println();
        }
        return hitsExtendedPairing;
    }

    private List<PairingAndNullSpaces> getExtendedPairingAndNullSpaces(List<ResultsFromEvaluateCost> resultsPairingTriangleSeed) {

        int countOfSubpacket = algoParameters.getSUB_THREAD_COUNT_FORK_AND_JOIN();
        int threshold = resultsPairingTriangleSeed.size() / countOfSubpacket + 1;
        if (threshold < 2) {
            threshold = 2;
        }

        ForkJoinPool pool = new ForkJoinPool();
        ExtendPairingRecursiveTask computeExtendedPairings = new ExtendPairingRecursiveTask(resultsPairingTriangleSeed, 0, resultsPairingTriangleSeed.size() - 1, threshold, shapeContainerQuery.getShape(), shapeContainerAnyShape.getShape(), algoParameters);
        List<PairingAndNullSpaces> listExtendedPair = pool.invoke(computeExtendedPairings);
        pool.shutdownNow();
        return listExtendedPair;
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





//	private boolean isItTooCloseToOneAlreadyThere(List<ResultsFromEvaluateCost> listResults, ResultsFromEvaluateCost candidateResult){
//
//		for (ResultsFromEvaluateCost resultFromList: listResults){
//
//			double distanceR = computeDistanceOnRotMatrix(resultFromList, candidateResult);
//			double distanceT = computeDistanceOnTranslation(resultFromList, candidateResult);
//
//			//System.out.println("distance R = " + distanceR + "  distanceT = " + distanceT);
//			if (distanceT < 2.0 * algoParameters.getCELL_DIMENSION_OF_THE_PROBABILITY_MAP_ANGSTROM() && distanceR < 60.0 / 180.0){ // 2.8 / 6.0
//				return true;
//			}
//		}
//		return false;
//	}


}
