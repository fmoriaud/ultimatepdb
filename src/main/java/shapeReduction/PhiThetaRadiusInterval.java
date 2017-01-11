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

public class PhiThetaRadiusInterval implements Comparable<PhiThetaRadiusInterval> {
    // -------------------------------------------------------------------
    // Class variables
    // -------------------------------------------------------------------
    private double phiMin;
    private double phiMax;
    private double thetaMin;
    private double thetaMax;
    private double rMin;
    private double rMax;


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public PhiThetaRadiusInterval(double phiMin, double phiMax, double thetaMin, double thetaMax, double rMin, double rMax) {
        this.phiMin = phiMin;
        this.phiMax = phiMax;
        this.thetaMin = thetaMin;
        this.thetaMax = thetaMax;
        this.rMin = rMin;
        this.rMax = rMax;
    }


    // -------------------------------------------------------------------
    // Override method
    // -------------------------------------------------------------------
    @Override
    public int compareTo(PhiThetaRadiusInterval o) {
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
        if (this.rMin < o.rMin) {
            return 1;
        }
        if (this.rMin > o.rMin) {
            return -1;
        }
        return 0;

    }


    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");

        result.append("phi Min and Max = " + phiMin * 180 / Math.PI + "  " + phiMax * 180 / Math.PI +
                "theta Min and Max = " + thetaMin * 180 / Math.PI + "  " + thetaMax * 180 / Math.PI +
                "radius Min and Max = " + rMin + " " + rMax + NEW_LINE);

        return result.toString();
    }


    // -------------------------------------------------------------------
    // Getter and Setter
    // -------------------------------------------------------------------
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

    public double getrMin() {
        return rMin;
    }

    public double getrMax() {
        return rMax;
    }
}