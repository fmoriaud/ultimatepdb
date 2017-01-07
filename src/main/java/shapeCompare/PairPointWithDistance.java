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
package shapeCompare;

import java.util.Comparator;

import shapeReduction.PairInteger;

public class PairPointWithDistance {
    // -------------------------------------------------------------------
    // Class variables
    // -------------------------------------------------------------------
    private PairInteger pairInteger;
    private double distance;


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public PairPointWithDistance(int point1, int point2, double distance) {

        this.pairInteger = new PairInteger(point1, point2);
        this.distance = distance;
    }


    // -------------------------------------------------------------------
    // Public && Interface method
    // -------------------------------------------------------------------
    @Override
    public String toString() {

        StringBuilder result = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");

        result.append("pointsIds : ");
        result.append(pairInteger.toString());
        result.append("  distance : ");
        result.append(distance);

        result.append(NEW_LINE);

        return result.toString();
    }


    public static class LowestDistancePairPointWithDistance implements Comparator<PairPointWithDistance> {

        @Override
        public int compare(PairPointWithDistance pair1, PairPointWithDistance pair2) {

            if (pair1.getDistance() > pair2.getDistance()) {
                return 1;
            }
            if (pair1.getDistance() < pair2.getDistance()) {
                return -1;
            }
            return 0;
        }
    }


    // -------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------
    public PairInteger getPairInteger() {
        return pairInteger;
    }

    public double getDistance() {
        return distance;
    }
}
