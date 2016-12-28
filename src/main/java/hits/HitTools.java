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
package hits;

import math.ToolsMath;
import mystructure.*;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import parameters.AlgoParameters;
import pointWithProperties.*;
import shape.HasPeptideIfc;
import shape.ShapeContainerIfc;
import shape.ShapeContainerWithLigand;
import shape.ShapeContainerWithPeptide;
import shapeCompare.NullResultFromAComparisonException;
import shapeCompare.PairingTools;
import shapeCompare.ResultsFromEvaluateCost;
import ultiJmol1462.MyJmolTools;
import ultiJmol1462.Protonate;
import ultiJmol1462.ResultsUltiJMolMinimizedHitLigandOnTarget;

import java.util.*;
import java.util.Map.Entry;

public class HitTools {
    //-------------------------------------------------------------
    // Static methods
    //-------------------------------------------------------------
    public static void minimizeHitInQuery(Hit hit, ShapeContainerIfc queryShape, ShapeContainerIfc targetShape, AlgoParameters algoParameters) throws NullResultFromAComparisonException, ExceptionInScoringUsingBioJavaJMolGUI {

        double cost = hit.getResultsFromEvaluateCost().getCost();
        System.out.println("distance residual = " + hit.getResultsFromEvaluateCost().getDistanceResidual());
        System.out.println("currentBestHit.getResultsFromEvaluateCost().getCost() = " + cost);

        if (targetShape instanceof ShapeContainerWithLigand || targetShape instanceof ShapeContainerWithPeptide) {
            // scoring with Jmol forcefield

            ResultsUltiJMolMinimizedHitLigandOnTarget resultsUltiJMolMinimizedHitLigandOnTarget = null;
            if (algoParameters.isOPTIMIZE_HIT_GEOMETRY() == true) {

                MyChainIfc peptideOrLigand = null;

                if (hit.getShapeContainer() instanceof HasPeptideIfc) {
                    HasPeptideIfc queryShapeWithPeptide = (HasPeptideIfc) hit.getShapeContainer();
                    peptideOrLigand = queryShapeWithPeptide.getPeptide();
                }
                if (hit.getShapeContainer() instanceof ShapeContainerWithLigand) {
                    ShapeContainerWithLigand shapeContainerWithLigand = (ShapeContainerWithLigand) hit.getShapeContainer();
                    peptideOrLigand = new MyChain(shapeContainerWithLigand.getHetatmLigand(), shapeContainerWithLigand.getHetatmLigandChainId()); // returnCloneRotatedPeptide(hetAtomInChain, hit.getResultsFromEvaluateCost());
                }

                Cloner cloner2 = new Cloner(peptideOrLigand, algoParameters);
                MyStructureIfc myStructurePeptide = cloner2.getClone();

                // MyStructureIfc myStructurePeptide = new MyStructure(peptideOrLigand, algoParameters);


                // TODO is it already protonated?, should be now ...
                Protonate protonate = new Protonate(myStructurePeptide, algoParameters);
                protonate.compute();

                MyStructureIfc preparedPeptide = protonate.getProtonatedMyStructure();

                Cloner cloner = new Cloner(preparedPeptide, algoParameters);
                MyStructureIfc clonedRotatedPeptide = cloner.getRotatedClone(hit.getResultsFromEvaluateCost());

                MyStructureIfc structureQueryComputeshape = queryShape.getMyStructureUsedToComputeShape();

                Protonate protonate2 = new Protonate(structureQueryComputeshape, algoParameters);
                protonate2.compute();

                MyStructureIfc preparedQuery = protonate2.getProtonatedMyStructure();
                resultsUltiJMolMinimizedHitLigandOnTarget = MyJmolTools.scoreByMinimizingLigandOnFixedReceptor(algoParameters, clonedRotatedPeptide, preparedQuery);

                // handle coverage of query into hit


                if (resultsUltiJMolMinimizedHitLigandOnTarget != null) {
                    System.out.println("InteractionEFinal = " + resultsUltiJMolMinimizedHitLigandOnTarget.getInteractionEFinal());
                    System.out.println("rmsd ligand = " + resultsUltiJMolMinimizedHitLigandOnTarget.getRmsdLigand());
                    System.out.println("ligand stained energy = " + resultsUltiJMolMinimizedHitLigandOnTarget.getLigandStrainedEnergy());
                    System.out.println("count longer than 2A change = " + resultsUltiJMolMinimizedHitLigandOnTarget.getCountOfLongDistanceChange());
                    System.out.println("All convergence reached = " + resultsUltiJMolMinimizedHitLigandOnTarget.isAllconvergenceReached());
                }

                if (resultsUltiJMolMinimizedHitLigandOnTarget == null) {
                    String message = "hitscore is null";
                    NullResultFromAComparisonException ex = new NullResultFromAComparisonException(message);
                    throw ex;
                }
            } else {

                resultsUltiJMolMinimizedHitLigandOnTarget = new ResultsUltiJMolMinimizedHitLigandOnTarget(0, 0.0f, 0f, 0.0f, false);
            }

            hit.setResultsUltiJMolMinimizedHitLigandOnTarget(resultsUltiJMolMinimizedHitLigandOnTarget);
        }
    }


    public static Set<char[]> makeListOfChainId(MyStructureIfc myStructure) {
        Set<char[]> setChainIds = new HashSet<>();

        for (MyChainIfc chain : myStructure.getAllChains()) {
            setChainIds.add(chain.getChainId());
        }
        return setChainIds;
    }


    public static Map<Integer, PointWithPropertiesIfc> returnCloneRotatedMiniShape(Map<Integer, PointWithPropertiesIfc> miniShape, ResultsFromEvaluateCost result) {

        Map<Integer, PointWithPropertiesIfc> newMiniShape = new HashMap<>();
        for (Entry<Integer, PointWithPropertiesIfc> entry : miniShape.entrySet()) {
            PointWithPropertiesIfc newPoint = rotateAndClonePointWithProperties(entry.getValue(), result);
            newMiniShape.put(entry.getKey(), newPoint);
        }
        return newMiniShape;
    }


    public static CollectionOfPointsWithPropertiesIfc returnCloneRotatedShape(CollectionOfPointsWithPropertiesIfc shape, ResultsFromEvaluateCost result) {

        List<PointWithPropertiesIfc> listNewpoints = new ArrayList<>();
        for (int i = 0; i < shape.getSize(); i++) {
            PointWithPropertiesIfc point = shape.getPointFromId(i);
            PointWithPropertiesIfc newPoint = rotateAndClonePointWithProperties(point, result);
            listNewpoints.add(newPoint);
        }

        CollectionOfPointsWithPropertiesIfc newShape = new CollectionOfPointsWithProperties(listNewpoints);
        return newShape;
    }


    public static MyChainIfc returnCloneRotatedPeptide(MyChainIfc inputchain, ResultsFromEvaluateCost result, AlgoParameters algoParameters) {


        Cloner cloner = new Cloner(inputchain, algoParameters);
        MyStructureIfc myStructureRotated = cloner.getRotatedClone(result);

        return myStructureRotated.getAllChains()[0];
    }


    public static Float computeRmsdBackboneAtomBetweenHitPeptideAndQueryLigandDefinigQuery(ShapeContainerIfc targetShape, ResultsFromEvaluateCost result, ShapeContainerIfc queryShape, AlgoParameters algoParameters) {

        boolean isQueryShapeContainerHasPeptideIfc = queryShape instanceof HasPeptideIfc;
        boolean isHitShapeContainerHasPeptideIfc = targetShape instanceof HasPeptideIfc;

        boolean canBeComputed = isQueryShapeContainerHasPeptideIfc && isHitShapeContainerHasPeptideIfc;
        if (!canBeComputed) {
            return null;
        }

        HasPeptideIfc queryShapeWithPeptide = (HasPeptideIfc) queryShape;
        MyChainIfc peptideUsedToBuiltTheQuery = queryShapeWithPeptide.getPeptide();

        HasPeptideIfc currentBestHitWithPeptide = (HasPeptideIfc) targetShape;
        MyChainIfc peptideCurrentBestHit = currentBestHitWithPeptide.getPeptide();

        if (peptideUsedToBuiltTheQuery != null) {

            List<MyAtomIfc> backboneAtomPeptideQuery = extractBackBoneAtoms(peptideUsedToBuiltTheQuery, algoParameters);
            List<MyAtomIfc> backboneAtomPeptideHit = extractBackBoneAtoms(peptideCurrentBestHit, algoParameters);
            // put hit in ref frame of query
            List<double[]> coordinatesHit = new ArrayList<>();
            for (MyAtomIfc atomHit : backboneAtomPeptideHit) {
                RealVector newPointCoords = PairingTools.alignPointFromShape2toShape1(result, new ArrayRealVector(ToolsMath.convertToDoubleArray(atomHit.getCoords())));
                coordinatesHit.add(newPointCoords.toArray());
            }
            List<double[]> coordinatesQuery = new ArrayList<>();
            for (MyAtomIfc atomQuery : backboneAtomPeptideQuery) {
                coordinatesQuery.add(ToolsMath.convertToDoubleArray(atomQuery.getCoords()));
            }

            List<double[]> smallestChainCoords = coordinatesHit;
            List<double[]> longestChainCoords = coordinatesQuery;
            List<MyAtomIfc> smallestChain = backboneAtomPeptideHit;
            List<MyAtomIfc> longestChain = backboneAtomPeptideQuery;

            if (backboneAtomPeptideHit.size() > backboneAtomPeptideQuery.size()) {
                smallestChain = backboneAtomPeptideQuery;
                longestChain = backboneAtomPeptideHit;
                smallestChainCoords = coordinatesQuery;
                longestChainCoords = coordinatesHit;
            }
            // 10
            // 6
            // pos 0 to pos 4 as start

            List<Integer> posibleStart = new ArrayList<>();

            int countPossibleOverlays = longestChain.size() - smallestChain.size() + 1;
            A:
            for (int j = 0; j <= countPossibleOverlays; j++) {

                for (int k = 0; k < smallestChain.size(); k++) {
                    MyAtomIfc currentAtomLongestchain = longestChain.get(k + j);
                    // if any mismatch in atom name I skip the current comparaison
                    //System.out.println(String.valueOf(smallestChain.get(k).getAtomName()) + " compared to " + String.valueOf(currentAtomLongestchain.getAtomName()));
                    if (!String.valueOf(smallestChain.get(k).getAtomName()).equals(String.valueOf(currentAtomLongestchain.getAtomName()))) {
                        continue A;
                    }
                }
                posibleStart.add(j);
            }
            //System.out.println("posibleStart : " + posibleStart);

            // for each possible start I compute the rmsd
            float minRmsd = Float.MAX_VALUE;
            for (int j = 0; j < posibleStart.size(); j++) {

                double rmsd = 0.0;
                for (int k = 0; k < smallestChain.size(); k++) {
                    double[] currentAtomSmallestchain = smallestChainCoords.get(k);
                    double[] currentAtomLongestchain = longestChainCoords.get(k + posibleStart.get(j));
                    double contribRmsd = ToolsMath.computeDistance(currentAtomSmallestchain, currentAtomLongestchain);
                    rmsd += contribRmsd * contribRmsd;
                }
                rmsd = rmsd / smallestChain.size();
                float finalRmsd = (float) Math.sqrt(rmsd);
                if (finalRmsd < minRmsd) {
                    minRmsd = finalRmsd;
                }
            }

            return minRmsd;
        }
        return null;
    }



    // -------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------
    private static List<MyAtomIfc> extractBackBoneAtoms(MyChainIfc peptideUsedToBuiltTheQuery, AlgoParameters algoParameters) {

        List<MyAtomIfc> backboneAtomToReturn = new ArrayList<>();
        for (MyMonomerIfc monomer : peptideUsedToBuiltTheQuery.getMyMonomers()) {
            MyAtomIfc atomN = null;
            MyAtomIfc atomC = null;
            MyAtomIfc atomCA = null;
            MyAtomIfc atomO = null;

            for (MyAtomIfc atom : monomer.getMyAtoms()) {
                if (String.valueOf(atom.getAtomName()).equals("N")) {
                    atomN = atom;
                    continue;
                }
                if (String.valueOf(atom.getAtomName()).equals("C")) {
                    atomC = atom;
                    continue;
                }
                if (String.valueOf(atom.getAtomName()).equals("CA")) {
                    atomCA = atom;
                    continue;
                }
                if (String.valueOf(atom.getAtomName()).equals("O")) {
                    atomO = atom;
                    continue;
                }
            }
            if (atomN != null && atomC != null && atomCA != null && atomO != null) {

                backboneAtomToReturn.add(atomN);
                backboneAtomToReturn.add(atomC);
                backboneAtomToReturn.add(atomCA);
                backboneAtomToReturn.add(atomO);
                //System.out.println(String.valueOf(atom.getAtomName()) + " is kept ");
            }
        }
        return backboneAtomToReturn;
    }


    private static PointWithPropertiesIfc rotateAndClonePointWithProperties(PointWithPropertiesIfc inputPoint, ResultsFromEvaluateCost result) {

        RealVector coordsVector = new ArrayRealVector(ToolsMath.convertToDoubleArray(inputPoint.getCoords().getCoords()));
        RealVector newPointCoords = PairingTools.alignPointFromShape2toShape1(result, coordsVector);
        PointWithPropertiesIfc newPoint = new PointWithProperties();
        newPoint.setCoords(new Point(ToolsMath.convertToFloatArray(newPointCoords.toArray())));
        newPoint.setStrikingProperties(inputPoint.getStrikingProperties());
        return newPoint;
    }
}