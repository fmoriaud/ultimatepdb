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
package fingerprint;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import math.MathTools;
import pointWithProperties.PointIfc;


public class SimilarityTools {
    //-------------------------------------------------------------
    // Static methods
    //-------------------------------------------------------------
    public static List<Integer> binValues(List<Float> listDistances, int sizeList, float distanceStep, float startAt) {

        List<Integer> distributionDistance = new ArrayList<>(sizeList);
        for (int i = 0; i < sizeList; i++) {
            distributionDistance.add(0);
        }
        for (Float distance : listDistances) {

            int roundedDistance = Math.round((distance - startAt) / distanceStep);
            if (roundedDistance > sizeList - 1) {
                roundedDistance = sizeList - 1;
            }
            int valueWas = distributionDistance.get(roundedDistance);
            distributionDistance.set(roundedDistance, valueWas + 1);
        }
        return distributionDistance;
    }


    public static List<Float> computelListDistanceBetweenTwoLists(List<PointIfc> listPoint1, List<PointIfc> listPoint2, double minDistance) {

        List<Float> listDistances = new ArrayList<>();

        for (int i = 0; i < listPoint1.size(); i++) {
            for (int j = 0; j < listPoint2.size(); j++) {
                float distance = (float) MathTools.computeDistance(listPoint1.get(i).getCoords(), listPoint2.get(j).getCoords());
                if (distance > minDistance) {
                    listDistances.add(distance);
                }
            }
        }
        return listDistances;
    }


    public static float computeTanimotoSafe(BitSet bitset1, BitSet bitset2) {
        BitSet bitSet1copy1 = (BitSet) bitset1.clone();
        BitSet bitSet1copy2 = (BitSet) bitset1.clone();

        bitSet1copy1.and(bitset2);
        bitSet1copy2.or(bitset2);

        float tanimoto = bitSet1copy1.cardinality() / (float) bitSet1copy2.cardinality();
        return tanimoto;
    }


    public static float computeTsversySafe(BitSet bitset1, BitSet bitset2, float emphasisOn1, float emphasisOn2) {
        BitSet bitSet1copy1 = (BitSet) bitset1.clone();
        BitSet bitSet1copy2 = (BitSet) bitset1.clone();

        bitSet1copy1.and(bitset2);
        bitSet1copy2.or(bitset2);
        float intersectionCardinality = bitSet1copy1.cardinality();

        BitSet bitSet1copy3 = (BitSet) bitset1.clone();
        BitSet bitSet2copy1 = (BitSet) bitset2.clone();

        bitSet1copy3.andNot(bitset2);
        bitSet2copy1.andNot(bitset1);
        float complementOf1in2 = bitSet2copy1.cardinality(); // 2 without those from 1
        float complementOf2in1 = bitSet1copy3.cardinality(); // 1 without those from 2

        float tversky = intersectionCardinality / (intersectionCardinality + complementOf2in1 * emphasisOn1 + complementOf1in2 * emphasisOn2);

        return tversky;
    }


    public static String toString(BitSet bs) {
        String stringToReturn = "";
        if (bs.isEmpty()) {
            return "empty";
        } else {
            for (int i = 0; i < bs.length(); i++) {
                if (bs.get(i) == true) {
                    stringToReturn += "1";
                } else {
                    stringToReturn += "0";
                }

            }
            //return Long.toString(bs.toLongArray()[0], 2);
        }
        return stringToReturn;
    }
}