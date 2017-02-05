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

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 29/08/16.
 */
public class MyMonomerTest {

    @Test
    public void MyMonomerGoodFromTestToolsTest() {

        try {
            TestTools.buildValidMyMonomer(1);
        } catch (ExceptionInMyStructurePackage e) {
            assertTrue(false);
        }
    }


    @Test
    public void MyMonomerGoodTest() {
        char[] element = "C".toCharArray();
        float[] coords = new float[3];
        char[] atomName = "CA".toCharArray();
        int originalAtomId = 0;

        MyAtomIfc[] myAtoms = new MyAtomIfc[2];
        try {
            MyAtomIfc myAtom1 = new MyAtom(element, coords, atomName, originalAtomId);
            MyAtomIfc myAtom2 = new MyAtom(element, coords, atomName, originalAtomId);
            myAtoms[0] = myAtom1;
            myAtoms[1] = myAtom2;
        } catch (ExceptionInMyStructurePackage e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        char[] threeLetterCode = "ASP".toCharArray();
        int residueID = 1;
        char insertionLetter = 0;

        try {
            MyMonomer monomer = new MyMonomer(myAtoms, threeLetterCode, residueID, MyMonomerType.AMINOACID, false, insertionLetter, " ".toCharArray()[0]);

        } catch (ExceptionInMyStructurePackage e) {
            assertTrue(false);
        }

        try {
            MyMonomer monomer = new MyMonomer(myAtoms, threeLetterCode, residueID, null, false, insertionLetter, " ".toCharArray()[0]);

        } catch (ExceptionInMyStructurePackage e) {
            assertTrue(true);
        }
    }


    @Test
    public void addAtomTest() {
        char[] element = "C".toCharArray();
        float[] coords = new float[3];
        char[] atomName = "CA".toCharArray();
        int originalAtomId = 0;

        MyAtomIfc[] myAtoms = new MyAtomIfc[2];
        try {
            MyAtomIfc myAtom1 = new MyAtom(element, coords, atomName, originalAtomId);
            MyAtomIfc myAtom2 = new MyAtom(element, coords, atomName, originalAtomId);
            myAtoms[0] = myAtom1;
            myAtoms[1] = myAtom2;
        } catch (ExceptionInMyStructurePackage e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        char[] threeLetterCode = "ASP".toCharArray();
        int residueID = 1;
        char insertionLetter = 0;

        MyMonomer monomer = null;
        try {
            monomer = new MyMonomer(myAtoms, threeLetterCode, residueID, MyMonomerType.AMINOACID, false, insertionLetter, " ".toCharArray()[0]);

        } catch (ExceptionInMyStructurePackage e) {
        }
        int atomCountBefore = monomer.getMyAtoms().length;
        assertTrue(atomCountBefore == 2);
        monomer.addAtom(myAtoms[0]);
        int atomCountAfter = monomer.getMyAtoms().length;
        assertTrue(atomCountAfter == 3);
    }
}
