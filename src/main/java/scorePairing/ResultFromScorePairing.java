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
package scorePairing;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class ResultFromScorePairing {

    //-------------------------------------------------------------
    // Class variables
    //-------------------------------------------------------------
    private double cost;

    private RealMatrix rotationMatrixToRotateShape2ToShape1;
    private RealVector translationVectorToTranslateShape2ToShape1;
    private RealVector translationVectorToTranslateShape2ToOrigin;
    private double distanceResidual;


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public ResultFromScorePairing(double cost, RealMatrix rotationMatrixToRotateShape2ToShape1, RealVector translationVectorToTranslateShape2ToShape1, RealVector translationVectorToTranslateShape2ToOrigin, double distanceResidual) {

        this.cost = cost;
        this.rotationMatrixToRotateShape2ToShape1 = rotationMatrixToRotateShape2ToShape1;
        this.translationVectorToTranslateShape2ToShape1 = translationVectorToTranslateShape2ToShape1;
        this.translationVectorToTranslateShape2ToOrigin = translationVectorToTranslateShape2ToOrigin;
        this.distanceResidual = distanceResidual;

    }


    // -------------------------------------------------------------------
    // Getter and Setter
    // -------------------------------------------------------------------
    public double getCost() {
        return cost;
    }

    public RealMatrix getRotationMatrixToRotateShape2ToShape1() {
        return rotationMatrixToRotateShape2ToShape1;
    }

    public RealVector getTranslationVectorToTranslateShape2ToShape1() {
        return translationVectorToTranslateShape2ToShape1;
    }

    public RealVector getTranslationVectorToTranslateShape2ToOrigin() {
        return translationVectorToTranslateShape2ToOrigin;
    }

    public double getDistanceResidual() {
        return distanceResidual;
    }
}
