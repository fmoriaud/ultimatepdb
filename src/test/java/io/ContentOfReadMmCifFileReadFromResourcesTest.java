package io;

import org.biojava.nbio.structure.*;
import org.junit.Test;
import parameters.AlgoParameters;
import structure.TestTools;

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
 * Created by Fabrice on 29/08/16.
 */
public class ContentOfReadMmCifFileReadFromResourcesTest {

    @Test
    public void testReadMMCIFFileFromFullPathDNARNAhybrid() {

        URL url = ContentOfReadMmCifFileReadFromResourcesTest.class.getClassLoader().getResource("394d.cif.gz");

        Path path = null;
        try {
            path = Paths.get(url.toURI());
        } catch (URISyntaxException e1) {
            assertTrue(false);
        }
        Structure cifStructure = null;
        try {
            cifStructure = IOTools.readMMCIFFile(path);
        } catch (ExceptionInIOPackage e) {
            assertTrue(false);
        }
        int count = cifStructure.getChains().size();
        assertTrue(count == 2);

        Chain chain = cifStructure.getChain(0);
        List<Group> listGroupsAmino = chain.getAtomGroups(GroupType.AMINOACID);
        assertTrue(listGroupsAmino.size() == 0);
        List<Group> listGroupsNucleotide = chain.getAtomGroups(GroupType.NUCLEOTIDE);
        assertTrue(listGroupsNucleotide.size() == 10);
        List<Group> listGroupsHetatm = chain.getAtomGroups(GroupType.HETATM);
        assertTrue(listGroupsHetatm.size() == 30);

        List<String> expectedSequence = new ArrayList<>(Arrays.asList("DC", "DC", "DG", "DG", "C", "G", "DC", "DC", "DG", "DG"));
        List<Group> groups = chain.getAtomGroups((GroupType.NUCLEOTIDE));
        for (int i=0; i<groups.size(); i++){
            String name = groups.get(i).getPDBName();
            assertTrue(name.equals(expectedSequence.get(i)));
        }

        chain = cifStructure.getChain(1);
        listGroupsAmino = chain.getAtomGroups(GroupType.AMINOACID);
        assertTrue(listGroupsAmino.size() == 0);
        listGroupsNucleotide = chain.getAtomGroups(GroupType.NUCLEOTIDE);
        assertTrue(listGroupsNucleotide.size() == 10);
        listGroupsHetatm = chain.getAtomGroups(GroupType.HETATM);
        assertTrue(listGroupsHetatm.size() == 32);
    }


    @Test
    public void testReadMMCIFFileFromFullPathProtein() {

        URL url = ContentOfReadMmCifFileReadFromResourcesTest.class.getClassLoader().getResource("1di9.cif.gz");

        Path path = null;
        try {
            path = Paths.get(url.toURI());
        } catch (URISyntaxException e1) {
            assertTrue(false);
        }
        Structure cifStructure = null;
        try {
            cifStructure = IOTools.readMMCIFFile(path);
        } catch (ExceptionInIOPackage e) {
            assertTrue(false);
        }
        int count = cifStructure.getChains().size();
        assertTrue(count == 1);

        Chain chain = cifStructure.getChain(0);
        List<Group> listGroupsAmino = chain.getAtomGroups(GroupType.AMINOACID);
        assertTrue(listGroupsAmino.size() == 348);
        List<Group> listGroupsNucleotide = chain.getAtomGroups(GroupType.NUCLEOTIDE);
        assertTrue(listGroupsNucleotide.size() == 0);
        List<Group> listGroupsHetatm = chain.getAtomGroups(GroupType.HETATM);
        assertTrue(listGroupsHetatm.size() == 62);

        Group expectedLigandMSQ = listGroupsHetatm.get(0);
        assertEquals(expectedLigandMSQ.getPDBName(), "MSQ");
        List<String> expectedSequenceBegining = new ArrayList<>(Arrays.asList("GLU", "ARG", "PRO", "THR", "PHE", "TYR", "ARG"));
        List<Group> groups = listGroupsAmino.subList(0,7);
        for (int i = 0; i< expectedSequenceBegining.size(); i++){
            String name = listGroupsAmino.get(i).getPDBName();
            assertTrue(name.equals(expectedSequenceBegining.get(i)));
        }
    }



    @Test
    public void bondCreationDNARNAhybrid() {

        AlgoParameters algoParameters = TestTools.getAlgoParameters();
        Structure cifStructure = null;
        try {
            cifStructure = IOTools.readMMCIFFileWithAtomCache("394d", algoParameters);
        } catch (ExceptionInIOPackage e) {
            assertTrue(false);

        }

        for (Chain chain: cifStructure.getChains()){
            List<Group> groups = chain.getAtomGroups(GroupType.NUCLEOTIDE);
            for (Group group: groups){
                for (Atom atom: group.getAtoms()){
                    List<Bond> bonds = atom.getBonds();
                    for (Bond bond: bonds){
                        assertTrue(bond != null);
                    }
                }
            }
        }
    }
}