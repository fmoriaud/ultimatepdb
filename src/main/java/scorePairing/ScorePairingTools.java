package scorePairing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import math.ToolsMath;
import parameters.AlgoParameters;
import pointWithProperties.PointWithProperties;
import pointWithProperties.PointWithPropertiesIfc;
import pointWithProperties.StrikingPropertiesTools;
import shapeCompare.PairPointWithDistance;
import shapeCompare.PairingAndNullSpaces;
import shapeCompare.PairingTools;
import shapeCompare.ResultsFromEvaluateCost;
import shapeReduction.PairInteger;

public class ScorePairingTools {
	//------------------------
	// Class variables
	//------------------------
	// there are as class variable as they are only used internally to this static class


	//------------------------
	// Static Method
	//------------------------
	public static List<ResultsFromEvaluateCost> getCostOfaListOfPairing(List<PairingAndNullSpaces> listPairingAndNullSpacesToBeScored, Map<Integer, PointWithPropertiesIfc> shape1, Map<Integer, PointWithPropertiesIfc> shape2, AlgoParameters algoParameters){

		List<ResultsFromEvaluateCost> listCost = new ArrayList<>();

		for (PairingAndNullSpaces pairing: listPairingAndNullSpacesToBeScored){

			ResultsFromEvaluateCost cost = getCostOfaPairing(pairing, shape1, shape2, algoParameters);
			listCost.add(cost);
		}
		return listCost;
	}



	public static ResultsFromEvaluateCost getCostOfaPairing(PairingAndNullSpaces pairingAndNullSpacesToBeScored, Map<Integer, PointWithPropertiesIfc> queryShape, Map<Integer, PointWithPropertiesIfc> hitShape, AlgoParameters algoParameters){

		ResultFromScorePairing resultFromScorePairing = ScorePairingWithStaticMethods.computeCost(pairingAndNullSpacesToBeScored, queryShape, hitShape, algoParameters);

		RealMatrix rotationMatrix = resultFromScorePairing.getRotationMatrixToRotateShape2ToShape1().copy();
		RealVector translationVector = resultFromScorePairing.getTranslationVectorToTranslateShape2ToShape1().copy();
		RealVector translationVectorToTranslateShape2ToOrigin = resultFromScorePairing.getTranslationVectorToTranslateShape2ToOrigin();
		double cost = resultFromScorePairing.getCost();

		double distanceResidual = resultFromScorePairing.getDistanceResidual();
		float ratioPairedPointInQuery = computeQueryPairingCoverage(pairingAndNullSpacesToBeScored, algoParameters);
		
		ResultsFromEvaluateCost resultsFromEvaluateCost = new ResultsFromEvaluateCost(cost, distanceResidual, rotationMatrix, 
				translationVector, translationVectorToTranslateShape2ToOrigin, pairingAndNullSpacesToBeScored, ratioPairedPointInQuery, algoParameters);

		return resultsFromEvaluateCost;
	}



	private static float computeQueryPairingCoverage(PairingAndNullSpaces pairingAndNullSpacesToBeScored, AlgoParameters algoParameters){

		int pairedPointCount = pairingAndNullSpacesToBeScored.getPairing().size();
		int unpairedPointQuery = pairingAndNullSpacesToBeScored.getNullSpaceOfMap1().size();

		float ratioPairedPointInQuery = (float) pairedPointCount / ((float) pairedPointCount + (float) unpairedPointQuery);

		return ratioPairedPointInQuery;
	}


	public static PairingAndNullSpaces generatePairingAndNullSpaceUsingMatchingPointWithProperties(PairingAndNullSpaces initialPairing, Map<Integer, PointWithProperties> shape1, Map<Integer, PointWithProperties> shape2alignedToShape1, AlgoParameters algoParameters) {

		List<PairPointWithDistance> listPairsMatchingStrikingPropertiesWithShortDistance = new ArrayList<>();
		List<PairPointWithDistance> listPairsMatchingWithoutStrikingPropertiesWithShortDistance = new ArrayList<>();

		for (Entry<Integer, PointWithProperties> entry1: shape1.entrySet()){
			for (Entry<Integer, PointWithProperties> entry2: shape2alignedToShape1.entrySet()){

				// Only Pairs of matching properties point are kept
				PointWithProperties pointWithProperties1 = entry1.getValue();
				PointWithProperties pointWithProperties2 = entry2.getValue();

				double distance = ToolsMath.computeDistance(pointWithProperties1.getCoords().getCoords(), pointWithProperties2.getCoords().getCoords());

				if (distance < algoParameters.getDISTANCE_MIN_FOR_EXTENDED_PAIRING_FROM_SEED()){

					PairPointWithDistance pairPointIdsFromMinishape1andMinishape2WithMatchingStrikingPropertiesWithDistance = new PairPointWithDistance(entry1.getKey().intValue(), entry2.getKey().intValue(), distance);
					boolean arePointsHavingMatchingProperties = StrikingPropertiesTools.evaluatePointsMatchingWithAtLeastOneProperty(pointWithProperties1, pointWithProperties2);
					if (arePointsHavingMatchingProperties == true){
						listPairsMatchingStrikingPropertiesWithShortDistance.add(pairPointIdsFromMinishape1andMinishape2WithMatchingStrikingPropertiesWithDistance);
					} else{
						listPairsMatchingWithoutStrikingPropertiesWithShortDistance.add(pairPointIdsFromMinishape1andMinishape2WithMatchingStrikingPropertiesWithDistance);
					}
				}
			}
		}

		cleanListPairsFromPointsInvolvedInSeedPairings(initialPairing, listPairsMatchingStrikingPropertiesWithShortDistance);
		Collections.sort(listPairsMatchingStrikingPropertiesWithShortDistance, new PairPointWithDistance.LowestDistancePairPointWithDistance());

		cleanListPairsFromPointsInvolvedInSeedPairings(initialPairing, listPairsMatchingWithoutStrikingPropertiesWithShortDistance);
		Collections.sort(listPairsMatchingWithoutStrikingPropertiesWithShortDistance, new PairPointWithDistance.LowestDistancePairPointWithDistance());

		// loop on the list
		// when a point already found is found then skip the pair

		cleanOfDuplicatePoints(listPairsMatchingStrikingPropertiesWithShortDistance);
		cleanOfDuplicatePoints(listPairsMatchingWithoutStrikingPropertiesWithShortDistance);

		// Now I should try to pair unpaired point so far 
		// clean the list of pairs matching distance but without matching properties OF pairs having points in matching properties list
		List<PairPointWithDistance> listPairsWithoutMatchingPropertiesToBeApplied = new ArrayList<>();

		for (PairPointWithDistance pairWithoutMatchingStrikingProperties: listPairsMatchingWithoutStrikingPropertiesWithShortDistance){

			PairInteger pair = pairWithoutMatchingStrikingProperties.getPairInteger();
			boolean isPAirToBeAdded = isPairhavingToPointsNotAlreadyInMatchingPropertiesList(pair, listPairsMatchingStrikingPropertiesWithShortDistance);
			if (isPAirToBeAdded == true){
				listPairsWithoutMatchingPropertiesToBeApplied.add(pairWithoutMatchingStrikingProperties);
			}
		}

		listPairsMatchingStrikingPropertiesWithShortDistance.addAll(listPairsWithoutMatchingPropertiesToBeApplied);
		if (listPairsWithoutMatchingPropertiesToBeApplied.size() != 0){
			//System.out.println(" added pair with unmatched properties " + listPairsWithoutMatchingPropertiesToBeApplied.size());
		}

		// now i can build the pairing
		// initial pairing might be on minishape and shape1 and aligned shape on a larger set like Shape
		PairingAndNullSpaces newPairingAndNewNullSpaces = PairingTools.deepCopyNewPairingAndNewNullSpacesAndExtendIfNeeded(initialPairing, shape1, shape2alignedToShape1);

		// loop on new pairs and add
		for (PairPointWithDistance pairPointWithDistance: listPairsMatchingStrikingPropertiesWithShortDistance){

			newPairingAndNewNullSpaces.getPairing().put(pairPointWithDistance.getPairInteger().point1, pairPointWithDistance.getPairInteger().point2);
			newPairingAndNewNullSpaces.getNullSpaceOfMap1().remove(Integer.valueOf(pairPointWithDistance.getPairInteger().point1));
			newPairingAndNewNullSpaces.getNullSpaceOfMap2().remove(Integer.valueOf(pairPointWithDistance.getPairInteger().point2));
		}
		return newPairingAndNewNullSpaces;
	}



	private static boolean isPairhavingToPointsNotAlreadyInMatchingPropertiesList(PairInteger pair, List<PairPointWithDistance> listPair){

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



	private static void cleanOfDuplicatePoints(List<PairPointWithDistance> listPairsWithShortDistanceCleanOfSeedPairs) {
		List<Integer> pointAlreadyFoundInShape1 = new ArrayList<>();
		List<Integer> pointAlreadyFoundInShape2 = new ArrayList<>();

		Iterator<PairPointWithDistance> itr = listPairsWithShortDistanceCleanOfSeedPairs.iterator();
		while(itr.hasNext()) {
			PairPointWithDistance pairPointWithDistance = itr.next();
			Integer point1 = pairPointWithDistance.getPairInteger().point1;
			Integer point2 = pairPointWithDistance.getPairInteger().point2;
			if ( pointAlreadyFoundInShape1.contains(point1) || pointAlreadyFoundInShape2.contains(point2)){
				itr.remove();
			}else{
				pointAlreadyFoundInShape1.add(point1);
				pointAlreadyFoundInShape2.add(point2);
			}
		}
	}



	private static void cleanListPairsFromPointsInvolvedInSeedPairings(PairingAndNullSpaces pairing, List<PairPointWithDistance> listPairsWithShortDistance) {

		Iterator<PairPointWithDistance> itr = listPairsWithShortDistance.iterator();
		A: while(itr.hasNext()) {
			PairPointWithDistance pairInteger = itr.next();

			for (Entry<Integer, Integer> entry: pairing.getPairing().entrySet()){
				Integer pointFromShape1 = entry.getKey();
				Integer pointFromShape2 = entry.getValue();

				if (pairInteger.getPairInteger().point1 == pointFromShape1 || pairInteger.getPairInteger().point2 == pointFromShape2){
					itr.remove();
					continue A;
				}
			}
		}
	}
}
