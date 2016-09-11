package convertformat;

import io.BiojavaReader;
import io.BiojavaReaderFromPathToMmcifFileTest;
import io.Tools;
import mystructure.*;
import org.biojava.nbio.structure.Structure;
import org.junit.Ignore;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 06/09/16.
 */
public class AdapterBioJavaStructureTest {

    /**
     * Test of the expected conversion os Structure to MyStructureIfc
     * H2O are discarded
     *
     * @throws ParsingConfigFileException
     * @throws IOException
     */
    @Test
    public void testconvertStructureToMyStructureProtein() throws ParsingConfigFileException, IOException {

        URL url = BiojavaReaderFromPathToMmcifFileTest.class.getClassLoader().getResource("1di9.cif.gz");
        Structure mmcifStructure = mmcifStructure = Tools.getStructure(url, Tools.testChemcompFolder);

        URL urlUltimate = BiojavaReaderFromPathToMmcifFileTest.class.getClassLoader().getResource("ultimate.xml");
        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure, EnumMyReaderBiojava.BioJava_MMCIFF);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException e) {
            assertTrue(false);
        }

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


    /**
     * Test of the expected conversion o Structure to MyStructureIfc. A Structure without Amino chains is not converted
     *
     * @throws ParsingConfigFileException
     * @throws IOException
     */
    @Test
    public void testconvertStructureToMyStructureDNARNAHybrid() throws ParsingConfigFileException, IOException {

        URL url = BiojavaReaderFromPathToMmcifFileTest.class.getClassLoader().getResource("394d.cif.gz");
        Structure mmcifStructure = mmcifStructure = Tools.getStructure(url, Tools.testChemcompFolder);

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure, EnumMyReaderBiojava.BioJava_MMCIFF);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException e) {
            assertTrue(e instanceof ReadingStructurefileException);
            assertTrue(e.getMessage().startsWith("Only empty amino chain were parsed for"));
        }
    }


    @Ignore
    @Test
    public void testconvertStructureToMyStructureProteinWithPolymericResidueAsHetatm() throws ParsingConfigFileException, IOException {

        URL url = BiojavaReaderFromPathToMmcifFileTest.class.getClassLoader().getResource("2hhf.cif.gz");
        Structure mmcifStructure = mmcifStructure = Tools.getStructure(url, Tools.testChemcompFolder);

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure, EnumMyReaderBiojava.BioJava_MMCIFF);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException e) {
            assertTrue(false);
        }

        // check content
        MyChainIfc[] myChains = mystructure.getAllChains();
        int count = myChains.length;
        assertTrue(count == 4);

        MyChainIfc[] aminoMyChains = mystructure.getAllAminochains();
        assertTrue(aminoMyChains.length == 2);
        MyChainIfc[] nucleosidesMyChains = mystructure.getAllNucleosidechains();
        assertTrue(nucleosidesMyChains.length == 0);
        MyChainIfc[] heteroMyChains = mystructure.getAllHetatmchains();
        assertTrue(heteroMyChains.length == 2);

        // Yhat is how it is, don't know if it makes sense that the number is different in Structure ...
        assertTrue(aminoMyChains[0].getMyMonomers().length == 347);
        assertTrue(aminoMyChains[1].getMyMonomers().length == 365);
        assertTrue(heteroMyChains[0].getMyMonomers().length == 1);
        assertTrue(heteroMyChains[1].getMyMonomers().length == 2);

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

        String fourLetterCode = "1di9";

        BiojavaReader reader = new BiojavaReader();

        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException e) {
            assertTrue(false);
        }
        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure, EnumMyReaderBiojava.BioJava_MMCIFF);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException e) {
            assertTrue(false);
        }

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

        URL url = BiojavaReaderFromPathToMmcifFileTest.class.getClassLoader().getResource("1di9.cif.gz");
        Structure mmcifStructure = mmcifStructure = Tools.getStructure(url, Tools.testChemcompFolder);

        URL urlUltimate = BiojavaReaderFromPathToMmcifFileTest.class.getClassLoader().getResource("ultimate.xml");
        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure, EnumMyReaderBiojava.BioJava_MMCIFF);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException e) {
            assertTrue(false);
        }

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
}