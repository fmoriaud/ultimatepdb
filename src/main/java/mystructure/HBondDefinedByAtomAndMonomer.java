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

public class HBondDefinedByAtomAndMonomer {
    //-------------------------------------------------------------
    // Class variables
    //-------------------------------------------------------------
    private MyAtomIfc myAtom1;
    private MyAtomIfc myAtom2;
    private MyAtomIfc hydrogen;
    private MyMonomerIfc myMonomer1;
    private MyMonomerIfc myMonomer2;


    //-------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------
    public HBondDefinedByAtomAndMonomer(MyAtomIfc myAtom1, MyAtomIfc myAtom2, MyAtomIfc hydrogen, MyMonomerIfc myMonomer1, MyMonomerIfc myMonomer2) {
        this.myAtom1 = myAtom1;
        this.myAtom2 = myAtom2;
        this.hydrogen = hydrogen;
        this.myMonomer1 = myMonomer1;
        this.myMonomer2 = myMonomer2;
    }


    //-------------------------------------------------------------
    // Interface & Public methods
    //-------------------------------------------------------------
    @Override
    public String toString() {
        return "[ "
                + String.valueOf(myMonomer1.getParent().getChainId()) + " " + String.valueOf(myMonomer1.getThreeLetterCode()) + " " + myMonomer1.getResidueID() + " " + String.valueOf(myAtom1.getAtomName()) +
                " - "
                + String.valueOf(myMonomer2.getParent().getChainId()) + " " + String.valueOf(myMonomer2.getThreeLetterCode()) + " " + myMonomer2.getResidueID() + " " + String.valueOf(myAtom2.getAtomName()) +
                " ]";
    }


    //-------------------------------------------------------------
    // Getters and Setters
    //-------------------------------------------------------------
    public MyAtomIfc getMyAtom1() {
        return myAtom1;
    }

    public MyAtomIfc getMyAtom2() {
        return myAtom2;
    }

    public MyAtomIfc getHydrogen() {
        return hydrogen;
    }

    public MyMonomerIfc getMyMonomer1() {
        return myMonomer1;
    }

    public MyMonomerIfc getMyMonomer2() {
        return myMonomer2;
    }
}
