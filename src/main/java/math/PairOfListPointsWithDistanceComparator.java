package math;

import java.util.Comparator;

import shapeReduction.PairOfListPointsWithDistance;

public class PairOfListPointsWithDistanceComparator implements Comparator<PairOfListPointsWithDistance>{

	@Override
	public int compare(PairOfListPointsWithDistance pair1, PairOfListPointsWithDistance pair2) {

		if (pair1.getDistance() < pair2.getDistance()){
			return -1;
		}
		if (pair1.getDistance() > pair2.getDistance()){
			return 1;
		}
		return 0;
	}
}