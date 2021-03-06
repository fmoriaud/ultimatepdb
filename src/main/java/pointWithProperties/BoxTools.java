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
package pointWithProperties;

import java.util.HashMap;
import java.util.Map;

import math.MathTools;
import parameters.AlgoParameters;
import mystructure.AtomProperties;
import mystructure.MyAtomIfc;
import mystructure.MyChainIfc;
import mystructure.MyMonomerIfc;
import mystructure.MyStructureIfc;

public class BoxTools {
    //-------------------------------------------------------------
    // Static methods
    //-------------------------------------------------------------
    public static Map<String, float[]> computeBoundariesOfBox(MyStructureIfc structure, AlgoParameters algoParameters) {

        float minX;
        float maxX;
        float minY;
        float maxY;
        float minZ;
        float maxZ;

        float[] atomBarycenter = computeBarycenter(structure);

        minX = atomBarycenter[0];
        maxX = atomBarycenter[0];
        minY = atomBarycenter[1];
        maxY = atomBarycenter[1];
        minZ = atomBarycenter[2];
        maxZ = atomBarycenter[2];

        Map<String, float[]> boundariesOfBox = new HashMap<>();

        for (MyChainIfc chain : structure.getAllChainsRelevantForShapeBuilding()) {
            for (MyMonomerIfc monomer : chain.getMyMonomers()) {
                for (MyAtomIfc atom : monomer.getMyAtoms()) {

                    if (atom.getCoords()[0] < minX) {
                        minX = atom.getCoords()[0];
                    }
                    if (atom.getCoords()[0] > maxX) {
                        maxX = atom.getCoords()[0];
                    }
                    if (atom.getCoords()[1] < minY) {
                        minY = atom.getCoords()[1];
                    }
                    if (atom.getCoords()[1] > maxY) {
                        maxY = atom.getCoords()[1];
                    }
                    if (atom.getCoords()[2] < minZ) {
                        minZ = atom.getCoords()[2];
                    }
                    if (atom.getCoords()[2] > maxZ) {
                        maxZ = atom.getCoords()[2];
                    }
                }
            }
        }

        float extraDistance = algoParameters.getEXTRA_DISTANCE_OUT_FORALLBOX();
        minX = minX - extraDistance;
        minY = minY - extraDistance;
        minZ = minZ - extraDistance;
        maxX = maxX + extraDistance;
        maxY = maxY + extraDistance;
        maxZ = maxZ + extraDistance;

        boundariesOfBox.put("x", new float[]{minX, maxX});
        boundariesOfBox.put("y", new float[]{minY, maxY});
        boundariesOfBox.put("z", new float[]{minZ, maxZ});

        return boundariesOfBox;
    }


    public static float[] computeBarycenter(MyStructureIfc structure) {

        float[] barycenter = new float[3];

        float fwhm = 1.0f;
        float sumFwhm = 0.0f;

        for (MyChainIfc chain : structure.getAllChainsRelevantForShapeBuilding()) {
            for (MyMonomerIfc monomer : chain.getMyMonomers()) {
                for (MyAtomIfc atom : monomer.getMyAtoms()) {
                    fwhm = AtomProperties.findFwhmForMyAtom(atom);
                    sumFwhm += fwhm;
                    for (int i = 0; i < 3; i++) {
                        barycenter[i] += fwhm * atom.getCoords()[i];
                    }
                }
            }
        }
        return MathTools.multiplyByScalar(barycenter, 1.0f / sumFwhm);
    }
}
