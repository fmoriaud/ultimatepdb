package shapeReduction;

public class PhiThetaIntervalWithRintervalWithCountOfPoints implements Comparable<PhiThetaIntervalWithRintervalWithCountOfPoints>{

	public PhiThetaRadiusInterval phiThetaRadiusInterval;
	int countOfPoints;


	public PhiThetaIntervalWithRintervalWithCountOfPoints(PhiThetaRadiusInterval phiThetaRadiusInterval, int countOfPoints){
		this.phiThetaRadiusInterval = phiThetaRadiusInterval;
		this.countOfPoints = countOfPoints;
	}

	@Override
	public String toString(){
		StringBuilder result = new StringBuilder();
		result.append("count of point = " + countOfPoints + "  " + phiThetaRadiusInterval.toString() );
		return result.toString();
	}


	@Override
	public int compareTo(PhiThetaIntervalWithRintervalWithCountOfPoints o) {
		if (this.countOfPoints > o.countOfPoints){
			return -1;
		}
		if (this.countOfPoints < o.countOfPoints){
			return 1;
		}

		if (phiThetaRadiusInterval.compareTo(o.phiThetaRadiusInterval) == -1 ){
			return -1;
		}
		if (phiThetaRadiusInterval.compareTo(o.phiThetaRadiusInterval) == 1 ){
			return 1;
		}
		return 0;
	}


}
