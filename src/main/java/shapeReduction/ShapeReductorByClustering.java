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
package shapeReduction;

import java.util.*;
import java.util.Map.Entry;

import math.MathTools;
import org.apache.commons.math3.geometry.euclidean.threed.SphericalCoordinates;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import math.ClusteringByLinkageWithAnUpdatedAndSortedCollectionOfClusterPairs;
import math.ClusteringByLinkageWithAnUpdatedAndSortedCollectionOfClusterPairs.ClusteringLinkageType;
import math.EquidistributionPhi;
import parameters.AlgoParameters;
import pointWithProperties.CollectionOfPointsWithPropertiesIfc;
import pointWithProperties.IdAndPointWithProperties;
import pointWithProperties.PointIfc;
import pointWithProperties.PointWithPropertiesIfc;
import pointWithProperties.StrikingProperties;
import pointWithProperties.StrikingPropertiesTools;

public class ShapeReductorByClustering implements ShapeReductorIfc {
    // -------------------------------------------------------------------
    // Class variables
    // -------------------------------------------------------------------
    private CollectionOfPointsWithPropertiesIfc shapeCollectionPoints;
    private AlgoParameters algoParameters;

    private List<Float> radiusValues;
    private boolean debug = false;

    private PointIfc barycenterShape;

    private Map<PointWithPropertiesIfc, Integer> mapPointToOriginalId;


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public ShapeReductorByClustering(CollectionOfPointsWithPropertiesIfc shapeCollectionPoints, AlgoParameters algoParameters) {
        this.shapeCollectionPoints = shapeCollectionPoints;
        this.barycenterShape = ShapeReductorTools.computeBarycenterOfACollectionOfPointCoords(shapeCollectionPoints);
        this.mapPointToOriginalId = fillMap();
        this.algoParameters = algoParameters;
    }


    // -------------------------------------------------------------------
    // Public Interface
    // -------------------------------------------------------------------
    @Override
    public Map<Integer, PointWithPropertiesIfc> computeReducedCollectionOfPointsWithProperties() {

        Map<StrikingProperties, Map<PhiThetaRadiusInterval, Map<Integer, PointWithPropertiesIfc>>> mapPropertyAndMapSectorAndPoints = distributePointsInSectorsAndGroupPerProperty();
        Map<Integer, PointWithPropertiesIfc> minishape = generateMiniShape(mapPropertyAndMapSectorAndPoints);
        return minishape;
    }


    // -------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------
    private Map<PointWithPropertiesIfc, Integer> fillMap() {

        Map<PointWithPropertiesIfc, Integer> mapPointToOriginalId = new LinkedHashMap<>();
        for (int i = 0; i < shapeCollectionPoints.getSize(); i++) {
            mapPointToOriginalId.put(shapeCollectionPoints.getPointFromId(i), i);
        }
        return mapPointToOriginalId;
    }


    private Map<Integer, PointWithPropertiesIfc> generateMiniShape(Map<StrikingProperties, Map<PhiThetaRadiusInterval, Map<Integer, PointWithPropertiesIfc>>> mapPropertyAndMapSectorAndPoints) {

        Map<Integer, PointWithPropertiesIfc> collectionOfPointsWithProperties = doClustering(mapPropertyAndMapSectorAndPoints);
        Map<PointWithPropertiesIfc, PointWithPropertiesIfc> pairsPointCloseBy = findPointsTooCloseWithASharedStrikingProperties(collectionOfPointsWithProperties);

        for (Entry<PointWithPropertiesIfc, PointWithPropertiesIfc> pairPoint : pairsPointCloseBy.entrySet()) {
            Integer idOfPoint = mapPointToOriginalId.get(ShapeReductorTools.returnPointWithLowerPriorityWhenThereIsAMatchingProperty(pairPoint.getKey(), pairPoint.getValue()));
            collectionOfPointsWithProperties.remove(idOfPoint);
        }

        removePointsOfStrikingPropertiesNoneIfCloseEnoughToAnotherPointWithAnyStrikingPropertiesNotNone(collectionOfPointsWithProperties);

        return collectionOfPointsWithProperties;
    }


    /**
     * @param collectionOfPointsWithProperties
     * @return
     */
    private Map<PointWithPropertiesIfc, PointWithPropertiesIfc> findPointsTooCloseWithASharedStrikingProperties(Map<Integer, PointWithPropertiesIfc> collectionOfPointsWithProperties) {

        Map<PointWithPropertiesIfc, PointWithPropertiesIfc> pairsPointCloseBy = new LinkedHashMap<>();

        for (Entry<Integer, PointWithPropertiesIfc> entry : collectionOfPointsWithProperties.entrySet()) {
            A:
            for (Entry<Integer, PointWithPropertiesIfc> entry2 : collectionOfPointsWithProperties.entrySet()) {

                PointWithPropertiesIfc pointWithProperties1 = entry.getValue();
                PointWithPropertiesIfc pointWithProperties2 = entry2.getValue();

                if (pointWithProperties1 != pointWithProperties2) {
                    double distance = (float) MathTools.computeDistance(pointWithProperties1.getCoords().getCoords(), pointWithProperties2.getCoords().getCoords());
                    //MathTools.computeDistance(pointWithProperties1.getCoords().getCoords(), pointWithProperties2.getCoords().getCoords());
                    if (distance < ((algoParameters.getCELL_DIMENSION_OF_THE_PROBABILITY_MAP_ANGSTROM() * Math.sqrt(3)) + 0.1)) {

                        int matchingPropertyCount = StrikingPropertiesTools.evaluatePointsMatchingWithAllProperties(pointWithProperties1, pointWithProperties2);
                        if (matchingPropertyCount != 0) {
                            if (pairsPointCloseBy.containsKey(entry2.getValue())) {
                                if (pairsPointCloseBy.get(entry2.getValue()).equals(entry.getValue())) {
                                    // already there
                                    continue A;
                                }
                            }
                            pairsPointCloseBy.put(entry.getValue(), entry2.getValue());
                        }
                    }
                }
            }
        }
        return pairsPointCloseBy;
    }


    private Map<Integer, PointWithPropertiesIfc> doClustering(Map<StrikingProperties, Map<PhiThetaRadiusInterval, Map<Integer, PointWithPropertiesIfc>>> mapPropertyAndMapSectorAndPoints) {

        Map<Integer, PointWithPropertiesIfc> miniShape = new LinkedHashMap<>();

        for (Entry<StrikingProperties, Map<PhiThetaRadiusInterval, Map<Integer, PointWithPropertiesIfc>>> mapSectorAndPoint : mapPropertyAndMapSectorAndPoints.entrySet()) {

            List<PointWithPropertiesIfc> listBarycenters = new ArrayList<>();
            for (Entry<PhiThetaRadiusInterval, Map<Integer, PointWithPropertiesIfc>> sectorAndPoints : mapSectorAndPoint.getValue().entrySet()) {

                PointWithPropertiesIfc pointWithProperties = ShapeReductorTools.getPointClosestToBarycenter(sectorAndPoints.getValue(), miniShape);
                if (pointWithProperties != null) {
                    listBarycenters.add(pointWithProperties);
                }
            }

            if (listBarycenters.size() == 0) {
                continue;
            }

            Collections.sort(listBarycenters, new PointOnlyDistanceToBarycenterComparator(this.barycenterShape));

            ClusteringByLinkageWithAnUpdatedAndSortedCollectionOfClusterPairs completeLinkageClustering = new ClusteringByLinkageWithAnUpdatedAndSortedCollectionOfClusterPairs(listBarycenters, algoParameters, ClusteringLinkageType.SINGLE_LINKAGE);
            List<List<PointWithPropertiesIfc>> clusteredPoints = completeLinkageClustering.getClusteredPoints();
            if (debug == true) {
                System.out.println("completeLinkageClustering of property : " + mapSectorAndPoint.getKey().toString() + " from " + listBarycenters.size() + " to " + clusteredPoints.size());
            }

            for (List<PointWithPropertiesIfc> clusterForThisProperty : clusteredPoints) {

                PointWithPropertiesIfc onePointPerCluster = ShapeReductorTools.selectOnePointFromAClusterButNotAlreadyInMinishapeIfPossible(clusterForThisProperty, miniShape);
                if (onePointPerCluster != null) {
                    Integer idOfPoint = mapPointToOriginalId.get(onePointPerCluster);
                    //onePointPerCluster.setMiniShapeStrikingProperty(mapSectorAndPoint.getKey()); // not used afterwards but maybe useful
                    if (miniShape.containsKey(idOfPoint)) {
                        System.out.println("miniShape.containsKey(idOfPoint) so will override, which one to choose ?? ");
                    }
                    miniShape.put(idOfPoint, onePointPerCluster);
                }
            }
        }
        return miniShape;
    }


    private void removePointsOfStrikingPropertiesNoneIfCloseEnoughToAnotherPointWithAnyStrikingPropertiesNotNone(Map<Integer, PointWithPropertiesIfc> collectionOfPointsWithProperties) {

        double threshold = algoParameters.getTHRESHOLD_DISTANCE_TO_KEEP_NEIGHBORING_NONE_STRIKING_PROPERTY();
        double distance;
        Set<Integer> setSomeNonePointsToRemove = new HashSet<>();

        A:
        for (Entry<Integer, PointWithPropertiesIfc> point : collectionOfPointsWithProperties.entrySet()) {

            List<StrikingProperties> listStrikingPropertiesForThisPoint = point.getValue().getStrikingProperties();
            if ((listStrikingPropertiesForThisPoint.size() == 1) && listStrikingPropertiesForThisPoint.get(0).equals(StrikingProperties.NONE)) {

                for (Entry<Integer, PointWithPropertiesIfc> neighBohrpoint : collectionOfPointsWithProperties.entrySet()) {
                    if (neighBohrpoint == point) {
                        continue;
                    }
                    List<StrikingProperties> listStrikingPropertiesForThisNeighBohrpoint = neighBohrpoint.getValue().getStrikingProperties();
                    if ((listStrikingPropertiesForThisNeighBohrpoint.size() != 0) && (!listStrikingPropertiesForThisNeighBohrpoint.contains(StrikingProperties.NONE))) {

                        distance = MathTools.computeDistance(point.getValue().getCoords().getCoords(), neighBohrpoint.getValue().getCoords().getCoords());
                        if (distance < threshold) {
                            setSomeNonePointsToRemove.add(point.getKey());
                            continue A;
                        }
                    }
                }
            }
        }

        for (Integer pointToRemove : setSomeNonePointsToRemove) {
            collectionOfPointsWithProperties.remove(pointToRemove);
        }
    }


    // -------------------------------------------------------------------
    // Private & Implementation Methods
    // -------------------------------------------------------------------
    /**
     * Each sector is multiply by different striking properties found in
     *
     * @return
     */
    private Map<StrikingProperties, Map<PhiThetaRadiusInterval, Map<Integer, PointWithPropertiesIfc>>> distributePointsInSectorsAndGroupPerProperty() {

        Map<PhiThetaRadiusInterval, Map<Integer, PointWithPropertiesIfc>> mapSectorAndPointsAllSectors = groupPoints();
        Map<PhiThetaRadiusInterval, Map<Integer, PointWithPropertiesIfc>> mapSectorAndPoints = removeEmptySectors(mapSectorAndPointsAllSectors);


        // group point having exactly the same properties, inlcuding 1 or 2 or more
        Map<PhiThetaRadiusInterval, List<StrikingProperties>> mapSectorAndListProperties = computePropertiesOfGroups(mapSectorAndPoints);
        Map<StrikingProperties, Map<PhiThetaRadiusInterval, Map<Integer, PointWithPropertiesIfc>>> mapPropertyAndMapSectorAndPoints = multiplySectorByProperty(mapSectorAndPoints, mapSectorAndListProperties);
        return mapPropertyAndMapSectorAndPoints;
    }


    private Map<StrikingProperties, Map<PhiThetaRadiusInterval, Map<Integer, PointWithPropertiesIfc>>> multiplySectorByProperty(Map<PhiThetaRadiusInterval, Map<Integer, PointWithPropertiesIfc>> mapSectorAndPoints, Map<PhiThetaRadiusInterval, List<StrikingProperties>> mapSectorAndListProperties) {

        Map<StrikingProperties, Map<PhiThetaRadiusInterval, Map<Integer, PointWithPropertiesIfc>>> mapPropertyAndMapSectorAndPoints = new LinkedHashMap<>();

        // Initialize the returned Map with all predefined properties
        StrikingProperties[] allProperties = StrikingProperties.values();

        for (StrikingProperties strikingProperty : allProperties) {
            Map<PhiThetaRadiusInterval, Map<Integer, PointWithPropertiesIfc>> instanciatedMap = new LinkedHashMap<>();

            mapPropertyAndMapSectorAndPoints.put(strikingProperty, instanciatedMap);
        }


        // TODO if I use mapSectorAndListProperties only here then I should modify to get directly splitted sectors without making mapSectorAndListProperties
        for (Entry<PhiThetaRadiusInterval, Map<Integer, PointWithPropertiesIfc>> entry : mapSectorAndPoints.entrySet()) {

            PhiThetaRadiusInterval sector = entry.getKey();
            Map<Integer, PointWithPropertiesIfc> points = entry.getValue();

            List<StrikingProperties> strikingPropertiesFoundInThisSector = mapSectorAndListProperties.get(sector);

            for (StrikingProperties strikingProperty : strikingPropertiesFoundInThisSector) {
                Map<Integer, PointWithPropertiesIfc> pointsWithTheStrikingProperty = StrikingPropertiesTools.extractPointsHavingTheProperty(points, strikingProperty);

                Map<PhiThetaRadiusInterval, Map<Integer, PointWithPropertiesIfc>> mapWhereIwantToAdd = mapPropertyAndMapSectorAndPoints.get(strikingProperty);
                mapWhereIwantToAdd.put(sector, pointsWithTheStrikingProperty);
            }
        }
        return mapPropertyAndMapSectorAndPoints;
    }


    private Map<PhiThetaRadiusInterval, Map<Integer, PointWithPropertiesIfc>> groupPoints() {

        double deltaOnlyForTheta = Math.PI / (double) algoParameters.getCOUNT_OF_INCREMENT_ANGLE();

        int countOfIncrementAngle = algoParameters.getCOUNT_OF_INCREMENT_ANGLE();
        EquidistributionPhi equidistributionPhi = new EquidistributionPhi();
        List<Double> phiValues = equidistributionPhi.getMapCountOfIntervalsAndPointValues().get(countOfIncrementAngle);
        // theta in map ranges from -pi to +pi in agreement with apache spherical coodinates
        List<Double> tethaValues = ShapeReductorTools.doBinningThetaValues(deltaOnlyForTheta, algoParameters.getCOUNT_OF_INCREMENT_ANGLE());

        double maxRinThisShape = findMaxRadiusInThisShape(barycenterShape);
        radiusValues = doBinningRadiusValues(maxRinThisShape);

        SectorsIfc sectors = generateSector(deltaOnlyForTheta, phiValues, tethaValues, radiusValues);

        Map<PhiThetaRadiusInterval, Map<Integer, PointWithPropertiesIfc>> groupPoints = new LinkedHashMap<>();

        Iterator<PhiThetaRadiusInterval> it = sectors.iterator();
        while (it.hasNext()) {
            PhiThetaRadiusInterval sector = it.next();
            Map<Integer, PointWithPropertiesIfc> collectionOfPointsWithProperties = new LinkedHashMap<>();
            groupPoints.put(sector, collectionOfPointsWithProperties);
        }


        for (int i = 0; i < shapeCollectionPoints.getSize(); i++) {

            Integer pointIDToBeKept = i;

            float[] point = shapeCollectionPoints.getPointFromId(i).getCoords().getCoords();

            float[] pointRelativeToBarycenter = MathTools.v1minusV2(point, barycenterShape.getCoords());
            Vector3D pointRelativeToBarycenterV3d = new Vector3D(pointRelativeToBarycenter[0], pointRelativeToBarycenter[1], pointRelativeToBarycenter[2]);

            SphericalCoordinates pointShericalRelative = new SphericalCoordinates(pointRelativeToBarycenterV3d);

            PhiThetaRadiusInterval intervalForThisPoint = sectors.getIntervalFromSphericalCoordinates(pointShericalRelative);

            if (intervalForThisPoint == null) { // it could be that some points doesnt fit so I should make the binning a bit larger I guess
                continue;
            }
            Map<Integer, PointWithPropertiesIfc> groupWherePointToAdd = groupPoints.get(intervalForThisPoint);
            groupWherePointToAdd.put(pointIDToBeKept, shapeCollectionPoints.getPointFromId(i));
        }

        return groupPoints;
    }


    private SectorsIfc generateSector(double deltaOnlyForTheta, List<Double> phiValues, List<Double> tethaValues, List<Float> radiusValues) {

        SectorsIfc sectors = new Sectors();

        for (int i = 0; i < phiValues.size() - 1; i++) {  // phi values are from 0 to PI by construction so I dont take PI
            double minPhi = phiValues.get(i);
            double maxPhi = phiValues.get(i + 1);

            for (Double tetha : tethaValues) {

                for (int k = 0; k < radiusValues.size() - 1; k++) {

                    double minR = radiusValues.get(k);
                    double maxR = radiusValues.get(k + 1);
                    double minTheta = tetha;
                    double maxTheta = tetha + deltaOnlyForTheta;
                    PhiThetaRadiusInterval phiThetaWithRinterval = new PhiThetaRadiusInterval(minPhi, maxPhi, minTheta, maxTheta, minR, maxR);
                    sectors.addSector(phiThetaWithRinterval);
                }
            }
        }
        return sectors;
    }


    private double findMaxRadiusInThisShape(PointIfc center) {

        double maxRadius = Double.MIN_VALUE;

        for (int i = 0; i < shapeCollectionPoints.getSize(); i++) {

            PointIfc point = shapeCollectionPoints.getPointFromId(i).getCoords();

            double distance = MathTools.computeDistance(center.getCoords(), point.getCoords());

            if (distance > maxRadius) {
                maxRadius = distance;
            }
        }

        return maxRadius;
    }


    private List<Float> doBinningRadiusValues(double maxRinThisShape) {

        List<Float> radiusValues = new ArrayList<>();

        radiusValues.add(0.0f);
        radiusValues.add(algoParameters.getFIRST_RADIUS_INCREMENT_IN_SHAPE_REDUCTION());

        int index = 2;
        float nextRadius;
        do {
            nextRadius = (float) (2.0 * Math.pow(radiusValues.get(index - 1), 3) - Math.pow(radiusValues.get(index - 2), 3));
            nextRadius = (float) Math.pow(nextRadius, 1.0 / 3.0);
            radiusValues.add(nextRadius);
            if (index == 99) {
                radiusValues.add(1000.0f);
                break;
            }
            index += 1;
        } while (nextRadius < maxRinThisShape);


        return radiusValues;
    }


    private Map<PhiThetaRadiusInterval, Map<Integer, PointWithPropertiesIfc>> removeEmptySectors(Map<PhiThetaRadiusInterval, Map<Integer, PointWithPropertiesIfc>> mapSectorAndPoints) {

        Map<PhiThetaRadiusInterval, Map<Integer, PointWithPropertiesIfc>> newMap = new LinkedHashMap<>();

        for (Entry<PhiThetaRadiusInterval, Map<Integer, PointWithPropertiesIfc>> entry : mapSectorAndPoints.entrySet()) {

            int sizeOfGroupOfPoints = entry.getValue().size();

            if (sizeOfGroupOfPoints > 0) {
                newMap.put(entry.getKey(), entry.getValue());
            }
        }

        return newMap;
    }


    private Map<PhiThetaRadiusInterval, List<StrikingProperties>> computePropertiesOfGroups(Map<PhiThetaRadiusInterval, Map<Integer, PointWithPropertiesIfc>> groupedPoints) {

        // TODO investigate why reproducibility is fixed by doing this treemap (and compare in PhiThethaInterval)
        Map<PhiThetaRadiusInterval, List<StrikingProperties>> mapGroupPropertiesForThisGroup = new TreeMap<>();

        for (Entry<PhiThetaRadiusInterval, Map<Integer, PointWithPropertiesIfc>> entry : groupedPoints.entrySet()) {

            Map<Integer, PointWithPropertiesIfc> pointsWithPropertiesInThisGroup = entry.getValue();
            List<StrikingProperties> propertiesForThisGroup = StrikingPropertiesTools.computeStrikingPropertiesOfAShape(pointsWithPropertiesInThisGroup);
            mapGroupPropertiesForThisGroup.put(entry.getKey(), propertiesForThisGroup);
        }

        return mapGroupPropertiesForThisGroup;
    }


    private class PointOnlyDistanceToBarycenterComparator implements Comparator<PointWithPropertiesIfc> {

        private PointIfc barycenter;

        public PointOnlyDistanceToBarycenterComparator(PointIfc barycenter) {
            this.barycenter = barycenter;
        }

        @Override
        public int compare(PointWithPropertiesIfc point1, PointWithPropertiesIfc point2) {

            float distancePoint1ToBarycenter = MathTools.computeDistance(barycenter.getCoords(), point1.getCoords().getCoords());
            float distancePoint2ToBarycenter = MathTools.computeDistance(barycenter.getCoords(), point2.getCoords().getCoords());
            if (distancePoint1ToBarycenter < distancePoint2ToBarycenter) {
                return -1;
            }
            if (distancePoint1ToBarycenter > distancePoint2ToBarycenter) {
                return 1;
            }
            return 0;
        }
    }
}

