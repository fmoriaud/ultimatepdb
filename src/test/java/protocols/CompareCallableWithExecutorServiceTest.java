/*
Author:
      Fabrice Moriaud <fmoriaud@ultimatepdb.org>

  Copyright (c) 2016 Fabrice Moriaud

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
  */
package protocols;

import io.ReadTextFile;
import io.Tools;
import multithread.CompareWithOneOnlyCallable;
import org.junit.Test;
import parameters.AlgoParameters;
import shape.ShapeContainerIfc;
import shapeBuilder.ShapeBuildingException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.logging.FileHandler;

import static org.junit.Assert.assertTrue;

public class CompareCallableWithExecutorServiceTest {

    /**
     * Add that if ill defined it never finish
     *
     * @throws IOException
     * @throws ParsingConfigFileException
     */
    @Test
    public void runEightComparisonsWithFourThreads() throws IOException, ParsingConfigFileException {

        int threadCount = 4;
        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol(threadCount);

        /*
        File resultFile = new File(algoParameters.getPATH_TO_RESULT_FILES() + ControllerLoger.LOGGER_FILE_NAME);
        if (resultFile.exists()) {
            Files.delete(resultFile.toPath());
        }
*/
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

        startingRankId = 0;
        peptideLength = 5;
        ShapeContainerDefined shapecontainerDefined7 = new ShapecontainerDefinedBySegmentOfChain("1be9".toCharArray(), "B".toCharArray(), startingRankId, peptideLength, algoParameters);
        targets.add(shapecontainerDefined7);

        int consumersCount = algoParameters.getSHAPE_COMPARISON_THREAD_COUNT();
        final ExecutorService executorService = ProtocolTools.getExecutorService(consumersCount);
        int timeSecondsToWaitIfQueueIsFullBeforeAddingMore = 20;


        List<CompareWithOneOnlyCallable> callablesToLauch = new ArrayList<>();
        for (ShapeContainerDefined target : targets) {
            boolean minimizeAllIfTrueOrOnlyOneIfFalse = false;
            CompareWithOneOnlyCallable compare = new CompareWithOneOnlyCallable(minimizeAllIfTrueOrOnlyOneIfFalse, queryShape, target, algoParameters);
            callablesToLauch.add(compare);
        }

        int countCallabeSubmited = 0;
        List<Future<Boolean>> allFuture = new ArrayList<>();
        for (CompareWithOneOnlyCallable callableToLauch : callablesToLauch) {
            try {
                Future<Boolean> future = executorService.submit(callableToLauch);
                allFuture.add(future);
                countCallabeSubmited += 1;

            } catch (RejectedExecutionException e) {
                try {
                    Thread.sleep(timeSecondsToWaitIfQueueIsFullBeforeAddingMore * 1000);
                    continue;
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        System.out.println("Submited " + countCallabeSubmited);
        assertTrue(countCallabeSubmited == targets.size());

        int countOfFutureTrue = 0;
        boolean notFinished = true;
        while (true && notFinished) {
            countOfFutureTrue = 0;
            try {
                Thread.sleep(20000);
                for (Future<Boolean> future : allFuture) {
                    Boolean result = future.get();
                    //System.out.println("Future result = " + result);
                    if (result == true) {
                        countOfFutureTrue += 1;
                    }
                }
                notFinished = false;

            } catch (InterruptedException | ExecutionException e) {
                //e.printStackTrace();
            }
        }
        executorService.shutdown();
        System.out.println("Program finished.");
        assertTrue(countOfFutureTrue == targets.size());

        String resultFileContent = ReadTextFile.readTextFile(algoParameters.getPATH_TO_RESULT_FILES() + ControllerLoger.LOGGER_FILE_NAME);
        String[] lines = resultFileContent.split("\\n");
        int startlines = 4;
        int expectedHits = 3;
        int linesPerHit = 3;
        assertTrue(lines.length == startlines + expectedHits * linesPerHit);

        int finalCount = algoParameters.ultiJMolBuffer.getSize();

        System.out.println("normal defined   " + finalCount + "  " + initialCount);
        assertTrue(finalCount == initialCount);

        try {
            for (int i = 0; i < initialCount; i++) {
                algoParameters.ultiJMolBuffer.get().frame.dispose();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 0);
        fh.close();
    }


    @Test
    public void runThreeComparisonsWithOneThreadsOneIsIllDefined() throws IOException, ParsingConfigFileException {

        int threadCount = 1;
        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol(threadCount);

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

        int startingRankId = 0;
        int peptideLength = 5;
        ShapeContainerDefined shapecontainerDefined0 = new ShapecontainerDefinedBySegmentOfChain("1be9".toCharArray(), "B".toCharArray(), startingRankId, peptideLength, algoParameters);
        targets.add(shapecontainerDefined0);

        // ill defined chain X is not existing
        startingRankId = 0;
        peptideLength = 5;
        ShapeContainerDefined shapecontainerDefined1 = new ShapecontainerDefinedBySegmentOfChain("1be9".toCharArray(), "X".toCharArray(), startingRankId, peptideLength, algoParameters);
        targets.add(shapecontainerDefined1);

        startingRankId = 0;
        peptideLength = 5;
        ShapeContainerDefined shapecontainerDefined2 = new ShapecontainerDefinedBySegmentOfChain("1be9".toCharArray(), "B".toCharArray(), startingRankId, peptideLength, algoParameters);
        targets.add(shapecontainerDefined2);

        int consumersCount = algoParameters.getSHAPE_COMPARISON_THREAD_COUNT();
        final ExecutorService executorService = ProtocolTools.getExecutorService(consumersCount);
        int timeSecondsToWaitIfQueueIsFullBeforeAddingMore = 20;


        List<CompareWithOneOnlyCallable> callablesToLauch = new ArrayList<>();
        for (ShapeContainerDefined target : targets) {
            boolean minimizeAllIfTrueOrOnlyOneIfFalse = false;
            CompareWithOneOnlyCallable compare = new CompareWithOneOnlyCallable(minimizeAllIfTrueOrOnlyOneIfFalse, queryShape, target, algoParameters);
            callablesToLauch.add(compare);
        }

        int countCallabeSubmited = 0;
        List<Future<Boolean>> allFuture = new ArrayList<>();
        for (CompareWithOneOnlyCallable callableToLauch : callablesToLauch) {
            try {
                Future<Boolean> future = executorService.submit(callableToLauch);
                allFuture.add(future);
                countCallabeSubmited += 1;

            } catch (RejectedExecutionException e) {
                try {
                    Thread.sleep(timeSecondsToWaitIfQueueIsFullBeforeAddingMore * 1000);
                    continue;
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        System.out.println("Submited " + countCallabeSubmited);
        assertTrue(countCallabeSubmited == targets.size());

        int countOfFutureTrue = 0;
        boolean notFinished = true;
        while (true && notFinished) {
            countOfFutureTrue = 0;
            try {
                Thread.sleep(20000);
                for (Future<Boolean> future : allFuture) {
                    Boolean result = future.get();
                    //System.out.println("Future result = " + result);
                    if (result == true) {
                        countOfFutureTrue += 1;
                    }
                }
                notFinished = false;

            } catch (InterruptedException | ExecutionException e) {
                //e.printStackTrace();
            }
        }
        executorService.shutdown();
        System.out.println("Program finished.");
        assertTrue(countOfFutureTrue == targets.size() - 1);

        String resultFileContent = ReadTextFile.readTextFile(algoParameters.getPATH_TO_RESULT_FILES() + ControllerLoger.LOGGER_FILE_NAME);
        String[] lines = resultFileContent.split("\\n");
        int startlines = 4;
        int expectedHits = 2;
        int linesPerHit = 3;
        assertTrue(lines.length == startlines + expectedHits * linesPerHit);

        int finalCount = algoParameters.ultiJMolBuffer.getSize();
        System.out.println("ill defined   " + finalCount + "  " + initialCount);
        assertTrue(finalCount == initialCount);

        try {
            for (int i = 0; i < initialCount; i++) {
                algoParameters.ultiJMolBuffer.get().frame.dispose();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 0);
        fh.close();
    }

}
