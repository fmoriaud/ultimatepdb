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
package math;

import mystructure.BigHetatmResidues;
import mystructure.MyAtomIfc;
import mystructure.MyMonomerIfc;
import mystructure.MyStructureTools;

public class ToolsMathAppliedToMyStructure {
    // -------------------------------------------------------------------
    // Static Methods
    // -------------------------------------------------------------------
    public static double computeDistanceBetweenTwoResidues(MyMonomerIfc monomer1, MyMonomerIfc monomer2) {
        float distance = 0.0f;

        // for large hetatm entities then the representative atom is not the best one
        if (BigHetatmResidues.isMyMonomerABigResidue(monomer1) || BigHetatmResidues.isMyMonomerABigResidue(monomer2)) {
            distance = MyStructureTools.computeDistance(monomer1, monomer2);

        } else {
            distance = MathTools.computeDistance(getCoordinatesOfRepresentativeAtom(monomer1), getCoordinatesOfRepresentativeAtom(monomer2));
        }
        return distance;
    }


    public static float[] getCoordinatesOfRepresentativeAtom(MyMonomerIfc monomer) {

        MyAtomIfc representativeAtom = MyStructureTools.getRepresentativeMyAtom(monomer);
        if (representativeAtom != null) {
            return representativeAtom.getCoords();
        } else {
            return null;
        }
    }
}
