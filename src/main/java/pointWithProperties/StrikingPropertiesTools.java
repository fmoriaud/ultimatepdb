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
package pointWithProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class StrikingPropertiesTools {
    //-------------------------------------------------------------
    // Static methods
    //-------------------------------------------------------------
    public static List<StrikingProperties> computeStrinkingPropertiesOfAPointWithPropertiesIncludingNone(PointWithPropertiesIfc pointWithProperties) {

        List<StrikingProperties> listPropertiesFound = computeStrinkingPropertiesOfAPointWithProperties(pointWithProperties);

        if (listPropertiesFound.size() == 0) {
            listPropertiesFound.add(StrikingProperties.NONE);
        }
        return listPropertiesFound;
    }


    public static Map<Integer, PointWithPropertiesIfc> extractPointsHavingTheProperty(Map<Integer, PointWithPropertiesIfc> points, StrikingProperties strikingProperty) {

        Map<Integer, PointWithPropertiesIfc> selectedPoints = new HashMap<>();

        for (Entry<Integer, PointWithPropertiesIfc> entry : points.entrySet()) {
            PointWithPropertiesIfc point = entry.getValue();
            List<StrikingProperties> listPropertiesFound = point.getStrikingProperties();

            if (listPropertiesFound.contains(strikingProperty)) {
                selectedPoints.put(entry.getKey(), entry.getValue());
            }
        }
        return selectedPoints;
    }


    public static int evaluatePointsMatchingWithAllProperties(PointWithPropertiesIfc point1, PointWithPropertiesIfc point2) {

        List<StrikingProperties> propertyOfpoint1 = point1.getStrikingProperties();
        List<StrikingProperties> propertyOfpoint2 = point2.getStrikingProperties();

        if ((propertyOfpoint1.containsAll(propertyOfpoint2)) && (propertyOfpoint2.containsAll(propertyOfpoint1))) {
            return propertyOfpoint1.size();
        }
        return 0;
    }


    public static boolean evaluatePointsMatchingWithAtLeastOneProperty(PointWithPropertiesIfc point1, PointWithPropertiesIfc point2) {

        List<StrikingProperties> propertyOfpoint1 = point1.getStrikingProperties();
        List<StrikingProperties> propertyOfpoint2 = point2.getStrikingProperties();

        for (StrikingProperties property : propertyOfpoint1) {
            if (propertyOfpoint2.contains(property)) {
                return true;
            }
        }
        return false;
    }


    public static List<StrikingProperties> findCommonStrikingProperties(PointWithPropertiesIfc point1, PointWithPropertiesIfc point2) {

        List<StrikingProperties> propertyOfpoint1 = point1.getStrikingProperties();
        List<StrikingProperties> propertyOfpoint2 = point2.getStrikingProperties();

        List<StrikingProperties> commonStrikingProperties = new ArrayList<>();
        for (StrikingProperties property : propertyOfpoint1) {
            if (propertyOfpoint2.contains(property)) {
                commonStrikingProperties.add(property);
            }
        }
        return commonStrikingProperties;
    }


    // Both points has StrikingProperties not none and they all match (also if only one of course)
    public static int evaluatePointsMatchingAllNotNoneProperties(PointWithPropertiesIfc point1, PointWithPropertiesIfc point2) {

        List<StrikingProperties> propertyOfpoint1 = point1.getStrikingProperties();
        List<StrikingProperties> propertyOfpoint2 = point2.getStrikingProperties();

        if (propertyOfpoint1.size() == 0 || propertyOfpoint2.size() == 0) {
            System.out.println("Big pb in StrikingProperties Tools ... killing ");
            System.exit(0);
        }
        if (propertyOfpoint1.get(0).equals(StrikingProperties.NONE) || propertyOfpoint2.get(0).equals(StrikingProperties.NONE)) {
            return 0;
        }

        return evaluatePointsMatchingWithAllProperties(point1, point2);
    }


    // Both points has more than one striking properties and at least one of them match
    public static boolean evaluatePointsMatchingOneButNotAllNotNoneProperties(PointWithPropertiesIfc point1, PointWithPropertiesIfc point2) {

        List<StrikingProperties> propertyOfpoint1 = point1.getStrikingProperties();
        List<StrikingProperties> propertyOfpoint2 = point2.getStrikingProperties();

        if (propertyOfpoint1.size() == 0 || propertyOfpoint2.size() == 0) {
            System.out.println("Big pb in StrikingProperties Tools ... killing ");
            System.exit(0);
        }
        if (propertyOfpoint1.get(0).equals(StrikingProperties.NONE) || propertyOfpoint2.get(0).equals(StrikingProperties.NONE)) {
            return false;
        }

        if (evaluatePointsMatchingWithAllProperties(point1, point2) > 0) { // if all matching then this methods says no
            return false;
        }

        if (evaluatePointsMatchingWithAtLeastOneProperty(point1, point2)) {
            return true;
        }

        return false;
    }


    // Both points has None Striking Properties
    public static boolean evaluatePointsMatchingOnlyNoneProperties(PointWithPropertiesIfc point1, PointWithPropertiesIfc point2) {

        List<StrikingProperties> propertyOfpoint1 = point1.getStrikingProperties();
        List<StrikingProperties> propertyOfpoint2 = point2.getStrikingProperties();

        if (propertyOfpoint1.size() == 0 || propertyOfpoint2.size() == 0) {
            System.out.println("Big pb in StrikingProperties Tools ... killing ");
            System.exit(0);
        }

        if (propertyOfpoint1.size() != 1 || propertyOfpoint2.size() != 1) { // useless as NONE goes always alone
            return false;
        }

        if (propertyOfpoint1.get(0).equals(StrikingProperties.NONE) && propertyOfpoint2.get(0).equals(StrikingProperties.NONE)) {
            return true;
        }
        return false;
    }


    // One point has None striking property and the others has striking properties
    public static boolean evaluatePointsNoneMatchingANotNONEtoOnlyNoneProperties(PointWithPropertiesIfc point1, PointWithPropertiesIfc point2) {

        List<StrikingProperties> propertyOfpoint1 = point1.getStrikingProperties();
        List<StrikingProperties> propertyOfpoint2 = point2.getStrikingProperties();

        if (propertyOfpoint1.size() == 0 || propertyOfpoint2.size() == 0) {
            System.out.println("Big pb in StrikingProperties Tools ... killing ");
            System.exit(0);
        }

        boolean point1IsNone = false;
        if (propertyOfpoint1.size() == 1 && propertyOfpoint1.get(0).equals(StrikingProperties.NONE)) {
            point1IsNone = true;
        }
        boolean point2IsNone = false;
        if (propertyOfpoint2.size() == 1 && propertyOfpoint2.get(0).equals(StrikingProperties.NONE)) {
            point2IsNone = true;
        }

        if (point1IsNone == true && point2IsNone == false) {
            return true;
        }
        if (point1IsNone == false && point2IsNone == true) {
            return true;
        }
        return false;
    }


    // Both points has Striking properties but not a single one is matching
    public static boolean evaluatePointsNoneMatchingANotNONEtoNotNoneProperties(PointWithPropertiesIfc point1, PointWithPropertiesIfc point2) {

        List<StrikingProperties> propertyOfpoint1 = point1.getStrikingProperties();
        List<StrikingProperties> propertyOfpoint2 = point2.getStrikingProperties();

        if (propertyOfpoint1.size() == 0 || propertyOfpoint2.size() == 0) {
            System.out.println("Big pb in StrikingProperties Tools ... killing ");
            System.exit(0);
        }

        boolean point1IsNone = false;
        if (propertyOfpoint1.size() != 1 && propertyOfpoint1.get(0).equals(StrikingProperties.NONE)) {
            point1IsNone = true;
        }
        if (point1IsNone == true) {
            return false;
        }
        boolean point2IsNone = false;
        if (propertyOfpoint2.size() != 1 && propertyOfpoint2.get(0).equals(StrikingProperties.NONE)) {
            point2IsNone = true;
        }
        if (point2IsNone == true) {
            return false;
        }
        if (!evaluatePointsMatchingWithAtLeastOneProperty(point1, point2)) {
            return true;
        }
        return false;
    }


    public static List<StrikingProperties> computeStrikingPropertiesOfAShape(Map<Integer, PointWithPropertiesIfc> pointsWithPropertiesInThisGroup) {
        List<StrikingProperties> propertiesForThisGroup = new ArrayList<>();

        if (pointsWithPropertiesInThisGroup.size() == 0) {
            System.out.println("should not happen as there are no empty sectors of points");
            System.exit(0);
        }

        int countHydrophobes = 0;
        int countNegativeCharges = 0;
        int countPositiveCharges = 0;
        int countOfHbondAcceptor = 0;
        int countOfHbondDonnor = 0;
        int countOfAromaticRing = 0;
        int countOfDehydrons = 0;

        int countNoneOfStrikingProperty = 0;

        for (Entry<Integer, PointWithPropertiesIfc> entry2 : pointsWithPropertiesInThisGroup.entrySet()) {

            PointWithPropertiesIfc pointWithProperties = entry2.getValue();

            List<StrikingProperties> strikingPropertiesForThisPoint = pointWithProperties.getStrikingProperties();

            if (strikingPropertiesForThisPoint.contains(StrikingProperties.HYDROPHOBE)) {
                countHydrophobes += 1;
            }
            if (strikingPropertiesForThisPoint.contains(StrikingProperties.NEGATIVE_CHARGE)) {
                countNegativeCharges += 1;
            }
            if (strikingPropertiesForThisPoint.contains(StrikingProperties.POSITIVE_CHARGE)) {
                countPositiveCharges += 1;
            }
            if (strikingPropertiesForThisPoint.contains(StrikingProperties.HBOND_ACCEPTOR)) {
                countOfHbondAcceptor += 1;
            }
            if (strikingPropertiesForThisPoint.contains(StrikingProperties.HBOND_DONNOR)) {
                countOfHbondDonnor += 1;
            }
            if (strikingPropertiesForThisPoint.contains(StrikingProperties.AROMATICRING)) {
                countOfAromaticRing += 1;
            }
            if (strikingPropertiesForThisPoint.contains(StrikingProperties.DEHYDRON)) {
                countOfDehydrons += 1;
            }
            if (strikingPropertiesForThisPoint.contains(StrikingProperties.NONE)) {
                countNoneOfStrikingProperty += 1;
            }


        }

        if (countHydrophobes > 0) {
            propertiesForThisGroup.add(StrikingProperties.HYDROPHOBE);
        }
        if (countNegativeCharges > 0) {
            propertiesForThisGroup.add(StrikingProperties.NEGATIVE_CHARGE);
        }
        if (countPositiveCharges > 0) {
            propertiesForThisGroup.add(StrikingProperties.POSITIVE_CHARGE);
        }
        if (countOfHbondAcceptor > 0) {
            propertiesForThisGroup.add(StrikingProperties.HBOND_ACCEPTOR);
        }
        if (countOfHbondDonnor > 0) {
            propertiesForThisGroup.add(StrikingProperties.HBOND_DONNOR);
        }
        if (countOfAromaticRing > 0) {
            propertiesForThisGroup.add(StrikingProperties.AROMATICRING);
        }
        if (countOfDehydrons > 0) {
            propertiesForThisGroup.add(StrikingProperties.DEHYDRON);
        }
        if (countNoneOfStrikingProperty > 0) {
            propertiesForThisGroup.add(StrikingProperties.NONE);
        }
        return propertiesForThisGroup;
    }


    // -------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------
    private static List<StrikingProperties> computeStrinkingPropertiesOfAPointWithProperties(PointWithPropertiesIfc pointWithProperties) {

        List<StrikingProperties> strikingPropertiesForThisPoint = new ArrayList<>();

        Float charge = pointWithProperties.get(PropertyName.FormalCharge);
        Float hydrophobicity = pointWithProperties.get(PropertyName.Hydrophobicity);
        Float hbondAcceptor = pointWithProperties.get(PropertyName.HbondAcceptor);
        Float hbondDonnor = pointWithProperties.get(PropertyName.HbondDonnor);
        Float dehydron = pointWithProperties.get(PropertyName.Dehydron);
        Float aromaticring = pointWithProperties.get(PropertyName.AromaticRing);

        if (hydrophobicity != null && hydrophobicity > 0.99) {
            strikingPropertiesForThisPoint.add(StrikingProperties.HYDROPHOBE);
        }
        if (charge != null && charge < -0.99) {
            strikingPropertiesForThisPoint.add(StrikingProperties.NEGATIVE_CHARGE);
        }
        if (charge != null && charge > 0.99) {
            strikingPropertiesForThisPoint.add(StrikingProperties.POSITIVE_CHARGE);
        }
        if (hbondAcceptor != null && hbondAcceptor > 0.99) {
            strikingPropertiesForThisPoint.add(StrikingProperties.HBOND_ACCEPTOR);
        }
        if (hbondDonnor != null && hbondDonnor > 0.99) {
            strikingPropertiesForThisPoint.add(StrikingProperties.HBOND_DONNOR);
        }
        if (dehydron != null && dehydron > 0.99) {
            strikingPropertiesForThisPoint.add(StrikingProperties.DEHYDRON);
        }
        if (aromaticring != null && aromaticring > 0.99) {
            strikingPropertiesForThisPoint.add(StrikingProperties.AROMATICRING);
        }
        return strikingPropertiesForThisPoint;
    }
}
