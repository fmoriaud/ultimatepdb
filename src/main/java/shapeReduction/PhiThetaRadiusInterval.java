package shapeReduction;

public class PhiThetaRadiusInterval implements Comparable<PhiThetaRadiusInterval>{


	private double phiMin;
	private double phiMax;
	private double thetaMin;
	private double thetaMax;
	private double rMin;
	private double rMax;
	

	public PhiThetaRadiusInterval (double phiMin, double phiMax, double thetaMin, double thetaMax, double rMin, double rMax){
		this.phiMin = phiMin;
		this.phiMax = phiMax;
		this.thetaMin = thetaMin;
		this.thetaMax = thetaMax;
		this.rMin = rMin;
		this.rMax = rMax;
	}

	
	
	@Override
	public int compareTo(PhiThetaRadiusInterval o) {
		if (this.phiMin < o.phiMin){
			return 1;
		}
		if (this.phiMin > o.phiMin){
			return -1;
		}
		if (this.thetaMin < o.thetaMin){
			return 1;
		}
		if (this.thetaMin > o.thetaMin){
			return -1;
		}
		if (this.rMin < o.rMin){
			return 1;
		}
		if (this.rMin > o.rMin){
			return -1;
		}
		return 0;

	}


	@Override
	public String toString(){
		StringBuilder result = new StringBuilder();
		String NEW_LINE = System.getProperty("line.separator");

		result.append("phi Min and Max = " + phiMin * 180 / Math.PI + "  " + phiMax * 180 / Math.PI + 
				"theta Min and Max = " + thetaMin * 180 / Math.PI + "  " + thetaMax * 180 / Math.PI +
				"radius Min and Max = " + rMin + " " + rMax + NEW_LINE);

		return result.toString();
	}

	public double getPhiMin() {
		return phiMin;
	}
	public void setPhiMin(double phiMin) {
		this.phiMin = phiMin;
	}
	public double getPhiMax() {
		return phiMax;
	}
	public void setPhiMax(double phiMax) {
		this.phiMax = phiMax;
	}
	public double getThetaMin() {
		return thetaMin;
	}
	public void setThetaMin(double thetaMin) {
		this.thetaMin = thetaMin;
	}
	public double getThetaMax() {
		return thetaMax;
	}
	public void setThetaMax(double thetaMax) {
		this.thetaMax = thetaMax;
	}
	public double getrMin() {
		return rMin;
	}
	public void setrMin(double rMin) {
		this.rMin = rMin;
	}
	public double getrMax() {
		return rMax;
	}
	public void setrMax(double rMax) {
		this.rMax = rMax;
	}
}
