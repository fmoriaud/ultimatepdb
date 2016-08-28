package multithread;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;

import parameters.AlgoParameters;
import shape.ShapeContainerIfc;
import shapeCompare.PairingAndNullSpaces;
import shapeCompare.ResultsFromEvaluateCost;

public class ExtendPairingToMaxPairingInMiniShapeWhileKeepingMatchingOfStrikingPropertiesIfPossibleOtherwiseClosestMatchOrLeftUnpairedIfTooFarCallable implements Callable<List<PairingAndNullSpaces>>{
	//------------------------
	// Class variables
	//------------------------
	private final AlgoParameters algoParameters;
	private final List<ResultsFromEvaluateCost> listResults;
	private final ShapeContainerIfc shapeContainerQuery;
	private final ShapeContainerIfc shapeContainerAnyShape;


	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public ExtendPairingToMaxPairingInMiniShapeWhileKeepingMatchingOfStrikingPropertiesIfPossibleOtherwiseClosestMatchOrLeftUnpairedIfTooFarCallable(AlgoParameters algoParameters, List<ResultsFromEvaluateCost> listResults, ShapeContainerIfc shapeContainerQuery, ShapeContainerIfc shapeContainerAnyShape){
		this.algoParameters = algoParameters;
		this.listResults = listResults;
		this.shapeContainerQuery = shapeContainerQuery;
		this.shapeContainerAnyShape = shapeContainerAnyShape;
	}




	// -------------------------------------------------------------------
	// Public && Interface method
	// -------------------------------------------------------------------
	@Override
	public List<PairingAndNullSpaces> call() throws Exception {

		int countOfSubpacket = algoParameters.getSUB_THREAD_COUNT_FORK_AND_JOIN();
		int threshold = listResults.size() / countOfSubpacket + 1;
		if (threshold < 2){
			threshold = 2;
		}
		ForkJoinPool pool = new ForkJoinPool();
		ExtendPairingToMaxPairingInMiniShapeWhileKeepingMatchingOfStrikingPropertiesIfPossibleOtherwiseClosestMatchOrLeftUnpairedIfTooFarMultiThread computeExtendedPairings = new ExtendPairingToMaxPairingInMiniShapeWhileKeepingMatchingOfStrikingPropertiesIfPossibleOtherwiseClosestMatchOrLeftUnpairedIfTooFarMultiThread(listResults, 0, listResults.size() - 1, threshold, shapeContainerQuery.getShape(), shapeContainerAnyShape.getShape(), algoParameters);
		List<PairingAndNullSpaces> listExtendedPair = pool.invoke(computeExtendedPairings);
		pool.shutdownNow();
		return listExtendedPair;
	}

}
