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
        try {
            List<Hit> listBestHitForEachAndEverySeed = comparatorShape.computeResults();
            for (Hit hit : listBestHitForEachAndEverySeed) {
                System.out.println(hit);
                System.out.println();
            }

        } catch (NullResultFromAComparisonException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        System.out.println();

    }

}
