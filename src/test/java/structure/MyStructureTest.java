package structure;

import org.junit.Test;
import parameters.AlgoParameters;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 29/08/16.
 */
public class MyStructureTest {

    @Test
    public void MyStructureConstructorWithOnlyOneMonomer() {

        MyMonomerIfc monomer = null;
        try {
            monomer = TestTools.buildValidMyMonomer(1);
        } catch (ExceptionInMyStructurePackage e1) {
        }

        AlgoParameters algoParameter = new AlgoParameters();
        try {
            MyStructure myStructureOK = new MyStructure(monomer, algoParameter);
        } catch (ExceptionInMyStructurePackage e) {
            assertTrue(false);
        }

        algoParameter = null;
        try {
            MyStructure MyStructureNotOK = new MyStructure(monomer, algoParameter);
        } catch (ExceptionInMyStructurePackage e) {
            assertTrue(true);
        }
    }

    @Test
    public void MyStructureConstructorWithThreeChainArray() {

        MyMonomerIfc myMonomer1 = null;
        MyMonomerIfc myMonomer2 = null;
        try {
            myMonomer1 = TestTools.buildValidMyMonomer(1);
            myMonomer2 = TestTools.buildValidMyMonomer(2);
        } catch (ExceptionInMyStructurePackage e) {
        }

        MyMonomerIfc[] myMonomers1 = new MyMonomerIfc[2];
        myMonomers1[0] = myMonomer1;
        myMonomers1[1] = myMonomer2;
        MyChainIfc myChain1 = new MyChain(myMonomers1, "A".toCharArray());
        MyChainIfc[] anyChainArray = new MyChainIfc[1];
        anyChainArray[0] = myChain1;

        AlgoParameters algoParameters = new AlgoParameters();
        try {
            MyStructureIfc myStructure1 = new MyStructure(anyChainArray, anyChainArray, anyChainArray, algoParameters);
        } catch (ExceptionInMyStructurePackage e) {
            assertTrue(false);
        }

        // one null MyChain[] throws an exception
        try {
            MyStructureIfc myStructure1 = new MyStructure(null, anyChainArray, anyChainArray, algoParameters);
        } catch (ExceptionInMyStructurePackage e) {
            assertTrue(true);
        }

        // All empty chains throw exception
        MyChainIfc[] emptyChainArray = new MyChainIfc[0];
        try {
            MyStructureIfc myStructure1 = new MyStructure(emptyChainArray, emptyChainArray, emptyChainArray, algoParameters);
        } catch (ExceptionInMyStructurePackage e) {
            assertTrue(true);
        }
    }



    //TODO  should test that each and every atom has monomer parent: as it is mandatory for the monomer by bond
    // so needed to put this test at the end of each MyStructure constructor

    // then build here the test home
    // should not throw exception

    // change one parent to null or to something else than a monomer then see if it throws an exception


    @Test
    public void testParentConstruction(){

        MyStructureIfc myStructure = null;
        try {
            myStructure = TestTools.buildValidMyStructure(MyMonomerType.AMINOACID);
        } catch (ExceptionInMyStructurePackage e1) {
            assertTrue(false);
        }

        for (MyChainIfc chainOriginal: myStructure.getAllChains()){
            for (MyMonomerIfc monomer: chainOriginal.getMyMonomers()){
                assertTrue(monomer.getParent() == chainOriginal);

                for (MyAtomIfc atom: monomer.getMyAtoms()){
                    assertTrue(atom.getParent() == monomer);
                }
            }
        }
    }



    // MyStructure integrity is not safe, one can modify everything inside without checking if it is valid
    // and without updating the neighbors... Don't know what to do.
}
