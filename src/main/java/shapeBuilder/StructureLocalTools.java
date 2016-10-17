package shapeBuilder;

import java.util.*;

import mystructure.*;
import parameters.AlgoParameters;

public class StructureLocalTools {
//-------------------------------------------------------------
// Public & Override methods
//-------------------------------------------------------------

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
    public static MyChainIfc extractSubChain(MyChainIfc inputChain, int rankIdinChain, int peptideLength, AlgoParameters algoParameters) {

        MyChainIfc myChain = extractSubChainByBond(inputChain, rankIdinChain, peptideLength);
        return myChain;
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
    public static MyChainIfc makeChainSegment(MyChainIfc inputChain, int rankIdinChain, int peptideLength, AlgoParameters algoParameters) {

        MyChainIfc myChain = extractSubChainByBond(inputChain, rankIdinChain, peptideLength);

        // Need to clone it because I remove some atoms so the original chain should not be changed
        Cloner cloner = new Cloner(myChain, algoParameters);
        MyStructureIfc clone = cloner.getClone();
        MyChainIfc clonedSegment = clone.getAllChains()[0];
        makeCaOnBothSideWithNoBoundAtoms(clonedSegment);

        return clonedSegment;
    }

    private static void makeCaOnBothSideWithNoBoundAtoms(MyChainIfc clonedSegment) {

        // delete some atoms from tip
        // only when CO-Ca and Ca-N
        // Wont work for weird ends like ACE I guess

        // Nterminal
        MyAtomIfc nTerminal = MyStructureTools.getNterminal(clonedSegment);

        // If Nterminal is only bound to the Ca of same monomer then I delete it and the bond from Ca to N
        int bondCountNterminal = nTerminal.getBonds().length;
        boolean bondToCaSameMonomer = nTerminal.getBonds()[0].getBondedAtom().getParent() == nTerminal.getParent();
        boolean bondToCa = Arrays.equals(nTerminal.getBonds()[0].getBondedAtom().getAtomName(), "CA".toCharArray());

        if (bondCountNterminal == 1 && bondToCaSameMonomer && bondToCa) {
            nTerminal.getParent().deleteAtomAndbonds(nTerminal);
        }

        // CO Cterminal
        MyAtomIfc cTerminal = MyStructureTools.getCterminal(clonedSegment);
        MyAtomIfc oTerminal = MyStructureTools.getOterminal(clonedSegment);

        int bondCountCterminal = cTerminal.getBonds().length;
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

            oTerminal.getParent().deleteAtomAndbonds(oTerminal);
            cTerminal.getParent().deleteAtomAndbonds(cTerminal);
            System.out.println();
        }
    }


    /**
     * Returns the neighboring monomers of a segement of chain
     *
     * @param wholeChain
     * @param ligand
     * @param startingRankId
     * @param peptideLength
     * @param tipMonoMerDistance
     * @return
     */
    public static List<MyMonomerIfc> findTipsSegmentOfChain(MyChainIfc wholeChain, MyChainIfc ligand, int startingRankId, int peptideLength, int tipMonoMerDistance) {

        List<MyMonomerIfc> tipsOfSegmentOfChain = new ArrayList<>();

        Set<MyMonomerIfc> tempSetPeptide = new HashSet<>();
        for (MyMonomerIfc myMonomer : ligand.getMyMonomers()) {
            tempSetPeptide.add(myMonomer);
        }

        for (int i = 0; i < tipMonoMerDistance - 1; i++) { // I explore bonded to this distance using ligand as input
            addMonomerBound(tempSetPeptide);
        }

        for (MyMonomerIfc myMonomer : ligand.getMyMonomers()) { //I remove the peptide itself as I want only the tips
            tempSetPeptide.remove(myMonomer);
        }

        tipsOfSegmentOfChain.addAll(tempSetPeptide);
        return tipsOfSegmentOfChain;
    }


    public static MyStructureIfc makeStructureLocalAroundAndExcludingMyMonomersFromInputMyChain(MyStructureIfc myStructureGlobalBrut, MyChainIfc myChain) {

        Set<MyMonomerIfc> queryMonomers = makeMyMonomersLocalAroundAndExcludingMyMonomersFromInputMyChain(myChain);

        MyStructureIfc myStructureLocal;
        try {
            myStructureLocal = myStructureGlobalBrut.cloneWithSameObjectsWhileKeepingOnlyMyMonomerInThisSet(queryMonomers);
        } catch (ExceptionInMyStructurePackage e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        return myStructureLocal;
    }


    public static Set<MyMonomerIfc> makeMyMonomersLocalAroundAndWithChain(MyChainIfc myChain) {

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


    //-------------------------------------------------------------
// Implementation
//-------------------------------------------------------------
    private static MyChainIfc extractSubChainByBond(MyChainIfc inputChain, int rankIdinChain, int peptideLength) {
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


    private static void addMonomersBoundIfHigherId(Set<MyMonomerIfc> inputMonomers, int startingMonomerId) {

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


    private static void addMonomerBound(Set<MyMonomerIfc> inputMonomers) {

        Set<MyMonomerIfc> tempASetMyMonomerIfc = new HashSet<>();

        for (MyMonomerIfc inputMonomer : inputMonomers) {
            MyMonomerIfc[] neighbors = inputMonomer.getNeighboringMyMonomerByBond();
            for (MyMonomerIfc neighbor : neighbors) {
                tempASetMyMonomerIfc.add(neighbor);
            }
        }
        inputMonomers.addAll(tempASetMyMonomerIfc);
    }


    private static Set<MyMonomerIfc> makeMyMonomersLocalAroundAndExcludingMyMonomersFromInputMyChain(MyChainIfc myChain) {

        Set<MyMonomerIfc> queryMyMonomer = makeMyMonomersLocalAroundAndWithChain(myChain);

        for (MyMonomerIfc monomerToRemove : myChain.getMyMonomers()) {
            queryMyMonomer.remove(monomerToRemove);
        }
        return queryMyMonomer;
    }
}
