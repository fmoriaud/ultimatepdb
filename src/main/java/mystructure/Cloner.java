package mystructure;

import math.ToolsMath;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import parameters.AlgoParameters;
import shapeCompare.PairingTools;
import shapeCompare.ResultsFromEvaluateCost;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Fabrice on 19/09/16.
 */
public class Cloner {

    private MyMonomerIfc myMonomer;
    private MyChainIfc myChain;
    private MyChainIfc[] myChains;
    private AlgoParameters algoParameters;


    private Map<MyAtomIfc, MyAtomIfc> keyIsOldAtomValueIsNewAtom = new LinkedHashMap<>();


    //-------------------------------------------------------------
    // Constructors
    //-------------------------------------------------------------
    public Cloner(MyStructureIfc myStructure) {


    }


    public Cloner(MyChainIfc[] myChains, AlgoParameters algoParameters) {

        this.myChains = myChains;
        this.algoParameters = algoParameters;

    }


    public Cloner(MyChainIfc myChain, AlgoParameters algoParameters) {

        this.myChain = myChain;
        this.algoParameters = algoParameters;

    }


    public Cloner(MyMonomerIfc myMonomer, AlgoParameters algoParameters) {

        this.myMonomer = myMonomer;
        this.algoParameters = algoParameters;
    }


    public MyStructureIfc getClone() {

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

        updateAllMyAtomReference(clonedMyChain);
        MyStructureTools.removeBondsToNonExistingAtoms(myChain);
        // TODO with bonds in removeNonExistingMyMonomerNeighbors
        MyStructureTools.removeNonExistingMyMonomerNeighbors(myChain);

        // TODO clean monomer by bond and by distance

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

        fixBondedAtom(myMonomer);

    }


    private void updateAllMyAtomReference(MyChainIfc[] myChains) {

        for (MyChainIfc myChain : myChains) {
            fixBondedAtom(myChain);
        }
        MyStructureTools.removeBondsToMyAtomsNotInMyStructure(myChains);
    }

    private void updateAllMyAtomReference(MyChainIfc myChain) {

        fixBondedAtom(myChain);

    }

    private void fixBondedAtom(MyStructureIfc myStructure) {

        for (MyChainIfc myChain : myStructure.getAllChains()) {
            fixBondedAtom(myChain);
        }
        MyStructureTools.removeBondsToMyAtomsNotInMyStructure(myStructure);
    }


    private void fixBondedAtom(MyChainIfc myChain) {

        for (MyMonomerIfc monomer : myChain.getMyMonomers()) {
            fixBondedAtom(monomer);
        }
        MyStructureTools.removeBondsToMyAtomsNotInMyStructure(myChain);
    }


    private void fixBondedAtom(MyMonomerIfc myMonomer) {

        for (MyAtomIfc atom : myMonomer.getMyAtoms()) {
            MyBondIfc[] bonds = atom.getBonds();
            if (bonds != null && bonds.length > 0) {
                for (MyBondIfc bond : bonds) {
                    MyAtomIfc bondedAtom = bond.getBondedAtom();
                    bond.setBondedAtom(keyIsOldAtomValueIsNewAtom.get(bondedAtom));
                }
            }
        }
        MyStructureTools.removeBondsToMyAtomsNotInMyStructure(myMonomer);
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
