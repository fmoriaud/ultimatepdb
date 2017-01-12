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
import jmolgui.UltiJmol1462;
import org.jmol.minimize.Minimizer;

import java.util.LinkedHashMap;
import java.util.Map;

public class GetEnergyTask implements DoMyJmolTaskIfc {
    // -------------------------------------------------------------------
    // Class variables
    // -------------------------------------------------------------------
    private String moleculeV3000;
    private String script;

    private Map<Results, Object> results = new LinkedHashMap<>();
    private String name;


    // -------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------
    public GetEnergyTask(String script, String moleculeV3000) {

        this.script = script;
        this.moleculeV3000 = moleculeV3000;
        this.name = "GetEnergyTask";
    }


    // -------------------------------------------------------------------
    // Public && Interface method
    // -------------------------------------------------------------------
    @Override
    public boolean doAndReturnConvergenceStatus(UltiJmol1462 ultiJmol) throws ExceptionInScoringUsingBioJavaJMolGUI {

        ultiJmol.jmolPanel.openStringInline(moleculeV3000);

        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            return false;
        }

        String newScript = script.replace("set minimizationsteps 50", "set minimizationsteps 0");

        ultiJmol.jmolPanel.evalString(newScript);

        if (!script.contains("minimize")) {
            return false;
        }

        Float energyAsInitialAsPossible = waitMinimizationEnergyAvailable(ultiJmol);

        if (energyAsInitialAsPossible == null) {
            return false;
        }
        results.put(Results.INITIAL_ENERGY, energyAsInitialAsPossible);
        // Whatever is the minimize script which contains what to fix and that matters for the energy
        ultiJmol.jmolPanel.evalString("minimize clear");

        try {
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            return false;
        }

        ultiJmol.jmolPanel.evalString("minimize energy");
        try {
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            return false;
        }

        ultiJmol.jmolPanel.evalString("show minimization");

        try {
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            return false;
        }
        return true;
    }

    @Override
    public Map<Results, Object> getResults() {
        return results;
    }

    @Override
    public String getName() {
        return name;
    }


    //-------------------------------------------------------------
    // Implementation
    //-------------------------------------------------------------
    private Float waitMinimizationEnergyAvailable(UltiJmol1462 ultiJmol) throws ExceptionInScoringUsingBioJavaJMolGUI {

        int maxIteration = 20;
        int countIteration = 0;

        long waitTimeMillisecond = 1000;
        Minimizer minimizer = ultiJmol.jmolPanel.getViewer().getMinimizer(true);

        while (minimizer == null || minimizer.getMinimizationEnergy() == null || Math.abs(minimizer.getMinimizationEnergy()) < 0.01) {
            try {
                Thread.sleep(waitTimeMillisecond);
                countIteration += 1;
                if (countIteration > maxIteration) {
                    return null;
                }
            } catch (InterruptedException e) {
                return null;
            }
            minimizer = ultiJmol.jmolPanel.getViewer().getMinimizer(true);
        }
        return minimizer.getMinimizationEnergy();
    }
}
