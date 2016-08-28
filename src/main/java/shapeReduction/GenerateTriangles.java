package shapeReduction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import math.ToolsMath;
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
	//private static Double edgeMaxFound = null; // trick so it is set only the first time used
	private double fractionMax = 0.7;
	private double fractionForMin = 0.3;

	private float absoluteEdgeMin = 4.0f;
	private float absoluteEdgeMax = 10.0f;


	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public GenerateTriangles(Map<Integer, PointWithPropertiesIfc> miniShape, AlgoParameters algoParameters){
		this.miniShape = miniShape;
		this.algoParameters = algoParameters;
	}



	// -------------------------------------------------------------------
	// Public Interface
	// -------------------------------------------------------------------
	public List<TriangleInteger> generateTriangles(){

		List<Integer> listOfIDinMap = new ArrayList<>();
		listOfIDinMap.addAll(miniShape.keySet());
		Collections.sort(listOfIDinMap);

		List<TriangleEdge> listEdges = generateListOfEdge(listOfIDinMap);

		List<TriangleInteger> listTriangle = generateListOfTriangles(listOfIDinMap, listEdges);

		if (debug == true){
			System.out.println("built trianglewith not OK angles " + listTriangle.size());
		}

		removeTriangleAccordingToAngles(listTriangle);

		if (debug == true){
			System.out.println("built triangle with too many NONE " + listTriangle.size());
		}

		removeTriangleAccordingToNONEoccurrenceCount(listTriangle);

		if (debug == true){
			System.out.println("built triangle without too many NONE " + listTriangle.size());
		}

		return listTriangle;
	}



	private void removeTriangleAccordingToNONEoccurrenceCount(List<TriangleInteger> listTriangle){

		Iterator<TriangleInteger> itr = listTriangle.iterator();
		while(itr.hasNext()) {
			TriangleInteger triangle = itr.next();

			int[] arrayTriangleIdAndPointId = triangle.getArrayTriangleIdAndPointId();

			int countOfNONE = 0;
			for (int i=0; i<3; i++){
				//for (Entry<Integer, Integer> entry: mapTriangleIdAndPointId.entrySet()){
				Integer pointId = arrayTriangleIdAndPointId[i];
				PointWithPropertiesIfc pointWithProperties = miniShape.get(pointId);
				List<StrikingProperties> properties = pointWithProperties.getStrikingProperties();

				if ((properties.size() == 1) && properties.get(0).equals(StrikingProperties.NONE)){
					countOfNONE += 1;
				}
			}

			if (countOfNONE > algoParameters.getMAX_COUNT_NONE_PROPERTY_IN_TRIANGLE()){
				itr.remove();
			}
		}
	}



	private void removeTriangleAccordingToAngles(List<TriangleInteger> listTriangle) {
		Iterator<TriangleInteger> itr = listTriangle.iterator();
		while(itr.hasNext()) {
			TriangleInteger triangle = itr.next();
			if (! isTriangleOKconcerningAngles(triangle)){
				itr.remove();
			}
		}
	}



	private List<TriangleInteger> generateListOfTriangles(List<Integer> listOfIDinMap, List<TriangleEdge> listEdges) {

		List<TriangleInteger> listTriangle = new ArrayList<>();

		// loop on each edge
		for (TriangleEdge edge: listEdges){
			int vertice1 = edge.point1;
			int vertice2 = edge.point2;
			//PointWithProperties pointVertice1 = miniShape.get(vertice1);
			//PointWithProperties pointVertice2 = miniShape.get(vertice2);
			// TODO done to big easier to code but not optimized
			for (Integer point: listOfIDinMap){

				if ((point != vertice1) && (point != vertice2)){

					double distance1 = ToolsMath.computeDistance(miniShape.get(vertice1).getCoords().getCoords(), miniShape.get(point).getCoords().getCoords());
					double distance2 = ToolsMath.computeDistance(miniShape.get(vertice2).getCoords().getCoords(), miniShape.get(point).getCoords().getCoords());

					if (distance1 > absoluteEdgeMin && distance1 < absoluteEdgeMax){
						if (distance2 > absoluteEdgeMin && distance2 < absoluteEdgeMax){

							//if (distance1 > fractionForMin * edgeMaxFound && distance1 < fractionMax * edgeMaxFound){
							//if (distance2 > fractionForMin * edgeMaxFound && distance2 < fractionMax * edgeMaxFound){

							int[] listPointsInTriangle = new int[3];
							listPointsInTriangle[0] = vertice1;
							listPointsInTriangle[1] = vertice2;
							listPointsInTriangle[2] = point.intValue();

							double[] arrayTriangleIdAndOppositeEdgeLength = new double[3];
							arrayTriangleIdAndOppositeEdgeLength[0] = distance2;
							arrayTriangleIdAndOppositeEdgeLength[1] = distance1;
							arrayTriangleIdAndOppositeEdgeLength[2] = edge.edgeLength;
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

		if (listOfIDinMap.size() == 6){
			System.out.println();
		}
		List<TriangleEdge> listEdges = new ArrayList<>();


		//		if (edgeMaxFound == null){
		//			edgeMaxFound = Double.MIN_VALUE;
		//
		//			for (int i=0; i<listOfIDinMap.size(); i++){
		//				for (int j=0; j<listOfIDinMap.size(); j++){
		//					if (j>i){
		//						Integer pointId1 = listOfIDinMap.get(i);
		//						Integer pointId2 = listOfIDinMap.get(j);
		//						double distance = ToolsMath.computeDistance(miniShape.get(pointId1).getCoords().getCoords(), miniShape.get(pointId2).getCoords().getCoords());
		//
		//						if (distance > edgeMaxFound){
		//							edgeMaxFound = distance;
		//						}
		//					}
		//				}
		//			}
		//		}

		//System.out.println("edgeMaxFound = " + edgeMaxFound);
		for (int i=0; i<listOfIDinMap.size(); i++){
			for (int j=0; j<listOfIDinMap.size(); j++){
				if (j>i){
					Integer pointId1 = listOfIDinMap.get(i);
					Integer pointId2 = listOfIDinMap.get(j);
					double distance = ToolsMath.computeDistance(miniShape.get(pointId1).getCoords().getCoords(), miniShape.get(pointId2).getCoords().getCoords());
					if (distance > absoluteEdgeMin && distance < absoluteEdgeMax){

						TriangleEdge triangleEdge = new TriangleEdge(pointId1.intValue(), pointId2.intValue(), distance);
						listEdges.add(triangleEdge);
					}
				}
			}
		}
		return listEdges;
	}



	public boolean isTriangleOKconcerningAngles(TriangleInteger triangle){

		int[] arrayTriangleIdAndPointId = triangle.getArrayTriangleIdAndPointId();

		PointWithPropertiesIfc point1 = miniShape.get(arrayTriangleIdAndPointId[0]);
		PointWithPropertiesIfc point2 = miniShape.get(arrayTriangleIdAndPointId[1]);
		PointWithPropertiesIfc point3 = miniShape.get(arrayTriangleIdAndPointId[2]);

		float[] v1v2 = ToolsMath.normalizeVector(ToolsMath.v1minusV2(point2.getCoords().getCoords(), point1.getCoords().getCoords()));
		float[] v2v1 = ToolsMath.normalizeVector(ToolsMath.v1minusV2(point1.getCoords().getCoords(), point2.getCoords().getCoords()));

		float[] v1v3 = ToolsMath.normalizeVector(ToolsMath.v1minusV2(point3.getCoords().getCoords(), point1.getCoords().getCoords()));
		float[] v3v1 = ToolsMath.normalizeVector(ToolsMath.v1minusV2(point1.getCoords().getCoords(), point3.getCoords().getCoords()));

		float[] v2v3 = ToolsMath.normalizeVector(ToolsMath.v1minusV2(point3.getCoords().getCoords(), point2.getCoords().getCoords()));
		float[] v3v2 = ToolsMath.normalizeVector(ToolsMath.v1minusV2(point2.getCoords().getCoords(), point3.getCoords().getCoords()));


		double angleRadian1 = ToolsMath.computeAngle(v1v2, v1v3);
		double angleRadian2 = ToolsMath.computeAngle(v2v1, v2v3);
		double angleRadian3 = ToolsMath.computeAngle(v3v1, v3v2);

		//		System.out.println("angle degres1 = " + angleRadian1 * 180 / Math.PI);
		//		System.out.println("angle degres2 = " + angleRadian2 * 180 / Math.PI);
		//		System.out.println("angle degres3 = " + angleRadian3 * 180 / Math.PI);

		double sum = (angleRadian1 + angleRadian2 + angleRadian3) ;
		if ((sum > Math.PI + 0.001) && (sum < Math.PI - 0.001)){
			System.out.println("sum should be 180 " + (angleRadian1 + angleRadian2 + angleRadian3) * 180 / Math.PI );
			System.exit(0);
		}

		if ((Math.abs(angleRadian1) > algoParameters.getANGLE_MIN()) && (Math.abs(angleRadian1) < algoParameters.getANGLE_MAX())){
			if ((Math.abs(angleRadian2) > algoParameters.getANGLE_MIN()) && (Math.abs(angleRadian2) < algoParameters.getANGLE_MAX())){
				if ((Math.abs(angleRadian3) > algoParameters.getANGLE_MIN()) && (Math.abs(angleRadian3) < algoParameters.getANGLE_MAX())){

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

