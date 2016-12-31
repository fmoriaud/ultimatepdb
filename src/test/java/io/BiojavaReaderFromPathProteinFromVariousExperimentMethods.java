package io;

import org.biojava.nbio.structure.ExperimentalTechnique;
import org.biojava.nbio.structure.Structure;
import org.junit.Test;
import protocols.ParsingConfigFileException;

import java.io.IOException;
import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 12/09/16.
 */
public class BiojavaReaderFromPathProteinFromVariousExperimentMethods {


    @Test
    public void testReadFromResourcesProteinSolutionNMR() throws ParsingConfigFileException {

        String fourLetterCode = "2n8y";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }

        Set<ExperimentalTechnique> expTechniques = mmcifStructure.getPDBHeader().getExperimentalTechniques();
        assertTrue(expTechniques.size() == 1);
        assertTrue(expTechniques.contains(ExperimentalTechnique.SOLUTION_NMR));
    }


    @Test
    public void testReadFromResourcesProteinSolidstateNMR() throws ParsingConfigFileException {

        String fourLetterCode = "2n3d";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }

        Set<ExperimentalTechnique> expTechniques = mmcifStructure.getPDBHeader().getExperimentalTechniques();
        assertTrue(expTechniques.size() == 1);
        assertTrue(expTechniques.contains(ExperimentalTechnique.SOLID_STATE_NMR));

    }


    @Test
    public void testReadFromResourcesProteinElectronMicroscopy() throws ParsingConfigFileException {

        String fourLetterCode = "5irz";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }

        Set<ExperimentalTechnique> expTechniques = mmcifStructure.getPDBHeader().getExperimentalTechniques();
        assertTrue(expTechniques.size() == 1);
        assertTrue(expTechniques.contains(ExperimentalTechnique.ELECTRON_MICROSCOPY));

    }


    @Test
    public void readFileNeutronDiffractionThatThrowABiojavaException() throws ParsingConfigFileException, IOException {

        String fourLetterCode = "5e5j";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }

        Set<ExperimentalTechnique> expTechniques = mmcifStructure.getPDBHeader().getExperimentalTechniques();
        assertTrue(expTechniques.size() == 2);
        assertTrue(expTechniques.contains(ExperimentalTechnique.XRAY_DIFFRACTION));
        assertTrue(expTechniques.contains(ExperimentalTechnique.NEUTRON_DIFFRACTION));
    }

    @Test
    public void readFileProteinFiberDiffractionThatThrowABiojavaException() throws ParsingConfigFileException, IOException {

        String fourLetterCode = "2zwh";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }

        Set<ExperimentalTechnique> expTechniques = mmcifStructure.getPDBHeader().getExperimentalTechniques();
        assertTrue(expTechniques.size() == 1);
        assertTrue(expTechniques.contains(ExperimentalTechnique.FIBER_DIFFRACTION));
    }

    @Test
    public void readFileHybridThatThrowABiojavaException() throws ParsingConfigFileException, IOException {

        String fourLetterCode = "5ebj";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }

        Set<ExperimentalTechnique> expTechniques = mmcifStructure.getPDBHeader().getExperimentalTechniques();
        assertTrue(expTechniques.size() == 2);
        assertTrue(expTechniques.contains(ExperimentalTechnique.XRAY_DIFFRACTION));
        assertTrue(expTechniques.contains(ExperimentalTechnique.NEUTRON_DIFFRACTION));
    }
   
}
