package multithread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.RecursiveTask;

import org.apache.commons.math3.linear.RealMatrix;

import math.AddToMap;
import math.ToolsMath;
import parameters.AlgoParameters;
import pointWithProperties.CollectionOfPointsWithPropertiesIfc;
import pointWithProperties.PointWithPropertiesIfc;
import pointWithProperties.StrikingPropertiesTools;
import shapeCompare.PairPointWithDistance;
import shapeCompare.PairingAndNullSpaces;
import shapeCompare.PairingTools;
import shapeCompare.ResultsFromEvaluateCost;
import shapeReduction.PairInteger;

public class ExtendPairingToMaxPairingInMiniShapeWhileKeepingMatchingOfStrikingPropertiesIfPossibleOtherwiseClosestMatchOrLeftUnpairedIfTooFarMultiThread extends RecursiveTask<List<PairingAndNullSpaces>>{

	//------------------------
	// Class variables
	//------------------------
	private final List<ResultsFromEvaluateCost> listResultSeed;
	private int start;
	private int end;
	private int threshold;
	private final CollectionOfPointsWithPropertiesIfc shape1;
	private final CollectionOfPointsWithPropertiesIfc shape2;
	private AlgoParameters algoParameters;
	private static final long serialVersionUID = 1L;

	private List<Integer> pointAlreadyFoundInShape1 = new ArrayList<>();
	private List<Integer> pointAlreadyFoundInShape2 = new ArrayList<>();
	private List<PairPointWithDistance> globalList = new ArrayList<>();

	private boolean debug = false;




	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public ExtendPairingToMaxPairingInMiniShapeWhileKeepingMatchingOfStrikingPropertiesIfPossibleOtherwiseClosestMatchOrLeftUnpairedIfTooFarMultiThread(List<ResultsFromEvaluateCost> listResultSeed, int start, int end, int threshold, CollectionOfPointsWithPropertiesIfc shape1, CollectionOfPointsWithPropertiesIfc shape2, AlgoParameters algoParameters){

		this.listResultSeed = listResultSeed;
		this.start = start;
		this.end = end;
		this.threshold = threshold;
		this.shape1 = shape1;
		this.shape2 = shape2;
		this.algoParameters = algoParameters;
	}


	@Override
	protected List<PairingAndNullSpaces> compute() {
		List<PairingAndNullSpaces> listExtendedPairing = new ArrayList<>();

		if (end - start < threshold){

			for (int i = start; i <= end; i++){

				ResultsFromEvaluateCost result = listResultSeed.get(i);

				extendSeed(result);
				listExtendedPairing.add(result.getPairingAndNullSpaces());

			}

		} else{
			int midway = (end - start) / 2 + start;
			ExtendPairingToMaxPairingInMiniShapeWhileKeepingMatchingOfStrikingPropertiesIfPossibleOtherwiseClosestMatchOrLeftUnpairedIfTooFarMultiThread extendTask1 = new ExtendPairingToMaxPairingInMiniShapeWhileKeepingMatchingOfStrikingPropertiesIfPossibleOtherwiseClosestMatchOrLeftUnpairedIfTooFarMultiThread(listResultSeed, start, midway, threshold, shape1, shape2, algoParameters);
			extendTask1.fork();

			ExtendPairingToMaxPairingInMiniShapeWhileKeepingMatchingOfStrikingPropertiesIfPossibleOtherwiseClosestMatchOrLeftUnpairedIfTooFarMultiThread extendTask2 = new ExtendPairingToMaxPairingInMiniShapeWhileKeepingMatchingOfStrikingPropertiesIfPossibleOtherwiseClosestMatchOrLeftUnpairedIfTooFarMultiThread(listResultSeed, midway + 1, end, threshold, shape1, shape2, algoParameters);
			listExtendedPairing.addAll(extendTask2.compute());
			listExtendedPairing.addAll(extendTask1.join());

		}

		return listExtendedPairing;
	}


	// -------------------------------------------------------------------
	// Implementation
	// -------------------------------------------------------------------
	private void extendSeed(ResultsFromEvaluateCost resultSeed){

		generatePairingAndNullSpaceUsingMatchingPointWithPropertiesNostaticlist(resultSeed, shape1, shape2, algoParameters);

		boolean isValid = PairingTools.validate(resultSeed.getPairingAndNullSpaces());
		if (isValid == true){
			//System.out.println("Valid");
		}else{
			System.out.println("Not Valid so exit");
			System.exit(0);
		}
	}



	private void generatePairingAndNullSpaceUsingMatchingPointWithPropertiesNostaticlist(ResultsFromEvaluateCost resultSeed, final CollectionOfPointsWithPropertiesIfc shape1, final CollectionOfPointsWithPropertiesIfc shape2, AlgoParameters algoParameters) {

		// Both points has StrikingProperties not none and they all match (also if only one of course)
		Map<Integer, List<PairPointWithDistance>> mapforAllMatchingStrikingProperties = new HashMap<>();

		// Both points has more than one striking properties and at least one of them match
		List<PairPointWithDistance> listPairsMatchingOneAmongOthersStrikingPropertiesWithShortDistance = new ArrayList<>();

		// Both points has None Striking Properties
		List<PairPointWithDistance> listPairsMatchingOnlyNoneStrikingPropertiesWithShortDistance = new ArrayList<>();

		// One point has None striking property and the others has striking properties
		List<PairPointWithDistance> listPairsNoneMatchingWithoutStrikingPropertiesWithShortDistance = new ArrayList<>();

		// Both points has Striking properties but not a single one is matching
		List<PairPointWithDistance> listPairsNonMatchingWithoutStrikingPropertiesWithShortDistance = new ArrayList<>();

		for (int i=0; i<shape1.getSize(); i++){
			for (int j=0; j<shape2.getSize(); j++){

				// Only Pairs of matching properties point are kept
				PointWithPropertiesIfc pointWithProperties1 = shape1.getPointFromId(i);
				PointWithPropertiesIfc pointWithProperties2 = shape2.getPointFromId(j);

				RealMatrix matrix = resultSeed.getRotationMatrix();
				float[] vector1 = new float[3];
				for (int k=0; k<3; k++){
					vector1[k] = (float) (pointWithProperties2.getCoords().getCoords()[k] + resultSeed.getTranslationVectorToTranslateShape2ToOrigin().getEntry(k));
				}

				double[] vector2 = new double[3];
				for (int k=0; k<3; k++){
					for (int l=0; l<3; l++){
						vector2[k] += matrix.getEntry(k, l) * vector1[l];
					}
				}

				for (int k=0; k<3; k++){
					vector1[k] = (float) (vector2[k] - resultSeed.getTranslationVectorToTranslateShape2ToOrigin().getEntry(k) + resultSeed.getTranslationVector().getEntry(k));
				}

				double distance = ToolsMath.computeDistance(pointWithProperties1.getCoords().getCoords(), vector1);

				if (distance < algoParameters.getDISTANCE_MIN_FOR_EXTENDED_PAIRING_FROM_SEED()){

					PairPointWithDistance pairPointIdsFromMinishape1andMinishape2WithMatchingStrikingPropertiesWithDistance = new PairPointWithDistance(i, j, distance);

					int countOfMatchingStrikingPropertiesWhenAllAreMatching = StrikingPropertiesTools.evaluatePointsMatchingAllNotNoneProperties(pointWithProperties1, pointWithProperties2);
					if (countOfMatchingStrikingPropertiesWhenAllAreMatching > 0){
						AddToMap.addElementToAMapOfList(mapforAllMatchingStrikingProperties, countOfMatchingStrikingPropertiesWhenAllAreMatching, pairPointIdsFromMinishape1andMinishape2WithMatchingStrikingPropertiesWithDistance);
						continue;
					}

					if (StrikingPropertiesTools.evaluatePointsMatchingOneButNotAllNotNoneProperties(pointWithProperties1, pointWithProperties2)){
						listPairsMatchingOneAmongOthersStrikingPropertiesWithShortDistance.add(pairPointIdsFromMinishape1andMinishape2WithMatchingStrikingPropertiesWithDistance);
						continue;
					}

					if (StrikingPropertiesTools.evaluatePointsMatchingOnlyNoneProperties(pointWithProperties1, pointWithProperties2)){
						listPairsMatchingOnlyNoneStrikingPropertiesWithShortDistance.add(pairPointIdsFromMinishape1andMinishape2WithMatchingStrikingPropertiesWithDistance);
						continue;
					}

					if (StrikingPropertiesTools.evaluatePointsNoneMatchingANotNONEtoOnlyNoneProperties(pointWithProperties1, pointWithProperties2)){
						listPairsNoneMatchingWithoutStrikingPropertiesWithShortDistance.add(pairPointIdsFromMinishape1andMinishape2WithMatchingStrikingPropertiesWithDistance);
						continue;
					}

					if (StrikingPropertiesTools.evaluatePointsNoneMatchingANotNONEtoNotNoneProperties(pointWithProperties1, pointWithProperties2)){
						listPairsNonMatchingWithoutStrikingPropertiesWithShortDistance.add(pairPointIdsFromMinishape1andMinishape2WithMatchingStrikingPropertiesWithDistance);
						continue;
					}

					System.out.println();
				}
			}
		}
		//		for (Entry<Integer, List<PairPointWithDistance>> entry: mapforAllMatchingStrikingProperties.entrySet()){
		//			cleanListPairsFromPointsInvolvedInSeedPairings(resultSeed.getPairingAndNullSpaces(), entry.getValue());
		//		}
		//		cleanListPairsFromPointsInvolvedInSeedPairings(resultSeed.getPairingAndNullSpaces(), listPairsMatchingOneAmongOthersStrikingPropertiesWithShortDistance);
		//		cleanListPairsFromPointsInvolvedInSeedPairings(resultSeed.getPairingAndNullSpaces(), listPairsMatchingOnlyNoneStrikingPropertiesWithShortDistance);
		//		cleanListPairsFromPointsInvolvedInSeedPairings(resultSeed.getPairingAndNullSpaces(), listPairsNoneMatchingWithoutStrikingPropertiesWithShortDistance);
		//		cleanListPairsFromPointsInvolvedInSeedPairings(resultSeed.getPairingAndNullSpaces(), listPairsNonMatchingWithoutStrikingPropertiesWithShortDistance);

		for (Entry<Integer, List<PairPointWithDistance>> entry: mapforAllMatchingStrikingProperties.entrySet()){
			Collections.sort(entry.getValue(), new PairPointWithDistance.LowestDistancePairPointWithDistance());
		}
		Collections.sort(listPairsMatchingOneAmongOthersStrikingPropertiesWithShortDistance, new PairPointWithDistance.LowestDistancePairPointWithDistance());
		Collections.sort(listPairsMatchingOnlyNoneStrikingPropertiesWithShortDistance, new PairPointWithDistance.LowestDistancePairPointWithDistance());
		Collections.sort(listPairsNoneMatchingWithoutStrikingPropertiesWithShortDistance, new PairPointWithDistance.LowestDistancePairPointWithDistance());
		Collections.sort(listPairsNonMatchingWithoutStrikingPropertiesWithShortDistance, new PairPointWithDistance.LowestDistancePairPointWithDistance());
		// loop on the list
		// when a point already found is found then skip the pair
		//		for (Entry<Integer, List<PairPointWithDistance>> entry: mapforAllMatchingStrikingProperties.entrySet()){
		//			cleanOfDuplicatePoints(entry.getValue());
		//		}
		//		cleanOfDuplicatePoints(listPairsMatchingOneAmongOthersStrikingPropertiesWithShortDistance);
		//		cleanOfDuplicatePoints(listPairsMatchingOnlyNoneStrikingPropertiesWithShortDistance);
		//		cleanOfDuplicatePoints(listPairsNoneMatchingWithoutStrikingPropertiesWithShortDistance);
		//		cleanOfDuplicatePoints(listPairsNonMatchingWithoutStrikingPropertiesWithShortDistance);

		// Now I should try to pair unpaired point so far 
		// clean the list of pairs matching distance but without matching properties OF pairs having points in matching properties list

		globalList.clear();
		List<Integer> keys = new ArrayList<>();
		keys.addAll(mapforAllMatchingStrikingProperties.keySet());
		Collections.sort(keys, Collections.reverseOrder());
		for (Integer key: keys){
			globalList.addAll(mapforAllMatchingStrikingProperties.get(key));
		}

		globalList.addAll(listPairsMatchingOneAmongOthersStrikingPropertiesWithShortDistance);
		globalList.addAll(listPairsMatchingOnlyNoneStrikingPropertiesWithShortDistance);
		globalList.addAll(listPairsNoneMatchingWithoutStrikingPropertiesWithShortDistance);
		globalList.addAll(listPairsNonMatchingWithoutStrikingPropertiesWithShortDistance);

		cleanOfDuplicatePoints(globalList);

		// now i can build the pairing
		// initial pairing might be on minishape and shape1 and aligned shape on a larger set like Shape
		//PairingAndNullSpaces newPairingAndNewNullSpaces = deepCopyNewPairingAndNewNullSpacesAndExtendIfNeeded(resultSeed.getPairingAndNullSpaces(), shape1, shape2);

		resultSeed.getPairingAndNullSpaces().getPairing().clear();
		emptyPairingShape1(shape1, resultSeed.getPairingAndNullSpaces());
		emptyPairingShape2(shape2, resultSeed.getPairingAndNullSpaces());

		// loop on new pairs and add
		Iterator<PairPointWithDistance> itr = globalList.iterator();
		while(itr.hasNext()) {
			PairPointWithDistance pairPointWithDistance = itr.next();
			if (pairPointWithDistance.getDistance() < 0.7){ // We really use the sorting for very short distances
				if (resultSeed.getPairingAndNullSpaces().getNullSpaceOfMap1().contains(Integer.valueOf(pairPointWithDistance.getPairInteger().point1)) 
						&& resultSeed.getPairingAndNullSpaces().getNullSpaceOfMap2().contains(Integer.valueOf(pairPointWithDistance.getPairInteger().point2)) ){
					resultSeed.getPairingAndNullSpaces().getPairing().put(pairPointWithDistance.getPairInteger().point1, pairPointWithDistance.getPairInteger().point2);
					boolean toto = resultSeed.getPairingAndNullSpaces().getNullSpaceOfMap1().remove(Integer.valueOf(pairPointWithDistance.getPairInteger().point1));
					boolean toto2 = resultSeed.getPairingAndNullSpaces().getNullSpaceOfMap2().remove(Integer.valueOf(pairPointWithDistance.getPairInteger().point2));

					itr.remove();
				}
			}
		}
		Collections.sort(globalList, new PairPointWithDistance.LowestDistancePairPointWithDistance());
		Iterator<PairPointWithDistance> itr2 = globalList.iterator();
		while(itr2.hasNext()) {
			PairPointWithDistance pairPointWithDistance = itr2.next();
			if (resultSeed.getPairingAndNullSpaces().getNullSpaceOfMap1().contains(Integer.valueOf(pairPointWithDistance.getPairInteger().point1)) 
					&& resultSeed.getPairingAndNullSpaces().getNullSpaceOfMap2().contains(Integer.valueOf(pairPointWithDistance.getPairInteger().point2)) ){
				resultSeed.getPairingAndNullSpaces().getPairing().put(pairPointWithDistance.getPairInteger().point1, pairPointWithDistance.getPairInteger().point2);
				boolean toto = resultSeed.getPairingAndNullSpaces().getNullSpaceOfMap1().remove(Integer.valueOf(pairPointWithDistance.getPairInteger().point1));
				boolean toto2 = resultSeed.getPairingAndNullSpaces().getNullSpaceOfMap2().remove(Integer.valueOf(pairPointWithDistance.getPairInteger().point2));
			}
		}

		if (debug == true){
			boolean validPairing = PairingTools.validate(resultSeed.getPairingAndNullSpaces());
			if (validPairing == false){
				System.out.println("Extended pairing is not valid");
			}

			// for debug I recheck if there are remaining short distance

			List<PairPointWithDistance> remainingShortDistance = new ArrayList<>();

			for (Integer point1: resultSeed.getPairingAndNullSpaces().getNullSpaceOfMap1()){
				PointWithPropertiesIfc pointWithProperties1 = shape1.getPointFromId(point1);

				for (Integer point2: resultSeed.getPairingAndNullSpaces().getNullSpaceOfMap2()){

					PointWithPropertiesIfc pointWithProperties2 = shape2.getPointFromId(point2);

					RealMatrix matrix = resultSeed.getRotationMatrix();
					float[] vector1 = new float[3];
					for (int k=0; k<3; k++){
						vector1[k] = (float) (pointWithProperties2.getCoords().getCoords()[k] + resultSeed.getTranslationVectorToTranslateShape2ToOrigin().getEntry(k));
					}

					double[] vector2 = new double[3];
					for (int k=0; k<3; k++){
						for (int l=0; l<3; l++){
							vector2[k] += matrix.getEntry(k, l) * vector1[l];
						}
					}

					for (int k=0; k<3; k++){
						vector1[k] = (float) (vector2[k] - resultSeed.getTranslationVectorToTranslateShape2ToOrigin().getEntry(k) + resultSeed.getTranslationVector().getEntry(k));
					}


					double distance = ToolsMath.computeDistance(pointWithProperties1.getCoords().getCoords(), vector1);

					if (distance < algoParameters.getDISTANCE_MIN_FOR_EXTENDED_PAIRING_FROM_SEED()){

						PairPointWithDistance pairPointIdsFromMinishape1andMinishape2WithMatchingStrikingPropertiesWithDistance = new PairPointWithDistance(point1, point2, distance);
						remainingShortDistance.add(pairPointIdsFromMinishape1andMinishape2WithMatchingStrikingPropertiesWithDistance);
						//System.out.println(distance);
					}
				}

			}
			System.out.println("remainingShortDistance = " + remainingShortDistance.size());
			if (remainingShortDistance.size() > 10){
				System.out.println();
			}
		}

	}



	private void cleanListPairFromDuplicatesPoints(List<PairPointWithDistance> listPair){

		Iterator<PairPointWithDistance> itr = listPair.iterator();
		int index = -1;
		while(itr.hasNext()) {
			index +=1;
			PairPointWithDistance nextPair = itr.next();
			if (index < 1){
				continue;
			}

			boolean isPairToBeKept = true;
			for (int i=0; i<index; i++){
				if (nextPair.getPairInteger().point1 == listPair.get(i).getPairInteger().point1){
					isPairToBeKept = false;
					break;
				}
				if (nextPair.getPairInteger().point2 == listPair.get(i).getPairInteger().point2){
					isPairToBeKept = false;
					break;
				}
			}
			if (isPairToBeKept == false){
				itr.remove();
			}
		}
	}



	private PairingAndNullSpaces deepCopyNewPairingAndNewNullSpacesAndExtendIfNeeded(PairingAndNullSpaces inputPairingAndNewNullSpaces, CollectionOfPointsWithPropertiesIfc shape1, CollectionOfPointsWithPropertiesIfc shape2){

		PairingAndNullSpaces newPairingAndNewNullSpaces = deepCopyNewPairingAndNewNullSpaces(inputPairingAndNewNullSpaces);

		dealWithShape1(shape1, newPairingAndNewNullSpaces);
		dealWithShape2(shape2, newPairingAndNewNullSpaces);

		return newPairingAndNewNullSpaces;
	}



	private PairingAndNullSpaces deepCopyNewPairingAndNewNullSpaces(PairingAndNullSpaces newPairingAndNewNullSpacesToCopy){

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



	private void emptyPairingShape1(CollectionOfPointsWithPropertiesIfc shape, PairingAndNullSpaces newPairingAndNewNullSpaces){


		newPairingAndNewNullSpaces.getNullSpaceOfMap1().clear();

		for (int i=0; i<shape.getSize(); i++){
			newPairingAndNewNullSpaces.getNullSpaceOfMap1().add(i);
		}

	}



	private void emptyPairingShape2(CollectionOfPointsWithPropertiesIfc shape, PairingAndNullSpaces newPairingAndNewNullSpaces){

		newPairingAndNewNullSpaces.getNullSpaceOfMap2().clear();

		for (int i=0; i<shape.getSize(); i++){
			newPairingAndNewNullSpaces.getNullSpaceOfMap2().add(i);
		}

	}



	private void dealWithShape1(CollectionOfPointsWithPropertiesIfc shape, PairingAndNullSpaces newPairingAndNewNullSpaces) {

		for (int i=0; i<shape.getSize(); i++){
			boolean inPairing = newPairingAndNewNullSpaces.getPairing().containsKey(i);
			boolean inNullSpace = newPairingAndNewNullSpaces.getNullSpaceOfMap1().contains(i);
			if ((! inPairing) && (! inNullSpace)){
				newPairingAndNewNullSpaces.getNullSpaceOfMap1().add(i);
			}
		}
	}



	private void dealWithShape2(CollectionOfPointsWithPropertiesIfc shape, PairingAndNullSpaces newPairingAndNewNullSpaces) {

		for (int i=0; i<shape.getSize(); i++){
			boolean inPairing = newPairingAndNewNullSpaces.getPairing().containsValue(i);
			boolean inNullSpace = newPairingAndNewNullSpaces.getNullSpaceOfMap2().contains(i);
			if ((! inPairing) && (! inNullSpace)){
				newPairingAndNewNullSpaces.getNullSpaceOfMap2().add(i);
			}
		}
	}



	private boolean isPairhavingToPointsNotAlreadyInMatchingPropertiesList(PairInteger pair, List<PairPointWithDistance> listPair){

		for (PairPointWithDistance pairAlready: listPair){
			if (pair.point1 == pairAlready.getPairInteger().point1){
				return false;
			}
			if (pair.point2 == pairAlready.getPairInteger().point2){
				return false;
			}
		}
		return true;
	}



	private void cleanOfDuplicatePoints(List<PairPointWithDistance> listPairsWithShortDistance) {

		pointAlreadyFoundInShape1.clear();
		pointAlreadyFoundInShape2.clear();
		Iterator<PairPointWithDistance> itr = listPairsWithShortDistance.iterator();
		while(itr.hasNext()) {
			PairPointWithDistance pairPointWithDistance = itr.next();
			int point1 = pairPointWithDistance.getPairInteger().point1;
			int point2 = pairPointWithDistance.getPairInteger().point2;
			if ( pointAlreadyFoundInShape1.contains(point1) || pointAlreadyFoundInShape2.contains(point2)){
				itr.remove();
			}else{
				pointAlreadyFoundInShape1.add(point1);
				pointAlreadyFoundInShape2.add(point2);
			}
		}
	}



	private void cleanListPairsFromPointsInvolvedInSeedPairings(PairingAndNullSpaces pairing, List<PairPointWithDistance> listPairsWithShortDistance) {

		Iterator<PairPointWithDistance> itr = listPairsWithShortDistance.iterator();
		A: while(itr.hasNext()) {
			PairPointWithDistance pairInteger = itr.next();

			for (Entry<Integer, Integer> entry: pairing.getPairing().entrySet()){
				Integer pointFromShape1 = entry.getKey();
				Integer pointFromShape2 = entry.getValue();

				if (pairInteger.getPairInteger().point1 == pointFromShape1.intValue() || pairInteger.getPairInteger().point2 == pointFromShape2.intValue()){
					itr.remove();
					continue A;
				}
			}
		}
	}
}