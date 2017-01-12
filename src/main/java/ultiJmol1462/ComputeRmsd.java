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

import math.MathTools;
import parameters.AlgoParameters;
import mystructure.ExceptionInMyStructurePackage;
import mystructure.MyAtomIfc;
import mystructure.MyStructure;
import mystructure.MyStructureIfc;

public class ComputeRmsd {
    // -------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------
    private final float thresholdLongDistance = 2.0f;


    // -------------------------------------------------------------------
    // Class variables
    // -------------------------------------------------------------------
    private String ligandAfterV3000;
    private String ligandBeforeV3000;
    private AlgoParameters algoParameters;

    private float rmsd;
    private int countOfLongDistanceChange;


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public ComputeRmsd(String ligandAfterV3000, String ligandBeforeV3000, AlgoParameters algoParameters) {

        this.ligandAfterV3000 = ligandAfterV3000;
        this.ligandBeforeV3000 = ligandBeforeV3000;
        this.algoParameters = algoParameters;
        try {
            compute();
        } catch (ExceptionInMyStructurePackage exceptionInMyStructurePackage) {
            rmsd = -1f;
        }
    }


    // -------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------
    private void compute() throws ExceptionInMyStructurePackage {

        MyStructureIfc ligandAfter = new MyStructure(ligandAfterV3000);
        MyStructureIfc ligandBefore = new MyStructure(ligandBeforeV3000);

        MyAtomIfc[] atomsBefore = ligandBefore.getAllAminochains()[0].getMyMonomers()[0].getMyAtoms();
        MyAtomIfc[] atomsAfter = ligandAfter.getAllAminochains()[0].getMyMonomers()[0].getMyAtoms();

        if (atomsBefore.length != atomsAfter.length) {
            rmsd = -1.0f; // meaning there is a problem so cannot be computed
            return;
        }
        float sum = 0;
        for (int i = 0; i < atomsBefore.length; i++) {
            MyAtomIfc atomBefore = atomsBefore[i];
            MyAtomIfc atomAfter = atomsAfter[i];
            float distance = MathTools.computeDistance(atomBefore.getCoords(), atomAfter.getCoords());
            if (distance > thresholdLongDistance) {
                countOfLongDistanceChange += 1;
            }
            sum += distance * distance;
        }
        sum /= atomsBefore.length;
        rmsd = (float) Math.sqrt(sum);
    }


    // -------------------------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------------------------
    public float getRmsd() {
        return rmsd;
    }

    public int getCountOfLongDistanceChange() {
        return countOfLongDistanceChange;
    }
}
