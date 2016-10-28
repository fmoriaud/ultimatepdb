package protocols;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import mystructure.*;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import hits.Hit;
import math.ToolsMath;
import parameters.AlgoParameters;
import shape.HasPeptideIfc;
import shape.ShapeContainerIfc;
import shape.ShapeContainerWithLigand;
import shapeBuilder.ShapeBuilderConstructorIfc;
import shapeBuilder.ShapeBuildingException;
import shapeCompare.ComparatorShapeContainerQueryVsAnyShapeContainer;
import shapeCompare.NullResultFromAComparisonException;
import shapeCompare.PairingTools;
import ultiJmol1462.MyJmolTools;
import ultiJmol1462.ResultsUltiJMolMinimizedHitLigandOnTarget;


public class ProtocolToolsToHandleInputFilesAndShapeComparisons {

    /**
     * compare a query shape to target shape builder
     * it is a convenience method calling the method to compare a query shape to a target shape
     * it is returning a data structure objectcalled Hit
     */
    public static Hit compareQueryToOneShape(ShapeContainerIfc queryShape, ShapeBuilderConstructorIfc targetShapeBuilder, AlgoParameters algoParameters) throws ShapeBuildingException {

        ShapeContainerIfc targetShape;
        try {
            targetShape = targetShapeBuilder.getShapeContainer();

        } catch (ShapeBuildingException e2) {
            String message = "Target shape failed to build. Reason underneath : " + e2.getMessage();
            throw new ShapeBuildingException(message);
        }
        Hit bestHit = compareQueryToOneShape(queryShape, targetShape, algoParameters);
        return bestHit;
    }


    /**
     * compare a query shape to target shape
     * it is returning a data structure object called Hit
     * it is handling the interpretation of the Hit ligand if it was minimized in query by returning Hit or null
     */
    public static Hit compareQueryToOneShape(ShapeContainerIfc queryShape, ShapeContainerIfc targetShape, AlgoParameters algoParameters) throws ShapeBuildingException {

        Hit bestHit = null;
        try {
            bestHit = doComparison(queryShape, algoParameters, targetShape);
        } catch (NullResultFromAComparisonException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        if (bestHit == null) {
            return null;
        }
        ResultsUltiJMolMinimizedHitLigandOnTarget hitScore = bestHit.getResultsUltiJMolMinimizedHitLigandOnTarget();
        boolean goodEnoughToBeStored = isThatHitGoodEnoughToBeStoredBasedOnMinimization(hitScore, algoParameters);
        if (goodEnoughToBeStored == true || algoParameters.isOPTIMIZE_HIT_GEOMETRY() == false) {

            String endOfFileName = targetShape.makeEndFileName();
            String fileName = "hitMiniShape_" + endOfFileName;
            bestHit.getShapeContainer().exportRotatedMiniShapeColoredToPDBFile(fileName, algoParameters, bestHit.getResultsFromEvaluateCost());
            String fileNameShape = "hitShape_" + endOfFileName;
            bestHit.getShapeContainer().exportRotatedShapeColoredToPDBFile(fileNameShape, algoParameters, bestHit.getResultsFromEvaluateCost());

            StringBuffer message = new StringBuffer();
            message.append("Hit scored to whole Query       : " + bestHit.toString());
            message.append("HitScore       : " + hitScore.toString());

            ControllerLoger.logger.log(Level.INFO, message.toString());
        }
        return bestHit;
    }


    private static boolean isThatHitGoodEnoughToBeStoredBasedOnMinimization(ResultsUltiJMolMinimizedHitLigandOnTarget hitScore, AlgoParameters algoParameters) {

        double cutoffInterEnergyPerResidue = 0.0;

        if (Double.isNaN(hitScore.getLigandStrainedEnergy()) || hitScore.getLigandStrainedEnergy() > cutoffInterEnergyPerResidue) {
            return false;
        }

        //if (hitScore.getReceptorFixedLigandOptimizedRmsdBeforeAndAfterOptimization() > 2.0){
        //	return false; // If the rmsd on backbone and CB before and after mini is higher than 2A then I consider
        // the minimization has too much modified the posed peptide by similarity
        //}

        if (hitScore.getCountOfLongDistanceChange() > 1) {
            return false;
        }

        return true;
    }


    private static Hit doComparison(ShapeContainerIfc queryShape, AlgoParameters algoParameters, ShapeContainerIfc targetShape) throws NullResultFromAComparisonException {

        Hit currentBestHit = null;

        ComparatorShapeContainerQueryVsAnyShapeContainer comparatorShapeContainerV1 = new ComparatorShapeContainerQueryVsAnyShapeContainer(queryShape, targetShape, algoParameters);

        List<Hit> listBestHitForEachAndEverySeed;
        try {
            listBestHitForEachAndEverySeed = comparatorShapeContainerV1.computeResults();
        } catch (NullResultFromAComparisonException e1) {

            String message = "Query and Target shape failed to compare." + e1.getMessage();
            ControllerLoger.logger.log(Level.INFO, message.toString());
            throw new NullResultFromAComparisonException(message);
        }

        if (listBestHitForEachAndEverySeed.size() != 0) {
            currentBestHit = listBestHitForEachAndEverySeed.get(0);

            double cost = currentBestHit.getResultsFromEvaluateCost().getCost();
            System.out.println("distance residual = " + currentBestHit.getResultsFromEvaluateCost().getDistanceResidual());
            System.out.println("currentBestHit.getResultsFromEvaluateCost().getCost() = " + cost);

            //if (cost < 1.00 || (rmsdLigand != null && rmsdLigand < 5.0)){
            Double rmsdLigand = computeRmsdBackboneAtomBetweenHitPeptideAndQueryLigandDefinigQuery(currentBestHit, queryShape, algoParameters);

            int pairedPointCount = currentBestHit.getResultsFromEvaluateCost().getPairingAndNullSpaces().getPairing().size();
            int unpairedPointHit = currentBestHit.getResultsFromEvaluateCost().getPairingAndNullSpaces().getNullSpaceOfMap2().size();
            float ratioPairedPointToHitPoints = (float) pairedPointCount / ((float) pairedPointCount + (float) unpairedPointHit);


            if (targetShape instanceof ShapeContainerWithLigand || (rmsdLigand != null && rmsdLigand < 100.0)) {
                // scoring with Jmol forcefield

                ResultsUltiJMolMinimizedHitLigandOnTarget resultsUltiJMolMinimizedHitLigandOnTarget;
                if (algoParameters.isOPTIMIZE_HIT_GEOMETRY() == true) {
                    try {

                        MyChainIfc peptideOrLigand = null;

                        if (currentBestHit.getShapeContainer() instanceof HasPeptideIfc) {
                            HasPeptideIfc queryShapeWithPeptide = (HasPeptideIfc) currentBestHit.getShapeContainer();
                            peptideOrLigand = queryShapeWithPeptide.getPeptide();
                        }
                        if (currentBestHit.getShapeContainer() instanceof ShapeContainerWithLigand) {
                            ShapeContainerWithLigand shapeContainerWithLigand = (ShapeContainerWithLigand) currentBestHit.getShapeContainer();
                            peptideOrLigand = new MyChain(shapeContainerWithLigand.getHetatmLigand(), shapeContainerWithLigand.getHetatmLigand().getParent().getChainId()); // returnCloneRotatedPeptide(hetAtomInChain, hit.getResultsFromEvaluateCost());
                        }

                        Cloner clonerPeptide = new Cloner(peptideOrLigand, algoParameters);
                        MyStructureIfc rotatedclone = clonerPeptide.getRotatedClone(currentBestHit.getResultsFromEvaluateCost());

                        MyStructureIfc clonedRotatedPeptide = MyJmolTools.protonateStructure(rotatedclone, algoParameters); // c'est ca qui deconne a mettre trop d'hydrogene


                        MyStructureIfc structureQueryComputeshape = queryShape.getMyStructureUsedToComputeShape();

                        Cloner cloner = new Cloner(structureQueryComputeshape, algoParameters);
                        MyStructureIfc clonedMyStructure = cloner.getClone();

                        MyStructureIfc preparedQuery = MyJmolTools.protonateStructure(clonedMyStructure, algoParameters);

                        resultsUltiJMolMinimizedHitLigandOnTarget = MyJmolTools.scoreByMinimizingLigandOnFixedReceptor(algoParameters, clonedRotatedPeptide, preparedQuery);

                        if (resultsUltiJMolMinimizedHitLigandOnTarget != null) {

                            System.out.println("interaction = " + resultsUltiJMolMinimizedHitLigandOnTarget.getInteractionEFinal());
                            System.out.println("strained energy = " + resultsUltiJMolMinimizedHitLigandOnTarget.getLigandStrainedEnergy());
                            System.out.println("rmsd before/after opt. = " + resultsUltiJMolMinimizedHitLigandOnTarget.getRmsdLigand());
                            System.out.println("count longer than 2A change = " + resultsUltiJMolMinimizedHitLigandOnTarget.getCountOfLongDistanceChange());
                        }

                    } catch (ExceptionInScoringUsingBioJavaJMolGUI | ShapeBuildingException e) {

                        // TODO FMM here I should restart the JmolGUI like when protonation failed in shape builder: is it done ?
                        System.out.println("HitTools.scoreHitWithinQuery " + e.getMessage());
                        String message = "scoreHitWithinQuery throws exception";
                        NullResultFromAComparisonException ex = new NullResultFromAComparisonException(message);
                        throw ex;
                    }
                    if (resultsUltiJMolMinimizedHitLigandOnTarget == null) {
                        String message = "hitscore is null";
                        NullResultFromAComparisonException ex = new NullResultFromAComparisonException(message);
                        throw ex;
                    }
                } else {

                    resultsUltiJMolMinimizedHitLigandOnTarget = new ResultsUltiJMolMinimizedHitLigandOnTarget(0, 0.0f, 0.0f, 0.0f, false);
                }

                if (rmsdLigand != null) {
                    System.out.println("rmsdLigand = " + rmsdLigand);
                    resultsUltiJMolMinimizedHitLigandOnTarget.setRmsdLigand((float) rmsdLigand.doubleValue());
                }

                //				boolean isShapeOverlapOK = checkIfQuerySignificantlyCovered(currentBestHit.getResultsFromEvaluateCost(), hitScore);
                //
                //				if (isShapeOverlapOK == false){
                //					return false;
                //				}

                currentBestHit.setResultsUltiJMolMinimizedHitLigandOnTarget(resultsUltiJMolMinimizedHitLigandOnTarget);

            } else {
                System.out.println();
                System.out.println("Hit output failed: targetShape instanceof ShapeContainerWithLigand || (rmsdLigand != null && rmsdLigand < 100.0)");
            }
        }
        return currentBestHit;
    }


    // -------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------
    private static Double computeRmsdBackboneAtomBetweenHitPeptideAndQueryLigandDefinigQuery(Hit currentBestHit, ShapeContainerIfc queryShape, AlgoParameters algoParameters) {

        boolean isQueryShapeContainerHasPeptideIfc = queryShape instanceof HasPeptideIfc;
        boolean isHitShapeContainerHasPeptideIfc = currentBestHit.getShapeContainer() instanceof HasPeptideIfc;

        boolean canBeComputed = isQueryShapeContainerHasPeptideIfc && isHitShapeContainerHasPeptideIfc;
        if (!canBeComputed) {
            return null;
        }

        HasPeptideIfc queryShapeWithPeptide = (HasPeptideIfc) queryShape;
        MyChainIfc peptideUsedToBuiltTheQuery = queryShapeWithPeptide.getPeptide();

        ShapeContainerIfc targetshape = currentBestHit.getShapeContainer();
        HasPeptideIfc currentBestHitWithPeptide = (HasPeptideIfc) targetshape;
        MyChainIfc peptideCurrentBestHit = currentBestHitWithPeptide.getPeptide();

        if (peptideUsedToBuiltTheQuery != null) {

            List<MyAtomIfc> backboneAtomPeptideQuery = extractBackBoneAtoms(peptideUsedToBuiltTheQuery, algoParameters);
            List<MyAtomIfc> backboneAtomPeptideHit = extractBackBoneAtoms(peptideCurrentBestHit, algoParameters);
            // put hit in ref frame of query
            List<double[]> coordinatesHit = new ArrayList<>();
            for (MyAtomIfc atomHit : backboneAtomPeptideHit) {
                RealVector newPointCoords = PairingTools.alignPointFromShape2toShape1(currentBestHit.getResultsFromEvaluateCost(), new ArrayRealVector(ToolsMath.convertToDoubleArray(atomHit.getCoords())));
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
            double minRmsd = Double.MAX_VALUE;
            for (int j = 0; j < posibleStart.size(); j++) {

                double rmsd = 0.0;
                for (int k = 0; k < smallestChain.size(); k++) {
                    double[] currentAtomSmallestchain = smallestChainCoords.get(k);
                    double[] currentAtomLongestchain = longestChainCoords.get(k + posibleStart.get(j));
                    double contribRmsd = ToolsMath.computeDistance(currentAtomSmallestchain, currentAtomLongestchain);
                    rmsd += contribRmsd * contribRmsd;
                }
                rmsd = rmsd / smallestChain.size();
                double finalRmsd = Math.sqrt(rmsd);
                if (finalRmsd < minRmsd) {
                    minRmsd = finalRmsd;
                }
            }

            return minRmsd;
        }
        return null;
    }


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
}
