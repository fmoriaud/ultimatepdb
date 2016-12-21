package shapeReduction;

import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.math3.geometry.euclidean.threed.SphericalCoordinates;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import math.ClusteringByLinkageWithAnUpdatedAndSortedCollectionOfClusterPairs;
import math.ClusteringByLinkageWithAnUpdatedAndSortedCollectionOfClusterPairs.ClusteringLinkageType;
import math.EquidistributionPhi;
import math.ToolsMath;
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
        this.barycenterShape = ToolsShapeReductor.computeBarycenterOfACollectionOfPointCoords(shapeCollectionPoints);
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

        // input map is ok each shape contains points with the correct property
        Map<Integer, PointWithPropertiesIfc> collectionOfPointsWithProperties = doClustering(mapPropertyAndMapSectorAndPoints);
        Map<PointWithPropertiesIfc, PointWithPropertiesIfc> pairsPointCloseBy = findPointsTooCloseWithASharedStrikingProperties(collectionOfPointsWithProperties);

        // They are closeby with q

        for (Entry<PointWithPropertiesIfc, PointWithPropertiesIfc> pairPoint : pairsPointCloseBy.entrySet()) {
            Integer idOfPoint = mapPointToOriginalId.get(ShapeReductorTools.returnPointWithLowerPriorityWhenThereIsAMatchingProperty(pairPoint.getKey(), pairPoint.getValue()));
            collectionOfPointsWithProperties.remove(idOfPoint);
        }

        removePointsOfStrikingPropertiesNoneIfCloseEnoughToAnotherPointWithAnyStrikingPropertiesNotNone(collectionOfPointsWithProperties);

        return collectionOfPointsWithProperties;
    }


    /**
     * Note: it is generating for matching 1-2 and 2-1. Is it needed ?
     *
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
                    double distance = (float) ToolsMath.computeDistance(pointWithProperties1.getCoords().getCoords(), pointWithProperties2.getCoords().getCoords());
                    //ToolsMath.computeDistance(pointWithProperties1.getCoords().getCoords(), pointWithProperties2.getCoords().getCoords());
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

        // debug code
        // One possibility is that the point was in two setors
/*
        for (Entry<StrikingProperties, Map<PhiThetaRadiusInterval, Map<Integer, PointWithPropertiesIfc>>> sectorAndPointsDebug : mapPropertyAndMapSectorAndPoints.entrySet()) {

            Map<PointWithPropertiesIfc, Integer> counts = new LinkedHashMap<>();

            Map<PhiThetaRadiusInterval, Map<Integer, PointWithPropertiesIfc>> pointsInThisSector = sectorAndPointsDebug.getValue();

            for (Entry<PhiThetaRadiusInterval, Map<Integer, PointWithPropertiesIfc>> entry : pointsInThisSector.entrySet()) {
                Map<Integer, PointWithPropertiesIfc> pointsWithProperty = entry.getValue();

                for (Entry<Integer, PointWithPropertiesIfc> entry2 : pointsWithProperty.entrySet()) {
                    PointWithPropertiesIfc point2 = entry2.getValue();
                    if (counts.containsKey(point2)) {
                        Integer currentcount = counts.get(point2);
                        counts.put(point2, currentcount += 1);
                    } else {
                        counts.put(point2, 1);
                    }
                }
            }
            for (Entry<PointWithPropertiesIfc, Integer> entry: counts.entrySet()){
                if (entry.getValue() != 1){
                    System.out.println("entry.getValue() != 1");
                }
            }
        }
        */


        Map<Integer, PointWithPropertiesIfc> miniShape = new LinkedHashMap<>();

        for (Entry<StrikingProperties, Map<PhiThetaRadiusInterval, Map<Integer, PointWithPropertiesIfc>>> mapSectorAndPoint : mapPropertyAndMapSectorAndPoints.entrySet()) {

            List<PointWithPropertiesIfc> listBarycenters = new ArrayList<>();
            // loop in a map of points of a given property
            for (Entry<PhiThetaRadiusInterval, Map<Integer, PointWithPropertiesIfc>> sectorAndPoints : mapSectorAndPoint.getValue().entrySet()) {

                // find the represenative point which is the point closest to barycenter
                PointWithPropertiesIfc pointWithProperties = ToolsShapeReductor.getPointClosestToBarycenter(sectorAndPoints.getValue(), miniShape);
                if (pointWithProperties != null) {

                    listBarycenters.add(pointWithProperties);
                }
                // TODO FMM That is an important change the points in Minishape can have simplified properties

            }

            if (listBarycenters.size() == 0) {
                continue;
            }

            // For the sake of reproducibility centers of sectors, called barycenters are sorted according to sector centers
            Collections.sort(listBarycenters, new PointOnlyDistanceToBarycenterComparator(this.barycenterShape));

            ClusteringByLinkageWithAnUpdatedAndSortedCollectionOfClusterPairs completeLinkageClustering = new ClusteringByLinkageWithAnUpdatedAndSortedCollectionOfClusterPairs(listBarycenters, algoParameters, ClusteringLinkageType.SINGLE_LINKAGE);
            List<List<PointWithPropertiesIfc>> clusteredPoints = completeLinkageClustering.getClusteredPoints();
            if (debug == true) {
                System.out.println("completeLinkageClustering of property : " + mapSectorAndPoint.getKey().toString() + " from " + listBarycenters.size() + " to " + clusteredPoints.size());
            }

            // I want only one point per cluster !!!!!!!!!!!!!!!!!!!!!
            for (List<PointWithPropertiesIfc> clusterForThisProperty : clusteredPoints) {

                PointWithPropertiesIfc onePointPerCluster = ToolsShapeReductor.selectOnePointFromAClusterButNotAlreadyInMinishapeIfPossible(clusterForThisProperty, miniShape);
                if (onePointPerCluster != null) {
                    Integer idOfPoint = mapPointToOriginalId.get(onePointPerCluster);
                    //onePointPerCluster.setMiniShapeStrikingProperty(mapSectorAndPoint.getKey()); // not used afterwards but maybe useful
                    if (miniShape.containsKey(idOfPoint)){
                        System.out.println("miniShape.containsKey(idOfPoint) so will override, which one to choose ?? ");
                    }
                    miniShape.put(idOfPoint, onePointPerCluster);
                }
            }
        }
        return miniShape;
    }


//	private void setOnlyOneProperty(PointWithProperties inputPoint, StrikingProperties strikingPropertyToKeepOnly){
//
//		List<StrikingProperties> listWithOnlyOneStrikingProperties = new ArrayList<>();
//
//		for (StrikingProperties strikingProperties: inputPoint.getStrikingProperties()){
//			if (strikingProperties.name().equals(strikingPropertyToKeepOnly.name())){
//				listWithOnlyOneStrikingProperties.add(strikingProperties);
//				break;
//			}
//		}
//		if (listWithOnlyOneStrikingProperties.size() == 0){
//			System.out.println("a point with chosen property is not available " + strikingPropertyToKeepOnly.name());
//		}
//		inputPoint.setStrikingProperties(listWithOnlyOneStrikingProperties);
//	}


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

                        distance = ToolsMath.computeDistance(point.getValue().getCoords().getCoords(), neighBohrpoint.getValue().getCoords().getCoords());
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

        //		int sizeAfter = collectionOfPointsWithProperties.size();
        //		System.out.println("found " + setSomeNonePointsToRemove.size() + " NONE points that can be removed because close enough a point with properties not NONE");
        //		System.out.println("sizeBefore = " + sizeBefore + " sizeAfter = " + sizeAfter);
        //		System.out.println();
    }


    //	private List<PointWithProperties> orderByPointID(List<PointWithProperties> listPointWithProperties, List<PointWithProperties> listShapePoints){
    //
    //		List<IdAndPointWithProperties> listToBeSorted = new ArrayList<>();
    //
    //		for (PointWithProperties point: listPointWithProperties){
    //			Integer pointId = listShapePoints.indexOf(point);
    //			listToBeSorted.add(new IdAndPointWithProperties(pointId, point));
    //		}
    //
    //		Collections.sort(listToBeSorted, new LowestIdIdAndPointWithPropertiesComparator());
    //
    //		List<PointWithProperties> listOrdered = new ArrayList<>();
    //		for (IdAndPointWithProperties point: listToBeSorted){
    //			listOrdered.add(point.point);
    //		}
    //
    //		return listOrdered;
    //	}


    public static class LowestIdIdAndPointWithPropertiesComparator implements Comparator<IdAndPointWithProperties> {

        @Override
        public int compare(IdAndPointWithProperties point1, IdAndPointWithProperties point2) {

            if (point1.id < point2.id) {
                return 1;
            }
            if (point1.id > point2.id) {
                return -1;
            }
            return 0;
        }
    }


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


    // -------------------------------------------------------------------
    // Private & Implementation Methods
    // -------------------------------------------------------------------
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

            // from one sector I do as many as there are striking properties and I add to the returned map
            List<StrikingProperties> strikingPropertiesFoundInThisSector = mapSectorAndListProperties.get(sector);

            for (StrikingProperties strikingProperty : strikingPropertiesFoundInThisSector) {
                Map<Integer, PointWithPropertiesIfc> pointsWithTheStrikingProperty = StrikingPropertiesTools.extractPointsHavingTheProperty(points, strikingProperty);

                // I have enough to create new entry in mapPropertyAndMapSectorAndPoints
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
        List<Double> tethaValues = ToolsShapeReductor.doBinningThetaValues(deltaOnlyForTheta, algoParameters.getCOUNT_OF_INCREMENT_ANGLE());

        double maxRinThisShape = findMaxRadiusInThisShape(barycenterShape);
        radiusValues = doBinningRadiusValues(maxRinThisShape);
        //System.out.println("radiusValues.size = " + radiusValues.size());

        // Now I built Sector object that is a Map with a key and an interval
        // it is easy a bijection
        // that is how I get the interval I want
        // TODO: bin according to R : I'll see about merging later on

        SectorsIfc sectors = generateSector(deltaOnlyForTheta, phiValues, tethaValues, radiusValues);

        // create the Map to return
        Map<PhiThetaRadiusInterval, Map<Integer, PointWithPropertiesIfc>> groupPoints = new LinkedHashMap<>();

        Iterator<PhiThetaRadiusInterval> it = sectors.iterator();
        while (it.hasNext()) {
            PhiThetaRadiusInterval sector = it.next();
            Map<Integer, PointWithPropertiesIfc> collectionOfPointsWithProperties = new LinkedHashMap<>();
            groupPoints.put(sector, collectionOfPointsWithProperties);
        }


        for (int i = 0; i < shapeCollectionPoints.getSize(); i++) {

            Integer pointIDToBeKept = i;

            // coords x y z of point
            float[] point = shapeCollectionPoints.getPointFromId(i).getCoords().getCoords();

            float[] pointRelativeToBarycenter = ToolsMath.v1minusV2(point, barycenterShape.getCoords());
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

            double distance = ToolsMath.computeDistance(center.getCoords(), point.getCoords());

            if (distance > maxRadius) {
                maxRadius = distance;
            }
        }

        return maxRadius;
    }


    private List<Float> doBinningRadiusValues(double maxRinThisShape) {

        //int countOfSectorRadius = (int) Math.round(maxRinThisShape / algoParameters.getFIRST_RADIUS_INCREMENT_IN_SHAPE_REDUCTION());
        //countOfSectorRadius += 2;

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

            float distancePoint1ToBarycenter = ToolsMath.computeDistance(barycenter.getCoords(), point1.getCoords().getCoords());
            float distancePoint2ToBarycenter = ToolsMath.computeDistance(barycenter.getCoords(), point2.getCoords().getCoords());
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

