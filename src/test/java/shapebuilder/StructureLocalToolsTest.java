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
import parameters.QueryAtomDefinedByIds;
import protocols.ParsingConfigFileException;
import shapeBuilder.ShapeBuildingException;
import shapeBuilder.StructureLocalToBuildAnyShape;
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


    // TODO what do we expect for neighbors in ligand, in structurelocal ??

    @Test
    public void testSegmentOfChain() throws IOException, ParsingConfigFileException {

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
            myStructureGlobalBrut = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure, EnumMyReaderBiojava.BioJava_MMCIFF);
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
            myStructureGlobalBrut = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure, EnumMyReaderBiojava.BioJava_MMCIFF);
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
            myStructureGlobalBrut = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure, EnumMyReaderBiojava.BioJava_MMCIFF);
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
        System.out.println();
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
            myStructureGlobalBrut = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure, EnumMyReaderBiojava.BioJava_MMCIFF);
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
        System.out.println();
    }


    public void testAtomIdsWithChainToIgnore() throws IOException, ParsingConfigFileException {

    }
}
