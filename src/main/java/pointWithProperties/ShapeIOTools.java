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
package pointWithProperties;

import java.util.ArrayList;
import java.util.List;

import mystructure.ExceptionInMyStructurePackage;
import mystructure.MyAtom;
import mystructure.MyAtomIfc;
import mystructure.MyBondIfc;
import mystructure.MyMonomer;
import mystructure.MyMonomerIfc;
import mystructure.MyMonomerType;


public class ShapeIOTools {
    //-------------------------------------------------------------
    // Static methods
    //-------------------------------------------------------------
    public static MyMonomerIfc convertAListOfPointIfcToAPseudoPDBFileForVisualization(List<PointIfc> listPoints, String elementNameForColor) throws ExceptionInMyStructurePackage {

        List<MyAtomIfc> listAtom = new ArrayList<>();
        int pointId = 0;
        for (PointIfc point : listPoints) {
            MyAtomIfc atom = new MyAtom(elementNameForColor.toCharArray(), point.getCoords(), elementNameForColor.toCharArray(), pointId);
            MyBondIfc[] bonds = new MyBondIfc[0];
            atom.setBonds(bonds);
            listAtom.add(atom);
        }

        MyAtomIfc[] myAtoms = listAtom.toArray(new MyAtomIfc[listAtom.size()]);

        MyMonomerIfc myMonomer = new MyMonomer(myAtoms, "XXX".toCharArray(), 999, MyMonomerType.AMINOACID, false, " ".toCharArray()[0], " ".toCharArray()[0]);
        for (MyAtomIfc atom : myAtoms) {
            atom.setParent(myMonomer);
        }
        return myMonomer;
    }
}
