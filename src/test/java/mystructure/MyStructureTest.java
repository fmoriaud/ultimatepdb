package mystructure;

import convertformat.AdapterBioJavaStructure;
import io.BiojavaReaderFromPathToMmcifFileTest;
import io.CdkTools;
import io.Tools;
import io.WriteTextFile;
import org.biojava.nbio.structure.Structure;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.openscience.cdk.interfaces.IAtomContainer;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;

import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 29/08/16.
 */
public class MyStructureTest {

    @Rule
    public TemporaryFolder folder= new TemporaryFolder();

    @Test
    public void MyStructureConstructorWithOnlyOneMonomer() throws ParsingConfigFileException, IOException{

        MyMonomerIfc monomer = null;
        try {
            monomer = TestTools.buildValidMyMonomer(1);
        } catch (ExceptionInMyStructurePackage e1) {
        }

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders(TestTools.testChemcompFolder);
        try {
            MyStructure myStructureOK = new MyStructure(monomer, algoParameters);
        } catch (ExceptionInMyStructurePackage e) {
            assertTrue(false);
        }

        algoParameters = null;
        try {
            MyStructure MyStructureNotOK = new MyStructure(monomer, algoParameters);
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

    @Test
    public void testToV3000() throws ParsingConfigFileException, IOException, ReadingStructurefileException, ExceptionInMyStructurePackage{

        URL url = BiojavaReaderFromPathToMmcifFileTest.class.getClassLoader().getResource("1di9.cif.gz");
        Structure mmcifStructure = mmcifStructure = Tools.getStructure(url, TestTools.testChemcompFolder);

        URL urlUltimate = BiojavaReaderFromPathToMmcifFileTest.class.getClassLoader().getResource("ultimate.xml");
        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders(TestTools.testChemcompFolder);

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc myStructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure, EnumMyReaderBiojava.BioJava_MMCIFF);

        String myStructureV3000 = myStructure.toV3000();

        // write to a temp text file
        String pathToTempFolder = folder.getRoot().getAbsolutePath();
        String pathTOWriteV3000Molfile = pathToTempFolder + "//v3000test.mol";
        WriteTextFile.writeTextFile(myStructureV3000, pathTOWriteV3000Molfile);

        // read it with cdk and check atom and bond count

        IAtomContainer mol = CdkTools.readV3000molFile(pathTOWriteV3000Molfile);
        int atomCount = getAtomCount(myStructure);
        int bondCount = getBondCount(myStructure);
        assertTrue(mol.getAtomCount() == atomCount);
        assertTrue(mol.getBondCount()*2 == bondCount);
    }


    private int getAtomCount(MyStructureIfc myStructure){

        int atomCount = 0;
        for (MyChainIfc chain: myStructure.getAllChains()){
            for (MyMonomerIfc monomer: chain.getMyMonomers()){
                atomCount += monomer.getMyAtoms().length;
            }
        }
        return atomCount;
    }


    private int getBondCount(MyStructureIfc myStructure){

        int bondCount = 0;
        for (MyChainIfc chain: myStructure.getAllChains()){
            for (MyMonomerIfc monomer: chain.getMyMonomers()){
                for (MyAtomIfc atom: monomer.getMyAtoms()) {
                    bondCount += atom.getBonds().length;
                }
            }
        }
        return bondCount;
    }
}
