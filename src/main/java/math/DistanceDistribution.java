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

import parameters.AlgoParameters;
import pointWithProperties.PointIfc;

public class DistanceDistribution {
    // -------------------------------------------------------------------
    // Class variables
    // -------------------------------------------------------------------
    private List<? extends PointIfc> listOfPoints;
    private AlgoParameters algoParameters;

    private int[] distanceVector;


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public DistanceDistribution(List<? extends PointIfc> listOfPoints, AlgoParameters algoParameters) {
        this.listOfPoints = listOfPoints;
        this.algoParameters = algoParameters;

        distanceVector = new int[algoParameters.getFINGERPRINT_COUNT_OF_BINS()];
    }


    // -------------------------------------------------------------------
    // Public Interface
    // -------------------------------------------------------------------
    public void compute() {

        double binningDistance = algoParameters.getFINGERPRINT_SIZE_OF_BINS_ANGSTROM();

        for (int i = 0; i < listOfPoints.size(); i++) {
            for (int j = i; j < listOfPoints.size(); j++) {
                if (i != j) {
                    double distance = MathTools.computeDistance(listOfPoints.get(i).getCoords(), listOfPoints.get(j).getCoords());
                    int binnedDistance = (int) Math.round(distance / binningDistance);
                    System.out.println("dist " + distance + " bin to " + binnedDistance);
                    if (binnedDistance <= distanceVector.length - 1) {
                        distanceVector[binnedDistance] += 1;
                    } else {
                        distanceVector[distanceVector.length - 1] += 1;
                    }
                }
            }
        }
    }


    // -------------------------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------------------------
    public int[] getDistanceVector() {

        return distanceVector;
    }
}