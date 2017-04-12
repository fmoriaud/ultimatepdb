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

import database.HashTablesTools;
import io.BiojavaReader;
import io.ExceptionInIOPackage;
import io.IOTools;
import io.Tools;
import mystructure.*;
import org.apache.commons.math3.util.Pair;
import org.biojava.nbio.structure.Chain;
import org.biojava.nbio.structure.Group;
import org.biojava.nbio.structure.GroupType;
import org.biojava.nbio.structure.Structure;
import org.junit.Before;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

public class AdapterBioJavaStructureTest {

    @Before
    public void initialize() {
        Tools.class.toString();
    }

    /**
     * Test of the expected conversion os Structure to MyStructureIfc
     * H2O are discarded
     *
     * @throws ParsingConfigFileException
     * @throws IOException
     */
    @Test
    public void testconvertStructureToMyStructureProtein() throws ParsingConfigFileException, IOException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        String fourLetterCode = "1di9";
        Pair<String, MyStructureIfc> pathAndMyStructure = IOTools.getMyStructureIfc(algoParameters, fourLetterCode.toCharArray());

        // check content
        MyChainIfc[] myChains = pathAndMyStructure.getValue().getAllChains();
        int count = myChains.length;
        assertTrue(count == 2);

        MyChainIfc[] aminoMyChains = pathAndMyStructure.getValue().getAllAminochains();
        assertTrue(aminoMyChains.length == 1);
        MyChainIfc[] nucleosidesMyChains = pathAndMyStructure.getValue().getAllNucleosidechains();
        assertTrue(nucleosidesMyChains.length == 0);
        MyChainIfc[] heteroMyChains = pathAndMyStructure.getValue().getAllHetatmchains();
        assertTrue(heteroMyChains.length == 1);

        assertTrue(aminoMyChains[0].getMyMonomers().length == 348);
        assertTrue(heteroMyChains[0].getMyMonomers().length == 1);

        MyMonomerIfc expectedLigandMSQ = heteroMyChains[0].getMyMonomers()[0];
        assertArrayEquals(expectedLigandMSQ.getThreeLetterCode(), "MSQ".toCharArray());
        List<String> expectedSequence = new ArrayList<>(Arrays.asList("GLU", "ARG", "PRO", "THR", "PHE", "TYR", "ARG"));
        for (int i = 0; i < expectedSequence.size(); i++) {
            MyMonomerIfc currentMyMonomerIfc = aminoMyChains[0].getMyMonomers()[i];
            assertArrayEquals(currentMyMonomerIfc.getThreeLetterCode(), expectedSequence.get(i).toCharArray());
        }

        // check bonds

        for (MyChainIfc chain : pathAndMyStructure.getValue().getAllChains()) {
            MyMonomerIfc[] monomers = chain.getMyMonomers();
            for (MyMonomerIfc monomer : monomers) {
                for (MyAtomIfc atom : monomer.getMyAtoms()) {
                    assertTrue(atom.getBonds() != null);
                    assertTrue(atom.getBonds().length != 0);
                }
            }
        }
    }


    /**
     * Test of the expected conversion o Structure to MyStructureIfc. A Structure without Amino chains is not converted
     *
     * @throws ParsingConfigFileException
     * @throws IOException
     */
    @Test
    public void testconvertStructureToMyStructureDNARNAHybrid() throws ParsingConfigFileException, IOException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        String fourLetterCode = "394d";
        BiojavaReader reader = new BiojavaReader(algoParameters);
        Pair<String, Structure> pathAndmmcifStructure = null;
        try {
            pathAndmmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }
        String hash = null;
        try {
            hash = HashTablesTools.getMD5hash(pathAndmmcifStructure.getKey());
        } catch (NoSuchAlgorithmException e) {
            assertTrue(false);
        }

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(pathAndmmcifStructure.getValue(), hash);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
            assertTrue(e instanceof ReadingStructurefileException);
            assertTrue(e.getMessage().startsWith("Only empty amino chain were parsed for"));
        }
    }


    @Test
    public void testconvertStructureToMyStructureProteinWithPolymericResidueAsHetatm() throws ParsingConfigFileException, IOException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        String fourLetterCode = "2hhf";
        BiojavaReader reader = new BiojavaReader(algoParameters);
        Pair<String, Structure> pathAndmmcifStructure = null;
        try {
            pathAndmmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }
        String hash = null;
        try {
            hash = HashTablesTools.getMD5hash(pathAndmmcifStructure.getKey());
        } catch (NoSuchAlgorithmException e) {
            assertTrue(false);
        }

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(pathAndmmcifStructure.getValue(), hash);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
            assertTrue(false);
        }

        Chain chainA = pathAndmmcifStructure.getValue().getChain(0);
        Chain chainB = pathAndmmcifStructure.getValue().getChain(1);
        List<Group> aminoChainA = chainA.getAtomGroups(GroupType.AMINOACID);
        assertTrue(aminoChainA.size() == 348);
        List<Group> heteroatomChainA = chainA.getAtomGroups(GroupType.HETATM);
        assertTrue(heteroatomChainA.size() == 98);
        assertTrue(heteroatomChainA.get(0).getPDBName().equals("PLP"));

        List<Group> aminoChainB = chainB.getAtomGroups(GroupType.AMINOACID);
        assertTrue(aminoChainB.size() == 365);
        List<Group> heteroatomChainB = chainA.getAtomGroups(GroupType.HETATM);
        assertTrue(heteroatomChainB.size() == 98);
        assertTrue(heteroatomChainB.get(0).getPDBName().equals("PLP"));
        assertTrue(heteroatomChainB.get(1).getPDBName().equals("HOH")); // EPE is gone

        // check content
        MyChainIfc[] myChains = mystructure.getAllChains();
        int count = myChains.length;
        assertTrue(count == 3); // On hetatm chain was emptied so was removed

        MyChainIfc[] aminoMyChains = mystructure.getAllAminochains();
        assertTrue(aminoMyChains.length == 2);
        MyChainIfc[] nucleosidesMyChains = mystructure.getAllNucleosidechains();
        assertTrue(nucleosidesMyChains.length == 0);
        MyChainIfc[] heteroMyChains = mystructure.getAllHetatmchains();
        assertTrue(heteroMyChains.length == 1);

        // Yhat is how it is, don't know if it makes sense that the number is different in Structure ...
        assertTrue(aminoMyChains[0].getMyMonomers().length == 349); // with PLP integrated
        assertTrue(aminoMyChains[1].getMyMonomers().length == 366); // with PLP integrated
        assertTrue(heteroMyChains[0].getMyMonomers().length == 1);
        assertTrue(Arrays.equals(heteroMyChains[0].getMyMonomers()[0].getThreeLetterCode(), "EPE".toCharArray()));

        // check bonds

        boolean foundLlinkingReside = false;
        for (MyChainIfc chain : mystructure.getAllChains()) {
            MyMonomerIfc[] monomers = chain.getMyMonomers();
            for (MyMonomerIfc monomer : monomers) {
                if (monomer.getResidueID() == 108) {
                    assertTrue(Arrays.equals(monomer.getThreeLetterCode(), "OCS".toCharArray()));
                    foundLlinkingReside = true;
                    for (MyAtomIfc atom : monomer.getMyAtoms()) {
                        assertTrue(atom.getBonds() != null);
                        assertTrue(atom.getBonds().length != 0);

                        if (Arrays.equals(atom.getAtomName(), "N".toCharArray())) {
                            assertTrue(atom.getBonds().length == 2); // N so it should be bonded to CA and to C of neighboring residue
                        }
                    }
                }
            }
        }
        assertTrue(foundLlinkingReside);
    }


    /**
     * Test if a Protein read from PDB folder has bonds in MyStructure
     *
     * @throws ParsingConfigFileException
     * @throws IOException
     */
    @Test
    public void testconvertStructureToMyStructureProteinFromFolder() throws ParsingConfigFileException, IOException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        String fourLetterCode = "1di9";
        Pair<String, MyStructureIfc> pathAndMyStructure = IOTools.getMyStructureIfc(algoParameters, fourLetterCode.toCharArray());
        MyStructureIfc mystructure = pathAndMyStructure.getValue();
        // check content
        MyChainIfc[] myChains = mystructure.getAllChains();
        int count = myChains.length;
        assertTrue(count == 2);

        MyChainIfc[] aminoMyChains = mystructure.getAllAminochains();
        assertTrue(aminoMyChains.length == 1);
        MyChainIfc[] nucleosidesMyChains = mystructure.getAllNucleosidechains();
        assertTrue(nucleosidesMyChains.length == 0);
        MyChainIfc[] heteroMyChains = mystructure.getAllHetatmchains();
        assertTrue(heteroMyChains.length == 1);

        assertTrue(aminoMyChains[0].getMyMonomers().length == 348);
        assertTrue(heteroMyChains[0].getMyMonomers().length == 1);

        MyMonomerIfc expectedLigandMSQ = heteroMyChains[0].getMyMonomers()[0];
        assertArrayEquals(expectedLigandMSQ.getThreeLetterCode(), "MSQ".toCharArray());
        List<String> expectedSequence = new ArrayList<>(Arrays.asList("GLU", "ARG", "PRO", "THR", "PHE", "TYR", "ARG"));
        for (int i = 0; i < expectedSequence.size(); i++) {
            MyMonomerIfc currentMyMonomerIfc = aminoMyChains[0].getMyMonomers()[i];
            assertArrayEquals(currentMyMonomerIfc.getThreeLetterCode(), expectedSequence.get(i).toCharArray());
        }

        // check bonds

        for (MyChainIfc chain : mystructure.getAllChains()) {
            MyMonomerIfc[] monomers = chain.getMyMonomers();
            for (MyMonomerIfc monomer : monomers) {
                for (MyAtomIfc atom : monomer.getMyAtoms()) {
                    assertTrue(atom.getBonds() != null);
                    assertTrue(atom.getBonds().length != 0);
                }
            }
        }
    }


    @Test
    public void testconvertStructureToMyStructureProteinHasNeighborsByBondAndDistance() throws ParsingConfigFileException, IOException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        String fourLetterCode = "1di9";
        Pair<String, MyStructureIfc> pathAndMyStructure = IOTools.getMyStructureIfc(algoParameters, fourLetterCode.toCharArray());
        MyStructureIfc mystructure = pathAndMyStructure.getValue();

        MyChainIfc aminoMyChain = mystructure.getAllAminochains()[0];
        for (MyMonomerIfc monomer : aminoMyChain.getMyMonomers()) {

            MyMonomerIfc[] neighborsByBond = monomer.getNeighboringMyMonomerByBond();
            assertTrue(neighborsByBond != null);
            assertTrue(neighborsByBond.length != 0);

            boolean foundNeighbor = false;
            MyChainIfc[] neighborsByDistance = monomer.getNeighboringAminoMyMonomerByRepresentativeAtomDistance();
            for (MyChainIfc chain : neighborsByDistance) {
                if (chain.getMyMonomers().length > 0) {
                    foundNeighbor = true;
                }
            }
            assertTrue(foundNeighbor);
        }
    }

    @Test
    public void testconvertStructureToMyStructureProteinWithNonPolymeric() throws ParsingConfigFileException, IOException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        String fourLetterCode = "2yjd";
        Pair<String, MyStructureIfc> pathAndMyStructure = IOTools.getMyStructureIfc(algoParameters, fourLetterCode.toCharArray());
        MyStructureIfc mystructure = pathAndMyStructure.getValue();

        List<String> expectedSequence = new ArrayList<>(Arrays.asList("ACE", "HIS", "MK8", "ILE", "LEU", "HIS", "MK8", "LEU", "LEU", "GLN", "ASP", "SER", "NH2"));

        List<String> sequence = new ArrayList<>();
        MyChainIfc aminoChainC = mystructure.getAminoMyChain("C".toCharArray());
        assertTrue(aminoChainC.getMyMonomers().length == 13);
        for (int i = 0; i < expectedSequence.size(); i++) {
            char[] name = aminoChainC.getMyMonomerByRank(i).getThreeLetterCode();
            assertTrue(Arrays.equals(name, expectedSequence.get(i).toCharArray()));
        }
        MyChainIfc aminoChainD = mystructure.getAminoMyChain("D".toCharArray());
        assertTrue(aminoChainD.getMyMonomers().length == 13);
        for (int i = 0; i < expectedSequence.size(); i++) {
            char[] name = aminoChainD.getMyMonomerByRank(i).getThreeLetterCode();
            assertTrue(Arrays.equals(name, expectedSequence.get(i).toCharArray()));
        }
    }


    @Test
    public void testconvertStructureToMyStructureNoBondBecauseOnlyCalpha() throws ParsingConfigFileException, IOException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        String fourLetterCode = "1a1d";
        BiojavaReader reader = new BiojavaReader(algoParameters);
        Pair<String, Structure> pathAndmmcifStructure = null;
        try {
            pathAndmmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }
        String hash = null;
        try {
            hash = HashTablesTools.getMD5hash(pathAndmmcifStructure.getKey());
        } catch (NoSuchAlgorithmException e) {
            assertTrue(false);
        }

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(pathAndmmcifStructure.getValue(), hash);

        } catch (Exception e) {
            assertTrue(e.getMessage().equals("Amino residue with only Calpha so giveup"));
        }
    }


    @Test
    public void testconvertStructureToMyStructureNoBondBecauseDisulfideBond() throws ParsingConfigFileException, IOException {


        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        String fourLetterCode = "2agg";
        BiojavaReader reader = new BiojavaReader(algoParameters);
        Pair<String, Structure> pathAndmmcifStructure = null;
        try {
            pathAndmmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }
        String hash = null;
        try {
            hash = HashTablesTools.getMD5hash(pathAndmmcifStructure.getKey());
        } catch (NoSuchAlgorithmException e) {
            assertTrue(false);
        }

        // HETATM 1692 CA CA  . CA  D 4 .   ? 22.071 47.084 17.265  1.00 11.97 ? ? ? ? ? ? 501 CA  X CA  1
        // Weird Atom line with a chain with only one atom which is CA
        // Biojava makes two chains with id X and A

        assertTrue(pathAndmmcifStructure.getValue().getChains().size() == 2);

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(pathAndmmcifStructure.getValue(), hash);
            assertTrue(false); // because an exception should be thrown
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("Amino residue with only Calpha so giveup"));
        }
    }


    @Test
    public void testconvertStructureToMyStructureBondToHOH() throws ParsingConfigFileException, IOException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        String fourLetterCode = "109m";
        BiojavaReader reader = new BiojavaReader(algoParameters);
        Pair<String, Structure> pathAndmmcifStructure = null;
        try {
            pathAndmmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }
        String hash = null;
        try {
            hash = HashTablesTools.getMD5hash(pathAndmmcifStructure.getKey());
        } catch (NoSuchAlgorithmException e) {
            assertTrue(false);
        }

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(pathAndmmcifStructure.getValue(), hash);

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }


    @Test
    public void testconvertStructureToMyStructureWhichHasProblemsInMmcif() throws ParsingConfigFileException, IOException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        String fourLetterCode = "3a0m";
        BiojavaReader reader = new BiojavaReader(algoParameters);
        Pair<String, Structure> pathAndmmcifStructure = null;
        try {
            pathAndmmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }
        String hash = null;
        try {
            hash = HashTablesTools.getMD5hash(pathAndmmcifStructure.getKey());
        } catch (NoSuchAlgorithmException e) {
            assertTrue(false);
        }

        // It cannot be fixed as it is already a Biojava problem
        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(pathAndmmcifStructure.getValue(), hash);
            assertTrue(false); // because an exception should be thrown
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("Corresponding alt loc residue not found so adapter fails"));
        }
    }


    @Test
    public void testconvertStructureToMyStructureWhichAnAtomIsNotSupportedInMyAtomItisCobaltInThatCase() throws ParsingConfigFileException, IOException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        String fourLetterCode = "212d";
        BiojavaReader reader = new BiojavaReader(algoParameters);
        Pair<String, Structure> pathAndmmcifStructure = null;
        try {
            pathAndmmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }
        String hash = null;
        try {
            hash = HashTablesTools.getMD5hash(pathAndmmcifStructure.getKey());
        } catch (NoSuchAlgorithmException e) {
            assertTrue(false);
        }

        // NCO Hetatm contains a Cobalt Atom

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(pathAndmmcifStructure.getValue(), hash);
            assertTrue(false); // because an exception should be thrown
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("Not supported atom type found"));
        }
    }


    @Test
    public void testconvertStructureToMyStructureWhich() throws ParsingConfigFileException, IOException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        String fourLetterCode = "5dn6";
        BiojavaReader reader = new BiojavaReader(algoParameters);
        Pair<String, Structure> pathAndmmcifStructure = null;
        boolean thrown = false;
        try {
            pathAndmmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            thrown = true;
            assertTrue(e.getMessage().equals("NumberFormatException in mMCIFileReader.getStructure()"));
        }
        assertTrue(thrown == true);
    }
}