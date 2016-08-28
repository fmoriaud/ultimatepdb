package shapeReduction;

import java.util.List;

import pointWithProperties.PointWithPropertiesIfc;


public class PairOfListPointsWithDistance{

	//------------------------
	// Class variables
	//------------------------
	private List<PointWithPropertiesIfc> listPoint1;
	private List<PointWithPropertiesIfc> listPoint2;
	private float distance;


	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public PairOfListPointsWithDistance(List<PointWithPropertiesIfc> listPoint1, List<PointWithPropertiesIfc> listPoint2, float distance){

		this.listPoint1 = listPoint1;
		this.listPoint2 = listPoint2;
		this.distance = distance;
	}


	
	
	// -------------------------------------------------------------------
	// Getter and Setter
	// -------------------------------------------------------------------
	public List<PointWithPropertiesIfc> getListPoint1() {
		return listPoint1;
	}


	public List<PointWithPropertiesIfc> getListPoint2() {
		return listPoint2;
	}


	public float getDistance() {
		return distance;
	}
}
