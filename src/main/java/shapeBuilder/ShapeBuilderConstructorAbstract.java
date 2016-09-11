package shapeBuilder;

import parameters.AlgoParameters;
import mystructure.MyStructureIfc;

public class ShapeBuilderConstructorAbstract {
	//-------------------------------------------------------------
	// Class variables
	//-------------------------------------------------------------
	protected AlgoParameters algoParameters;




	//-------------------------------------------------------------
	// Constructor
	//-------------------------------------------------------------
	public ShapeBuilderConstructorAbstract(AlgoParameters algoParameters){
		this.algoParameters = algoParameters;
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
