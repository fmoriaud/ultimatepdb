/*
Author:
      Fabrice Moriaud <fmoriaud@ultimatepdb.org>

  Copyright (c) 2016 Fabrice Moriaud

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
  */
package mystructure;

import java.util.ArrayList;
import java.util.List;

import math.MathTools;
import parameters.AlgoParameters;

public class MergingPeptideTools {
	// -------------------------------------------------------------------
	// Static Methods
	// -------------------------------------------------------------------
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
		float bondLength = MathTools.computeDistance(carbonCarbonyl.getCoords(), nitrogen.getCoords());
		if (Math.abs(bondLength - 1.33) > 0.5){
			return null;
		}

		MyAtomIfc oxygenCarbonyl = leftMonomer.getMyAtomFromMyAtomName("O".toCharArray());
		MyAtomIfc cAlpha = rightMonomer.getMyAtomFromMyAtomName("CA".toCharArray());

		if (oxygenCarbonyl == null || cAlpha == null){
			return null;
		}

		// check for flat dihedral angle and normal orientation: so +-20 degres
		float omegaAngle = MathTools.computeTorsionAngle(oxygenCarbonyl.getCoords(), carbonCarbonyl.getCoords(), nitrogen.getCoords(), cAlpha.getCoords());
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
		//MyStructureIfc myStructure = new MyStructure(mergedPeptide, algoParameters);

		//MyStructureIfc myStructureCloned = myStructure.cloneWithSameObjects();


		// comment retrouver la bond en question comme ca a ete clone ???


		// I need to make a new bond

		return null;
	}
}
