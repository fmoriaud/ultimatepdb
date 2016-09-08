package math;

import mystructure.BigHetatmResidues;
import mystructure.MyAtomIfc;
import mystructure.MyMonomerIfc;
import mystructure.MyStructureTools;

public class ToolsMathAppliedToObjects {
	// -------------------------------------------------------------------
	// Static Methods
	// -------------------------------------------------------------------
	public static double computeDistanceBetweenTwoResidues(MyMonomerIfc monomer1, MyMonomerIfc monomer2) {
		float distance = 0.0f;

		// for large hetatm entities then the representative atom is not the best one
		if (BigHetatmResidues.isMyMonomerABigResidue(monomer1) || BigHetatmResidues.isMyMonomerABigResidue(monomer2)){
			distance = MyStructureTools.computeDistance(monomer1, monomer2);
			
		}else{
			distance = ToolsMath.computeDistance(getCoordinatesOfRepresentativeAtom(monomer1), getCoordinatesOfRepresentativeAtom(monomer2));
		}
		return distance;
	}



	public static float[] getCoordinatesOfRepresentativeAtom (MyMonomerIfc monomer) {

		MyAtomIfc representativeAtom = MyStructureTools.getRepresentativeMyAtom(monomer);
		if ( representativeAtom != null  ) {
			return representativeAtom.getCoords();
		} else {
			return null;
		}
	}


}
