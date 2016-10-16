package mystructure;

import java.util.Arrays;


public class MyMonomer implements MyMonomerIfc {
    //-------------------------------------------------------------
    // Class variables
    //-------------------------------------------------------------
    private MyAtomIfc[] myAtoms;

    private MyChainIfc[] neighboringAminoMyMonomerByRepresentativeAtomDistance;
    private MyMonomerIfc[] neighboringMyMonomerByBond;

    private char[] threeLetterCode;

    private int residueID;
    private char[] type;

    private boolean wasHetatm = false;
    private char insertionLetter;
    private char altLocGroup;

    private MyChainIfc parent;

    private char[] secStruc;


    //-------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------

    /**
     * Class to store an ensemble of MyAtom, PDB structure residues and nucleic acids
     *
     * @param myAtoms
     * @param threeLetterCode: as found in PDB files
     * @param residueID
     * @param myMonomerType:   same as from org.biojava.bio.structure.Group type: amino, hetatm or nucleotide
     * @param insertionLetter
     * @throws ExceptionInMyStructurePackage
     */
    public MyMonomer(MyAtomIfc[] myAtoms, char[] threeLetterCode, int residueID, MyMonomerType myMonomerType, boolean wasHetatm, char insertionLetter, char altLocGroup) throws ExceptionInMyStructurePackage {
        this.myAtoms = myAtoms;
        this.threeLetterCode = threeLetterCode;
        this.residueID = residueID;
        if (myMonomerType == null) {
            throw new ExceptionInMyStructurePackage("MyMonomer cannot be built with null MyMonomerType");
        }
        this.type = myMonomerType.getType();
        this.wasHetatm = wasHetatm;
        this.insertionLetter = insertionLetter;
        this.altLocGroup = altLocGroup;
        this.secStruc = secStruc;
    }


    //-------------------------------------------------------------
    // Interface & Public methods
    //-------------------------------------------------------------

    /**
     * For debugging only
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("monomer: " + parent.toString() + " " + String.valueOf(threeLetterCode) + "  " + residueID);
        if (altLocGroup != " ".toCharArray()[0]) {
            sb.append(" altLoc = " + altLocGroup);
        }
        return sb.toString();
    }


    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof MyMonomer)) return false;
        MyMonomer otherMyMonomer = (MyMonomer) other;
        if (!Arrays.equals(this.getParent().getChainId(), otherMyMonomer.getParent().getChainId())) {
            return false;
        }
        if (this.getResidueID() != otherMyMonomer.residueID) {
            return false;
        }
        if (!Arrays.equals(this.getThreeLetterCode(), otherMyMonomer.getThreeLetterCode())) {
            return false;
        }
        return true;
    }


    /**
     * Get the atom with a given atomName or null if not found
     */
    @Override
    public MyAtomIfc getMyAtomFromMyAtomName(char[] atomName) {
        MyAtomIfc atomToreturn = null;

        for (MyAtomIfc atom : this.getMyAtoms()) {
            if (String.valueOf(atom.getAtomName()).equals(String.valueOf(atomName))) {
                atomToreturn = atom;
                break;
            }
        }
        return atomToreturn;
    }


    /**
     * get the atom with a given originalAtomId or null if not found
     */
    @Override
    public MyAtomIfc getAtomById(int atomId) {
        for (MyAtomIfc myAtom : myAtoms) {
            if (myAtom.getOriginalAtomId() == atomId) {
                return myAtom;
            }
        }
        return null;
    }


    /**
     * Add one atom
     */
    @Override
    public void addAtom(MyAtomIfc atom) {
        MyAtomIfc[] newAtoms = new MyAtomIfc[myAtoms.length + 1];
        for (int i = 0; i < myAtoms.length; i++) {
            newAtoms[i] = myAtoms[i];
        }
        newAtoms[myAtoms.length] = atom;
        myAtoms = newAtoms;
    }


    public boolean containAtom(MyAtomIfc myAtom) {

        for (MyAtomIfc myAtomInMonomer : getMyAtoms()) {
            if (myAtomInMonomer == myAtom) {
                return true;
            }
        }
        return false;
    }


    /**
     * Delete an atom if found and delete the bonds in the monomer
     *
     * @param atom
     */
    public void deleteAtomAndbonds(MyAtomIfc atom) {

        if (!containAtom(atom)) {
            return;
        }
        MyAtomIfc[] newAtoms = new MyAtomIfc[myAtoms.length - 1];
        int atomId = 0;
        for (int i = 0; i < myAtoms.length; i++) {
            if (!(myAtoms[i] == atom)) {
                newAtoms[atomId] = myAtoms[i];
                atomId += 1;
            }
        }

        for (MyBondIfc bond : atom.getBonds()) {
            MyAtomIfc bondedAtom = bond.getBondedAtom();
            if (containAtom(bondedAtom)) { // if bonded atom to deleted atom is from this monomer
                for (MyBondIfc bondFromBondedAtom : bondedAtom.getBonds()) {
                    if (bondFromBondedAtom.getBondedAtom() == atom) {
                        System.out.println("delete bond " + bondFromBondedAtom);
                        bondedAtom.removeBond(bondFromBondedAtom);
                    }
                }
            }
        }
        myAtoms = newAtoms;
    }


    //-------------------------------------------------------------
    // Implementation
    //-------------------------------------------------------------
    //	private boolean isTypeValid(char[] type){
    //
    //		if (Arrays.equals(GroupType.AMINOACID.name().toCharArray(), type)){
    //			return true;
    //		}
    //		if (Arrays.equals(GroupType.HETATM.name().toCharArray(), type)){
    //			return true;
    //		}
    //		if (Arrays.equals(GroupType.NUCLEOTIDE.name().toCharArray(), type)){
    //			return true;
    //		}
    //		// TODO Add XXX when it is used in V3000 format
    //		return false;
    //	}


    //-------------------------------------------------------------
    // Getters and Setters
    //-------------------------------------------------------------
    @Override
    public MyAtomIfc[] getMyAtoms() {
        return myAtoms;
    }


    @Override
    public void setMyAtoms(MyAtomIfc[] myAtoms) {
        this.myAtoms = myAtoms;
    }


    /**
     * Get neighboring myMonomers by distance through space in between representative atoms
     */
    @Override
    public MyChainIfc[] getNeighboringAminoMyMonomerByRepresentativeAtomDistance() {
        if (neighboringAminoMyMonomerByRepresentativeAtomDistance == null) {
            neighboringAminoMyMonomerByRepresentativeAtomDistance = new MyChainIfc[0];
        }
        return neighboringAminoMyMonomerByRepresentativeAtomDistance;
    }


    /**
     * Set neighboring myMonomers by distance through space in between representative atoms
     */
    @Override
    public void setNeighboringAminoMyMonomerByRepresentativeAtomDistance(
            MyChainIfc[] neighboringMyMonomerByRepresentativeAtomDistance) {
        this.neighboringAminoMyMonomerByRepresentativeAtomDistance = neighboringMyMonomerByRepresentativeAtomDistance;
    }


    /**
     * Get bonded myMonomers
     */
    @Override
    public MyMonomerIfc[] getNeighboringMyMonomerByBond() {
        if (neighboringMyMonomerByBond == null) {
            neighboringMyMonomerByBond = new MyMonomerIfc[0];
        }
        return neighboringMyMonomerByBond;
    }


    /**
     * Set bonded myMonomers
     */
    @Override
    public void setNeighboringMyMonomerByBond(
            MyMonomerIfc[] neighboringMyMonomerByBond) {
        this.neighboringMyMonomerByBond = neighboringMyMonomerByBond;
    }


    @Override
    public char[] getThreeLetterCode() {
        return threeLetterCode;
    }


    @Override
    public int getResidueID() {
        return residueID;
    }


    @Override
    public char[] getType() {
        return type;
    }


    @Override
    public void setType(char[] type) {
        this.type = type;
    }

    @Override
    public boolean isWasHetatm() {
        return wasHetatm;
    }

    @Override
    public void setWasHetatm(boolean wasHetatm) {
        this.wasHetatm = wasHetatm;
    }

    @Override
    public char getInsertionLetter() {
        return insertionLetter;
    }


    @Override
    public char getAltLocGroup() {
        return altLocGroup;
    }

    @Override
    public MyChainIfc getParent() {
        return parent;
    }


    public void setParent(MyChainIfc parent) {
        this.parent = parent;
    }

}
