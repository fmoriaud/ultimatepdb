package shapeReduction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.threed.SphericalCoordinates;

public class Sectors implements SectorsIfc{

	private List<PhiThetaRadiusInterval> sectors;



	public Sectors(){
		this.sectors = new ArrayList<>();
	}


	@Override
	public void addSector(PhiThetaRadiusInterval phiThetaRadiusInterval) {
		this.sectors.add(phiThetaRadiusInterval);
	}



	@Override
	public PhiThetaRadiusInterval getIntervalFromSphericalCoordinates(SphericalCoordinates pointShericalRelative) {

		double phi = pointShericalRelative.getPhi();
		double theta = pointShericalRelative.getTheta();
		double radius = pointShericalRelative.getR();

		for (PhiThetaRadiusInterval phiThetaRadiusInterval: this.sectors){
			double phiMin = phiThetaRadiusInterval.getPhiMin();
			double phiMax = phiThetaRadiusInterval.getPhiMax();
			double thetaMin = phiThetaRadiusInterval.getThetaMin();
			double thetaMax = phiThetaRadiusInterval.getThetaMax();
			double rMin = phiThetaRadiusInterval.getrMin();
			double rMax = phiThetaRadiusInterval.getrMax();

			if ( phi < phiMin || phi > phiMax){
				continue;
			}
			if ( theta < thetaMin || theta > thetaMax){
				continue;
			}
			if ( radius < rMin || radius > rMax){
				continue;
			}
			return phiThetaRadiusInterval;

		}
		// sector was not found
		return null;
	}


	@Override
	public Iterator<PhiThetaRadiusInterval> iterator() {
		return this.sectors.iterator();
	}
}
