package structure;

public interface MyBondIfc {

	MyAtomIfc getBondedAtom();
	void setBondedAtom(MyAtomIfc atom);
	int getBondOrder();
}
