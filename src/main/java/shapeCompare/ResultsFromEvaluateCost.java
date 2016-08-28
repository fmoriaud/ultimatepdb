package shapeCompare;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import parameters.AlgoParameters;

public class ResultsFromEvaluateCost {
	//------------------------
	// Class variables
	//------------------------
	private double cost;
	private double distanceResidual;
	private RealMatrix rotationMatrix;
	private RealVector translationVector;
	private RealVector translationVectorToTranslateShape2ToOrigin;
	private PairingAndNullSpaces pairingAndNullSpaces;
	private float coverage;
	private AlgoParameters algoParameters;



	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public ResultsFromEvaluateCost(double cost, double distanceResidual, RealMatrix rotationMatrix, RealVector translationVector, RealVector translationVectorToTranslateShape2ToOrigin, 
			PairingAndNullSpaces pairingAndNullSpaces, float coverage, AlgoParameters algoParameters){
		this.cost = cost;
		this.distanceResidual = distanceResidual;
		this.rotationMatrix = rotationMatrix;
		this.translationVector = translationVector;
		this.translationVectorToTranslateShape2ToOrigin = translationVectorToTranslateShape2ToOrigin;
		this.pairingAndNullSpaces = pairingAndNullSpaces;
		this.coverage = coverage;
		this.algoParameters = algoParameters;
	}




	// -------------------------------------------------------------------
	// Override methods
	// -------------------------------------------------------------------
	@Override
	public int hashCode() {

		float unit = 3* algoParameters.getCELL_DIMENSION_OF_THE_PROBABILITY_MAP_ANGSTROM();


		RealVector trans = this.getTranslationVector();
		double x = trans.getEntry(0);
		double y = trans.getEntry(1);
		double z = trans.getEntry(2);
		int xInt = (int) Math.round(x / unit);
		int yInt = (int) Math.round(y / unit);
		int zInt = (int) Math.round(z / unit);


		RealMatrix rotMat = this.getRotationMatrix();
		Rotation rotation = new Rotation(rotMat.getData(), 0.01);

		//		Vector3D axis = rotation.getAxis();
		//		double axisX = axis.getX();
		//		double axisY = axis.getY();
		//		double axisZ = axis.getZ();
		//		int axisXInt = (int) Math.round(axisX / unit);
		//		int axisYInt = (int) Math.round(axisY / unit);
		//		int axisZInt = (int) Math.round(axisZ / unit);

		int unitAngle = 8; // degres
		double angle = rotation.getAngle();
		int angleInt = (int) Math.round((angle * 180 / Math.PI) / unitAngle);
		// trans is a vector, i round it to a factor of algoParameters.getCELL_DIMENSION_OF_THE_PROBABILITY_MAP_ANGSTROM()



		int hashcode = xInt;
		hashcode = hashcode * 71 + yInt;
		hashcode = hashcode * 71 + zInt;
		hashcode = hashcode * 71 + angleInt;		
		//System.out.println(hashcode);
		return hashcode;
	}



	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ResultsFromEvaluateCost)){
			return false;
		}

		if (obj == this){
			return true;
		}

		if (this.hashCode() == ((ResultsFromEvaluateCost) obj).hashCode()){
			return true;
		}

		return false;
	}


	//	@Override
	//	public boolean equals(Object obj) {
	//		if (!(obj instanceof ResultsFromEvaluateCost)){
	//			return false;
	//		}
	//
	//		if (obj == this){
	//			return true;
	//		}
	//
	//		ResultsFromEvaluateCost result = (ResultsFromEvaluateCost) obj;
	//
	//		double distanceR = computeDistanceOnRotMatrix(this, result);
	//		double distanceT = computeDistanceOnTranslation(this, result);
	//
	//		//System.out.println("distance R = " + distanceR + "  distanceT = " + distanceT);
	//		if (distanceT < 2.0 * algoParameters.getCELL_DIMENSION_OF_THE_PROBABILITY_MAP_ANGSTROM() && distanceR < 60.0 / 180.0){ // 2.8 / 6.0
	//			return true;
	//		}
	//
	//		return false;
	//	}




	// -------------------------------------------------------------------
	// Private methods
	// -------------------------------------------------------------------
	private double computeDistanceOnTranslation(ResultsFromEvaluateCost result1, ResultsFromEvaluateCost result2){

		RealVector trans1 = result1.getTranslationVector();
		RealVector trans2 = result2.getTranslationVector();
		double norm = Math.sqrt(Math.pow(trans1.getEntry(0) - trans2.getEntry(0), 2) + Math.pow(trans1.getEntry(1) - trans2.getEntry(1), 2) + Math.pow(trans1.getEntry(2) - trans2.getEntry(2), 2));
		return norm;
	}



	private double computeDistanceOnRotMatrix(ResultsFromEvaluateCost result1, ResultsFromEvaluateCost result2){

		RealMatrix rotMatrix1 = result1.getRotationMatrix();
		RealMatrix rotMatrix2 = result2.getRotationMatrix();

		//RealMatrix matrixForComputeDistance = MatrixUtils.createRealIdentityMatrix(3).subtract(rotMatrix1.multiply(rotMatrix2.transpose()));
		double m11 = 1 - (rotMatrix1.getEntry(0, 0) * rotMatrix2.getEntry(0, 0) + rotMatrix1.getEntry(0, 1) * rotMatrix2.getEntry(0, 1) + rotMatrix1.getEntry(0, 2) * rotMatrix2.getEntry(0, 2));
		double m12 = -1.0 * (rotMatrix1.getEntry(0, 0) * rotMatrix2.getEntry(1, 0) + rotMatrix1.getEntry(0, 1) * rotMatrix2.getEntry(1, 1) + rotMatrix1.getEntry(0, 2) * rotMatrix2.getEntry(1, 2));
		double m13 =  -1.0 * (rotMatrix1.getEntry(0, 0) * rotMatrix2.getEntry(2, 0) + rotMatrix1.getEntry(0, 1) * rotMatrix2.getEntry(2, 1) + rotMatrix1.getEntry(0, 2) * rotMatrix2.getEntry(2, 2));
		double m21 =  -1.0 * (rotMatrix1.getEntry(1, 0) * rotMatrix2.getEntry(0, 0) + rotMatrix1.getEntry(1, 1) * rotMatrix2.getEntry(0, 1) + rotMatrix1.getEntry(1, 2) * rotMatrix2.getEntry(0, 2));
		double m22 = 1 - (rotMatrix1.getEntry(1, 0) * rotMatrix2.getEntry(1, 0) + rotMatrix1.getEntry(1, 1) * rotMatrix2.getEntry(1, 1) + rotMatrix1.getEntry(1, 2) * rotMatrix2.getEntry(1, 2));
		double m23 =  -1.0 * (rotMatrix1.getEntry(1, 0) * rotMatrix2.getEntry(2, 0) + rotMatrix1.getEntry(1, 1) * rotMatrix2.getEntry(2, 1) + rotMatrix1.getEntry(1, 2) * rotMatrix2.getEntry(2, 2));
		double m31 =  -1.0 * (rotMatrix1.getEntry(2, 0) * rotMatrix2.getEntry(0, 0) + rotMatrix1.getEntry(2, 1) * rotMatrix2.getEntry(0, 1) + rotMatrix1.getEntry(2, 2) * rotMatrix2.getEntry(0, 2));
		double m32 =  -1.0 * (rotMatrix1.getEntry(2, 0) * rotMatrix2.getEntry(1, 0) + rotMatrix1.getEntry(2, 1) * rotMatrix2.getEntry(1, 1) + rotMatrix1.getEntry(2, 2) * rotMatrix2.getEntry(1, 2));
		double m33 = 1 - (rotMatrix1.getEntry(2, 0) * rotMatrix2.getEntry(2, 0) + rotMatrix1.getEntry(2, 1) * rotMatrix2.getEntry(2, 1) + rotMatrix1.getEntry(2, 2) * rotMatrix2.getEntry(2, 2));
		//System.out.println(matrixForComputeDistance.toString());

		double rmsd = m11*m11 + m12*m12 + m13*m13 + m21*m21 + m22*m22 + m23*m23 + m31 *m31 + m32*m32 + m33*m33;
		//		System.out.println(rotMatrix1.getRowDimension() + "  " + rotMatrix1.getColumnDimension() + "  " + rotMatrix2.getRowDimension() + "  " + rotMatrix2.getColumnDimension());
		//		double rmsdOnMatrix = 0;
		//		for (int i=0; i< 3; i++){
		//			for (int j=0; j< 3; j++){
		//				rmsdOnMatrix += matrixForComputeDistance.getEntry(i, j) * matrixForComputeDistance.getEntry(i, j);
		//			}
		//		}
		//		double residualMatrix = Math.sqrt(rmsdOnMatrix);
		double residual =  Math.sqrt(rmsd);
		//System.out.println(" vector dist = " + norm + "  matrix dit = " + residualMatrix);

		//		if (Math.abs(residualMatrix - residual) > 0.0001){
		//			System.out.println("pb");
		//		}else{
		//			System.out.println("ok");
		//		}
		return residual;
	}

	// -------------------------------------------------------------------
	// Getter and Setter
	// -------------------------------------------------------------------
	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
	public double getDistanceResidual() {
		return distanceResidual;
	}
	public RealMatrix getRotationMatrix() {
		return rotationMatrix;
	}
	public RealVector getTranslationVector() {
		return translationVector;
	}
	public RealVector getTranslationVectorToTranslateShape2ToOrigin() {
		return translationVectorToTranslateShape2ToOrigin;
	}
	public PairingAndNullSpaces getPairingAndNullSpaces() {
		return pairingAndNullSpaces;
	}
	public float getCoverage() {
		return coverage;
	}
}
