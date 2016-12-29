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
package math;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import parameters.AlgoParameters;
import pointWithProperties.PointWithPropertiesIfc;
import shapeReduction.PairOfListPointsWithDistance;

public class ClusteringByLinkageWithAnUpdatedAndSortedCollectionOfClusterPairs {

    public enum ClusteringLinkageType {

        COMPLETE_LINKAGE,
        SINGLE_LINKAGE,;
    }

    // -------------------------------------------------------------------
    // Class variables
    // -------------------------------------------------------------------
    private List<PointWithPropertiesIfc> listStartingPoint;
    private AlgoParameters algoParameters;

    private List<PairOfListPointsWithDistance> distancesBetweenClustersToBeKeptUpdated = new ArrayList<>();
    private List<List<PointWithPropertiesIfc>> listClusteredPoints;

    private ClusteringLinkageType clusteringLinkageType;


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public ClusteringByLinkageWithAnUpdatedAndSortedCollectionOfClusterPairs(List<PointWithPropertiesIfc> listStartingPoint, AlgoParameters algoParameters, ClusteringLinkageType clusteringLinkageType) {
        this.listStartingPoint = listStartingPoint;
        this.algoParameters = algoParameters;
        this.listClusteredPoints = new ArrayList<>();
        this.clusteringLinkageType = clusteringLinkageType;
    }


    // -------------------------------------------------------------------
    // Public & Interface Methods
    // -------------------------------------------------------------------
    public List<List<PointWithPropertiesIfc>> getClusteredPoints() {


        putEachPointInADifferentCluster();

        if (listStartingPoint.size() < 2) {
            return listClusteredPoints;
        }

        computeInitialSet();

        //long startTimeMs = System.currentTimeMillis();
        boolean clusteringIsDone = false;

        //System.out.println("clustering starts size = " + currentClustering.size());
        while (clusteringIsDone == false) {

            if (!distancesBetweenClustersToBeKeptUpdated.isEmpty()) {
                PairOfListPointsWithDistance closestpairOfListPointsWithDistance = distancesBetweenClustersToBeKeptUpdated.get(0); // distancesBetweenClustersToBeKeptUpdated.iterator().next();
                List<PointWithPropertiesIfc> newCluster = updateClusteringByAddingANewMerge(closestpairOfListPointsWithDistance);
                updateListOfPairWithdistance(closestpairOfListPointsWithDistance, newCluster);
            } else {
                clusteringIsDone = true;
            }
        }
        if (distancesBetweenClustersToBeKeptUpdated.size() != 0) {
            System.out.println("size before clearing = " + distancesBetweenClustersToBeKeptUpdated.size() + "  exit");
            System.exit(0);
        }

        return listClusteredPoints;
    }


    // -------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------
    private void computeInitialSet() {

        double limiteOfMaxDistanceToStopMerging = algoParameters.getLIMIT_MAX_DISTANCE_TO_STOP_MERGING();

        for (int i = 0; i < listClusteredPoints.size(); i++) {
            for (int j = i + 1; j < listClusteredPoints.size(); j++) {

                List<PointWithPropertiesIfc> cluster1 = listClusteredPoints.get(i);
                List<PointWithPropertiesIfc> cluster2 = listClusteredPoints.get(j);

                float distance = computeMaxDistanceBetweenTwoCluster(cluster1, cluster2);
                if (distance < limiteOfMaxDistanceToStopMerging) {
                    PairOfListPointsWithDistance PairOfListPointsWithDistance = new PairOfListPointsWithDistance(cluster1, cluster2, distance);
                    distancesBetweenClustersToBeKeptUpdated.add(PairOfListPointsWithDistance);
                }
            }
        }
        Collections.sort(distancesBetweenClustersToBeKeptUpdated, new PairOfListPointsWithDistanceComparator());
    }


    private boolean isTheTwoPairsHavingAClusterInCommon(PairOfListPointsWithDistance pair1, PairOfListPointsWithDistance pair2) {

        if (pair1.getListPoint1() == pair2.getListPoint1()) {
            return true;
        }
        if (pair1.getListPoint1() == pair2.getListPoint2()) {
            return true;
        }
        if (pair1.getListPoint2() == pair2.getListPoint1()) {
            return true;
        }
        if (pair1.getListPoint2() == pair2.getListPoint2()) {
            return true;
        }

        return false;
    }


    private void updateListOfPairWithdistance(PairOfListPointsWithDistance closestpairOfListPointsWithDistance, List<PointWithPropertiesIfc> newCluster) {

        double limiteOfMaxDistanceToStopMerging = algoParameters.getLIMIT_MAX_DISTANCE_TO_STOP_MERGING();

        // I must remove all pairs containing any of the two merged clusters
        Iterator<PairOfListPointsWithDistance> itr = distancesBetweenClustersToBeKeptUpdated.iterator();
        while (itr.hasNext()) {
            PairOfListPointsWithDistance pairOfListPointsWithDistance = itr.next();
            if (isTheTwoPairsHavingAClusterInCommon(pairOfListPointsWithDistance, closestpairOfListPointsWithDistance)) {
                itr.remove();
            }
        }
        // I must compute distances of the new cluster to all others
        for (List<PointWithPropertiesIfc> cluster : listClusteredPoints) {
            if (cluster == newCluster) {
                continue;
            }
            float distance = computeMaxDistanceBetweenTwoCluster(cluster, newCluster);
            if (distance < limiteOfMaxDistanceToStopMerging) {
                PairOfListPointsWithDistance pairOfListPointsWithDistance = new PairOfListPointsWithDistance(cluster, newCluster, distance);
                distancesBetweenClustersToBeKeptUpdated.add(pairOfListPointsWithDistance);
            }
        }

        Collections.sort(distancesBetweenClustersToBeKeptUpdated, new PairOfListPointsWithDistanceComparator());
    }


    private List<PointWithPropertiesIfc> updateClusteringByAddingANewMerge(PairOfListPointsWithDistance pairCluster) {

        List<PointWithPropertiesIfc> cluster1 = pairCluster.getListPoint1();
        List<PointWithPropertiesIfc> cluster2 = pairCluster.getListPoint2();

        List<PointWithPropertiesIfc> newCluster = new ArrayList<>();

        boolean cluster1Found = false;
        boolean cluster2Found = false;

        Iterator<List<PointWithPropertiesIfc>> itr = listClusteredPoints.iterator();
        while (itr.hasNext()) {
            List<PointWithPropertiesIfc> cluster = itr.next();

            if (cluster == cluster1) {
                newCluster.addAll(cluster);
                itr.remove();
                cluster1Found = true;
            }
            if (cluster == cluster2) {
                newCluster.addAll(cluster);
                itr.remove();
                cluster2Found = true;
            }
            if (cluster1Found == true && cluster2Found == true) {
                break;
            }
        }

        listClusteredPoints.add(newCluster);

        if ((cluster1Found == false) || (cluster2Found == false)) {
            System.out.println("problem cluster to merge is/are not found ");
            System.exit(0);
        }
        return newCluster;
    }


    private void putEachPointInADifferentCluster() {

        for (PointWithPropertiesIfc point : listStartingPoint) {

            List<PointWithPropertiesIfc> list = new ArrayList<>();
            list.add(point);
            listClusteredPoints.add(list);
        }
    }


    private float computeMaxDistanceBetweenTwoCluster(List<PointWithPropertiesIfc> cluster1, List<PointWithPropertiesIfc> cluster2) {

        float maxDistance = Float.MIN_VALUE;
        float mindistance = Float.MAX_VALUE;
        for (PointWithPropertiesIfc point1 : cluster1) {
            for (PointWithPropertiesIfc point2 : cluster2) {

                float distance = MathTools.computeDistance(point1.getCoords().getCoords(), point2.getCoords().getCoords());
                if (distance > maxDistance) {
                    maxDistance = distance;
                }
                if (distance < mindistance) {
                    mindistance = distance;
                }
            }
        }
        if (clusteringLinkageType == ClusteringLinkageType.COMPLETE_LINKAGE) {
            return maxDistance;
        }
        if (clusteringLinkageType == ClusteringLinkageType.SINGLE_LINKAGE) {
            return mindistance;
        }
        return 0.0f; // should never happen anyway
    }
}