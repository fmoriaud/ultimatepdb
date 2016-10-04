package io;

import genericBuffer.GenericBuffer;
import math.ProcrustesAnalysisIfc;
import org.apache.commons.io.FileUtils;
import org.biojava.nbio.structure.*;
import parameters.AlgoParameters;
import protocols.CommandLineTools;
import protocols.ParsingConfigFileException;
import mystructure.EnumMyReaderBiojava;
import shapeCompare.ProcrustesAnalysis;
import ultiJmol1462.MyJmol1462;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * When Tools is called then the PDB files in resources are copied to a test folder in home directory
 * TODO delete it, run test, copy chemcomp to resources, add copy of chemcomp. Then delete folder in all cases.
 * TODO Then Test should run off line
 */
public class Tools {

    /**
     * A test folder is defined for all test. That is because I couldn't make it work with TemporaryFolders
     */
    public static String testChemcompFolder = createPermanentTestFolder();
    public static String testPDBFolder;

    /**
     * Create test PDB and Chemcomp folder. Also all PDB files in resources are copied there so all test can use this
     * folder
     *
     * @return
     */
    public static String createPermanentTestFolder() {

        String d = System.getProperty("user.home");
        String builttestChemcompFolder = d + File.separator + "Documents" + File.separator + "testultimatepdb";
        final File baseDir = new File(builttestChemcompFolder);

        // if baseDir already exists then empty it
        /*
        if (baseDir.exists()) {
            try {
                FileUtils.deleteDirectory(baseDir);
            } catch (IOException e) {

            }
        }
        */
        String builttestPDBFolder = builttestChemcompFolder + File.separator + "pdb";
        baseDir.mkdirs();
        final File pdbDir = new File(builttestPDBFolder);
        pdbDir.mkdir();
        testChemcompFolder = builttestChemcompFolder;
        testPDBFolder = builttestPDBFolder;

        //URL url = BiojavaReaderFromPathToMmcifFileTest.class.getClassLoader().getResource("1di9.cif.gz");
        String resourcesPDBFolder = null;
        try {
            URL url = BiojavaReaderFromPDBFolderTest.class.getClassLoader().getResource("pdb/1di9.cif.gz");
            File pdb1di9file = new File(url.toURI());
            resourcesPDBFolder = pdb1di9file.getParent();
            Map<String, List<Path>> indexPDBFileInFolder = IOTools.indexPDBFileInFolder(new File(resourcesPDBFolder).toString());
            for (Map.Entry<String, List<Path>> entry : indexPDBFileInFolder.entrySet()) {
                try {
                    FileUtils.copyFileToDirectory(new File(entry.getValue().get(0).toString()), pdbDir);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return testChemcompFolder;
    }

    /**
     * Tested method to get a PDB file from path
     * The chemcomp are automatically downloaded
     *
     * @param url
     * @throws ParsingConfigFileException
     * @throws IOException
     */
    private static Structure getStructure(URL url) throws ParsingConfigFileException, IOException {
        Path path = null;
        try {
            path = Paths.get(url.toURI());
        } catch (URISyntaxException e1) {
            assertTrue(false);
        }
        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();
        Structure structure = null;
        BiojavaReaderIfc reader = new BiojavaReader();
        structure = reader.read(path.toAbsolutePath(), algoParameters.getPATH_TO_CHEMCOMP_FOLDER());
        return structure;
    }


    public static AlgoParameters generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol() throws ParsingConfigFileException, IOException {

        AlgoParameters algoParameters = generateModifiedAlgoParametersForTestWithTestFolders();
        // add a ultiJmol which is needed in the ShapeBuilder
        algoParameters.ultiJMolBuffer = new GenericBuffer<MyJmol1462>(algoParameters.getSHAPE_COMPARISON_THREAD_COUNT());
        MyJmol1462 ultiJMol = new MyJmol1462();
        try {
            algoParameters.ultiJMolBuffer.put(ultiJMol);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        algoParameters.procrustesAnalysisBuffer = new GenericBuffer<ProcrustesAnalysisIfc>(algoParameters.getSHAPE_COMPARISON_THREAD_COUNT());
        ProcrustesAnalysisIfc procrustesAnalysisIfc = new ProcrustesAnalysis();
        try {
            algoParameters.procrustesAnalysisBuffer.put(procrustesAnalysisIfc);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return algoParameters;
    }


    public static AlgoParameters generateModifiedAlgoParametersForTestWithTestFolders() throws ParsingConfigFileException, IOException {

        URL url = BiojavaReaderFromPDBFolderTest.class.getClassLoader().getResource("ultimate.xml");
        AlgoParameters algoParameters = CommandLineTools.generateModifiedAlgoParameters(url.getPath(), EnumMyReaderBiojava.BioJava_MMCIFF);

        algoParameters.setPATH_TO_REMEDIATED_PDB_MMCIF_FOLDER(testPDBFolder);
        algoParameters.setPATH_TO_CHEMCOMP_FOLDER(testChemcompFolder);

        return algoParameters;
    }


    public static boolean isGood1di9(Structure mmcifStructure) {
        int count = mmcifStructure.getChains().size();
        if (count != 1) {
            return false;
        }

        Chain chain0 = mmcifStructure.getChain(0);
        List<Group> listGroupsAmino = chain0.getAtomGroups(GroupType.AMINOACID);
        if (listGroupsAmino.size() != 348) {
            return false;
        }
        List<Group> listGroupsNucleotide = chain0.getAtomGroups(GroupType.NUCLEOTIDE);
        if (listGroupsNucleotide.size() != 0) {
            return false;
        }
        List<Group> listGroupsHetatm = chain0.getAtomGroups(GroupType.HETATM);
        if (listGroupsHetatm.size() != 62) {
            return false;
        }

        Group expectedLigandMSQ = listGroupsHetatm.get(0);
        if (!expectedLigandMSQ.getPDBName().equals("MSQ")) {
            return false;
        }

        List<String> expectedSequenceBegining = new ArrayList<>(Arrays.asList("GLU", "ARG", "PRO", "THR", "PHE", "TYR", "ARG"));
        for (int i = 0; i < expectedSequenceBegining.size(); i++) {
            String name = listGroupsAmino.get(i).getPDBName();
            if (!name.equals(expectedSequenceBegining.get(i))) {
                return false;
            }
        }

        boolean atLeastOneBond = false;
        for (Chain chain : mmcifStructure.getChains()) {
            List<Group> groups = chain.getAtomGroups(GroupType.AMINOACID);
            for (Group group : groups) {
                for (Atom atom : group.getAtoms()) {
                    List<Bond> bonds = atom.getBonds();
                    assertTrue(bonds != null);
                    for (Bond bond : bonds) {
                        atLeastOneBond = true;
                        assertTrue(bond != null);
                    }
                }
            }
        }
        assertTrue(atLeastOneBond);
        return true;
    }
}