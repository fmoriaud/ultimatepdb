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
package math;

import java.util.List;

import pointWithProperties.PointIfc;

public class ToolsDistance {
    //-------------------------------------------------------------
    // Static methods
    //-------------------------------------------------------------
    public static float computeSmallestDistanceBetweenAPointAndListOfPoints(float[] atPosition, List<PointIfc> listOfPointsWithLennardJonesQuery) {

        float minDistance = Float.MAX_VALUE;

        for (PointIfc pointsWithLennardJones : listOfPointsWithLennardJonesQuery) {

            float[] atomPosition = pointsWithLennardJones.getCoords();
            float distance = MathTools.computeDistance(atomPosition, atPosition);
            if (distance < minDistance) {
                minDistance = distance;
            }
        }
        return minDistance;
    }
}
