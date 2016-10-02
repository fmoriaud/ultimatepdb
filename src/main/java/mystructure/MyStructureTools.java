package mystructure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//import org.biojava.nbio.structure.secstruc.SecStrucInfo;

import math.ToolsMath;
import org.biojava.nbio.structure.*;
import parameters.AlgoParameters;
import pointWithProperties.Point;
import pointWithProperties.PointIfc;
import pointWithProperties.PointsTools;
import mystructure.EnumResidues.HetatmResidues;

public class MyStructureTools {

    /**
     * @param type BioJava Residue type
     * @return MyMonomerType
     */
    public static MyMonomerType convertType(GroupType type) {

        MyMonomerType myMonomerType = null;
        switch (type) {
            case AMINOACID:
                myMonomerType = MyMonomerType.AMINOACID;
                break;
            case NUCLEOTIDE:
                myMonomerType = MyMonomerType.NUCLEOTIDE;
                break;
            case HETATM:
                myMonomerType = MyMonomerType.HETATM;
                break;
        }
        return myMonomerType;
    }


    /**
     * Test if given MyAtom is a HYdrogen atom based on its element
     *
     * @param atom
     * @return
     */
    public static boolean isHydrogen(MyAtomIfc atom) {

        if (Arrays.equals(atom.getElement(), "H".toCharArray())) {
            return true;
        }
        return false;
    }


    /**
     * Automatically generates Hydrogen names from heavy atom names
     *
     * @param heavyAtomName
     * @return
     */
    public static List<char[]> generateHydrogenAtomName(String heavyAtomName) {

        switch (heavyAtomName) {
            case "CA":
                return makeHydrogensName(1, heavyAtomName);
            case "N":
                return makeHydrogensName(1, heavyAtomName);

            default:
                List<char[]> hydrogenAtomName = new ArrayList<>();
                return hydrogenAtomName;
        }
    }


    /**
     * @return Backbone atoms names: CA, C, N and O
     */
    public static List<char[]> getBackBoneAtomNames() {

        List<char[]> backboneAtomName = new ArrayList<>();
        backboneAtomName.add("CA".toCharArray());
        backboneAtomName.add("C".toCharArray());
        backboneAtomName.add("N".toCharArray());
        backboneAtomName.add("O".toCharArray());

        return backboneAtomName;
    }


    /**
     * @return Backbone atoms names including hydrogens: CA, C, N, O, H1CA, H1N
     */
    public static List<char[]> getBackBoneAtomWithHydrogensNames() {

        List<char[]> backboneAtomNames = new ArrayList<>();
        backboneAtomNames.addAll(getBackBoneAtomNames());
        for (char[] backboneAtomName : getBackBoneAtomNames()) {
            List<char[]> hydrogensName = generateHydrogenAtomName(String.valueOf(backboneAtomName));
            backboneAtomNames.addAll(hydrogensName);
        }
        return backboneAtomNames;
    }


    /**
     * Returns true if atom has name C, N, O, H1CA or H1N
     *
     * @param atom
     * @return
     */
    public static boolean isAtomFromBackBoneOtherThanCA(MyAtomIfc atom) {

        char[] atomName = atom.getAtomName();
        return isAtomNameFromBackBoneOtherThanCA(atomName);
    }


    /**
     * Returns true if atom name is C, N, O, H1CA or H1N
     *
     * @param atomName
     * @return
     */
    public static boolean isAtomNameFromBackBoneOtherThanCA(char[] atomName) {

        List<char[]> backBoneAtomName = MyStructureTools.getBackBoneAtomWithHydrogensNames();
        if (Arrays.equals("CA".toCharArray(), atomName)) {
            return false;
        }
        if (MyStructureTools.isInList(backBoneAtomName, atomName)) {
            return true;
        }
        return false;
    }


    /**
     * Set new atom ids from 1 to count of Atoms
     *
     * @param myStructure
     */
    public static void renumberAllAtomIds(MyStructureIfc myStructure) {

        int atomId = 0;
        for (MyChainIfc chain : myStructure.getAllChains()) {
            for (MyMonomerIfc monomer : chain.getMyMonomers()) {
                for (MyAtomIfc atom : monomer.getMyAtoms()) {
                    atomId += 1;
                    atom.setOriginalAtomId(atomId);
                }
            }
        }
    }


    /**
     * Set to all MyAtom the parent reference to MyMonomer
     *
     * @param myMonomer
     */
    public static void setAtomParentReference(MyMonomerIfc myMonomer) {
        for (int i = 0; i < myMonomer.getMyAtoms().length; i++) {
            myMonomer.getMyAtoms()[i].setParent(myMonomer);
        }
    }


    public static void setAtomParentReference(MyChainIfc myChain) {
        for (int i = 0; i < myChain.getMyMonomers().length; i++) {
            setAtomParentReference(myChain.getMyMonomers()[i]);
            myChain.getMyMonomers()[i].setParent(myChain);
        }
    }

    /**
     * Returns the count of bonds. There are two MyBond object for one bond, one on each of the 2 bonded atom.
     *
     * @param myStructure
     * @return
     * @throws ExceptionInMyStructurePackage
     */
    public static int countBonds(MyStructureIfc myStructure) throws ExceptionInMyStructurePackage {

        int myBondCount = countMyBond(myStructure);
        if (!(myBondCount % 2 == 0)) {
            throw new ExceptionInMyStructurePackage("MyStructure contains an odd number of MyBond which is invalid");
        }
        return myBondCount / 2;
    }


    /**
     * Returns the count of MyBonds. In valid MyStructure it is twice the count of bonds
     *
     * @param myStructure
     * @return
     */
    public static int countMyBond(MyStructureIfc myStructure) {

        int myBondCount = 0;
        List<MyAtomIfc> myAtoms = extractMyAtoms(myStructure);
        for (MyAtomIfc atom : myAtoms) {
            myBondCount += atom.getBonds().length;
        }
        return myBondCount;
    }


    /**
     * Clean up the bonds to MyAtom not in the given MyStructure.
     * By removing MyBond which have a bonded atom which is not in the MyStructure
     * Needed to not get null in V3000 file
     *
     * @param myStructure
     */
    public static void removeBondsToMyAtomsNotInMyStructure(MyStructureIfc myStructure) {

        List<MyAtomIfc> tempListMyAtomIfc = extractMyAtoms(myStructure);
        removeBondsToMyAtomsNotInMyStructure(tempListMyAtomIfc);
    }


    public static void removeBondsToMyAtomsNotInMyStructure(MyChainIfc[] myChains) {

        List<MyAtomIfc> tempListMyAtomIfc = extractMyAtoms(myChains);
        removeBondsToMyAtomsNotInMyStructure(tempListMyAtomIfc);
    }


    public static void removeBondsToMyAtomsNotInMyStructure(MyChainIfc myChain) {

        List<MyAtomIfc> tempListMyAtomIfc = extractMyAtoms(myChain);
        removeBondsToMyAtomsNotInMyStructure(tempListMyAtomIfc);
    }


    public static void removeBondsToMyAtomsNotInMyStructure(MyMonomerIfc myMonomer) {

        List<MyAtomIfc> tempListMyAtomIfc = extractMyAtoms(myMonomer);
        removeBondsToMyAtomsNotInMyStructure(tempListMyAtomIfc);
    }

    /**
     * Clean up the bonds to MyAtom not in the given MyChain.
     * Needed to not get null in V3000 file
     *
     * @param myChain
     */
    public static void removeBondsToNonExistingAtoms(MyChainIfc myChain) {

        List<MyAtomIfc> tempListMyAtomIfc = extractMyAtomsFromMyChain(myChain);
        removeBondsToMyAtomsNotInMyStructure(tempListMyAtomIfc);
    }


    /**
     * Return a specific MyAtom from each and every MyMonomer. It is a backbone atom for Chains.
     *
     * @param monomer
     * @return
     */
    public static MyAtomIfc getRepresentativeMyAtom(MyMonomerIfc monomer) {

        MyAtomIfc[] myAtoms = monomer.getMyAtoms();
        char[] atomName = getAtomNameOfRepresentativeMyMonomer(monomer);
        for (MyAtomIfc myAtom : myAtoms) {
            if (String.valueOf(myAtom.getAtomName()).equals(String.valueOf(atomName))) {
                return myAtom;
            }
        }
        return null;
    }


    /**
     * Set in each and every MyMonomers the neighborhing MyMonomers based on a distance cutoff.
     * Distance between two MyMonomers is defined as distance between representative MyAtoms
     *
     * @param myStructure
     * @param algoParameters
     */
    public static void computeAndStoreNeighBorhingAminoMonomersByDistanceBetweenRepresentativeMyAtom(MyStructureIfc myStructure, AlgoParameters algoParameters) {

        MyChainIfc[] allChains = myStructure.getAllChains();
        computeAndStoreNeighBorhingAminoMonomersByDistanceBetweenRepresentativeMyAtom(algoParameters, allChains);
    }


    public static void computeAndStoreNeighBorhingAminoMonomersByDistanceBetweenRepresentativeMyAtom(AlgoParameters algoParameters, MyChainIfc[]... myChains) {

        double minDistanceToBeNeighbors = algoParameters.getMIN_DISTANCE_TO_BE_NEIBHOR();
        GeneratorNeighboringMonomer distancesBetweenResidues = new GeneratorNeighboringMonomer(minDistanceToBeNeighbors, myChains);

        for (MyChainIfc[] chains : myChains) {
            for (MyChainIfc chain : chains) {
                for (MyMonomerIfc monomer : chain.getMyMonomers()) {
                    monomer.setNeighboringAminoMyMonomerByRepresentativeAtomDistance(distancesBetweenResidues.computeAminoNeighborsOfAGivenResidue(monomer));
                }
            }
        }
    }

    /**
     * Compute MyMonomer Neighbors using MyBonds. They are stored as a MyMonomer array in each and every MyMonomer.
     *
     * @param myStructure
     */
    public static void computeAndStoreNeighboringMonomersByBond(MyStructureIfc myStructure) {

        List<MyMonomerIfc> tempMyMonomerList = new ArrayList<>();

        for (MyChainIfc myChain : myStructure.getAllChains()) {
            for (MyMonomerIfc myMonomer : myChain.getMyMonomers()) {

                tempMyMonomerList.clear();
                for (MyAtomIfc atom : myMonomer.getMyAtoms()) {
                    MyBondIfc[] bonds = atom.getBonds();
                    if (bonds != null) {
                        for (MyBondIfc myBond : bonds) {
                            MyAtomIfc bondedAtom = myBond.getBondedAtom();
                            MyMonomerIfc myBondedMonomer = bondedAtom.getParent();
                            if (myBondedMonomer == null) {
                                continue; // dont really know why but it happens
                            }
                            if (myBondedMonomer != myMonomer && !tempMyMonomerList.contains(myBondedMonomer)) {
                                tempMyMonomerList.add(myBondedMonomer);
                            }
                        }
                    }
                }
                MyMonomerIfc[] myMonomersNeighborsByBond = tempMyMonomerList.toArray(new MyMonomerIfc[tempMyMonomerList.size()]);
                myMonomer.setNeighboringMyMonomerByBond(myMonomersNeighborsByBond);
            }
        }
    }


    /**
     * Removes Hydrogen atom from a given MyStructure. Bonds to hydrogen atoms are also removed.
     *
     * @param myStructure
     */
    public static void removeAllExplicitHydrogens(MyStructureIfc myStructure) {

        List<MyAtomIfc> atomToKeep = new ArrayList<>();
        List<MyBondIfc> bondToKeep = new ArrayList<>();

        for (MyChainIfc chain : myStructure.getAllChains()) {
            for (int i = 0; i < chain.getMyMonomers().length; i++) {
                MyMonomerIfc monomer = chain.getMyMonomers()[i];
                atomToKeep.clear();
                for (MyAtomIfc atom : monomer.getMyAtoms()) {
                    if (!MyStructureTools.isHydrogen(atom)) {
                        atomToKeep.add(atom);
                    }
                }
                if (atomToKeep.size() == monomer.getMyAtoms().length) {
                    continue;
                }
                // remove bonds to hydrogen
                bondToKeep.clear();
                for (MyAtomIfc atom : atomToKeep) {
                    MyBondIfc[] bonds = atom.getBonds();
                    for (MyBondIfc bond : bonds) {
                        if (!MyStructureTools.isHydrogen(bond.getBondedAtom())) {
                            bondToKeep.add(bond);
                        }
                    }
                    if (bondToKeep.size() == atom.getBonds().length) {
                        continue;
                    }
                    MyBondIfc[] newBonds = bondToKeep.toArray(new MyBondIfc[bondToKeep.size()]);
                    atom.setBonds(newBonds);
                }
                MyAtomIfc[] atomsNotHydrogen = atomToKeep.toArray(new MyAtomIfc[atomToKeep.size()]);
                monomer.setMyAtoms(atomsNotHydrogen);
            }
        }
    }


    public static float computeDistance(MyMonomerIfc monomer1, MyMonomerIfc monomer2) {

        float mindistance = Float.MAX_VALUE;

        for (MyAtomIfc atom1 : monomer1.getMyAtoms()) {
            for (MyAtomIfc atom2 : monomer2.getMyAtoms()) {
                float distance = ToolsMath.computeDistance(atom1.getCoords(), atom2.getCoords());
                if (distance < mindistance) {
                    mindistance = distance;
                }
            }
        }
        return mindistance;
    }


    public static MyAtomIfc findAtomClosestToBarycenter(MyMonomerIfc monomer) {

        List<PointIfc> listOfPoints = PointsTools.createListOfPointIfcFromMonomer(monomer);
        PointIfc barycenter = PointsTools.computeLigandBarycenter(listOfPoints);

        double mindistance = Double.MAX_VALUE;
        MyAtomIfc atomToReturn = null;
        for (MyAtomIfc atom : monomer.getMyAtoms()) {
            double distance = ToolsMath.computeDistance(barycenter.getCoords(), atom.getCoords());
            if (distance < mindistance) {
                mindistance = distance;
                atomToReturn = atom;
            }
        }
        return atomToReturn;
    }


    public static String generateSequence(MyChainIfc chain) {

        StringBuffer stringBuffer = new StringBuffer();
        for (MyMonomerIfc monomer : chain.getMyMonomers()) {
            stringBuffer.append(String.valueOf(monomer.getThreeLetterCode()));
            //stringBuffer.append(" ");
        }
        String sequence = stringBuffer.toString();
        return sequence;
    }


    public static MyChainIfc[] makeArrayFromList(List<MyChainIfc> myChains) {

        int countOfChains = myChains.size();
        MyChainIfc[] myChainsArray = new MyChainIfc[countOfChains];
        for (int i = 0; i < countOfChains; i++) {
            myChainsArray[i] = myChains.get(i);
        }
        return myChainsArray;
    }


    public static int getAtomCount(MyStructureIfc myStructure) {

        int atomCount = 0;
        for (MyChainIfc chain : myStructure.getAllChains()) {
            for (MyMonomerIfc monomer : chain.getMyMonomers()) {
                atomCount += monomer.getMyAtoms().length;
            }
        }
        return atomCount;
    }


    public static MyStructureIfc mergeTwoV3000FileReturnIdOfFirstAtomMyStructure2AndLoadInViewer(String structureV3000, String peptideV3000, AlgoParameters algoParameters) {

        MyStructureIfc myStructureFile1 = null;
        try {
            myStructureFile1 = new MyStructure(structureV3000, algoParameters);
        } catch (ExceptionInMyStructurePackage e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        MyStructureIfc myStructureFile2 = null;
        try {
            myStructureFile2 = new MyStructure(peptideV3000, algoParameters);
        } catch (ExceptionInMyStructurePackage e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Merger merger = new Merger(myStructureFile1.getAllAminochains()[0], myStructureFile2.getAllAminochains()[0], algoParameters);

        MyStructureIfc mergedMyStructure = merger.getMerge();
        return mergedMyStructure;
    }


    //-------------------------------------------------------------
    // Implementation
    //-------------------------------------------------------------
    private static void removeBondsToMyAtomsNotInMyStructure(List<MyAtomIfc> tempListMyAtomIfc) {
        for (MyAtomIfc atom : tempListMyAtomIfc) {
            MyBondIfc[] bonds = atom.getBonds();
            if (bonds != null) {
                for (MyBondIfc bond : bonds) {
                    MyAtomIfc bondedAtom = bond.getBondedAtom();
                    if (!tempListMyAtomIfc.contains(bondedAtom)) {
                        atom.removeBond(bond);
                    }
                }
            }
        }
    }


    //	public static String fixFullAtomName(String name, Group currentGroup){
    //
    //		if (name.equals("N")){
    //			return " N  ";
    //		}
    //
    //		// for amino acids this will be a C alpha
    //		if (currentGroup.getType().equals(GroupType.AMINOACID) && name.equals("CA")){
    //			return " CA ";
    //		}
    //		// for ligands this will be calcium
    //		if (currentGroup.getType().equals(GroupType.HETATM) && name.equals("CA")){
    //			return "CA  ";
    //		}
    //		if (name.equals("C")){
    //			return " C  ";
    //		}
    //		if (name.equals("O")){
    //			return " O  ";
    //		}
    //		if (name.equals("CB")){
    //			return " CB ";
    //		}
    //		if (name.equals("CG"))
    //			return " CG ";
    //
    //		if (name.length() == 2) {
    //			StringBuilder b = new StringBuilder();
    //			b.append(" ");
    //			b.append(name);
    //			b.append(" ");
    //			return b.toString();
    //		}
    //
    //		if (name.length() == 1) {
    //			StringBuilder b = new StringBuilder();
    //			b.append(" ");
    //			b.append(name);
    //			b.append("  ");
    //			return b.toString();
    //		}
    //
    //		if (name.length() == 3) {
    //
    //			StringBuilder b = new StringBuilder();
    //			b.append(" ");
    //			b.append(name);
    //			return b.toString();
    //		}
    //
    //		return name;
    //	}


    public static boolean isInList(List<char[]> backBoneAtomName, char[] atomName) {

        for (char[] backboneAtomName : backBoneAtomName) {
            if (Arrays.equals(atomName, backboneAtomName)) {
                return true;
            }
        }
        return false;
    }


    private static char[] getAtomNameOfRepresentativeMyMonomer(MyMonomerIfc monomer) {

        if (Arrays.equals(monomer.getType(), MyMonomerType.AMINOACID.getType())) {
            return "CA".toCharArray();
        }
        if (Arrays.equals(monomer.getType(), MyMonomerType.NUCLEOTIDE.getType())) {
            return "P".toCharArray();
        }
        if (Arrays.equals(monomer.getType(), MyMonomerType.HETATM.getType())) {
            MyAtomIfc representativeAtom = getRepresentativeAtomFromHetAtomResidue(monomer);
            return representativeAtom.getAtomName();
        }
        return null;
    }


    private static MyAtomIfc getRepresentativeAtomFromHetAtomResidue(MyMonomerIfc monomer) {

        // First we keep according to specific cases
        // HOH ("HOH", "O"),
        // CL ("CL ", "Cl"),
        // ACT ("ACT", "CH3");
        for (MyAtomIfc atom : monomer.getMyAtoms()) {
            for (HetatmResidues hetatmResidues : HetatmResidues.values()) {

                if (Arrays.equals(atom.getAtomName(), hetatmResidues.getbackBoneAtomName().toCharArray()) && Arrays.equals(monomer.getThreeLetterCode(), hetatmResidues.getThreeLetterCode().toCharArray())) {
                    return atom;
                }
            }
        }

        for (MyAtomIfc atom : monomer.getMyAtoms()) {
            if (String.valueOf(atom.getAtomName()).equals("CA")) {
                return atom; // MK8 for instance, I guess all polymeric residues will go through that
            }
        }

        for (MyAtomIfc atom : monomer.getMyAtoms()) {
            if (String.valueOf(atom.getAtomName()).equals("P")) {
                return atom;
            }
        }

        // for others residue representative atom is the one closest to barycenter
        MyAtomIfc atomClosestToBarycenter = findAtomClosestToBarycenter(monomer);

        return atomClosestToBarycenter;
    }


    private static void copySecStructureInformation(Structure structure, Structure structureToReturn) {


        for (Chain chain : structure.getChains()) {

            // collect sec struc info from input chain
            //Map<ResidueNumber, SecStrucInfo> mapResIdAndSecStruc = new HashMap<>();
            //for (Group amino: chain.getAtomGroups(GroupType.AMINOACID)){
            //	AminoAcid aa = (AminoAcid) amino;
            //	SecStrucInfo readsecStruc = (SecStrucInfo) aa.getProperty(Group.SEC_STRUC);
            //	mapResIdAndSecStruc.put(amino.getResidueNumber(), readsecStruc);
            //}

            // put this chain info to outputchain
            List<Chain> outputChains = structureToReturn.getChains();
            for (Chain outputChain : outputChains) {
                if (outputChain.getChainID().equals(chain.getChainID())) {
                    for (Group amino : outputChain.getAtomGroups(GroupType.AMINOACID)) {
                        int newAAId = amino.getResidueNumber().getSeqNum();
                        //for (Entry<ResidueNumber, SecStrucInfo> entry: mapResIdAndSecStruc.entrySet()){
                        //	if (entry.getKey().getSeqNum().equals(newAAId)){
                        //		AminoAcid aa = (AminoAcid) amino;
                        //String type = AdapterBioJavaStructure.convertSecType(entry.getValue());

                        //		aa.setProperty(Group.SEC_STRUC, mapResIdAndSecStruc.get(newAAId));
                        //	}
                        //}
                    }
                }
            }
        }
    }


    public static List<PointIfc> makeQueryPointsFromMyChainIfc(MyChainIfc myChain) {

        List<PointIfc> listPoints = new ArrayList<>();
        for (MyMonomerIfc monomer : myChain.getMyMonomers()) {
            for (MyAtomIfc atom : monomer.getMyAtoms()) {
                float[] coords = atom.getCoords();
                PointIfc point = new Point(coords);
                listPoints.add(point);
            }
        }
        return listPoints;
    }


    private static void regroupHetAtmGroupAfterProtonationUsingStructureNotClearIfItWorks(Structure structureToReturn) {

        for (int chainId = 0; chainId < structureToReturn.size(); chainId++) {
            Chain chain = structureToReturn.getChain(chainId);

            List<Group> listGroupsHetattm = chain.getAtomGroups(GroupType.HETATM);
            List<Group> groupToRemove = new ArrayList<>();
            for (int i = 0; i < listGroupsHetattm.size(); i++) {
                Group group1 = listGroupsHetattm.get(i);
                for (int j = i; j < listGroupsHetattm.size(); j++) {
                    if (i != j) {
                        Group group2 = listGroupsHetattm.get(j);
                        if (group1.getResidueNumber().getSeqNum().intValue() == group2.getResidueNumber().getSeqNum().intValue()) {
                            System.out.println("we have a solved case of splitted hetatm residue after protonation  : " + group1.getResidueNumber().getSeqNum().intValue());
                            List<Atom> atomsToMove = group2.getAtoms();
                            for (Atom atom : atomsToMove) {
                                group1.addAtom(atom);
                            }
                            groupToRemove.add(group2);
                        }
                    }
                }
            }
            listGroupsHetattm.removeAll(groupToRemove);
        }
    }


    private static Chain regroupHetAtmGroupAfterProtonation(Structure structureToReturn) {
        Chain chainToReturn;
        // needed to check if duplicates residue id
        // I had a case with 2qlj chainE ACE residue which is hetatm
        // proton added is added at the end so then there are two hetatm residue with the same id
        // I wish to group them
        // this one has hydrogens
        chainToReturn = structureToReturn.getChain(0);
        List<Group> listGroupsHetattm = chainToReturn.getAtomGroups(GroupType.HETATM);
        List<Group> groupToRemove = new ArrayList<>();
        for (int i = 0; i < listGroupsHetattm.size(); i++) {
            Group group1 = listGroupsHetattm.get(i);
            for (int j = i; j < listGroupsHetattm.size(); j++) {
                if (i != j) {
                    Group group2 = listGroupsHetattm.get(j);
                    if (group1.getResidueNumber().getSeqNum().intValue() == group2.getResidueNumber().getSeqNum().intValue()) {
                        System.out.println("we have a solved case of splitted hetatm residue after protonation  : " + group1.getResidueNumber().getSeqNum().intValue());
                        List<Atom> atomsToMove = group2.getAtoms();
                        for (Atom atom : atomsToMove) {
                            group1.addAtom(atom);
                        }
                        groupToRemove.add(group2);
                    }
                }
            }
        }
        listGroupsHetattm.removeAll(groupToRemove);
        return chainToReturn;
    }


    public static List<char[]> makeHydrogensName(int size, String heavyAtomName) {

        List<char[]> hydrogens = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            hydrogens.add(makeHydrogenAtomName(i + 1, heavyAtomName));
        }
        return hydrogens;
    }


    public static char[] makeHydrogenAtomName(int hydrogenId, String heavyAtomName) {

        String name = "H" + (hydrogenId) + heavyAtomName;
        return name.toCharArray();
    }


    private static List<MyAtomIfc> extractMyAtoms(MyStructureIfc myStructure) {
        List<MyAtomIfc> tempListMyAtomIfc = new ArrayList<>();

        for (MyChainIfc chain : myStructure.getAllChains()) {
            for (MyMonomerIfc monomer : chain.getMyMonomers()) {
                for (MyAtomIfc atom : monomer.getMyAtoms()) {
                    tempListMyAtomIfc.add(atom);
                }
            }
        }
        return tempListMyAtomIfc;
    }


    private static List<MyAtomIfc> extractMyAtoms(MyChainIfc[] myChains) {
        List<MyAtomIfc> tempListMyAtomIfc = new ArrayList<>();

        for (MyChainIfc myChain : myChains) {
            for (MyMonomerIfc monomer : myChain.getMyMonomers()) {
                for (MyAtomIfc atom : monomer.getMyAtoms()) {
                    tempListMyAtomIfc.add(atom);
                }
            }
        }
        return tempListMyAtomIfc;
    }


    private static List<MyAtomIfc> extractMyAtoms(MyChainIfc myChain) {
        List<MyAtomIfc> tempListMyAtomIfc = new ArrayList<>();

        for (MyMonomerIfc monomer : myChain.getMyMonomers()) {
            for (MyAtomIfc atom : monomer.getMyAtoms()) {
                tempListMyAtomIfc.add(atom);
            }
        }
        return tempListMyAtomIfc;
    }


    private static List<MyAtomIfc> extractMyAtoms(MyMonomerIfc myMonomer) {
        List<MyAtomIfc> tempListMyAtomIfc = new ArrayList<>();

        for (MyAtomIfc atom : myMonomer.getMyAtoms()) {
            tempListMyAtomIfc.add(atom);
        }
        return tempListMyAtomIfc;
    }


    private static List<MyAtomIfc> extractMyAtomsFromMyChain(MyChainIfc myChain) {
        List<MyAtomIfc> tempListMyAtomIfc = new ArrayList<>();

        for (MyMonomerIfc monomer : myChain.getMyMonomers()) {
            for (MyAtomIfc atom : monomer.getMyAtoms()) {
                tempListMyAtomIfc.add(atom);
            }
        }

        return tempListMyAtomIfc;
    }
}