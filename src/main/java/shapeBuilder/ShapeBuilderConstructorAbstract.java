package shapeBuilder;

import parameters.AlgoParameters;
import mystructure.EnumMyReaderBiojava;
import mystructure.MyStructureIfc;

public class ShapeBuilderConstructorAbstract {
	//-------------------------------------------------------------
	// Class variables
	//-------------------------------------------------------------
	protected AlgoParameters algoParameters;
	protected EnumMyReaderBiojava enumMyReaderBiojava;




	//-------------------------------------------------------------
	// Constructor
	//-------------------------------------------------------------
	public ShapeBuilderConstructorAbstract(AlgoParameters algoParameters, EnumMyReaderBiojava enumMyReaderBiojava){
		this.algoParameters = algoParameters;
		this.enumMyReaderBiojava = enumMyReaderBiojava;
	}




	//-------------------------------------------------------------
	// Interface & Public methods
	//-------------------------------------------------------------
	protected MyStructureIfc getMyStructure(char[] fourLetterCode) throws ShapeBuildingException {
		MyStructureIfc myStructureGlobalBrut = null;

			myStructureGlobalBrut = null; // IOTools.getMyStructures(fourLetterCode, algoParameters, enumMyReaderBiojava, StructureReaderMode.ReadyForShapeComputation);

		System.out.println("Structure read successfully : " + String.valueOf(myStructureGlobalBrut.getFourLetterCode()));
		return myStructureGlobalBrut;
	}
}
