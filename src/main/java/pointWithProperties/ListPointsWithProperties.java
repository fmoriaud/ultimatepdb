package pointWithProperties;

import java.util.ArrayList;

public class ListPointsWithProperties extends ArrayList<PointWithProperties>{

	@Override public String toString() {
		StringBuilder result = new StringBuilder();
		String NEW_LINE = System.getProperty("line.separator");

		result.append(this.getClass().getName() + " Object {" + NEW_LINE);
		result.append(" Size: " + this.size() + NEW_LINE);
		result.append(" AverageY: " + computeAverageY() + NEW_LINE);
		result.append("}");

		return result.toString();
	}
	
	
	private double computeAverageY(){
		double averageY = 0.0;
		for (PointWithProperties pointWithProperties: this){
			averageY += pointWithProperties.getCoords().getCoords()[1];
		}
		return averageY / this.size();
	}
	
}
