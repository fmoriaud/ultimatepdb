package structure;

import org.junit.Test;
import tools.TestTools;

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
        char[] secStruc = null;

        try {
            MyMonomer monomer = new MyMonomer(myAtoms, threeLetterCode, residueID, MyMonomerType.AMINOACID, insertionLetter, secStruc);

        } catch (ExceptionInMyStructurePackage e) {
            assertTrue(false);
        }

        try {
            MyMonomer monomer = new MyMonomer(myAtoms, threeLetterCode, residueID, null, insertionLetter, secStruc);

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
        char[] secStruc = null;

        MyMonomer monomer = null;
        try {
            monomer = new MyMonomer(myAtoms, threeLetterCode, residueID, MyMonomerType.AMINOACID, insertionLetter, secStruc);

        } catch (ExceptionInMyStructurePackage e) {
        }
        int atomCountBefore = monomer.getMyAtoms().length;
        assertTrue(atomCountBefore == 2);
        monomer.addAtom(myAtoms[0]);
        int atomCountAfter = monomer.getMyAtoms().length;
        assertTrue(atomCountAfter == 3);
    }
}