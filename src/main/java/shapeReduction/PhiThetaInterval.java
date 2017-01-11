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

public class PhiThetaInterval implements Comparable<PhiThetaInterval> {
    // -------------------------------------------------------------------
    // Class variables
    // -------------------------------------------------------------------
    private double phiMin;
    private double phiMax;
    private double thetaMin;
    private double thetaMax;


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public PhiThetaInterval(double phiMin, double phiMax, double thetaMin, double thetaMax) {
        this.phiMin = phiMin;
        this.phiMax = phiMax;
        this.thetaMin = thetaMin;
        this.thetaMax = thetaMax;
    }


    // -------------------------------------------------------------------
    // Override method
    // -------------------------------------------------------------------
    @Override
    public int compareTo(PhiThetaInterval o) {
        if (this.phiMin < o.phiMin) {
            return 1;
        }
        if (this.phiMin > o.phiMin) {
            return -1;
        }
        if (this.thetaMin < o.thetaMin) {
            return 1;
        }
        if (this.thetaMin > o.thetaMin) {
            return -1;
        }
        return 0;

    }


    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(phiMin * 180 / Math.PI + "  " + phiMax * 180 / Math.PI + "  " + thetaMin * 180 / Math.PI + "  " + thetaMax * 180 / Math.PI);
        return result.toString();
    }

    public double getPhiMin() {
        return phiMin;
    }

    public double getPhiMax() {
        return phiMax;
    }

    public double getThetaMin() {
        return thetaMin;
    }

    public double getThetaMax() {
        return thetaMax;
    }
}
