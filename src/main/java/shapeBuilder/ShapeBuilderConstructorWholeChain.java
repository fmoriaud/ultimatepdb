package shapeBuilder;

import parameters.AlgoParameters;
import shape.ShapeContainerIfc;
import shape.ShapeContainerWithPeptide;
import mystructure.EnumMyReaderBiojava;
import mystructure.MyStructureIfc;

public class ShapeBuilderConstructorWholeChain extends ShapeBuilderConstructorAbstract implements ShapeBuilderConstructorIfc{
	//-------------------------------------------------------------
	// Class variables
	//-------------------------------------------------------------
	private char[] chainId; // and so on that is what is needed to indicate what to build
	private MyStructureIfc myStructure;



	//-------------------------------------------------------------
	// Constructor
	//-------------------------------------------------------------
	public ShapeBuilderConstructorWholeChain(MyStructureIfc myStructure, char[] chainId, AlgoParameters algoParameters){

		super(algoParameters);
		this.chainId = chainId;
		this.myStructure = myStructure;
	}



	//-------------------------------------------------------------
	// Interface & Public methods
	//-------------------------------------------------------------
	@Override
	public String toString(){

		StringBuffer sb = new StringBuffer();
		sb.append(String.valueOf(myStructure.getFourLetterCode()) + " ");
		sb.append(String.valueOf(chainId) + " ");
		return sb.toString();
	}



	@Override
	public ShapeContainerIfc getShapeContainer() throws ShapeBuildingException {

		ShapeBuilder shapeBuilder = new ShapeBuilder(myStructure, algoParameters);
		ShapeContainerWithPeptide shapeContainerPeptide = shapeBuilder.getShapeAroundAChain(chainId);
		return shapeContainerPeptide;
	}
}
