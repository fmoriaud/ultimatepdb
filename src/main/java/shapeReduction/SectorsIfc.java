package shapeReduction;

import java.util.Iterator;

import org.apache.commons.math3.geometry.euclidean.threed.SphericalCoordinates;

public interface SectorsIfc {

	void addSector(PhiThetaRadiusInterval phiThetaRadiusInterval);
	PhiThetaRadiusInterval getIntervalFromSphericalCoordinates(SphericalCoordinates pointShericalRelative);
	Iterator<PhiThetaRadiusInterval> iterator();
}
