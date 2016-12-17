package shapeCompare;

import hits.Hit;
import math.ToolsMath;
import mystructure.*;
import parameters.AlgoParameters;
import protocols.ShapeContainerFactory;
import scorePairing.CheckDistanceToOutside;
import scorePairing.ExtendPairing;
import scorePairing.ScorePairing;
import scorePairing.ScorePairingTools;
import shape.ShapeContainerIfc;
import shape.ShapeContainerWithLigand;
import shape.ShapeContainerWithPeptide;
import shapeBuilder.EnumShapeReductor;
import shapeBuilder.ShapeBuildingException;
import shapeBuilder.StructureLocalToBuildAnyShape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Fabrice on 14/11/16.
 */
public class CompareCompleteCheck {


    //------------------------
    // Class variables
    //------------------------
    private ShapeContainerIfc shapeContainerQuery;
    private ShapeContainerIfc shapeContainerAnyShape;
    private AlgoParameters algoParameters;

    private float thresholdCostOriginal = 0.15f; // arbitrary but fix a limit on quality of paired points so partial hit quality
    private int thresholdClashesCount = 5; // ten clashes maximum
    private float thresholdPercentageClashesCount = 0.1f; // 25% of ligand atoms clashing
    private float thresholdPercentageIncreaseCompleteCheck = 0.2f;

    private int countHitDeletedBecauseOforiginalCost;
    private int countHitDeletedBecauseOfHitLigandClashesInQuery;

    // TODO maybe this increase could be normalized by the number of additional points ?
    private int countHitDeletedBecauseOfPercentageIncreaseCompleteCheck;

    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public CompareCompleteCheck(ShapeContainerIfc shapeContainerQuery, ShapeContainerIfc shapeContainerWithLigandOrPeptide, AlgoParameters algoParameters) {

        this.shapeContainerQuery = shapeContainerQuery;
        this.shapeContainerAnyShape = shapeContainerWithLigandOrPeptide;
        this.algoParameters = algoParameters;
    }

    public List<Hit> computeResults() throws NullResultFromAComparisonException {


        if (!(shapeContainerAnyShape instanceof ShapeContainerWithPeptide || shapeContainerAnyShape instanceof ShapeContainerWithLigand)) {

            String message = "CompareCompleteCheck can be used only if a peptide or a ligand in target shape";
            NullResultFromAComparisonException exception = new NullResultFromAComparisonException(message);
            throw exception;
        }

        // Compare query shqpe and target shape
        ComparatorShapeContainerQueryVsAnyShapeContainer compareQueryAndTarget = new ComparatorShapeContainerQueryVsAnyShapeContainer(shapeContainerQuery, shapeContainerAnyShape, algoParameters);
        List<Hit> resultsCompareQueryAndTarget = compareQueryAndTarget.computeResults();

        if (resultsCompareQueryAndTarget.size() == 0) {
            String message = "No Hit Found";
            NullResultFromAComparisonException exception = new NullResultFromAComparisonException(message);
            throw exception;
        }

        // Select best hits ?

        // Do complete check

        // 60 hits
        List<Hit> resultsCompareQueryAndTargetSelectedOnCompleteCheck = new ArrayList<>();

        for (Hit hit : resultsCompareQueryAndTarget) {

            // cost of overlay of query to hit shape, based on paired points,
            // not very informative as relative to the number of points, a good local overlay and a large good overlay
            // scores the same. Good thing is that cosly overlay is detected.
            double originalCost = hit.getResultsFromEvaluateCost().getCost();

            if (originalCost > thresholdCostOriginal) {
                countHitDeletedBecauseOforiginalCost += 1;
                continue;
            }
            // get the hit ligand in the query global structure

            MyStructureIfc rotatedLigandOrPeptide = CompareTools.getLigandOrPeptideInReferenceOfQuery(shapeContainerAnyShape, hit.getResultsFromEvaluateCost(), algoParameters);


            // I could compute neighbors by representative distance and then use the same code for shape ??
            // know what was removed to build MyStructureLocal

            MyStructureIfc myStructureLocalQuery = shapeContainerQuery.getMyStructureUsedToComputeShape();

            // TODO shapelocal and ligand are protonated ...
            int clashesCount = computeClashes(myStructureLocalQuery, rotatedLigandOrPeptide);


            int atomCountLigand = MyStructureTools.getAtomCount(rotatedLigandOrPeptide);
            float percentClashes = (float) clashesCount / (float) atomCountLigand;
            if (clashesCount > thresholdClashesCount || percentClashes > thresholdPercentageClashesCount) {
                countHitDeletedBecauseOfHitLigandClashesInQuery += 1;
                continue;

            }
            List<MyMonomerIfc> foreignMonomerToExclude = shapeContainerQuery.getForeignMonomerToExclude();


            // we use myStructureLocalQuery but the entire MyStructure would be better if the ligand is much bigger but
            // it is unlikely
            // 618 32 the same as shape container query in the test
            ShapeContainerIfc shapeContainerCompleteCheck = ShapeContainerFactory.getShapeAroundForeignLigand(EnumShapeReductor.CLUSTERING, myStructureLocalQuery, foreignMonomerToExclude, rotatedLigandOrPeptide, algoParameters);

            // Now I want to pair and score shapeContainerAnyShape to shapeContainerCompleteCheck
            // using the rotation and translation form query to hit.
            ResultsFromEvaluateCost result = hit.getResultsFromEvaluateCost();

            ResultsFromEvaluateCost resultCompleteCheck = ScorePairingTools.score(shapeContainerCompleteCheck, shapeContainerAnyShape, result, algoParameters);

            ResultsFromEvaluateCost resultRedone = ScorePairingTools.score(shapeContainerQuery, shapeContainerAnyShape, result, algoParameters);


            // results are relative to the number of points in the pairing
            int pairedPointsCompleteCheck = resultCompleteCheck.getPairingAndNullSpaces().getPairing().size();
            double costCompleteCheck = resultCompleteCheck.getCost();
            double absoluteCostCompleteCheck = pairedPointsCompleteCheck * costCompleteCheck;

            int pairedPointsRedone = resultRedone.getPairingAndNullSpaces().getPairing().size();
            double costRedone = resultRedone.getCost();
            double absoluteCostRedone = pairedPointsRedone * costRedone;

            int pairedPointsOriginal = result.getPairingAndNullSpaces().getPairing().size();
            double costOriginal = result.getCost();
            double absoluteCostOriginal = pairedPointsOriginal * costOriginal;

            double percentageIncreaseCompleteCheck = 0;
            if (absoluteCostCompleteCheck > 0.001) { // in some cases we could get a zero cost with perfect pairing
                percentageIncreaseCompleteCheck = (absoluteCostCompleteCheck - absoluteCostRedone) / absoluteCostRedone;
            }

            if (percentageIncreaseCompleteCheck > thresholdPercentageIncreaseCompleteCheck) {
                countHitDeletedBecauseOfPercentageIncreaseCompleteCheck += 1;
                continue;
            }

            hit.setClashesCount(clashesCount);
            hit.setPercentageIncreaseCompleteCheck(percentageIncreaseCompleteCheck);
            resultsCompareQueryAndTargetSelectedOnCompleteCheck.add(hit);
            /*
            System.out.println("absoluteCostCompleteCheck = " + absoluteCostCompleteCheck + " with " + pairedPointsCompleteCheck + " points");
            System.out.println("absoluteCostRedone = " + absoluteCostRedone + " with " + pairedPointsRedone + " points");
            System.out.println("Cost original = " + costOriginal);
            System.out.println("absoluteCostOriginal = " + absoluteCostOriginal + " with " + pairedPointsOriginal + " points");
            System.out.println();
            */
        }

        return resultsCompareQueryAndTargetSelectedOnCompleteCheck;
    }


    private int computeClashes(MyStructureIfc myStructureLocalQuery, MyStructureIfc rotatedLigandOrPeptide) {

        float distanceThresholdToDefineClashes = 2.0f;
        int clashescount = 0;

        for (MyChainIfc chainLigand : rotatedLigandOrPeptide.getAllChains()) {
            for (MyMonomerIfc monomerLigand : chainLigand.getMyMonomers()) {
                for (MyAtomIfc atomLigand : monomerLigand.getMyAtoms()) {
                    if (MyStructureTools.isHydrogen(atomLigand)) {
                        continue;
                    }

                    // TODO check if getAllChainsRelevantForShapeBuilding excludes heatm
                    MyChainIfc[] chains = myStructureLocalQuery.getAllChainsRelevantForShapeBuilding();
                    for (MyChainIfc chain : chains) {
                        for (MyMonomerIfc monomer : chain.getMyMonomers()) {

                            for (MyAtomIfc atom : monomer.getMyAtoms()) {
                                if (MyStructureTools.isHydrogen(atom)) {
                                    continue;
                                }
                                float distance = ToolsMath.computeDistance(atom.getCoords(), atomLigand.getCoords());
                                if (distance < distanceThresholdToDefineClashes) {
                                    clashescount += 1;
                                }
                            }
                        }
                    }
                }
            }
        }
        return clashescount;
    }


    public int getCountHitDeletedBecauseOforiginalCost() {
        return countHitDeletedBecauseOforiginalCost;
    }

    /**
     * For test only
     *
     * @return
     */
    public int getCountHitDeletedBecauseOfHitLigandClashesInQuery() {
        return countHitDeletedBecauseOfHitLigandClashesInQuery;
    }

    /**
     * For test only
     *
     * @return
     */
    public int getCountHitDeletedBecauseOfPercentageIncreaseCompleteCheck() {
        return countHitDeletedBecauseOfPercentageIncreaseCompleteCheck;
    }
}