package ultiJmol;

import structure.MyStructureIfc;

public class ResultsUltiJMolMinimizeSideChain {

	// -------------------------------------------------------------------
	// Class variables
	// -------------------------------------------------------------------
	private MyStructureIfc minimizedStructure;
	private float strainedEnergySideChainAfterMinimization;




	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public ResultsUltiJMolMinimizeSideChain(MyStructureIfc minimizedStructure, float strainedEnergySideChainAfterMinimization){
		this.minimizedStructure = minimizedStructure;
		this.strainedEnergySideChainAfterMinimization = strainedEnergySideChainAfterMinimization;
	}




	//------------------------
	// Getter and Setter
	//------------------------
	public MyStructureIfc getMinimizedStructure() {
		return minimizedStructure;
	}



	public float getStrainedEnergySideChainAfterMinimization() {
		return strainedEnergySideChainAfterMinimization;
	}


}
