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

import java.util.Map;

import parameters.AlgoParameters;
import mystructure.MyStructureIfc;

public class Box {
    //-------------------------------------------------------------
    // Class variables
    //-------------------------------------------------------------
    private Map<String, float[]> boundariesOfBox;
    private float deltaX;
    private float deltaY;
    private float deltaZ;

    private float minX;
    private float minY;
    private float minZ;

    private int countOfPointsInXDirection;

    private int countOfPointsInYDirection;
    private int countOfPointsInZDirection;

    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public Box(MyStructureIfc myStructure, AlgoParameters algoParameters) {

        boundariesOfBox = BoxTools.computeBoundariesOfBox(myStructure, algoParameters);
        deltaX = boundariesOfBox.get("x")[1] - boundariesOfBox.get("x")[0];
        deltaY = boundariesOfBox.get("y")[1] - boundariesOfBox.get("y")[0];
        deltaZ = boundariesOfBox.get("z")[1] - boundariesOfBox.get("z")[0];

        minX = boundariesOfBox.get("x")[0];
        minY = boundariesOfBox.get("y")[0];
        minZ = boundariesOfBox.get("z")[0];

        countOfPointsInXDirection = (int) Math.round(deltaX / algoParameters.getCELL_DIMENSION_OF_THE_PROBABILITY_MAP_ANGSTROM());
        countOfPointsInYDirection = (int) Math.round(deltaY / algoParameters.getCELL_DIMENSION_OF_THE_PROBABILITY_MAP_ANGSTROM());
        countOfPointsInZDirection = (int) Math.round(deltaZ / algoParameters.getCELL_DIMENSION_OF_THE_PROBABILITY_MAP_ANGSTROM());
    }


    // -------------------------------------------------------------------
    // Getter and Setter
    // -------------------------------------------------------------------
    public Map<String, float[]> getBoundariesOfBox() {
        return boundariesOfBox;
    }

    public float getDeltaX() {
        return deltaX;
    }

    public float getDeltaY() {
        return deltaY;
    }

    public float getDeltaZ() {
        return deltaZ;
    }

    public float getMinX() {
        return minX;
    }

    public float getMinY() {
        return minY;
    }

    public float getMinZ() {
        return minZ;
    }

    public int getCountOfPointsInXDirection() {
        return countOfPointsInXDirection;
    }

    public int getCountOfPointsInYDirection() {
        return countOfPointsInYDirection;
    }

    public int getCountOfPointsInZDirection() {
        return countOfPointsInZDirection;
    }
}
