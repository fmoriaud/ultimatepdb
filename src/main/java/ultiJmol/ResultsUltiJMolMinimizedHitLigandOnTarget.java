package ultiJmol;

public class ResultsUltiJMolMinimizedHitLigandOnTarget {
	// -------------------------------------------------------------------
	// Class variables
	// -------------------------------------------------------------------
	private double receptorFixedLigandOptimizedEStart;
	private double receptorFixedLigandOptimizedEFinal;
	private int receptorFixedLigandOptimizedCountOfIteration;
	private boolean receptorFixedLigandOptimizedConvergenceReached;
	private double receptorFixedLigandOptimizedRmsdBeforeAndAfterOptimization;
	private int countOfLongDistanceChange;
	private double interactionEFinal;
	private double eCorrectedStrained;
	private double rmsdLigand = Double.MAX_VALUE; // TODO it is shitty as Hitscore used for all shapes and
	
	// it is actually only working if query is a peptide. In other cases this value stays to DoubleMax
	private double ratioPairedPointToHitPoints;
	



	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public ResultsUltiJMolMinimizedHitLigandOnTarget(double receptorFixedLigandOptimizedEStart, double receptorFixedLigandOptimizedEFinal, 
			int receptorFixedLigandOptimizedCountOfIteration, boolean receptorFixedLigandOptimizedConvergenceReached, 
			double receptorFixedLigandOptimizedRmsdBeforeAndAfterOptimization, int countOfLongDistanceChange, 
			double interactionEFinal, double eCorrectedStrained){

		this.receptorFixedLigandOptimizedEStart = receptorFixedLigandOptimizedEStart;
		this.receptorFixedLigandOptimizedEFinal = receptorFixedLigandOptimizedEFinal;
		this.receptorFixedLigandOptimizedCountOfIteration = receptorFixedLigandOptimizedCountOfIteration;
		this.receptorFixedLigandOptimizedConvergenceReached = receptorFixedLigandOptimizedConvergenceReached;
		this.receptorFixedLigandOptimizedRmsdBeforeAndAfterOptimization = receptorFixedLigandOptimizedRmsdBeforeAndAfterOptimization;
		this.countOfLongDistanceChange = countOfLongDistanceChange;
		this.interactionEFinal = interactionEFinal;
		this.eCorrectedStrained = eCorrectedStrained;
	}




	// -------------------------------------------------------------------
	// Public && Interface method
	// -------------------------------------------------------------------
	@Override public String toString() {
		StringBuilder result = new StringBuilder();
		String NEW_LINE = System.getProperty("line.separator");

		result.append("Estart = " + this.getReceptorFixedLigandOptimizedEStart() +
				" Efinal = " + this.getReceptorFixedLigandOptimizedEFinal() +
				" Iterations = " + this.getReceptorFixedLigandOptimizedCountOfIteration() + 
				" Convergence reached = " + this.isReceptorFixedLigandOptimizedConvergenceReached() + 
				" Rmsd before/after opt. = " + this.getReceptorFixedLigandOptimizedRmsdBeforeAndAfterOptimization() +
				" count of Long distance = " + this.countOfLongDistanceChange +
				" Einteraction final = " + this.interactionEFinal +
				" Einteraction + Estrainedligand = " + this.eCorrectedStrained +
				" rmsd ligand = " + this.rmsdLigand +
				" ratioPairedPointToHitPoints = " + this.ratioPairedPointToHitPoints
				+ NEW_LINE);


		return result.toString();
	}

	//------------------------
	// Getter and Setter
	//------------------------
	public double getReceptorFixedLigandOptimizedEStart() {
		return receptorFixedLigandOptimizedEStart;
	}

	public double getReceptorFixedLigandOptimizedEFinal() {
		return receptorFixedLigandOptimizedEFinal;
	}

	public int getReceptorFixedLigandOptimizedCountOfIteration() {
		return receptorFixedLigandOptimizedCountOfIteration;
	}

	public boolean isReceptorFixedLigandOptimizedConvergenceReached() {
		return receptorFixedLigandOptimizedConvergenceReached;
	}

	public double getReceptorFixedLigandOptimizedRmsdBeforeAndAfterOptimization() {
		return receptorFixedLigandOptimizedRmsdBeforeAndAfterOptimization;
	}

	public int getCountOfLongDistanceChange() {
		return countOfLongDistanceChange;
	}

	public double getInteractionEFinal() {
		return interactionEFinal;
	}

	public double geteCorrectedStrained() {
		return eCorrectedStrained;
	}

	public double getRmsdLigand() {
		return rmsdLigand;
	}
	
	public void setRmsdLigand(double rmsdLigand) {
		this.rmsdLigand = rmsdLigand;
	}
	
	public double getRatioPairedPointToHitPoints() {
		return ratioPairedPointToHitPoints;
	}
	public void setRatioPairedPointToHitPoints(double ratioPairedPointToHitPoints) {
		this.ratioPairedPointToHitPoints = ratioPairedPointToHitPoints;
	}
}
