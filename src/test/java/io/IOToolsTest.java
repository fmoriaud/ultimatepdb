package io;

import org.biojava.nbio.structure.*;
import org.biojava.nbio.structure.secstruc.SecStrucInfo;
import org.biojava.nbio.structure.secstruc.SecStrucType;
import org.junit.Ignore;
import org.junit.Test;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 29/08/16.
 */
public class IOToolsTest {

    @Test
    public void testReadMMCIFFileFromFullPathDNARNAhybrid() {

        URL url = IOToolsTest.class.getClassLoader().getResource("394d.cif.gz");

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


    @Ignore
    @Test
    public void testMmcifCheckSecStructure() {

        URL url = IOToolsTest.class.getClassLoader().getResource("1di9.cif");

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

        Chain chain = cifStructure.getChain(0);
        List<Group> listGroupsAmino = chain.getAtomGroups(GroupType.AMINOACID);
        assertTrue(listGroupsAmino.size() == 348);

        for (Group currentGroup: listGroupsAmino){

            AminoAcid aa = (AminoAcid)currentGroup;

            SecStrucInfo readsecStruc = (SecStrucInfo) aa.getProperty(Group.SEC_STRUC);
            if (readsecStruc != null){
                SecStrucType secType = readsecStruc.getType();
                assertTrue(secType != null);
            }
        }
    }
}