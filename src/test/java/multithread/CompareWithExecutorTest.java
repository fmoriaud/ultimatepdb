package multithread;

import convertformat.AdapterBioJavaStructure;
import convertformat.ExceptionInConvertFormat;
import hits.ExceptionInScoringUsingBioJavaJMolGUI;
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
import shapeBuilder.EnumShapeReductor;
import shapeBuilder.ShapeBuildingException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.logging.FileHandler;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 02/11/16.
 */
public class CompareWithExecutorTest {


    @Test
    public void testCompareSeveral() throws ExceptionInScoringUsingBioJavaJMolGUI, ReadingStructurefileException, ExceptionInMyStructurePackage, CommandLineException, ParsingConfigFileException, ShapeBuildingException, IOException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();

        FileHandler fh = null;
        try {
            fh = new FileHandler(algoParameters.getPATH_TO_RESULT_FILES() + "log_Project.txt");
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        fh.setFormatter(new OptimizerFormater());
        ControllerLoger.logger.addHandler(fh);

        int consumersCount = algoParameters.getSHAPE_COMPARISON_THREAD_COUNT();
        final ExecutorService executorService = ProtocolTools.getExecutorServiceForComparisons(consumersCount);
        int timeSecondsToWaitIfQueueIsFullBeforeAddingMore = 60;

        ShapeContainerIfc queryShape = generateQueryShape(algoParameters);
        // prepare a few compareRunnable
        List<ShapeContainerDefined> targets = new ArrayList<>();
        char[] chainId = "C".toCharArray();
        char[] fourLetterCode = "2yjd".toCharArray();

        ShapeContainerDefined shapeContainerDefined = new ShapecontainerDefinedByWholeChain(fourLetterCode, chainId, algoParameters);
        for (int i=0; i<2; i++){
            targets.add(shapeContainerDefined);
        }

        for (ShapeContainerDefined target : targets) {

            CompareOneOnlyRunnable compare = new CompareOneOnlyRunnable(queryShape, target, algoParameters);

            try {
                executorService.execute(compare);
            } catch (RejectedExecutionException e) {

                try {
                    Thread.sleep(timeSecondsToWaitIfQueueIsFullBeforeAddingMore * 1000);
                    continue;
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }

        while(true){
            try {
                Thread.sleep(100000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }


    private ShapeContainerIfc generateQueryShape(AlgoParameters algoParameters) throws IOException, ParsingConfigFileException {

        char[] chainId = "C".toCharArray();


        String fourLetterCode = "2yjd";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure, EnumMyReaderBiojava.BioJava_MMCIFF);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
            assertTrue(false);
        }

        ShapeContainerIfc shapecontainer = null;
        try {
            shapecontainer = ShapeContainerFactory.getShapeAroundAChain(EnumShapeReductor.CLUSTERING, mystructure, algoParameters, chainId);
        } catch (ShapeBuildingException e) {
            e.printStackTrace();
        }

        return shapecontainer;
    }
}
