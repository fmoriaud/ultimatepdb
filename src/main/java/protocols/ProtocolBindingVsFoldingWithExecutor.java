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

import database.HitInSequenceDb;
import database.SequenceTools;
import multithread.CompareWithOneOnlyCallable;
import mystructure.MyChainIfc;
import parameters.AlgoParameters;
import shape.ShapeContainerIfc;
import shape.ShapeContainerWithPeptide;
import shapeBuilder.ShapeBuildingException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.logging.FileHandler;

public class ProtocolBindingVsFoldingWithExecutor {
    //-------------------------------------------------------------
    // Class members
    //-------------------------------------------------------------
    private final ShapeContainerIfc queryShape;
    private final List<ShapeContainerDefined> targets;
    private final AlgoParameters algoParameters;


    //-------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------
    public ProtocolBindingVsFoldingWithExecutor(ShapeContainerIfc queryShape, List<ShapeContainerDefined> targets, AlgoParameters algoParameters) {

        this.queryShape = queryShape;
        this.targets = targets;
        this.algoParameters = algoParameters;
    }


    // -------------------------------------------------------------------
    // Public && Interface method
    // -------------------------------------------------------------------
    public void run() throws ParsingConfigFileException {

        int consumersCount = algoParameters.getSHAPE_COMPARISON_THREAD_COUNT();
        final ExecutorService executorService = ProtocolTools.getExecutorService(consumersCount);
        int timeSecondsToWaitIfQueueIsFullBeforeAddingMore = 60;


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

                // ControllerLoger.logger.log(Level.INFO, "&&&&&& Added to Executor ");

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
    }
}