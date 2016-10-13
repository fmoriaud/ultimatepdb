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
		ligand = StructureLocalTools.makeChainSegmentOutOfAChainUsingBondingInformation(wholeChain, startingRankId, peptideLength, algoParameters);
		if (ligand.getMyMonomers().length != peptideLength){
			ShapeBuildingException exception = new ShapeBuildingException("makeChainSegmentOutOfAChainUsingBondingInformation failed to return a peptide of the right length. Could be due to PDB parsing missing residues");
			throw exception;
		}

		List<MyMonomerIfc> tipMyMonomersToRemove = StructureLocalTools.findTipsSegmentOfChain(wholeChain, ligand, startingRankId, peptideLength, algoParameters.getCOUNT_OF_RESIDUES_IGNORED_IN_SHAPE_BUILDING_BEFORE_AND_AFTER_PEPTIDE());
		myStructureLocal = StructureLocalTools.makeStructureLocalAroundAndExcludingMyMonomersFromInputMyChainAndTips(myStructureGlobalBrut, ligand, tipMyMonomersToRemove); // to skip some monomers at tip is not implemented

	}




	//-------------------------------------------------------------
	// Implementation
	//-------------------------------------------------------------





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
