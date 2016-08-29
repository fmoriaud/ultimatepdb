import org.junit.Test;
import structure.ExceptionInMyStructurePackage;
import structure.MyAtom;
import structure.MyBond;
import structure.MyBondIfc;

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
