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
package convertformat;

import io.BiojavaReader;
import io.ExceptionInIOPackage;
import io.Tools;
import mystructure.*;
import org.biojava.nbio.structure.Group;
import org.biojava.nbio.structure.GroupType;
import org.biojava.nbio.structure.Structure;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 17/09/16.
 */
public class AdapterBiojavaStructureVariousCheckCovalentHetatmInsertion {

    // PTR is L-Peptide so already integrated by Biojava
    @Test
    public void testconvertStructureToMyStructureWithPTRcovalentLigand() throws ParsingConfigFileException, IOException {

        String fourLetterCode = "2mrk";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
            assertTrue(false);
        }

        Group mmcifPTR = mmcifStructure.getChain(1).getAtomGroup(3);
        assertTrue(mmcifPTR.getPDBName().equals("PTR"));
        GroupType type = mmcifPTR.getType();
        assertTrue(type == GroupType.AMINOACID);
        MyMonomerIfc myStructurePTR = mystructure.getAminoMyChain("B".toCharArray()).getMyMonomerByRank(3);
        assertTrue(Arrays.equals(myStructurePTR.getThreeLetterCode(), "PTR".toCharArray()));
        assertTrue(Arrays.equals(myStructurePTR.getType(), MyMonomerType.AMINOACID.getType()));
        assertTrue(myStructurePTR.isWasHetatm() == false);
    }


    @Test
    public void testconvertStructureToMyStructureWithORGcovalentLigand() throws ParsingConfigFileException, IOException {

        String fourLetterCode = "3kw9";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
            assertTrue(false);
        }

        // ORG is integrated with cutoff bond distance 2.0 but not at 1.8.
        // I think it is better to integrate covalent ligand of any kind.
        // The problem is just if I want to build a query from the covalent one.
        // But is is maybe better to skip those ones as anyway they are not binding only with soft interaction and
        // potential covalent binding is out of the scope of ultimatepdb.
        // So that is nice that it is integrated to I put 2.0 A
        Group mmcifORG = mmcifStructure.getChain(0).getAtomGroup(215);
        assertTrue(mmcifORG.getPDBName().equals("ORG"));
        GroupType type = mmcifORG.getType();
        assertTrue(type == GroupType.HETATM);
        MyMonomerIfc myStructureORG = mystructure.getAminoMyChain("A".toCharArray()).getMyMonomerByRank(215);
        assertTrue(Arrays.equals(myStructureORG.getThreeLetterCode(), "ORG".toCharArray()));
        // ORG is moved, changed to AminoAcid type and the was Hetatm is set to true
        assertTrue(Arrays.equals(myStructureORG.getType(), MyMonomerType.AMINOACID.getType()));
        assertTrue(myStructureORG.isWasHetatm() == true);
    }


    @Test
    public void testconvertStructureToMyStructureWithcovalentPSOLigandToNucleosides() throws ParsingConfigFileException, IOException {

        String fourLetterCode = "203d";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
            assertTrue(false);
        }

        // PSO is integrated with cutoff bond distance 1.4but not at 1.6.
        // It looks really tightly bound in the structure

        Group mmcifPSO = mmcifStructure.getChain(0).getAtomGroup(8);
        assertTrue(mmcifPSO.getPDBName().equals("PSO"));
        GroupType type = mmcifPSO.getType();
        assertTrue(type == GroupType.HETATM);
        MyMonomerIfc myStructurePSO = mystructure.getNucleosideChain(("A").toCharArray()).getMyMonomerByRank(8);
        assertTrue(Arrays.equals(myStructurePSO.getThreeLetterCode(), "PSO".toCharArray()));
        // PSO is moved, changed to AminoAcid type and the was Hetatm is set to true
        assertTrue(Arrays.equals(myStructurePSO.getType(), MyMonomerType.AMINOACID.getType()));
        assertTrue(myStructurePSO.isWasHetatm() == true);
    }


    @Test
    public void testconvertStructureToMyStructureWithThreeUMPcovalentToNucleosides() throws ParsingConfigFileException, IOException {

        String fourLetterCode = "229d";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
            assertTrue(false);
        }

        // PSO is integrated with cutoff bond distance 1.4but not at 1.6.
        // It looks really tightly bound in the structure

        Group mmcifUMP1 = mmcifStructure.getChain(0).getAtomGroup(6);
        assertTrue(mmcifUMP1.getPDBName().equals("UMP"));
        GroupType type = mmcifUMP1.getType();
        assertTrue(type == GroupType.HETATM);

        Group mmcifUMP2 = mmcifStructure.getChain(0).getAtomGroup(12);
        assertTrue(mmcifUMP2.getPDBName().equals("UMP"));
        type = mmcifUMP2.getType();
        assertTrue(type == GroupType.HETATM);

        Group mmcifUMP3 = mmcifStructure.getChain(0).getAtomGroup(14);
        assertTrue(mmcifUMP3.getPDBName().equals("UMP"));
        type = mmcifUMP3.getType();
        assertTrue(type == GroupType.HETATM);

        MyMonomerIfc myStructureUMP1 = mystructure.getNucleosideChain(("A").toCharArray()).getMyMonomerByRank(6);
        assertTrue(Arrays.equals(myStructureUMP1.getThreeLetterCode(), "UMP".toCharArray()));
        assertTrue(Arrays.equals(myStructureUMP1.getType(), MyMonomerType.AMINOACID.getType()));
        assertTrue(myStructureUMP1.isWasHetatm() == true);

        MyMonomerIfc myStructureUMP2 = mystructure.getNucleosideChain(("A").toCharArray()).getMyMonomerByRank(12);
        assertTrue(Arrays.equals(myStructureUMP2.getThreeLetterCode(), "UMP".toCharArray()));
        assertTrue(Arrays.equals(myStructureUMP2.getType(), MyMonomerType.AMINOACID.getType()));
        assertTrue(myStructureUMP2.isWasHetatm() == true);

        MyMonomerIfc myStructureUMP3 = mystructure.getNucleosideChain(("A").toCharArray()).getMyMonomerByRank(14);
        assertTrue(Arrays.equals(myStructureUMP3.getThreeLetterCode(), "UMP".toCharArray()));
        assertTrue(Arrays.equals(myStructureUMP3.getType(), MyMonomerType.AMINOACID.getType()));
        assertTrue(myStructureUMP3.isWasHetatm() == true);

    }
}
