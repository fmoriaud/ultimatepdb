package protocols;

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
import org.junit.Ignore;
import org.junit.Test;
import parameters.AlgoParameters;
import shape.ShapeContainerIfc;
import shapeBuilder.EnumShapeReductor;
import shapeBuilder.ShapeBuildingException;
import shapeCompare.ComparatorShapeContainerQueryVsAnyShapeContainer;
import shapeCompare.NullResultFromAComparisonException;
import ultiJmol1462.ResultsUltiJMolMinimizedHitLigandOnTarget;

import java.io.IOException;
import java.util.List;
import java.util.logging.FileHandler;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 14/10/16.
 */
public class AutoShapeSegmentOfChainWithProtocolToolsTest {

    @Ignore // till I have less hits minimizing
    @Test
    public void testAutoCompareShapeFromSegmentOfChain() throws ExceptionInScoringUsingBioJavaJMolGUI, ReadingStructurefileException, ExceptionInMyStructurePackage, CommandLineException, ParsingConfigFileException, ShapeBuildingException, IOException {

        char[] chainId = "C".toCharArray();
        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        int initialCount = algoParameters.ultiJMolBuffer.getSize();

        FileHandler fh = null;
        try {
            fh = new FileHandler(algoParameters.getPATH_TO_RESULT_FILES() + "log_Project_2timesTheQueryAsTargetOneThread.txt");
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        fh.setFormatter(new OptimizerFormater());
        ControllerLoger.logger.addHandler(fh);


        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 1);

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

        boolean minimizeAllIfTrueOrOnlyOneIfFalse = true;
        ProtocolTools.compareAndWriteToResultFolder(minimizeAllIfTrueOrOnlyOneIfFalse, shapeQuery, shapeTarget, algoParameters);

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
