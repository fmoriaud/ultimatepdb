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
package multithread;

import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import io.Tools;
import mystructure.ExceptionInMyStructurePackage;
import mystructure.ReadingStructurefileException;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.*;
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

        int consumersCount = algoParameters.getSHAPE_COMPARISON_THREAD_COUNT();
        final ExecutorService executorService = ProtocolTools.getExecutorService(consumersCount);
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
                for (Future<Boolean> future : allFuture) {
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


    /**
     * Callable built with an already built ShapeContainerIfc as query and a ShapecontainerDefined as Target.
     * This callable is meant for protocols comparing always the same query to any target.
     *
     * @throws ExceptionInScoringUsingBioJavaJMolGUI
     * @throws ReadingStructurefileException
     * @throws ExceptionInMyStructurePackage
     * @throws CommandLineException
     * @throws ParsingConfigFileException
     * @throws ShapeBuildingException
     * @throws IOException
     */
    @Test
    public void testCompareTwoKinaseLigandShape() throws ExceptionInScoringUsingBioJavaJMolGUI, ReadingStructurefileException, ExceptionInMyStructurePackage, CommandLineException, ParsingConfigFileException, ShapeBuildingException, IOException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        int initialCount = algoParameters.ultiJMolBuffer.getSize();

        final ExecutorService executorService = ProtocolTools.getExecutorService(1);
        int timeSecondsToWaitIfQueueIsFullBeforeAddingMore = 60;

        char[] fourLetterCode1bmk = "1bmk".toCharArray();
        char[] hetAtomsLigandId1bmk = "SB5".toCharArray();
        int occurrenceId = 1;
        ShapeContainerDefined shapeContainerDefined1bmk = new ShapecontainerDefinedByHetatm(fourLetterCode1bmk, algoParameters, hetAtomsLigandId1bmk, occurrenceId);
        ShapeContainerIfc shapeContainer1bmk = shapeContainerDefined1bmk.getShapecontainer();

        char[] fourLetterCode1a9u = "1a9u".toCharArray();
        char[] hetAtomsLigandId1a9u = "SB2".toCharArray();

        ShapeContainerDefined shapeContainerDefined1a9u = new ShapecontainerDefinedByHetatm(fourLetterCode1a9u, algoParameters, hetAtomsLigandId1a9u, occurrenceId);

        boolean minimizeAllIfTrueOrOnlyOneIfFalse = false;
        CompareWithOneOnlyCallable callableToLauch = new CompareWithOneOnlyCallable(minimizeAllIfTrueOrOnlyOneIfFalse, shapeContainer1bmk, shapeContainerDefined1a9u, algoParameters);

        Future<Boolean> future = executorService.submit(callableToLauch);

        boolean succes = false;
        boolean notFinished = true;
        while (true && notFinished) {

            try {
                Thread.sleep(100000);
                succes = future.get();
                notFinished = false;

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        assertTrue(succes == true);
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
