package pointWithProperties;

public class PointsWithLennardJones implements PointIfc{

	//------------------------
	// Class variables
	//------------------------
	private float[] coords;
	private float lennardJones;




	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public PointsWithLennardJones(){

	}



	public PointsWithLennardJones(float[] coords, float lennardJones){
		this.coords = coords;
		this.lennardJones = lennardJones;
	}



	// -------------------------------------------------------------------
	// Getters & Setters
	// -------------------------------------------------------------------
	public float[] getCoords() {
		return coords;
	}



	public void setCoords(float[] coords) {
		this.coords = coords;
	}



	public double getLennardJones() {
		return lennardJones;
	}


	public void setLennardJones(float lennardJones) {
		this.lennardJones = lennardJones;
	}
}
