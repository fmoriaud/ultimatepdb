package structure;

import org.biojava.bio.structure.GroupType;
import org.junit.Before;
import org.junit.Test;
import parameters.AlgoParameters;
import structure.*;

import structure.TestTools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 29/08/16.
 */
public class MyStructureToolsTest {

    private  AlgoParameters algoParameters;


    @Before
    public void prepare(){
        algoParameters = TestTools.getAlgoParameters();
    }



    @Test
    public void testConversion() throws Exception {

        assertTrue(MyStructureTools.convertType(GroupType.AMINOACID).equals(MyMonomerType.AMINOACID));
        assertTrue(MyStructureTools.convertType(GroupType.HETATM).equals(MyMonomerType.HETATM));
        assertTrue(MyStructureTools.convertType(GroupType.NUCLEOTIDE).equals(MyMonomerType.NUCLEOTIDE));
    }



    @Test
    public void testIsHydrogen() {

        char[] element = "H".toCharArray();
        float[] coords = new float[3];
        char[] atomName = "HCA".toCharArray();
        int originalAtomId = 0;

        MyAtom myAtomValid = null;
        try {
            myAtomValid = new MyAtom(element, coords, atomName, originalAtomId);
        } catch (ExceptionInMyStructurePackage e) {
        }
        assertTrue(MyStructureTools.isHydrogen(myAtomValid));
    }



    @Test
    public void testIsNotHydrogen() {

        char[] element = "C".toCharArray();
        float[] coords = new float[3];
        char[] atomName = "CA".toCharArray();
        int originalAtomId = 0;

        MyAtom myAtomValid = null;
        try {
            myAtomValid = new MyAtom(element, coords, atomName, originalAtomId);
        } catch (ExceptionInMyStructurePackage e) {
        }
        assertFalse(MyStructureTools.isHydrogen(myAtomValid));
    }



    @Test
    public void testGenerateHydrogenAtomName() {

        String heavyAtomName = "CA";
        List<char[]> hydrogenNames = MyStructureTools.generateHydrogenAtomName(heavyAtomName);
        assertTrue(hydrogenNames.size() == 1);
        assertTrue(Arrays.equals(hydrogenNames.get(0), "H1CA".toCharArray()));

        heavyAtomName="N";
        hydrogenNames = MyStructureTools.generateHydrogenAtomName(heavyAtomName);
        assertTrue(hydrogenNames.size() == 1);
        assertTrue(Arrays.equals(hydrogenNames.get(0), "H1N".toCharArray()));

        heavyAtomName="O";
        hydrogenNames = MyStructureTools.generateHydrogenAtomName(heavyAtomName);
        assertTrue(hydrogenNames.size() == 0);
    }



    @Test
    public void testIsAtomNameFromBackBoneOtherThanCA() {

        assertFalse(MyStructureTools.isAtomNameFromBackBoneOtherThanCA("CA".toCharArray()));
        assertTrue(MyStructureTools.isAtomNameFromBackBoneOtherThanCA("H1CA".toCharArray()));
        assertTrue(MyStructureTools.isAtomNameFromBackBoneOtherThanCA("H1N".toCharArray()));
        assertTrue(MyStructureTools.isAtomNameFromBackBoneOtherThanCA("N".toCharArray()));
        assertTrue(MyStructureTools.isAtomNameFromBackBoneOtherThanCA("O".toCharArray()));

        String atomNameNotFromBackBone = "CB";
        assertFalse(MyStructureTools.isAtomNameFromBackBoneOtherThanCA(atomNameNotFromBackBone.toCharArray()));
    }


    @Test
    public void testRenumberAllAtomIds(){

        MyStructureIfc myStructure = null;
        try {
            myStructure = TestTools.buildValidMyStructure(MyMonomerType.AMINOACID);
        } catch (ExceptionInMyStructurePackage e) {
            assertTrue(false);
        }

        MyStructureTools.renumberAllAtomIds(myStructure);
        MyChainIfc[] aminoChains = myStructure.getAllAminochains();
        int atomId = 1;
        for (MyChainIfc chain: aminoChains){
            for (MyMonomerIfc monomer: chain.getMyMonomers()){
                for (MyAtomIfc atom: monomer.getMyAtoms()){
                    int currentAtomId = atom.getOriginalAtomId();
                    assertTrue(currentAtomId == atomId);
                    atomId += 1;
                }
            }
        }
    }



    @Test
    public void testCountBonds(){

        MyStructureIfc myStructure = null;
        try {
            myStructure = TestTools.buildValidMyStructure(MyMonomerType.AMINOACID);
        } catch (ExceptionInMyStructurePackage e) {
            assertTrue(false);
        }
        int countOfBonds = 0;
        try {
            countOfBonds = MyStructureTools.countBonds(myStructure);
        } catch (ExceptionInMyStructurePackage e) {
            assertTrue(false);
        }
        // currently there are 4 monomers having each 2 bonds so 8
        int countOfMyBonds = MyStructureTools.countMyBond(myStructure);
        assertTrue(countOfMyBonds == 20);
        assertTrue(countOfBonds == 10);
    }



    @Test
    public void testRemoveBondsToNonExistingAtoms(){

        MyStructureIfc myStructure = null;
        try {
            myStructure = TestTools.buildValidMyStructure(MyMonomerType.AMINOACID);
        } catch (ExceptionInMyStructurePackage e1) {
            assertTrue(false);
        }

        MyAtomIfc atomOutSideOfMyStructure = null;
        try {
            atomOutSideOfMyStructure = TestTools.buildValidMyAtomCarbonAlpha(15);
        } catch (ExceptionInMyStructurePackage e2) {
            assertTrue(false);
        }

        int countOfBondsBefore = MyStructureTools.countMyBond(myStructure);

        MyAtomIfc anyAtom = myStructure.getAminoChain(0).getMyMonomerByRank(0).getMyAtoms()[0];
        MyBondIfc validbond = anyAtom.getBonds()[0];
        validbond.setBondedAtom(atomOutSideOfMyStructure);

        MyStructureTools.removeBondsToMyAtomsNotInMyStructure(myStructure);

        int countOfBondsAfter = MyStructureTools.countMyBond(myStructure);

        assertTrue(countOfBondsAfter == countOfBondsBefore - 1);
    }



    @Test
    public void testGetRepresentativeMyAtom(){

        MyMonomer myMonomerAmino = null;
        try {
            myMonomerAmino = TestTools.buildValidMyMonomer(1);
        } catch (ExceptionInMyStructurePackage e) {
            assertTrue(false);
        }
        MyAtomIfc repAtom = MyStructureTools.getRepresentativeMyAtom(myMonomerAmino);
        assertTrue(Arrays.equals(repAtom.getAtomName(), "CA".toCharArray()));
    }



    @Test
    public void testStorageComputeAndStoreNeighBorhingAminoMonomersByDistanceBetweenRepresentativeMyAtom(){

        MyStructureIfc myStructure = null;
        try {
            myStructure = TestTools.buildValidMyStructure(MyMonomerType.AMINOACID);
        } catch (ExceptionInMyStructurePackage e1) {
            assertTrue(false);
        }

        // needed as it is the distance used in MyStructure

        float minDistanceToBeneighbor = algoParameters.getMIN_DISTANCE_TO_BE_NEIBHOR();
        for (int i=1; i<10; i++){

            float y = 0f;
            float z = 0f;
            float x = 0f;

            // set coordinates to representative myAtom
            // set their x,y,z on a x axis, so distance is 1D and therefore easy to check
            List<Float> xCoordinates = new ArrayList<>();
            for (MyChainIfc chain: myStructure.getAllChains()){
                for (MyMonomerIfc monomer: chain.getMyMonomers()){
                    MyAtomIfc repAtom = MyStructureTools.getRepresentativeMyAtom(monomer);

                    x += minDistanceToBeneighbor * 2 / i;
                    float[] coords = new float[3];
                    coords[0] = x;
                    coords[1] = y;
                    coords[2] = z;
                    repAtom.setCoords(coords);
                    xCoordinates.add(x);
                }
            }

            // clone in order that neighbors are computed (coordinates were changed so neighbors are wrong)
            MyStructureIfc myStructureCloned = null;
            try {
                myStructureCloned = myStructure.cloneWithSameObjects();
            } catch (ExceptionInMyStructurePackage e) {
                e.printStackTrace();
            }

            GeneratorNeighboringMonomerUsedForShapeGeneration generator = new GeneratorNeighboringMonomerUsedForShapeGeneration(myStructureCloned, minDistanceToBeneighbor);

            MyChainIfc[] aminoChains = myStructureCloned.getAllAminochains();
            for (MyChainIfc chain: aminoChains){
                A: for (MyMonomerIfc startingMonomer: chain.getMyMonomers()){
                    MyChainIfc[] neighborsRecomputed = generator.computeAminoNeighborsOfAGivenResidue(startingMonomer);

                    for (MyChainIfc chainOriginal: myStructureCloned.getAllAminochains()){
                        for (MyMonomerIfc monomer: chainOriginal.getMyMonomers()){
                            if (monomer == startingMonomer){ // found momomer

                                MyChainIfc[] neighborsOriginal = monomer.getNeighboringAminoMyMonomerByRepresentativeAtomDistance();

                                //System.out.println(neighborsOriginal.length + " " + neighborsRecomputed.length);
                                assertTrue(neighborsOriginal.length == neighborsRecomputed.length);

                                for (int j=0; j<neighborsOriginal.length; j++){
                                    MyChainIfc recomp = neighborsRecomputed[j];
                                    MyMonomerIfc[] recompMono = recomp.getMyMonomers();
                                    MyChainIfc orig = neighborsOriginal[j];
                                    MyMonomerIfc[] origMono = orig.getMyMonomers();
                                    //System.out.println(recompMono.length + " " + origMono.length);
                                    assertTrue(recompMono.length == origMono.length);
                                    for (int k=0; k<recompMono.length; k++){
                                        assertTrue(recompMono[k] == origMono[k]);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }



    @Test
    public void testcomputeAndStoreNeighboringMonomersByBond(){

        MyStructureIfc myStructure = null;
        try {
            myStructure = TestTools.buildValidMyStructure(MyMonomerType.AMINOACID);
        } catch (ExceptionInMyStructurePackage e1) {
            assertTrue(false);
        }

        // check if good when build from TestTools
        for (MyChainIfc chainOriginal: myStructure.getAllAminochains()){
            for (MyMonomerIfc monomer: chainOriginal.getMyMonomers()){
                MyMonomerIfc[] neighbors = monomer.getNeighboringMyMonomerByBond();
                assertTrue(neighbors.length == 1);
            }
        }

        // removing the neighbors by bond
        for (MyChainIfc chainOriginal: myStructure.getAllAminochains()){
            for (MyMonomerIfc monomer: chainOriginal.getMyMonomers()){
                MyMonomerIfc[] neighbors = null;
                monomer.setNeighboringMyMonomerByBond(neighbors);
            }
        }

        MyStructureTools.computeAndStoreNeighboringMonomersByBond(myStructure);

        for (MyChainIfc chainOriginal: myStructure.getAllAminochains()){
            for (MyMonomerIfc monomer: chainOriginal.getMyMonomers()){
                MyMonomerIfc[] neighbors = monomer.getNeighboringMyMonomerByBond();
                // Because the test MyStructure has 2 chains of 2 linked mymonomer so each has one neighboring monomer by bond
                assertTrue(neighbors.length == 1);
            }
        }
    }



    @Test
    public void testRemoveAllExplicitHydrogensd(){

        // Make a MyStructure test then add some explicit h
        // use the method to remove
        // check that they are removed
    }
}
