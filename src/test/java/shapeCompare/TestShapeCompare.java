package shapeCompare;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.logging.FileHandler;

import convertformat.AdapterBioJavaStructure;
import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import hits.Hit;
import io.BiojavaReaderFromPathToMmcifFileTest;
import io.Tools;
import org.biojava.nbio.structure.Structure;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.CommandLineException;
import protocols.CommandLineTools;
import protocols.ControllerLoger;
import protocols.OptimizerFormater;
import protocols.ParsingConfigFileException;
import shape.ShapeContainerIfc;
import shape.ShapeContainerWithPeptide;
import shapeBuilder.ShapeBuilder;
import shapeBuilder.ShapeBuilderConstructorHetAtm;
import shapeBuilder.ShapeBuilderConstructorIfc;
import shapeBuilder.ShapeBuildingException;
import mystructure.EnumMyReaderBiojava;
import mystructure.ExceptionInMyStructurePackage;
import mystructure.MyStructureIfc;
import mystructure.ReadingStructurefileException;
import tools.ToolsForTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class TestShapeCompare {

    @Test
    public void testCompareTwoKinaseLigandShape() throws ExceptionInScoringUsingBioJavaJMolGUI, ReadingStructurefileException, ExceptionInMyStructurePackage, CommandLineException, ParsingConfigFileException, ShapeBuildingException, IOException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();

        MyStructureIfc myStructure1di9 = ToolsForTests.getMyStructureIfc(algoParameters, "1di9.cif.gz");
        MyStructureIfc myStructure5lar = ToolsForTests.getMyStructureIfc(algoParameters, "1a9u.cif.gz");


        char[] hetatmLigandF46 = "SB2".toCharArray();
        int occurenceId = 1;
        ShapeBuilderConstructorIfc shapeBuilderSB2 = new ShapeBuilderConstructorHetAtm(myStructure5lar, hetatmLigandF46, occurenceId, algoParameters);
        ShapeContainerIfc shapeF46 = null;
        try {
            shapeF46 = shapeBuilderSB2.getShapeContainer();
        } catch (
                ShapeBuildingException e) {
            assertTrue(false);
        }
        char[] hetatmLigandMSQ = "MSQ".toCharArray();
        ShapeBuilderConstructorIfc shapeBuilder = new ShapeBuilderConstructorHetAtm(myStructure1di9, hetatmLigandMSQ, occurenceId, algoParameters);
        ShapeContainerIfc shapeMSQ = null;
        try {
            shapeMSQ = shapeBuilder.getShapeContainer();
        } catch (
                ShapeBuildingException e) {
            assertTrue(false);
        }

        ComparatorShapeContainerQueryVsAnyShapeContainer comparatorShape = new ComparatorShapeContainerQueryVsAnyShapeContainer(shapeMSQ, shapeF46, algoParameters);
        List<Hit> listBestHitForEachAndEverySeed = null;
        try {
            listBestHitForEachAndEverySeed = comparatorShape.computeResults();

        } catch (NullResultFromAComparisonException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // dont know if good but it is like this currently
        // It is not reproducible
        //assertTrue(listBestHitForEachAndEverySeed.size() == 16 || listBestHitForEachAndEverySeed.size() == 19 || listBestHitForEachAndEverySeed.size() == 20);
        assertTrue(listBestHitForEachAndEverySeed.size() == 20);
        float coverageTopHit = listBestHitForEachAndEverySeed.get(0).getResultsFromEvaluateCost().getCoverage();
        assertEquals(coverageTopHit, 0.679, 0.001);
        double costTopHit = listBestHitForEachAndEverySeed.get(0).getResultsFromEvaluateCost().getCost();
        assertEquals(costTopHit, 0.0386, 0.0001);

       /* if (listBestHitForEachAndEverySeed.size() == 19) {

            float coverageLastHit = listBestHitForEachAndEverySeed.get(18).getResultsFromEvaluateCost().getCoverage();
            assertEquals(coverageLastHit, 0.665, 0.001);
            double costLastHit = listBestHitForEachAndEverySeed.get(18).getResultsFromEvaluateCost().getCost();
            assertEquals(costLastHit, 0.140, 0.001);
        }
        if (listBestHitForEachAndEverySeed.size() == 16) {

            float coverageLastHit = listBestHitForEachAndEverySeed.get(15).getResultsFromEvaluateCost().getCoverage();
            assertEquals(coverageLastHit, 0.665, 0.001);
            double costLastHit = listBestHitForEachAndEverySeed.get(15).getResultsFromEvaluateCost().getCost();
            assertEquals(costLastHit, 0.140, 0.001);
        }
*/
        if (listBestHitForEachAndEverySeed.size() == 20) {

            float coverageLastHit = listBestHitForEachAndEverySeed.get(19).getResultsFromEvaluateCost().getCoverage();
            assertEquals(coverageLastHit, 0.665, 0.001);
            double costLastHit = listBestHitForEachAndEverySeed.get(19).getResultsFromEvaluateCost().getCost();
            assertEquals(costLastHit, 0.139, 0.001);
        }
    }

}
