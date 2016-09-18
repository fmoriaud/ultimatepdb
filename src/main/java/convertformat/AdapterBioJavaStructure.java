package convertformat;

import java.util.*;

import math.AddToMap;
import math.ToolsMath;
import mystructure.*;
import org.biojava.nbio.structure.*;
import parameters.AlgoParameters;

public class AdapterBioJavaStructure {
    private EnumMyReaderBiojava enumMyReaderBiojava;

    private AlgoParameters algoParameters;

    private List<MyChainIfc> aminoChains = new ArrayList<>();
    private List<MyChainIfc> hetatmChains = new ArrayList<>();
    private List<MyChainIfc> nucleotidesChains = new ArrayList<>();

    private List<MyMonomerIfc> tempMyMonomerList = new ArrayList<>();
    private List<MyAtomIfc> tempMyAtomList = new ArrayList<>();

    private Map<MyAtomIfc, Atom> tempMapMyAtomToAtomAsHelperToBuildMyBond = new HashMap<>();
    private Map<Atom, MyAtomIfc> tempMapAtomToMyAtomAsHelperToBuildMyBond = new HashMap<>();

    private List<MyBondIfc> tempMyBondList = new ArrayList<>();

    private List<MyAtomIfc> listAtom1 = new ArrayList<>();
    private List<MyAtomIfc> listAtom2 = new ArrayList<>();

    //-------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------
    public AdapterBioJavaStructure(AlgoParameters algoParameters) {

        this.algoParameters = algoParameters;
    }


    //-------------------------------------------------------------
    // Interface & Public methods
    //-------------------------------------------------------------
    public MyStructureIfc getMyStructureAndSkipHydrogens(Structure structure, EnumMyReaderBiojava enumMyReaderBiojava) throws ExceptionInMyStructurePackage, ReadingStructurefileException {

        this.enumMyReaderBiojava = enumMyReaderBiojava;

        MyStructureIfc myStructure = convertStructureToMyStructure(structure, algoParameters);

        return myStructure;
    }


    public MyStructureIfc convertStructureToMyStructure(Structure structure, AlgoParameters algoParameters) throws ReadingStructurefileException, ExceptionInMyStructurePackage {

        aminoChains.clear();
        hetatmChains.clear();
        nucleotidesChains.clear();

        char[] fourLetterCode = structure.getPDBCode().toCharArray();

        int countOfChains = structure.getChains().size();
        Chain chain;
        List<Group> listGroupsAmino;
        int countOfAmino;
        List<Group> listGroupsHetAtm;
        int countOfHetAtm;
        List<Group> listGroupsNucleotide;
        int countOfNucleotide;

        tempMapMyAtomToAtomAsHelperToBuildMyBond.clear();
        tempMapAtomToMyAtomAsHelperToBuildMyBond.clear();

        for (int i = 0; i < countOfChains; i++) {

            chain = structure.getChain(i);

            listGroupsAmino = chain.getAtomGroups(GroupType.AMINOACID);
            cleanListOfGroup(listGroupsAmino);
            countOfAmino = listGroupsAmino.size();
            if (countOfAmino > 0) {
                char[] chainType = "amino".toCharArray();
                MyChainIfc aminoChain = createAChainFromAListOfGroups(listGroupsAmino, countOfAmino, algoParameters, chainType);
                if (aminoChain != null) {
                    aminoChains.add(aminoChain);
                }
            }

            listGroupsHetAtm = chain.getAtomGroups(GroupType.HETATM);
            cleanListOfGroup(listGroupsHetAtm);
            countOfHetAtm = listGroupsHetAtm.size();
            if (countOfHetAtm > 0) {
                char[] chainType = "hetatm".toCharArray();
                MyChainIfc hetatomChain = createAChainFromAListOfGroups(listGroupsHetAtm, countOfHetAtm, algoParameters, chainType);
                if (hetatomChain != null) {
                    hetatmChains.add(hetatomChain);
                }
            }

            listGroupsNucleotide = chain.getAtomGroups(GroupType.NUCLEOTIDE);
            cleanListOfGroup(listGroupsNucleotide);
            countOfNucleotide = listGroupsNucleotide.size();
            if (countOfNucleotide > 0) {
                char[] chainType = "nucleotide".toCharArray();
                MyChainIfc nucleotideChain = createAChainFromAListOfGroups(listGroupsNucleotide, countOfNucleotide, algoParameters, chainType);
                if (nucleotideChain != null) {
                    nucleotidesChains.add(nucleotideChain);
                }
            }
        }

        if (aminoChains.size() == 0 && nucleotidesChains.size() == 0) {
            // then nothing to do, problem in the file
            String message = "Only empty amino chain and empty nucleotides chain were parsed for " + String.valueOf(fourLetterCode);
            ReadingStructurefileException exception = new ReadingStructurefileException(message);
            //there still can be hetatom with only one atom because not resolved completely ...
            throw exception;
        }

        // define bonds before building MyStructure otherwise the call for building neighbors by Bond won't work
        int countOfBonds = 0;
        for (MyChainIfc myChain : aminoChains) {
            int count = defineBonds(myChain);
            countOfBonds += count;
        }
        for (MyChainIfc myChain : hetatmChains) {
            int count = defineBonds(myChain);
            countOfBonds += count;
        }
        for (MyChainIfc myChain : nucleotidesChains) {
            int count = defineBonds(myChain);
            countOfBonds += count;
        }

        Set<ExperimentalTechnique> expTechniqueBiojava = structure.getPDBHeader().getExperimentalTechniques();
        ExpTechniquesEnum expTechniqueUltimate = convertExpTechniques(expTechniqueBiojava);
        MyStructureIfc myStructure = new MyStructure(MyStructureTools.makeArrayFromList(aminoChains), MyStructureTools.makeArrayFromList(hetatmChains), MyStructureTools.makeArrayFromList(nucleotidesChains), expTechniqueUltimate, algoParameters);
        myStructure.setFourLetterCode(fourLetterCode);

        if (countOfBonds > 0) {
            addHydrogens();
        } else {
            System.out.println("countOfBonds = " + countOfBonds);
            System.out.println("because bond orders not set in Structure from BioJava");
            System.out.println("Terminating");
            System.exit(0);
        }
        moveHetatmResiduesThatAreBoundCovalentlyToAnAminoResidue(myStructure);

        return myStructure;
    }


    //-------------------------------------------------------------
    // Implementation
    //-------------------------------------------------------------

    private void moveHetatmResiduesThatAreBoundCovalentlyToAnAminoResidue(MyStructureIfc myStructure) throws ExceptionInMyStructurePackage {

        // look at hetatm residue if they can bind an amino
        float thresholdDistance = 2.0f;

        // loop on all pair of atoms to find out covalent bonds
        // I loop to find out covalent bonds between hetatm and amino
        Map<MyAtomIfc, MyAtomIfc> covalentbonds = new LinkedHashMap<>();

        MyChainIfc[] hetatmChains = myStructure.getAllHetatmchains();
        for (MyChainIfc hetatmChain : hetatmChains) {
            MyMonomerIfc[] hetatmMonomers = hetatmChain.getMyMonomers();
            for (MyMonomerIfc hetatmMonomer : hetatmMonomers) {

                MyChainIfc[] neighbors = hetatmMonomer.getNeighboringAminoMyMonomerByRepresentativeAtomDistance();
                for (MyChainIfc neighborsInchain : neighbors) {
                    for (MyMonomerIfc neighborMymonomer : neighborsInchain.getMyMonomers()) {
                        for (MyAtomIfc neighborAtom : neighborMymonomer.getMyAtoms()) {

                            // for each neighborAtom I look at distance to any of the hetarom
                            for (MyAtomIfc atomFroHetAtom : hetatmMonomer.getMyAtoms()) {
                                float distance = ToolsMath.computeDistance(neighborAtom.getCoords(), atomFroHetAtom.getCoords());
                                if (distance < thresholdDistance) {
                                    covalentbonds.put(atomFroHetAtom, neighborAtom);
                                }
                            }
                        }
                    }
                }
            }
        }

        // create bonds
        for (Map.Entry<MyAtomIfc, MyAtomIfc> covalentbond : covalentbonds.entrySet()) {

            MyAtomIfc hetatom = covalentbond.getKey();
            MyAtomIfc aminoatom = covalentbond.getValue();
            MyBondIfc newBond1 = new MyBond(aminoatom, 1);
            hetatom.addBond(newBond1);
            MyBondIfc newBond2 = new MyBond(hetatom, 1);
            aminoatom.addBond(newBond2);
        }

        // loop on covalentbonds and build a unique list of MyMonomer from hetatm to insert
        List<MyMonomerIfc> monomersToInsert = new ArrayList<>();
        for (
                Map.Entry<MyAtomIfc, MyAtomIfc> covalentbond : covalentbonds.entrySet())

        {

            MyAtomIfc hetatom = covalentbond.getKey();
            MyAtomIfc aminoatom = covalentbond.getValue();

            if (!monomersToInsert.contains(hetatom.getParent())) {
                monomersToInsert.add(hetatom.getParent());
            }
        }

        // insert them based on residue Id or add at the end
        // and delete from hetchain
        for (MyMonomerIfc monomerToInsert : monomersToInsert) {

            int residueIdToInsert = monomerToInsert.getResidueID();
            char[] chainId = monomerToInsert.getParent().getChainId();
            MyChainIfc aminoChain = myStructure.getAminoMyChain(chainId);
            // look if there is a gap
            if (aminoChain.getMyMonomerFromResidueId(residueIdToInsert) == null) {
                aminoChain.addAtCorrectRank(monomerToInsert);
                System.out.println("moved " + monomerToInsert);
                monomerToInsert.getParent().removeMyMonomer(monomerToInsert);

            } else {
                // add at the end
                aminoChain.addLastRank(monomerToInsert);
                monomerToInsert.getParent().removeMyMonomer(monomerToInsert);
                System.out.println("moved " + monomerToInsert);
            }
        }

    }


    private ExpTechniquesEnum convertExpTechniques(Set<ExperimentalTechnique> expTechniqueBiojava) {

        if (expTechniqueBiojava.contains(ExperimentalTechnique.XRAY_DIFFRACTION)) {
            return ExpTechniquesEnum.XRAY_DIFFRACTION;
        }
        if (expTechniqueBiojava.contains(ExperimentalTechnique.SOLUTION_NMR)) {
            return ExpTechniquesEnum.SOLUTION_NMR;
        }
        if (expTechniqueBiojava.contains(ExperimentalTechnique.SOLID_STATE_NMR)) {
            return ExpTechniquesEnum.SOLID_STATE_NMR;
        }
        if (expTechniqueBiojava.contains(ExperimentalTechnique.ELECTRON_MICROSCOPY)) {
            return ExpTechniquesEnum.ELECTRON_MICROSCOPY;
        }
        if (expTechniqueBiojava.contains(ExperimentalTechnique.ELECTRON_CRYSTALLOGRAPHY)) {
            return ExpTechniquesEnum.ELECTRON_CRYSTALLOGRAPHY;
        }
        if (expTechniqueBiojava.contains(ExperimentalTechnique.FIBER_DIFFRACTION)) {
            return ExpTechniquesEnum.FIBER_DIFFRACTION;
        }
        if (expTechniqueBiojava.contains(ExperimentalTechnique.NEUTRON_DIFFRACTION)) {
            return ExpTechniquesEnum.NEUTRON_DIFFRACTION;
        }
        if (expTechniqueBiojava.contains(ExperimentalTechnique.POWDER_DIFFRACTION)) {
            return ExpTechniquesEnum.POWDER_DIFFRACTION;
        }
        if (expTechniqueBiojava.contains(ExperimentalTechnique.SOLUTION_SCATTERING)) {
            return ExpTechniquesEnum.SOLUTION_SCATTERING;
        }
        return ExpTechniquesEnum.UNDEFINED;
    }


    private void cleanListOfGroup(List<Group> listGroups) {

        // check alternate location in structure there is in 2FA1 GLN A 197
        // I simply get rid of amino acids with alternate location

        // if there is only one atom in a residue I skip the monomer: lets see what happen for 1dgi
        // indirectly I have problems later on with atom with no bonds set
        // I prefer to fix the reason than the consequence

        Iterator<Group> it = listGroups.iterator();

        while (it.hasNext()) {
            Group group = it.next();
            if (group.hasAltLoc() == true) {


                //it.remove();
                //System.out.println(structure.getPDBCode() + "  " + group.getPDBName() + "  " + group.getResidueNumber() + " has alt loc so removed ");
                //continue;
            }
            if (group.getAtoms().size() == 1 && (group.getPDBName().equals("HOH") || group.getPDBName().equals("CA"))) { // only CA and HOH
                it.remove();
                //System.out.println(structure.getPDBCode() + "  " + group.getPDBName() + "  " + group.getResidueNumber() + " has only one atom so removed  " + group.getAtoms().get(0).getFullName());
            }
        }
    }


    private void addHydrogens() {

        // I dont have the bonded atoms by distance
        // I use the atom name


    }


    private MyChainIfc createAChainFromAListOfGroups(List<Group> listGroups, int countOfGroups, AlgoParameters algoParameters, char[] chainType) {

        tempMyMonomerList.clear();
        int countOfAtoms;
        Group currentGroup;

        for (int j = 0; j < countOfGroups; j++) {

            tempMyAtomList.clear();
            currentGroup = listGroups.get(j);
            countOfAtoms = currentGroup.getAtoms().size();

            Atom atom = null;
            //System.out.println(countOfAtoms + " read atom");
            for (int k = 0; k < countOfAtoms; k++) {
                atom = currentGroup.getAtom(k);
                // TODO Figure out if it is nice to skip H as I also delete them when I build the MyStructure
                // I would say I should skip the one from exp files as not reliable
                if (atom.getElement().equals(Element.H)) {
                    continue;
                }

                char[] atomElement = atom.getElement().toString().toCharArray();
                char[] atomName = atom.getName().toCharArray();
                float[] coords = ToolsMath.convertToFloatArray(atom.getCoords());
                int originalAtomID = atom.getPDBserial();
                MyAtomIfc myAtom;
                try {
                    myAtom = new MyAtom(atomElement, coords, atomName, originalAtomID);
                } catch (ExceptionInMyStructurePackage e) {
                    continue;
                }
                tempMyAtomList.add(myAtom);

                tempMapMyAtomToAtomAsHelperToBuildMyBond.put(myAtom, atom);
                tempMapAtomToMyAtomAsHelperToBuildMyBond.put(atom, myAtom);
            }

            MyAtomIfc[] myAtoms = tempMyAtomList.toArray(new MyAtomIfc[tempMyAtomList.size()]);

            char[] threeLetterCode = getThreeLetterCode(currentGroup);
            if (String.valueOf(threeLetterCode).equals("UNK")) {
                continue;
            }
            int residueId = currentGroup.getResidueNumber().getSeqNum();

            char insertionLetter = 0;
            if (currentGroup.getResidueNumber().getInsCode() != null) {
                insertionLetter = currentGroup.getResidueNumber().getInsCode().toString().toCharArray()[0];
            }

            MyMonomerType monomerType = MyStructureTools.convertType(currentGroup.getType());
            MyMonomerIfc myMonomer;
            try {
                myMonomer = new MyMonomer(myAtoms, threeLetterCode, residueId, monomerType, insertionLetter);
            } catch (ExceptionInMyStructurePackage e) {
                continue;
            }
            addParentReference(myMonomer, myAtoms);
            tempMyMonomerList.add(myMonomer);
        }

        if (tempMyMonomerList.size() == 0) {
            return null;
        }
        MyMonomerIfc[] myMonomers = tempMyMonomerList.toArray(new MyMonomerIfc[tempMyMonomerList.size()]);

        char[] chainId = listGroups.get(0).getChainId().toCharArray();
        MyChainIfc myChain = new MyChain(myMonomers, chainId);
        addParentReference(myChain, myMonomers);

        return myChain;
    }


    private int defineBonds(MyChainIfc myChain) {

        int countOfBond = 0;
        for (MyMonomerIfc myMonomer : myChain.getMyMonomers()) {

            for (MyAtomIfc myAtom : myMonomer.getMyAtoms()) {
                Atom atom = tempMapMyAtomToAtomAsHelperToBuildMyBond.get(myAtom);
                tempMyBondList.clear();

                int sourceBondCount = 0; //atom.getBonds().size();
                if (atom.getBonds() == null) {
                    continue; // that could happen that an atom has no bond e.g. N in Non polymeric NH2
                }
                for (Bond bond : atom.getBonds()) {

                    Atom bondAtomA = bond.getAtomA();
                    Atom bondAtomB = bond.getAtomB();
                    if (bondAtomA.getElement().equals(Element.H) || bondAtomB.getElement().equals(Element.H)) {
                        continue; // as I skiped Hydrogens I must skip as well bonds to hydrogens
                    }
                    if (bondAtomA != atom && bondAtomB != atom) {
                        System.out.println("Fatal to help debugging : a bond is ill defined in Structure " + bondAtomA.getName() + "  " + bondAtomB.getName());
                        continue;
                    }

                    sourceBondCount += 1;

                    MyAtomIfc bondedAtom = null;
                    if (bondAtomA == atom) {
                        bondedAtom = tempMapAtomToMyAtomAsHelperToBuildMyBond.get(bondAtomB);
                    }
                    if (bondAtomB == atom) {
                        bondedAtom = tempMapAtomToMyAtomAsHelperToBuildMyBond.get(bondAtomA);
                    }
                    if (bondedAtom == null) {
                        System.out.println("Fatal to help debugging : unknown reason " + bondAtomA.getName() + "  " + bondAtomB.getName());
                        continue;
                    }
                    int bondOrder = bond.getBondOrder();
                    MyBondIfc myBond;
                    try {
                        myBond = new MyBond(bondedAtom, bondOrder);
                    } catch (ExceptionInMyStructurePackage e) {
                        continue;
                    }
                    countOfBond += 1;
                    tempMyBondList.add(myBond);
                }

                MyBondIfc[] myBonds = tempMyBondList.toArray(new MyBondIfc[tempMyBondList.size()]);
                int outputBondCount = myBonds.length;
                //System.out.println(sourceBondCount + "  " + outputBondCount);
                if (sourceBondCount != outputBondCount) {
                    System.out.println("Error in defineBonds ");
                    continue;
                }
                myAtom.setBonds(myBonds);
            }
        }

        // need to add the bonds between N and C
        //int sourceBondCount = atom.getBonds().size();
        //if (atom.getFullName().equals(" C  ")){
        //	System.out.println();
        //}
        // I dont assume consecutive C to N
        // TODO FMM make that static
        listAtom1.clear();
        listAtom2.clear();
        for (MyMonomerIfc myMonomer : myChain.getMyMonomers()) {
            for (MyAtomIfc myAtom : myMonomer.getMyAtoms()) {
                if (Arrays.equals(myAtom.getAtomName(), "N".toCharArray())) {
                    listAtom2.add(myAtom);
                }
                if (Arrays.equals(myAtom.getAtomName(), "C".toCharArray())) {
                    listAtom1.add(myAtom);
                }
            }
        }

        for (MyAtomIfc atomC : listAtom1) {
            for (MyAtomIfc atomN : listAtom2) {
                float distance = ToolsMath.computeDistance(atomC.getCoords(), atomN.getCoords());
                if (distance < 1.5) {
                    MyBondIfc myBondCtoN;
                    try {
                        myBondCtoN = new MyBond(atomN, 1);
                        atomC.addBond(myBondCtoN);
                        countOfBond += 1;
                    } catch (ExceptionInMyStructurePackage e) {
                    }

                    MyBondIfc myBondNtoC;
                    try {
                        myBondNtoC = new MyBond(atomC, 1);
                        atomN.addBond(myBondNtoC);
                        countOfBond += 1;
                    } catch (ExceptionInMyStructurePackage e) {
                    }
                }
            }
        }

        // like in 101M and I dont know why some atoms are not connected
        // It is very costly all atom to all atom

        return countOfBond;
    }


    private char[] getThreeLetterCode(Group currentGroup) {
        char[] threeLetterCode = null;
        if (currentGroup.getChemComp().getThree_letter_code() != null) {
            threeLetterCode = currentGroup.getChemComp().getThree_letter_code().toCharArray();
        } else {
            threeLetterCode = currentGroup.getPDBName().toCharArray();
        }
        return threeLetterCode;
    }


    private void addParentReference(MyMonomerIfc myMonomer, MyAtomIfc[] myAtoms) {

        for (MyAtomIfc atom : myAtoms) {
            atom.setParent(myMonomer);
        }
    }


    private void addParentReference(MyChainIfc myChain, MyMonomerIfc[] myMonomers) {

        for (MyMonomerIfc monomer : myMonomers) {
            monomer.setParent(myChain);
        }
    }


    private static void updateMyMonomerParentReference(MyChainIfc myChain) {

        for (MyMonomerIfc myMonomer : myChain.getMyMonomers()) {
            myMonomer.setParent(myChain);
        }
    }


    private static MyMonomerIfc findMonomerJustBefore(int monomerId, MyChainIfc chain) {

        for (int i = chain.getMyMonomers().length - 1; i >= 0; i--) {
            MyMonomerIfc monomer = chain.getMyMonomerByRank(i);
            if (monomer.getResidueID() < monomerId) {
                return monomer;
            }
        }
        return null;
    }


    private static boolean isThatMonomerChainIdAndResidueIdFitsInAGapOfAnAminoChain(MyStructureIfc myStructure, Map<String, Set<Integer>> mapChainAndResidueIDBeforeAGap, MyMonomerIfc monomer) {

        int residueIdOfMonomerToInsert = monomer.getResidueID();
        char[] chainId = monomer.getParent().getChainId();
        if (mapChainAndResidueIDBeforeAGap.containsKey(String.valueOf(chainId))) {
            //
            Set<Integer> residuesBeforeGapsInChain = mapChainAndResidueIDBeforeAGap.get(String.valueOf(chainId));
            if (residuesBeforeGapsInChain.contains(residueIdOfMonomerToInsert - 1)) {
                return true;
            }
        }
        return false;
    }


    private static Map<String, Set<Integer>> findGapsInAminoChains(MyStructureIfc myStructure) {

        Map<String, Set<Integer>> mapChainAndResidueIDBeforeAGap = new HashMap<>();
        for (MyChainIfc chain : myStructure.getAllAminochains()) {

            // residues withan id > 0 and before first element are considered as a gap
            // maybe silly but works for 2qlj E
            int residueIdOfFirstMonomerInChain = chain.getMyMonomerByRank(0).getResidueID();
            if (residueIdOfFirstMonomerInChain > 1) {
                for (int j = 1; j < residueIdOfFirstMonomerInChain; j++) {
                    AddToMap.addElementToAMapOfSet(mapChainAndResidueIDBeforeAGap, String.valueOf(chain.getChainId()), j);
                }
            }

            for (int i = 1; i < chain.getMyMonomers().length; i++) {
                MyMonomerIfc currentMonomer = chain.getMyMonomerByRank(i); // guess they are sorted according to residue ID
                MyMonomerIfc previousMonomer = chain.getMyMonomerByRank(i - 1);
                if (currentMonomer.getResidueID() > previousMonomer.getResidueID() + 1) {
                    //
                    //System.out.println("Found a gap in amino chain " + chain.getChainId() + " after position " +  previousMonomer.getResidueID());
                    // I should add all id in between !!!
                    int startId = previousMonomer.getResidueID();
                    int endId = currentMonomer.getResidueID();
                    for (int j = startId; j < endId - 1; j++) {
                        AddToMap.addElementToAMapOfSet(mapChainAndResidueIDBeforeAGap, String.valueOf(chain.getChainId()), j);
                    }
                }
            }
        }
        return mapChainAndResidueIDBeforeAGap;
    }
}
