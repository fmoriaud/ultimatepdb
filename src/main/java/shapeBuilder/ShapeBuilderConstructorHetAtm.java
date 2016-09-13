package shapeBuilder;

import parameters.AlgoParameters;
import shape.ShapeContainerIfc;
import shape.ShapeContainerWithLigand;
import mystructure.EnumMyReaderBiojava;
import mystructure.MyStructureIfc;

public class ShapeBuilderConstructorHetAtm extends ShapeBuilderConstructorAbstract implements ShapeBuilderConstructorIfc{
	//-------------------------------------------------------------
	// Class variables
	//-------------------------------------------------------------
	private MyStructureIfc myStructure;
	private char[] hetatmLigandThreeLetterCode;
	private int occurenceId;



	//-------------------------------------------------------------
	// Constructor
	//-------------------------------------------------------------
	public ShapeBuilderConstructorHetAtm(MyStructureIfc myStructure, char[] hetatmLigandThreeLetterCode, int occurenceId, AlgoParameters algoParameters) {
		super(algoParameters);
		this.myStructure = myStructure;
		this.hetatmLigandThreeLetterCode = hetatmLigandThreeLetterCode;
		this.occurenceId = occurenceId;
	}




	//-------------------------------------------------------------
	// Interface & Public methods
	//-------------------------------------------------------------
	@Override
	public String toString(){
		
		StringBuffer sb = new StringBuffer();
		sb.append(String.valueOf(myStructure.getFourLetterCode()) + " ");
		sb.append(String.valueOf(hetatmLigandThreeLetterCode) + " ");
		sb.append(occurenceId);
		return sb.toString();
	}



	@Override
	public ShapeContainerIfc getShapeContainer() throws ShapeBuildingException {

		ShapeBuilder shapeBuilder = new ShapeBuilder(myStructure, algoParameters);
		ShapeContainerWithLigand shapeContainerWithLigand = shapeBuilder.getShapeAroundAHetAtomLigand(hetatmLigandThreeLetterCode, occurenceId);
		return shapeContainerWithLigand;
	}
}
