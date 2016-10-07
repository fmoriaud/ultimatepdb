package myjmol;

import convertformat.AdapterBioJavaStructure;
import convertformat.ExceptionInConvertFormat;
import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import io.BiojavaReader;
import io.ExceptionInIOPackage;
import io.Tools;
import mystructure.*;
import org.biojava.nbio.structure.Structure;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;
import ultiJmol1462.Protonate;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 28/09/16.
 */
public class ProtonateTest {

    @Test
    public void testProteinStructureWhichIsAlreadyProtonated() throws IOException, ParsingConfigFileException {


        String fourLetterCode = "2n0u";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 1);

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure, EnumMyReaderBiojava.BioJava_MMCIFF);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
            assertTrue(false);
        }

        Protonate protonate = new Protonate(mystructure, algoParameters);
        try {
            protonate.compute();
        } catch (ExceptionInScoringUsingBioJavaJMolGUI exceptionInScoringUsingBioJavaJMolGUI) {
            exceptionInScoringUsingBioJavaJMolGUI.printStackTrace();
        }
        MyStructureIfc protonatedMyStructure = protonate.getProtonatedMyStructure();

        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 1);
        try {
            algoParameters.ultiJMolBuffer.get().frame.dispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 0);

        MyChainIfc chainBeforeProtonation = mystructure.getAminoMyChain("A".toCharArray());
        MyMonomerIfc gln23BeforeProtonation = chainBeforeProtonation.getMyMonomerFromResidueId(23);
        assertTrue(gln23BeforeProtonation.getMyAtoms().length == 9);

        MyChainIfc chainAfterProtonation = protonatedMyStructure.getAminoMyChain("A".toCharArray());
        MyMonomerIfc gln23AfterProtonation = chainAfterProtonation.getMyMonomerFromResidueId(23);
        assertTrue(gln23AfterProtonation.getMyAtoms().length == 18);

    }



    @Test
    public void testRNADNA() throws IOException, ParsingConfigFileException {

        String fourLetterCode = "394d";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 1);

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure, EnumMyReaderBiojava.BioJava_MMCIFF);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
            assertTrue(false);
        }

        Protonate protonate = new Protonate(mystructure, algoParameters);
        try {
            protonate.compute();
        } catch (ExceptionInScoringUsingBioJavaJMolGUI exceptionInScoringUsingBioJavaJMolGUI) {
            exceptionInScoringUsingBioJavaJMolGUI.printStackTrace();
        }
        MyStructureIfc protonatedMyStructure = protonate.getProtonatedMyStructure();

        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 1);
        try {
            algoParameters.ultiJMolBuffer.get().frame.dispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 0);

        MyChainIfc chainBeforeProtonation = mystructure.getNucleosideChain("B".toCharArray());
        MyMonomerIfc dc11BBeforeProtonation = chainBeforeProtonation.getMyMonomerFromResidueId(11);
        assertTrue(dc11BBeforeProtonation.getMyAtoms().length == 16);

        MyChainIfc chainAfterProtonation = protonatedMyStructure.getNucleosideChain("B".toCharArray());
        MyMonomerIfc dc11BAfterProtonation = chainAfterProtonation.getMyMonomerFromResidueId(11);
        assertTrue(dc11BAfterProtonation.getMyAtoms().length == 29);

    }



    @Test
    public void testProtonateStructureWhichHasAHetAtomGroupThatWasInsertedInchain() throws IOException, ParsingConfigFileException {

        String fourLetterCode = "5b59";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 1);

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure, EnumMyReaderBiojava.BioJava_MMCIFF);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
            assertTrue(false);
        }

        Protonate protonate = new Protonate(mystructure, algoParameters);
        try {
            protonate.compute();
        } catch (ExceptionInScoringUsingBioJavaJMolGUI exceptionInScoringUsingBioJavaJMolGUI) {
            exceptionInScoringUsingBioJavaJMolGUI.printStackTrace();
        }
        MyStructureIfc protonatedMyStructure = protonate.getProtonatedMyStructure();

        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 1);
        try {
            algoParameters.ultiJMolBuffer.get().frame.dispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 0);

        MyChainIfc chainBeforeProtonation = mystructure.getAminoMyChain("A".toCharArray());
        MyMonomerIfc kto201BeforeProtonation = chainBeforeProtonation.getMyMonomerFromResidueId(201);
        assertTrue(kto201BeforeProtonation.getMyAtoms().length == 26);

        MyChainIfc chainAfterProtonation = protonatedMyStructure.getAminoMyChain("A".toCharArray());
        MyMonomerIfc kto201AfterProtonation = chainAfterProtonation.getMyMonomerFromResidueId(201);
        assertTrue(kto201AfterProtonation.getMyAtoms().length == 49);

        // Test if atom names are correct
        MyAtomIfc atomH1C15 = kto201AfterProtonation.getMyAtomFromMyAtomName("H1C15".toCharArray());
        MyAtomIfc atomH2C15 = kto201AfterProtonation.getMyAtomFromMyAtomName("H2C15".toCharArray());
        assertTrue(atomH1C15 != null);
        assertTrue(atomH2C15 != null);
    }
}
