package shapebuilder;

import convertformat.AdapterBioJavaStructure;
import convertformat.ExceptionInConvertFormat;
import io.BiojavaReader;
import io.ExceptionInIOPackage;
import io.IOTools;
import io.Tools;
import mystructure.*;
import org.biojava.nbio.structure.Structure;
import org.junit.Ignore;
import org.junit.Test;
import parameters.AlgoParameters;
import parameters.QueryAtomDefinedByIds;
import protocols.ParsingConfigFileException;
import protocols.ShapeContainerFactory;
import shape.ShapeContainerIfc;
import shapeBuilder.EnumShapeReductor;
import shapeBuilder.ShapeBuildingException;
import shapeBuilder.StructureLocalToBuildAnyShape;
import shapeBuilder.StructureLocalTools;
import shapeCompare.CompareTools;
import shapeCompare.ResultsFromEvaluateCost;

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


    // TODO what do we expect for neighbors in ligand, in structurelocal ??

    @Test
    public void testSegmentOfChainSimpleCase() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

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
            myStructureGlobalBrut = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
            assertTrue(false);
        }

        char[] chainId = "X".toCharArray();
        int rankIdinChain = 2;
        int peptideLength = 3;


        StructureLocalToBuildAnyShape structureLocalToBuildAnyShape = null;
        try {
            structureLocalToBuildAnyShape = new StructureLocalToBuildAnyShape(myStructureGlobalBrut, chainId, rankIdinChain, peptideLength, algoParameters);
        } catch (ShapeBuildingException e) {
            assertTrue(false);
        }
        MyStructureIfc structureLocal = structureLocalToBuildAnyShape.getMyStructureLocal();

        // assume it is ok like it is
        assertTrue(structureLocal.getAminoMyChain("A".toCharArray()).getMyMonomers().length == 30);
        assertTrue(structureLocal.getAminoMyChain("X".toCharArray()).getMyMonomers().length == 6);
        assertTrue(structureLocal.getAminoMyChain("X".toCharArray()).getMyMonomerFromResidueId(3) == null);
        assertTrue(structureLocal.getAminoMyChain("X".toCharArray()).getMyMonomerFromResidueId(4) == null);
        assertTrue(structureLocal.getAminoMyChain("X".toCharArray()).getMyMonomerFromResidueId(5) == null);
        assertTrue(structureLocal.getAminoMyChain("X".toCharArray()).getMyMonomerFromResidueId(1) != null);
        assertTrue(structureLocal.getAminoMyChain("X".toCharArray()).getMyMonomerFromResidueId(2) != null);
        assertTrue(structureLocal.getAminoMyChain("X".toCharArray()).getMyMonomerFromResidueId(6) != null);
        assertTrue(structureLocal.getAminoMyChain("X".toCharArray()).getMyMonomerFromResidueId(7) != null);
        assertTrue(structureLocal.getAminoMyChain("X".toCharArray()).getMyMonomerFromResidueId(8) != null);
        assertTrue(structureLocal.getAminoMyChain("X".toCharArray()).getMyMonomerFromResidueId(9) != null);


        // Test deletion of atoms on StructureLocal
        MyMonomerIfc monomerOnLeftStructureLocal = structureLocal.getAminoMyChain("X".toCharArray()).getMyMonomerFromResidueId(2);
        assertTrue(monomerOnLeftStructureLocal.getMyAtomFromMyAtomName("C".toCharArray()) == null);
        assertTrue(monomerOnLeftStructureLocal.getMyAtomFromMyAtomName("O".toCharArray()) == null);
        assertTrue(monomerOnLeftStructureLocal.getMyAtomFromMyAtomName("N".toCharArray()) != null);

        MyMonomerIfc monomerOnRightStructureLocal = structureLocal.getAminoMyChain("X".toCharArray()).getMyMonomerFromResidueId(6);
        assertTrue(monomerOnRightStructureLocal.getMyAtomFromMyAtomName("C".toCharArray()) != null);
        assertTrue(monomerOnRightStructureLocal.getMyAtomFromMyAtomName("O".toCharArray()) != null);
        assertTrue(monomerOnRightStructureLocal.getMyAtomFromMyAtomName("N".toCharArray()) == null);


        // test on monomersToDiscard: they must covert the MyMonomers from segment of chain
        // AND be in myStructureGlobalBrut if we want to reuse them in the same myStructureGlobalBrut
        List<MyMonomerIfc> monomersToDiscard = structureLocalToBuildAnyShape.getMonomerToDiscard();
        assertTrue(monomersToDiscard.size() == peptideLength);

        MyChainIfc ligandChain = myStructureGlobalBrut.getAminoMyChain(chainId);
        List<MyMonomerIfc> monomersFromLigandChain = MyStructureTools.makeListFromArray(ligandChain.getMyMonomers());
        for (MyMonomerIfc myMonomerToDiscard : monomersToDiscard) {
            assertTrue(monomersFromLigandChain.contains(myMonomerToDiscard));
        }

        // test on ligand
        MyChainIfc ligand = structureLocalToBuildAnyShape.getLigand();
        assertTrue(ligand.getMyMonomers().length == peptideLength);

        // Test deletion of atoms on ligand
        MyMonomerIfc monomerOnLeftLigand = ligand.getMyMonomerByRank(0);
        assertTrue(monomerOnLeftLigand.getMyAtomFromMyAtomName("C".toCharArray()) != null);
        assertTrue(monomerOnLeftLigand.getMyAtomFromMyAtomName("O".toCharArray()) != null);
        assertTrue(monomerOnLeftLigand.getMyAtomFromMyAtomName("N".toCharArray()) == null);

        MyMonomerIfc monomerOnRightLigand = ligand.getMyMonomerByRank(2);
        assertTrue(monomerOnRightLigand.getMyAtomFromMyAtomName("C".toCharArray()) == null);
        assertTrue(monomerOnRightLigand.getMyAtomFromMyAtomName("O".toCharArray()) == null);
        assertTrue(monomerOnRightLigand.getMyAtomFromMyAtomName("N".toCharArray()) != null);

        // Check peptide bonds in between 0 and 1
        MyAtomIfc n1 = ligand.getMyMonomerByRank(1).getMyAtomFromMyAtomName("N".toCharArray());
        MyAtomIfc c0 = ligand.getMyMonomerByRank(0).getMyAtomFromMyAtomName("C".toCharArray());
        boolean foundPeptideBond = false;
        for (MyBondIfc bond : n1.getBonds()) {
            if (bond.getBondedAtom() == c0) {
                foundPeptideBond = true;
            }
        }
        assertTrue(foundPeptideBond);

        MyAtomIfc n2 = ligand.getMyMonomerByRank(2).getMyAtomFromMyAtomName("N".toCharArray());
        MyAtomIfc c1 = ligand.getMyMonomerByRank(1).getMyAtomFromMyAtomName("C".toCharArray());
        foundPeptideBond = false;
        for (MyBondIfc bond : n2.getBonds()) {
            if (bond.getBondedAtom() == c1) {
                foundPeptideBond = true;
            }
        }
        assertTrue(foundPeptideBond);
    }


    @Test
    public void testSegmentOfChainAnotherSimpleCase() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        String fourLetterCode = "1be9";
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
            myStructureGlobalBrut = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
            assertTrue(false);
        }

        char[] chainId = "B".toCharArray();
        int rankIdinChain = 1;
        int peptideLength = 3;


        StructureLocalToBuildAnyShape structureLocalToBuildAnyShape = null;
        try {
            structureLocalToBuildAnyShape = new StructureLocalToBuildAnyShape(myStructureGlobalBrut, chainId, rankIdinChain, peptideLength, algoParameters);
        } catch (ShapeBuildingException e) {
            assertTrue(false);
        }
        MyStructureIfc structureLocal = structureLocalToBuildAnyShape.getMyStructureLocal();

        // assume it is ok like it is
        assertTrue(structureLocal.getAminoMyChain("A".toCharArray()).getMyMonomers().length == 31);
        assertTrue(structureLocal.getAminoMyChain("B".toCharArray()).getMyMonomers().length == 2);
        assertTrue(structureLocal.getAminoMyChain("B".toCharArray()).getMyMonomerFromResidueId(6) == null);
        assertTrue(structureLocal.getAminoMyChain("B".toCharArray()).getMyMonomerFromResidueId(7) == null);
        assertTrue(structureLocal.getAminoMyChain("B".toCharArray()).getMyMonomerFromResidueId(8) == null);
        assertTrue(structureLocal.getAminoMyChain("B".toCharArray()).getMyMonomerFromResidueId(5) != null);
        assertTrue(structureLocal.getAminoMyChain("B".toCharArray()).getMyMonomerFromResidueId(9) != null);


        // Test deletion of atoms on StructureLocal
        MyMonomerIfc monomerOnLeftStructureLocal = structureLocal.getAminoMyChain("B".toCharArray()).getMyMonomerFromResidueId(5);
        assertTrue(monomerOnLeftStructureLocal.getMyAtomFromMyAtomName("C".toCharArray()) == null);
        assertTrue(monomerOnLeftStructureLocal.getMyAtomFromMyAtomName("O".toCharArray()) == null);
        assertTrue(monomerOnLeftStructureLocal.getMyAtomFromMyAtomName("N".toCharArray()) != null);

        MyMonomerIfc monomerOnRightStructureLocal = structureLocal.getAminoMyChain("B".toCharArray()).getMyMonomerFromResidueId(9);
        assertTrue(monomerOnRightStructureLocal.getMyAtomFromMyAtomName("C".toCharArray()) != null);
        assertTrue(monomerOnRightStructureLocal.getMyAtomFromMyAtomName("O".toCharArray()) != null);
        assertTrue(monomerOnRightStructureLocal.getMyAtomFromMyAtomName("N".toCharArray()) == null);


        // test on monomersToDiscard: they must covert the MyMonomers from segment of chain
        // AND be in myStructureGlobalBrut if we want to reuse them in the same myStructureGlobalBrut
        List<MyMonomerIfc> monomersToDiscard = structureLocalToBuildAnyShape.getMonomerToDiscard();
        assertTrue(monomersToDiscard.size() == peptideLength);

        MyChainIfc ligandChain = myStructureGlobalBrut.getAminoMyChain(chainId);
        List<MyMonomerIfc> monomersFromLigandChain = MyStructureTools.makeListFromArray(ligandChain.getMyMonomers());
        for (MyMonomerIfc myMonomerToDiscard : monomersToDiscard) {
            assertTrue(monomersFromLigandChain.contains(myMonomerToDiscard));
        }

        // test on ligand
        MyChainIfc ligand = structureLocalToBuildAnyShape.getLigand();
        assertTrue(ligand.getMyMonomers().length == peptideLength);

        // Test deletion of atoms on ligand
        MyMonomerIfc monomerOnLeftLigand = ligand.getMyMonomerByRank(0);
        assertTrue(monomerOnLeftLigand.getMyAtomFromMyAtomName("C".toCharArray()) != null);
        assertTrue(monomerOnLeftLigand.getMyAtomFromMyAtomName("O".toCharArray()) != null);
        assertTrue(monomerOnLeftLigand.getMyAtomFromMyAtomName("N".toCharArray()) == null);

        MyMonomerIfc monomerOnRightLigand = ligand.getMyMonomerByRank(2);
        assertTrue(monomerOnRightLigand.getMyAtomFromMyAtomName("C".toCharArray()) == null);
        assertTrue(monomerOnRightLigand.getMyAtomFromMyAtomName("O".toCharArray()) == null);
        assertTrue(monomerOnRightLigand.getMyAtomFromMyAtomName("N".toCharArray()) != null);

        // Check peptide bonds in between 0 and 1
        MyAtomIfc n1 = ligand.getMyMonomerByRank(1).getMyAtomFromMyAtomName("N".toCharArray());
        MyAtomIfc c0 = ligand.getMyMonomerByRank(0).getMyAtomFromMyAtomName("C".toCharArray());
        boolean foundPeptideBond = false;
        for (MyBondIfc bond : n1.getBonds()) {
            if (bond.getBondedAtom() == c0) {
                foundPeptideBond = true;
            }
        }
        assertTrue(foundPeptideBond);

        MyAtomIfc n2 = ligand.getMyMonomerByRank(2).getMyAtomFromMyAtomName("N".toCharArray());
        MyAtomIfc c1 = ligand.getMyMonomerByRank(1).getMyAtomFromMyAtomName("C".toCharArray());
        foundPeptideBond = false;
        for (MyBondIfc bond : n2.getBonds()) {
            if (bond.getBondedAtom() == c1) {
                foundPeptideBond = true;
            }
        }
        assertTrue(foundPeptideBond);
    }


    @Test
    public void testSegmentOfChainSegmentstartsAtBeginingOfChain() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        String fourLetterCode = "1be9";
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
            myStructureGlobalBrut = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
            assertTrue(false);
        }

        char[] chainId = "B".toCharArray();
        int rankIdinChain = 0;
        int peptideLength = 3;


        StructureLocalToBuildAnyShape structureLocalToBuildAnyShape = null;
        try {
            structureLocalToBuildAnyShape = new StructureLocalToBuildAnyShape(myStructureGlobalBrut, chainId, rankIdinChain, peptideLength, algoParameters);
        } catch (ShapeBuildingException e) {
            assertTrue(false);
        }
        MyStructureIfc structureLocal = structureLocalToBuildAnyShape.getMyStructureLocal();

        // assume it is ok like it is
        assertTrue(structureLocal.getAminoMyChain("A".toCharArray()).getMyMonomers().length == 27);
        assertTrue(structureLocal.getAminoMyChain("B".toCharArray()).getMyMonomers().length == 2);
        assertTrue(structureLocal.getAminoMyChain("B".toCharArray()).getMyMonomerFromResidueId(5) == null);
        assertTrue(structureLocal.getAminoMyChain("B".toCharArray()).getMyMonomerFromResidueId(6) == null);
        assertTrue(structureLocal.getAminoMyChain("B".toCharArray()).getMyMonomerFromResidueId(7) == null);
        assertTrue(structureLocal.getAminoMyChain("B".toCharArray()).getMyMonomerFromResidueId(8) != null);
        assertTrue(structureLocal.getAminoMyChain("B".toCharArray()).getMyMonomerFromResidueId(9) != null);


        // Test deletion of atoms on StructureLocal
        // There should have been nothing on the left to be deleted as the fragment starts at the beginning of the chain


        MyMonomerIfc monomerOnRightStructureLocal = structureLocal.getAminoMyChain("B".toCharArray()).getMyMonomerFromResidueId(8);
        assertTrue(monomerOnRightStructureLocal.getMyAtomFromMyAtomName("C".toCharArray()) != null);
        assertTrue(monomerOnRightStructureLocal.getMyAtomFromMyAtomName("O".toCharArray()) != null);
        assertTrue(monomerOnRightStructureLocal.getMyAtomFromMyAtomName("N".toCharArray()) == null);

        // test on monomersToDiscard: they must covert the MyMonomers from segment of chain
        // AND be in myStructureGlobalBrut if we want to reuse them in the same myStructureGlobalBrut
        List<MyMonomerIfc> monomersToDiscard = structureLocalToBuildAnyShape.getMonomerToDiscard();
        assertTrue(monomersToDiscard.size() == peptideLength);

        MyChainIfc ligandChain = myStructureGlobalBrut.getAminoMyChain(chainId);
        List<MyMonomerIfc> monomersFromLigandChain = MyStructureTools.makeListFromArray(ligandChain.getMyMonomers());
        for (MyMonomerIfc myMonomerToDiscard : monomersToDiscard) {
            assertTrue(monomersFromLigandChain.contains(myMonomerToDiscard));
        }

        // test on ligand
        MyChainIfc ligand = structureLocalToBuildAnyShape.getLigand();
        assertTrue(ligand.getMyMonomers().length == peptideLength);

        // Test deletion of atoms on ligand
        MyMonomerIfc monomerOnLeftLigand = ligand.getMyMonomerByRank(0);
        assertTrue(monomerOnLeftLigand.getMyAtomFromMyAtomName("C".toCharArray()) != null);
        assertTrue(monomerOnLeftLigand.getMyAtomFromMyAtomName("O".toCharArray()) != null);
        assertTrue(monomerOnLeftLigand.getMyAtomFromMyAtomName("N".toCharArray()) != null);

        MyMonomerIfc monomerOnRightLigand = ligand.getMyMonomerByRank(2);
        assertTrue(monomerOnRightLigand.getMyAtomFromMyAtomName("C".toCharArray()) == null);
        assertTrue(monomerOnRightLigand.getMyAtomFromMyAtomName("O".toCharArray()) == null);
        assertTrue(monomerOnRightLigand.getMyAtomFromMyAtomName("N".toCharArray()) != null);

        // Check peptide bonds in between 0 and 1
        MyAtomIfc n1 = ligand.getMyMonomerByRank(1).getMyAtomFromMyAtomName("N".toCharArray());
        MyAtomIfc c0 = ligand.getMyMonomerByRank(0).getMyAtomFromMyAtomName("C".toCharArray());
        boolean foundPeptideBond = false;
        for (MyBondIfc bond : n1.getBonds()) {
            if (bond.getBondedAtom() == c0) {
                foundPeptideBond = true;
            }
        }
        assertTrue(foundPeptideBond);

        MyAtomIfc n2 = ligand.getMyMonomerByRank(2).getMyAtomFromMyAtomName("N".toCharArray());
        MyAtomIfc c1 = ligand.getMyMonomerByRank(1).getMyAtomFromMyAtomName("C".toCharArray());
        foundPeptideBond = false;
        for (MyBondIfc bond : n2.getBonds()) {
            if (bond.getBondedAtom() == c1) {
                foundPeptideBond = true;
            }
        }
        assertTrue(foundPeptideBond);
    }


    @Test
    public void testSegmentOfChainSegmentEndingAtEndOfChain() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        String fourLetterCode = "1be9";
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
            myStructureGlobalBrut = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
            assertTrue(false);
        }

        char[] chainId = "B".toCharArray();
        int rankIdinChain = 2;
        int peptideLength = 3;


        StructureLocalToBuildAnyShape structureLocalToBuildAnyShape = null;
        try {
            structureLocalToBuildAnyShape = new StructureLocalToBuildAnyShape(myStructureGlobalBrut, chainId, rankIdinChain, peptideLength, algoParameters);
        } catch (ShapeBuildingException e) {
            assertTrue(false);
        }
        MyStructureIfc structureLocal = structureLocalToBuildAnyShape.getMyStructureLocal();

        // assume it is ok like it is
        assertTrue(structureLocal.getAminoMyChain("A".toCharArray()).getMyMonomers().length == 35);
        assertTrue(structureLocal.getAminoMyChain("B".toCharArray()).getMyMonomers().length == 2);
        assertTrue(structureLocal.getAminoMyChain("B".toCharArray()).getMyMonomerFromResidueId(7) == null);
        assertTrue(structureLocal.getAminoMyChain("B".toCharArray()).getMyMonomerFromResidueId(8) == null);
        assertTrue(structureLocal.getAminoMyChain("B".toCharArray()).getMyMonomerFromResidueId(9) == null);
        assertTrue(structureLocal.getAminoMyChain("B".toCharArray()).getMyMonomerFromResidueId(5) != null);
        assertTrue(structureLocal.getAminoMyChain("B".toCharArray()).getMyMonomerFromResidueId(6) != null);


        // Test deletion of atoms on StructureLocal

        MyMonomerIfc monomerOnLeftStructureLocal = structureLocal.getAminoMyChain("B".toCharArray()).getMyMonomerFromResidueId(6);
        assertTrue(monomerOnLeftStructureLocal.getMyAtomFromMyAtomName("C".toCharArray()) == null);
        assertTrue(monomerOnLeftStructureLocal.getMyAtomFromMyAtomName("O".toCharArray()) == null);
        assertTrue(monomerOnLeftStructureLocal.getMyAtomFromMyAtomName("N".toCharArray()) != null);

        // There should have been nothing on the right to be deleted as the fragment ends at the end of the chain


        // test on monomersToDiscard: they must covert the MyMonomers from segment of chain
        // AND be in myStructureGlobalBrut if we want to reuse them in the same myStructureGlobalBrut
        List<MyMonomerIfc> monomersToDiscard = structureLocalToBuildAnyShape.getMonomerToDiscard();
        assertTrue(monomersToDiscard.size() == peptideLength);

        MyChainIfc ligandChain = myStructureGlobalBrut.getAminoMyChain(chainId);
        List<MyMonomerIfc> monomersFromLigandChain = MyStructureTools.makeListFromArray(ligandChain.getMyMonomers());
        for (MyMonomerIfc myMonomerToDiscard : monomersToDiscard) {
            assertTrue(monomersFromLigandChain.contains(myMonomerToDiscard));
        }

        // test on ligand
        MyChainIfc ligand = structureLocalToBuildAnyShape.getLigand();
        assertTrue(ligand.getMyMonomers().length == peptideLength);

        // Test deletion of atoms on ligand
        MyMonomerIfc monomerOnLeftLigand = ligand.getMyMonomerByRank(0);
        assertTrue(monomerOnLeftLigand.getMyAtomFromMyAtomName("C".toCharArray()) != null);
        assertTrue(monomerOnLeftLigand.getMyAtomFromMyAtomName("O".toCharArray()) != null);
        assertTrue(monomerOnLeftLigand.getMyAtomFromMyAtomName("N".toCharArray()) == null);

        MyMonomerIfc monomerOnRightLigand = ligand.getMyMonomerByRank(2);
        assertTrue(monomerOnRightLigand.getMyAtomFromMyAtomName("C".toCharArray()) != null);
        assertTrue(monomerOnRightLigand.getMyAtomFromMyAtomName("O".toCharArray()) != null);
        assertTrue(monomerOnRightLigand.getMyAtomFromMyAtomName("N".toCharArray()) != null);

        // Check peptide bonds in between 0 and 1
        MyAtomIfc n1 = ligand.getMyMonomerByRank(1).getMyAtomFromMyAtomName("N".toCharArray());
        MyAtomIfc c0 = ligand.getMyMonomerByRank(0).getMyAtomFromMyAtomName("C".toCharArray());
        boolean foundPeptideBond = false;
        for (MyBondIfc bond : n1.getBonds()) {
            if (bond.getBondedAtom() == c0) {
                foundPeptideBond = true;
            }
        }
        assertTrue(foundPeptideBond);

        MyAtomIfc n2 = ligand.getMyMonomerByRank(2).getMyAtomFromMyAtomName("N".toCharArray());
        MyAtomIfc c1 = ligand.getMyMonomerByRank(1).getMyAtomFromMyAtomName("C".toCharArray());
        foundPeptideBond = false;
        for (MyBondIfc bond : n2.getBonds()) {
            if (bond.getBondedAtom() == c1) {
                foundPeptideBond = true;
            }
        }
        assertTrue(foundPeptideBond);
    }


    @Test
    public void testSegmentOfChainThatCoversTheWholeChain() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        String fourLetterCode = "1be9";
        char[] chainId = "B".toCharArray();
        int rankIdinChain = 0;
        int peptideLength = 5;

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
            myStructureGlobalBrut = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
            assertTrue(false);
        }


        StructureLocalToBuildAnyShape structureLocalToBuildAnyShape = null;
        try {
            structureLocalToBuildAnyShape = new StructureLocalToBuildAnyShape(myStructureGlobalBrut, chainId, rankIdinChain, peptideLength, algoParameters);
        } catch (ShapeBuildingException e) {
            assertTrue(false);
        }
        MyStructureIfc structureLocal = structureLocalToBuildAnyShape.getMyStructureLocal();

        assertTrue(structureLocal.getAminoMyChain("A".toCharArray()).getMyMonomers().length == 38);
        assertTrue(structureLocal.getAminoMyChain("B".toCharArray()) == null);

        // test on monomersToDiscard: they must covert the MyMonomers from segment of chain
        // AND be in myStructureGlobalBrut if we want to reuse them in the same myStructureGlobalBrut
        List<MyMonomerIfc> monomersToDiscard = structureLocalToBuildAnyShape.getMonomerToDiscard();
        assertTrue(monomersToDiscard.size() == peptideLength);


        // test on ligand
        MyChainIfc ligand = structureLocalToBuildAnyShape.getLigand();
        assertTrue(ligand.getMyMonomers().length == peptideLength);

        // Test deletion of atoms on ligand
        MyMonomerIfc monomerOnLeftLigand = ligand.getMyMonomerByRank(0);
        assertTrue(monomerOnLeftLigand.getMyAtomFromMyAtomName("C".toCharArray()) != null);
        assertTrue(monomerOnLeftLigand.getMyAtomFromMyAtomName("O".toCharArray()) != null);
        assertTrue(monomerOnLeftLigand.getMyAtomFromMyAtomName("N".toCharArray()) != null);

        MyMonomerIfc monomerOnRightLigand = ligand.getMyMonomerByRank(4);
        assertTrue(monomerOnRightLigand.getMyAtomFromMyAtomName("C".toCharArray()) != null);
        assertTrue(monomerOnRightLigand.getMyAtomFromMyAtomName("O".toCharArray()) != null);
        assertTrue(monomerOnRightLigand.getMyAtomFromMyAtomName("N".toCharArray()) != null);

        // Check peptide bonds
        for (int i=0; i<peptideLength-1; i++){
            MyAtomIfc n1 = ligand.getMyMonomerByRank(i+1).getMyAtomFromMyAtomName("N".toCharArray());
            MyAtomIfc c0 = ligand.getMyMonomerByRank(i).getMyAtomFromMyAtomName("C".toCharArray());
            boolean foundPeptideBond = false;
            for (MyBondIfc bond : n1.getBonds()) {
                if (bond.getBondedAtom() == c0) {
                    foundPeptideBond = true;
                }
            }
            assertTrue(foundPeptideBond);
        }

        System.out.println();
    }



    @Test
    public void testWholeChain() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        String fourLetterCode = "2yjd";
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
            myStructureGlobalBrut = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
            assertTrue(false);
        }

        char[] chainId = "C".toCharArray();


        StructureLocalToBuildAnyShape structureLocalToBuildAnyShape = null;
        try {
            structureLocalToBuildAnyShape = new StructureLocalToBuildAnyShape(myStructureGlobalBrut, chainId, algoParameters);
        } catch (ShapeBuildingException e) {
            assertTrue(false);
        }

        // test structureLocal
        MyStructureIfc structureLocal = structureLocalToBuildAnyShape.getMyStructureLocal();

        assertTrue(structureLocal.getAminoMyChain("A".toCharArray()).getMyMonomers().length == 32);
        assertTrue(structureLocal.getAminoMyChain("C".toCharArray()) == null);


        // test monomersToDiscard
        List<MyMonomerIfc> monomersToDiscard = structureLocalToBuildAnyShape.getMonomerToDiscard();
        assertTrue(monomersToDiscard.size() == myStructureGlobalBrut.getAminoMyChain("C".toCharArray()).getMyMonomers().length);

        for (MyMonomerIfc monomer : myStructureGlobalBrut.getAminoMyChain("C".toCharArray()).getMyMonomers()) {
            assertTrue(monomersToDiscard.contains(monomer));
        }

        // test on ligand
        MyChainIfc ligand = structureLocalToBuildAnyShape.getLigand();
        assertTrue(ligand.getMyMonomers().length == myStructureGlobalBrut.getAminoMyChain("C".toCharArray()).getMyMonomers().length);
    }


    @Test
    public void testHetatm() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        String fourLetterCode = "1di9";
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
            myStructureGlobalBrut = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
            assertTrue(false);
        }

        char[] hetAtomsLigandId = "MSQ".toCharArray();
        int occurrenceId = 1;

        StructureLocalToBuildAnyShape structureLocalToBuildAnyShape = null;
        try {
            structureLocalToBuildAnyShape = new StructureLocalToBuildAnyShape(myStructureGlobalBrut, hetAtomsLigandId, occurrenceId, algoParameters);
        } catch (ShapeBuildingException e) {
            assertTrue(false);
        }
        MyStructureIfc structureLocal = structureLocalToBuildAnyShape.getMyStructureLocal();

        // test structureLocal
        assertTrue(structureLocal.getAminoMyChain("A".toCharArray()).getMyMonomers().length == 39);

        // test monomersToDiscard
        List<MyMonomerIfc> monomersToDiscard = structureLocalToBuildAnyShape.getMonomerToDiscard();
        assertTrue(monomersToDiscard.size() == 0);

        // test on ligand
        MyChainIfc ligand = structureLocalToBuildAnyShape.getLigand();
        assertTrue(ligand.getMyMonomers().length == 1);
    }


    @Test
    public void testAtomIds() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        String fourLetterCode = "1di9";
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
            myStructureGlobalBrut = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
            assertTrue(false);
        }

        List<QueryAtomDefinedByIds> listAtomDefinedByIds = new ArrayList<>();
        String chainQuery = "A";
        int residueId = 168;
        String atomName = "OD2";
        float radiusForQueryAtomsDefinedByIds = 8;
        QueryAtomDefinedByIds queryAtomDefinedByIds = new QueryAtomDefinedByIds(fourLetterCode, chainQuery, residueId, atomName, radiusForQueryAtomsDefinedByIds);
        listAtomDefinedByIds.add(queryAtomDefinedByIds);

        List<String> chainToIgnore = new ArrayList<>();
        StructureLocalToBuildAnyShape structureLocalToBuildAnyShape = null;
        try {
            structureLocalToBuildAnyShape = new StructureLocalToBuildAnyShape(myStructureGlobalBrut, listAtomDefinedByIds, algoParameters, chainToIgnore);
        } catch (ShapeBuildingException e) {
            assertTrue(false);
        }
        MyStructureIfc structureLocal = structureLocalToBuildAnyShape.getMyStructureLocal();

        // TODO problem the structure local contains the hetatm chain with MSQ
        // Should be fine as hetatm are not used in shape, except a few ones like HEM
    }


    @Test
    public void testAtomIdsWithChainToIgnore() throws IOException, ParsingConfigFileException {

    }


    @Test
    public void testForeignLigandFromStructureLocalWholeChain() throws IOException, ParsingConfigFileException, ShapeBuildingException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        int initialCount = algoParameters.ultiJMolBuffer.getSize();

        // need a foreign ligand which is defined as a MyStructure
        // This ligand is built with cloning so the neighbors from original structure are kept
        char[] fourLetterCodeTarget = "2ce8".toCharArray();
        char[] chainIdTarget = "X".toCharArray();
        MyStructureIfc myStructureTarget = IOTools.getMyStructureIfc(algoParameters, fourLetterCodeTarget);
        MyChainIfc foreignLigandChain = myStructureTarget.getAminoMyChain(chainIdTarget);
        Cloner cloner = new Cloner(foreignLigandChain, algoParameters);
        MyStructureIfc foreignLigandFromTarget = cloner.getClone();


        char[] fourLetterCodeQuery = "2ce9".toCharArray();
        char[] chainIdQuery = "X".toCharArray();
        MyStructureIfc myStructureQuery = IOTools.getMyStructureIfc(algoParameters, fourLetterCodeQuery);

        StructureLocalToBuildAnyShape structureLocalToBuildAnyShapeQuery = null;
        try {
            structureLocalToBuildAnyShapeQuery = new StructureLocalToBuildAnyShape(myStructureQuery, chainIdQuery, algoParameters);
        } catch (ShapeBuildingException e) {
            assertTrue(false);
        }
        // has 51 monomers
        MyStructureIfc structureLocalOriginalQuery = structureLocalToBuildAnyShapeQuery.getMyStructureLocal();
        assertTrue(structureLocalOriginalQuery.getAminoMyChain("A".toCharArray()).getMyMonomers().length == 51);
        assertTrue(structureLocalOriginalQuery.getAminoMyChain("X".toCharArray()) == null);

        // need to have foreignLigand in the reference frame of the query
        ShapeContainerIfc shapeTarget = ShapeContainerFactory.getShapeAroundAChain(EnumShapeReductor.CLUSTERING, myStructureTarget, algoParameters, chainIdTarget);
        ShapeContainerIfc shapeQuery = ShapeContainerFactory.getShapeAroundAChain(EnumShapeReductor.CLUSTERING, myStructureQuery, algoParameters, chainIdQuery);
        // So I compare shapeTarget to shapeLigand so the hit ligand can be put in reference frame of the target
        List<ResultsFromEvaluateCost> resultsPairingTriangleSeed = CompareTools.compareShapesBasedOnTriangles(shapeQuery, shapeTarget, algoParameters);
        MyStructureIfc rotatedLigandOrPeptide = CompareTools.getLigandOrPeptideInReferenceOfQuery(shapeTarget, resultsPairingTriangleSeed.get(0), algoParameters);

        // has 51 monomers: a bit bigger because protonation
        MyStructureIfc accessibleStructureLocal = shapeQuery.getMyStructureUsedToComputeShape();
        assertTrue(accessibleStructureLocal.getAminoMyChain("A".toCharArray()).getMyMonomers().length == 51);
        assertTrue(accessibleStructureLocal.getAminoMyChain("X".toCharArray()) == null);
        //System.out.println();

        // I get the monomer to exclude using the structureLocalToBuildAnyShapeQuery
        List<MyMonomerIfc> foreignMonomerToExclude = structureLocalToBuildAnyShapeQuery.getMonomerToDiscard();
        StructureLocalToBuildAnyShape structureLocalToBuildAnyShapeCustomLigand = new StructureLocalToBuildAnyShape(myStructureQuery, foreignMonomerToExclude, rotatedLigandOrPeptide, algoParameters);

        // test its structure local
        MyStructureIfc structureLocalCustomLigand = structureLocalToBuildAnyShapeCustomLigand.getMyStructureLocal();
        // test structureLocal
        // As hit ligand is longer, we get more amino acids in structureLocal
        // has 54 now ... 51 too now, was 60 ...
        assertTrue(structureLocalCustomLigand.getAminoMyChain("A".toCharArray()).getMyMonomers().length == 54);
        assertTrue(structureLocalCustomLigand.getAminoMyChain("X".toCharArray()) == null);
        // it is what it is but must be similar to accessibleStructureLocal

        // test its ligand
        MyChainIfc ligandCustom = structureLocalToBuildAnyShapeCustomLigand.getLigand();
        assertTrue(ligandCustom.getMyMonomers().length == 9);

        int finalCount = algoParameters.ultiJMolBuffer.getSize();
        assertTrue(finalCount == initialCount);
        try {
            for (int i = 0; i < initialCount; i++) {
                algoParameters.ultiJMolBuffer.get().frame.dispose();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 0);
    }
}
