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

public class TriangleEdge {
    // -------------------------------------------------------------------
    // Class variables
    // -------------------------------------------------------------------
    private int point1;
    private int point2;
    private double edgeLength;


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public TriangleEdge(int point1, int point2, double edgeLength) {
        this.point1 = point1;
        this.point2 = point2;
        this.edgeLength = edgeLength;
    }


    // -------------------------------------------------------------------
    // Getter and Setter
    // -------------------------------------------------------------------
    public int getPoint1() {
        return point1;
    }

    public int getPoint2() {
        return point2;
    }

    public double getEdgeLength() {
        return edgeLength;
    }

}
