package shapeCompare;

import convertformat.AdapterBioJavaStructure;
import convertformat.ExceptionInConvertFormat;
import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import hits.Hit;
import hits.HitPeptideWithQueryPeptide;
import hits.HitTools;
import io.BiojavaReader;
import io.ExceptionInIOPackage;
import io.Tools;
import mystructure.EnumMyReaderBiojava;
import mystructure.ExceptionInMyStructurePackage;
import mystructure.MyStructureIfc;
import mystructure.ReadingStructurefileException;
import org.biojava.nbio.structure.Structure;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.*;
import shape.ShapeContainerIfc;
import shape.ShapeContainerWithPeptide;
import shapeBuilder.EnumShapeReductor;
import shapeBuilder.ShapeBuildingException;
import ultiJmol1462.ResultsUltiJMolMinimizedHitLigandOnTarget;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 14/10/16.
 */
public class AutoShapeSegmentOfChainTest {

    @Test
    public void testAutoCompareShapeFromSegmentOfChain() throws ExceptionInScoringUsingBioJavaJMolGUI, ReadingStructurefileException, ExceptionInMyStructurePackage, CommandLineException, ParsingConfigFileException, ShapeBuildingException, IOException {


        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        int initialCount = algoParameters.ultiJMolBuffer.getSize();

        String fourLetterCode = "2yjd";
        char[] chainId = "C".toCharArray();
        int startingRankId = 3;
        int peptideLength = 4;
        ShapeContainerDefined shapeContainerbuilderQuery = new ShapecontainerDefinedBySegmentOfChain(fourLetterCode.toCharArray(), chainId, startingRankId, peptideLength, algoParameters);
        ShapeContainerIfc shapeQuery = null;
        try {
            shapeQuery = shapeContainerbuilderQuery.getShapecontainer();
        } catch (ShapeBuildingException e) {
            assertTrue(false);
        }

        ShapeContainerDefined shapeContainerbuilderTarget = new ShapecontainerDefinedBySegmentOfChain(fourLetterCode.toCharArray(), chainId, startingRankId, peptideLength, algoParameters);
        ShapeContainerIfc shapeTarget = null;
        try {
            shapeTarget = shapeContainerbuilderTarget.getShapecontainer();
        } catch (ShapeBuildingException e) {
            assertTrue(false);
        }


        ComparatorShapeContainerQueryVsAnyShapeContainer comparatorShape = new ComparatorShapeContainerQueryVsAnyShapeContainer(shapeQuery, shapeTarget, algoParameters);
        List<Hit> listBestHitForEachAndEverySeed = null;
        try {
            listBestHitForEachAndEverySeed = comparatorShape.computeResults();

        } catch (NullResultFromAComparisonException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // first hit
        // InteractionEFinal = 6.20062255859375
        // rmsd ligand = 0.9117103815078735 // rmsdLigand = 4.2638924E-16
        // ligand stained energy = 133.43154907226562
        // count longer than 2A change = 4
        try {
            Hit hit = listBestHitForEachAndEverySeed.get(0);
            HitTools.minimizeHitInQuery(hit, shapeQuery, shapeTarget, algoParameters);
            ResultsUltiJMolMinimizedHitLigandOnTarget hitScore = hit.getResultsUltiJMolMinimizedHitLigandOnTarget();
            assertTrue(hitScore != null);
            assertTrue(hitScore.getInteractionEFinal() > -30 && hitScore.getInteractionEFinal() < -28);
            assertTrue(Math.abs(hitScore.getLigandStrainedEnergy()) > 40 && Math.abs(hitScore.getLigandStrainedEnergy()) < 50);
            assertTrue(hitScore.getRmsdLigand() < 0.7 && hitScore.getRmsdLigand() > 0.5);
            assertTrue(hitScore.getCountOfLongDistanceChange() == 3);
            HitPeptideWithQueryPeptide hitPeptideWithQueryPeptide = (HitPeptideWithQueryPeptide) hit;
            assertTrue(hitPeptideWithQueryPeptide.getRmsdBackboneWhencomparingPeptideToPeptide() > 0.25 && hitPeptideWithQueryPeptide.getRmsdBackboneWhencomparingPeptideToPeptide() < 0.30);

        } catch (NullResultFromAComparisonException e) {
            e.printStackTrace();
        }
        int finalCount = algoParameters.ultiJMolBuffer.getSize();
        assertTrue(finalCount == initialCount);
        try {
            for (int i = 0; i < initialCount; i++) {
                algoParameters.ultiJMolBuffer.get().frame.dispose();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 0);

    }
}
