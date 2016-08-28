package math;

import org.apache.commons.math3.linear.RealMatrix;

public interface ProcrustesAnalysisIfc {

	void run(RealMatrix matrixPointsModel, RealMatrix matrixPointsCandidate);
	double getResidual();
	RealMatrix getRotationMatrix();
	public void initialize();
}
