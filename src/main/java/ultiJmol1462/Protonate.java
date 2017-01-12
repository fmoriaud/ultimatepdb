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
import mystructure.MyStructureIfc;
import parameters.AlgoParameters;

import java.util.LinkedHashMap;
import java.util.Map;

public class Protonate {
    // -------------------------------------------------------------------
    // Class variables
    // -------------------------------------------------------------------
    private MyStructureIfc myStructure;
    private AlgoParameters algoParameters;

    private Map<Results, Object> results = new LinkedHashMap<>();
    private MyStructureIfc protonatedMyStructure;


    // -------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------
    /**
     * Assume there are no hydrogens
     * Uses a MyJmol from algoParameters
     *
     * @param myStructure
     * @param algoParameters
     */
    public Protonate(MyStructureIfc myStructure, AlgoParameters algoParameters) {
        this.myStructure = myStructure;
        this.algoParameters = algoParameters;
    }


    // -------------------------------------------------------------------
    // Public && Interface method
    // -------------------------------------------------------------------
    public void compute() throws ExceptionInScoringUsingBioJavaJMolGUI {

        SafeUltiJmolUsage safeUltiJmolUsage = new SafeUltiJmolUsage(algoParameters);
        ProtonateTask protonateTask = new ProtonateTask(myStructure, algoParameters);
        safeUltiJmolUsage.setDoMyJmolTaskIfc(protonateTask);

        safeUltiJmolUsage.run();
        results = safeUltiJmolUsage.getResults();

        protonatedMyStructure = (MyStructureIfc) results.get(Results.PROTONATED_STRUCTURE);
    }


    // -------------------------------------------------------------------
    // Getter and Setter
    // -------------------------------------------------------------------
    public MyStructureIfc getProtonatedMyStructure() {
        return protonatedMyStructure;
    }

    public Map<Results, Object> getResults() {
        return results;
    }
}
