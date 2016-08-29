import org.junit.Before;
import org.junit.Test;
import structure.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 29/08/16.
 */
public class MyChainTest {

    private MyMonomer monomer1, monomer2;

    @Before
    public void prepare(){
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
            monomer1 = new MyMonomer(myAtoms, threeLetterCode, residueID, MyMonomerType.HETATM, insertionLetter, secStruc);
            monomer2 = new MyMonomer(myAtoms, threeLetterCode, residueID, MyMonomerType.HETATM, insertionLetter, secStruc);
        } catch (ExceptionInMyStructurePackage e) {
        }
    }



    @Test
    public void testConstructorWithAListOfMyMonomersWithNoParents() {
        List<MyMonomerIfc> myMonomers = new ArrayList<>();
        myMonomers.add(monomer1);
        myMonomers.add(monomer2);
        MyChain myChain = new MyChain(myMonomers);
        assertTrue(myChain.getMyMonomers().length == 2);
        assertTrue(Arrays.equals(myChain.getChainId(), MyStructureConstants.CHAIN_ID_DEFAULT.toCharArray()));
    }



    @Test
    public void testConstructorWithAListOfMyMonomersWithParents() {

        List<MyMonomerIfc> myMonomers = new ArrayList<>();
        myMonomers.add(monomer1);
        myMonomers.add(monomer2);
        MyChain myChain = new MyChain(myMonomers);
        assertTrue(myChain.getMyMonomers().length == 2);
        assertTrue(Arrays.equals(myChain.getChainId(), MyStructureConstants.CHAIN_ID_DEFAULT.toCharArray()));
        monomer1.setParent(myChain);
        myChain.setChainId("A".toCharArray());

        MyChain myChain2 = new MyChain(myMonomers);
        assertTrue(myChain2.getMyMonomers().length == 2);
        assertTrue(Arrays.equals(myChain2.getChainId(), "A".toCharArray()));
    }



    @Test
    public void testRemoveMyMonomer() {

        List<MyMonomerIfc> myMonomers = new ArrayList<>();
        myMonomers.add(monomer1);
        myMonomers.add(monomer2);
        MyChain myChain = new MyChain(myMonomers);
        assertTrue(myChain.getMyMonomers().length == 2);

        myChain.removeMyMonomer(monomer1);
        assertTrue(myChain.getMyMonomers().length == 1);

        myChain.removeMyMonomer(monomer1);
        assertTrue(myChain.getMyMonomers().length == 1);

        myChain.removeMyMonomer(monomer2);
        assertTrue(myChain.getMyMonomers().length == 0);
    }



    @Test
    public void testReplaceMonomer() {

        // TODO write test when the methos will be used

    }
}