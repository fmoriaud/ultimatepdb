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

public class HBondDefinedWithAtoms {
    //-------------------------------------------------------------
    // Class variables
    //-------------------------------------------------------------
    private MyAtomIfc myAtomDonor;
    private MyAtomIfc myAtomAcceptor;
    private MyAtomIfc myAtomHydrogen;


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public HBondDefinedWithAtoms(MyAtomIfc myAtomDonor, MyAtomIfc myAtomAcceptor, MyAtomIfc myAtomHydrogen) {
        this.myAtomDonor = myAtomDonor;
        this.myAtomAcceptor = myAtomAcceptor;
        this.myAtomHydrogen = myAtomHydrogen;
    }


    // -------------------------------------------------------------------
    // Public & Interface
    // -------------------------------------------------------------------
    @Override
    public String toString() {
        return "[ "
                + String.valueOf(myAtomDonor.getParent().getParent().getChainId()) + " " + String.valueOf(myAtomDonor.getParent().getThreeLetterCode()) + " " + myAtomDonor.getParent().getResidueID() + " " + String.valueOf(myAtomDonor.getAtomName()) +
                " - "
                + String.valueOf(myAtomAcceptor.getParent().getParent().getChainId()) + " " + String.valueOf(myAtomAcceptor.getParent().getThreeLetterCode()) + " " + myAtomAcceptor.getParent().getResidueID() + " " + String.valueOf(myAtomAcceptor.getAtomName()) +
                " - "
                + String.valueOf(myAtomHydrogen.getParent().getParent().getChainId()) + " " + String.valueOf(myAtomHydrogen.getParent().getThreeLetterCode()) + " " + myAtomHydrogen.getParent().getResidueID() + " " + String.valueOf(myAtomHydrogen.getAtomName()) +
                " ]";
    }


    // -------------------------------------------------------------------
    // Getter and Setter
    // -------------------------------------------------------------------
    public MyAtomIfc getMyAtomDonor() {
        return myAtomDonor;
    }

    public MyAtomIfc getMyAtomAcceptor() {
        return myAtomAcceptor;
    }

    public MyAtomIfc getMyAtomHydrogen() {
        return myAtomHydrogen;
    }
}
