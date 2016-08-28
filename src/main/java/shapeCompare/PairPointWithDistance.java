package shapeCompare;

import java.util.Comparator;

import shapeReduction.PairInteger;

public class PairPointWithDistance {
	// -------------------------------------------------------------------
	// Class variables
	// -------------------------------------------------------------------
	private PairInteger pairInteger;
	private double distance;




	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public PairPointWithDistance(int point1, int point2, double distance){

		this.pairInteger = new PairInteger(point1, point2);
		this.distance = distance;
	}




	// -------------------------------------------------------------------
	// Public && Interface method
	// -------------------------------------------------------------------
	@Override public String toString() {

		StringBuilder result = new StringBuilder();
		String NEW_LINE = System.getProperty("line.separator");

		result.append("pointsIds : ");
		result.append(pairInteger.toString());
		result.append("  distance : ");
		result.append(distance);
		
		result.append(NEW_LINE);

		return result.toString();
	}

	
	
	public static class LowestDistancePairPointWithDistance implements Comparator<PairPointWithDistance>{

		@Override
		public int compare(PairPointWithDistance pair1, PairPointWithDistance pair2) {

			if (pair1.getDistance() > pair2.getDistance()){
				return 1;
			}
			if (pair1.getDistance() < pair2.getDistance()){
				return -1;
			}
			return 0;
		}
	}

	// -------------------------------------------------------------------
	// Getters & Setters
	// -------------------------------------------------------------------
	public PairInteger getPairInteger() {
		return pairInteger;
	}

	public double getDistance() {
		return distance;
	}
}
