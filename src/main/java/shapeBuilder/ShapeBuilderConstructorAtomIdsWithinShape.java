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

	/**
	 * Constructor of Shapecontainer based on Atoms. The shape is built on the neighborhing surface.
	 * @param myStructureGlobalBrut
	 * @param listAtomDefinedByIds is a list of definition of MyAtom. Residue Id is the residue Id not the rank in the chain.
	 * @param chainToIgnore
	 * @param algoParameters
	 */
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


	/**
	 * Return the shape container based on parameters of the constructor
	 * @return
	 * @throws ShapeBuildingException
	 */
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
