package shapebuilder;

import convertformat.AdapterBioJavaStructure;
import convertformat.ExceptionInConvertFormat;
import io.BiojavaReader;
import io.ExceptionInIOPackage;
import io.Tools;
import mystructure.*;
import org.biojava.nbio.structure.Structure;
import org.junit.Ignore;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;
import shapeBuilder.StructureLocalTools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 11/10/16.
 */
public class StructureLocalToolsTest {

    @Test
    public void testMethodMakeStructureLocalForSegmentAroundAndExcludingMyMonomersFromInputMyChain() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 1);

        String fourLetterCode = "2ce8";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc myStructureGlobalBrut = null;
        try {
            myStructureGlobalBrut = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure, EnumMyReaderBiojava.BioJava_MMCIFF);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
            assertTrue(false);
        }

        MyChainIfc inputChain = myStructureGlobalBrut.getAminoMyChain("X".toCharArray());

        int rankIdinChain = 2;
        int peptideLength = 3;
        MyChainIfc segmentOfChain = StructureLocalTools.extractSubChain(inputChain, rankIdinChain, peptideLength, algoParameters);


        MyStructureIfc structureLocal = StructureLocalTools.makeStructureLocalForSegmentAroundAndExcludingMyMonomersFromInputMyChain(myStructureGlobalBrut, segmentOfChain, algoParameters);
        MyMonomerIfc monomerOnLeft = structureLocal.getAminoMyChain("X".toCharArray()).getMyMonomerFromResidueId(2);
        assertTrue(monomerOnLeft.getMyAtomFromMyAtomName("C".toCharArray()) == null);
        assertTrue(monomerOnLeft.getMyAtomFromMyAtomName("O".toCharArray()) == null);
        assertTrue(monomerOnLeft.getMyAtomFromMyAtomName("N".toCharArray()) != null);

        MyMonomerIfc monomerOnRight = structureLocal.getAminoMyChain("X".toCharArray()).getMyMonomerFromResidueId(6);
        assertTrue(monomerOnRight.getMyAtomFromMyAtomName("C".toCharArray()) != null);
        assertTrue(monomerOnRight.getMyAtomFromMyAtomName("O".toCharArray()) != null);
        assertTrue(monomerOnRight.getMyAtomFromMyAtomName("N".toCharArray()) == null);
    }


    @Test
    public void testMethodExtractSubChain() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 1);

        String fourLetterCode = "2ce8";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure, EnumMyReaderBiojava.BioJava_MMCIFF);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
            assertTrue(false);
        }

        // SEQRES   1 X    9  MET PHE SER ILE ASP ASN ILE LEU ALA
        MyChainIfc inputChain = mystructure.getAminoMyChain("X".toCharArray());

        int rankIdinChain = 2;
        int peptideLength = 3;
        MyChainIfc segmentOfChain = StructureLocalTools.extractSubChain(inputChain, rankIdinChain, peptideLength, algoParameters);
        assertTrue(segmentOfChain.getMyMonomers().length == 3);
        // SER ILE ASP
        assertArrayEquals(segmentOfChain.getMyMonomerByRank(0).getThreeLetterCode(), "SER".toCharArray());
        assertArrayEquals(segmentOfChain.getMyMonomerByRank(1).getThreeLetterCode(), "ILE".toCharArray());
        assertArrayEquals(segmentOfChain.getMyMonomerByRank(2).getThreeLetterCode(), "ASP".toCharArray());

        // Check peptide bonds
        MyAtomIfc n1 = segmentOfChain.getMyMonomers()[1].getMyAtomFromMyAtomName("N".toCharArray());
        MyAtomIfc c0 = segmentOfChain.getMyMonomers()[0].getMyAtomFromMyAtomName("C".toCharArray());
        boolean foundPeptideBond = false;
        for (MyBondIfc bond : n1.getBonds()) {
            if (bond.getBondedAtom() == c0) {
                foundPeptideBond = true;
            }
        }
        assertTrue(foundPeptideBond);

        MyAtomIfc n2 = segmentOfChain.getMyMonomers()[2].getMyAtomFromMyAtomName("N".toCharArray());
        MyAtomIfc c1 = segmentOfChain.getMyMonomers()[1].getMyAtomFromMyAtomName("C".toCharArray());
        foundPeptideBond = false;
        for (MyBondIfc bond : n2.getBonds()) {
            if (bond.getBondedAtom() == c1) {
                foundPeptideBond = true;
            }
        }
        assertTrue(foundPeptideBond);
    }


    @Test
    public void testMethodMakeChainSegment() throws IOException, ParsingConfigFileException {

        char[] chainId = "C".toCharArray();
        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 1);

        String fourLetterCode = "2ce8";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure, EnumMyReaderBiojava.BioJava_MMCIFF);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
            assertTrue(false);
        }

        // SEQRES   1 X    9  MET PHE SER ILE ASP ASN ILE LEU ALA
        MyChainIfc inputChain = mystructure.getAminoMyChain("X".toCharArray());
        assertTrue(inputChain.getMyMonomers().length == 9);

        int rankIdinChain = 2;
        int peptideLength = 3;
        MyChainIfc segmentOfChain = StructureLocalTools.makeChainSegment(inputChain, rankIdinChain, peptideLength, algoParameters);
        assertTrue(segmentOfChain.getMyMonomers().length == 3);
        // In this chain the monomers id goes from 0 to 12 so easy to test
        for (int i = 0; i < segmentOfChain.getMyMonomers().length; i++) {
            assertTrue(segmentOfChain.getMyMonomers()[i].getResidueID() == (i + rankIdinChain + 1));
        }

        // Check if bonds are ok
        List<MyAtomIfc> atomsInSEgment = new ArrayList<>();

        for (MyMonomerIfc monomer : segmentOfChain.getMyMonomers()) {
            MyAtomIfc[] atoms = monomer.getMyAtoms();
            for (MyAtomIfc atom : atoms) {
                if (!atomsInSEgment.contains(atom)) {
                    atomsInSEgment.add(atom);
                }
            }
        }
        for (MyAtomIfc atom : atomsInSEgment) {
            assertTrue(atom.getBonds() != null && atom.getBonds().length != 0);
            for (MyBondIfc bond : atom.getBonds()) {
                if (atomsInSEgment.contains(bond.getBondedAtom()) == false) {
                    System.out.println();
                }
                assertTrue(atomsInSEgment.contains(bond.getBondedAtom()));
            }
        }

        // Check peptide bonds
        MyAtomIfc n1 = segmentOfChain.getMyMonomers()[1].getMyAtomFromMyAtomName("N".toCharArray());
        MyAtomIfc c0 = segmentOfChain.getMyMonomers()[0].getMyAtomFromMyAtomName("C".toCharArray());
        boolean foundPeptideBond = false;
        for (MyBondIfc bond : n1.getBonds()) {
            if (bond.getBondedAtom() == c0) {
                foundPeptideBond = true;
            }
        }
        assertTrue(foundPeptideBond);

        // Check bonds at tip
        MyAtomIfc nTerminal = MyStructureTools.getNterminal(segmentOfChain);
        assertTrue(nTerminal == null); // there is no Nterminal in segment
        MyAtomIfc caNTerminal = MyStructureTools.getCaNterminal(segmentOfChain);


        // caNterminal has two bonds to C and CB
        assertTrue(caNTerminal.getBonds().length == 2);
        boolean cFound = false;
        boolean cbFound = false;
        for (MyBondIfc bond : caNTerminal.getBonds()) {
            if (Arrays.equals(bond.getBondedAtom().getAtomName(), "C".toCharArray())) {
                cFound = true;
            }
            if (Arrays.equals(bond.getBondedAtom().getAtomName(), "CB".toCharArray())) {
                cbFound = true;
            }
        }
        assertTrue(cFound);
        assertTrue(cbFound);

        MyAtomIfc cTerminal = MyStructureTools.getCterminal(segmentOfChain);
        assertTrue(cTerminal == null); // there is no Cterminal in segment
        MyAtomIfc oTerminal = MyStructureTools.getOterminal(segmentOfChain);
        assertTrue(oTerminal == null); // there is no Cterminal in segment

        MyAtomIfc caCTerminal = MyStructureTools.getCaCterminal(segmentOfChain);

        // caCTerminal has two bonds: to N and to CB of the same monomer
        assertTrue(caCTerminal.getBonds().length == 2);
        MyBondIfc[] bonds = caCTerminal.getBonds();
        cbFound = false;
        boolean nFound = false;
        for (MyBondIfc bond : bonds) {
            if (Arrays.equals(bond.getBondedAtom().getAtomName(), "N".toCharArray())) {
                nFound = true;
            }
            if (Arrays.equals(bond.getBondedAtom().getAtomName(), "CB".toCharArray())) {
                cbFound = true;
            }
        }
        assertTrue(cbFound);
        assertTrue(nFound);

        List<MyMonomerIfc> monomersInSegment = MyStructureTools.makeListFromArray(segmentOfChain.getMyMonomers());
        // Check neighbors
        for (MyMonomerIfc monomer : segmentOfChain.getMyMonomers()) {
            for (MyChainIfc neighborsByDistance : monomer.getNeighboringAminoMyMonomerByRepresentativeAtomDistance()) {
                for (MyMonomerIfc neighborByDistance : neighborsByDistance.getMyMonomers()) {
                    assertTrue(monomersInSegment.contains(neighborByDistance));
                }
            }
        }
    }


    @Ignore
    @Test
    public void testMethodMakeChainSegmentStappledPeptide() throws IOException, ParsingConfigFileException {

        char[] chainId = "C".toCharArray();
        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 1);

        String fourLetterCode = "2yjd";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure, EnumMyReaderBiojava.BioJava_MMCIFF);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
            assertTrue(false);
        }

        // Chain C is a peptide bound and has 13 monomers
        MyChainIfc inputChain = mystructure.getAminoMyChain("C".toCharArray());
        assertTrue(inputChain.getMyMonomers().length == 13);

        int rankIdinChain = 2;
        int peptideLength = 3;
        MyChainIfc segmentOfChain = StructureLocalTools.makeChainSegment(inputChain, rankIdinChain, peptideLength, algoParameters);
        assertTrue(segmentOfChain.getMyMonomers().length == 3);
        // In this chain the monomers id goes from 0 to 12 so easy to test
        for (int i = 0; i < segmentOfChain.getMyMonomers().length; i++) {
            assertTrue(segmentOfChain.getMyMonomers()[i].getResidueID() == i);
        }

        // Check if bonds are ok
        List<MyAtomIfc> atomsInSEgment = new ArrayList<>();

        for (MyMonomerIfc monomer : segmentOfChain.getMyMonomers()) {
            MyAtomIfc[] atoms = monomer.getMyAtoms();
            for (MyAtomIfc atom : atoms) {
                if (!atomsInSEgment.contains(atom)) {
                    atomsInSEgment.add(atom);
                }
            }
        }
        for (MyAtomIfc atom : atomsInSEgment) {
            assertTrue(atom.getBonds() != null && atom.getBonds().length != 0);
            for (MyBondIfc bond : atom.getBonds()) {
                assertTrue(atomsInSEgment.contains(bond.getBondedAtom()));
            }
        }

        MyMonomerIfc firstMonomer = segmentOfChain.getMyMonomers()[0];
        MyMonomerIfc lastMonomer = segmentOfChain.getMyMonomers()[segmentOfChain.getMyMonomers().length - 1];
        MyAtomIfc nTerminal = firstMonomer.getMyAtomFromMyAtomName("N".toCharArray());
        MyAtomIfc cTerminal = lastMonomer.getMyAtomFromMyAtomName("C".toCharArray());
        // Nterminal has only one bon to CA of the same monomer
        assertTrue(nTerminal.getBonds().length == 1);
        assertTrue(nTerminal.getBonds()[0].getBondedAtom().getParent() == nTerminal.getParent());
        assertArrayEquals(nTerminal.getBonds()[0].getBondedAtom().getAtomName(), "CA".toCharArray());
        // Cterminal has two bonds: to O and to CA of the same monomer
        assertTrue(cTerminal.getBonds().length == 2);
        MyBondIfc[] bonds = cTerminal.getBonds();

    }


    @Test
    public void testMethodfindTipsSegmentOfChain() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 1);

        String fourLetterCode = "2yjd";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure, EnumMyReaderBiojava.BioJava_MMCIFF);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
            assertTrue(false);
        }

        // Chain C is a peptide bound and has 13 monomers
        char[] chainId = "C".toCharArray();
        MyChainIfc wholeChain = mystructure.getAminoMyChain(chainId);
        MyChainIfc ligand = null;
        int startingRankId = 0;
        int peptideLength = 4;
        int tipMonoMerDistance = 2;
        // List<MyMonomerIfc> tipMonomers = StructureLocalTools.findTipsSegmentOfChain(wholeChain, ligand, startingRankId, peptideLength, tipMonoMerDistance);
        System.out.println();

    }

}
