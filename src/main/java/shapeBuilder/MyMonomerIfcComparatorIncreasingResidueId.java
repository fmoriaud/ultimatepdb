package shapeBuilder;

import java.util.Comparator;

import mystructure.MyMonomerIfc;

public class MyMonomerIfcComparatorIncreasingResidueId implements Comparator<MyMonomerIfc>{

	@Override
	public int compare(MyMonomerIfc monomer1, MyMonomerIfc monomer2) {
		int monomer1ID = monomer1.getResidueID();
		int monomer2ID = monomer2.getResidueID();

		return (monomer2ID>monomer1ID ? -1 : (monomer1ID==monomer2ID ? 0 : 1));
	}
}
