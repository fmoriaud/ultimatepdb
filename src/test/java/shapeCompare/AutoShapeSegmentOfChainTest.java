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
import protocols.CommandLineException;
import protocols.ParsingConfigFileException;
import protocols.ShapeContainerFactory;
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

        char[] chainId = "C".toCharArray();
        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        int initialCount = algoParameters.ultiJMolBuffer.getSize();

        String fourLetterCode = "2yjd";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructureQuery = null;
        try {
            mmcifStructureQuery = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructureQuery = null;
        try {
            mystructureQuery = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructureQuery, EnumMyReaderBiojava.BioJava_MMCIFF);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
            assertTrue(false);
        }

        int startingRankId = 3;
        int peptideLength = 4;
        ShapeContainerIfc shapeQuery = null;
        try {
            shapeQuery = ShapeContainerFactory.getShapeAroundASegmentOfChainUsingStartingMyMonomerPositionInChain(EnumShapeReductor.CLUSTERING, mystructureQuery, algoParameters, chainId, startingRankId, peptideLength);
        } catch (ShapeBuildingException e) {
            e.printStackTrace();
        }


        Structure mmcifStructureTarget = null;
        try {
            mmcifStructureTarget = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }

        MyStructureIfc mystructureTarget = null;
        try {
            mystructureTarget = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructureTarget, EnumMyReaderBiojava.BioJava_MMCIFF);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
            assertTrue(false);
        }

        ShapeContainerIfc shapeTarget = null;
        try {
            shapeTarget = ShapeContainerFactory.getShapeAroundASegmentOfChainUsingStartingMyMonomerPositionInChain(EnumShapeReductor.CLUSTERING, mystructureTarget, algoParameters, chainId, startingRankId, peptideLength);
        } catch (ShapeBuildingException e) {
            e.printStackTrace();
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
            assertTrue(hitScore.getInteractionEFinal() > -20 && hitScore.getInteractionEFinal() < -19);
            assertTrue(Math.abs(hitScore.getLigandStrainedEnergy()) > 130 && Math.abs(hitScore.getLigandStrainedEnergy()) < 132);
            assertTrue(hitScore.getRmsdLigand() < 0.7 && hitScore.getRmsdLigand() > 0.6);
            assertTrue(hitScore.getCountOfLongDistanceChange() == 0);
            HitPeptideWithQueryPeptide hitPeptideWithQueryPeptide = (HitPeptideWithQueryPeptide) hit;
            assertTrue(hitPeptideWithQueryPeptide.getRmsdBackboneWhencomparingPeptideToPeptide() < 0.001);

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
