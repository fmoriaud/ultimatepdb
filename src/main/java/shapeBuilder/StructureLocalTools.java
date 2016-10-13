package shapeBuilder;

import java.util.*;

import mystructure.*;
import parameters.AlgoParameters;

public class StructureLocalTools {
	/**
	 * returns a MyChain which contains a segment of a chain
	 * It uses the Monomer at rankIdinChains, then search monomers among bonded monomers, keeps the ones with higher rankid
	 * And stops when the lenght is >= to peptideLength
	 * The length can be larger than expected if there is a branch in the structure, e.g. Monomer ID=3 bounded to Monomer ID=4 and Monomer ID=5
	 * @param inputChain
	 * @param rankIdinChain
	 * @param peptideLength
	 * @return new MyChain made of the monomers sorted by residueId. Bonds to non existing atoms are removed
	 * But monomers were not cloned so some bonds disappears in the inputChain
	 * TODO Do a proper cloning ?
	 */
	public static MyChainIfc makeChainSegmentOutOfAChainUsingBondingInformation(MyChainIfc inputChain, int rankIdinChain, int peptideLength, AlgoParameters algoParameters){

		Set<MyMonomerIfc> tempSetPeptide = new HashSet<>();

		MyMonomerIfc startingMonomer = inputChain.getMyMonomerByRank(rankIdinChain);
		tempSetPeptide.add(startingMonomer);

		int startingMonomerId = startingMonomer.getResidueID();
		for (int i=0 ; i<peptideLength-1; i++){

			addMonomersBoundIfHigherId(tempSetPeptide, startingMonomerId);

			if (tempSetPeptide.size() >= peptideLength){
				break;
			}
		}

		List<MyMonomerIfc> tempListAMyMonomerIfc = new ArrayList<>();
		tempListAMyMonomerIfc.addAll(tempSetPeptide);

		Collections.sort(tempListAMyMonomerIfc, new MyMonomerIfcComparatorIncreasingResidueId());
		MyMonomerIfc[] myMonomers = tempListAMyMonomerIfc.toArray(new MyMonomerIfc[tempListAMyMonomerIfc.size()]);
		MyChainIfc myChain = new MyChain(myMonomers, inputChain.getChainId());

		// Need to clone it
		Cloner cloner = new Cloner(myChain, algoParameters);
		MyStructureIfc clone = cloner.getClone();

		// from list of monomer I want a clean MyStructure
		MyStructureTools.removeBondsToNonExistingAtoms(myChain);

		return myChain;
	}



	private static void addMonomersBoundIfHigherId(Set<MyMonomerIfc> inputMonomers, int startingMonomerId){

		Set<MyMonomerIfc> tempASetMyMonomerIfc = new HashSet<>();

		for (MyMonomerIfc inputMonomer: inputMonomers){
			MyMonomerIfc[] neighbors = inputMonomer.getNeighboringMyMonomerByBond();
			for (MyMonomerIfc neighbor: neighbors){
				if (neighbor.getResidueID() > startingMonomerId){
					tempASetMyMonomerIfc.add(neighbor);
				}
			}
		}
		inputMonomers.addAll(tempASetMyMonomerIfc);
	}
	/**
	 * Returns the neighboring monomers of a segement of chain
	 * @param wholeChain
	 * @param ligand
	 * @param startingRankId
	 * @param peptideLength
	 * @param tipMonoMerDistance
	 * @return
	 */
	public static List<MyMonomerIfc> findTipsSegmentOfChain(MyChainIfc wholeChain, MyChainIfc ligand, int startingRankId, int peptideLength, int tipMonoMerDistance){

		List<MyMonomerIfc> tipsOfSegmentOfChain = new ArrayList<>();

		Set<MyMonomerIfc> tempSetPeptide = new HashSet<>();
		for (MyMonomerIfc myMonomer: ligand.getMyMonomers()){
			tempSetPeptide.add(myMonomer);
		}

		for (int i=0 ; i<tipMonoMerDistance-1; i++){ // I explore bonded to this distance using ligand as input
			addMonomerBound(tempSetPeptide);
		}

		for (MyMonomerIfc myMonomer: ligand.getMyMonomers()){ //I remove the peptide itself as I want only the tips
			tempSetPeptide.remove(myMonomer);
		}

		tipsOfSegmentOfChain.addAll(tempSetPeptide);
		return tipsOfSegmentOfChain;
	}



	private static void addMonomerBound(Set<MyMonomerIfc> inputMonomers){

		Set<MyMonomerIfc> tempASetMyMonomerIfc = new HashSet<>();

		for (MyMonomerIfc inputMonomer: inputMonomers){
			MyMonomerIfc[] neighbors = inputMonomer.getNeighboringMyMonomerByBond();
			for (MyMonomerIfc neighbor: neighbors){
				tempASetMyMonomerIfc.add(neighbor);
			}
		}
		inputMonomers.addAll(tempASetMyMonomerIfc);
	}

	public static MyStructureIfc makeStructureLocalAroundAndExcludingMyMonomersFromInputMyChainAndTips(MyStructureIfc myStructureGlobalBrut, MyChainIfc myChain, List<MyMonomerIfc> tipMyMonomersToRemove) {

		Set<MyMonomerIfc> queryMonomers = makeMyMonomersLocalAroundAndExcludingMyMonomersFromInputMyChain(myChain);
		queryMonomers.removeAll(tipMyMonomersToRemove);
		MyStructureIfc myStructureLocal;
		try {
			myStructureLocal = myStructureGlobalBrut.cloneWithSameObjectsWhileKeepingOnlyMyMonomerInThisSet(queryMonomers);
		} catch (ExceptionInMyStructurePackage e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		return myStructureLocal;
	}



	private static Set<MyMonomerIfc> makeMyMonomersLocalAroundAndExcludingMyMonomersFromInputMyChain(MyChainIfc myChain){

		Set<MyMonomerIfc> queryMyMonomer = makeMyMonomersLocalAroundAndWithChain(myChain);

		for (MyMonomerIfc monomerToRemove: myChain.getMyMonomers()){
			queryMyMonomer.remove(monomerToRemove);
		}
		return queryMyMonomer;
	}



	public static Set<MyMonomerIfc> makeMyMonomersLocalAroundAndWithChain(MyChainIfc myChain){

		Set<MyMonomerIfc> queryMyMonomer = new HashSet<>();
		for (MyMonomerIfc myMonomer: myChain.getMyMonomers()){
			queryMyMonomer.add(myMonomer);
		}

		for (MyMonomerIfc monomer: myChain.getMyMonomers()){
			MyChainIfc[] neighbors = monomer.getNeighboringAminoMyMonomerByRepresentativeAtomDistance();
			for (MyChainIfc mychain: neighbors){
				for (MyMonomerIfc neighbor: mychain.getMyMonomers()){
					queryMyMonomer.add(neighbor);
				}
			}
		}

		return queryMyMonomer;
	}
}
