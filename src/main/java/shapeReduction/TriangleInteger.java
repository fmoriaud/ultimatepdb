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

import java.util.Arrays;

public class TriangleInteger {
    // -------------------------------------------------------------------
    // Class variables
    // -------------------------------------------------------------------
    private int[] arrayTriangleIdAndPointId;
    private double[] arrayTriangleIdAndCorrespondingAngles;
    private double[] arrayTriangleIdAndOppositeEdgeLength;


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public TriangleInteger(int[] points) {
        arrayTriangleIdAndPointId = points;
    }


    // -------------------------------------------------------------------
    // Public Interface
    // -------------------------------------------------------------------
    @Override
    public boolean equals(Object other) {

        if (other == null) {
            return false;
        }

        if (other == this) {
            return true;
        }

        if (!(other instanceof TriangleInteger)) {
            return false;
        }

        int[] arrayCopy = this.arrayTriangleIdAndPointId.clone();
        Arrays.sort(arrayCopy);

        TriangleInteger otherTriangle = (TriangleInteger) other;
        int[] otherCopy = otherTriangle.arrayTriangleIdAndPointId.clone();
        Arrays.sort(otherCopy);

        if (Arrays.equals(arrayCopy, otherCopy)) {
            return true;
        }

        return false;
    }


    @Override
    public int hashCode() {

        int sum = this.arrayTriangleIdAndPointId[0] + this.arrayTriangleIdAndPointId[1] + this.arrayTriangleIdAndPointId[2];
        return sum;
    }


    @Override
    public String toString() {

        StringBuilder result = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");

        result.append("pointsIds : ");
        for (int i = 0; i < 3; i++) {
            result.append(arrayTriangleIdAndPointId[i] + " ");
        }
        result.append(NEW_LINE);
        result.append("opposite edge length : ");
        for (int i = 0; i < 3; i++) {
            //for (Entry<Integer, Double> entry: mapTriangleIdAndOppositeEdgeLength.entrySet()){
            result.append(arrayTriangleIdAndOppositeEdgeLength[i] + " ");
        }
        result.append(NEW_LINE);

        if (arrayTriangleIdAndCorrespondingAngles != null) {
            result.append("angles degrees : ");
            for (int i = 0; i < 3; i++) {
                //for (Entry<Integer, Double> entry: mapTriangleIdAndCorrespondingAngles.entrySet()){
                result.append(arrayTriangleIdAndCorrespondingAngles[i] * 180.0 / Math.PI + " ");
            }
            result.append(NEW_LINE);
        }


        return result.toString();
    }


    // -------------------------------------------------------------------
    // Getter and Setter
    // -------------------------------------------------------------------
    public int[] getArrayTriangleIdAndPointId() {
        return arrayTriangleIdAndPointId;
    }

    public double[] getArrayTriangleIdAndCorrespondingAngles() {
        return arrayTriangleIdAndCorrespondingAngles;
    }

    public void setArrayTriangleIdAndCorrespondingAngles(double[] arrayTriangleIdAndCorrespondingAngles) {

        this.arrayTriangleIdAndCorrespondingAngles = arrayTriangleIdAndCorrespondingAngles;
    }

    public double[] getArrayTriangleIdAndOppositeEdgeLength() {
        return arrayTriangleIdAndOppositeEdgeLength;
    }

    public void setArrayTriangleIdAndOppositeEdgeLength(double[] arrayTriangleIdAndOppositeEdgeLength) {
        this.arrayTriangleIdAndOppositeEdgeLength = arrayTriangleIdAndOppositeEdgeLength;
    }
}