package shapeBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mystructure.MyChain;
import mystructure.MyChainIfc;
import mystructure.MyMonomerIfc;
import mystructure.MyStructureIfc;


public class StructureLocalToBuildShapeHetAtm implements StructureLocalToBuildShapeIfc{
	//-------------------------------------------------------------
	// Class variables
	//-------------------------------------------------------------
	private MyStructureIfc myStructureGlobalBrut;

	private MyChainIfc ligand;
	private MyStructureIfc myStructureLocal;
	private MyMonomerIfc hetAtomsGroup;

	private char[] hetAtomsLigandId;
	private int occurrenceId;


	//-------------------------------------------------------------
	// Constructor
	//-------------------------------------------------------------
	public StructureLocalToBuildShapeHetAtm(MyStructureIfc myStructureGlobalBrut,
			char[] hetAtomsLigandId, int occurrenceId){

		this.myStructureGlobalBrut = myStructureGlobalBrut;
		this.hetAtomsLigandId = hetAtomsLigandId;
		this.occurrenceId = occurrenceId;
	}




	//-------------------------------------------------------------
	// Interface & Public methods
	//-------------------------------------------------------------
	public void compute() throws ShapeBuildingException{

		hetAtomsGroup = findHetAtomLigand(hetAtomsLigandId, occurrenceId, myStructureGlobalBrut);

		if (hetAtomsGroup == null){
			System.out.println("ligand hetatm not found");
			String message = "ligand hetatm not found : " + String.valueOf(hetAtomsLigandId) + " " + occurrenceId + " in " + String.valueOf(myStructureGlobalBrut.getFourLetterCode());
			ShapeBuildingException exception = new ShapeBuildingException(message);
			throw exception;
		}
		ligand = new MyChain(hetAtomsGroup, hetAtomsGroup.getParent().getChainId());

		// Would be nice to refactor but I dont know how
		List<MyMonomerIfc> tipMyMonomersToRemove = new ArrayList<>();
		myStructureLocal = StructureLocalTools.makeStructureLocalAroundAndExcludingMyMonomersFromInputMyChainAndTips(myStructureGlobalBrut, ligand, tipMyMonomersToRemove); // to skip some monomers at tip is not implemented

	}




	//-------------------------------------------------------------
	// Implementation
	//-------------------------------------------------------------
	private MyMonomerIfc findHetAtomLigand(char[] hetAtomsLigandId, int occurrenceId, MyStructureIfc myStructure){

		int countOfFoundRightHetAtomsLigand = 0;
		MyChainIfc[] allHetAtomsChains = myStructure.getAllHetatmchains(); // then the hetatm which are part of aminochains (e.g. 2qlj E) 
		for (MyChainIfc myChain: allHetAtomsChains){
			for (MyMonomerIfc myMonomer: myChain.getMyMonomers()){
				if (Arrays.equals(myMonomer.getThreeLetterCode(), hetAtomsLigandId)){
					countOfFoundRightHetAtomsLigand += 1;
					if (countOfFoundRightHetAtomsLigand == occurrenceId){
						return myMonomer;
					}
				}
			}
		}
		return null;
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



	public MyMonomerIfc getHetAtomsGroup() {
		return hetAtomsGroup;
	}
}
