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

	private char[] fourLetterCode;
	private char[] chainId;
	private int startingRankId;
	private int peptideLength;




	//-------------------------------------------------------------
	// Constructor
	//-------------------------------------------------------------
	public ShapeBuilderConstructorSegmentOfChain(MyStructureIfc myStructureGlobalBrut, char[] fourLetterCode, char[] chainId, int startingRankId, int peptideLength, AlgoParameters algoParameters, EnumMyReaderBiojava enumMyReaderBiojava){
		super(algoParameters, enumMyReaderBiojava);

		this.myStructureGlobalBrut = myStructureGlobalBrut;
		this.fourLetterCode = fourLetterCode;
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
		sb.append(String.valueOf(fourLetterCode) + " ");
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
		return fourLetterCode;
	}

	public char[] getChainId() {
		return chainId;
	}

	public int getStartingRankId() {
		return startingRankId;
	}
}
