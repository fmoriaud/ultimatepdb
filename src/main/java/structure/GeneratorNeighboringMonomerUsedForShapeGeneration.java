package structure;
import java.util.ArrayList;
import java.util.List;

import math.ToolsMathAppliedToObjects;

public class GeneratorNeighboringMonomerUsedForShapeGeneration {
	//------------------------
	// Class variables
	//------------------------
	private MyStructureIfc structure ;
	private double minDistanceToBeNeighbors;

	private List<MyMonomerIfc> tempListMyMonomer = new ArrayList<>();
	private List<MyChainIfc> tempListMyChain = new ArrayList<>();




	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	/**
	 * Generator of the neighbors based on distance between representative atom
	 * @param structure : MyStructure where the neighbors are from
	 * @param minDistanceToBeNeighbors : threshold of distance to be a neighbor
	 */
	public GeneratorNeighboringMonomerUsedForShapeGeneration(MyStructureIfc structure, double minDistanceToBeNeighbors) {
		this.minDistanceToBeNeighbors = minDistanceToBeNeighbors;
		this.structure = structure ;
	}




	// -------------------------------------------------------------------
	// Public & Interface Methods
	// -------------------------------------------------------------------
	/**
	 * Return neighbors of a given MyMonomer in the MyStructure defined in Constructor
	 * @param startingMonomer : the monomer for which neighbors are computed can be in the MyStructure or not.
	 * @return neighbors are organized in MyChains, keeping the same MyChain as in the MyStructure in constructor
	 */
	public MyChainIfc[] computeAminoNeighborsOfAGivenResidue (MyMonomerIfc startingMonomer ) {

		tempListMyChain.clear();

		for (MyChainIfc myChain: structure.getAllChainsRelevantForShapeBuilding()){ // here we loop on partners then only authorized for shape
			treatchains(startingMonomer, myChain);
		}

		MyChainIfc[] neighborsOfThisMonomer = tempListMyChain.toArray(new MyChainIfc[tempListMyChain.size()]);
		return neighborsOfThisMonomer;
		//}
		//return null;
	}



	private void treatchains(MyMonomerIfc startingMonomer, MyChainIfc myChain) {

		if (startingMonomer.getMyAtoms().length == 0){
			return;
		}
		double distance;
		tempListMyMonomer.clear();
		for (MyMonomerIfc myMonomer: myChain.getMyMonomers()){

			if (myMonomer.getMyAtoms().length == 0){
				continue;
			}
			
			distance = ToolsMathAppliedToObjects.computeDistanceBetweenTwoResidues(startingMonomer, myMonomer );

			if ((distance < minDistanceToBeNeighbors - 0.001) && (distance > 0.001 )){
				//System.out.println("distance = " + distance);
				tempListMyMonomer.add(myMonomer);
			}
		}
		if (tempListMyMonomer.size() != 0){
			MyMonomerIfc[] monomers = tempListMyMonomer.toArray(new MyMonomerIfc[tempListMyMonomer.size()]);
			MyChainIfc chainWithNeighbors = new MyChain(monomers, myChain.getChainId());
			tempListMyChain.add(chainWithNeighbors);
		}
	}
}
