package math;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class ConvertFormat {

	public static RealVector makeRealVectorFromFloatArray(float[] floatArray){

		double[] coordsDouble = new double[3];
		coordsDouble[0] = floatArray[0];
		coordsDouble[1] = floatArray[1];
		coordsDouble[2] = floatArray[2];

		RealVector coordsVector = new ArrayRealVector(coordsDouble);
		return coordsVector;
	}
	
	
	public static float[] makeFloatArrayFromRealVector(RealVector inputRealVector){
		
		float[] floatArray = new float[3];
		floatArray[0] = (float) inputRealVector.getEntry(0);
		floatArray[1] = (float) inputRealVector.getEntry(1);
		floatArray[2] = (float) inputRealVector.getEntry(2);
		return floatArray;
	}
}
