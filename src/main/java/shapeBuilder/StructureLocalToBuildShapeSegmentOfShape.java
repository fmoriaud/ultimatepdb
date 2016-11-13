package shapeBuilder;

import parameters.AlgoParameters;
import mystructure.MyChainIfc;
import mystructure.MyStructureIfc;

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

		// ligand is cloned and neighbors bond and distance should be updated
		// Then Structure local can be built

		ligand = StructureLocalTools.makeChainSegment(wholeChain, startingRankId, peptideLength, algoParameters);
		MyChainIfc extractedSegment = StructureLocalTools.extractSubChain(wholeChain, startingRankId, peptideLength, algoParameters);

		if (ligand.getMyMonomers().length != peptideLength){
			ShapeBuildingException exception = new ShapeBuildingException("makeChainSegment failed to return a peptide of the right length. Could be due to PDB parsing missing residues");
			throw exception;
		}

		myStructureLocal = StructureLocalTools.makeStructureLocalForSegmentAroundAndExcludingMyMonomersFromInputMyChain(myStructureGlobalBrut, extractedSegment, algoParameters);

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
