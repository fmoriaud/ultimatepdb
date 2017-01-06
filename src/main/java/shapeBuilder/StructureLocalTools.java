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

import mystructure.MyChainIfc;
import mystructure.MyMonomerIfc;

import java.util.HashSet;
import java.util.Set;

public class StructureLocalTools {
    //-------------------------------------------------------------
    // Public & Override methods
    //-------------------------------------------------------------
    public static Set<MyMonomerIfc> makeMyMonomersLocalAroundAndExcludingMyMonomersFromInputMyChain(MyChainIfc myChain) {

        Set<MyMonomerIfc> queryMyMonomer = makeMyMonomersLocalAroundAndWithChain(myChain);

        for (MyMonomerIfc monomerToRemove : myChain.getMyMonomers()) {
            queryMyMonomer.remove(monomerToRemove);
        }
        return queryMyMonomer;
    }


    public static Set<MyMonomerIfc> makeMyMonomersLocalAroundAndWithChain(MyChainIfc myChain) {

        Set<MyMonomerIfc> queryMyMonomer = new HashSet<>();
        for (MyMonomerIfc myMonomer : myChain.getMyMonomers()) {
            queryMyMonomer.add(myMonomer);
        }

        for (MyMonomerIfc monomer : myChain.getMyMonomers()) {
            MyChainIfc[] neighbors = monomer.getNeighboringAminoMyMonomerByRepresentativeAtomDistance();
            for (MyChainIfc mychain : neighbors) {
                for (MyMonomerIfc neighbor : mychain.getMyMonomers()) {
                    queryMyMonomer.add(neighbor);
                }
            }
        }
        return queryMyMonomer;
    }
}
