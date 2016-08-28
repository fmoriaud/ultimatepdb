package math;

import java.util.List;

import pointWithProperties.PointIfc;

public class ToolsDistance {


	public static float computeSmallestDistanceBetweenAPointAndListOfPoints(float[] atPosition, List<PointIfc> listOfPointsWithLennardJonesQuery){

		float minDistance = Float.MAX_VALUE;

		for(PointIfc pointsWithLennardJones: listOfPointsWithLennardJonesQuery) {

			float[] atomPosition = pointsWithLennardJones.getCoords();
			float distance = ToolsMath.computeDistance(atomPosition, atPosition);
			if (distance < minDistance){
				minDistance = distance;
			}
		}
		return minDistance;
	}


}
