package pointWithProperties;

public class Point implements PointIfc{

	float[] coords;
	
	
	public Point(float[] coords){
		this.coords = coords;
	}
	
	
	
	public Point(float x, float y, float z){
		this.coords = new float[]{x, y, z};
	}
	
	
	@Override
	public float[] getCoords() {
		
		return coords;
	}

	@Override
	public void setCoords(float[] coords) {
		this.coords = coords;
	}
	
	@Override public String toString() {
		StringBuilder result = new StringBuilder();
		String NEW_LINE = System.getProperty("line.separator");

		result.append(this.getClass().getName() + " Object {" + NEW_LINE);
		result.append(coords[0] + " " + coords[1] + " " + coords[2] + NEW_LINE);
		result.append("}");

		return result.toString();
	}
	

}
