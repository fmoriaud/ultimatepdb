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

import jmolgui.UltiJmol1462;
import parameters.AlgoParameters;

import java.util.LinkedHashMap;
import java.util.Map;

public class SafeUltiJmolUsage {
    // -------------------------------------------------------------------
    // Class variables
    // -------------------------------------------------------------------
    private DoMyJmolTaskIfc doMyJmolTaskIfc;
    private Map<Results, Object> results = new LinkedHashMap<>();
    private AlgoParameters algoParameters;

    private boolean convergenceReached = true;


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public SafeUltiJmolUsage(AlgoParameters algoParameters) {

        this.algoParameters = algoParameters;
    }


    // -------------------------------------------------------------------
    // Public && Interface method
    // -------------------------------------------------------------------
    public void run() {

        UltiJmol1462 ultiJmol = null;
        try {
            ultiJmol = algoParameters.ultiJMolBuffer.get();

            Boolean convergenceStatus = doMyJmolTaskIfc.doAndReturnConvergenceStatus(ultiJmol);
            results.putAll(doMyJmolTaskIfc.getResults());
            results.put(Results.CONVERGENCE_REACHED, convergenceStatus);

        } catch (Exception e) {

            results.put(Results.STATUS, "ultiJmol crash");
            // If exception then ultijmol is disposed and a new one is put in the buffer
            System.out.println("Exception in  SafeUltiJmolUsage " + doMyJmolTaskIfc.getName());
            ultiJmol.frame.dispose(); // it is destroyed so not returned to factory
            try {
                algoParameters.ultiJMolBuffer.put(new UltiJmol1462());
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            return;

        }

        try {
            ultiJmol.jmolPanel.evalString("zap");
            ultiJmol.jmolPanel.evalString("cache CLEAR");
            ultiJmol.jmolPanel.evalString("reset ALL");
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {

            }
            algoParameters.ultiJMolBuffer.put(ultiJmol);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    // -------------------------------------------------------------------
    // Getter and Setter
    // -------------------------------------------------------------------
    public void setDoMyJmolTaskIfc(DoMyJmolTaskIfc doMyJmolTaskIfc) {
        this.doMyJmolTaskIfc = doMyJmolTaskIfc;
    }

    public Map<Results, Object> getResults() {
        return results;
    }
}
