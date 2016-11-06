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
import java.util.concurrent.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 02/11/16.
 */
public class CompareWithExecutorTest {


    @Test
    public void testCompareSeveral() throws ExceptionInScoringUsingBioJavaJMolGUI, ReadingStructurefileException, ExceptionInMyStructurePackage, CommandLineException, ParsingConfigFileException, ShapeBuildingException, IOException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        int initialCount = algoParameters.ultiJMolBuffer.getSize();

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

        char[] chainId1 = "C".toCharArray();
        char[] fourLetterCode1 = "2yjd".toCharArray();
        ShapeContainerDefined queryShapeDefined = new ShapecontainerDefinedByWholeChain(fourLetterCode1, chainId1, algoParameters);
        ShapeContainerIfc queryShape = queryShapeDefined.getShapecontainer();

        // prepare a few compareRunnable
        List<ShapeContainerDefined> targets = new ArrayList<>();

        chainId1 = "C".toCharArray();
        fourLetterCode1 = "2yjd".toCharArray();
        ShapeContainerDefined shapeContainerDefined1 = new ShapecontainerDefinedByWholeChain(fourLetterCode1, chainId1, algoParameters);


        //targets.add(shapeContainerDefined1);

        char[] chainId2 = "X".toCharArray();
        char[] fourLetterCode2 = "2ce8".toCharArray();
        ShapeContainerDefined shapeContainerDefined2 = new ShapecontainerDefinedByWholeChain(fourLetterCode2, chainId2, algoParameters);
        targets.add(shapeContainerDefined1);
        targets.add(shapeContainerDefined1);
        /*
        targets.add(shapeContainerDefined1);
        targets.add(shapeContainerDefined2);
        targets.add(shapeContainerDefined1);
        targets.add(shapeContainerDefined2);
        targets.add(shapeContainerDefined1);
        targets.add(shapeContainerDefined2);
*/
        List<CompareWithOneOnlyCallable> callablesToLauch = new ArrayList<>();
        for (ShapeContainerDefined target : targets) {
            //ShapecontainerDefinedByWholeChain targetWholechain = (ShapecontainerDefinedByWholeChain) target;
            //MyStructureIfc myStructureTarget = IOTools.getMyStructureIfc(algoParameters, targetWholechain.getFourLetterCode());
            boolean minimizeAllIfTrueOrOnlyOneIfFalse = false;
            CompareWithOneOnlyCallable compare = new CompareWithOneOnlyCallable(minimizeAllIfTrueOrOnlyOneIfFalse, queryShape, target, algoParameters);
            callablesToLauch.add(compare);
        }

        List<Future<Boolean>> allFuture = new ArrayList<>();
        for (CompareWithOneOnlyCallable callableToLauch : callablesToLauch) {
            try {

                Future<Boolean> future = executorService.submit(callableToLauch);
                allFuture.add(future);

                ControllerLoger.logger.log(Level.INFO, "&&&&&& Added to Executor ");

            } catch (RejectedExecutionException e) {

                try {
                    Thread.sleep(timeSecondsToWaitIfQueueIsFullBeforeAddingMore * 1000);
                    continue;
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }

        boolean notFinished = true;
        while (true && notFinished) {

            try {
                Thread.sleep(100000);
                for (Future<Boolean> future: allFuture){
                    future.get();
                }
                notFinished = false;

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        executorService.shutdown();

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
