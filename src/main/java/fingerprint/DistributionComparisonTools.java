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

import java.util.BitSet;
import java.util.List;

import math.MathTools;

public class DistributionComparisonTools {
    //-------------------------------------------------------------
    // Static methods
    //-------------------------------------------------------------
    public static float computeDistance(List<Integer> distrib1, List<Integer> distrib2) throws CannotCompareDistributionException {

        if (distrib1.size() != distrib2.size() || distrib1.size() == 0) {
            throw new CannotCompareDistributionException("Error: Distribution to compare have differet length");
        }
        float diff = 0;
        for (int i = 0; i < distrib1.size(); i++) {
            int max = Math.max(distrib1.get(i), distrib2.get(i));
            float localDiff = Math.abs(distrib1.get(i) - distrib2.get(i));
            if (max > 0) {
                localDiff /= max;
            }
            diff += localDiff;
        }
        float average = diff / distrib1.size();
        return average;
    }


    public static float computeSubDistributionTanimoto(List<Integer> distrib1, List<Integer> distrib2) throws CannotCompareDistributionException {

        if (distrib1.size() != distrib2.size() || distrib1.size() == 0) {
            throw new CannotCompareDistributionException("Error: Distribution to compare have differet length");
        }
        BitSet bitset1 = new BitSet();
        BitSet bitset2 = new BitSet();

        for (int i = 0; i < distrib1.size(); i++) {

            int value1 = distrib1.get(i);
            int value2 = distrib2.get(i);

            treatPairValues(bitset1, bitset2, i, value1, value2);
        }

        //		System.out.println(bitset1);
        //		System.out.println(SimilarityTools.toString(bitset1));
        //		System.out.println(bitset2);
        //		System.out.println(SimilarityTools.toString(bitset2));

        float tanimoto = SimilarityTools.computeTanimotoSafe(bitset1, bitset2);
        //float tsersky = SimilarityTools.computeTsversySafe(bitset1, bitset2, 0.2f, 0.8f);
        //System.out.println("tanimoto = " + tanimoto);
        //System.out.println("tsersky = " + tsersky);

        return tanimoto;
    }


    public static float computeTanimotoOfTwoDistribution(List<Integer> distrib1, List<Integer> distrib2) throws CannotCompareDistributionException {


        if (distrib1.size() != distrib2.size() || distrib1.size() == 0) {
            throw new CannotCompareDistributionException("Error: Distribution to compare have differet length");
        }

        //		System.out.println(distrib1);
        //		System.out.println(distrib2);
        int bitsetsize = distrib1.size();

        // parameters to say if values similar
        float precentageToleranceOnEachBit = 0.33f;
        int absoluteTolerance = 1;
        // parameters to say if values are significant
        float percentageOfAverageToSaysignificant = 0.33f;

        float averageDistrib1 = MathTools.getAverageValueFromList(distrib1);
        float averageDistrib2 = MathTools.getAverageValueFromList(distrib2);
        float overallAverage = (averageDistrib1 + averageDistrib2) / 2.0f;

        BitSet bitset1 = new BitSet();
        BitSet bitset2 = new BitSet();

        for (int i = 0; i < bitsetsize; i++) {
            int bitValue1 = distrib1.get(i);
            int bitValue2 = distrib2.get(i);

            // shall I set  0 0 ; 1 0 ; 0 1 or 1 1 ??

            boolean areTheySimilar;
            int diff = Math.abs(bitValue1 - bitValue2);
            int max = Math.max(bitValue1, bitValue2);
            int toleranceToBeSimilar = Math.max(absoluteTolerance, (int) (max * precentageToleranceOnEachBit));

            if (diff > toleranceToBeSimilar) {
                areTheySimilar = false;
            } else {
                areTheySimilar = true;
            }

            boolean areTheySignificant;
            if (bitValue1 > percentageOfAverageToSaysignificant * overallAverage && bitValue2 > percentageOfAverageToSaysignificant * overallAverage) {
                areTheySignificant = true;
            } else {
                areTheySignificant = false;
            }

            if (areTheySimilar == true && areTheySignificant == true) {
                bitset1.set(i);
                bitset2.set(i);
            } else if (areTheySimilar == true && areTheySignificant == false) {
                bitset1.set(i, false);
                bitset2.set(i, false);
            } else if (areTheySimilar == false) {
                if (bitValue1 > bitValue2) {
                    bitset1.set(i);
                    bitset2.set(i, false);
                } else {
                    bitset1.set(i, false);
                    bitset2.set(i);
                }
            }
        }
        //		System.out.println(bitset1);
        //		System.out.println(SimilarityTools.toString(bitset1));
        //		System.out.println(bitset2);
        //		System.out.println(SimilarityTools.toString(bitset2));

        float tanimoto = SimilarityTools.computeTanimotoSafe(bitset1, bitset2);
        float tsersky = SimilarityTools.computeTsversySafe(bitset1, bitset2, 0.2f, 0.8f);
        //System.out.println("tanimoto = " + tanimoto);
        //System.out.println("tsersky = " + tsersky);
        return tanimoto;
    }


    // -------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------
    private static void treatPairValues(BitSet bitset1, BitSet bitset2, int i, int value1, int value2) {
        if (value1 == 0 && value2 == 0) {
            return;
        }

        if (value1 == 0 && value2 > 0) {
            bitset2.set(i);
            return;
        }

        if (value2 <= value1) {
            bitset1.set(i);
            bitset2.set(i);
            return;
        }

        // need to put something for the case
        if (value2 > value1) {
            bitset2.set(i);
            return;
        }
    }
}
