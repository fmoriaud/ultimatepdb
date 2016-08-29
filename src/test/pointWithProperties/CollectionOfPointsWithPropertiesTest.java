import genericBuffer.GenericBuffer;
import genericBuffer.MyStructureBuffer;
import math.ProcrustesAnalysisIfc;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.CommandLineException;
import protocols.CommandLineTools;
import protocols.ParsingConfigFileException;
import protocols.ProtocolToolsToHandleInputFilesAndShapeComparisons;
import shape.ShapeContainerWithPeptide;
import shapeBuilder.ShapeBuilder;
import shapeBuilder.ShapeBuildingException;
import shapeBuilder.ShapeBuildingTools;
import shapeCompare.ProcrustesAnalysis;
import structure.EnumMyReaderBiojava;
import structure.MyStructureIfc;
import ultiJmol.UltiJMol;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 29/08/16.
 */
public class CollectionOfPointsWithPropertiesTest {

    @Test
    public void testMakeACollectionOfPointsWithProperties() throws CommandLineException, ParsingConfigFileException, ShapeBuildingException, InterruptedException{


        EnumMyReaderBiojava enumMyReaderBiojava = EnumMyReaderBiojava.BioJava_MMCIFF;
        String[] args = new String[1];
        URL url = IOToolsTest.class.getClassLoader().getResource("ultimate.xml");
        Path path = null;
        try {
            path = Paths.get(url.toURI());
        } catch (URISyntaxException e1) {
            assertTrue(false);
        }
        args[0] = path.toString();
        AlgoParameters algoParameters = CommandLineTools.analyzeArgs(args, enumMyReaderBiojava);
        algoParameters.ultiJMolBuffer = new GenericBuffer<UltiJMol>(algoParameters.getSHAPE_COMPARISON_THREAD_COUNT());
        algoParameters.procrustesAnalysisBuffer = new GenericBuffer<ProcrustesAnalysisIfc>(algoParameters.getSHAPE_COMPARISON_THREAD_COUNT());
        algoParameters.myStructureBuffer = new MyStructureBuffer(algoParameters.getSHAPE_COMPARISON_THREAD_COUNT() * 2);
        algoParameters.listOfPDBFiles = ProtocolToolsToHandleInputFilesAndShapeComparisons.makeAListOfInputPDBFilesRecursivelyFromInputControllerFolder(algoParameters, enumMyReaderBiojava);
        for (int i=0; i<algoParameters.getSHAPE_COMPARISON_THREAD_COUNT(); i++){
            ProcrustesAnalysisIfc procrustesAnalysis = new ProcrustesAnalysis(algoParameters);
            try {
                algoParameters.procrustesAnalysisBuffer.put(procrustesAnalysis);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        UltiJMol ultiJMol = new UltiJMol();
        algoParameters.ultiJMolBuffer.put(ultiJMol);

        MyStructureIfc myStructureGlobalBrut = ShapeBuildingTools.getMyStructure("1JK3".toCharArray(), algoParameters, enumMyReaderBiojava);

        ShapeBuilder shapeBuilder = new ShapeBuilder(myStructureGlobalBrut, algoParameters);
        ShapeContainerWithPeptide shapeContainerPeptide = shapeBuilder.getShapeAroundASegmentOfChainUsingStartingMyMonomerPositionInChain("A".toCharArray(), 0, 5);

        System.out.println();
    }

}
