/*
Author:
      Fabrice Moriaud <fmoriaud@ultimatepdb.org>

  Copyright (c) 2016 Fabrice Moriaud

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
  */
package io;

import org.biojava.nbio.structure.ExperimentalTechnique;
import org.biojava.nbio.structure.Structure;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;

import java.io.IOException;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class BiojavaReaderFromPathProteinFromVariousExperimentMethods {


    @Test
    public void testReadFromResourcesProteinSolutionNMR() throws ParsingConfigFileException, IOException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        String fourLetterCode = "2n8y";
        BiojavaReader reader = new BiojavaReader(algoParameters);
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
    public void testReadFromResourcesProteinSolidstateNMR() throws ParsingConfigFileException, IOException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        String fourLetterCode = "2n3d";
        BiojavaReader reader = new BiojavaReader(algoParameters);
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
    public void testReadFromResourcesProteinElectronMicroscopy() throws ParsingConfigFileException, IOException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        String fourLetterCode = "5irz";
        BiojavaReader reader = new BiojavaReader(algoParameters);
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

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        String fourLetterCode = "5e5j";
        BiojavaReader reader = new BiojavaReader(algoParameters);
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

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        String fourLetterCode = "2zwh";
        BiojavaReader reader = new BiojavaReader(algoParameters);
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

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();

        String fourLetterCode = "5ebj";
        BiojavaReader reader = new BiojavaReader(algoParameters);
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
