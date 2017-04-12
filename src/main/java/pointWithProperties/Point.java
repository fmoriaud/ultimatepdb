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

import java.io.Serializable;

public class Point implements PointIfc, Serializable {

    float[] coords;

    // -------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------
    public Point(float[] coords) {
        this.coords = coords;
    }

    public Point(float x, float y, float z) {
        this.coords = new float[]{x, y, z};
    }


    // -------------------------------------------------------------------
    // Public Interface
    // -------------------------------------------------------------------
    @Override
    public float[] getCoords() {
        return coords;
    }

    @Override
    public void setCoords(float[] coords) {
        this.coords = coords;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");

        result.append(this.getClass().getName() + " Object {" + NEW_LINE);
        result.append(coords[0] + " " + coords[1] + " " + coords[2] + NEW_LINE);
        result.append("}");

        return result.toString();
    }
}
