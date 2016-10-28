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
    public Cloner(MyStructureIfc myStructure, Set<MyMonomerIfc> queryMonomers, AlgoParameters algoParameters) {

        this.myStructure = myStructure;
        this.algoParameters = algoParameters;
        this.queryMonomers = queryMonomers;
    }


    public Cloner(MyStructureIfc myStructure, AlgoParameters algoParameters) {

        this.myStructure = myStructure;
        this.algoParameters = algoParameters;
    }


    public Cloner(MyChainIfc[] myChains, AlgoParameters algoParameters) {

        this.myChains = myChains;
        this.algoParameters = algoParameters;
    }

    /**
     * Returns a clone of input MyChain. All objects are different but with the same data.
     * Bonds to MyAtom not in MyChain are removed
     * Neighbors by distance to representative atoms are cleaned to be only from the cloned chain.
     *
     * @param myChain
     * @param algoParameters
     */
    public Cloner(MyChainIfc myChain, AlgoParameters algoParameters) {

        this.myChain = myChain;
        this.algoParameters = algoParameters;

    }


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

        if (myMonomer != null) {
            MyStructureIfc clone = makeClone(myMonomer, algoParameters);
            return clone;
        }
        if (myChain != null) {
            MyStructureIfc clone = makeClone(myChain, algoParameters);
            return clone;
        }
        if (myChains != null) {
            MyStructureIfc clone = makeClone(myChains, algoParameters);
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
    private MyStructureIfc makeClone(MyStructureIfc myStructure, AlgoParameters algoParameters) {

        // queryMonomers are to be excluded

        // assume monomer by distance and bond are correctly set
        // clone each chain while keeping track of atom and monomer correspondance

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

        ExpTechniquesEnum expTechnique = myStructure.getExpTechnique();
        if (expTechnique == null) {
            expTechnique = ExpTechniquesEnum.UNDEFINED;
        }
        MyStructureIfc clone = null;
        try {
            clone = new MyStructure(expTechnique, algoParameters, myAminoChains, myHetatmChains, myNucleotideChains);
        } catch (ExceptionInMyStructurePackage exceptionInMyStructurePackage) {
            exceptionInMyStructurePackage.printStackTrace();
        }
        clone.setFourLetterCode(MyStructureConstants.PDB_ID_DEFAULT.toCharArray());

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

        // fix parents
        fixParents(clone);
        // Fix bonded atom reference && remove the ones to non existing atom
        fixBondedAtomReference(clone);

        return clone;
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


    private MyStructureIfc makeClone(MyChainIfc[] myChains, AlgoParameters algoParameters) {

        MyChainIfc[] clonedChains = new MyChainIfc[myChains.length];
        int counter = 0;
        for (MyChainIfc myChain : myChains) {
            try {
                MyChainIfc clonedMyChain = cloneMyChain(myChain);
                clonedChains[counter] = clonedMyChain;
                counter += 1;
            } catch (ExceptionInMyStructurePackage exceptionInMyStructurePackage) {
                exceptionInMyStructurePackage.printStackTrace();
            }
        }
        updateAllMyAtomReference(clonedChains);

        // it is wrong but assume they are all amino

        MyChainIfc[] other1 = new MyChainIfc[0];
        MyChainIfc[] myNucleotideChains = other1;
        MyChainIfc[] other2 = new MyChainIfc[0];
        MyChainIfc[] myHetatmChains = other2;

        MyStructureIfc clone = null;
        try {
            clone = new MyStructure(clonedChains, myHetatmChains, myNucleotideChains,
                    ExpTechniquesEnum.UNDEFINED, algoParameters);
        } catch (ExceptionInMyStructurePackage exceptionInMyStructurePackage) {
            exceptionInMyStructurePackage.printStackTrace();
        }
        clone.setFourLetterCode(MyStructureConstants.PDB_ID_DEFAULT.toCharArray());

        fixParents(clone);
        return clone;
    }


    private MyStructureIfc makeClone(MyChainIfc myChain, AlgoParameters algoParameters) {
        MyChainIfc clonedMyChain = null;

        try {
            clonedMyChain = cloneMyChain(myChain);

        } catch (ExceptionInMyStructurePackage exceptionInMyStructurePackage) {
            exceptionInMyStructurePackage.printStackTrace();
        }

        fixBondedAtomReference(clonedMyChain);
        MyStructureTools.removeBondsToNonExistingAtoms(clonedMyChain);
        // TODO with bonds in removeNonExistingMyMonomerNeighbors
        MyStructureTools.removeNonExistingMyMonomerNeighbors(clonedMyChain);


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

        MyStructureIfc clone = null;
        try {
            clone = new MyStructure(myAminoChains, myHetatmChains, myNucleotideChains,
                    ExpTechniquesEnum.UNDEFINED, algoParameters);
        } catch (ExceptionInMyStructurePackage exceptionInMyStructurePackage) {
            exceptionInMyStructurePackage.printStackTrace();
        }
        clone.setFourLetterCode(MyStructureConstants.PDB_ID_DEFAULT.toCharArray());

        fixParents(clone);
        return clone;
    }


    private MyStructureIfc makeClone(MyMonomerIfc myMonomer, AlgoParameters algoParameters) {
        MyMonomerIfc clonedMyMonomer = null;
        try {
            clonedMyMonomer = cloneMyMonomer(myMonomer);

        } catch (ExceptionInMyStructurePackage exceptionInMyStructurePackage) {
            exceptionInMyStructurePackage.printStackTrace();
        }

        updateAllMyAtomReference(clonedMyMonomer);

        MyChainIfc[] myAminoChains = new MyChainIfc[0];
        MyChainIfc[] myHetatmChains = new MyChainIfc[0];
        MyChainIfc[] myNucleotideChains = new MyChainIfc[0];

        // then put it cleanly in a MyStructure and return it
        char[] parentChainId = null;
        if (myMonomer.getParent() != null) {
            parentChainId = myMonomer.getParent().getChainId();
        } else {
            parentChainId = MyStructureConstants.CHAIN_ID_DEFAULT.toCharArray();
        }
        MyChainIfc newChain = new MyChain(clonedMyMonomer, parentChainId);

        MyChainIfc[] chains = new MyChainIfc[1];
        chains[0] = newChain;

        if (String.valueOf(myMonomer.getType()).equals("amino")) {
            myAminoChains = chains;
            MyChainIfc[] other1 = new MyChainIfc[0];
            myNucleotideChains = other1;
            MyChainIfc[] other2 = new MyChainIfc[0];
            myHetatmChains = other2;
        }
        if (String.valueOf(myMonomer.getType()).equals("nucleotide")) {
            myNucleotideChains = chains;
            MyChainIfc[] other1 = new MyChainIfc[0];
            myAminoChains = other1;
            MyChainIfc[] other2 = new MyChainIfc[0];
            myHetatmChains = other2;
        }
        if (String.valueOf(myMonomer.getType()).equals("hetatm")) {
            myHetatmChains = chains;
            MyChainIfc[] other1 = new MyChainIfc[0];
            myNucleotideChains = other1;
            MyChainIfc[] other2 = new MyChainIfc[0];
            myAminoChains = other2;
        }


        MyStructureIfc clone = null;
        try {
            clone = new MyStructure(myAminoChains, myHetatmChains, myNucleotideChains,
                    ExpTechniquesEnum.UNDEFINED, algoParameters);
        } catch (ExceptionInMyStructurePackage exceptionInMyStructurePackage) {
            exceptionInMyStructurePackage.printStackTrace();
        }
        clone.setFourLetterCode(MyStructureConstants.PDB_ID_DEFAULT.toCharArray());

        fixParents(clone);
        return clone;
    }


    private void removeHydrogenAndcomputeStructuralInformations(AlgoParameters algoParameters) {

        // MyStructureTools.removeAllExplicitHydrogens(this);
        // computeStructuralInformation(this, algoParameters);
    }


    private void computeStructuralInformation(MyStructureIfc myStructure, AlgoParameters algoParameters) {
        fixParents(myStructure);
        MyStructureTools.computeAndStoreNeighBorhingAminoMonomersByDistanceBetweenRepresentativeMyAtom(myStructure, algoParameters);
        MyStructureTools.computeAndStoreNeighboringMonomersByBond(myStructure);
    }


    private void fixParents(MyStructureIfc myStructure) {
        for (MyChainIfc chain : myStructure.getAllChains()) {
            for (MyMonomerIfc monomer : chain.getMyMonomers()) {
                monomer.setParent(chain);
                for (MyAtomIfc atom : monomer.getMyAtoms()) {
                    atom.setParent(monomer);
                }
            }
        }
    }


    private void fixParents(MyMonomerIfc myMonomer) {

        for (MyAtomIfc atom : myMonomer.getMyAtoms()) {
            atom.setParent(myMonomer);
        }
    }


    private void updateAllMyAtomReference(MyMonomerIfc myMonomer) {

        fixBondedAtomReference(myMonomer);

    }


    private void updateAllMyAtomReference(MyChainIfc[] myChains) {

        for (MyChainIfc myChain : myChains) {
            fixBondedAtomReference(myChain);
        }
        MyStructureTools.removeBondsToMyAtomsNotInMyStructure(myChains);
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


    private MyChainIfc cloneMyChain(MyChainIfc chain) throws ExceptionInMyStructurePackage {

        Map<MyMonomerIfc, MyMonomerIfc> myMonomerToClonedMyMonomer = new LinkedHashMap<>();

        MyMonomerIfc[] myMonomersCloned = new MyMonomerIfc[chain.getMyMonomers().length];

        for (int i = 0; i < chain.getMyMonomers().length; i++) {
            myMonomersCloned[i] = cloneMyMonomer(chain.getMyMonomers()[i]);
            myMonomerToClonedMyMonomer.put(chain.getMyMonomers()[i], myMonomersCloned[i]);
        }
        MyChainIfc myChainCloned = new MyChain(myMonomersCloned, chain.getChainId());

        // need to handle neighbors by representative distance
        MyStructureTools.setAtomParentReference(myChainCloned);

        return myChainCloned;
    }


    private MyMonomerIfc cloneMyMonomer(MyMonomerIfc monomer) throws ExceptionInMyStructurePackage {

        MyAtomIfc[] myAtomsCloned = new MyAtomIfc[monomer.getMyAtoms().length];

        for (int i = 0; i < monomer.getMyAtoms().length; i++) {
            try {
                myAtomsCloned[i] = cloneMyAtom(monomer.getMyAtoms()[i]);
            } catch (ExceptionInMyStructurePackage e) {
                continue;
            }
        }
        MyMonomerIfc myMonomerCloned = new MyMonomer(myAtomsCloned, monomer.getThreeLetterCode(), monomer.getResidueID(), MyMonomerType.getEnumType(monomer.getType()), monomer.isWasHetatm(), monomer.getInsertionLetter(), monomer.getAltLocGroup());
        myMonomerCloned.setNeighboringAminoMyMonomerByRepresentativeAtomDistance(monomer.getNeighboringAminoMyMonomerByRepresentativeAtomDistance());
        MyStructureTools.setAtomParentReference(myMonomerCloned);

        return myMonomerCloned;
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
