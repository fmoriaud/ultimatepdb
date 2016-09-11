package ultiJmol1462;

import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import parameters.AlgoParameters;

public class Minimize {
	//------------------------
	// Class variables
	//------------------------
	private Float receptorFixedLigandOptimizedEStart;
	private Float energyComplexFinal;
	private int countIteration;
	private boolean receptorFixedLigandOptimizedConvergenceReached;


	private MyJmol1462 ultiJMol;
	private String selectCommandOfWhatToFix;
	private AlgoParameters algoParameters;




	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public Minimize(MyJmol1462 ultiJMol, String selectCommandOfWhatToFix, AlgoParameters algoParameters){
		this.ultiJMol = ultiJMol;
		this.selectCommandOfWhatToFix = selectCommandOfWhatToFix;
		this.algoParameters = algoParameters;
	}


	public boolean compute() throws InterruptedException, ExceptionInScoringUsingBioJavaJMolGUI{

		ultiJMol.jmolPanel.evalString("set forcefield \"UFF\"\n" + "set minimizationsteps 50\n");
		ultiJMol.jmolPanel.evalString("set logLevel 0\nset undo ON\n set echo off\n set useMinimizationThread ON\n");

		// shall I unfix all before to make it more safe 
		ultiJMol.jmolPanel.evalString("minimize FIX {" + selectCommandOfWhatToFix + "} select {*}\n");

		energyComplexFinal = 1E9f;
		// TODO extract this method and improve by a maximum count of iteration as a parameter, lenght of delay, diff threshold to stop ...
		int maxIteration = 100;
		countIteration = 0;
		receptorFixedLigandOptimizedConvergenceReached = false;

		boolean goAhead = true;

		Thread.sleep(2000L);
		receptorFixedLigandOptimizedEStart = MyJmolTools.getEnergyBiojavaJmolNewCode(ultiJMol, algoParameters);

		while (countIteration <= maxIteration && goAhead == true){

			Thread.sleep(2000L);

			//double currentEnergy = WritingTempFilesToDiskAsACommunicationWithJMol.getEnergyBiojavaJmol(StaticObjects.ultiJMol, algoParameters);

			Float currentEnergy = MyJmolTools.getEnergyBiojavaJmolNewCode(ultiJMol, algoParameters);
			if (currentEnergy == null){
				System.out.println();
			}

			countIteration +=1;

			System.out.println("complex currentEnergy = " + currentEnergy);
			// when too high then I should give up 
			if (currentEnergy > 1E9){
				System.out.println("Minimization is aborted as energy is > 1E8 ");
				return false;
			}

			if (Math.abs(currentEnergy - energyComplexFinal) < 5.0){
				goAhead = false;
			}
			energyComplexFinal = currentEnergy;
		}

		ultiJMol.jmolPanel.evalString("minimize stop");
		System.out.println("did " + countIteration + " iterations in scoreByMinimizingLigandOnFixedReceptor");

		Thread.sleep(2000L);

		if (countIteration <= maxIteration == false){
			receptorFixedLigandOptimizedConvergenceReached = false;
		}else{
			receptorFixedLigandOptimizedConvergenceReached = true;
		}
		System.out.println("Ecf = " + energyComplexFinal);
		return true;
	}


	


	// -------------------------------------------------------------------
	// Getter & Setter
	// -------------------------------------------------------------------
	public Float getReceptorFixedLigandOptimizedEStart() {
		return receptorFixedLigandOptimizedEStart;
	}

	public Float getEnergyComplexFinal() {
		return energyComplexFinal;
	}

	public int getCountIteration() {
		return countIteration;
	}
	
	public boolean isReceptorFixedLigandOptimizedConvergenceReached() {
		return receptorFixedLigandOptimizedConvergenceReached;
	}
}
