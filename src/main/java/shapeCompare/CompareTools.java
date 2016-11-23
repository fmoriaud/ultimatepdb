package shapeCompare;

import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import math.ToolsMath;
import multithread.FindMatchingTriangleRecursiveTask;
import mystructure.*;
import parameters.AlgoParameters;
import pointWithProperties.PointWithPropertiesIfc;
import scorePairing.ScorePairing;
import shape.HasPeptideIfc;
import shape.ShapeContainerIfc;
import shape.ShapeContainerWithLigand;
import shape.ShapeContainerWithPeptide;
import shapeReduction.TriangleInteger;
import ultiJmol1462.Protonate;

import java.util.*;
import java.util.concurrent.ForkJoinPool;

/**
 * Created by Fabrice on 14/11/16.
 */
public class CompareTools {

    public static MyStructureIfc getLigandOrPeptideInReferenceOfQuery(ShapeContainerIfc shapeContainerAnyShape, ResultsFromEvaluateCost result, AlgoParameters algoParameters){


        MyStructureIfc clonedRotatedPeptideOrLigand = null;
        if (shapeContainerAnyShape instanceof ShapeContainerWithPeptide) {
            ShapeContainerWithPeptide shapeContainerWithPeptide = (ShapeContainerWithPeptide) shapeContainerAnyShape;
            MyChainIfc peptide = shapeContainerWithPeptide.getPeptide();
            Cloner cloner = new Cloner(peptide, algoParameters);
            clonedRotatedPeptideOrLigand = cloner.getRotatedClone(result);

        }
        if (shapeContainerAnyShape instanceof ShapeContainerWithLigand) {
            ShapeContainerWithLigand shapeContainerWithLigand = (ShapeContainerWithLigand) shapeContainerAnyShape;
            MyMonomerIfc ligand = shapeContainerWithLigand.getHetatmLigand();
            Cloner cloner = new Cloner(ligand, algoParameters);
            clonedRotatedPeptideOrLigand = cloner.getRotatedClone(result);
        }

        return clonedRotatedPeptideOrLigand;
    }

    /**
     * Compare two shapes based on triangle only.
     * Selection by diversity of hit. Filter based on distance to outside.
     *
     * @param shapeContainerQuery
     * @param shapeContainerAnyShape
     * @param algoParameters
     * @return
     */
    public static List<ResultsFromEvaluateCost> compareShapesBasedOnTriangles(ShapeContainerIfc shapeContainerQuery, ShapeContainerIfc shapeContainerAnyShape, AlgoParameters algoParameters) {


        List<TriangleInteger> listTriangleShape1 = shapeContainerQuery.getListTriangleOfPointsFromMinishape();
        List<TriangleInteger> listTriangleShape2 = shapeContainerAnyShape.getListTriangleOfPointsFromMinishape();


        List<PairingAndNullSpaces> listPairingTriangleSeed = getTrianglePairingAndNullSpaces(listTriangleShape1, listTriangleShape2, shapeContainerQuery, shapeContainerAnyShape, algoParameters);

        System.out.println("scoring " + listPairingTriangleSeed.size() + " pairs of triangles");

        ScorePairing scorePairingBasedOnMinishape = new ScorePairing(shapeContainerQuery.getMiniShape(), shapeContainerAnyShape.getMiniShape(), algoParameters);
        List<ResultsFromEvaluateCost> resultsPairingTriangleSeed = null;
        resultsPairingTriangleSeed = scorePairingBasedOnMinishape.getCostOfaListOfPairing(listPairingTriangleSeed);
        Collections.sort(resultsPairingTriangleSeed, new LowestCostPairingComparator());

        if (resultsPairingTriangleSeed.size() < 2) {
            return resultsPairingTriangleSeed;
        }

//		List<ResultsFromEvaluateCost> diverseList = new ArrayList<>();
//		diverseList.add(resultsPairingTriangleSeed.get(0));

        selectResultsByDiversityOfTransAndRotUsingHashSet(resultsPairingTriangleSeed);

        Iterator<ResultsFromEvaluateCost> it = resultsPairingTriangleSeed.iterator();
        while (it.hasNext()) {

            ResultsFromEvaluateCost nextResult = it.next();
            float ratioPairedPointInQuery = nextResult.getRatioPairedPointInQuery();

            boolean isDistanceToOutsideOk = checkDistanceToOutside(nextResult, shapeContainerQuery.getMiniShape(), shapeContainerAnyShape.getMiniShape());

            if (isDistanceToOutsideOk == false) {
                it.remove();
                continue;
            }
        }

        return resultsPairingTriangleSeed;
    }


    private static List<PairingAndNullSpaces> getTrianglePairingAndNullSpaces(List<TriangleInteger> listTriangleShape1, List<TriangleInteger> listTriangleShape2, ShapeContainerIfc shapeContainerQuery, ShapeContainerIfc shapeContainerAnyShape, AlgoParameters algoParameters) {

        int countOfSubpacket = algoParameters.getSUB_THREAD_COUNT_FORK_AND_JOIN();
        int threshold = listTriangleShape1.size() / countOfSubpacket + 1;
        if (threshold < 2) {
            threshold = 2;
        }
        ForkJoinPool pool = new ForkJoinPool();
        FindMatchingTriangleRecursiveTask computeTriangleSeedExtentions = new FindMatchingTriangleRecursiveTask(0, listTriangleShape1.size() - 1, threshold, listTriangleShape1, listTriangleShape2, shapeContainerQuery, shapeContainerAnyShape, algoParameters);
        List<PairingAndNullSpaces> listPairingTriangleSeed = pool.invoke(computeTriangleSeedExtentions);
        pool.shutdownNow();
        return listPairingTriangleSeed;
    }


    private static class LowestCostPairingComparator implements Comparator<ResultsFromEvaluateCost> {

        @Override
        public int compare(ResultsFromEvaluateCost cost1, ResultsFromEvaluateCost cost2) {

            if (cost1.getCost() > cost2.getCost()) {
                return 1;
            }
            if (cost1.getCost() < cost2.getCost()) {
                return -1;
            }
            return 0;
        }
    }


    /**
     * This method filters a list of results based on the trans vector and rot matrix to align hit on query.
     * It is based on a HashSet so the equals and hashcode of ResultsFromEvaluateCost is critical on the filtering
     *
     * @param resultsPairingTriangleSeed a list of results to filter
     */
    private static void selectResultsByDiversityOfTransAndRotUsingHashSet(List<ResultsFromEvaluateCost> resultsPairingTriangleSeed) {

        Set<ResultsFromEvaluateCost> uniqueSet = new HashSet<>();

        for (ResultsFromEvaluateCost result : resultsPairingTriangleSeed) {
            boolean added = uniqueSet.add(result);
            //System.out.println(added + "  " + uniqueSet.size());
        }

        resultsPairingTriangleSeed.clear();
        resultsPairingTriangleSeed.addAll(uniqueSet);
    }


    private static boolean checkDistanceToOutside(ResultsFromEvaluateCost result, Map<Integer, PointWithPropertiesIfc> queryMiniShape, Map<Integer, PointWithPropertiesIfc> hitMiniShape) {

        PairingAndNullSpaces currentNewPairingAndNewNullSpaces = result.getPairingAndNullSpaces();
        // This regression should detect very firmly if the hit ligand is on the same side as the query ligand
        //SimpleRegression regression = new SimpleRegression();
        int countCasesDifferentSign = 0;
        int countConsideredCases = 0;

        for (Map.Entry<Integer, Integer> entry : currentNewPairingAndNewNullSpaces.getPairing().entrySet()) {

            Integer idFromMap1a = entry.getKey();
            Integer idFromMap2a = entry.getValue();
            PointWithPropertiesIfc point1a = queryMiniShape.get(idFromMap1a);
            PointWithPropertiesIfc point2a = hitMiniShape.get(idFromMap2a);
            float distanceToOutsideOfPoint1a = point1a.getDistanceToLigand();
            float distanceToOutsideOfPoint2a = point2a.getDistanceToLigand();

            for (Map.Entry<Integer, Integer> entry2 : currentNewPairingAndNewNullSpaces.getPairing().entrySet()) {

                if (entry.getKey().equals(entry2.getKey()) && entry.getValue().equals(entry2.getValue())) {
                    continue;
                }

                Integer idFromMap1b = entry2.getKey();
                Integer idFromMap2b = entry2.getValue();
                PointWithPropertiesIfc point1b = queryMiniShape.get(idFromMap1b);
                PointWithPropertiesIfc point2b = hitMiniShape.get(idFromMap2b);
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
}
