package io;

import org.biojava.nbio.structure.ExperimentalTechnique;
import org.biojava.nbio.structure.Structure;
import org.junit.Ignore;
import org.junit.Test;
import protocols.ParsingConfigFileException;

import java.io.IOException;
import java.net.URL;
import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 12/09/16.
 */
public class BiojavaReaderFromPathProteinFromVariousExperimentMethods {


    @Test
    public void testReadFromResourcesProteinSolutionNMR() throws ParsingConfigFileException {

        URL url = BiojavaReaderFromPathToMmcifFileTest.class.getClassLoader().getResource("2n8y.cif.gz");
        Structure mmcifStructure = null;
        try {
            mmcifStructure = Tools.getStructure(url);
        } catch (IOException e) {
            assertTrue(false);
        }
        Set<ExperimentalTechnique> expTechniques = mmcifStructure.getPDBHeader().getExperimentalTechniques();
        assertTrue(expTechniques.size() == 1);
        assertTrue(expTechniques.contains(ExperimentalTechnique.SOLUTION_NMR));
    }


    @Test
    public void testReadFromResourcesProteinSolidstateNMR() throws ParsingConfigFileException {

        URL url = BiojavaReaderFromPathToMmcifFileTest.class.getClassLoader().getResource("2n3d.cif.gz");
        Structure mmcifStructure = null;
        try {
            mmcifStructure = Tools.getStructure(url);
        } catch (IOException e) {
            assertTrue(false);
        }
        Set<ExperimentalTechnique> expTechniques = mmcifStructure.getPDBHeader().getExperimentalTechniques();
        assertTrue(expTechniques.size() == 1);
        assertTrue(expTechniques.contains(ExperimentalTechnique.SOLID_STATE_NMR));

    }


    @Test
    public void testReadFromResourcesProteinElectronMicroscopy() throws ParsingConfigFileException {
        // 5fwm is not working
        URL url = BiojavaReaderFromPathToMmcifFileTest.class.getClassLoader().getResource("5irz.cif.gz");
        Structure mmcifStructure = null;
        try {
            mmcifStructure = Tools.getStructure(url);

        } catch (IOException | NullPointerException e) {
            //assertTrue(false);
        }
        Set<ExperimentalTechnique> expTechniques = mmcifStructure.getPDBHeader().getExperimentalTechniques();
        assertTrue(expTechniques.size() == 1);
        assertTrue(expTechniques.contains(ExperimentalTechnique.ELECTRON_MICROSCOPY));

    }


    @Test
    public void testReadFromResourcesProteinElectronCrystallography() throws ParsingConfigFileException {

        URL url = BiojavaReaderFromPathToMmcifFileTest.class.getClassLoader().getResource("4d0a.cif.gz");
        Structure mmcifStructure = null;
        try {
            mmcifStructure = Tools.getStructure(url);
        } catch (IOException e) {
            assertTrue(false);
        }
        Set<ExperimentalTechnique> expTechniques = mmcifStructure.getPDBHeader().getExperimentalTechniques();
        assertTrue(expTechniques.size() == 1);
        assertTrue(expTechniques.contains(ExperimentalTechnique.ELECTRON_CRYSTALLOGRAPHY));
    }


    @Test
    public void testReadFromResourcesSolutionScattering() throws ParsingConfigFileException {

        URL url = BiojavaReaderFromPathToMmcifFileTest.class.getClassLoader().getResource("2n5t.cif.gz");
        Structure mmcifStructure = null;
        try {
            mmcifStructure = Tools.getStructure(url);
        } catch (IOException e) {
            assertTrue(false);
        }
        Set<ExperimentalTechnique> expTechniques = mmcifStructure.getPDBHeader().getExperimentalTechniques();
        assertTrue(expTechniques.size() == 2);
        assertTrue(expTechniques.contains(ExperimentalTechnique.SOLUTION_SCATTERING));
        assertTrue(expTechniques.contains(ExperimentalTechnique.SOLUTION_NMR));
    }


    @Test
    public void testReadFromResourcesPowderDiffraction() throws ParsingConfigFileException {

        URL url = BiojavaReaderFromPathToMmcifFileTest.class.getClassLoader().getResource("2o2w.cif.gz");
        Structure mmcifStructure = null;
        try {
            mmcifStructure = Tools.getStructure(url);
        } catch (IOException e) {
            assertTrue(false);
        }
        Set<ExperimentalTechnique> expTechniques = mmcifStructure.getPDBHeader().getExperimentalTechniques();
        assertTrue(expTechniques.size() == 1);
        assertTrue(expTechniques.contains(ExperimentalTechnique.POWDER_DIFFRACTION));
    }
}
