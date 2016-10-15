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
import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 11/10/16.
 */
public class StructureLocalToolsTest {

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
        boolean caFound = false;
        boolean oFound = false;
        for (MyBondIfc bond : bonds) {
            if (Arrays.equals(bond.getBondedAtom().getAtomName(), "CA".toCharArray())) {
                caFound = true;
            }
            if (Arrays.equals(bond.getBondedAtom().getAtomName(), "O".toCharArray())) {
                if (bond.getBondOrder() == 2) {
                    oFound = true;
                }
            }
        }
        assertTrue(caFound);
        assertTrue(oFound);
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
