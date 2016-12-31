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


        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        int initialCount = algoParameters.ultiJMolBuffer.getSize();

        FileHandler fh = null;
        try {
            fh = new FileHandler(algoParameters.getPATH_TO_RESULT_FILES() + ControllerLoger.LOGGER_FILE_NAME);
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
        char[] chainId = "C".toCharArray();
        int startingRankId = 3;
        int peptideLength = 4;

        ShapeContainerDefined shapeContainerDefinedQuery = new ShapecontainerDefinedBySegmentOfChain(fourLetterCode.toCharArray(), chainId, startingRankId, peptideLength, algoParameters);
        ShapeContainerIfc shapeQuery = null;
        try {
            shapeQuery = shapeContainerDefinedQuery.getShapecontainer();
        } catch (ShapeBuildingException e) {
            e.printStackTrace();
        }

        ShapeContainerDefined shapeContainerDefinedTarget = new ShapecontainerDefinedBySegmentOfChain(fourLetterCode.toCharArray(), chainId, startingRankId, peptideLength, algoParameters);
        ShapeContainerIfc shapeTarget = null;
        try {
            shapeTarget = shapeContainerDefinedTarget.getShapecontainer();
        } catch (ShapeBuildingException e) {
            e.printStackTrace();
        }

        boolean minimizeAllIfTrueOrOnlyOneIfFalse = true;
        ProtocolTools.compareCompleteCheckAndWriteToResultFolder(minimizeAllIfTrueOrOnlyOneIfFalse, shapeQuery, shapeTarget, algoParameters);

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
