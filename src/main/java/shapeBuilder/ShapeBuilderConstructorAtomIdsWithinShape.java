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
	private double radiusForQueryAtomsDefinedByIds;
	private List<String> chainToIgnore;
	private MyStructureIfc myStructureGlobalBrut;




	//-------------------------------------------------------------
	// Constructor
	//-------------------------------------------------------------
	public ShapeBuilderConstructorAtomIdsWithinShape(char[] fourLetterCode, List<QueryAtomDefinedByIds> listAtomDefinedByIds, double radiusForQueryAtomsDefinedByIds, 
			AlgoParameters algoParameters, List<String> chainToIgnore){
		super(algoParameters);
		this.fourLetterCode = fourLetterCode;
		this.listAtomDefinedByIds = listAtomDefinedByIds;
		this.radiusForQueryAtomsDefinedByIds = radiusForQueryAtomsDefinedByIds;
		this.chainToIgnore = chainToIgnore;
	}



	public ShapeBuilderConstructorAtomIdsWithinShape(MyStructureIfc myStructureGlobalBrut, List<QueryAtomDefinedByIds> listAtomDefinedByIds, double radiusForQueryAtomsDefinedByIds, 
			AlgoParameters algoParameters, List<String> chainToIgnore){
		super(algoParameters);
		this.myStructureGlobalBrut = myStructureGlobalBrut;
		this.fourLetterCode = myStructureGlobalBrut.getFourLetterCode();
		this.listAtomDefinedByIds = listAtomDefinedByIds;
		this.radiusForQueryAtomsDefinedByIds = radiusForQueryAtomsDefinedByIds;
		this.chainToIgnore = chainToIgnore;
	}


	//-------------------------------------------------------------
	// Interface & Public methods
	//-------------------------------------------------------------
	@Override
	public String toString(){

		StringBuffer sb = new StringBuffer();
		sb.append(String.valueOf(fourLetterCode) + " ");
		sb.append(radiusForQueryAtomsDefinedByIds + " Angstr�ms ");
		for (QueryAtomDefinedByIds atomDefinedByIds: listAtomDefinedByIds){
			sb.append(atomDefinedByIds.toString());
		}
		sb.append(" | " + radiusForQueryAtomsDefinedByIds + " Angstr�ms ");
		return "";
	}



	@Override
	public ShapeContainerIfc getShapeContainer() throws ShapeBuildingException {

		if (myStructureGlobalBrut == null){
			myStructureGlobalBrut = getMyStructure(fourLetterCode);
		}

		ShapeBuilder shapeBuilder = new ShapeBuilder(myStructureGlobalBrut, algoParameters);
		ShapeContainerAtomIdsWithinShapeWithPeptide shapeContainerAtomIdsWithinShape = shapeBuilder.getShapeAroundAtomDefinedByIds(listAtomDefinedByIds, radiusForQueryAtomsDefinedByIds, chainToIgnore);
		return shapeContainerAtomIdsWithinShape;
	}
}
