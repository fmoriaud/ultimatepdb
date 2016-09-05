package shapeBuilder;

import java.util.List;

import math.ToolsMath;
import parameters.AlgoParameters;
import structure.AtomProperties;
import structure.EnumMyReaderBiojava;
import structure.ExceptionInMyStructurePackage;
import structure.MyAtomIfc;
import structure.MyChainIfc;
import structure.MyMonomerIfc;
import structure.MyStructureIfc;
import structure.ReadingStructurefileException;
import structure.StructureReaderMode;

public class ShapeBuildingTools {

	public static void deleteChains(List<String> chainsToDelete, MyStructureIfc myStructure){

		for (String chainTodelete: chainsToDelete){
			myStructure.removeChain(chainTodelete.toCharArray());
		}
	}



	public static MyStructureIfc getMyStructure(char[] fourLetterCode, AlgoParameters algoParameters, EnumMyReaderBiojava enumMyReaderBiojava) throws ShapeBuildingException {
		MyStructureIfc myStructureGlobalBrut = null;
		

				myStructureGlobalBrut = null; // IOTools.getMyStructures(fourLetterCode, algoParameters, enumMyReaderBiojava, StructureReaderMode.ReadyForShapeComputation);

		
		System.out.println("Structure read successfully : " + String.valueOf(myStructureGlobalBrut.getFourLetterCode()));
		return myStructureGlobalBrut;
	}



	public static int getCountOfHydrophobicAtomsInTheNeighborhoodOfMyAtomForDehydronsUseOnly(MyAtomIfc myAtom, AlgoParameters algoParameters){
		int countHydrophobicAtom = 0;
		for (MyChainIfc chainNeighbor: myAtom.getParent().getNeighboringAminoMyMonomerByRepresentativeAtomDistance()){
			for (MyMonomerIfc monomerNeighbor: chainNeighbor.getMyMonomers()){
				for (MyAtomIfc atomNeighbor: monomerNeighbor.getMyAtoms()){

					if (isMyAtomHydrophobic(atomNeighbor)){
						float distance = ToolsMath.computeDistance(myAtom.getCoords(), atomNeighbor.getCoords());
						if (distance <( algoParameters.getCUTOFF_DISTANCE_FORHYDROPHOBIC_AROUND_HBOND() + 1.2f)){ // because we use the H in the middle
							countHydrophobicAtom += 1;
						}
					}
				}
			}
		}
		return countHydrophobicAtom;
	}



	private static boolean isMyAtomHydrophobic(MyAtomIfc myAtom){

		Float hydrophobicity = AtomProperties.findHydrophobicityForMyAtom(myAtom);
		if (hydrophobicity != null && hydrophobicity > 0.9f){
			return true;
		}
		return false;
	}
}
