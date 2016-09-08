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
	private char[] fourLetterCode;
	private char[] hetatmLigandThreeLetterCode;
	private int occurenceId;



	//-------------------------------------------------------------
	// Constructor
	//-------------------------------------------------------------
	public ShapeBuilderConstructorHetAtm(char[] fourLetterCode, char[] hetatmLigandThreeLetterCode, int occurenceId, AlgoParameters algoParameters, EnumMyReaderBiojava enumMyReaderBiojava) {
		super(algoParameters, enumMyReaderBiojava);
		this.fourLetterCode = fourLetterCode;
		this.hetatmLigandThreeLetterCode = hetatmLigandThreeLetterCode;
		this.occurenceId = occurenceId;
	}




	//-------------------------------------------------------------
	// Interface & Public methods
	//-------------------------------------------------------------
	@Override
	public String toString(){
		
		StringBuffer sb = new StringBuffer();
		sb.append(String.valueOf(fourLetterCode) + " ");
		sb.append(String.valueOf(hetatmLigandThreeLetterCode) + " ");
		sb.append(occurenceId);
		return sb.toString();
	}



	@Override
	public ShapeContainerIfc getShapeContainer() throws ShapeBuildingException {
		
		MyStructureIfc myStructureGlobalBrut = getMyStructure(fourLetterCode);
		
		ShapeBuilder shapeBuilder = new ShapeBuilder(myStructureGlobalBrut, algoParameters);
		ShapeContainerWithLigand shapeContainerWithLigand = shapeBuilder.getShapeAroundAHetAtomLigand(hetatmLigandThreeLetterCode, occurenceId);
		return shapeContainerWithLigand;
	}
}
