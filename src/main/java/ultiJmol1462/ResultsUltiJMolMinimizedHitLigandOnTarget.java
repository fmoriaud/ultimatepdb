package ultiJmol1462;

public class ResultsUltiJMolMinimizedHitLigandOnTarget {
    // -------------------------------------------------------------------
    // Class variables
    // -------------------------------------------------------------------
    private int countOfLongDistanceChange;
    private float interactionEFinal;
    private float ligandStrainedEnergy;
    private float rmsdLigand = Float.MAX_VALUE;
    private boolean allconvergenceReached;


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public ResultsUltiJMolMinimizedHitLigandOnTarget(int countOfLongDistanceChange,
                                                     float interactionEFinal, float strainedEnergyLigand, float rmsdLigand, boolean allconvergenceReached) {

        this.countOfLongDistanceChange = countOfLongDistanceChange;
        this.interactionEFinal = interactionEFinal;
        this.ligandStrainedEnergy = strainedEnergyLigand;
        this.rmsdLigand = rmsdLigand;
        this.allconvergenceReached = allconvergenceReached;
    }


    // -------------------------------------------------------------------
    // Public && Interface method
    // -------------------------------------------------------------------
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");

        result.append(
                " count of Long distance = " + this.countOfLongDistanceChange +
                        " Einteraction final = " + this.interactionEFinal +
                        " Einteraction + Estrainedligand = " + this.ligandStrainedEnergy +
                        " rmsd ligand = " + this.rmsdLigand
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

    public boolean isAllconvergenceReached() {
        return allconvergenceReached;
    }

}
