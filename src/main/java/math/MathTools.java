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


public class MathTools {
    // -------------------------------------------------------------------
    // Static Methods
    // -------------------------------------------------------------------
    public static double[] convertToDoubleArray(float[] inputArray) {

        int arrayLength = inputArray.length;
        double[] doubleArray = new double[arrayLength];
        for (int i = 0; i < arrayLength; i++) {
            doubleArray[i] = inputArray[i];
        }
        return doubleArray;
    }


    public static float[] convertToFloatArray(double[] inputArray) {

        int arrayLength = inputArray.length;
        float[] floatArray = new float[arrayLength];
        for (int i = 0; i < arrayLength; i++) {
            floatArray[i] = (float) inputArray[i];
        }
        return floatArray;
    }


    public static double[] addCoords(double[] v1, double[] v2) {

        double[] vectorSum = new double[3];
        for (int i = 0; i < 3; i++) {
            vectorSum[i] = v1[i] + v2[i];
        }
        return vectorSum;
    }


    public static float[] multiplyByScalar(float[] v1, float scalar) {

        float[] vectorSum = new float[3];
        for (int i = 0; i < 3; i++) {
            vectorSum[i] = v1[i] * scalar;
        }

        return vectorSum;
    }


    public static double[] multiplyByScalar(double[] v1, double scalar) {

        double[] vectorSum = new double[3];
        for (int i = 0; i < 3; i++) {
            vectorSum[i] = v1[i] * scalar;
        }

        return vectorSum;
    }


    public static double computeDistance(double[] v1, double[] v2) {
        double distanceToReturn = 0.0;
        if ((v1 != null) && (v2 != null)) {
            for (int i = 0; i < 3; i++) {
                distanceToReturn += (v1[i] - v2[i]) * (v1[i] - v2[i]);
            }
            distanceToReturn = Math.sqrt(distanceToReturn);
            return distanceToReturn;
        } else return -1.0;
    }


    public static float computeDistance(float[] v1, float[] v2) {
        float distanceToReturn = 0.0f;
        if ((v1 != null) && (v2 != null)) {
            for (int i = 0; i < 3; i++) {
                distanceToReturn += (v1[i] - v2[i]) * (v1[i] - v2[i]);
            }
            distanceToReturn = (float) Math.sqrt(distanceToReturn);
            return distanceToReturn;
        } else return -1.0f;
    }


    public static double computeEuclidianNorm(double[] v1) {
        double norm = Math.sqrt(v1[0] * v1[0] + v1[1] * v1[1] + v1[2] * v1[2]);
        return norm;
    }


    public static float computeDistance(float[] v1, List<float[]> listV2) {

        float minDistance = Float.MAX_VALUE;
        for (float[] v2 : listV2) {

            float distance = computeDistance(v1, v2);
            if (distance < -0.8) {
                continue;
            }
            if (distance < minDistance) {
                minDistance = distance;
            }
        }
        return minDistance;
    }


    public static double computeDistance(double[] v1, List<double[]> listV2) {

        double minDistance = Double.MAX_VALUE;
        for (double[] v2 : listV2) {

            double distance = computeDistance(v1, v2);
            if (distance < -0.8) {
                continue;
            }
            if (distance < minDistance) {
                minDistance = distance;
            }
        }
        return minDistance;
    }


    public static float computeTorsionAngle(float[] v1, float[] v2, float[] v3, float[] v4) {

        float torsionAngle = 0.0f;

        float[] v12 = vectorFromTwoPoints(v1, v2);
        float[] v32 = vectorFromTwoPoints(v3, v2);
        float[] v43 = vectorFromTwoPoints(v4, v3);

        float[] cross1 = normalizeVector(computeCrossVectorProduct(v32, v12));
        float[] cross2 = normalizeVector(computeCrossVectorProduct(v32, v43));

        float[] crossOfCross = computeCrossVectorProduct(cross1, cross2);
        float doubleWhichISOfTheSameSignAsTheTorsionAngle = computeScalarVectorProduct(crossOfCross, v32);

        torsionAngle = (float) Math.acos(correctTorsionAngle((computeScalarVectorProduct(cross2, cross1))));

        float factor = 1.0f;
        if (doubleWhichISOfTheSameSignAsTheTorsionAngle < 0.0) {
            factor = -1.0f;
        }

        return factor * covertAngleFromRadianToDegrees(torsionAngle);
    }


    public static float[] v1minusV2(float[] v1, float[] v2) {

        return vectorFromTwoPoints(v1, v2);
    }


    public static float[] v1plusV2(float[] v1, float[] v2) {
        float[] returnedVector = new float[3];
        for (int i = 0; i < 3; i++) {
            returnedVector[i] = v1[i] + v2[i];
        }
        return returnedVector;
    }


    public static float[] vectorFromTwoPoints(float[] v1, float[] v2) {

        float[] returnedVector = new float[3];
        for (int i = 0; i < 3; i++) {
            returnedVector[i] = v1[i] - v2[i];
        }
        return returnedVector;

    }


    public static float[] computeCrossVectorProduct(float[] v1, float[] v2) {

        float[] returnedVector = new float[3];

        returnedVector[0] = v1[1] * v2[2] - v1[2] * v2[1];
        returnedVector[1] = v1[2] * v2[0] - v1[0] * v2[2];
        returnedVector[2] = v1[0] * v2[1] - v1[1] * v2[0];

        return returnedVector;
    }


    public static double computeScalarVectorProduct(double[] v1, double[] v2) {

        return v1[0] * v2[0] + v1[1] * v2[1] + v1[2] * v2[2];
    }


    public static float computeScalarVectorProduct(float[] v1, float[] v2) {

        return v1[0] * v2[0] + v1[1] * v2[1] + v1[2] * v2[2];
    }


    public static double computeAngle(double[] v1, double[] v2) {

        return Math.acos(computeScalarVectorProduct(v1, v2));
    }


    public static float computeAngle(float[] v1, float[] v2) {

        return (float) Math.acos(computeScalarVectorProduct(v1, v2));
    }


    public static double[] normalizeVector(double[] v) {

        double[] returnedVector = new double[3];

        double norm = Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);

        for (int i = 0; i < 3; i++) {
            returnedVector[i] = v[i] / norm;
        }

        return returnedVector;
    }


    public static float[] normalizeVector(float[] v) {

        float[] returnedVector = new float[3];

        float norm = (float) Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);

        for (int i = 0; i < 3; i++) {
            returnedVector[i] = v[i] / norm;
        }

        return returnedVector;
    }


    public static float covertAngleFromRadianToDegrees(float angleInRadian) {
        return angleInRadian * 180.0f / (float) Math.PI;
    }


    public static int toIntegerSmaller(double value) {
        int valueToInt = (int) Math.round(value);
        if (valueToInt > value) {
            valueToInt -= 1;
        }
        return valueToInt;
    }


    public static float getAverageValueFromList(List<Integer> values) {
        float average = 0.0f;
        for (Integer value : values) {
            average += value;
        }
        average /= values.size();
        return average;
    }


    // -------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------
    private static float correctTorsionAngle(float torsionAngle) {
        float correctedTorsionAngle = torsionAngle;

        if ((torsionAngle > 1.0) && (torsionAngle < 1.0001)) {
            correctedTorsionAngle = 1.0f;
        }
        if ((torsionAngle < -1.0) && (torsionAngle > -1.0001)) {
            correctedTorsionAngle = -1.0f;
        }

        if ((torsionAngle > 1.0) || (torsionAngle < -1.0)) {
            System.out.println("Failed: cannot compute acos");
        }

        return correctedTorsionAngle;
    }
}