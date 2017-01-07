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

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import parameters.AlgoParameters;

public class ResultsFromEvaluateCost {
    // -------------------------------------------------------------------
    // Class variables
    // -------------------------------------------------------------------
    private double cost;
    private double distanceResidual;

    private RealMatrix rotationMatrix;
    private RealVector translationVector;
    private RealVector translationVectorToTranslateShape2ToOrigin;
    private PairingAndNullSpaces pairingAndNullSpaces;
    private float ratioPairedPointInQuery;
    private AlgoParameters algoParameters;


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public ResultsFromEvaluateCost(double cost, double distanceResidual, RealMatrix rotationMatrix, RealVector translationVector, RealVector translationVectorToTranslateShape2ToOrigin,
                                   PairingAndNullSpaces pairingAndNullSpaces, float ratioPairedPointInQuery, AlgoParameters algoParameters) {
        this.cost = cost;
        this.distanceResidual = distanceResidual;
        this.rotationMatrix = rotationMatrix;
        this.translationVector = translationVector;
        this.translationVectorToTranslateShape2ToOrigin = translationVectorToTranslateShape2ToOrigin;
        this.pairingAndNullSpaces = pairingAndNullSpaces;
        this.ratioPairedPointInQuery = ratioPairedPointInQuery;
        this.algoParameters = algoParameters;
    }


    // -------------------------------------------------------------------
    // Override methods
    // -------------------------------------------------------------------
    @Override
    public int hashCode() {

        float unit = 3 * algoParameters.getCELL_DIMENSION_OF_THE_PROBABILITY_MAP_ANGSTROM();


        RealVector trans = this.getTranslationVector();
        double x = trans.getEntry(0);
        double y = trans.getEntry(1);
        double z = trans.getEntry(2);
        int xInt = (int) Math.round(x / unit);
        int yInt = (int) Math.round(y / unit);
        int zInt = (int) Math.round(z / unit);


        RealMatrix rotMat = this.getRotationMatrix();
        Rotation rotation = new Rotation(rotMat.getData(), 0.01);

        int unitAngleDegrees = 8;
        double angle = rotation.getAngle();
        int angleInt = (int) Math.round((angle * 180 / Math.PI) / unitAngleDegrees);

        int hashcode = xInt;
        hashcode = hashcode * 71 + yInt;
        hashcode = hashcode * 71 + zInt;
        hashcode = hashcode * 71 + angleInt;
        return hashcode;
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ResultsFromEvaluateCost)) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (this.hashCode() == ((ResultsFromEvaluateCost) obj).hashCode()) {
            return true;
        }

        return false;
    }


    // -------------------------------------------------------------------
    // Getter and Setter
    // -------------------------------------------------------------------
    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getDistanceResidual() {
        return distanceResidual;
    }

    public RealMatrix getRotationMatrix() {
        return rotationMatrix;
    }

    public RealVector getTranslationVector() {
        return translationVector;
    }

    public RealVector getTranslationVectorToTranslateShape2ToOrigin() {
        return translationVectorToTranslateShape2ToOrigin;
    }

    public PairingAndNullSpaces getPairingAndNullSpaces() {
        return pairingAndNullSpaces;
    }

    public float getRatioPairedPointInQuery() {
        return ratioPairedPointInQuery;
    }


}
