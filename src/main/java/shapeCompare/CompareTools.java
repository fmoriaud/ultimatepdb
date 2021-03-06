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

import math.MathTools;
import multithread.ExtendPairingRecursiveTask;
import multithread.FindMatchingTriangleRecursiveTask;
import mystructure.Cloner;
import mystructure.MyChainIfc;
import mystructure.MyMonomerIfc;
import mystructure.MyStructureIfc;
import parameters.AlgoParameters;
import pointWithProperties.PointWithPropertiesIfc;
import scorePairing.ScorePairing;
import shape.ShapeContainerIfc;
import shape.ShapeContainerWithLigand;
import shape.ShapeContainerWithPeptide;
import shapeReduction.TriangleInteger;

import java.util.*;
import java.util.concurrent.ForkJoinPool;

public class CompareTools {
    //-------------------------------------------------------------
    // Static methods
    //-------------------------------------------------------------
    public static List<ResultsFromEvaluateCost> compare(ShapeContainerIfc shapeContainerQuery, ShapeContainerIfc shapeContainerAnyShape, AlgoParameters algoParameters) {

        List<ResultsFromEvaluateCost> resultsPairingTriangleSeed = CompareTools.compareShapesBasedOnTriangles(shapeContainerQuery, shapeContainerAnyShape, algoParameters);
        List<PairingAndNullSpaces> listExtendedPair = CompareTools.getExtendedPairingAndNullSpaces(resultsPairingTriangleSeed, algoParameters, shapeContainerQuery, shapeContainerAnyShape);
        ScorePairing scorePairingBasedOnShape = new ScorePairing(shapeContainerQuery.getShape(), shapeContainerAnyShape.getShape(), algoParameters);
        List<ResultsFromEvaluateCost> resultsExtendedPairing = scorePairingBasedOnShape.getCostOfaListOfPairing(listExtendedPair);

        return resultsExtendedPairing;
    }


    public static List<PairingAndNullSpaces> getExtendedPairingAndNullSpaces(List<ResultsFromEvaluateCost> resultsPairingTriangleSeed, AlgoParameters algoParameters, ShapeContainerIfc shapeContainerQuery, ShapeContainerIfc shapeContainerAnyShape) {

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


    public static MyStructureIfc getLigandOrPeptideInReferenceOfQuery(ShapeContainerIfc shapeContainerAnyShape, ResultsFromEvaluateCost result, AlgoParameters algoParameters) {

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

        ScorePairing scorePairingBasedOnMinishape = new ScorePairing(shapeContainerQuery.getMiniShape(), shapeContainerAnyShape.getMiniShape(), algoParameters);
        List<ResultsFromEvaluateCost> resultsPairingTriangleSeed = null;
        resultsPairingTriangleSeed = scorePairingBasedOnMinishape.getCostOfaListOfPairing(listPairingTriangleSeed);
        Collections.sort(resultsPairingTriangleSeed, new LowestCostPairingComparator());

        if (resultsPairingTriangleSeed.size() < 2) {
            return resultsPairingTriangleSeed;
        }

        selectResultsByDiversityOfTransAndRotUsingHashSet(resultsPairingTriangleSeed);

        Iterator<ResultsFromEvaluateCost> it = resultsPairingTriangleSeed.iterator();
        while (it.hasNext()) {

            ResultsFromEvaluateCost nextResult = it.next();

            boolean isDistanceToOutsideOk = checkDistanceToOutside(nextResult, shapeContainerQuery.getMiniShape(), shapeContainerAnyShape.getMiniShape());

            if (isDistanceToOutsideOk == false) {
                it.remove();
                continue;
            }
        }

        return resultsPairingTriangleSeed;
    }


    // -------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------
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

                float distBetweenQueryPoints = MathTools.computeDistance(point1a.getCoords().getCoords(), point1b.getCoords().getCoords());
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
            }
        }

        float percentageDifferentSign = (float) countCasesDifferentSign / countConsideredCases;

        if (percentageDifferentSign > 0.4) {
            return false;
        }
        return true;
    }
}
