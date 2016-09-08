package pointWithProperties;

import java.util.Map;

import parameters.AlgoParameters;
import mystructure.MyStructureIfc;

public class Box {
	//------------------------
	// Class variables
	//------------------------
	private Map<String, float[]> boundariesOfBox;
	private float deltaX;
	private float deltaY;
	private float deltaZ;

	private float minX;
	private float minY;
	private float minZ;

	private int countOfPointsInXDirection;
	
	private int countOfPointsInYDirection;
	private int countOfPointsInZDirection;

	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public Box(MyStructureIfc myStructure, AlgoParameters algoParameters){

		boundariesOfBox = BoxTools.computeBoundariesOfBox(myStructure, algoParameters);
		deltaX = boundariesOfBox.get("x")[1] - boundariesOfBox.get("x")[0];
		deltaY = boundariesOfBox.get("y")[1] - boundariesOfBox.get("y")[0];
		deltaZ = boundariesOfBox.get("z")[1] - boundariesOfBox.get("z")[0];

		minX = boundariesOfBox.get("x")[0];
		minY = boundariesOfBox.get("y")[0];
		minZ = boundariesOfBox.get("z")[0];
		
		countOfPointsInXDirection = (int) Math.round( deltaX / algoParameters.getCELL_DIMENSION_OF_THE_PROBABILITY_MAP_ANGSTROM());
		countOfPointsInYDirection = (int) Math.round( deltaY / algoParameters.getCELL_DIMENSION_OF_THE_PROBABILITY_MAP_ANGSTROM());
		countOfPointsInZDirection = (int) Math.round( deltaZ / algoParameters.getCELL_DIMENSION_OF_THE_PROBABILITY_MAP_ANGSTROM());
	}




	//------------------------
	// Getter and Setter
	//------------------------
	public Map<String, float[]> getBoundariesOfBox() {
		return boundariesOfBox;
	}
	public float getDeltaX() {
		return deltaX;
	}
	public float getDeltaY() {
		return deltaY;
	}
	public float getDeltaZ() {
		return deltaZ;
	}
	public float getMinX() {
		return minX;
	}
	public float getMinY() {
		return minY;
	}
	public float getMinZ() {
		return minZ;
	}
	public int getCountOfPointsInXDirection() {
		return countOfPointsInXDirection;
	}
	public int getCountOfPointsInYDirection() {
		return countOfPointsInYDirection;
	}
	public int getCountOfPointsInZDirection() {
		return countOfPointsInZDirection;
	}
}
