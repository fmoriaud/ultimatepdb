package shapeBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import parameters.AlgoParameters;
import mystructure.MyChain;
import mystructure.MyChainIfc;
import mystructure.MyMonomerIfc;
import mystructure.MyStructureIfc;
import mystructure.MyStructureTools;

public class StructureLocalToBuildShapeSegmentOfShape implements StructureLocalToBuildShapeIfc{
	//-------------------------------------------------------------
	// Class variables
	//-------------------------------------------------------------
	private MyStructureIfc myStructureGlobalBrut;
	private AlgoParameters algoParameters;

	private char[] chainId;
	private int startingRankId;
	private int peptideLength;

	private MyChainIfc ligand;
	private MyStructureIfc myStructureLocal;




	//-------------------------------------------------------------
	// Constructor
	//-------------------------------------------------------------
	public StructureLocalToBuildShapeSegmentOfShape(MyStructureIfc myStructureGlobalBrut,
			char[] chainId, int startingRankId, int peptideLength, AlgoParameters algoParameters){

		this.myStructureGlobalBrut = myStructureGlobalBrut;
		this.algoParameters = algoParameters;
		this.chainId = chainId;
		this.startingRankId = startingRankId;
		this.peptideLength = peptideLength;
	}




	//-------------------------------------------------------------
	// Interface & Public methods
	//-------------------------------------------------------------
	public void compute() throws ShapeBuildingException{

		MyChainIfc wholeChain = myStructureGlobalBrut.getAminoMyChain(chainId);
		if (startingRankId >= wholeChain.getMyMonomers().length){
			System.out.println(String.valueOf(myStructureGlobalBrut.getFourLetterCode()) + "  " + String.valueOf(wholeChain.getChainId()) + " " + startingRankId);
			ShapeBuildingException exception = new ShapeBuildingException("bug ask for startingRankId > chain length");
			throw exception;
		}
		ligand = makeChainSegmentOutOfAChainUsingBondingInformation(wholeChain, startingRankId, peptideLength);
		if (ligand.getMyMonomers().length != peptideLength){
			ShapeBuildingException exception = new ShapeBuildingException("makeChainSegmentOutOfAChainUsingBondingInformation failed to return a peptide of the right length. Could be due to PDB parsing missing residues");
			throw exception;
		}

		List<MyMonomerIfc> tipMyMonomersToRemove = findTipsSegmentOfChain(wholeChain, ligand, startingRankId, peptideLength, algoParameters.getCOUNT_OF_RESIDUES_IGNORED_IN_SHAPE_BUILDING_BEFORE_AND_AFTER_PEPTIDE());
		myStructureLocal = StructureLocalTools.makeStructureLocalAroundAndExcludingMyMonomersFromInputMyChainAndTips(myStructureGlobalBrut, ligand, tipMyMonomersToRemove); // to skip some monomers at tip is not implemented

	}




	//-------------------------------------------------------------
	// Implementation
	//-------------------------------------------------------------
	private MyChainIfc makeChainSegmentOutOfAChainUsingBondingInformation(MyChainIfc inputChain, int rankIdinChain, int peptideLength){

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

		// from list of monomer I want a clean MyStructure
		MyStructureTools.removeBondsToNonExistingAtoms(myChain);

		return myChain;
	}



	private List<MyMonomerIfc> findTipsSegmentOfChain(MyChainIfc wholeChain, MyChainIfc ligand, int startingRankId, int peptideLength, int tipMonoMerDistance){

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



	private void addMonomerBound(Set<MyMonomerIfc> inputMonomers){

		Set<MyMonomerIfc> tempASetMyMonomerIfc = new HashSet<>();

		for (MyMonomerIfc inputMonomer: inputMonomers){
			MyMonomerIfc[] neighbors = inputMonomer.getNeighboringMyMonomerByBond();
			for (MyMonomerIfc neighbor: neighbors){
				tempASetMyMonomerIfc.add(neighbor);
			}
		}
		inputMonomers.addAll(tempASetMyMonomerIfc);
	}



	private void addMonomersBoundIfHigherId(Set<MyMonomerIfc> inputMonomers, int startingMonomerId){

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




	//-------------------------------------------------------------
	// Getters & Setters
	//-------------------------------------------------------------
	public MyChainIfc getLigand() {
		return ligand;
	}



	@Override
	public MyStructureIfc getMyStructureLocal() {
		return myStructureLocal;
	}
}
