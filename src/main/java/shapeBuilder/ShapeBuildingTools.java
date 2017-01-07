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
package shapeBuilder;

import java.util.List;

import math.MathTools;
import parameters.AlgoParameters;
import mystructure.AtomProperties;
import mystructure.EnumMyReaderBiojava;
import mystructure.MyAtomIfc;
import mystructure.MyChainIfc;
import mystructure.MyMonomerIfc;
import mystructure.MyStructureIfc;

public class ShapeBuildingTools {
    //-------------------------------------------------------------
    // Static methods
    //-------------------------------------------------------------
    public static void deleteChains(List<String> chainsToDelete, MyStructureIfc myStructure) {

        for (String chainTodelete : chainsToDelete) {
            myStructure.removeChain(chainTodelete.toCharArray());
        }
    }


    public static int getCountOfHydrophobicAtomsInTheNeighborhoodOfMyAtomForDehydronsUseOnly(MyAtomIfc myAtom, AlgoParameters algoParameters) {
        int countHydrophobicAtom = 0;
        for (MyChainIfc chainNeighbor : myAtom.getParent().getNeighboringAminoMyMonomerByRepresentativeAtomDistance()) {
            for (MyMonomerIfc monomerNeighbor : chainNeighbor.getMyMonomers()) {
                for (MyAtomIfc atomNeighbor : monomerNeighbor.getMyAtoms()) {

                    if (isMyAtomHydrophobic(atomNeighbor)) {
                        float distance = MathTools.computeDistance(myAtom.getCoords(), atomNeighbor.getCoords());
                        if (distance < (algoParameters.getCUTOFF_DISTANCE_FORHYDROPHOBIC_AROUND_HBOND() + 1.2f)) { // because we use the H in the middle
                            countHydrophobicAtom += 1;
                        }
                    }
                }
            }
        }
        return countHydrophobicAtom;
    }


    // -------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------
    private static boolean isMyAtomHydrophobic(MyAtomIfc myAtom) {

        Float hydrophobicity = AtomProperties.findHydrophobicityForMyAtom(myAtom);
        if (hydrophobicity != null && hydrophobicity > 0.9f) {
            return true;
        }
        return false;
    }
}
