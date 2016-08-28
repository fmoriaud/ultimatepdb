package shapeReduction;

public class PhiThetaInterval implements Comparable<PhiThetaInterval>{

	
	private double phiMin;
	private double phiMax;
	private double thetaMin;
	private double thetaMax;
	
	public PhiThetaInterval (double phiMin, double phiMax, double thetaMin, double thetaMax){
		this.phiMin = phiMin;
		this.phiMax = phiMax;
		this.thetaMin = thetaMin;
		this.thetaMax = thetaMax;
	}
	
	@Override
	public int compareTo(PhiThetaInterval o) {
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
		return 0;
		
	}

	
	@Override
	public String toString(){
		StringBuilder result = new StringBuilder();

		result.append(phiMin * 180 / Math.PI + "  " + phiMax * 180 / Math.PI + "  " + thetaMin * 180 / Math.PI + "  " + thetaMax * 180 / Math.PI);

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

	
	
	
	
}
