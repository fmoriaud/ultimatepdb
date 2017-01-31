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

import multithread.CompareWithOneOnlyCallable;
import parameters.AlgoParameters;
import shape.ShapeContainerIfc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

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


        final List<CompareWithOneOnlyCallable> callablesToLauch = new ArrayList<>();
        for (ShapeContainerDefined target : targets) {
            boolean minimizeAllIfTrueOrOnlyOneIfFalse = false;
            final CompareWithOneOnlyCallable compare = new CompareWithOneOnlyCallable(minimizeAllIfTrueOrOnlyOneIfFalse, queryShape, target, algoParameters);
            callablesToLauch.add(compare);
        }

        final List<Future<Boolean>> allFuture = new ArrayList<>();
        for (CompareWithOneOnlyCallable callableToLauch : callablesToLauch) {
            try {
                final Future<Boolean> future = executorService.submit(callableToLauch);
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
        executorService.shutdown();

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

        System.out.println("Program finished.");
        System.exit(0);
    }
}