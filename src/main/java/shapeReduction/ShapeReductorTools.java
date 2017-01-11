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

import math.MathTools;
import pointWithProperties.*;

import java.util.*;
import java.util.Map.Entry;

public class ShapeReductorTools {
    //-------------------------------------------------------------
    // Static methods
    //-------------------------------------------------------------
    public static List<Double> doBinningThetaValues(double delta, int countOfIncrementAngle) {

        List<Double> thetaValues = new ArrayList<>();

        for (int j = 0; j < 2 * countOfIncrementAngle; j++) {
            double theta = -Math.PI + (double) j * delta;
            thetaValues.add(theta);
        }
        return thetaValues;
    }


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

        if (listPointsFreeOfMiniShapePoints.size() == 0) {
            System.out.println("could not find a point not already in minishape so one minishape point is lost");
            return null;
        }
        PointIfc barycenter = computeBarycenterOfAListOfPoint(listPointsFreeOfMiniShapePoints);
        Collections.sort(listPointsFreeOfMiniShapePoints, new PointDistanceToBarycenterComparator(barycenter));

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

        if (listPointsFreeOfMiniShapePoints.size() == 0) {
            System.out.println("could not find a point not already in minishape so one minishape point is lost");
            return null;
        }
        PointIfc barycenter = computeBarycenterOfAListOfPoint(listPointsFreeOfMiniShapePoints);
        Collections.sort(listPointsFreeOfMiniShapePoints, new PointDistanceToBarycenterComparator(barycenter));

        return listPointsFreeOfMiniShapePoints.get(0);
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


    //-------------------------------------------------------------
    // Inner Class
    //-------------------------------------------------------------
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
}
