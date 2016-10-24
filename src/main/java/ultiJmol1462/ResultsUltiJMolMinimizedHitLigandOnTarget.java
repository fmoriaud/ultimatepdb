package ultiJmol1462;

public class ResultsUltiJMolMinimizedHitLigandOnTarget {
	// -------------------------------------------------------------------
	// Class variables
	// -------------------------------------------------------------------
	private int countOfLongDistanceChange;
	private float interactionEFinal;
	private float ligandStrainedEnergy;
	private float rmsdLigand = Float.MAX_VALUE;


	// it is actually only working if query is a peptide. In other cases this value stays to DoubleMax
	private float ratioPairedPointToHitPoints = Float.MAX_VALUE;


	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public ResultsUltiJMolMinimizedHitLigandOnTarget(int countOfLongDistanceChange,
			float interactionEFinal, float strainedEnergyLigand, float rmsdLigand){

		this.countOfLongDistanceChange = countOfLongDistanceChange;
		this.interactionEFinal = interactionEFinal;
		this.ligandStrainedEnergy = strainedEnergyLigand;
		this.rmsdLigand = rmsdLigand;
	}




	// -------------------------------------------------------------------
	// Public && Interface method
	// -------------------------------------------------------------------
	@Override public String toString() {
		StringBuilder result = new StringBuilder();
		String NEW_LINE = System.getProperty("line.separator");

		result.append(
				" count of Long distance = " + this.countOfLongDistanceChange +
				" Einteraction final = " + this.interactionEFinal +
				" Einteraction + Estrainedligand = " + this.ligandStrainedEnergy +
				" rmsd ligand = " + this.rmsdLigand +
				" ratioPairedPointToHitPoints = " + this.ratioPairedPointToHitPoints
				+ NEW_LINE);


		return result.toString();
	}

	//------------------------
	// Getter and Setter
	//------------------------
	public int getCountOfLongDistanceChange() {
		return countOfLongDistanceChange;
	}

	public double getInteractionEFinal() {
		return interactionEFinal;
	}

	public double getLigandStrainedEnergy() {
		return ligandStrainedEnergy;
	}

	public double getRmsdLigand() {
		return rmsdLigand;
	}

	public void setRmsdLigand(float rmsdLigand) {
		this.rmsdLigand = rmsdLigand;
	}
	
	public double getRatioPairedPointToHitPoints() {
		return ratioPairedPointToHitPoints;
	}

	public void setRatioPairedPointToHitPoints(float ratioPairedPointToHitPoints) {
		this.ratioPairedPointToHitPoints = ratioPairedPointToHitPoints;
	}
}
