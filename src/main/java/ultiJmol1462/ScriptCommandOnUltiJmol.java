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

public class ScriptCommandOnUltiJmol {
    // -------------------------------------------------------------------
    // Class variables
    // -------------------------------------------------------------------
    private String script;
    private String moleculeV3000;
    private AlgoParameters algoParameters;
    private Map<Results, Object> results = new LinkedHashMap<>();
    private Integer atomCountTarget;


    // -------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------
    /**
     * Uses a MyJmol from algoParameters
     * Minimize what is unfixed in the script
     * Energy returned is related to only what is unfixed
     *
     * @param script
     * @param moleculeV3000
     * @param algoParameters
     */
    public ScriptCommandOnUltiJmol(String script, String moleculeV3000, AlgoParameters algoParameters, Integer atomCountTarget) {

        this.script = script;
        this.moleculeV3000 = moleculeV3000;
        this.algoParameters = algoParameters;
        this.atomCountTarget = atomCountTarget;
    }


    // -------------------------------------------------------------------
    // Public && Interface method
    // -------------------------------------------------------------------
    public void execute() throws ExceptionInScoringUsingBioJavaJMolGUI {


        SafeUltiJmolUsage safeUltiJmolUsage = new SafeUltiJmolUsage(algoParameters);
        ScriptCommandOnUltiJmolTask scriptCommandOnUltiJmolTask = new ScriptCommandOnUltiJmolTask(script, moleculeV3000, atomCountTarget);
        safeUltiJmolUsage.setDoMyJmolTaskIfc(scriptCommandOnUltiJmolTask);

        safeUltiJmolUsage.run();
        results = safeUltiJmolUsage.getResults();

    }


    // -------------------------------------------------------------------
    // Getter and Setter
    // -------------------------------------------------------------------
    public Map<Results, Object> getResults() {
        return results;
    }
}
