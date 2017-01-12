/*
Author:
      Fabrice Moriaud <fmoriaud@ultimatepdb.org>

  Copyright (c) 2016 Fabrice Moriaud

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
  */
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


    // -------------------------------------------------------------------
    // Getter and Setter
    // -------------------------------------------------------------------
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

    public boolean isAllconvergenceReached() {
        return allconvergenceReached;
    }
}
