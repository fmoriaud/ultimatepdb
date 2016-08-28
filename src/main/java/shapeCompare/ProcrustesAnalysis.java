package shapeCompare;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import math.ProcrustesAnalysisIfc;
import parameters.AlgoParameters;


public class ProcrustesAnalysis implements ProcrustesAnalysisIfc{
	// -------------------------------------------------------------------
	// Class variables
	// -------------------------------------------------------------------
	public double residual;
	public RealMatrix rotationMatrix;

	private double[][] matrixInBetweenDouble;
	private RealMatrix matrixInBetween;

	private AlgoParameters algoParameters;




	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public ProcrustesAnalysis(AlgoParameters algoParameters){
		this.algoParameters = algoParameters;
		initialize();
	}



	public ProcrustesAnalysis(){
		initialize();
	}


	@Override
	public void initialize() {
		matrixInBetweenDouble = new double[3][3];

		for (int i = 0; i<3; i++){
			for (int j = 0; j<3; j++){
				matrixInBetweenDouble[i][j] = 0.0;
			}
		}
		matrixInBetweenDouble[0][0] = 1.0;
		matrixInBetweenDouble[1][1] = 1.0;

		this.matrixInBetween = new Array2DRowRealMatrix(matrixInBetweenDouble);

	}



	public void run(RealMatrix matrixPointsModel, RealMatrix matrixPointsCandidate){

		RealMatrix transposedMatrixPointsCandidate = matrixPointsCandidate.transpose();
		RealMatrix matrixToApplyToSingularValueDecomposition = matrixPointsModel.multiply(transposedMatrixPointsCandidate);

		SingularValueDecomposition singularValueDecomposition = new SingularValueDecomposition(matrixToApplyToSingularValueDecomposition);

		RealMatrix uMatrix = singularValueDecomposition.getU();
		RealMatrix vTMatrix = singularValueDecomposition.getVT();
		RealMatrix uVt = uMatrix.multiply(vTMatrix);

		LUDecomposition lUDecomposition = new LUDecomposition(uVt);

		double detUvT = lUDecomposition.getDeterminant();
		matrixInBetween.setEntry(2, 2, detUvT);

		RealMatrix matrixInBetweenXvTMatrix = matrixInBetween.multiply(vTMatrix);
		RealMatrix rotationMatrix = uMatrix.multiply(matrixInBetweenXvTMatrix);
		this.rotationMatrix = rotationMatrix;

		RealMatrix rotatedmatrixPointsCandidate = rotationMatrix.multiply(matrixPointsCandidate);
		this.residual  = computeResidual(matrixPointsModel, rotatedmatrixPointsCandidate);	
	}




	// -------------------------------------------------------------------
	// Private & Implementation Methods
	// -------------------------------------------------------------------
	private float computeResidual(RealMatrix matrixPointsModel, RealMatrix rotatedmatrixPointsCandidate){

		float distance ;

		double sumSquareDeltaDistances = 0.0;

		for (int i=0; i< matrixPointsModel.getColumnDimension(); i++){
			RealVector vectorFromModel = matrixPointsModel.getColumnVector(i);
			RealVector vectorFromCandidate = rotatedmatrixPointsCandidate.getColumnVector(i);

			distance = (float) vectorFromModel.getDistance(vectorFromCandidate);
			if (algoParameters != null){
				float correctedDistance = computeCorrectedDistance(distance);
				sumSquareDeltaDistances += (correctedDistance * correctedDistance);
				continue;
			}
			sumSquareDeltaDistances += distance * distance;
		}

		float rmsd = (float) Math.sqrt(sumSquareDeltaDistances / matrixPointsModel.getColumnDimension());

		return rmsd;
	}



	private float computeCorrectedDistance(float distance){

		if (distance < 0.9 * algoParameters.getCELL_DIMENSION_OF_THE_PROBABILITY_MAP_ANGSTROM()){ // ( sqrt 3 )/ 2
			return 0.0f;
		}
		return distance;
	}


	// -------------------------------------------------------------------
	// Getters & Setters
	// -------------------------------------------------------------------



	public double getResidual() {
		return residual;
	}



	public RealMatrix getRotationMatrix() {
		return rotationMatrix.copy();
	}
}
