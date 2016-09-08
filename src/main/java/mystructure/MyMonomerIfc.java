package mystructure;

public interface MyMonomerIfc {

	MyAtomIfc[] getMyAtoms();
	MyChainIfc[] getNeighboringAminoMyMonomerByRepresentativeAtomDistance();
	void setNeighboringAminoMyMonomerByRepresentativeAtomDistance(MyChainIfc[] neighboringMyMonomerByRepresentativeAtomDistance);
	MyMonomerIfc[] getNeighboringMyMonomerByBond();
	void setNeighboringMyMonomerByBond(MyMonomerIfc[] neighboringMyMonomerByBond);
	char[] getThreeLetterCode();
	int getResidueID();
	char[] getType();
	char getInsertionLetter();
	MyChainIfc getParent();
	void setParent(MyChainIfc parent);
	MyAtomIfc getMyAtomFromMyAtomName(char[] atomName);
	MyAtomIfc getAtomById(int atomId);
	void addAtom(MyAtomIfc atom);
	void setMyAtoms(MyAtomIfc[] myAtoms);
}
