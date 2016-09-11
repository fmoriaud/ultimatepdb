package shapeBuilder;

import parameters.AlgoParameters;
import shape.ShapeContainerIfc;
import shape.ShapeContainerWithPeptide;
import mystructure.EnumMyReaderBiojava;
import mystructure.MyStructureIfc;

public class ShapeBuilderConstructorSegmentOfChain extends ShapeBuilderConstructorAbstract implements ShapeBuilderConstructorIfc{
	//-------------------------------------------------------------
	// Class variables
	//-------------------------------------------------------------
	private MyStructureIfc myStructureGlobalBrut;

	private char[] chainId;
	private int startingRankId;
	private int peptideLength;




	//-------------------------------------------------------------
	// Constructor
	//-------------------------------------------------------------

	/**
	 * Builder for a shape defined by the environment of a segment of a chain of input myStructure
	 * @param myStructure
	 * @param chainId
	 * @param startingRankId is the rank id in chain (not the residue id). It starts from 0.
	 * @param peptideLength is the final length of the segment of chain
	 * @param algoParameters
	 */
	public ShapeBuilderConstructorSegmentOfChain(MyStructureIfc myStructure, char[] chainId, int startingRankId, int peptideLength, AlgoParameters algoParameters){
		super(algoParameters);

		this.myStructureGlobalBrut = myStructure;
		this.chainId = chainId;
		this.startingRankId = startingRankId;
		this.peptideLength = peptideLength;
	}




	//-------------------------------------------------------------
	// Interface & Public methods
	//-------------------------------------------------------------
	@Override
	public String toString(){

		StringBuffer sb = new StringBuffer();
		sb.append(String.valueOf(myStructureGlobalBrut.getFourLetterCode()) + " ");
		sb.append(String.valueOf(chainId) + " ");
		sb.append(startingRankId);
		return sb.toString();
	}



	@Override
	public ShapeContainerIfc getShapeContainer() throws ShapeBuildingException {

		ShapeBuilder shapeBuilder = new ShapeBuilder(myStructureGlobalBrut, algoParameters);
		ShapeContainerWithPeptide shapeContainerPeptide = shapeBuilder.getShapeAroundASegmentOfChainUsingStartingMyMonomerPositionInChain(chainId, startingRankId, peptideLength);
		return shapeContainerPeptide;
	}




	//-------------------------------------------------------------
	// Getters and Setters
	//-------------------------------------------------------------
	public char[] getFourLetterCode() {
		return myStructureGlobalBrut.getFourLetterCode();
	}

	public char[] getChainId() {
		return chainId;
	}

	public int getStartingRankId() {
		return startingRankId;
	}
}
