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
package shapeReduction;

import java.util.List;

import pointWithProperties.PointWithPropertiesIfc;


public class PairOfListPointsWithDistance {
    // -------------------------------------------------------------------
    // Class variables
    // -------------------------------------------------------------------
    private List<PointWithPropertiesIfc> listPoint1;
    private List<PointWithPropertiesIfc> listPoint2;
    private float distance;


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public PairOfListPointsWithDistance(List<PointWithPropertiesIfc> listPoint1, List<PointWithPropertiesIfc> listPoint2, float distance) {

        this.listPoint1 = listPoint1;
        this.listPoint2 = listPoint2;
        this.distance = distance;
    }


    // -------------------------------------------------------------------
    // Getter and Setter
    // -------------------------------------------------------------------
    public List<PointWithPropertiesIfc> getListPoint1() {
        return listPoint1;
    }


    public List<PointWithPropertiesIfc> getListPoint2() {
        return listPoint2;
    }


    public float getDistance() {
        return distance;
    }
}
