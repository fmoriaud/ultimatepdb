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

import java.util.List;
import java.util.Map;

public class PairingAndNullSpaces {
    //-------------------------------------------------------------
    // Class variables
    //-------------------------------------------------------------
    private List<Integer> nullSpaceOfMap1;
    private List<Integer> nullSpaceOfMap2;

    private Map<Integer, Integer> pairing;


    //-------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------
    public PairingAndNullSpaces(Map<Integer, Integer> pairing, List<Integer> nullSpaceOfMap1, List<Integer> nullSpaceOfMap2) {

        this.nullSpaceOfMap1 = nullSpaceOfMap1;
        this.nullSpaceOfMap2 = nullSpaceOfMap2;
        this.pairing = pairing;

    }


    //-------------------------------------------------------------
    // Interface & Public methods
    //-------------------------------------------------------------
    @Override
    public String toString() {
        return String.format("pair count = " + pairing.size() + " null space map 1 count = " + nullSpaceOfMap1.size() + " null space map 2 count = " + nullSpaceOfMap2.size());
    }


    // -------------------------------------------------------------------
    // Getters and Setters
    // -------------------------------------------------------------------
    public List<Integer> getNullSpaceOfMap1() {
        return nullSpaceOfMap1;
    }

    public List<Integer> getNullSpaceOfMap2() {
        return nullSpaceOfMap2;
    }

    public Map<Integer, Integer> getPairing() {
        return pairing;
    }

}
