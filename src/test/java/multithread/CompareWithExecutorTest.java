package multithread;

import convertformat.AdapterBioJavaStructure;
import convertformat.ExceptionInConvertFormat;
import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import io.BiojavaReader;
import io.ExceptionInIOPackage;
import io.IOTools;
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
import java.util.concurrent.*;
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
        final ExecutorService executorService = getExecutorServiceForComparisons(consumersCount);
        int timeSecondsToWaitIfQueueIsFullBeforeAddingMore = 60;

        ShapeContainerIfc queryShape = generateQueryShape(algoParameters);
        // prepare a few compareRunnable
        List<ShapeContainerDefined> targets = new ArrayList<>();
        char[] chainId1 = "C".toCharArray();
        char[] fourLetterCode1 = "2yjd".toCharArray();
        ShapeContainerDefined shapeContainerDefined1 = new ShapecontainerDefinedByWholeChain(fourLetterCode1, chainId1, algoParameters);
        targets.add(shapeContainerDefined1);

        char[] chainId2 = "X".toCharArray();
        char[] fourLetterCode2 = "2ce8".toCharArray();
        ShapeContainerDefined shapeContainerDefined2 = new ShapecontainerDefinedByWholeChain(fourLetterCode2, chainId2, algoParameters);
        targets.add(shapeContainerDefined2);
        /*
        targets.add(shapeContainerDefined1);
        targets.add(shapeContainerDefined2);
        targets.add(shapeContainerDefined1);
        targets.add(shapeContainerDefined2);
        targets.add(shapeContainerDefined1);
        targets.add(shapeContainerDefined2);
*/
        List<CompareOneOnlyRunnable> runnablesToLauch = new ArrayList<>();
        for (ShapeContainerDefined target : targets) {
            //ShapecontainerDefinedByWholeChain targetWholechain = (ShapecontainerDefinedByWholeChain) target;
            //MyStructureIfc myStructureTarget = IOTools.getMyStructureIfc(algoParameters, targetWholechain.getFourLetterCode());
            CompareOneOnlyRunnable compare = new CompareOneOnlyRunnable(queryShape, target, algoParameters);
            runnablesToLauch.add(compare);
        }

        for (CompareOneOnlyRunnable runnableToLauch : runnablesToLauch) {
            try {

                // Problem is the executor service waits to finish the task before adding a new one
                executorService.execute(runnableToLauch);
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

        /*
        executorService.shutdown();
        try {
            executorService.awaitTermination(5, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */

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



    private static ExecutorService getExecutorServiceForComparisons(int consumersCount) {
        int corePoolSize = 0; // no need to keep idle ones
        long keepAliveTime = 500000000; // no need to terminate if thread gets no job, that
        // could happen when searching database for a potetial hit, that could last as long
        // as the time to search the whole system
        int maxCountRunnableInBoundQueue = 10000; // 10000;

        ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(1);

        /*
                new ThreadPoolExecutor(
                        corePoolSize,
                        consumersCount,
                        keepAliveTime,
                        TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<Runnable>(maxCountRunnableInBoundQueue)
                );
*/
        return threadPoolExecutor;
    }
}
