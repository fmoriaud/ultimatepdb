package structure;

import java.util.ArrayList;
import java.util.List;

import math.ToolsMath;
import parameters.AlgoParameters;

public class MergingPeptideTools {



	public static MyChainIfc mergePeptide(MyChainIfc peptideLeft, MyChainIfc peptideRight, AlgoParameters algoParameters) throws ExceptionInMyStructurePackage{

		// i use an order because I will anyhow know how two peptides should be merged to match the query sequence
		MyMonomerIfc leftMonomer = peptideLeft.getMyMonomerByRank(peptideLeft.getMyMonomers().length-1);
		MyMonomerIfc rightMonomer = peptideRight.getMyMonomerByRank(0);

		MyAtomIfc carbonCarbonyl = leftMonomer.getMyAtomFromMyAtomName("C".toCharArray());
		MyAtomIfc nitrogen = rightMonomer.getMyAtomFromMyAtomName("N".toCharArray());

		if (carbonCarbonyl == null || nitrogen == null){
			return null;
		}
		// check bond length 1.33 +- 0.5 (it is drastic)
		float bondLength = ToolsMath.computeDistance(carbonCarbonyl.getCoords(), nitrogen.getCoords());
		if (Math.abs(bondLength - 1.33) > 0.5){
			return null;
		}

		MyAtomIfc oxygenCarbonyl = leftMonomer.getMyAtomFromMyAtomName("O".toCharArray());
		MyAtomIfc cAlpha = rightMonomer.getMyAtomFromMyAtomName("CA".toCharArray());

		if (oxygenCarbonyl == null || cAlpha == null){
			return null;
		}

		// check for flat dihedral angle and normal orientation: so +-20 degres
		float omegaAngle = ToolsMath.computeTorsionAngle(oxygenCarbonyl.getCoords(), carbonCarbonyl.getCoords(), nitrogen.getCoords(), cAlpha.getCoords());
		if (Math.abs(omegaAngle) > 20.0f){
			return null;
		}

		// there it is ok to I could make a merge peptide
		List<MyMonomerIfc> monomersMergePeptides = new ArrayList<>();
		for (MyMonomerIfc monomer: peptideLeft.getMyMonomers()){
			monomersMergePeptides.add(monomer);
		}
		for (MyMonomerIfc monomer: peptideRight.getMyMonomers()){
			monomersMergePeptides.add(monomer);
		}
		MyChainIfc mergedPeptide = new MyChain(monomersMergePeptides);
		MyStructureIfc myStructure = new MyStructure(mergedPeptide, algoParameters);

		MyStructureIfc myStructureCloned = myStructure.cloneWithSameObjects();


		// comment retrouver la bond en question comme ca a ete clone ???


		// I need to make a new bond

		return null;
	}
}
