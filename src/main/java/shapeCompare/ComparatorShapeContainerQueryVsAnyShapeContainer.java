package shapeCompare;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import fingerprint.CannotCompareDistributionException;
import fingerprint.DistributionComparisonTools;
import hits.Hit;
import math.ToolsMath;
import multithread.ExtendPairingToMaxPairingInMiniShapeWhileKeepingMatchingOfStrikingPropertiesIfPossibleOtherwiseClosestMatchOrLeftUnpairedIfTooFarCallable;
import multithread.FindMatchingTriangleWithOrderedMatchingPointsCallable;
import parameters.AlgoParameters;
import pointWithProperties.PointWithPropertiesIfc;
import scorePairing.ScorePairing;
import shape.ShapeContainerIfc;
import shapeReduction.TriangleInteger;

public class ComparatorShapeContainerQueryVsAnyShapeContainer {
	//------------------------
	// Constant
	//------------------------
	public boolean debug = false;
	private List<Hit> emptyList = new ArrayList<>();



	//------------------------
	// Class variables
	//------------------------
	ShapeContainerIfc shapeContainerQuery;
	ShapeContainerIfc shapeContainerAnyShape;
	AlgoParameters algoParameters;




	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public ComparatorShapeContainerQueryVsAnyShapeContainer(ShapeContainerIfc shapeContainerQuery, ShapeContainerIfc shapeContainerAnyShape, AlgoParameters algoParameters){

		this.shapeContainerQuery = shapeContainerQuery;
		this.shapeContainerAnyShape = shapeContainerAnyShape;
		this.algoParameters = algoParameters;
	}




	// -------------------------------------------------------------------
	// Public && Interface method
	// -------------------------------------------------------------------
	public List<Hit> computeResults() throws NullResultFromAComparisonException {

		try {
			float tanimotoHistogramProperties = DistributionComparisonTools.computeSubDistributionTanimoto(shapeContainerQuery.getHistogramStrikingProperties(), shapeContainerAnyShape.getHistogramStrikingProperties());
			float tanimotoHistogramProperties2 = DistributionComparisonTools.computeSubDistributionTanimoto(shapeContainerQuery.getHistogramD2(), shapeContainerAnyShape.getHistogramD2());
			//float distance = DistributionComparisonTools.computeDistance(shapeContainerQuery.getHistogramStrikingProperties(), shapeContainerAnyShape.getHistogramStrikingProperties());
			//float distance2 = DistributionComparisonTools.computeDistance(shapeContainerQuery.getHistogramD2(), shapeContainerAnyShape.getHistogramD2());

			System.out.println("getHistogramStrikingProperties getHistogramD2");
			System.out.println("fingerprint = " + tanimotoHistogramProperties + "  " + tanimotoHistogramProperties2);
			//System.out.println("distance = " + distance + "  " + distance2);
			if (tanimotoHistogramProperties < 0.4 || tanimotoHistogramProperties2 < 0.6){
				//List<Hit> emptyHitList = new ArrayList<>();
				System.out.println("comparison skipped because of Fingerprint ");
				//return emptyList;
			}

		} catch (CannotCompareDistributionException e2) {
			e2.printStackTrace();

		}

		List<TriangleInteger> listTriangleShape1 = shapeContainerQuery.getListTriangleOfPointsFromMinishape();
		List<TriangleInteger> listTriangleShape2 = shapeContainerAnyShape.getListTriangleOfPointsFromMinishape();
		System.out.println("compairing " + listTriangleShape1.size() + " triangles from query to " + listTriangleShape2.size() + " from potential hit" );
		System.out.println(shapeContainerAnyShape.getMiniShape().size() + " objects");
		ExecutorService poolExecutor = Executors.newFixedThreadPool(1);
		Callable<List<PairingAndNullSpaces>> callable = new FindMatchingTriangleWithOrderedMatchingPointsCallable(algoParameters, listTriangleShape1, listTriangleShape2, shapeContainerQuery, shapeContainerAnyShape);
		Future<List<PairingAndNullSpaces>> listPairingTriangleSeedCallable = poolExecutor.submit(callable);
		List<PairingAndNullSpaces> listPairingTriangleSeed = null;

		try {
			listPairingTriangleSeed = listPairingTriangleSeedCallable.get();
		} catch (InterruptedException | ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		System.out.println("scoring " + listPairingTriangleSeed.size() + " pairs of triangles");
		poolExecutor.shutdownNow();

		ScorePairing scorePairingBasedOnMinishape = new ScorePairing(shapeContainerQuery.getMiniShape(), shapeContainerAnyShape.getMiniShape(), algoParameters);
		List<ResultsFromEvaluateCost> resultsPairingTriangleSeed = null;
		resultsPairingTriangleSeed = scorePairingBasedOnMinishape.getCostOfaListOfPairing(listPairingTriangleSeed);
		Collections.sort(resultsPairingTriangleSeed, new LowestCostPairingComparator());

		if (resultsPairingTriangleSeed.size() == 0){
			return emptyList;
		}

//		List<ResultsFromEvaluateCost> diverseList = new ArrayList<>();
//		diverseList.add(resultsPairingTriangleSeed.get(0));

		selectResultsByDiversityOfTransAndRotUsingHashSet(resultsPairingTriangleSeed);
		
		poolExecutor = Executors.newFixedThreadPool(1); // 1 is enough as the callable afterward is a for and join splitting the task. TODO Simplify this code
		callable = new ExtendPairingToMaxPairingInMiniShapeWhileKeepingMatchingOfStrikingPropertiesIfPossibleOtherwiseClosestMatchOrLeftUnpairedIfTooFarCallable(algoParameters, resultsPairingTriangleSeed, shapeContainerQuery, shapeContainerAnyShape);
		Future<List<PairingAndNullSpaces>> listExtendedPair = poolExecutor.submit(callable);

		ScorePairing scorePairingBasedOnShape = new ScorePairing(shapeContainerQuery.getShape(), shapeContainerAnyShape.getShape(), algoParameters);
		List<ResultsFromEvaluateCost> resultsExtendedPairing = null;
		try {
			List<PairingAndNullSpaces> currentExtendedPairing = listExtendedPair.get();
			resultsExtendedPairing = scorePairingBasedOnShape.getCostOfaListOfPairing(currentExtendedPairing);
		} catch (InterruptedException | ExecutionException e) {
			String message = "Extended pairing scoring failed ";
			NullResultFromAComparisonException exception = new NullResultFromAComparisonException(message);
			throw exception;
		}
		poolExecutor.shutdownNow();

		// Remove hit where hit is not enough matching query based on covergage
		//int before = resultsExtendedPairing.size();
		Iterator<ResultsFromEvaluateCost> it = resultsExtendedPairing.iterator();
		while (it.hasNext()){

			ResultsFromEvaluateCost nextResult = it.next();
			float fractionNeededOnHit = algoParameters.getFRACTION_NEEDED_ON_HIT();
			float coverage = nextResult.getCoverage();

			boolean isDistanceToOutsideOk = checkDistanceToOutside(nextResult, shapeContainerQuery, shapeContainerAnyShape);

			if (coverage < fractionNeededOnHit || isDistanceToOutsideOk == false){
				it.remove();
				continue;
			}
		}

		List<Hit> hitsExtendedPairing = PairingTools.generateHitsListFromResultList(resultsExtendedPairing, shapeContainerAnyShape);
		Collections.sort(hitsExtendedPairing, new PairingTools.LowestCostHitComparator());

//		System.out.println("hits : ");
//		for (Hit hit: hitsExtendedPairing){
//			System.out.println(hit.getResultsFromEvaluateCost().getCoverage());
//		}
		return hitsExtendedPairing;
	}



	/**
	 * This method filters a list of results based on the trans vector and rot matrix to align hit on query.  
	 * It is based on a HashSet so the equals and hashcode of ResultsFromEvaluateCost is critical on the filtering
	 * @param  resultsPairingTriangleSeed  a list of results to filter
	 */
	private void selectResultsByDiversityOfTransAndRotUsingHashSet(List<ResultsFromEvaluateCost> resultsPairingTriangleSeed) {
		
		System.out.println(resultsPairingTriangleSeed.size() + " trieangle hits scored for " + String.valueOf(shapeContainerAnyShape.getFourLetterCode()));

		Set<ResultsFromEvaluateCost> uniqueSet = new HashSet<>();
		System.out.println("size of triangle matches before = " + resultsPairingTriangleSeed.size());
		for (ResultsFromEvaluateCost result: resultsPairingTriangleSeed){
			boolean added = uniqueSet.add(result);
			//System.out.println(added + "  " + uniqueSet.size());
		}

		resultsPairingTriangleSeed.clear();
		resultsPairingTriangleSeed.addAll(uniqueSet);
		
		System.out.println("size of triangle matches after diversity filter = " + resultsPairingTriangleSeed.size());
	}



	private static boolean checkDistanceToOutside(ResultsFromEvaluateCost result, ShapeContainerIfc queryShape, ShapeContainerIfc hitShape){


		PairingAndNullSpaces currentNewPairingAndNewNullSpaces = result.getPairingAndNullSpaces();
		// This regression should detect very firmly if the hit ligand is on the same side as the query ligand
		//SimpleRegression regression = new SimpleRegression();
		int countCasesDifferentSign = 0;
		int countConsideredCases = 0;

		for( Map.Entry<Integer, Integer> entry: currentNewPairingAndNewNullSpaces.getPairing().entrySet() ) { 

			Integer idFromMap1a = entry.getKey();
			Integer idFromMap2a = entry.getValue();
			PointWithPropertiesIfc point1a = queryShape.get(idFromMap1a);
			PointWithPropertiesIfc point2a = hitShape.get(idFromMap2a);
			float distanceToOutsideOfPoint1a = point1a.getDistanceToLigand();
			float distanceToOutsideOfPoint2a = point2a.getDistanceToLigand();

			for( Map.Entry<Integer, Integer> entry2: currentNewPairingAndNewNullSpaces.getPairing().entrySet() ) { 

				if (entry.getKey().equals(entry2.getKey()) && entry.getValue().equals(entry2.getValue())){
					continue;
				}

				Integer idFromMap1b = entry2.getKey();
				Integer idFromMap2b = entry2.getValue();
				PointWithPropertiesIfc point1b = queryShape.get(idFromMap1b);
				PointWithPropertiesIfc point2b = hitShape.get(idFromMap2b);
				float distanceToOutsideOfPoint1b = point1b.getDistanceToLigand();
				float distanceToOutsideOfPoint2b = point2b.getDistanceToLigand();

				float deltaQuery = distanceToOutsideOfPoint1a - distanceToOutsideOfPoint1b;
				float deltaHit = distanceToOutsideOfPoint2a - distanceToOutsideOfPoint2b;

				float distBetweenQueryPoints = ToolsMath.computeDistance(point1a.getCoords().getCoords(), point1b.getCoords().getCoords());
				if (distBetweenQueryPoints < 3.0f){ // I consider that the dist to ligand difference cannot be reliable if shape points are too close
					continue;
				}

				countConsideredCases += 1;
				if (deltaQuery > 0){
					if (deltaHit < 0){
						countCasesDifferentSign += 1;
					}
				}else{
					if (deltaHit > 0){
						countCasesDifferentSign += 1;
					}
				}

				//System.out.println(deltaQuery + "  " + deltaHit );
				//regression.addData(deltaQuery, deltaHit);
			}
		}

		float percentageDifferentSign = (float) countCasesDifferentSign / countConsideredCases;
		//System.out.println("percentageDifferentSign = " + percentageDifferentSign + " countConsideredCases =  " + countConsideredCases);

		//System.out.println("percentageDifferentSign = " + percentageDifferentSign);
		if (percentageDifferentSign > 0.4){
			//System.out.println("hit deleted");
			return false;
		}
		return true;
	}

	
	
	private class LowestCostPairingComparator implements Comparator<ResultsFromEvaluateCost>{

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


//	private boolean isItTooCloseToOneAlreadyThere(List<ResultsFromEvaluateCost> listResults, ResultsFromEvaluateCost candidateResult){
//
//		for (ResultsFromEvaluateCost resultFromList: listResults){
//
//			double distanceR = computeDistanceOnRotMatrix(resultFromList, candidateResult);
//			double distanceT = computeDistanceOnTranslation(resultFromList, candidateResult);
//
//			//System.out.println("distance R = " + distanceR + "  distanceT = " + distanceT);
//			if (distanceT < 2.0 * algoParameters.getCELL_DIMENSION_OF_THE_PROBABILITY_MAP_ANGSTROM() && distanceR < 60.0 / 180.0){ // 2.8 / 6.0
//				return true;
//			}
//		}
//		return false;
//	}



	
}
