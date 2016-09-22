package myjmol;

import convertformat.AdapterBioJavaStructure;
import io.BiojavaReader;
import io.Tools;
import mystructure.*;
import org.biojava.nbio.structure.Structure;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;
import shapeBuilder.ShapeBuildingException;
import ultiJmol1462.MyJmolTools;

import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 11/09/16.
 */
public class MyJmolToolsProtonateTest {

    @Test
    public void testProtonateStructureWhichIsAlreadyProtonated() throws ParsingConfigFileException, IOException, ReadingStructurefileException, ExceptionInMyStructurePackage {

        String fourLetterCode = "2n0u";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException e) {
            assertTrue(false);
        }

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure, EnumMyReaderBiojava.BioJava_MMCIFF);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException e) {
            assertTrue(false);
        }

        MyStructureIfc protonatedStructure = null;
        try {
            protonatedStructure = MyJmolTools.protonateStructure(mystructure, algoParameters);
        } catch (ShapeBuildingException e) {
            assertTrue(false);
        }

        MyChainIfc chainBeforeProtonation = mystructure.getAminoMyChain("A".toCharArray());
        MyMonomerIfc gln23BeforeProtonation = chainBeforeProtonation.getMyMonomerFromResidueId(23);
        assertTrue(gln23BeforeProtonation.getMyAtoms().length == 9);

        MyChainIfc chainAfterProtonation = protonatedStructure.getAminoMyChain("A".toCharArray());
        MyMonomerIfc gln23AfterProtonation = chainAfterProtonation.getMyMonomerFromResidueId(23);
        assertTrue(gln23AfterProtonation.getMyAtoms().length == 18);
    }


    @Test
    public void testProtonateStructureRNADNA() throws ParsingConfigFileException, IOException, ReadingStructurefileException, ExceptionInMyStructurePackage {

        String fourLetterCode = "394d";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException e) {
            assertTrue(false);
        }

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure, EnumMyReaderBiojava.BioJava_MMCIFF);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException e) {
            assertTrue(false);
        }

        MyStructureIfc protonatedStructure = null;
        try {
            protonatedStructure = MyJmolTools.protonateStructure(mystructure, algoParameters);
        } catch (ShapeBuildingException e) {
            assertTrue(false);
        }

        MyChainIfc chainBeforeProtonation = mystructure.getNucleosideChain("B".toCharArray());
        MyMonomerIfc dc11BBeforeProtonation = chainBeforeProtonation.getMyMonomerFromResidueId(11);
        assertTrue(dc11BBeforeProtonation.getMyAtoms().length == 16);

        MyChainIfc chainAfterProtonation = protonatedStructure.getNucleosideChain("B".toCharArray());
        MyMonomerIfc dc11BAfterProtonation = chainAfterProtonation.getMyMonomerFromResidueId(11);
        assertTrue(dc11BAfterProtonation.getMyAtoms().length == 29);
    }



    @Test
    public void testProtonateStructureWhichHasAHetAtomGroupThatWasInsertedInchain() throws ParsingConfigFileException, IOException, ReadingStructurefileException, ExceptionInMyStructurePackage {

        String fourLetterCode = "5b59";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException e) {
            assertTrue(false);
        }

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure, EnumMyReaderBiojava.BioJava_MMCIFF);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException e) {
            assertTrue(false);
        }

        MyStructureIfc protonatedStructure = null;
        try {
            protonatedStructure = MyJmolTools.protonateStructure(mystructure, algoParameters);
        } catch (ShapeBuildingException e) {
            assertTrue(false);
        }

        MyChainIfc chainBeforeProtonation = mystructure.getAminoMyChain("A".toCharArray());
        MyMonomerIfc kto201BeforeProtonation = chainBeforeProtonation.getMyMonomerFromResidueId(201);
        assertTrue(kto201BeforeProtonation.getMyAtoms().length == 26);

        MyChainIfc chainAfterProtonation = protonatedStructure.getAminoMyChain("A".toCharArray());
        MyMonomerIfc kto201AfterProtonation = chainAfterProtonation.getMyMonomerFromResidueId(201);
        assertTrue(kto201AfterProtonation.getMyAtoms().length == 49);

        // Test if atom names are correct
        MyAtomIfc atomH1C15 = kto201AfterProtonation.getMyAtomFromMyAtomName("H1C15".toCharArray());
        MyAtomIfc atomH2C15 = kto201AfterProtonation.getMyAtomFromMyAtomName("H2C15".toCharArray());
        assertTrue(atomH1C15 != null);
        assertTrue(atomH2C15 != null);
    }
}
