package protocols;

import io.Tools;
import multithread.CompareWithOneOnlyCallable;
import org.junit.Test;
import parameters.AlgoParameters;
import shape.ShapeContainerIfc;
import shapeBuilder.ShapeBuildingException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.logging.FileHandler;

import static org.junit.Assert.assertTrue;

public class SpecificCaseTest {

    @Test
    public void generateModifiedAlgoParametersTest() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();

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

        int initialCount = algoParameters.ultiJMolBuffer.getSize();

        String fourLetterCodeQuery = "1be9";
        char[] chainIdQuery = "B".toCharArray();
        ShapeContainerDefined shapeContainerbuilder = new ShapecontainerDefinedByWholeChain(fourLetterCodeQuery.toCharArray(), chainIdQuery, algoParameters);
        ShapeContainerIfc queryShape = null;
        try {
            queryShape = shapeContainerbuilder.getShapecontainer();
        } catch (ShapeBuildingException e) {
            assertTrue(false);
        }

        List<ShapeContainerDefined> targets = new ArrayList<>();

        int startingRankId = 224;
        int peptideLength = 5;
        ShapeContainerDefined shapecontainerDefined0 = new ShapecontainerDefinedBySegmentOfChain("5cew".toCharArray(), "A".toCharArray(), startingRankId, peptideLength, algoParameters);
        targets.add(shapecontainerDefined0);

        startingRankId = 111;
        peptideLength = 5;
        ShapeContainerDefined shapecontainerDefined1 = new ShapecontainerDefinedBySegmentOfChain("3cfd".toCharArray(), "H".toCharArray(), startingRankId, peptideLength, algoParameters);
        targets.add(shapecontainerDefined1);

        startingRankId = 111;
        peptideLength = 5;
        ShapeContainerDefined shapecontainerDefined2 = new ShapecontainerDefinedBySegmentOfChain("3cfd".toCharArray(), "B".toCharArray(), startingRankId, peptideLength, algoParameters);
        targets.add(shapecontainerDefined2);

        startingRankId = 111;
        peptideLength = 5;
        ShapeContainerDefined shapecontainerDefined3 = new ShapecontainerDefinedBySegmentOfChain("3cfe".toCharArray(), "H".toCharArray(), startingRankId, peptideLength, algoParameters);
        targets.add(shapecontainerDefined3);

        startingRankId = 111;
        peptideLength = 5;
        ShapeContainerDefined shapecontainerDefined4 = new ShapecontainerDefinedBySegmentOfChain("3cfe".toCharArray(), "B".toCharArray(), startingRankId, peptideLength, algoParameters);
        targets.add(shapecontainerDefined4);

        startingRankId = 72;
        peptideLength = 5;
        ShapeContainerDefined shapecontainerDefined5 = new ShapecontainerDefinedBySegmentOfChain("3cfy".toCharArray(), "A".toCharArray(), startingRankId, peptideLength, algoParameters);
        targets.add(shapecontainerDefined5);


        startingRankId = 109;
        ShapeContainerDefined shapecontainerDefined6 = new ShapecontainerDefinedBySegmentOfChain("3cfy".toCharArray(), "A".toCharArray(), startingRankId, peptideLength, algoParameters);
        targets.add(shapecontainerDefined6);

        int consumersCount = algoParameters.getSHAPE_COMPARISON_THREAD_COUNT();
        final ExecutorService executorService = ProtocolTools.getExecutorService(consumersCount);
        int timeSecondsToWaitIfQueueIsFullBeforeAddingMore = 60;


        List<CompareWithOneOnlyCallable> callablesToLauch = new ArrayList<>();
        for (ShapeContainerDefined target : targets) {
            boolean minimizeAllIfTrueOrOnlyOneIfFalse = false;
            CompareWithOneOnlyCallable compare = new CompareWithOneOnlyCallable(minimizeAllIfTrueOrOnlyOneIfFalse, queryShape, target, algoParameters);
            callablesToLauch.add(compare);
        }

        List<Future<Boolean>> allFuture = new ArrayList<>();
        for (CompareWithOneOnlyCallable callableToLauch : callablesToLauch) {
            try {
                Future<Boolean> future = executorService.submit(callableToLauch);
                allFuture.add(future);

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
                for (Future<Boolean> future : allFuture) {
                    future.get();
                }
                notFinished = false;

            } catch (InterruptedException | ExecutionException e) {
                //e.printStackTrace();
            }
        }
        executorService.shutdown();
        System.out.println("Program finished.");

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
