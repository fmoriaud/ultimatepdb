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

import hits.Hit;
import hits.HitPeptideWithQueryPeptide;
import hits.HitTools;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import parameters.AlgoParameters;
import pointWithProperties.PointWithProperties;
import shape.ShapeContainerIfc;
import shape.ShapeContainerWithPeptide;

import java.util.*;
import java.util.Map.Entry;


public class PairingTools {
    //-------------------------------------------------------------
    // Static methods
    //-------------------------------------------------------------
    public static PairingAndNullSpaces deepCopyNewPairingAndNewNullSpacesAndExtendIfNeeded(PairingAndNullSpaces inputPairingAndNewNullSpaces, Map<Integer, PointWithProperties> shape1, Map<Integer, PointWithProperties> shape2) {

        PairingAndNullSpaces newPairingAndNewNullSpaces = PairingTools.deepCopyNewPairingAndNewNullSpaces(inputPairingAndNewNullSpaces);

        dealWithShape1(shape1, newPairingAndNewNullSpaces);
        dealWithShape2(shape2, newPairingAndNewNullSpaces);

        return newPairingAndNewNullSpaces;
    }


    public static List<Hit> generateHitsListFromResultList(List<ResultsFromEvaluateCost> resultList, ShapeContainerIfc targetShape, ShapeContainerIfc queryShape, AlgoParameters algoParameters) {

        List<Hit> hitsList = new ArrayList<>();
        for (ResultsFromEvaluateCost result : resultList) {

            if (queryShape instanceof ShapeContainerWithPeptide && targetShape instanceof ShapeContainerWithPeptide) {
                Float rmsdBackboneWhencomparingPeptideToPeptide = HitTools.computeRmsdBackboneAtomBetweenHitPeptideAndQueryLigandDefinigQuery(targetShape, result, queryShape, algoParameters);

                Hit hit = new HitPeptideWithQueryPeptide(targetShape, result, rmsdBackboneWhencomparingPeptideToPeptide);
                hitsList.add(hit);
            } else {
                Hit hit = new Hit(targetShape, result);
                hitsList.add(hit);
            }


        }
        return hitsList;
    }


    public static PairingAndNullSpaces deepCopyNewPairingAndNewNullSpaces(PairingAndNullSpaces newPairingAndNewNullSpacesToCopy) {

        Map<Integer, Integer> deepCopyPairing = new TreeMap<Integer, Integer>();
        List<Integer> deepCopyNullSpaceMap1 = new ArrayList<Integer>();
        List<Integer> deepCopyNullSpaceMap2 = new ArrayList<Integer>();

        for (Entry<Integer, Integer> entry : newPairingAndNewNullSpacesToCopy.getPairing().entrySet()) {
            deepCopyPairing.put(Integer.valueOf(entry.getKey().intValue()), Integer.valueOf(entry.getValue().intValue()));
        }


        for (Integer point : newPairingAndNewNullSpacesToCopy.getNullSpaceOfMap1()) {
            deepCopyNullSpaceMap1.add(Integer.valueOf(point.intValue()));
        }
        for (Integer point : newPairingAndNewNullSpacesToCopy.getNullSpaceOfMap2()) {
            deepCopyNullSpaceMap2.add(Integer.valueOf(point.intValue()));
        }

        if (newPairingAndNewNullSpacesToCopy.getPairing().size() != deepCopyPairing.size()) {
            System.out.println("Critical error in deepCopyNewPairingAndNewNullSpaces: size of pairing differs");
            System.exit(0);
        }
        if (newPairingAndNewNullSpacesToCopy.getNullSpaceOfMap1().size() != deepCopyNullSpaceMap1.size()) {
            System.out.println("Critical error in deepCopyNewPairingAndNewNullSpaces: size of NullSpaceMap1 differs");
            System.exit(0);
        }
        if (newPairingAndNewNullSpacesToCopy.getNullSpaceOfMap2().size() != deepCopyNullSpaceMap2.size()) {
            System.out.println("Critical error in deepCopyNewPairingAndNewNullSpaces: size of NullSpaceMap2 differs");
            System.exit(0);
        }

        PairingAndNullSpaces newPairingAndNewNullSpaces = new PairingAndNullSpaces(deepCopyPairing, deepCopyNullSpaceMap1, deepCopyNullSpaceMap2);

        return newPairingAndNewNullSpaces;
    }


    public static boolean validate(PairingAndNullSpaces newPairingAndNewNullSpacesToTest) {

        // I check if unique values
        double duplicateValue = 0;
        boolean duplicateValueFound = false;
        for (Entry<Integer, Integer> entry : newPairingAndNewNullSpacesToTest.getPairing().entrySet()) {

            int valueToTest = entry.getValue().intValue();
            int countOfFoundTimes = 0;
            for (Entry<Integer, Integer> entry2 : newPairingAndNewNullSpacesToTest.getPairing().entrySet()) {
                if (entry2.getValue().intValue() == valueToTest) {
                    duplicateValue = valueToTest;
                    countOfFoundTimes += 1;
                }
            }
            if (countOfFoundTimes > 1) {
                duplicateValueFound = true;
                System.out.println("duplicateValue = " + duplicateValue);

            }
        }

        double duplicateKey = 0;
        boolean duplicateKeyFound = false;
        for (Entry<Integer, Integer> entry : newPairingAndNewNullSpacesToTest.getPairing().entrySet()) {

            int valueToTest = entry.getKey().intValue();
            int countOfFoundTimes = 0;
            for (Entry<Integer, Integer> entry2 : newPairingAndNewNullSpacesToTest.getPairing().entrySet()) {
                if (entry2.getKey().intValue() == valueToTest) {
                    duplicateKey = valueToTest;
                    countOfFoundTimes += 1;
                }
            }
            if (countOfFoundTimes > 1) {
                duplicateKeyFound = true;
                System.out.println("duplicateKey = " + duplicateKey);

            }
        }

        if (duplicateValueFound || duplicateKeyFound) {
            System.out.println("Problem found : validation duplicateValueFound not OK");
            System.out.println(newPairingAndNewNullSpacesToTest.getPairing());
            //System.exit(0);
            return false;
        }

        // check if there is a point in nullspace and also in pairing
        for (Entry<Integer, Integer> entry : newPairingAndNewNullSpacesToTest.getPairing().entrySet()) {

            int valueToTest = entry.getKey().intValue();
            for (Integer keyInNullSpace : newPairingAndNewNullSpacesToTest.getNullSpaceOfMap1()) {
                if (valueToTest == keyInNullSpace.intValue()) {
                    System.out.println("Problem found : point in nullMap1 and in Pairing " + valueToTest);
                    //System.exit(0);
                    return false;
                }
            }

            valueToTest = entry.getValue().intValue();
            for (Integer keyInNullSpace : newPairingAndNewNullSpacesToTest.getNullSpaceOfMap2()) {
                if (valueToTest == keyInNullSpace.intValue()) {
                    System.out.println("Problem found : point in nullMap2 and in Pairing " + valueToTest);
                    //System.exit(0);
                    return false;
                }
            }

        }
        return true;
    }


    public static class LowestCostHitComparator implements Comparator<Hit> {

        @Override
        public int compare(Hit hit1, Hit hit2) {

            if (hit1.getResultsFromEvaluateCost().getCost() > hit2.getResultsFromEvaluateCost().getCost()) {
                return 1;
            }
            if (hit1.getResultsFromEvaluateCost().getCost() < hit2.getResultsFromEvaluateCost().getCost()) {
                return -1;
            }
            return 0;
        }
    }


    public static RealVector alignPointFromShape2toShape1(ResultsFromEvaluateCost result, RealVector pointFromShape2) {

        RealVector transformedPoint = alignPointFromShape2toShape1(result.getTranslationVectorToTranslateShape2ToOrigin(), result.getTranslationVector(), result.getRotationMatrix(), pointFromShape2);
        return transformedPoint;
    }


    public static RealVector alignPointFromShape2toShape1(RealVector trans2toOrigin, RealVector trans, RealMatrix rot, RealVector point) {

        RealVector translatedToOriginPoint = point.add(trans2toOrigin.copy());
        RealVector rotatedPoint = rot.operate(translatedToOriginPoint);
        RealVector translatedBackPoint = rotatedPoint.subtract(trans2toOrigin);

        RealVector translatedToShape1 = translatedBackPoint.add(trans);

        return translatedToShape1;
    }


    // -------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------
    private static void dealWithShape1(Map<Integer, PointWithProperties> shape, PairingAndNullSpaces newPairingAndNewNullSpaces) {

        for (Integer pointShape : shape.keySet()) {
            boolean inPairing = newPairingAndNewNullSpaces.getPairing().containsKey(pointShape);
            boolean inNullSpace = newPairingAndNewNullSpaces.getNullSpaceOfMap1().contains(pointShape);
            if ((!inPairing) && (!inNullSpace)) {
                newPairingAndNewNullSpaces.getNullSpaceOfMap1().add(pointShape);
            }
        }
    }


    private static void dealWithShape2(Map<Integer, PointWithProperties> shape, PairingAndNullSpaces newPairingAndNewNullSpaces) {

        for (Integer pointShape : shape.keySet()) {
            boolean inPairing = newPairingAndNewNullSpaces.getPairing().containsValue(pointShape);
            boolean inNullSpace = newPairingAndNewNullSpaces.getNullSpaceOfMap2().contains(pointShape);
            if ((!inPairing) && (!inNullSpace)) {
                newPairingAndNewNullSpaces.getNullSpaceOfMap2().add(pointShape);
            }
        }
    }
}
