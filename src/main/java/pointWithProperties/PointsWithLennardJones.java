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

public class PointsWithLennardJones implements PointIfc {
    //-------------------------------------------------------------
    // Class variables
    //-------------------------------------------------------------
    private float[] coords;
    private float lennardJones;


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public PointsWithLennardJones(float[] coords, float lennardJones) {
        this.coords = coords;
        this.lennardJones = lennardJones;
    }


    // -------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------
    public float[] getCoords() {
        return coords;
    }


    public void setCoords(float[] coords) {
        this.coords = coords;
    }


    public double getLennardJones() {
        return lennardJones;
    }


    public void setLennardJones(float lennardJones) {
        this.lennardJones = lennardJones;
    }
}
