package io;

import genericBuffer.GenericBuffer;
import mystructure.MyStructureIfc;
import org.biojava.nbio.structure.Chain;
import org.biojava.nbio.structure.Group;
import org.biojava.nbio.structure.GroupType;
import org.biojava.nbio.structure.Structure;
import parameters.AlgoParameters;
import protocols.CommandLineTools;
import protocols.ParsingConfigFileException;
import mystructure.EnumMyReaderBiojava;
import ultiJmol1462.MyJmol1462;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 05/09/16.
 */
public class Tools {

    /**
     * A test folder is defined for all test. That is because I couldn't make it work with TemporaryFolders
     */
    public static final String testChemcompFolder = "//Users//Fabrice//Documents//test";
    public static final String testPDBFolder = "//Users//Fabrice//Documents//test//pdb";


    /**
     * Tested method to get a PDB file from path
     * The chemcomp are automatically downloaded
     *
     * @param url
     * @throws ParsingConfigFileException
     * @throws IOException
     */
    public static Structure getStructure(URL url, String pathToTempPDBFolder) throws ParsingConfigFileException, IOException {
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


    public static AlgoParameters generateModifiedAlgoParametersForTestWithTestFolders() throws ParsingConfigFileException, IOException {

        URL url = BiojavaReaderFromPathToMmcifFileTest.class.getClassLoader().getResource("ultimate.xml");
        AlgoParameters algoParameters = CommandLineTools.generateModifiedAlgoParameters(url.getPath(), EnumMyReaderBiojava.BioJava_MMCIFF);

        algoParameters.setPATH_TO_REMEDIATED_PDB_MMCIF_FOLDER(testPDBFolder);
        algoParameters.setPATH_TO_CHEMCOMP_FOLDER(testChemcompFolder);

        // add a ultiJmol which is needed in the ShapeBuilder
        algoParameters.ultiJMolBuffer = new GenericBuffer<MyJmol1462>(algoParameters.getSHAPE_COMPARISON_THREAD_COUNT());
        MyJmol1462 ultiJMol = new MyJmol1462();
        try {
            algoParameters.ultiJMolBuffer.put(ultiJMol);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return algoParameters;
    }


    public static boolean isGood1di9(Structure mmcifStructure) {
        int count = mmcifStructure.getChains().size();
        if (count != 1) {
            return false;
        }

        Chain chain = mmcifStructure.getChain(0);
        List<Group> listGroupsAmino = chain.getAtomGroups(GroupType.AMINOACID);
        if (listGroupsAmino.size() != 348) {
            return false;
        }
        List<Group> listGroupsNucleotide = chain.getAtomGroups(GroupType.NUCLEOTIDE);
        if (listGroupsNucleotide.size() != 0) {
            return false;
        }
        List<Group> listGroupsHetatm = chain.getAtomGroups(GroupType.HETATM);
        if (listGroupsHetatm.size() != 62) {
            return false;
        }

        Group expectedLigandMSQ = listGroupsHetatm.get(0);
        if (!expectedLigandMSQ.getPDBName().equals("MSQ")) {
            return false;
        }

        List<String> expectedSequenceBegining = new ArrayList<>(Arrays.asList("GLU", "ARG", "PRO", "THR", "PHE", "TYR", "ARG"));
        List<Group> groups = listGroupsAmino.subList(0, 7);
        for (int i = 0; i < expectedSequenceBegining.size(); i++) {
            String name = listGroupsAmino.get(i).getPDBName();
            if (!name.equals(expectedSequenceBegining.get(i))) {
                return false;
            }
        }
        return true;
    }
}