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

import java.io.Serializable;

public class MyBond implements MyBondIfc, Serializable {
    //-------------------------------------------------------------
    // Class variables
    //-------------------------------------------------------------
    MyAtomIfc bondedAtom;
    int bondOrder;


    //-------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------
    /**
     * @param bondedAtom is the bonded MyAtom of this bond
     * @param bondOrder  is the bond order of this bond, e.g. 1 for single bond, 2 for double bond, 3 for triple bond
     * @throws ExceptionInMyStructurePackage
     */
    public MyBond(MyAtomIfc bondedAtom, int bondOrder) throws ExceptionInMyStructurePackage {
        this.bondedAtom = bondedAtom;
        if (!isBondOrderValid(bondOrder)) {
            throw new ExceptionInMyStructurePackage("MyBond cannot be built with bond order " + bondOrder);

        }
        this.bondOrder = bondOrder;
    }


    //-------------------------------------------------------------
    // Interface & Public methods
    //-------------------------------------------------------------
    /**
     * for debugging
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("bond order " + bondOrder);
        sb.append(" to atom: ");
        if (bondedAtom != null && bondedAtom.getParent() != null) {
            sb.append(bondedAtom.getParent().toString() + "  " + bondedAtom.toString());
        }
        return sb.toString();
    }

    /**
     * Return bonded MyAtom
     */
    @Override
    public MyAtomIfc getBondedAtom() {
        return bondedAtom;
    }

    /**
     * Return the bond order of this myBond
     */
    @Override
    public int getBondOrder() {
        return bondOrder;
    }

    /**
     * Change bonded MyAtom, useful when cloning an object containing some MyAtom
     */
    @Override
    public void setBondedAtom(MyAtomIfc atom) {
        bondedAtom = atom;
    }


    //-------------------------------------------------------------
    // Implementation
    //-------------------------------------------------------------
    private boolean isBondOrderValid(int bondOrder) {

        if (bondOrder == 1 || bondOrder == 2 || bondOrder == 3) {
            return true;
        }
        return false;
    }
}