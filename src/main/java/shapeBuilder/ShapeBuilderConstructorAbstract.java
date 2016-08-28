package shapeBuilder;

import io.IOTools;
import parameters.AlgoParameters;
import structure.EnumMyReaderBiojava;
import structure.ExceptionInMyStructurePackage;
import structure.MyStructureIfc;
import structure.ReadingStructurefileException;
import structure.StructureReaderMode;

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
		try {
			myStructureGlobalBrut = IOTools.getMyStructures(fourLetterCode, algoParameters, enumMyReaderBiojava, StructureReaderMode.ReadyForShapeComputation);
		} catch (ReadingStructurefileException | ExceptionInMyStructurePackage e1) {
			ShapeBuildingException shapeBuildingException = new ShapeBuildingException(e1.getMessage());
			throw shapeBuildingException;
		}
		System.out.println("Structure read successfully : " + String.valueOf(myStructureGlobalBrut.getFourLetterCode()));
		return myStructureGlobalBrut;
	}
}
