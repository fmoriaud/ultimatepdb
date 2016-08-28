package shapeBuilder;

import parameters.AlgoParameters;
import shape.ShapeContainerIfc;
import shape.ShapeContainerWithPeptide;
import structure.EnumMyReaderBiojava;
import structure.MyStructureIfc;

public class ShapeBuilderConstructorWholeChain extends ShapeBuilderConstructorAbstract implements ShapeBuilderConstructorIfc{
	//-------------------------------------------------------------
	// Class variables
	//-------------------------------------------------------------
	private char[] chainId; // and so on that is what is needed to indicate what to build
	private char[] fourLetterCode;
	private MyStructureIfc myStructure;



	//-------------------------------------------------------------
	// Constructor
	//-------------------------------------------------------------
	public ShapeBuilderConstructorWholeChain(char[] fourLetterCode, char[] chainId, AlgoParameters algoParameters, EnumMyReaderBiojava enumMyReaderBiojava){

		super(algoParameters, enumMyReaderBiojava);
		this.chainId = chainId;
		this.fourLetterCode = fourLetterCode;
		this.myStructure = null;
	}



	public ShapeBuilderConstructorWholeChain(MyStructureIfc myStructure, char[] chainId, AlgoParameters algoParameters, EnumMyReaderBiojava enumMyReaderBiojava){

		super(algoParameters, enumMyReaderBiojava);
		this.chainId = chainId;
		this.fourLetterCode = null;
		this.myStructure = myStructure;
	}



	//-------------------------------------------------------------
	// Interface & Public methods
	//-------------------------------------------------------------
	@Override
	public String toString(){

		StringBuffer sb = new StringBuffer();
		sb.append(String.valueOf(fourLetterCode) + " ");
		sb.append(String.valueOf(chainId) + " ");
		return sb.toString();
	}



	@Override
	public ShapeContainerIfc getShapeContainer() throws ShapeBuildingException {

		MyStructureIfc myStructureGlobalBrut = null;
		if (myStructure == null){
			myStructureGlobalBrut = getMyStructure(fourLetterCode);
		}else{
			myStructureGlobalBrut = myStructure;
		}

		ShapeBuilder shapeBuilder = new ShapeBuilder(myStructureGlobalBrut, algoParameters);
		ShapeContainerWithPeptide shapeContainerPeptide = shapeBuilder.getShapeAroundAChain(chainId);
		return shapeContainerPeptide;
	}
}
