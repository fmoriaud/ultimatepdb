package scorePairing;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class ResultFromScorePairing {

	//------------------------
	// Class variables
	//------------------------
	private double cost;

	private RealMatrix rotationMatrixToRotateShape2ToShape1;
	private RealVector translationVectorToTranslateShape2ToShape1;
	private RealVector translationVectorToTranslateShape2ToOrigin;
	private double distanceResidual;


	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public ResultFromScorePairing(double cost, RealMatrix rotationMatrixToRotateShape2ToShape1, RealVector translationVectorToTranslateShape2ToShape1, RealVector translationVectorToTranslateShape2ToOrigin, double distanceResidual){
		
		this.cost = cost;
		this.rotationMatrixToRotateShape2ToShape1 = rotationMatrixToRotateShape2ToShape1;
		this.translationVectorToTranslateShape2ToShape1 = translationVectorToTranslateShape2ToShape1;
		this.translationVectorToTranslateShape2ToOrigin = translationVectorToTranslateShape2ToOrigin;
		this.distanceResidual = distanceResidual;
		
	}


	//------------------------
	// Getter and Setter
	//------------------------
	public double getCost() {
		return cost;
	}

	public RealMatrix getRotationMatrixToRotateShape2ToShape1() {
		return rotationMatrixToRotateShape2ToShape1;
	}

	public RealVector getTranslationVectorToTranslateShape2ToShape1() {
		return translationVectorToTranslateShape2ToShape1;
	}

	public RealVector getTranslationVectorToTranslateShape2ToOrigin() {
		return translationVectorToTranslateShape2ToOrigin;
	}

	public double getDistanceResidual() {
		return distanceResidual;
	}
}
