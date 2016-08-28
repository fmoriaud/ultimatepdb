package alteratepdbfile;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import shapeCompare.ResultsFromEvaluateCost;


public class ResultsFromEvaluationCostTools {

	public static boolean isThatResultsHavingUnsignificantTransAndRot(ResultsFromEvaluateCost result ){

		double thresholdTrans = 0.7;
		double thresholdRotDiag = 0.9;
		double thresholdRotOffDiag = 0.1;
		RealVector transVector = result.getTranslationVector();
		RealMatrix rotMat = result.getRotationMatrix();

		if (transVector.getNorm() > thresholdTrans){
			return false;
		}

		if (rotMat.getEntry(0, 0) < thresholdRotDiag){
			return false;
		}
		if (rotMat.getEntry(1, 1) < thresholdRotDiag){
			return false;
		}
		if (rotMat.getEntry(2, 2) < thresholdRotDiag){
			return false;
		}
		if (rotMat.getEntry(0, 1) > thresholdRotOffDiag){
			return false;
		}
		if (rotMat.getEntry(0, 2) > thresholdRotOffDiag){
			return false;
		}
		if (rotMat.getEntry(1, 2) > thresholdRotOffDiag){
			return false;
		}
		return true;
	}


}
