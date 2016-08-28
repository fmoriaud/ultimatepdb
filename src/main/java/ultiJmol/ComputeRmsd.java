package ultiJmol;

import math.ToolsMath;
import parameters.AlgoParameters;
import structure.ExceptionInMyStructurePackage;
import structure.MyAtomIfc;
import structure.MyStructure;
import structure.MyStructureIfc;

public class ComputeRmsd {
	//------------------------
	// Constants
	//------------------------
	private final float thresholdLongDistance = 2.0f;




	//------------------------
	// Class variables
	//------------------------
	private String ligandAfterV3000;
	private String ligandBeforeV3000;
	private AlgoParameters algoParameters;

	private float rmsd;
	private int countOfLongDistanceChange;




	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public ComputeRmsd(String ligandAfterV3000, String ligandBeforeV3000, AlgoParameters algoParameters){

		this.ligandAfterV3000 = ligandAfterV3000;
		this.ligandBeforeV3000 = ligandBeforeV3000;
		this.algoParameters = algoParameters;
		compute();
	}



	// -------------------------------------------------------------------
	// Implementation
	// -------------------------------------------------------------------
	private void compute(){

		// there should be the same except the coodinates
		MyStructureIfc ligandAfter = null;
		try {
			ligandAfter = new MyStructure(ligandAfterV3000, algoParameters);
		} catch (ExceptionInMyStructurePackage e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MyStructureIfc ligandBefore = null;
		try {
			ligandBefore = new MyStructure(ligandBeforeV3000, algoParameters);
		} catch (ExceptionInMyStructurePackage e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		MyAtomIfc[] atomsBefore = ligandBefore.getAllAminochains()[0].getMyMonomers()[0].getMyAtoms();
		MyAtomIfc[] atomsAfter = ligandAfter.getAllAminochains()[0].getMyMonomers()[0].getMyAtoms();

		if (atomsBefore.length != atomsAfter.length){
			rmsd = -1.0f; // meaning there is a problem so cannot be computed

			return;
		}
		float sum = 0;
		for (int i=0; i<atomsBefore.length; i++){
			MyAtomIfc atomBefore = atomsBefore[i];
			MyAtomIfc atomAfter = atomsAfter[i];
			float distance = ToolsMath.computeDistance(atomBefore.getCoords(), atomAfter.getCoords());
			if (distance > thresholdLongDistance){
				countOfLongDistanceChange += 1;
			}
			sum += distance * distance;
		}
		sum /= atomsBefore.length;
		rmsd = (float) Math.sqrt(sum);
	}


	// -------------------------------------------------------------------
	// Getter & Setter
	// -------------------------------------------------------------------
	public float getRmsd() {
		return rmsd;
	}

	public int getCountOfLongDistanceChange() {
		return countOfLongDistanceChange;
	}
}
