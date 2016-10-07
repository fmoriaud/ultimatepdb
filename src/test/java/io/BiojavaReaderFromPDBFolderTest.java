package io;

import org.biojava.nbio.structure.*;
import org.junit.Test;
import protocols.ParsingConfigFileException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 11/09/16.
 */
public class BiojavaReaderFromPDBFolderTest {


    @Test
    public void testReadFromPDBFolderProtein() throws ParsingConfigFileException {

        String fourLetterCode = "1di9";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }
        assertTrue(Tools.isGood1di9(mmcifStructure));
    }



    @Test
    public void testReadFromResourcesDNARNAHybrid() throws ParsingConfigFileException {

        String fourLetterCode = "394d";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }

        int count = mmcifStructure.getChains().size();
        assertTrue(count == 2);

        Chain chain = mmcifStructure.getChain(0);
        List<Group> listGroupsAmino = chain.getAtomGroups(GroupType.AMINOACID);
        assertTrue(listGroupsAmino.size() == 0);
        List<Group> listGroupsNucleotide = chain.getAtomGroups(GroupType.NUCLEOTIDE);
        assertTrue(listGroupsNucleotide.size() == 10);
        List<Group> listGroupsHetatm = chain.getAtomGroups(GroupType.HETATM);
        assertTrue(listGroupsHetatm.size() == 30);

        List<String> expectedSequence = new ArrayList<>(Arrays.asList("DC", "DC", "DG", "DG", "C", "G", "DC", "DC", "DG", "DG"));
        List<Group> groups = chain.getAtomGroups((GroupType.NUCLEOTIDE));
        for (int i = 0; i < groups.size(); i++) {
            String name = groups.get(i).getPDBName();
            assertTrue(name.equals(expectedSequence.get(i)));
        }

        chain = mmcifStructure.getChain(1);
        listGroupsAmino = chain.getAtomGroups(GroupType.AMINOACID);
        assertTrue(listGroupsAmino.size() == 0);
        listGroupsNucleotide = chain.getAtomGroups(GroupType.NUCLEOTIDE);
        assertTrue(listGroupsNucleotide.size() == 10);
        listGroupsHetatm = chain.getAtomGroups(GroupType.HETATM);
        assertTrue(listGroupsHetatm.size() == 32);
    }



    @Test
    public void testReadFromResourcesProteinWithLPeptideLinkingResidue() throws ParsingConfigFileException {

        String fourLetterCode = "2hhf";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }

        int count = mmcifStructure.getChains().size();
        assertTrue(count == 2);

        Chain chain = mmcifStructure.getChain(0);
        List<Group> listGroupsAmino = chain.getAtomGroups(GroupType.AMINOACID);
        assertTrue(listGroupsAmino.size() == 348);
        List<Group> listGroupsNucleotide = chain.getAtomGroups(GroupType.NUCLEOTIDE);
        assertTrue(listGroupsNucleotide.size() == 0);
        List<Group> listGroupsHetatm = chain.getAtomGroups(GroupType.HETATM);
        assertTrue(listGroupsHetatm.size() == 98);

        List<String> expectedSequence = new ArrayList<>(Arrays.asList("DC", "DC", "DG", "DG", "C", "G", "DC", "DC", "DG", "DG"));
        List<Group> groups = chain.getAtomGroups((GroupType.AMINOACID));
        for (int i = 0; i < groups.size(); i++) {
            String name = groups.get(i).getPDBName();
            AminoAcid aa = (AminoAcid)  groups.get(i);
            int residueNumer = aa.getResidueNumber().getSeqNum();
            if (residueNumer == 108){
                assertTrue(name.equals("OCS"));
            }
        }
        // Not very surprising as OCS 108 is well integrated in the mmcif file as a residue with ATOM
    }



    @Test
    public void testBondsReadFromResourcesProtein() throws ParsingConfigFileException {

        String fourLetterCode = "1di9";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
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
    }


    @Test
    public void testBondsReadFromResourcesDNARNAHybrid() throws ParsingConfigFileException {

        String fourLetterCode = "394d";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }

        boolean atLeastOneBond = false;
        for (Chain chain : mmcifStructure.getChains()) {
            List<Group> groups = chain.getAtomGroups(GroupType.NUCLEOTIDE);
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
    }

    /**
     * It is expected in Biojava 4.2 that L-Peptide Linking residue are well integrated as regular Amino Acids
     * At least when reading from mmcif files. It is not tested for PDB files.
     * @throws ParsingConfigFileException
     */
    @Test
    public void testBondsReadFromResourcesProteinWithLPeptideLinkingResidue() throws ParsingConfigFileException {

        String fourLetterCode = "2hhf";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }
        for (Chain chain : mmcifStructure.getChains()) {
            List<Group> groups = chain.getAtomGroups(GroupType.AMINOACID);
            for (Group group : groups) {
                String name = group.getPDBName();
                AminoAcid aa = (AminoAcid)  group;
                int residueNumer = aa.getResidueNumber().getSeqNum();
                if (residueNumer == 108){
                    assertTrue(name.equals("OCS"));
                    for (Atom atom : group.getAtoms()) {
                        List<Bond> bonds = atom.getBonds();
                        assertTrue(bonds != null);
                        for (Bond bond : bonds) {
                            assertTrue(bond != null);
                            if (atom.getName().equals("N")){
                                assertTrue(atom.getBonds().size() == 2); // N so it should be bonded to CA and to C of neighboring residue
                            }
                        }
                    }
                }
            }
        }
    }



    @Test
    public void testReadFromResourcesProtein() throws ParsingConfigFileException {

        String fourLetterCode = "2yjd";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }

        // Check the current situation Biojava 4.2.4
        // Four chains which is fine
        // Non Polymeric groups are found as HETATM Groups

        List<Chain> chains = mmcifStructure.getChains();
        assertTrue(chains.size() == 4);
        Chain peptideA = chains.get(2);
        List<Group> peptideAAminoAcids = peptideA.getAtomGroups(GroupType.AMINOACID);
        assertTrue(peptideAAminoAcids.size() == 11);
        List<Group> peptideAHetatm = peptideA.getAtomGroups(GroupType.HETATM);
        assertTrue(peptideAHetatm.size() == 7);
        Chain peptideB = chains.get(3);
        List<Group> peptideBAminoAcids = peptideB.getAtomGroups(GroupType.AMINOACID);
        assertTrue(peptideBAminoAcids.size() == 11);
        List<Group> peptideBHetatm = peptideB.getAtomGroups(GroupType.HETATM);
        assertTrue(peptideBHetatm.size() == 2);
        System.out.println();
    }
}
