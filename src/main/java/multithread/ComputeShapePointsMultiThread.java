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
package multithread;

import math.MathTools;
import mystructure.*;
import parameters.AlgoParameters;
import pointWithProperties.*;
import pointWithProperties.PropertyName;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;


public class ComputeShapePointsMultiThread extends RecursiveTask<List<PointWithPropertiesIfc>> {
    //-------------------------------------------------------------
    // Class members
    //-------------------------------------------------------------
    private static final long serialVersionUID = 1L;

    private final List<float[]> listPositionWhereToComputeProperties;
    //private List<Float> listMinDistanceOfThisGridPointToAnyAtomOfPeptide;

    private int start;
    private int end;
    private int threshold;

    private final MyStructureIfc myStructureShape;
    private final List<PointIfc> listOfLigandPoints;
    private final AlgoParameters algoParameters;
    private final List<HBondDefinedWithAtoms> dehydrons;

    private final ComputePropertiesProtein computeProperties;


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public ComputeShapePointsMultiThread(List<float[]> listPositionWhereToComputeProperties,
                                         //List<Float> listMinDistanceOfThisGridPointToAnyAtomOfPeptide,
                                         int start, int end, int threshold,
                                         MyStructureIfc myStructureShape,
                                         List<PointIfc> listOfLigandPoints,
                                         AlgoParameters algoParameters,
                                         List<HBondDefinedWithAtoms> dehydrons) {

        //this.computeProperties = computeProperties;
        this.listPositionWhereToComputeProperties = listPositionWhereToComputeProperties;
        //this.listMinDistanceOfThisGridPointToAnyAtomOfPeptide = listMinDistanceOfThisGridPointToAnyAtomOfPeptide;

        this.start = start;
        this.end = end;
        this.threshold = threshold;

        this.myStructureShape = myStructureShape;
        this.listOfLigandPoints = listOfLigandPoints;
        this.algoParameters = algoParameters;
        this.dehydrons = dehydrons;

        this.computeProperties = new ComputePropertiesProtein(myStructureShape, algoParameters, dehydrons, listOfLigandPoints);

    }


    // -------------------------------------------------------------------
    // Public & Interface
    // -------------------------------------------------------------------
    @Override
    protected List<PointWithPropertiesIfc> compute() {

        List<PointWithPropertiesIfc> listPoints = new ArrayList<>();
        if (end - start < threshold) {

            for (int i = start; i <= end; i++) {

                float[] atPosition = listPositionWhereToComputeProperties.get(i);
                PointWithPropertiesIfc newPointCreated = computeNewShapePoint(myStructureShape, listOfLigandPoints, algoParameters, atPosition, dehydrons);
                if (newPointCreated != null) {
                    listPoints.add(newPointCreated);
                }
            }

        } else {

            int midway = (end - start) / 2 + start;
            ComputeShapePointsMultiThread computePointTask1 = new ComputeShapePointsMultiThread(listPositionWhereToComputeProperties,
                    start, midway, threshold, myStructureShape, listOfLigandPoints, algoParameters, dehydrons);
            computePointTask1.fork();

            ComputeShapePointsMultiThread extendTask2 = new ComputeShapePointsMultiThread(listPositionWhereToComputeProperties,
                    midway + 1, end, threshold, myStructureShape, listOfLigandPoints, algoParameters, dehydrons);
            listPoints.addAll(extendTask2.compute());
            listPoints.addAll(computePointTask1.join());

        }

        return listPoints;
    }


    // -------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------
    private PointWithPropertiesIfc computeNewShapePoint(MyStructureIfc myStructureShape, List<PointIfc> listOfLigandPoints, AlgoParameters algoParameters,
                                                        float[] atPosition, List<HBondDefinedWithAtoms> dehydrons) {

        float probability = computeProbability(myStructureShape, atPosition, algoParameters);
        PointWithPropertiesIfc pointWithProperties = null;
        if ((!(algoParameters.isUSE_CUTOFF_PROBABILITY_IN_SHAPES())) ||
                ((algoParameters.isUSE_CUTOFF_PROBABILITY_IN_SHAPES()) && (probability > algoParameters.getCUTOFF_MIN_PROBABILITY_IN_SHAPES()))) {

            pointWithProperties = new PointWithProperties();
            pointWithProperties.setCoords(new Point(atPosition));

            float minDistanceOfThisGridPointToAnyAtomOfPeptide = computeSmallestDistanceBetweenAPointAndListOfPoints(atPosition, listOfLigandPoints);
            pointWithProperties.setDistanceToLigand(minDistanceOfThisGridPointToAnyAtomOfPeptide);

            boolean pointClosestEnoughToAnAtom = computeProperties.compute(atPosition);

            if (pointClosestEnoughToAnAtom == false) {
                return null;
            }

            pointWithProperties.setElectronProbability(probability);

            Float charge = computeProperties.getCharge();
            if (charge != null) {
                pointWithProperties.put(PropertyName.FormalCharge, charge);
            }

            Float hBondDonnor = computeProperties.gethDonnor();
            if (hBondDonnor != null) {
                pointWithProperties.put(PropertyName.HbondDonnor, hBondDonnor);
            }

            Float hBondAcceptor = computeProperties.gethAcceptor();
            if (hBondAcceptor != null) {
                pointWithProperties.put(PropertyName.HbondAcceptor, hBondAcceptor);
            }

            Float dehydron = computeProperties.getDehydron();
            if (dehydron != null) {
                pointWithProperties.put(PropertyName.Dehydron, dehydron);
            }

            Float hydrophobicity = computeProperties.getHydrophobicity();
            if (hydrophobicity != null) {
                pointWithProperties.put(PropertyName.Hydrophobicity, hydrophobicity);
            }

            Float aromaticring = computeProperties.getAromaticRing();
            if (aromaticring != null) {
                pointWithProperties.put(PropertyName.AromaticRing, aromaticring);
            }

            // compute striking properties of this point
            List<StrikingProperties> strikingProperties = StrikingPropertiesTools.computeStrinkingPropertiesOfAPointWithPropertiesIncludingNone(pointWithProperties);
            pointWithProperties.setStrikingProperties(strikingProperties);

            return pointWithProperties;
        }
        return null;
    }


    private float computeSmallestDistanceBetweenAPointAndListOfPoints(float[] atPosition, List<PointIfc> listOfPointsWithLennardJonesQuery) {

        float minDistance = Float.MAX_VALUE;
        for (PointIfc pointsWithLennardJones : listOfPointsWithLennardJonesQuery) {

            float[] atomPosition = pointsWithLennardJones.getCoords();
            float distance = MathTools.computeDistance(atomPosition, atPosition);
            if (distance < minDistance) {
                minDistance = distance;
            }
        }
        return minDistance;
    }


    private float computeProbability(MyStructureIfc structure, float[] atPosition, AlgoParameters algoParameters) {
        float probabilityToReturn = 0.0f;

        for (MyChainIfc chain : structure.getAllChainsRelevantForShapeBuilding()) {
            for (MyMonomerIfc monomer : chain.getMyMonomers()) {
                for (MyAtomIfc atom : monomer.getMyAtoms()) {
                    probabilityToReturn += computeProbability(atom, atPosition);
                }
            }
        }
        return probabilityToReturn;
    }


    private float computeProbability(MyAtomIfc atom, float[] atPosition) {

        float r = MathTools.computeDistance(atom.getCoords(), atPosition);
        float fwhm = AtomProperties.findFwhmForMyAtom(atom);
        float sigma = fwhm / 2.3548f;

        float a = (float) (1.0f / (sigma * Math.sqrt(2.0f * Math.PI)));
        //System.out.println("a = " + a);
        // TODO OPTIMIZATION Maybe the Gaussian should have an integral which is different for each atom
        // But that may have consequences on the maximum cutoff for probability
        float valueToReturn = (float) (a * ((Math.exp(-1.0f * r * r / (2.0f * sigma * sigma)))));

        //		if ( r < 0.001 ) {
        //			System.out.println(" valueToReturn = " + valueToReturn);
        //		}

        return valueToReturn;
    }
}
