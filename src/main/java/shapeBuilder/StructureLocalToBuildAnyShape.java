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
package shapeBuilder;

import mystructure.*;
import parameters.AlgoParameters;
import parameters.QueryAtomDefinedByIds;

import java.util.*;

public class StructureLocalToBuildAnyShape {
    //-------------------------------------------------------------
    // Class variables
    //-------------------------------------------------------------
    private MyStructureIfc myStructureGlobalBrut;
    private AlgoParameters algoParameters;

    private MyChainIfc ligand;
    private MyStructureIfc myStructureLocal;
    private List<MyMonomerIfc> monomerToDiscard;


    //-------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------
    /**
     * Constructor to get a StructureLocal using a whole structure, some monomer to exclude and a foreign ligand
     * neighbors by representative atom distance must be recomputed
     *
     * @param myStructureGlobalBrut
     * @param foreignMonomerToExclude
     * @param rotatedLigandOrPeptideForeigner
     * @param algoParameters
     * @throws ShapeBuildingException
     */
    public StructureLocalToBuildAnyShape(MyStructureIfc myStructureGlobalBrut, List<MyMonomerIfc> foreignMonomerToExclude, MyStructureIfc rotatedLigandOrPeptideForeigner, AlgoParameters algoParameters) throws ShapeBuildingException {

        this.myStructureGlobalBrut = myStructureGlobalBrut;
        this.algoParameters = algoParameters;

        Cloner cloner = new Cloner(rotatedLigandOrPeptideForeigner, algoParameters);
        MyStructureIfc clonedLigand = cloner.getClone();

        MyStructureTools.computeAndStoreNeighBorhingAminoMonomersByDistanceBetweenRepresentativeMyAtom(algoParameters, myStructureGlobalBrut, clonedLigand, foreignMonomerToExclude);
        // clonedLigand neighbors are ready to build a StructureLocal
        Set<MyMonomerIfc> monomerToKeep = makeMyMonomersLocalAround(clonedLigand);

        Cloner cloner2 = new Cloner(myStructureGlobalBrut, monomerToKeep, algoParameters);
        MyStructureIfc clonedMyStructure = cloner2.getClone();
        myStructureLocal = clonedMyStructure;

        ligand = clonedLigand.getAllChains()[0];
    }


    /**
     * Constructor to get StructureLocal using myStructureGlobalBrut and a chainId
     * Assume myStructureGlobalBrut is good, so neighbors by distance are correct in the chain with chainid
     *
     * @param myStructureGlobalBrut
     * @param chainId
     * @param algoParameters
     */
    public StructureLocalToBuildAnyShape(MyStructureIfc myStructureGlobalBrut, char[] chainId, AlgoParameters algoParameters) throws ShapeBuildingException {

        this.myStructureGlobalBrut = myStructureGlobalBrut;
        this.algoParameters = algoParameters;

        MyMonomerIfc[] myMonomomers = myStructureGlobalBrut.getAminoMyChain(chainId).getMyMonomers();

        Set<MyMonomerIfc> monomerToKeep = makeMyMonomersLocalAroundAndExcludingMyMonomersFromInput(myMonomomers);
        monomerToDiscard = MyStructureTools.makeListFromArray(myMonomomers);

        Cloner cloner = new Cloner(myStructureGlobalBrut, monomerToKeep, algoParameters);
        MyStructureIfc clonedMyStructure = cloner.getClone();

        if (clonedMyStructure.getAllAminochains().length == 0) {
            ShapeBuildingException exception = new ShapeBuildingException("getShapeAroundAChain return no amino chain: likely that the chain has no neighboring chain in that case");
            throw exception;
        }
        myStructureLocal = clonedMyStructure;
        ligand = myStructureGlobalBrut.getAminoMyChain(chainId);
    }


    /**
     * Constructor to get StructureLocal using myStructureGlobalBrut and a hetatm 3 letter code and the occurenceId to use
     * Assume myStructureGlobalBrut is good, so neighbors by distance are correct in the chain with chainid
     *
     * @param myStructureGlobalBrut
     * @param hetAtomsLigandId
     * @param occurrenceId
     * @param algoParameters
     */
    public StructureLocalToBuildAnyShape(MyStructureIfc myStructureGlobalBrut,
                                         char[] hetAtomsLigandId, int occurrenceId, AlgoParameters algoParameters) throws ShapeBuildingException {

        this.myStructureGlobalBrut = myStructureGlobalBrut;
        this.algoParameters = algoParameters;

        MyMonomerIfc hetAtomsGroup = findHetAtomLigand(hetAtomsLigandId, occurrenceId, myStructureGlobalBrut);

        if (hetAtomsGroup == null) {
            System.out.println("ligand hetatm not found");
            String message = "ligand hetatm not found : " + String.valueOf(hetAtomsLigandId) + " " + occurrenceId + " in " + String.valueOf(myStructureGlobalBrut.getFourLetterCode());
            ShapeBuildingException exception = new ShapeBuildingException(message);
            throw exception;
        }

        MyMonomerIfc[] myMonomomers = new MyMonomerIfc[1];
        myMonomomers[0] = hetAtomsGroup;

        Set<MyMonomerIfc> monomerToKeep = makeMyMonomersLocalAroundAndExcludingMyMonomersFromInput(myMonomomers);
        monomerToDiscard = new ArrayList<>();

        Cloner cloner = new Cloner(myStructureGlobalBrut, monomerToKeep, algoParameters);
        MyStructureIfc clonedMyStructure = cloner.getClone();

        Cloner clonerLigand = new Cloner(hetAtomsGroup, algoParameters);
        MyStructureIfc clonedLigand = clonerLigand.getClone();
        ligand = clonedLigand.getAllChains()[0];

        if (clonedMyStructure.getAllAminochains().length == 0) {
            ShapeBuildingException exception = new ShapeBuildingException("getShapeAroundAChain return no amino chain: likely that the chain has no neighboring chain in that case");
            throw exception;
        }
        myStructureLocal = clonedMyStructure;
    }


    /**
     * Constructor to get StructureLocal using myStructureGlobalBrut and a definition of the chain segment to use
     * Assume myStructureGlobalBrut is good, so neighbors by distance are correct in the chain with chainid
     *
     * @param myStructureGlobalBrut
     * @param chainId
     * @param startingRankId which can be from 0 to last id of MyMonmer = size-1
     * @param peptideLength
     * @param algoParameters
     */
    public StructureLocalToBuildAnyShape(MyStructureIfc myStructureGlobalBrut,
                                         char[] chainId, int startingRankId, int peptideLength, AlgoParameters algoParameters) throws ShapeBuildingException {

        this.myStructureGlobalBrut = myStructureGlobalBrut;
        this.algoParameters = algoParameters;

        MyMonomerIfc[] myMonomomers = myStructureGlobalBrut.getAminoMyChain(chainId).getMyMonomers();
        MyChainIfc wholeChain = myStructureGlobalBrut.getAminoMyChain(chainId);

        if (startingRankId >= myMonomomers.length) {
            System.out.println(String.valueOf(myStructureGlobalBrut.getFourLetterCode()) + "  " + String.valueOf(chainId) + " " + startingRankId);
            ShapeBuildingException exception = new ShapeBuildingException("bug ask for startingRankId > chain length");
            throw exception;
        }

        ligand = makeChainSegment(wholeChain, startingRankId, peptideLength, algoParameters);

        MyChainIfc extractedSegment = extractSubChain(wholeChain, startingRankId, peptideLength, algoParameters);
        monomerToDiscard = MyStructureTools.makeListFromArray(extractedSegment.getMyMonomers());

        if (ligand.getMyMonomers().length != peptideLength) {
            ShapeBuildingException exception = new ShapeBuildingException("makeChainSegment failed to return a peptide of the right length. Could be due to PDB parsing missing residues");
            throw exception;
        }

        myStructureLocal = makeStructureLocalForSegmentAroundAndExcludingMyMonomersFromInputMyChain(myStructureGlobalBrut, extractedSegment, algoParameters);
    }


    /**
     * Constructor to get StructureLocal using myStructureGlobalBrut and a definition of queryAtomsDefinedByIds
     * Assume myStructureGlobalBrut is good, so neighbors by distance are correct in the chain with chainid
     *
     * @param myStructureGlobalBrut
     * @param queryAtomsDefinedByIds
     * @param algoParameters
     * @param chainToIgnore
     */
    public StructureLocalToBuildAnyShape(MyStructureIfc myStructureGlobalBrut,
                                         List<QueryAtomDefinedByIds> queryAtomsDefinedByIds, AlgoParameters algoParameters, List<String> chainToIgnore) throws ShapeBuildingException {

        this.myStructureGlobalBrut = myStructureGlobalBrut;
        this.algoParameters = algoParameters;

        List<MyMonomerIfc> monomersContainingAtomsDefinedByIds = findMyMonomersOnlyInAminoChainsContainingAtomsDefinedByIds(myStructureGlobalBrut, queryAtomsDefinedByIds);
        if (monomersContainingAtomsDefinedByIds.isEmpty()) {
            ShapeBuildingException exception = new ShapeBuildingException("Atomids monomers not found in StructureLocalToBuildAnyShape");
            throw exception;
        }
        MyChainIfc correspondingChain = new MyChain(monomersContainingAtomsDefinedByIds);
        myStructureLocal = makeStructureLocalAroundAndWithChain(correspondingChain, chainToIgnore);
    }


    //-------------------------------------------------------------
    // Implementation
    //-------------------------------------------------------------
    private Set<MyMonomerIfc> makeMyMonomersLocalAround(MyStructureIfc clonedLigand) {

        Set<MyMonomerIfc> queryMyMonomer = new HashSet<>();

        for (MyChainIfc myChainLigand : clonedLigand.getAllChains()) {
            for (MyMonomerIfc monomer : myChainLigand.getMyMonomers()) {
                MyChainIfc[] neighbors = monomer.getNeighboringAminoMyMonomerByRepresentativeAtomDistance();
                for (MyChainIfc mychain : neighbors) {
                    for (MyMonomerIfc neighbor : mychain.getMyMonomers()) {
                        queryMyMonomer.add(neighbor);
                    }
                }
            }
        }
        return queryMyMonomer;

    }


    private MyStructureIfc makeStructureLocalAroundAndWithChain(MyChainIfc myChain, List<String> chainToIgnore) {

        Set<MyMonomerIfc> queryMonomers = makeMyMonomersLocalAroundAndWithChain(myChain);

        Cloner cloner = new Cloner(myStructureGlobalBrut, queryMonomers, algoParameters);
        MyStructureIfc myStructureLocal = cloner.getClone();

        ShapeBuildingTools.deleteChains(chainToIgnore, myStructureLocal);

        return myStructureLocal;
    }


    private MyStructureIfc makeStructureLocalForSegmentAroundAndExcludingMyMonomersFromInputMyChain(MyStructureIfc myStructureGlobalBrut, MyChainIfc extractedSegment, AlgoParameters algoParameters) {

        Set<MyMonomerIfc> queryMonomers = makeMyMonomersLocalAroundAndExcludingMyMonomersFromInputMyChain(extractedSegment);

        Cloner cloner = new Cloner(myStructureGlobalBrut, queryMonomers, algoParameters);
        MyStructureIfc clonedMyStructure = cloner.getClone();

        makeCaOnBothSideWithNoBoundAtomsStructureLocal(extractedSegment, clonedMyStructure, myStructureGlobalBrut);

        return clonedMyStructure;
    }


    private Set<MyMonomerIfc> makeMyMonomersLocalAroundAndExcludingMyMonomersFromInputMyChain(MyChainIfc myChain) {

        Set<MyMonomerIfc> queryMyMonomer = makeMyMonomersLocalAroundAndWithChain(myChain);

        for (MyMonomerIfc monomerToRemove : myChain.getMyMonomers()) {
            queryMyMonomer.remove(monomerToRemove);
        }
        return queryMyMonomer;
    }


    private Set<MyMonomerIfc> makeMyMonomersLocalAroundAndWithChain(MyChainIfc myChain) {

        Set<MyMonomerIfc> queryMyMonomer = new HashSet<>();
        for (MyMonomerIfc myMonomer : myChain.getMyMonomers()) {
            queryMyMonomer.add(myMonomer);
        }

        for (MyMonomerIfc monomer : myChain.getMyMonomers()) {
            MyChainIfc[] neighbors = monomer.getNeighboringAminoMyMonomerByRepresentativeAtomDistance();
            for (MyChainIfc mychain : neighbors) {
                for (MyMonomerIfc neighbor : mychain.getMyMonomers()) {
                    queryMyMonomer.add(neighbor);
                }
            }
        }
        return queryMyMonomer;
    }


    private Set<MyMonomerIfc> makeMyMonomersLocalAroundAndExcludingMyMonomersFromInput(MyMonomerIfc[] myMonomomers) {

        Set<MyMonomerIfc> queryMyMonomer = makeMyMonomersLocalAroundAndWithChain(myMonomomers);

        for (MyMonomerIfc monomerToRemove : myMonomomers) {
            queryMyMonomer.remove(monomerToRemove);
        }
        return queryMyMonomer;
    }


    private Set<MyMonomerIfc> makeMyMonomersLocalAroundAndWithChain(MyMonomerIfc[] myMonomomers) {

        Set<MyMonomerIfc> queryMyMonomer = new HashSet<>();
        for (MyMonomerIfc myMonomer : myMonomomers) {
            queryMyMonomer.add(myMonomer);
        }

        for (MyMonomerIfc monomer : myMonomomers) {
            MyChainIfc[] neighbors = monomer.getNeighboringAminoMyMonomerByRepresentativeAtomDistance();
            for (MyChainIfc mychain : neighbors) {
                for (MyMonomerIfc neighbor : mychain.getMyMonomers()) {
                    queryMyMonomer.add(neighbor);
                }
            }
        }

        return queryMyMonomer;
    }


    //-------------------------------------------------------------
    // Implementation only for hetatm
    //-------------------------------------------------------------
    private MyMonomerIfc findHetAtomLigand(char[] hetAtomsLigandId, int occurrenceId, MyStructureIfc myStructure) {

        int countOfFoundRightHetAtomsLigand = 0;
        MyChainIfc[] allHetAtomsChains = myStructure.getAllHetatmchains(); // then the hetatm which are part of aminochains (e.g. 2qlj E)
        for (MyChainIfc myChain : allHetAtomsChains) {
            for (MyMonomerIfc myMonomer : myChain.getMyMonomers()) {
                if (Arrays.equals(myMonomer.getThreeLetterCode(), hetAtomsLigandId)) {
                    countOfFoundRightHetAtomsLigand += 1;
                    if (countOfFoundRightHetAtomsLigand == occurrenceId) {
                        return myMonomer;
                    }
                }
            }
        }
        return null;
    }

    //-------------------------------------------------------------
    // Implementation only for segment of chain
    //-------------------------------------------------------------
    private void makeCaOnBothSideWithNoBoundAtomsStructureLocal(MyChainIfc extractedSegment, MyStructureIfc clonedMyStructure, MyStructureIfc myStructureGlobalBrut) {

        // Find residue neighbor of Nterminal in Structurelocal
        char[] type = extractedSegment.getMyMonomers()[0].getType();
        char[] chainId = extractedSegment.getChainId();
        MyChainIfc segmentChainInStructureGlobalBrut = findChain(myStructureGlobalBrut, type, chainId);
        if (segmentChainInStructureGlobalBrut == null) {
            return;
        }

        MyChainIfc segmentChainInStructureLocal = findChain(clonedMyStructure, type, chainId);

        if (segmentChainInStructureLocal == null) { // all residues from chain are in the segment so
            // nothing to delete
            return;
        }
        handleCterminal(extractedSegment, segmentChainInStructureGlobalBrut, segmentChainInStructureLocal);
        handleNterminal(extractedSegment, segmentChainInStructureGlobalBrut, segmentChainInStructureLocal);
    }


    private MyChainIfc findChain(MyStructureIfc myStructureGlobalBrut, char[] type, char[] chainId) {
        MyChainIfc segmentChainInStructureGlobalBrut = null; // this structure is used because there are still the bonds
        // between segment and structure local
        if (Arrays.equals(type, MyMonomerType.AMINOACID.getType())) {
            segmentChainInStructureGlobalBrut = myStructureGlobalBrut.getAminoMyChain(chainId);
        }
        if (Arrays.equals(type, MyMonomerType.NUCLEOTIDE.getType())) {
            segmentChainInStructureGlobalBrut = myStructureGlobalBrut.getNucleosideChain(chainId);
        }
        return segmentChainInStructureGlobalBrut;
    }


    private void handleCterminal(MyChainIfc extractedSegment, MyChainIfc segmentChainInStructureGlobalBrut, MyChainIfc segmentChainInStructureLocal) {
        MyAtomIfc cTerminalSegment = MyStructureTools.getCterminal(extractedSegment);
        if (cTerminalSegment != null) {
            int residueIdCterminalSegment = cTerminalSegment.getParent().getResidueID();
            List<Integer> residueIdToModifyOnNTerminal = findResidueIdToModifyNterminal(segmentChainInStructureGlobalBrut, residueIdCterminalSegment, extractedSegment);

            for (Integer residueId : residueIdToModifyOnNTerminal) {
                MyMonomerIfc monomerToModifyOnNTerminal = segmentChainInStructureLocal.getMyMonomerFromResidueId(residueId);
                MyAtomIfc nTerminalAtom = monomerToModifyOnNTerminal.getMyAtomFromMyAtomName("N".toCharArray());
                doDeletionAtNTerminalStructureLocal(nTerminalAtom);
            }
        }
    }


    private List<Integer> findResidueIdToModifyNterminal(MyChainIfc segmentChainInStructureGlobalBrut, int residueIdCterminalSegment, MyChainIfc extractedSegment) {

        List<Integer> residueIds = new ArrayList<>();
        List<MyMonomerIfc> monomerToModifyTheNterminal = new ArrayList<>();

        for (MyMonomerIfc monomer : segmentChainInStructureGlobalBrut.getMyMonomers()) {
            MyMonomerIfc[] neighborsByBond = monomer.getNeighboringMyMonomerByBond();
            for (MyMonomerIfc neighborByBond : neighborsByBond) {
                if (neighborByBond.getResidueID() == residueIdCterminalSegment) {
                    if (!monomerToModifyTheNterminal.contains(monomer)) {
                        if (!isInSegment(extractedSegment, monomer)) {
                            monomerToModifyTheNterminal.add(monomer);
                        }
                    }
                }
            }
        }
        for (MyMonomerIfc monomer : monomerToModifyTheNterminal) {
            residueIds.add(monomer.getResidueID());
        }
        return residueIds;
    }


    private boolean isInSegment(MyChainIfc extractedSegment, MyMonomerIfc monomer) {
        for (MyMonomerIfc inExtractedSegment : extractedSegment.getMyMonomers()) {
            if (inExtractedSegment.getResidueID() == monomer.getResidueID()) {
                return true;
            }
        }
        return false;
    }


    private void handleNterminal(MyChainIfc extractedSegment, MyChainIfc segmentChainInStructureGlobalBrut, MyChainIfc segmentChainInStructureLocal) {
        MyAtomIfc nTerminalSegment = MyStructureTools.getNterminal(extractedSegment);

        if (nTerminalSegment != null) {
            int residueIdNterminalSegment = nTerminalSegment.getParent().getResidueID();
            List<Integer> residueIdToModifyOnCTerminal = findResidueIdToModifyCterminal(segmentChainInStructureGlobalBrut, residueIdNterminalSegment, extractedSegment);

            for (Integer residueId : residueIdToModifyOnCTerminal) {
                MyMonomerIfc monomerToModifyOnNTerminal = segmentChainInStructureLocal.getMyMonomerFromResidueId(residueId);
                MyAtomIfc cTerminalAtom = monomerToModifyOnNTerminal.getMyAtomFromMyAtomName("C".toCharArray());
                MyAtomIfc oTerminalAtom = monomerToModifyOnNTerminal.getMyAtomFromMyAtomName("O".toCharArray());
                doDeletionAtCTerminalStructureLocal(cTerminalAtom, oTerminalAtom);
            }
        }
    }


    private List<Integer> findResidueIdToModifyCterminal(MyChainIfc segmentChainInStructureGlobalBrut, int residueIdNterminalSegment, MyChainIfc extractedSegment) {

        List<Integer> residueIds = new ArrayList<>();
        List<MyMonomerIfc> monomerToModifyTheCterminal = new ArrayList<>();

        for (MyMonomerIfc monomer : segmentChainInStructureGlobalBrut.getMyMonomers()) {
            MyMonomerIfc[] neighborsByBond = monomer.getNeighboringMyMonomerByBond();
            for (MyMonomerIfc neighborByBond : neighborsByBond) {
                if (neighborByBond.getResidueID() == residueIdNterminalSegment) {
                    if (!monomerToModifyTheCterminal.contains(monomer)) {
                        if (!isInSegment(extractedSegment, monomer)) {
                            monomerToModifyTheCterminal.add(monomer);
                        }
                    }
                }
            }
        }
        for (MyMonomerIfc monomer : monomerToModifyTheCterminal) {
            residueIds.add(monomer.getResidueID());
        }
        return residueIds;
    }


    /**
     * Extract the MyMonomers that form the segment. It is meant to build the local structure around the segment for a Shape built from segment of chain.
     * It uses the Monomer at rankIdinChains, then search monomers among bonded monomers, keeps the ones with higher rankid
     * And stops when the lenght is >= to peptideLength
     * The length can be larger than expected if there is a branch in the structure, e.g. Monomer ID=3 bounded to Monomer ID=4 and Monomer ID=5
     *
     * @param inputChain
     * @param rankIdinChain
     * @param peptideLength
     * @return MyChain with MyMonomers objects from input MyChain. Neighbors by distance are kept as in original MyStructure.
     */
    private MyChainIfc extractSubChain(MyChainIfc inputChain, int rankIdinChain, int peptideLength, AlgoParameters algoParameters) {

        MyChainIfc myChain = extractSubChainByBond(inputChain, rankIdinChain, peptideLength);
        return myChain;
    }


    private MyChainIfc extractSubChainByBond(MyChainIfc inputChain, int rankIdinChain, int peptideLength) {
        Set<MyMonomerIfc> tempSetPeptide = new HashSet<>();

        MyMonomerIfc startingMonomer = inputChain.getMyMonomerByRank(rankIdinChain);
        tempSetPeptide.add(startingMonomer);

        // TODO track begining and end for further atom deletion at tip
        int startingMonomerId = startingMonomer.getResidueID();
        for (int i = 0; i < peptideLength - 1; i++) {

            addMonomersBoundIfHigherId(tempSetPeptide, startingMonomerId);

            if (tempSetPeptide.size() >= peptideLength) {
                break;
            }
        }

        List<MyMonomerIfc> tempListAMyMonomerIfc = new ArrayList<>();
        tempListAMyMonomerIfc.addAll(tempSetPeptide);

        Collections.sort(tempListAMyMonomerIfc, new MyMonomerIfcComparatorIncreasingResidueId());
        MyMonomerIfc[] myMonomers = tempListAMyMonomerIfc.toArray(new MyMonomerIfc[tempListAMyMonomerIfc.size()]);
        return new MyChain(myMonomers, inputChain.getChainId());
    }


    private void addMonomersBoundIfHigherId(Set<MyMonomerIfc> inputMonomers, int startingMonomerId) {

        Set<MyMonomerIfc> tempASetMyMonomerIfc = new HashSet<>();

        for (MyMonomerIfc inputMonomer : inputMonomers) {
            MyMonomerIfc[] neighbors = inputMonomer.getNeighboringMyMonomerByBond();
            for (MyMonomerIfc neighbor : neighbors) {
                if (neighbor.getResidueID() > startingMonomerId) {
                    tempASetMyMonomerIfc.add(neighbor);
                }
            }
        }
        inputMonomers.addAll(tempASetMyMonomerIfc);
    }


    /**
     * returns a MyChain which contains a segment of a chain. It is meant to create the ligand for a Shape built from segment of chain.
     * It uses the Monomer at rankIdinChains, then search monomers among bonded monomers, keeps the ones with higher rankid.
     * It is cloned. Without neighbors by distance. With neighbors by bonds only within segment.
     * NH and CO at tips are removed. Bonds to atomds not in the segment are removed.
     *
     * @param inputChain
     * @param rankIdinChain
     * @param peptideLength
     * @param algoParameters
     * @return new MyChain containing the same MyMonomer as from extractSubChain. But with cloned and modified MyMonomers.
     */
    private MyChainIfc makeChainSegment(MyChainIfc inputChain, int rankIdinChain, int peptideLength, AlgoParameters algoParameters) {

        MyChainIfc myChain = extractSubChainByBond(inputChain, rankIdinChain, peptideLength);

        // Need to clone it because I remove some atoms so the original chain should not be changed
        Cloner cloner = new Cloner(myChain, algoParameters);
        MyStructureIfc clone = cloner.getClone();
        MyChainIfc clonedSegment = clone.getAllChains()[0];
        makeCaOnBothSideWithNoBoundAtoms(clonedSegment);

        return clonedSegment;
    }


    private void makeCaOnBothSideWithNoBoundAtoms(MyChainIfc clonedSegment) {

        // delete some atoms from tip
        // only when CO-Ca and Ca-N
        // Wont work for weird ends like ACE I guess

        // Nterminal
        MyAtomIfc nTerminal = MyStructureTools.getNterminal(clonedSegment);
        doDeletionAtNTerminalSegment(nTerminal, clonedSegment);


        // CO Cterminal
        MyAtomIfc cTerminal = MyStructureTools.getCterminal(clonedSegment);
        MyAtomIfc oTerminal = MyStructureTools.getOterminal(clonedSegment);

        doDeletionAtCTerminalSegment(cTerminal, oTerminal, clonedSegment);
    }


    private void doDeletionAtCTerminalSegment(MyAtomIfc cTerminal, MyAtomIfc oTerminal, MyChainIfc clonedSegment) {

        if (applySegmentCterminal(cTerminal, clonedSegment)) {
            oTerminal.getParent().deleteAtomAndbonds(oTerminal);
            cTerminal.getParent().deleteAtomAndbonds(cTerminal);
            //System.out.println();
        }
    }


    private void doDeletionAtNTerminalSegment(MyAtomIfc nTerminal, MyChainIfc clonedSegment) {

        if (applySegmentNterminal(nTerminal, clonedSegment)) {
            nTerminal.getParent().deleteAtomAndbonds(nTerminal);
        }
    }


    private void doDeletionAtCTerminalStructureLocal(MyAtomIfc cTerminal, MyAtomIfc oTerminal) {

        if (applyStructureLocalCterminal(cTerminal)) {
            oTerminal.getParent().deleteAtomAndbonds(oTerminal);
            cTerminal.getParent().deleteAtomAndbonds(cTerminal);
            //System.out.println();
        }
    }


    private void doDeletionAtNTerminalStructureLocal(MyAtomIfc nTerminal) {

        if (doApplyStructureLocalNterminalWithOutDoingAnythingOnAtom(nTerminal)) {
            nTerminal.getParent().deleteAtomAndbonds(nTerminal);
        }
    }

    private boolean applySegmentCterminal(MyAtomIfc cTerminal, MyChainIfc clonedSegment) {

        if (cTerminal == null) {
            return false;
        }

        // if it is a terminal monomer then dont delete anything
        List<MyMonomerIfc> neighborsByBond = MyStructureTools.makeListFromArray(cTerminal.getParent().getNeighboringMyMonomerByBond());
        List<MyMonomerIfc> segmentMonomers = MyStructureTools.makeListFromArray(clonedSegment.getMyMonomers());
        if (segmentMonomers.containsAll(neighborsByBond)) {
            return false;
        }

        int bondCountCterminal = cTerminal.getBonds().length;
        if (bondCountCterminal == 0) { // safety like for nTerminal
            return true;
        }
        MyBondIfc[] bondsToCTerminal = cTerminal.getBonds();
        boolean bondCountCterminalSameMonomer = true;
        for (MyBondIfc bond : bondsToCTerminal) {
            if (!(bond.getBondedAtom().getParent() == cTerminal.getParent())) {
                bondCountCterminalSameMonomer = false;
            }
        }
        boolean oFound = false;
        boolean caFound = false;
        for (MyBondIfc bond : bondsToCTerminal) {
            if (Arrays.equals(bond.getBondedAtom().getAtomName(), "O".toCharArray())) {
                oFound = true;
            }
            if (Arrays.equals(bond.getBondedAtom().getAtomName(), "CA".toCharArray())) {
                caFound = true;
            }
        }

        if (bondCountCterminal == 2 && bondCountCterminalSameMonomer == true && oFound == true && caFound == true) {
            return true;
        }
        return false;
    }


    private boolean applySegmentNterminal(MyAtomIfc nTerminal, MyChainIfc clonedSegment) {

        if (nTerminal == null) {
            return false;
        }

        List<MyMonomerIfc> neighborsByBond = MyStructureTools.makeListFromArray(nTerminal.getParent().getNeighboringMyMonomerByBond());
        List<MyMonomerIfc> segmentMonomers = MyStructureTools.makeListFromArray(clonedSegment.getMyMonomers());
        if (segmentMonomers.containsAll(neighborsByBond)) {
            return false;
        }

        // If Nterminal is only bound to the Ca of same monomer then I delete it and the bond from Ca to N
        // or no bond
        int bondCountNterminal = nTerminal.getBonds().length;
        if (bondCountNterminal == 0) { // 5it7 chain OO rankid 5 peptide length 5
            return true;
        }
        boolean bondToCaSameMonomer = nTerminal.getBonds()[0].getBondedAtom().getParent() == nTerminal.getParent();
        boolean bondToCa = Arrays.equals(nTerminal.getBonds()[0].getBondedAtom().getAtomName(), "CA".toCharArray());

        if (bondCountNterminal == 1 && bondToCaSameMonomer && bondToCa) {
            return true;
        }
        return false;
    }


    private boolean applyStructureLocalCterminal(MyAtomIfc cTerminal) {

        if (cTerminal == null) {
            return false;
        }
        int bondCountCterminal = cTerminal.getBonds().length;
        if (bondCountCterminal == 0) { // safety like for nTerminal
            return true;
        }
        MyBondIfc[] bondsToCTerminal = cTerminal.getBonds();

        boolean oFound = false;
        boolean caFound = false;
        for (MyBondIfc bond : bondsToCTerminal) {
            if (Arrays.equals(bond.getBondedAtom().getAtomName(), "O".toCharArray())) {
                oFound = true;
            }
            if (Arrays.equals(bond.getBondedAtom().getAtomName(), "CA".toCharArray())) {
                caFound = true;
            }
        }


        if (bondCountCterminal == 2 && oFound == true && caFound == true) {
            return true;
        }
        return false;
    }


    private boolean doApplyStructureLocalNterminalWithOutDoingAnythingOnAtom(MyAtomIfc nTerminal) {

        if (nTerminal == null) {
            return false;
        }

        // If Nterminal is only bound to the Ca of same monomer then I delete it and the bond from Ca to N
        int bondCountNterminal = nTerminal.getBonds().length;
        if (bondCountNterminal == 0) { // 5it7 chain OO rankid 5 peptide length 5
            return true;
        }

        boolean bondToCaSameMonomer = nTerminal.getBonds()[0].getBondedAtom().getParent() == nTerminal.getParent();
        boolean bondToCa = Arrays.equals(nTerminal.getBonds()[0].getBondedAtom().getAtomName(), "CA".toCharArray());
        MyBondIfc[] bondsToNTerminal = nTerminal.getBonds();


        boolean caFound = false;
        for (MyBondIfc bond : bondsToNTerminal) {

            if (Arrays.equals(bond.getBondedAtom().getAtomName(), "CA".toCharArray())) {
                caFound = true;
            }
        }

        if (bondCountNterminal == 1 && caFound) {
            return true;
        }
        return false;
    }


    //-------------------------------------------------------------
    // Implementation only for AtomsDefinedByIds
    //-------------------------------------------------------------
    private List<MyMonomerIfc> findMyMonomersOnlyInAminoChainsContainingAtomsDefinedByIds(MyStructureIfc myStructure, List<QueryAtomDefinedByIds> queryAtomsDefinedByIds) {

        List<MyMonomerIfc> monomersFound = new ArrayList<>();

        for (QueryAtomDefinedByIds atomDefinedByIds : queryAtomsDefinedByIds) {

            char[] chainIdToFind = atomDefinedByIds.getChainQuery().toCharArray();
            MyChainIfc[] chains = myStructure.getAllChainsRelevantForShapeBuilding();
            for (MyChainIfc foundMyChain : chains) {
                if (Arrays.equals(foundMyChain.getChainId(), chainIdToFind)) {
                    int residueIdToFind = atomDefinedByIds.getResidueId();
                    MyMonomerIfc foundMyMonomer = foundMyChain.getMyMonomerFromResidueId(residueIdToFind);
                    char[] atomNameToFind = atomDefinedByIds.getAtomName().toCharArray();
                    MyAtomIfc foundMyAtom = foundMyMonomer.getMyAtomFromMyAtomName(atomNameToFind);
                    if (foundMyAtom != null) {
                        monomersFound.add(foundMyMonomer);
                        break;
                    }
                }
            }
            if (monomersFound.isEmpty()) {
                System.out.println("Monomer not found ");
            }
        }
        return monomersFound;
    }


    //-------------------------------------------------------------
    // Getters & Setters
    //-------------------------------------------------------------
    public MyChainIfc getLigand() {
        return ligand;
    }

    public MyStructureIfc getMyStructureLocal() {
        return myStructureLocal;
    }

    public List<MyMonomerIfc> getMonomerToDiscard() {
        return monomerToDiscard;
    }
}
