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

import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import parameters.AlgoParameters;

import java.util.LinkedHashMap;
import java.util.Map;

public class GetEnergy {

    // -------------------------------------------------------------------
    // Class variables
    // -------------------------------------------------------------------
    private String moleculeV3000;
    private AlgoParameters algoParameters;
    private String script;

    private Map<Results, Object> results = new LinkedHashMap<>();
    private boolean convergenceReached = true;


    // -------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------
    /**
     * Uses a MyJmol from algoParameters
     *
     * @param script
     * @param moleculeV3000
     * @param algoParameters
     */
    public GetEnergy(String script, String moleculeV3000, AlgoParameters algoParameters) {

        this.script = script;
        this.moleculeV3000 = moleculeV3000;
        this.algoParameters = algoParameters;
    }


    // -------------------------------------------------------------------
    // Public && Interface method
    // -------------------------------------------------------------------
    public void execute() throws ExceptionInScoringUsingBioJavaJMolGUI {

        SafeUltiJmolUsage safeUltiJmolUsage = new SafeUltiJmolUsage(algoParameters);
        GetEnergyTask getEnergyTask = new GetEnergyTask(script, moleculeV3000);
        safeUltiJmolUsage.setDoMyJmolTaskIfc(getEnergyTask);

        safeUltiJmolUsage.run();
        results = safeUltiJmolUsage.getResults();
    }


    // -------------------------------------------------------------------
    // Getter and Setter
    // -------------------------------------------------------------------
    public Map<Results, Object> getResults() {
        return results;
    }

    public boolean isConvergenceReached() {
        return convergenceReached;
    }
}
