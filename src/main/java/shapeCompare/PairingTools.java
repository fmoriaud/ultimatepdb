package shapeCompare;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import hits.Hit;
import pointWithProperties.PointWithProperties;
import shape.ShapeContainerIfc;


public class PairingTools {

	public static PairingAndNullSpaces deepCopyNewPairingAndNewNullSpacesAndExtendIfNeeded(PairingAndNullSpaces inputPairingAndNewNullSpaces, Map<Integer, PointWithProperties> shape1, Map<Integer, PointWithProperties> shape2){

		PairingAndNullSpaces newPairingAndNewNullSpaces = PairingTools.deepCopyNewPairingAndNewNullSpaces(inputPairingAndNewNullSpaces);

		dealWithShape1(shape1, newPairingAndNewNullSpaces);
		dealWithShape2(shape2, newPairingAndNewNullSpaces);

		return newPairingAndNewNullSpaces;
	}



	private static void dealWithShape1(Map<Integer, PointWithProperties> shape, PairingAndNullSpaces newPairingAndNewNullSpaces) {

		for (Integer pointShape: shape.keySet()){
			boolean inPairing = newPairingAndNewNullSpaces.getPairing().containsKey(pointShape);
			boolean inNullSpace = newPairingAndNewNullSpaces.getNullSpaceOfMap1().contains(pointShape);
			if ((! inPairing) && (! inNullSpace)){
				newPairingAndNewNullSpaces.getNullSpaceOfMap1().add(pointShape);
			}
		}
	}



	private static void dealWithShape2(Map<Integer, PointWithProperties> shape, PairingAndNullSpaces newPairingAndNewNullSpaces) {

		for (Integer pointShape: shape.keySet()){
			boolean inPairing = newPairingAndNewNullSpaces.getPairing().containsValue(pointShape);
			boolean inNullSpace = newPairingAndNewNullSpaces.getNullSpaceOfMap2().contains(pointShape);
			if ((! inPairing) && (! inNullSpace)){
				newPairingAndNewNullSpaces.getNullSpaceOfMap2().add(pointShape);
			}
		}
	}



	//	public static PairingAndNullSpaces createPairingAndNullSpacesWithEmptyPairing(CollectionOfPointsWithProperties shape1, CollectionOfPointsWithProperties shape2){
	//
	//		Map<Integer,Integer> pairing = new HashMap<>();
	//		List<Integer> nullSpaceOfMap1 = new ArrayList<>();
	//		nullSpaceOfMap1.addAll(shape1.keySet());
	//		List<Integer> nullSpaceOfMap2 = new ArrayList<>();
	//		nullSpaceOfMap2.addAll(shape2.keySet());
	//
	//		PairingAndNullSpaces pairingAndNullSpaces = new PairingAndNullSpaces(pairing, nullSpaceOfMap1, nullSpaceOfMap2);
	//		return pairingAndNullSpaces;
	//	}



	public static List<Hit> generateHitsListFromResultList(List<ResultsFromEvaluateCost> resultList, ShapeContainerIfc shape){

		List<Hit> hitsList = new ArrayList<>();
		for (ResultsFromEvaluateCost result: resultList){

			Hit hit = new Hit(shape, result);
			hitsList.add(hit);
		}
		return hitsList;
	}



	public static PairingAndNullSpaces deepCopyNewPairingAndNewNullSpaces(PairingAndNullSpaces newPairingAndNewNullSpacesToCopy){

		Map<Integer, Integer> deepCopyPairing = new TreeMap<Integer, Integer>();
		List<Integer> deepCopyNullSpaceMap1 = new ArrayList<Integer>();
		List<Integer> deepCopyNullSpaceMap2 = new ArrayList<Integer>();

		for( Entry<Integer, Integer> entry: newPairingAndNewNullSpacesToCopy.getPairing().entrySet() ) { 
			deepCopyPairing.put(Integer.valueOf(entry.getKey().intValue()), Integer.valueOf(entry.getValue().intValue()));
		}


		for (Integer point: newPairingAndNewNullSpacesToCopy.getNullSpaceOfMap1()){
			deepCopyNullSpaceMap1.add(Integer.valueOf(point.intValue()));
		}
		for (Integer point: newPairingAndNewNullSpacesToCopy.getNullSpaceOfMap2()){
			deepCopyNullSpaceMap2.add(Integer.valueOf(point.intValue()));
		}

		if (newPairingAndNewNullSpacesToCopy.getPairing().size() != deepCopyPairing.size()){
			System.out.println("Critical error in deepCopyNewPairingAndNewNullSpaces: size of pairing differs");
			System.exit(0);
		}
		if (newPairingAndNewNullSpacesToCopy.getNullSpaceOfMap1().size() != deepCopyNullSpaceMap1.size()){
			System.out.println("Critical error in deepCopyNewPairingAndNewNullSpaces: size of NullSpaceMap1 differs");
			System.exit(0);
		}
		if (newPairingAndNewNullSpacesToCopy.getNullSpaceOfMap2().size() != deepCopyNullSpaceMap2.size()){
			System.out.println("Critical error in deepCopyNewPairingAndNewNullSpaces: size of NullSpaceMap2 differs");
			System.exit(0);
		}

		PairingAndNullSpaces newPairingAndNewNullSpaces = new PairingAndNullSpaces(deepCopyPairing, deepCopyNullSpaceMap1, deepCopyNullSpaceMap2);

		return newPairingAndNewNullSpaces;
	}



	public static Map<Integer, Integer> deepCopyPairing(Map<Integer, Integer> pairingToCopy){

		Map<Integer, Integer> deepCopyPairing = new TreeMap<Integer, Integer>();

		for( Entry<Integer, Integer> entry: pairingToCopy.entrySet() ) { 
			deepCopyPairing.put(Integer.valueOf(entry.getKey().intValue()), Integer.valueOf(entry.getValue().intValue()));
		}

		if (pairingToCopy.size() != deepCopyPairing.size()){
			System.out.println("Critical error in deepCopyPairing: size of pairing differs");
			System.exit(0);
		}

		return deepCopyPairing;
	}



	public static List<Integer> deepCopyNullSpaces(List<Integer> nullSpacesToCopy){

		List<Integer> deepCopyNullSpace = new ArrayList<Integer>();


		for (Integer point: nullSpacesToCopy){
			deepCopyNullSpace.add(Integer.valueOf(point.intValue()));
		}

		if (nullSpacesToCopy.size() != deepCopyNullSpace.size()){
			System.out.println("Critical error in deepCopyNullSpaces: size of NullSpaceMap1 differs");
			System.exit(0);
		}

		return deepCopyNullSpace;
	}



	public static PairingAndNullSpaces razorBladeOfAPairing(PairingAndNullSpaces inputPairingAndNewNullSpaces, List<Integer> listPairsWithHighDistance, Map<Integer, Integer> mapOfIdInDistanceMatrixAndPairID){


		//System.out.println("size of list before razor blading = " + mapOfIdInDistanceMatrixAndPairID.size());
		//System.out.println("size of listPairsWithHighDistance = " + listPairsWithHighDistance.size());

		PairingAndNullSpaces cutInputPairingAndNewNullSpaces = PairingTools.deepCopyNewPairingAndNewNullSpaces(inputPairingAndNewNullSpaces);

		//System.out.println("size of the pairting = " + inputPairing.getPairing().size());
		//System.out.println("size of list of HighDistance = " + listPairsWithHighDistance.size());

		for (Integer iDOfPairByPointInMap1: listPairsWithHighDistance){
			//Integer idOfPairToRemoveInMapping = mapOfIdInDistanceMatrixAndPairID.get(iDOfPairByPointInMap1);

			Integer idOfPartnerInMapping = inputPairingAndNewNullSpaces.getPairing().get(Integer.valueOf(iDOfPairByPointInMap1.intValue()));

			if ( idOfPartnerInMapping == null){
				System.out.println("Error: pair from listPairsWithHighDistance is not a pair  " + iDOfPairByPointInMap1 + "   " +  idOfPartnerInMapping);
				continue;
			}

			// put in null space the high distances points 
			cutInputPairingAndNewNullSpaces.getNullSpaceOfMap1().add(Integer.valueOf(iDOfPairByPointInMap1.intValue()));
			cutInputPairingAndNewNullSpaces.getNullSpaceOfMap2().add(Integer.valueOf(idOfPartnerInMapping.intValue()));
			cutInputPairingAndNewNullSpaces.getPairing().remove(Integer.valueOf(iDOfPairByPointInMap1.intValue()));
			//}
		}



		//System.out.println("after razor blade pair count is " + cutInputPairingAndNewNullSpaces.getPairing().size());
		return cutInputPairingAndNewNullSpaces;
	}



	public static PairingAndNullSpaces highDistanceToOutsideDifferenceCutter(PairingAndNullSpaces inputPairingAndNewNullSpaces, List<Integer> listOfPairsWithAHighDistanceToOutsideDifference){
		PairingAndNullSpaces cutInputPairingAndNewNullSpaces = PairingTools.deepCopyNewPairingAndNewNullSpaces(inputPairingAndNewNullSpaces);

		for (Integer iDOfPairByPointInMap1: listOfPairsWithAHighDistanceToOutsideDifference){
			//Integer idOfPairToRemoveInMapping = mapOfIdInDistanceMatrixAndPairID.get(iDOfPairByPointInMap1);

			Integer idOfPartnerInMapping = inputPairingAndNewNullSpaces.getPairing().get(Integer.valueOf(iDOfPairByPointInMap1.intValue()));

			if ( idOfPartnerInMapping == null){
				//System.out.println("Error: pair from listPairsWithHighDistance is not a pair  " + iDOfPairByPointInMap1 + "   " +  idOfPartnerInMapping);
				// Normal that there are missing, they could have been withdrawn by razorblading before
			} else{

				// put in null space the high distances points 
				cutInputPairingAndNewNullSpaces.getNullSpaceOfMap1().add(Integer.valueOf(iDOfPairByPointInMap1.intValue()));
				cutInputPairingAndNewNullSpaces.getNullSpaceOfMap2().add(Integer.valueOf(idOfPartnerInMapping.intValue()));
				cutInputPairingAndNewNullSpaces.getPairing().remove(Integer.valueOf(iDOfPairByPointInMap1.intValue()));
				//}
			}
		}

		return cutInputPairingAndNewNullSpaces;
	}



	public static boolean isMatrixAndTranslationNearlyPerfect(RealMatrix bestRotationMatrix, RealVector bestTranslationVector){

		double thresholdRotDiag = 0.95;
		double thresholdRotOutDiag = 0.20;
		double thresholdTranslation = 1.5;

		if (bestRotationMatrix.getEntry(0, 0) < thresholdRotDiag ){
			//System.out.println("bestRotationMatrix.getEntry(0, 0) bad = " + bestRotationMatrix.getEntry(0, 0));
			return false;
		}
		if (bestRotationMatrix.getEntry(1, 1) < thresholdRotDiag ){
			//System.out.println("bestRotationMatrix.getEntry(1, 1) bad = " + bestRotationMatrix.getEntry(1, 1));

			return false;
		}
		if (bestRotationMatrix.getEntry(2, 2) < thresholdRotDiag ){
			//System.out.println("bestRotationMatrix.getEntry(2, 2) bad = " + bestRotationMatrix.getEntry(2, 2));

			return false;
		}
		if (Math.abs(bestRotationMatrix.getEntry(0, 1)) > thresholdRotOutDiag ){
			//System.out.println("bestRotationMatrix.getEntry(0, 1) bad = " + bestRotationMatrix.getEntry(0, 1));

			return false;
		}
		if (Math.abs(bestRotationMatrix.getEntry(0, 2)) > thresholdRotOutDiag ){
			//System.out.println("bestRotationMatrix.getEntry(0, 2) bad = " + bestRotationMatrix.getEntry(0, 2));

			return false;
		}
		if (Math.abs(bestRotationMatrix.getEntry(1, 2)) > thresholdRotOutDiag ){
			//System.out.println("bestRotationMatrix.getEntry(1, 2) bad = " + bestRotationMatrix.getEntry(1, 2));

			return false;
		}
		if (Math.abs(bestTranslationVector.getEntry(0)) > thresholdTranslation){
			//System.out.println("bestTranslationVector.getEntry(0) bad = " + bestTranslationVector.getEntry(0));

			return false;
		}

		if (Math.abs(bestTranslationVector.getEntry(1)) > thresholdTranslation){
			//System.out.println("bestTranslationVector.getEntry(1) bad = " + bestTranslationVector.getEntry(1));

			return false;
		}
		if (Math.abs(bestTranslationVector.getEntry(2)) > thresholdTranslation){
			//System.out.println("bestTranslationVector.getEntry(2) bad = " + bestTranslationVector.getEntry(2));

			return false;
		}

		return true;
	}



	public static boolean validate(PairingAndNullSpaces newPairingAndNewNullSpacesToTest){

		// I check if unique values
		double duplicateValue = 0;
		boolean duplicateValueFound = false;
		for( Entry<Integer, Integer> entry: newPairingAndNewNullSpacesToTest.getPairing().entrySet() ) { 

			int valueToTest = entry.getValue().intValue();
			int countOfFoundTimes = 0;
			for( Entry<Integer, Integer> entry2: newPairingAndNewNullSpacesToTest.getPairing().entrySet() ) {
				if (entry2.getValue().intValue() == valueToTest){
					duplicateValue = valueToTest;
					countOfFoundTimes +=1;
				}
			}
			if ( countOfFoundTimes > 1) {
				duplicateValueFound = true;
				System.out.println("duplicateValue = " + duplicateValue);

			}
		}

		double duplicateKey = 0;
		boolean duplicateKeyFound = false;
		for( Entry<Integer, Integer> entry: newPairingAndNewNullSpacesToTest.getPairing().entrySet() ) { 

			int valueToTest = entry.getKey().intValue();
			int countOfFoundTimes = 0;
			for( Entry<Integer, Integer> entry2: newPairingAndNewNullSpacesToTest.getPairing().entrySet() ) {
				if (entry2.getKey().intValue() == valueToTest){
					duplicateKey = valueToTest;
					countOfFoundTimes +=1;
				}
			}
			if ( countOfFoundTimes > 1) {
				duplicateKeyFound = true;
				System.out.println("duplicateKey = " + duplicateKey);

			}
		}

		if ( duplicateValueFound || duplicateKeyFound ){
			System.out.println("Problem found : validation duplicateValueFound not OK");
			System.out.println(newPairingAndNewNullSpacesToTest.getPairing());
			//System.exit(0);
			return false;
		}

		// check if there is a point in nullspace and also in pairing
		for( Entry<Integer, Integer> entry: newPairingAndNewNullSpacesToTest.getPairing().entrySet() ) { 

			int valueToTest = entry.getKey().intValue();
			for (Integer keyInNullSpace : newPairingAndNewNullSpacesToTest.getNullSpaceOfMap1()){
				if (valueToTest == keyInNullSpace.intValue()){
					System.out.println("Problem found : point in nullMap1 and in Pairing " + valueToTest);
					//System.exit(0);
					return false;
				}
			}

			valueToTest = entry.getValue().intValue();
			for (Integer keyInNullSpace : newPairingAndNewNullSpacesToTest.getNullSpaceOfMap2()){
				if (valueToTest == keyInNullSpace.intValue()){
					System.out.println("Problem found : point in nullMap2 and in Pairing " + valueToTest);
					//System.exit(0);
					return false;
				}
			}

		}
		return true;
	}


	public static boolean validate(PairingAndNullSpaces newPairingAndNewNullSpacesToTest, PairingAndNullSpaces lastAcceptedNewPairingAndNewNullSpaces){

		boolean isValid = validate(newPairingAndNewNullSpacesToTest);
		if (isValid == false){
			return false;
		}
		// the sum of pairs and null point of Map1 should be constant
		boolean checkedSumOfPairsAndNullPointOfMap1 = true;
		boolean checkedSumOfPairsAndNullPointOfMap2 = true;

		int sumOfPairsAndNullPointOfMap1 = newPairingAndNewNullSpacesToTest.getPairing().size() + newPairingAndNewNullSpacesToTest.getNullSpaceOfMap1().size();
		int sumOfPairsAndNullPointOfMap2 = newPairingAndNewNullSpacesToTest.getPairing().size() + newPairingAndNewNullSpacesToTest.getNullSpaceOfMap2().size();

		int lastAcceptedNewPairingSize = lastAcceptedNewPairingAndNewNullSpaces.getPairing().size();
		int initialSumOfPairsAndNullPointOfMap1 = lastAcceptedNewPairingSize + lastAcceptedNewPairingAndNewNullSpaces.getNullSpaceOfMap1().size();
		int initialSumOfPairsAndNullPointOfMap2 = lastAcceptedNewPairingSize + lastAcceptedNewPairingAndNewNullSpaces.getNullSpaceOfMap2().size();

		if ( sumOfPairsAndNullPointOfMap1 != initialSumOfPairsAndNullPointOfMap1){
			System.out.println("sumOfPairsAndNullPointOfMap1 "  +  sumOfPairsAndNullPointOfMap1 + "  " + initialSumOfPairsAndNullPointOfMap1);
			System.out.println(newPairingAndNewNullSpacesToTest.getNullSpaceOfMap1());
			checkedSumOfPairsAndNullPointOfMap1 = false;
		}
		if ( sumOfPairsAndNullPointOfMap2 != initialSumOfPairsAndNullPointOfMap2){
			System.out.println("sumOfPairsAndNullPointOfMap2 "  +  sumOfPairsAndNullPointOfMap2 + "  " + initialSumOfPairsAndNullPointOfMap2);
			System.out.println(newPairingAndNewNullSpacesToTest.getNullSpaceOfMap2());
			checkedSumOfPairsAndNullPointOfMap2 = false;
		}

		if ( checkedSumOfPairsAndNullPointOfMap1 == false ){

			System.out.println("Problem found : validation checkedSumOfPairsAndNullPointOfMap1 not OK");

			System.exit(0);
			return false;
		}

		if ( checkedSumOfPairsAndNullPointOfMap2 == false ){

			System.out.println("Problem found : validation checkedSumOfPairsAndNullPointOfMap2 not OK");

			System.exit(0);
			return false;
		}
		return true;
	}



	public static class HighestCoverageHitComparator implements Comparator<Hit>{

		@Override
		public int compare(Hit hit1, Hit hit2) {

			if (hit1.getResultsFromEvaluateCost().getCoverage() < hit2.getResultsFromEvaluateCost().getCoverage()){
				return 1;
			}
			if (hit1.getResultsFromEvaluateCost().getCoverage() > hit2.getResultsFromEvaluateCost().getCoverage()){
				return -1;
			}
			return 0;
		}
	}



	public static class LowestCostHitComparator implements Comparator<Hit>{

		@Override
		public int compare(Hit hit1, Hit hit2) {

			if (hit1.getResultsFromEvaluateCost().getCost() > hit2.getResultsFromEvaluateCost().getCost()){
				return 1;
			}
			if (hit1.getResultsFromEvaluateCost().getCost() < hit2.getResultsFromEvaluateCost().getCost()){
				return -1;
			}
			return 0;
		}
	}



	public static class LowestCostPairingComparator implements Comparator<ResultsFromEvaluateCost>{

		@Override
		public int compare(ResultsFromEvaluateCost cost1, ResultsFromEvaluateCost cost2) {

			if (cost1.getCost() > cost2.getCost()){
				return 1;
			}
			if (cost1.getCost() < cost2.getCost()){
				return -1;
			}
			return 0;
		}
	}



	public static class HighestPairCountPairingComparator implements Comparator<ResultsFromEvaluateCost>{

		@Override
		public int compare(ResultsFromEvaluateCost cost1, ResultsFromEvaluateCost cost2) {

			if (cost1.getPairingAndNullSpaces().getPairing().size() < cost2.getPairingAndNullSpaces().getPairing().size()){
				return 1;
			}
			if (cost1.getPairingAndNullSpaces().getPairing().size() > cost2.getPairingAndNullSpaces().getPairing().size()){
				return -1;
			}
			return 0;
		}
	}


	public static RealVector alignPointFromShape2toShape1(ResultsFromEvaluateCost result, RealVector pointFromShape2){
		
		RealVector transformedPoint = alignPointFromShape2toShape1(result.getTranslationVectorToTranslateShape2ToOrigin(), result.getTranslationVector(), result.getRotationMatrix(), pointFromShape2);
		return transformedPoint;
	}



	public static RealVector alignPointFromShape2toShape1(RealVector trans2toOrigin, RealVector trans, RealMatrix rot, RealVector point){

		RealVector translatedToOriginPoint = point.add(trans2toOrigin.copy());
		RealVector rotatedPoint = rot.operate(translatedToOriginPoint);
		RealVector translatedBackPoint = rotatedPoint.subtract(trans2toOrigin);

		RealVector translatedToShape1 = translatedBackPoint.add(trans);

		return translatedToShape1;
	}


	//	public static RealVector alignPointFromShape2toShape1(ResultsFromEvaluateCost result, RealVector pointFromShape2){
	//
	//		RealVector translatedToOriginPoint = pointFromShape2.add(result.getTranslationVectorToTranslateShape2ToOrigin().copy());
	//		RealVector rotatedPoint = result.getRotationMatrix().operate(translatedToOriginPoint);
	//		RealVector translatedBackPoint = rotatedPoint.subtract(result.getTranslationVectorToTranslateShape2ToOrigin());
	//
	//		RealVector translatedToShape1 = translatedBackPoint.add(result.getTranslationVector());
	//
	//		return translatedToShape1;
	//	}
}
