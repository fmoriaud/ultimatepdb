package structure;

public interface MyAtomIfc {

	float[] getCoords();
	void setCoords(float[] coords);
	char[] getElement();
	char[] getAtomName();
	MyMonomerIfc getParent();
	void setParent(MyMonomerIfc parent);
	MyBondIfc[] getBonds();
	void setBonds(MyBondIfc[] bonds);
	int getOriginalAtomId();

	void setOriginalAtomId(int originalAtomId);
	void addBond(MyBondIfc bond);
	void removeBond(MyBondIfc bond);
}
