package shapeReduction;


public class PairInteger {

	public int point1;
	public int point2;

	public PairInteger(int point1, int point2){
		this.point1 = point1;
		this.point2 = point2;
	}



	@Override public String toString() {

		StringBuilder result = new StringBuilder();
		String NEW_LINE = System.getProperty("line.separator");

		result.append("pointsIds : ");
		result.append(point1 + " " + point2);
		result.append(NEW_LINE);

		return result.toString();
	}
}
