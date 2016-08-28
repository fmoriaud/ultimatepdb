package multithread;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;

import parameters.AlgoParameters;
import shape.ShapeContainerIfc;
import shapeCompare.PairingAndNullSpaces;
import shapeReduction.TriangleInteger;


public class FindMatchingTriangleWithOrderedMatchingPointsCallable implements Callable<List<PairingAndNullSpaces>>{
	//------------------------
	// Class variables
	//------------------------
	private final AlgoParameters algoParameters;
	private final List<TriangleInteger> listTriangleShape1;
	private final List<TriangleInteger> listTriangleShape2;
	private final ShapeContainerIfc shapeContainerQuery;
	private final ShapeContainerIfc shapeContainerAnyShape;




	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public FindMatchingTriangleWithOrderedMatchingPointsCallable(AlgoParameters algoParameters, List<TriangleInteger> listTriangleShape1, List<TriangleInteger> listTriangleShape2, ShapeContainerIfc shapeContainerQuery, ShapeContainerIfc shapeContainerAnyShape){
		this.algoParameters = algoParameters;
		this.shapeContainerQuery = shapeContainerQuery;
		this.shapeContainerAnyShape = shapeContainerAnyShape;
		this.listTriangleShape1 = listTriangleShape1;
		this.listTriangleShape2 = listTriangleShape2;
	}



	@Override
	public List<PairingAndNullSpaces> call() throws Exception {

		int countOfSubpacket = algoParameters.getSUB_THREAD_COUNT_FORK_AND_JOIN();
		int threshold = listTriangleShape1.size() / countOfSubpacket + 1;
		if (threshold < 2){
			threshold = 2;
		}
		ForkJoinPool pool = new ForkJoinPool();
		FindMatchingTriangleWithOrderedMatchingPointsMultithread computeTriangleSeedExtentions = new FindMatchingTriangleWithOrderedMatchingPointsMultithread(0, listTriangleShape1.size() - 1, threshold, listTriangleShape1, listTriangleShape2, shapeContainerQuery, shapeContainerAnyShape, algoParameters);
		List<PairingAndNullSpaces> listPairingTriangleSeed = pool.invoke(computeTriangleSeedExtentions);
		pool.shutdownNow();

		return listPairingTriangleSeed;
	}

}
