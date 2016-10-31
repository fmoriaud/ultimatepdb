package mystructure;

import math.ToolsMath;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import parameters.AlgoParameters;
import shapeCompare.PairingTools;
import shapeCompare.ResultsFromEvaluateCost;

import java.util.*;

/**
 * Created by Fabrice on 19/09/16.
 */
public class Cloner {

    private MyStructureIfc myStructure;
    private Set<MyMonomerIfc> queryMonomers = null;
    private MyMonomerIfc myMonomer;
    private MyChainIfc myChain;
    private MyChainIfc[] myChains;
    private AlgoParameters algoParameters;


    private Map<MyAtomIfc, MyAtomIfc> keyIsOldAtomValueIsNewAtom = new LinkedHashMap<>();
    private Map<MyMonomerIfc, MyMonomerIfc> keyIsOldMonomerValueIsNewMonomer = new LinkedHashMap<>();
    private Map<MyMonomerIfc, MyMonomerIfc> keyIsNewMonomerValueIsOldMonomer = new LinkedHashMap<>();

    //-------------------------------------------------------------
    // Constructors
    //-------------------------------------------------------------

    /**
     * This Cloner will clone a MyStructure but keeps in the clone only a subset of MyMonomer
     * Amino, Nucleoside, Hetatm MyChains grouping is kept
     * MyMonomer neighbors by distance and by bond are updated to cloned MyMonomer
     * The MyAtom, MyMonomer parents are updated to cloned MyMonomer and cloned MyChain
     * MyBond bonded atom are updated to cloned MyAtom. If bonded atom is coned because MyAtom is not found in MyMonomers
     * then MyBond is removed.
     *
     * @param myStructure is the input MyStructure to be cloned
     * @param queryMonomers is the subset of MyMonomer to only keep in the clone
     * @param algoParameters
     */
    public Cloner(MyStructureIfc myStructure, Set<MyMonomerIfc> queryMonomers, AlgoParameters algoParameters) {

        this.myStructure = myStructure;
        this.algoParameters = algoParameters;
        this.queryMonomers = queryMonomers;
    }



    /**
     * This Cloner will clone a MyStructure keeping all MyMonomer therein.
     * Amino, Nucleoside, Hetatm MyChains grouping is kept
     * MyMonomer neighbors by distance and by bond are updated to cloned MyMonomer
     * The MyAtom, MyMonomer parents are updated to cloned MyMonomer and cloned MyChain
     * MyBond bonded atom are updated to cloned MyAtom. If bonded atom is coned because MyAtom is not found in MyMonomers
     * then MyBond is removed.
     *
     * @param myStructure is the input MyStructure to be cloned
     * @param algoParameters
     */
    public Cloner(MyStructureIfc myStructure, AlgoParameters algoParameters) {

        this.myStructure = myStructure;
        this.algoParameters = algoParameters;
    }


    /**
     * This Cloner will clone an array of MyChainIfc keeping all MyMonomer therein.
     * It is used to make a MyStructure from the neighbors by distance of a MyMonomer
     * The grouping is based on the type of the first MyMonomer in each and every MyChain
     * MyMonomer neighbors by distance and by bond are updated to cloned MyMonomer
     * The MyAtom, MyMonomer parents are updated to cloned MyMonomer and cloned MyChain
     * MyBond bonded atom are updated to cloned MyAtom. If bonded atom is coned because MyAtom is not found in MyMonomers
     * then MyBond is removed.
     * @param myChains
     * @param algoParameters
     */
    public Cloner(MyChainIfc[] myChains, AlgoParameters algoParameters) {

        this.myChains = myChains;
        this.algoParameters = algoParameters;
    }


    /**
     * This Cloner will clone a MyChain into a cloned MyStructure keeping all MyMonomer therein.
     * The cloned Mychain is found in Amino, Nucleoside, Hetatm based on the type of the first MyMonomer in the input MyChain
     * Cloned MyChain is obtained by clone.getAllChains()[0];
     * @param myChain
     * @param algoParameters
     */
    public Cloner(MyChainIfc myChain, AlgoParameters algoParameters) {

        this.myChain = myChain;
        this.algoParameters = algoParameters;

    }



    /**
     * This Cloner will clone a MyMonomer into a cloned MyStructure
     * The cloned MyMonomer is found in a MyChain Amino, Nucleoside, Hetatm based on the type of the input MyMonomer
     * Cloned MyMonomer is obtained by cloner.getClone().getAllChains()[0].getMyMonomers()[0]
     * @param myMonomer
     * @param algoParameters
     */
    public Cloner(MyMonomerIfc myMonomer, AlgoParameters algoParameters) {

        this.myMonomer = myMonomer;
        this.algoParameters = algoParameters;
    }




//-------------------------------------------------------------
// Public & Override methods
//-------------------------------------------------------------
    public MyStructureIfc getClone() {

        if (myStructure != null) {
            MyStructureIfc clone = makeClone(myStructure, algoParameters);
            return clone;
        }

        if (myChains != null){
            MyStructureIfc clone = makeClone(myChains, algoParameters);
            return clone;
        }

        if (myChain != null) {
            MyStructureIfc clone = makeClone(myChain, algoParameters);
            return clone;
        }

        if (myMonomer != null) {
            MyStructureIfc clone = makeClone(myMonomer, algoParameters);
            return clone;
        }

        return null;
    }


    public MyStructureIfc getRotatedClone(ResultsFromEvaluateCost result) {

        if (myMonomer != null) {
            MyStructureIfc rotatedClone = getRotatedCloneMyMonomer(result);
            return rotatedClone;
        }

        if (myStructure != null) {
            MyStructureIfc rotatedClone = getRotatedCloneMyStructure(result);
            return rotatedClone;
        }
        return null;
    }


    private MyStructureIfc getRotatedCloneMyStructure(ResultsFromEvaluateCost result) {

        MyStructureIfc rotatedClone = makeClone(myStructure, algoParameters);

        for (MyChainIfc chain : rotatedClone.getAllChains()) {
            for (MyMonomerIfc monomer : chain.getMyMonomers()) {
                for (MyAtomIfc atom : monomer.getMyAtoms()) {
                    RealVector coordsVector = new ArrayRealVector(ToolsMath.convertToDoubleArray(atom.getCoords().clone()));
                    RealVector newPointCoords = PairingTools.alignPointFromShape2toShape1(result, coordsVector);
                    atom.setCoords(ToolsMath.convertToFloatArray(newPointCoords.toArray()));
                }
            }
        }

        return rotatedClone;
    }


    public MyStructureIfc getRotatedCloneMyMonomer(ResultsFromEvaluateCost result) {

        MyStructureIfc rotatedClone = makeClone(myMonomer, algoParameters);

        for (MyChainIfc chain : rotatedClone.getAllChains()) {
            for (MyMonomerIfc monomer : chain.getMyMonomers()) {
                for (MyAtomIfc atom : monomer.getMyAtoms()) {
                    RealVector coordsVector = new ArrayRealVector(ToolsMath.convertToDoubleArray(atom.getCoords().clone()));
                    RealVector newPointCoords = PairingTools.alignPointFromShape2toShape1(result, coordsVector);
                    atom.setCoords(ToolsMath.convertToFloatArray(newPointCoords.toArray()));
                }
            }
        }

        return rotatedClone;
    }


    // -------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------
    private MyStructureIfc makeClone(MyChainIfc[] myChains, AlgoParameters algoParameters){

        List<MyChainIfc> tmpChainsAmino = new ArrayList<>();
        List<MyChainIfc> tmpChainsNucleoside = new ArrayList<>();
        List<MyChainIfc> tmpChainsHetatm = new ArrayList<>();

        for (MyChainIfc myChain: myChains){

            MyChainIfc clonedChain = cloneAtomAndMonomersInMyChain(myChain);
            if (clonedChain.getMyMonomers().length > 0) {
                String type = String.valueOf(myChain.getMyMonomers()[0].getType());
                if (type.equals("amino")) {
                    tmpChainsAmino.add(clonedChain);
                }
                if (type.equals("nucleotide")) {
                    tmpChainsNucleoside.add(clonedChain);
                }
                if (type.equals("hetatm")) {
                    tmpChainsHetatm.add(clonedChain);
                }
            }
        }
        MyChainIfc[] myAminoChains = MyStructureTools.makeArrayFromListMyChains(tmpChainsAmino);
        MyChainIfc[] myNucleotideChains = MyStructureTools.makeArrayFromListMyChains(tmpChainsNucleoside);
        MyChainIfc[] myHetatmChains = MyStructureTools.makeArrayFromListMyChains(tmpChainsHetatm);

        ExpTechniquesEnum expTechnique = ExpTechniquesEnum.UNDEFINED;
        MyStructureIfc clone = null;
        try {
            clone = new MyStructure(expTechnique, algoParameters, myAminoChains, myHetatmChains, myNucleotideChains);
        } catch (ExceptionInMyStructurePackage exceptionInMyStructurePackage) {
            exceptionInMyStructurePackage.printStackTrace();
        }
        clone.setFourLetterCode(MyStructureConstants.PDB_ID_DEFAULT.toCharArray());
        updateMyMonomerToClonedOneInNeighbors(clone);
        MyStructureTools.fixParents(clone);
        fixBondedAtomReference(clone);

        return clone;
    }



    private MyStructureIfc makeClone(MyStructureIfc myStructure, AlgoParameters algoParameters) {

        List<MyChainIfc> tmpChains = new ArrayList<>();
        for (int i = 0; i < myStructure.getAllAminochains().length; i++) {

            MyChainIfc clonedChain = cloneAtomAndMonomersInMyChain(myStructure.getAminoChain(i));
            if (clonedChain.getMyMonomers().length > 0) {
                tmpChains.add(clonedChain);
            }
        }
        MyChainIfc[] myAminoChains = MyStructureTools.makeArrayFromListMyChains(tmpChains);

        tmpChains.clear();
        for (int i = 0; i < myStructure.getAllNucleosidechains().length; i++) {

            MyChainIfc clonedChain = cloneAtomAndMonomersInMyChain(myStructure.getNucleosideChain(i));
            if (clonedChain.getMyMonomers().length > 0) {
                tmpChains.add(clonedChain);
            }
        }
        MyChainIfc[] myNucleotideChains = MyStructureTools.makeArrayFromListMyChains(tmpChains);

        tmpChains.clear();
        for (int i = 0; i < myStructure.getAllHetatmchains().length; i++) {

            MyChainIfc clonedChain = cloneAtomAndMonomersInMyChain(myStructure.getHetatmChain(i));
            if (clonedChain.getMyMonomers().length > 0) {
                tmpChains.add(clonedChain);
            }
        }
        MyChainIfc[] myHetatmChains = MyStructureTools.makeArrayFromListMyChains(tmpChains);

        ExpTechniquesEnum expTechnique = determineExperimentalTechnique(myStructure);

        MyStructureIfc clone = null;
        try {
            clone = new MyStructure(expTechnique, algoParameters, myAminoChains, myHetatmChains, myNucleotideChains);
        } catch (ExceptionInMyStructurePackage exceptionInMyStructurePackage) {
            exceptionInMyStructurePackage.printStackTrace();
        }

        clone.setFourLetterCode(determineFourLetterCode(myStructure));

        updateMyMonomerToClonedOneInNeighbors(clone);
        MyStructureTools.fixParents(clone);
        fixBondedAtomReference(clone);

        return clone;
    }



    private MyStructureIfc makeClone(MyChainIfc myChain, AlgoParameters algoParameters) {

        MyChainIfc clonedMyChain = cloneAtomAndMonomersInMyChain(myChain);

        MyChainIfc[] chains = new MyChainIfc[1];
        chains[0] = clonedMyChain;

        MyChainIfc[] myAminoChains = new MyChainIfc[0];
        MyChainIfc[] myHetatmChains = new MyChainIfc[0];
        MyChainIfc[] myNucleotideChains = new MyChainIfc[0];
        String type = String.valueOf(myChain.getMyMonomers()[0].getType());
        if (type.equals("amino")) {
            myAminoChains = chains;
            MyChainIfc[] other1 = new MyChainIfc[0];
            myNucleotideChains = other1;
            MyChainIfc[] other2 = new MyChainIfc[0];
            myHetatmChains = other2;
        }
        if (type.equals("nucleotide")) {
            myNucleotideChains = chains;
            MyChainIfc[] other1 = new MyChainIfc[0];
            myAminoChains = other1;
            MyChainIfc[] other2 = new MyChainIfc[0];
            myHetatmChains = other2;
        }
        if (type.equals("hetatm")) {
            myHetatmChains = chains;
            MyChainIfc[] other1 = new MyChainIfc[0];
            myNucleotideChains = other1;
            MyChainIfc[] other2 = new MyChainIfc[0];
            myAminoChains = other2;
        }

        ExpTechniquesEnum expTechnique = ExpTechniquesEnum.UNDEFINED;
        MyStructureIfc clone = null;
        try {
            clone = new MyStructure(expTechnique, algoParameters, myAminoChains, myHetatmChains, myNucleotideChains);
        } catch (ExceptionInMyStructurePackage exceptionInMyStructurePackage) {
            exceptionInMyStructurePackage.printStackTrace();
        }
        clone.setFourLetterCode(MyStructureConstants.PDB_ID_DEFAULT.toCharArray());

        updateMyMonomerToClonedOneInNeighbors(clone);
        MyStructureTools.fixParents(clone);
        fixBondedAtomReference(clone);

        return clone;
    }



    private MyStructureIfc makeClone(MyMonomerIfc myMonomer, AlgoParameters algoParameters) {

        MyMonomerIfc cloneMyMonomer = null;
        try {
            cloneMyMonomer = simplyCloneMyMonomer(myMonomer);
        } catch (ExceptionInMyStructurePackage exceptionInMyStructurePackage) {
            exceptionInMyStructurePackage.printStackTrace();
            // TODO abort cloning if one atom is not clonable ???

        }

        MyChainIfc[] chains = new MyChainIfc[1];
        chains[0] = new MyChain(cloneMyMonomer, MyStructureConstants.CHAIN_ID_DEFAULT.toCharArray());
        MyChainIfc[] myAminoChains = new MyChainIfc[0];
        MyChainIfc[] myHetatmChains = new MyChainIfc[0];
        MyChainIfc[] myNucleotideChains = new MyChainIfc[0];
        String type = String.valueOf(myMonomer.getType());
        if (type.equals("amino")) {
            myAminoChains = chains;
            MyChainIfc[] other1 = new MyChainIfc[0];
            myNucleotideChains = other1;
            MyChainIfc[] other2 = new MyChainIfc[0];
            myHetatmChains = other2;
        }
        if (type.equals("nucleotide")) {
            myNucleotideChains = chains;
            MyChainIfc[] other1 = new MyChainIfc[0];
            myAminoChains = other1;
            MyChainIfc[] other2 = new MyChainIfc[0];
            myHetatmChains = other2;
        }
        if (type.equals("hetatm")) {
            myHetatmChains = chains;
            MyChainIfc[] other1 = new MyChainIfc[0];
            myNucleotideChains = other1;
            MyChainIfc[] other2 = new MyChainIfc[0];
            myAminoChains = other2;
        }

        ExpTechniquesEnum expTechnique = ExpTechniquesEnum.UNDEFINED;
        MyStructureIfc clone = null;
        try {
            clone = new MyStructure(expTechnique, algoParameters, myAminoChains, myHetatmChains, myNucleotideChains);
        } catch (ExceptionInMyStructurePackage exceptionInMyStructurePackage) {
            exceptionInMyStructurePackage.printStackTrace();
        }
        clone.setFourLetterCode(MyStructureConstants.PDB_ID_DEFAULT.toCharArray());

        MyStructureTools.setEmptyNeighbors(cloneMyMonomer);
        MyStructureTools.fixParents(clone);
        fixBondedAtomReference(clone);

        return clone;
    }



    private ExpTechniquesEnum determineExperimentalTechnique(MyStructureIfc myStructure) {
        ExpTechniquesEnum expTechnique = myStructure.getExpTechnique();
        if (expTechnique == null) {
            expTechnique = ExpTechniquesEnum.UNDEFINED;
        }
        return expTechnique;
    }



    private char[] determineFourLetterCode(MyStructureIfc myStructure) {
        char[] fourLetterCode = myStructure.getFourLetterCode();
        if (fourLetterCode == null || fourLetterCode.length != 4) {
            fourLetterCode = MyStructureConstants.PDB_ID_DEFAULT.toCharArray();
        }
        return fourLetterCode;
    }



    private MyChainIfc cloneAtomAndMonomersInMyChain(MyChainIfc myChain) {

        int countExcluded = 0;
        if (queryMonomers != null) { // then none excluded
            for (int i = 0; i < myChain.getMyMonomers().length; i++) {
                if (!queryMonomers.contains(myChain.getMyMonomers()[i])) {
                    countExcluded += 1;
                    continue;
                }
            }
        }
        MyMonomerIfc[] myMonomersCloned = new MyMonomerIfc[myChain.getMyMonomers().length - countExcluded];

        int idIncludingExclusion = 0;
        for (int i = 0; i < myChain.getMyMonomers().length; i++) {

            if (queryMonomers != null && !queryMonomers.contains(myChain.getMyMonomers()[i])) {
                continue;
            }
            try {
                myMonomersCloned[idIncludingExclusion] = simplyCloneMyMonomer(myChain.getMyMonomers()[i]);
            } catch (ExceptionInMyStructurePackage exceptionInMyStructurePackage) {
                exceptionInMyStructurePackage.printStackTrace();
                // TODO abort cloning if one atom is not clonable ???
                continue;
            }
            keyIsOldMonomerValueIsNewMonomer.put(myChain.getMyMonomers()[i], myMonomersCloned[idIncludingExclusion]);
            keyIsNewMonomerValueIsOldMonomer.put(myMonomersCloned[idIncludingExclusion], myChain.getMyMonomers()[i]);
            idIncludingExclusion += 1;
        }
        MyChainIfc myChainCloned = new MyChain(myMonomersCloned, myChain.getChainId());
        return myChainCloned;
    }


    private MyMonomerIfc simplyCloneMyMonomer(MyMonomerIfc monomer) throws ExceptionInMyStructurePackage {

        MyAtomIfc[] myAtomsCloned = new MyAtomIfc[monomer.getMyAtoms().length];

        for (int i = 0; i < monomer.getMyAtoms().length; i++) {
            try {
                myAtomsCloned[i] = cloneMyAtom(monomer.getMyAtoms()[i]);
            } catch (ExceptionInMyStructurePackage e) {
                continue;
            }
        }
        MyMonomerIfc myMonomerCloned = new MyMonomer(myAtomsCloned, monomer.getThreeLetterCode(), monomer.getResidueID(), MyMonomerType.getEnumType(monomer.getType()), monomer.isWasHetatm(), monomer.getInsertionLetter(), monomer.getAltLocGroup());

        return myMonomerCloned;
    }



    private void updateMyMonomerToClonedOneInNeighbors(MyStructureIfc clone) {
        // Fix neighbors references
        List<MyMonomerIfc> tmpMonomers = new ArrayList<>();
        for (MyChainIfc chain : clone.getAllChains()) {

            MyMonomerIfc[] monomers = chain.getMyMonomers();
            for (MyMonomerIfc monomer : monomers) {
                MyMonomerIfc oldMonomer = keyIsNewMonomerValueIsOldMonomer.get(monomer);
                // neighbors by distance
                MyChainIfc[] neighborchains = oldMonomer.getNeighboringAminoMyMonomerByRepresentativeAtomDistance();
                MyChainIfc[] newneighborchains = new MyChainIfc[neighborchains.length];
                for (int i = 0; i < neighborchains.length; i++) {
                    tmpMonomers.clear();
                    for (MyMonomerIfc neighMonomer : neighborchains[i].getMyMonomers()) {
                        if (keyIsOldMonomerValueIsNewMonomer.containsKey(neighMonomer)) {
                            tmpMonomers.add(keyIsOldMonomerValueIsNewMonomer.get(neighMonomer));
                        }
                    }
                    MyChainIfc newNeighborchains = new MyChain(tmpMonomers);
                    newNeighborchains.setChainId(neighborchains[i].getChainId());
                    // TODO there could be empty neighbor chain
                    newneighborchains[i] = newNeighborchains;
                }
                monomer.setNeighboringAminoMyMonomerByRepresentativeAtomDistance(newneighborchains);

                // neighbors by bond

                MyMonomerIfc[] neighborsByBond = oldMonomer.getNeighboringMyMonomerByBond();

                tmpMonomers.clear();
                for (int i = 0; i < neighborsByBond.length; i++) {
                    if (keyIsOldMonomerValueIsNewMonomer.containsKey(neighborsByBond[i])) {
                        tmpMonomers.add(keyIsOldMonomerValueIsNewMonomer.get(neighborsByBond[i]));
                    }
                }
                MyMonomerIfc[] newneighborsByBond = MyStructureTools.makeArrayFromListMyMonomers(tmpMonomers);
                monomer.setNeighboringMyMonomerByBond(newneighborsByBond);
            }
        }
    }



    private void fixBondedAtomReference(MyStructureIfc myStructure) {

        for (MyChainIfc myChain : myStructure.getAllChains()) {
            fixBondedAtomReference(myChain);
        }
        MyStructureTools.removeBondsToMyAtomsNotInMyStructure(myStructure);
    }



    private void fixBondedAtomReference(MyChainIfc myChain) {

        for (MyMonomerIfc monomer : myChain.getMyMonomers()) {
            fixBondedAtomReference(monomer);
        }
    }



    private void fixBondedAtomReference(MyMonomerIfc myMonomer) {

        for (MyAtomIfc atom : myMonomer.getMyAtoms()) {
            MyBondIfc[] bonds = atom.getBonds();
            if (bonds != null && bonds.length > 0) {
                for (MyBondIfc bond : bonds) {
                    MyAtomIfc bondedAtom = bond.getBondedAtom();
                    bond.setBondedAtom(keyIsOldAtomValueIsNewAtom.get(bondedAtom));
                }
            }
        }
    }



    private MyAtomIfc cloneMyAtom(MyAtomIfc atom) throws ExceptionInMyStructurePackage {
        MyAtomIfc myatomCloned = null;

        float[] newCoords = new float[3];
        for (int i = 0; i < 3; i++) {
            newCoords[i] = atom.getCoords()[i];
        }
        myatomCloned = new MyAtom(atom.getElement(), newCoords, atom.getAtomName(), atom.getOriginalAtomId());

        if (atom.getBonds() != null) {
            MyBondIfc[] newBonds = new MyBondIfc[atom.getBonds().length];
            int bondCount = 0;
            for (MyBondIfc bond : atom.getBonds()) {
                MyBondIfc newBond = new MyBond(bond.getBondedAtom(), bond.getBondOrder());
                newBonds[bondCount] = newBond;
                bondCount += 1;
            }
            myatomCloned.setBonds(newBonds);
        }
        keyIsOldAtomValueIsNewAtom.put(atom, myatomCloned);
        return myatomCloned;
    }
}