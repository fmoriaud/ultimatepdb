package structure;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 29/08/16.
 */
public class MyBondTest {

    @Test
    public void testMyBond() {

        char[] element = "C".toCharArray();
        float[] coords = new float[3];
        char[] atomName = "CA".toCharArray();
        int originalAtomId = 0;
        try {
            MyAtom myAtom1 = new MyAtom(element, coords, atomName, originalAtomId);
            MyAtom myAtom2 = new MyAtom(element, coords, atomName, originalAtomId);
            MyBondIfc myBond = new MyBond(myAtom2, 4);
        } catch (ExceptionInMyStructurePackage e) {
            assertTrue(true);
        }
    }
}
