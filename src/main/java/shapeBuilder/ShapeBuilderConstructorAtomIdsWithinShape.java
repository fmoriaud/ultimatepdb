package shapeBuilder;

import java.util.List;

import parameters.AlgoParameters;
import parameters.QueryAtomDefinedByIds;
import shape.ShapeContainerAtomIdsWithinShapeWithPeptide;
import shape.ShapeContainerIfc;
import mystructure.EnumMyReaderBiojava;
import mystructure.MyStructureIfc;

public class ShapeBuilderConstructorAtomIdsWithinShape extends ShapeBuilderConstructorAbstract implements ShapeBuilderConstructorIfc{
	//-------------------------------------------------------------
	// Class variables
	//-------------------------------------------------------------
	private char[] fourLetterCode;
	private List<QueryAtomDefinedByIds> listAtomDefinedByIds;
	private List<String> chainToIgnore;
	private MyStructureIfc myStructureGlobalBrut;




	//-------------------------------------------------------------
	// Constructor
	//-------------------------------------------------------------
	public ShapeBuilderConstructorAtomIdsWithinShape(MyStructureIfc myStructureGlobalBrut, List<QueryAtomDefinedByIds> listAtomDefinedByIds,
			List<String> chainToIgnore, AlgoParameters algoParameters){
		super(algoParameters);
		this.myStructureGlobalBrut = myStructureGlobalBrut;
		this.fourLetterCode = myStructureGlobalBrut.getFourLetterCode();
		this.listAtomDefinedByIds = listAtomDefinedByIds;
		this.chainToIgnore = chainToIgnore;
	}


	//-------------------------------------------------------------
	// Interface & Public methods
	//-------------------------------------------------------------
	@Override
	public String toString(){

		StringBuffer sb = new StringBuffer();
		sb.append(String.valueOf(fourLetterCode) + " ");
		for (QueryAtomDefinedByIds atomDefinedByIds: listAtomDefinedByIds){
			sb.append(atomDefinedByIds.toString());
		}
		return "";
	}



	@Override
	public ShapeContainerIfc getShapeContainer() throws ShapeBuildingException {

		if (myStructureGlobalBrut == null){
			myStructureGlobalBrut = getMyStructure(fourLetterCode);
		}

		ShapeBuilder shapeBuilder = new ShapeBuilder(myStructureGlobalBrut, algoParameters);
		ShapeContainerAtomIdsWithinShapeWithPeptide shapeContainerAtomIdsWithinShape = shapeBuilder.getShapeAroundAtomDefinedByIds(listAtomDefinedByIds, chainToIgnore);
		return shapeContainerAtomIdsWithinShape;
	}
}
