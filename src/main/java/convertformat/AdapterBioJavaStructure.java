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

import math.MathTools;
import mystructure.*;
import org.biojava.nbio.structure.*;
import parameters.AlgoParameters;

import java.util.*;

public class AdapterBioJavaStructure {
    //-------------------------------------------------------------
    // Class variables
    //-------------------------------------------------------------
    private AlgoParameters algoParameters;

    private List<MyChainIfc> aminoChains = new ArrayList<>();
    private List<MyChainIfc> hetatmChains = new ArrayList<>();
    private List<MyChainIfc> nucleotidesChains = new ArrayList<>();

    private List<MyMonomerIfc> tempMyMonomerList = new ArrayList<>();
    private List<MyAtomIfc> tempMyAtomList = new ArrayList<>();

    private List<Group> excludedGroup = new ArrayList<>();

    private Map<MyAtomIfc, Atom> tempMapMyAtomToAtomAsHelperToBuildMyBond = new HashMap<>();
    private Map<Atom, MyAtomIfc> tempMapAtomToMyAtomAsHelperToBuildMyBond = new HashMap<>();

    private List<MyBondIfc> tempMyBondList = new ArrayList<>();

    private List<MyAtomIfc> listAtom1 = new ArrayList<>();
    private List<MyAtomIfc> listAtom2 = new ArrayList<>();

    private boolean skipAllHydrogenAtoms = true;


    //-------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------
    public AdapterBioJavaStructure(AlgoParameters algoParameters) {

        this.algoParameters = algoParameters;
    }


    //-------------------------------------------------------------
    // Interface & Public methods
    //-------------------------------------------------------------
    public MyStructureIfc getMyStructureAndSkipHydrogens(Structure structure, String pdbFileHash) throws ExceptionInMyStructurePackage, ReadingStructurefileException, ExceptionInConvertFormat {

        MyStructureIfc myStructure = convertStructureToMyStructure(structure, algoParameters, pdbFileHash);
        return myStructure;
    }



    //-------------------------------------------------------------
    // Implementation
    //-------------------------------------------------------------
    private MyStructureIfc convertStructureToMyStructure(Structure structure, AlgoParameters algoParameters, String pdbFileHash) throws ReadingStructurefileException, ExceptionInMyStructurePackage, ExceptionInConvertFormat {

        aminoChains.clear();
        hetatmChains.clear();
        nucleotidesChains.clear();

        char[] fourLetterCode = structure.getPDBCode().toUpperCase().toCharArray();

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
            defineBonds(myChain);
        }
        for (MyChainIfc myChain : hetatmChains) {
            defineBonds(myChain);
        }
        for (MyChainIfc myChain : nucleotidesChains) {
            defineBonds(myChain);
        }

        Set<ExperimentalTechnique> expTechniqueBiojava = structure.getPDBHeader().getExperimentalTechniques();
        ExpTechniquesEnum expTechniqueUltimate = convertExpTechniques(expTechniqueBiojava);

        MyChainIfc[] aminoArray = MyStructureTools.makeArrayFromListMyChains(aminoChains);
        MyChainIfc[] hetatmArray = MyStructureTools.makeArrayFromListMyChains(hetatmChains);
        MyChainIfc[] nucleotidesArray = MyStructureTools.makeArrayFromListMyChains(nucleotidesChains);

        // that computes the neighbors by representative distance
        // needed by moveHetatmResiduesThatAreBoundCovalentlyToChains
        MyStructureTools.computeAndStoreNeighBorhingAminoMonomersByDistanceBetweenRepresentativeMyAtom(algoParameters, aminoArray, hetatmArray, nucleotidesArray);

        // move covalently bound heatm to respective amino and nucleosides chains
        // Done one after the other
        moveHetatmResiduesThatAreBoundCovalentlyToChains(hetatmArray, aminoArray);
        moveHetatmResiduesThatAreBoundCovalentlyToChains(hetatmArray, nucleotidesArray);

        // I move all content as there might be some hetatm as well that were moved so covalently bound wont work
        moveNucleosidesIfTheyAreInAchainWithSameIdAsAminoChainAndCovalentlyBound(aminoArray, nucleotidesArray);

        MyChainIfc[] cleanaminoArray = removeEmptychains(aminoArray);
        MyChainIfc[] cleanhetatmArray = removeEmptychains(hetatmArray);
        MyChainIfc[] cleannucleotidesArray = removeEmptychains(nucleotidesArray);

        MyStructureIfc myStructure = new MyStructure(cleanaminoArray, cleanhetatmArray, cleannucleotidesArray, expTechniqueUltimate, algoParameters, pdbFileHash);
        myStructure.setFourLetterCode(fourLetterCode);

        // Must redo neighbors by distance. They are the same but some MyMonomer Hetatm moved to Amino or Nucleosides chains.
        MyStructureTools.computeAndStoreNeighBorhingAminoMonomersByDistanceBetweenRepresentativeMyAtom(algoParameters, aminoArray, hetatmArray, nucleotidesArray);
        // Do the neighbors by Bond here. So they include the new bonds created by moving MyMonomer from Hetatm to Amino or Nucleosides chains.
        MyStructureTools.computeAndStoreNeighboringMonomersByBond(myStructure);
        return myStructure;
    }


    private MyChainIfc[] removeEmptychains(MyChainIfc[] myChains) {

        List<MyChainIfc> keptMyChain = new ArrayList<>();
        for (MyChainIfc myChain : myChains) {
            if (myChain.getMyMonomers().length > 0) {
                keptMyChain.add(myChain);
            }
        }
        return MyStructureTools.makeArrayFromListMyChains(keptMyChain);
    }


    private void moveNucleosidesIfTheyAreInAchainWithSameIdAsAminoChainAndCovalentlyBound(MyChainIfc[] currentAminoChains, MyChainIfc[] currentNucleosidesChains) {

        List<char[]> aminoChainIds = new ArrayList<>();
        for (MyChainIfc chain : currentAminoChains) {
            aminoChainIds.add(chain.getChainId());
        }

        for (MyChainIfc candidateChain : currentNucleosidesChains) {
            for (char[] aminoChainId : aminoChainIds) {
                if (Arrays.equals(aminoChainId, candidateChain.getChainId())) {
                    System.out.println(" Nucleoside chain " + String.valueOf(aminoChainId) + " is a candidate ");

                    List<MyMonomerIfc> monomersToInsert = Arrays.asList(candidateChain.getMyMonomers());
                    insertAndDelete(currentAminoChains, monomersToInsert);
                }
            }
        }
    }


    private void moveHetatmResiduesThatAreBoundCovalentlyToChains(MyChainIfc[] hetatmArray, MyChainIfc[] destinationChains) throws ExceptionInMyStructurePackage {

        move(hetatmArray, destinationChains);
    }


    private void move(MyChainIfc[] chainsWithMovingCandidates, MyChainIfc[] destinationChains) throws ExceptionInMyStructurePackage {

        // look at hetatm residue if they can bind an amino
        float thresholdDistance = 2.0f;

        // loop on all pair of atoms to find out covalent bonds
        // I loop to find out covalent bonds between hetatm and amino
        Map<MyAtomIfc, MyAtomIfc> covalentbonds = new LinkedHashMap<>();

        for (MyChainIfc hetatmChain : chainsWithMovingCandidates) {
            MyMonomerIfc[] hetatmMonomers = hetatmChain.getMyMonomers();
            for (MyMonomerIfc hetatmMonomer : hetatmMonomers) {

                MyChainIfc[] neighbors = hetatmMonomer.getNeighboringAminoMyMonomerByRepresentativeAtomDistance();
                for (MyChainIfc neighborsInchain : neighbors) {
                    for (MyMonomerIfc neighborMymonomer : neighborsInchain.getMyMonomers()) {
                        for (MyAtomIfc neighborAtom : neighborMymonomer.getMyAtoms()) {

                            // for each neighborAtom I look at distance to any of the hetarom
                            for (MyAtomIfc atomFroHetAtom : hetatmMonomer.getMyAtoms()) {
                                float distance = MathTools.computeDistance(neighborAtom.getCoords(), atomFroHetAtom.getCoords());
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

            // TODO should check if bond is not already existing ???
            MyAtomIfc hetatom = covalentbond.getKey();
            MyAtomIfc aminoatom = covalentbond.getValue();
            MyBondIfc newBond1 = new MyBond(aminoatom, 1);
            hetatom.addBond(newBond1);
            MyBondIfc newBond2 = new MyBond(hetatom, 1);
            aminoatom.addBond(newBond2);
        }

        // loop on covalentbonds and build a unique list of MyMonomer from hetatm to insert
        List<MyMonomerIfc> monomersToInsert = new ArrayList<>();
        for (Map.Entry<MyAtomIfc, MyAtomIfc> covalentbond : covalentbonds.entrySet()) {

            MyAtomIfc hetatom = covalentbond.getKey();
            MyAtomIfc aminoatom = covalentbond.getValue();

            if (!monomersToInsert.contains(hetatom.getParent())) {
                hetatom.getParent().setType(MyMonomerType.AMINOACID.getType());
                hetatom.getParent().setWasHetatm(true);
                monomersToInsert.add(hetatom.getParent());
            }
        }
        insertAndDelete(destinationChains, monomersToInsert);
    }


    private void insertAndDelete(MyChainIfc[] destinationChains, List<MyMonomerIfc> monomersToInsert) {
        // insert them based on residue Id or add at the end
        // and delete from hetchain
        for (MyMonomerIfc monomerToInsert : monomersToInsert) {

            char[] chainId = monomerToInsert.getParent().getChainId();
            MyChainIfc relevantChain = null;
            for (MyChainIfc myChain : destinationChains) {
                if (Arrays.equals(myChain.getChainId(), chainId)) {
                    relevantChain = myChain;
                    break;
                }
            }

            if (relevantChain == null) {
                System.out.println("failed to find chain to insert");
                continue;
            }

            int residueIdToInsert = monomerToInsert.getResidueID();

            // look if there is a gap
            if (relevantChain.getMyMonomerFromResidueId(residueIdToInsert) == null) {
                relevantChain.addAtCorrectRank(monomerToInsert);
                System.out.println("moved " + monomerToInsert);
                monomerToInsert.getParent().removeMyMonomer(monomerToInsert);

            } else {
                // add at the end
                relevantChain.addLastRank(monomerToInsert);
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

        // if there is only one atom in a residue I skip the monomer: lets see what happen for 1dgi
        // indirectly I have problems later on with atom with no bonds set
        // I prefer to fix the reason than the consequence

        Iterator<Group> it = listGroups.iterator();

        while (it.hasNext()) {
            Group group = it.next();

            if (group.getAtoms().size() == 1 && (group.getPDBName().equals("HOH"))) { // HOH
                it.remove();
                excludedGroup.add(group);
                //System.out.println(structure.getPDBCode() + "  " + group.getPDBName() + "  " + group.getResidueNumber() + " has only one atom so removed  " + group.getAtoms().get(0).getFullName());
            }
        }
    }


    private MyChainIfc createAChainFromAListOfGroups(List<Group> listGroups, int countOfGroups, AlgoParameters algoParameters, char[] chainType) throws ExceptionInConvertFormat {

        tempMyMonomerList.clear();
        int countOfAtoms;
        Group currentGroup;

        for (int j = 0; j < countOfGroups; j++) {

            tempMyAtomList.clear();

            // Biojava alredy select one altLoc Group
            currentGroup = listGroups.get(j);
            countOfAtoms = currentGroup.getAtoms().size();

            boolean hasAltLoc = currentGroup.hasAltLoc();

            if (countOfAtoms == 1) {
                if (currentGroup.getAtom(0).getName().equals("CA")) {
                    ExceptionInConvertFormat exception = new ExceptionInConvertFormat("Amino residue with only Calpha so giveup");
                    throw exception;
                }
            }
            Atom atom = null;
            //System.out.println(countOfAtoms + " read atom");

            char altLocGroup = " ".toCharArray()[0];
            boolean foundChosenAltGroupByBiojava = false;

            for (int k = 0; k < countOfAtoms; k++) {
                atom = currentGroup.getAtom(k);

                if (skipAllHydrogenAtoms && atom.getElement().equals(Element.H)) {
                    continue;
                }

                char altLoc = atom.getAltLoc();
                if (hasAltLoc == true && foundChosenAltGroupByBiojava == false && altLoc != altLocGroup) {
                    altLocGroup = altLoc;
                    foundChosenAltGroupByBiojava = true;
                }

                char[] atomElement = atom.getElement().toString().toCharArray();
                char[] atomName = atom.getName().toCharArray();
                float[] coords = MathTools.convertToFloatArray(atom.getCoords());
                int originalAtomID = atom.getPDBserial();
                MyAtomIfc myAtom;
                try {
                    myAtom = new MyAtom(atomElement, coords, atomName, originalAtomID);
                } catch (ExceptionInMyStructurePackage e) {
                    ExceptionInConvertFormat exception = new ExceptionInConvertFormat("Not supported atom type found");
                    throw exception;
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
                // TODO add altLocGroup that was chosen
                // Useful when making bonds, then I would safely ignore bonds defined to alt group I didn' keep
                boolean wasHetatm = false;
                myMonomer = new MyMonomer(myAtoms, threeLetterCode, residueId, monomerType, wasHetatm, insertionLetter, altLocGroup);
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


    private void defineBonds(MyChainIfc myChain) throws ExceptionInConvertFormat {

        for (MyMonomerIfc myMonomer : myChain.getMyMonomers()) {

            for (MyAtomIfc myAtom : myMonomer.getMyAtoms()) {
                Atom atom = tempMapMyAtomToAtomAsHelperToBuildMyBond.get(myAtom);
                tempMyBondList.clear();

                int sourceBondCount = 0; //atom.getBonds().size();
                if (atom.getBonds() == null) {
                    continue; // that could happen that an atom has no bond e.g. N in Non polymeric NH2
                }
                Bonds:
                for (Bond bond : atom.getBonds()) {

                    // Because not sure of the order in bond
                    Atom bondBondedAtom = null;
                    Atom bondStartAtom = null;
                    Atom atomA = bond.getAtomA();
                    Atom atomB = bond.getAtomB();
                    if (atomA == atom) {
                        bondStartAtom = atomA;
                        bondBondedAtom = atomB;
                    }
                    if (atomB == atom) {
                        bondStartAtom = atomB;
                        bondBondedAtom = atomA;
                    }

                    if (excludedGroup.contains(bondBondedAtom.getGroup())) {
                        System.out.println("Ignored a bond to excluded group ");
                        continue Bonds; // ignore bonds to water
                    }

                    // if bonded atom in structure has alt Loc then only the chosen one for MyStructure will be found
                    char altLocOfBondedAtom = bondBondedAtom.getAltLoc();

                    if (altLocOfBondedAtom != " ".toCharArray()[0]) {

                        MyMonomerIfc bondedMyMonomer = findBondedMyMonomer(bondBondedAtom);
                        if (bondedMyMonomer == null) {
                            ExceptionInConvertFormat exception = new ExceptionInConvertFormat("Corresponding alt loc residue not found so adapter fails");
                            throw exception;
                        }
                        if (bondedMyMonomer.getAltLocGroup() != altLocOfBondedAtom) {
                            //System.out.println("Safe to skip bond because it was to an alt loc not kept in MyStructure");
                            continue Bonds;
                        }

                    }
                    if (bondStartAtom.getElement().equals(Element.H) || bondBondedAtom.getElement().equals(Element.H)) {
                        continue; // as I skiped Hydrogens I must skip as well bonds to hydrogens
                    }

                    if (bondStartAtom != atom && bondBondedAtom != atom) {
                        System.out.println("Fatal to help debugging : a bond is ill defined in Structure " + bondStartAtom.getName() + "  " + bondBondedAtom.getName());

                        continue Bonds;
                    }

                    sourceBondCount += 1;

                    MyAtomIfc bondedAtomMyStructure = null;
                    if (bondStartAtom == atom) {
                        bondedAtomMyStructure = tempMapAtomToMyAtomAsHelperToBuildMyBond.get(bondBondedAtom);
                    }
                    if (bondBondedAtom == atom) {
                        bondedAtomMyStructure = tempMapAtomToMyAtomAsHelperToBuildMyBond.get(bondStartAtom);
                    }
                    if (bondedAtomMyStructure == null) {
                        System.out.println("Fatal to help debugging : unknown reason " + bondStartAtom.getName() + "  " + bondBondedAtom.getName());
                        continue Bonds;
                    }
                    int bondOrder = bond.getBondOrder();
                    MyBondIfc myBond;
                    try {
                        myBond = new MyBond(bondedAtomMyStructure, bondOrder);
                    } catch (ExceptionInMyStructurePackage e) {
                        continue;
                    }
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
                float distance = MathTools.computeDistance(atomC.getCoords(), atomN.getCoords());
                if (distance < 1.5) {
                    MyBondIfc myBondCtoN;
                    try {
                        myBondCtoN = new MyBond(atomN, 1);
                        atomC.addBond(myBondCtoN);
                    } catch (ExceptionInMyStructurePackage e) {
                    }

                    MyBondIfc myBondNtoC;
                    try {
                        myBondNtoC = new MyBond(atomC, 1);
                        atomN.addBond(myBondNtoC);
                    } catch (ExceptionInMyStructurePackage e) {
                    }
                }
            }
        }
    }


    private MyMonomerIfc findBondedMyMonomer(Atom bondBondedAtom) {

        String chainIDToFind = bondBondedAtom.getGroup().getChainId();
        String threeLetterCode = bondBondedAtom.getGroup().getPDBName();
        int residueId = bondBondedAtom.getGroup().getResidueNumber().getSeqNum();

        for (MyChainIfc myChain : aminoChains) {
            if (Arrays.equals(myChain.getChainId(), chainIDToFind.toCharArray())) {
                MyMonomerIfc candidate = myChain.getMyMonomerFromResidueId(residueId);
                if (candidate == null) {
                    continue;
                }
                if (Arrays.equals(candidate.getThreeLetterCode(), threeLetterCode.toCharArray())) {
                    return candidate;
                }
            }
        }
        for (MyChainIfc myChain : nucleotidesChains) {
            if (Arrays.equals(myChain.getChainId(), chainIDToFind.toCharArray())) {
                MyMonomerIfc candidate = myChain.getMyMonomerFromResidueId(residueId);
                if (candidate == null) {
                    continue;
                }
                if (Arrays.equals(candidate.getThreeLetterCode(), threeLetterCode.toCharArray())) {
                    return candidate;
                }
            }
        }
        for (MyChainIfc myChain : hetatmChains) {
            if (Arrays.equals(myChain.getChainId(), chainIDToFind.toCharArray())) {
                MyMonomerIfc candidate = myChain.getMyMonomerFromResidueId(residueId);
                if (candidate == null) {
                    continue;
                }
                if (Arrays.equals(candidate.getThreeLetterCode(), threeLetterCode.toCharArray())) {
                    return candidate;
                }
            }
        }
        return null;
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
}
