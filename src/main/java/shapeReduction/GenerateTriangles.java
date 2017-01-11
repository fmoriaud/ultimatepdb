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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import math.MathTools;
import parameters.AlgoParameters;
import pointWithProperties.PointWithPropertiesIfc;
import pointWithProperties.StrikingProperties;

public class GenerateTriangles {
    // -------------------------------------------------------------------
    // Class variables
    // -------------------------------------------------------------------
    private Map<Integer, PointWithPropertiesIfc> miniShape;
    private AlgoParameters algoParameters;

    private boolean debug = false;

    private float absoluteEdgeMin = 4.0f;
    private float absoluteEdgeMax = 10.0f;


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public GenerateTriangles(Map<Integer, PointWithPropertiesIfc> miniShape, AlgoParameters algoParameters) {
        this.miniShape = miniShape;
        this.algoParameters = algoParameters;
    }


    // -------------------------------------------------------------------
    // Public Interface
    // -------------------------------------------------------------------
    public List<TriangleInteger> generateTriangles() {

        List<Integer> listOfIDinMap = new ArrayList<>();
        listOfIDinMap.addAll(miniShape.keySet());
        Collections.sort(listOfIDinMap);

        List<TriangleEdge> listEdges = generateListOfEdge(listOfIDinMap);

        List<TriangleInteger> listTriangle = generateListOfTriangles(listOfIDinMap, listEdges);

        if (debug == true) {
            System.out.println("built trianglewith not OK angles " + listTriangle.size());
        }

        removeTriangleAccordingToAngles(listTriangle);

        if (debug == true) {
            System.out.println("built triangle with too many NONE " + listTriangle.size());
        }

        removeTriangleAccordingToNONEoccurrenceCount(listTriangle);

        if (debug == true) {
            System.out.println("built triangle without too many NONE " + listTriangle.size());
        }

        return listTriangle;
    }


    // -------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------
    private void removeTriangleAccordingToNONEoccurrenceCount(List<TriangleInteger> listTriangle) {

        Iterator<TriangleInteger> itr = listTriangle.iterator();
        while (itr.hasNext()) {
            TriangleInteger triangle = itr.next();

            int[] arrayTriangleIdAndPointId = triangle.getArrayTriangleIdAndPointId();

            int countOfNONE = 0;
            for (int i = 0; i < 3; i++) {
                //for (Entry<Integer, Integer> entry: mapTriangleIdAndPointId.entrySet()){
                Integer pointId = arrayTriangleIdAndPointId[i];
                PointWithPropertiesIfc pointWithProperties = miniShape.get(pointId);
                List<StrikingProperties> properties = pointWithProperties.getStrikingProperties();

                if ((properties.size() == 1) && properties.get(0).equals(StrikingProperties.NONE)) {
                    countOfNONE += 1;
                }
            }

            if (countOfNONE > algoParameters.getMAX_COUNT_NONE_PROPERTY_IN_TRIANGLE()) {
                itr.remove();
            }
        }
    }


    private void removeTriangleAccordingToAngles(List<TriangleInteger> listTriangle) {
        Iterator<TriangleInteger> itr = listTriangle.iterator();
        while (itr.hasNext()) {
            TriangleInteger triangle = itr.next();
            if (!isTriangleOKconcerningAngles(triangle)) {
                itr.remove();
            }
        }
    }


    private List<TriangleInteger> generateListOfTriangles(List<Integer> listOfIDinMap, List<TriangleEdge> listEdges) {

        List<TriangleInteger> listTriangle = new ArrayList<>();

        // loop on each edge
        for (TriangleEdge edge : listEdges) {
            int vertice1 = edge.getPoint1();
            int vertice2 = edge.getPoint2();
            //PointWithProperties pointVertice1 = miniShape.get(vertice1);
            //PointWithProperties pointVertice2 = miniShape.get(vertice2);
            // TODO done to big easier to code but not optimized
            for (Integer point : listOfIDinMap) {

                if ((point != vertice1) && (point != vertice2)) {

                    double distance1 = MathTools.computeDistance(miniShape.get(vertice1).getCoords().getCoords(), miniShape.get(point).getCoords().getCoords());
                    double distance2 = MathTools.computeDistance(miniShape.get(vertice2).getCoords().getCoords(), miniShape.get(point).getCoords().getCoords());

                    if (distance1 > absoluteEdgeMin && distance1 < absoluteEdgeMax) {
                        if (distance2 > absoluteEdgeMin && distance2 < absoluteEdgeMax) {

                            //if (distance1 > fractionForMin * edgeMaxFound && distance1 < fractionMax * edgeMaxFound){
                            //if (distance2 > fractionForMin * edgeMaxFound && distance2 < fractionMax * edgeMaxFound){

                            int[] listPointsInTriangle = new int[3];
                            listPointsInTriangle[0] = vertice1;
                            listPointsInTriangle[1] = vertice2;
                            listPointsInTriangle[2] = point.intValue();

                            double[] arrayTriangleIdAndOppositeEdgeLength = new double[3];
                            arrayTriangleIdAndOppositeEdgeLength[0] = distance2;
                            arrayTriangleIdAndOppositeEdgeLength[1] = distance1;
                            arrayTriangleIdAndOppositeEdgeLength[2] = edge.getEdgeLength();
                            TriangleInteger triangleInteger = new TriangleInteger(listPointsInTriangle);
                            triangleInteger.setArrayTriangleIdAndOppositeEdgeLength(arrayTriangleIdAndOppositeEdgeLength);

                            listTriangle.add(triangleInteger);
                        }
                    }
                }
            }
        }
        return listTriangle;
    }


    private List<TriangleEdge> generateListOfEdge(List<Integer> listOfIDinMap) {

        if (listOfIDinMap.size() == 6) {
            System.out.println("listOfIDinMap.size() == 6");
        }
        List<TriangleEdge> listEdges = new ArrayList<>();

        for (int i = 0; i < listOfIDinMap.size(); i++) {
            for (int j = 0; j < listOfIDinMap.size(); j++) {
                if (j > i) {
                    Integer pointId1 = listOfIDinMap.get(i);
                    Integer pointId2 = listOfIDinMap.get(j);
                    double distance = MathTools.computeDistance(miniShape.get(pointId1).getCoords().getCoords(), miniShape.get(pointId2).getCoords().getCoords());
                    if (distance > absoluteEdgeMin && distance < absoluteEdgeMax) {

                        TriangleEdge triangleEdge = new TriangleEdge(pointId1.intValue(), pointId2.intValue(), distance);
                        listEdges.add(triangleEdge);
                    }
                }
            }
        }
        return listEdges;
    }


    public boolean isTriangleOKconcerningAngles(TriangleInteger triangle) {

        int[] arrayTriangleIdAndPointId = triangle.getArrayTriangleIdAndPointId();

        PointWithPropertiesIfc point1 = miniShape.get(arrayTriangleIdAndPointId[0]);
        PointWithPropertiesIfc point2 = miniShape.get(arrayTriangleIdAndPointId[1]);
        PointWithPropertiesIfc point3 = miniShape.get(arrayTriangleIdAndPointId[2]);

        float[] v1v2 = MathTools.normalizeVector(MathTools.v1minusV2(point2.getCoords().getCoords(), point1.getCoords().getCoords()));
        float[] v2v1 = MathTools.normalizeVector(MathTools.v1minusV2(point1.getCoords().getCoords(), point2.getCoords().getCoords()));

        float[] v1v3 = MathTools.normalizeVector(MathTools.v1minusV2(point3.getCoords().getCoords(), point1.getCoords().getCoords()));
        float[] v3v1 = MathTools.normalizeVector(MathTools.v1minusV2(point1.getCoords().getCoords(), point3.getCoords().getCoords()));

        float[] v2v3 = MathTools.normalizeVector(MathTools.v1minusV2(point3.getCoords().getCoords(), point2.getCoords().getCoords()));
        float[] v3v2 = MathTools.normalizeVector(MathTools.v1minusV2(point2.getCoords().getCoords(), point3.getCoords().getCoords()));


        double angleRadian1 = MathTools.computeAngle(v1v2, v1v3);
        double angleRadian2 = MathTools.computeAngle(v2v1, v2v3);
        double angleRadian3 = MathTools.computeAngle(v3v1, v3v2);

        if ((Math.abs(angleRadian1) > algoParameters.getANGLE_MIN()) && (Math.abs(angleRadian1) < algoParameters.getANGLE_MAX())) {
            if ((Math.abs(angleRadian2) > algoParameters.getANGLE_MIN()) && (Math.abs(angleRadian2) < algoParameters.getANGLE_MAX())) {
                if ((Math.abs(angleRadian3) > algoParameters.getANGLE_MIN()) && (Math.abs(angleRadian3) < algoParameters.getANGLE_MAX())) {

                    double[] arrayTriangleIdAndCorrespondingAngles = new double[3];
                    //Map<Integer, Double> mapTriangleIdAndCorrespondingAngles = new HashMap<>(3);
                    arrayTriangleIdAndCorrespondingAngles[0] = angleRadian1;
                    arrayTriangleIdAndCorrespondingAngles[1] = angleRadian2;
                    arrayTriangleIdAndCorrespondingAngles[2] = angleRadian3;
                    triangle.setArrayTriangleIdAndCorrespondingAngles(arrayTriangleIdAndCorrespondingAngles);
                    return true;
                }
            }
        }
        return false;
    }
}

