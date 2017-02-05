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
public class MyAtomTest {

    // TODO Improve test by using AtomGaussianDescriptors
    @Test
    public void testMyAtom() {

        char[] element = "C".toCharArray();
        float[] coords = new float[3];
        char[] atomName = "CA".toCharArray();
        int originalAtomId = 0;
        try {
            MyAtom myAtomValid = new MyAtom(element, coords, atomName, originalAtomId);
        } catch (ExceptionInMyStructurePackage e) {
            assertTrue(false);
        }
    }

    @Test
    public void testMyAtomIllegalElement() {

        char[] element = "X".toCharArray();
        float[] coords = new float[3];
        char[] atomName = "CA".toCharArray();
        int originalAtomId = 0;
        try {
            MyAtom myAtomValid = new MyAtom(element, coords, atomName, originalAtomId);
        } catch (ExceptionInMyStructurePackage e) {
            assertTrue(true);
        }
    }

    @Test
    public void testMyBondsEmpty() {
        char[] element = "C".toCharArray();
        float[] coords = new float[3];
        char[] atomName = "CA".toCharArray();
        int originalAtomId = 0;
        try {
            MyAtom myAtomValid = new MyAtom(element, coords, atomName, originalAtomId);
            assertTrue(myAtomValid.getBonds() != null);
            assertTrue(myAtomValid.getBonds().length == 0);
        } catch (ExceptionInMyStructurePackage e) {
        }
    }

    @Test
    public void testAddMyBond() {

        char[] element = "C".toCharArray();
        float[] coords = new float[3];
        char[] atomName = "CA".toCharArray();
        int originalAtomId = 0;
        try {
            MyAtom myAtom1 = new MyAtom(element, coords, atomName, originalAtomId);
            MyAtom myAtom2 = new MyAtom(element, coords, atomName, originalAtomId);
            MyBondIfc myBond = new MyBond(myAtom2, 1);
            myAtom1.addBond(myBond);
            assertTrue(myAtom1.getBonds().length == 1);
            assertTrue(myAtom1.getBonds()[0].getBondedAtom() == myAtom2);

        } catch (ExceptionInMyStructurePackage e) {
        }
    }

    @Test
    public void testRemoveMyBond() {

        char[] element = "C".toCharArray();
        float[] coords = new float[3];
        char[] atomName = "CA".toCharArray();
        int originalAtomId = 0;
        try {
            MyAtom myAtom1 = new MyAtom(element, coords, atomName, originalAtomId);
            MyAtom myAtom2 = new MyAtom(element, coords, atomName, originalAtomId);
            MyBondIfc myBond = new MyBond(myAtom2, 1);
            myAtom1.addBond(myBond);
            myAtom1.removeBond(myBond);
            assertTrue(myAtom1.getBonds().length == 0);

        } catch (ExceptionInMyStructurePackage e) {
        }
    }
}
