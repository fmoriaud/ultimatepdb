package shapeBuilder;

import mystructure.MyChainIfc;
import mystructure.MyStructureIfc;


public class StructureLocalToBuildShapeWholeChain implements StructureLocalToBuildShapeIfc{
	//-------------------------------------------------------------
	// Class variables
	//-------------------------------------------------------------
	private MyStructureIfc myStructureGlobalBrut;

	private char[] chainId;

	private MyChainIfc ligand;
	private MyStructureIfc myStructureLocal;




	//-------------------------------------------------------------
	// Constructor
	//-------------------------------------------------------------
	public StructureLocalToBuildShapeWholeChain(MyStructureIfc myStructureGlobalBrut,
			char[] chainId){

		this.myStructureGlobalBrut = myStructureGlobalBrut;

		this.chainId = chainId;
	}




	//-------------------------------------------------------------
	// Interface & Public methods
	//-------------------------------------------------------------
	public void compute() throws ShapeBuildingException{

		ligand = myStructureGlobalBrut.getAminoMyChain(chainId);

		myStructureLocal = StructureLocalTools.makeStructureLocalAroundAndExcludingMyMonomersFromInputMyChain(myStructureGlobalBrut, ligand); // to skip some monomers at tip is not implemented

		if (myStructureLocal.getAllAminochains().length == 0){
			ShapeBuildingException exception = new ShapeBuildingException("getShapeAroundAChain return no amino chain: likely that the chain has no neighboring chain in that case");
			throw exception;
		}
	}




	//-------------------------------------------------------------
	// Getters & Setters
	//-------------------------------------------------------------
	public MyChainIfc getLigand() {
		return ligand;
	}



	@Override
	public MyStructureIfc getMyStructureLocal() {
		return myStructureLocal;
	}
}