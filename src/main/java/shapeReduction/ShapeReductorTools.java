package shapeReduction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import math.MathTools;
import pointWithProperties.*;


public class ShapeReductorTools {

    public static List<Double> doBinningThetaValues(double delta, int countOfIncrementAngle) {

        List<Double> thetaValues = new ArrayList<>();

        for (int j = 0; j < 2 * countOfIncrementAngle; j++) {
            double theta = -Math.PI + (double) j * delta;
            thetaValues.add(theta);
        }
        return thetaValues;
    }


    // change to use the same safe code as the next method. Maybe a bit silly code
    public static PointWithPropertiesIfc getPointClosestToBarycenter(Map<Integer, PointWithPropertiesIfc> pointsCluster, Map<Integer, PointWithPropertiesIfc> miniShape) {

        // I can sort point according to Id for reproducibility

        List<PointWithPropertiesIfc> listPoint = new ArrayList<>();
        for (Entry<Integer, PointWithPropertiesIfc> entry : pointsCluster.entrySet()) {
            listPoint.add(entry.getValue());
        }

        PointWithPropertiesIfc pointCloserToBarycenterPointWithProperties = selectOnePointFromACluster(listPoint, miniShape);
        return pointCloserToBarycenterPointWithProperties;
    }


    /**
     * Select One point from a cluster, among points not already in minishape, to avoid duplicates point
     *
     * @param listPoint
     * @param miniShape
     * @return
     */
    public static PointWithPropertiesIfc selectOnePointFromACluster(List<PointWithPropertiesIfc> listPoint, Map<Integer, PointWithPropertiesIfc> miniShape) {

        // TODO listPointsFreeOfMiniShapePoints is not needed anymore
        List<PointWithPropertiesIfc> listPointsFreeOfMiniShapePoints = new ArrayList<>();
        listPointsFreeOfMiniShapePoints.addAll(listPoint);
        //listPointsFreeOfMiniShapePoints.removeAll(miniShape.values());

        //Collections.sort(listPointsFreeOfMiniShapePoints, new PointIdComparator(listShapePoints)); // there I use the list of point then the minishqpe mqp format will be asier to get rid of

        //List<PointWithProperties> listPointToUse;
        if (listPointsFreeOfMiniShapePoints.size() == 0) {
            System.out.println("could not find a point not already in minishape so one minishape point is lost");
            return null;
        }
        PointIfc barycenter = computeBarycenterOfAListOfPoint(listPointsFreeOfMiniShapePoints);
        Collections.sort(listPointsFreeOfMiniShapePoints, new PointDistanceToBarycenterComparator(barycenter));
        //		System.out.println("dist ");
        //		for (PointWithProperties point: listPointsFreeOfMiniShapePoints){
        //			float distancePoint1ToBarycenter = MathTools.computeDistance(barycenter.getCoords(), point.getCoords().getCoords());
        //			System.out.println("dist = " + distancePoint1ToBarycenter + " count of striking prop = " + point.getStrikingProperties().size());
        //		}

        return listPointsFreeOfMiniShapePoints.get(0);
    }

    /**
     * Select One point from a cluster, among points not already in minishape, to avoid duplicates point
     *
     * @param listPoint
     * @param miniShape
     * @return
     */
    public static PointWithPropertiesIfc selectOnePointFromAClusterButNotAlreadyInMinishapeIfPossible(List<PointWithPropertiesIfc> listPoint, Map<Integer, PointWithPropertiesIfc> miniShape) {

        List<PointWithPropertiesIfc> listPointsFreeOfMiniShapePoints = new ArrayList<>();
        listPointsFreeOfMiniShapePoints.addAll(listPoint);
        listPointsFreeOfMiniShapePoints.removeAll(miniShape.values());

        //Collections.sort(listPointsFreeOfMiniShapePoints, new PointIdComparator(listShapePoints)); // there I use the list of point then the minishqpe mqp format will be asier to get rid of

        //List<PointWithProperties> listPointToUse;
        if (listPointsFreeOfMiniShapePoints.size() == 0) {
            System.out.println("could not find a point not already in minishape so one minishape point is lost");
            return null;
        }
        PointIfc barycenter = computeBarycenterOfAListOfPoint(listPointsFreeOfMiniShapePoints);
        Collections.sort(listPointsFreeOfMiniShapePoints, new PointDistanceToBarycenterComparator(barycenter));
        //		System.out.println("dist ");
        //		for (PointWithProperties point: listPointsFreeOfMiniShapePoints){
        //			float distancePoint1ToBarycenter = MathTools.computeDistance(barycenter.getCoords(), point.getCoords().getCoords());
        //			System.out.println("dist = " + distancePoint1ToBarycenter + " count of striking prop = " + point.getStrikingProperties().size());
        //		}

        return listPointsFreeOfMiniShapePoints.get(0);
    }


    private static class PointIdComparator implements Comparator<PointWithProperties> {

        private List<PointWithProperties> listShapePoints;

        public PointIdComparator(List<PointWithProperties> listShapePoints) {
            this.listShapePoints = listShapePoints;
        }

        @Override
        public int compare(PointWithProperties point1, PointWithProperties point2) {

            Integer point1Id = listShapePoints.indexOf(point1);
            Integer point2Id = listShapePoints.indexOf(point2);

            if (point1Id < point2Id) {
                return 1;
            }
            if (point1Id > point2Id) {
                return -1;
            }
            return 0;
        }
    }


    //	public static PointWithProperties selectOnePointFromAClusterButNotAlreadyInMinishapeIfPossibleOld(List<PointWithProperties> listPoint, List<PointWithProperties> listShapePoints, Map<Integer, PointWithProperties> miniShape){
    //
    //
    //		List<PointWithProperties> listPointsFreeOfMiniShapePoints = new ArrayList<>();
    //		listPointsFreeOfMiniShapePoints.addAll(listPoint);
    //		listPointsFreeOfMiniShapePoints.removeAll(miniShape.values());
    //
    //		//List<PointWithProperties> listPointToUse;
    //		if (listPointsFreeOfMiniShapePoints.size() == 0){
    //			System.out.println("could not find a point not already in minishape so one minishape point is lost");
    //			return null;
    //		}
    //		PointIfc barycenter = computeBarycenterOfAListOfPoint(listPointsFreeOfMiniShapePoints);
    //
    //		PointWithProperties pointCloserToBarycenter = null;
    //		Integer pointCloserToBarycenterID = null;
    //		double minDistance = Double.MAX_VALUE;
    //		double diffThreshold = 0.00001;
    //
    //		for (PointWithProperties point: listPointsFreeOfMiniShapePoints){
    //			float[] pointInShapecoords = point.getCoords().getCoords();
    //			float distance = MathTools.computeDistance(barycenter.getCoords(), pointInShapecoords);
    //
    //			if (distance < (minDistance - diffThreshold)){
    //				minDistance = distance;
    //				pointCloserToBarycenter = point;
    //				pointCloserToBarycenterID = listShapePoints.indexOf(point);
    //				continue;
    //			}
    //
    //			if (distance < (minDistance + diffThreshold)){
    //				// repechage si le point est presque a la meme distance en fait i should do the same exactly
    //				Integer sameDistancepointCloserToBarycenterID = listShapePoints.indexOf(point);
    //				if (sameDistancepointCloserToBarycenterID < pointCloserToBarycenterID){
    //					minDistance = distance;
    //					pointCloserToBarycenter = point;
    //					pointCloserToBarycenterID = sameDistancepointCloserToBarycenterID;
    //				}
    //			}
    //		}
    //		return pointCloserToBarycenter;
    //	}


    private static class PointDistanceToBarycenterComparator implements Comparator<PointWithPropertiesIfc> {

        private PointIfc barycenter;

        public PointDistanceToBarycenterComparator(PointIfc barycenter) {
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
            if (point1.getStrikingProperties().size() < point2.getStrikingProperties().size()) { // we favor if less striking properties: less chance to loose an important other one
                // as we change striking properties, not here but lqter on
                return -1;
            }
            if (point2.getStrikingProperties().size() < point1.getStrikingProperties().size()) { // we favor if less striking properties: less chance to loose an important other one
                return 1;
            }
            return 0;
        }
    }


    public static PointIfc computeLigandBarycenter(List<? extends PointIfc> ligandPoints) {

        float[] coord = new float[3];
        PointIfc barycenter = new Point(coord);

        for (PointIfc point : ligandPoints) {
            barycenter.getCoords()[0] += point.getCoords()[0];
            barycenter.getCoords()[1] += point.getCoords()[1];
            barycenter.getCoords()[2] += point.getCoords()[2];
        }
        for (int i = 0; i < 3; i++) {
            barycenter.getCoords()[i] /= ligandPoints.size();
        }
        return barycenter;
    }


    public static PointIfc computeBarycenterOfAListOfPoint(List<PointWithPropertiesIfc> shape) {

        float[] coord = new float[3];
        PointIfc barycenter = new Point(coord);

        for (PointWithPropertiesIfc point : shape) {
            barycenter.getCoords()[0] += point.getCoords().getCoords()[0];
            barycenter.getCoords()[1] += point.getCoords().getCoords()[1];
            barycenter.getCoords()[2] += point.getCoords().getCoords()[2];
        }
        for (int i = 0; i < 3; i++) {
            barycenter.getCoords()[i] /= shape.size();
        }
        return barycenter;

    }


//	public static PointIfc computeBarycenterOfACollectionOfPointCoordsRotated(Map<Integer, PointWithProperties> inputShape){
//
//		float[] coord = new float[3];
//		PointIfc barycenter = new Point(coord);
//
//		for (Entry<Integer, PointWithProperties> entry: inputShape.entrySet()) {
//			barycenter.getCoords()[0] += entry.getValue().getRotatedCoords().getCoords()[0];
//			barycenter.getCoords()[1] += entry.getValue().getRotatedCoords().getCoords()[1];
//			barycenter.getCoords()[2] += entry.getValue().getRotatedCoords().getCoords()[2];
//		}
//		for(int i=0; i<3; i++){
//			barycenter.getCoords()[i] /= inputShape.size();
//		}
//		return barycenter;
//
//	}


    public static PointIfc computeBarycenterOfACollectionOfPointCoords(CollectionOfPointsWithPropertiesIfc shapeCollectionPoints) {

        float[] coord = new float[3];
        PointIfc barycenter = new Point(coord);

        for (int i = 0; i < shapeCollectionPoints.getSize(); i++) {

            barycenter.getCoords()[0] += shapeCollectionPoints.getPointFromId(i).getCoords().getCoords()[0];
            barycenter.getCoords()[1] += shapeCollectionPoints.getPointFromId(i).getCoords().getCoords()[1];
            barycenter.getCoords()[2] += shapeCollectionPoints.getPointFromId(i).getCoords().getCoords()[2];
        }
        for (int i = 0; i < 3; i++) {
            barycenter.getCoords()[i] /= shapeCollectionPoints.getSize();
        }
        return barycenter;

    }


    public static PointWithPropertiesIfc returnPointWithLowerPriorityWhenThereIsAMatchingProperty(PointWithPropertiesIfc point1, PointWithPropertiesIfc point2) {

        List<StrikingProperties> commonStrikingProperties = StrikingPropertiesTools.findCommonStrikingProperties(point1, point2);

        if (commonStrikingProperties.size() == 0) {
            System.out.println("none in common !! " + commonStrikingProperties.size());
            //System.out.println();
        }

        if (point1.getStrikingProperties().size() > 2 || point2.getStrikingProperties().size() > 2) {
            System.out.println("More than two " + commonStrikingProperties.size());
            //System.out.println();
        }

        if (point1.getStrikingProperties().size() < point2.getStrikingProperties().size()) {
            return point1;
        } else {
            return point2;


        }
    }
}
