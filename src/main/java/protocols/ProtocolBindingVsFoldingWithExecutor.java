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
import java.util.concurrent.*;

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

        boolean minimizeAllIfTrueOrOnlyOneIfFalse = false;
        List<CompareWithOneOnlyCallable> callablesToLauch = ProtocolTools.getCallablesFromTargets(algoParameters, queryShape, targets, minimizeAllIfTrueOrOnlyOneIfFalse);
        List<Future<Boolean>> allFuture = ProtocolTools.submitToExecutorAndGetFutures(executorService, timeSecondsToWaitIfQueueIsFullBeforeAddingMore, callablesToLauch);

        int countOfFutureTrue = ProtocolTools.checkIfFutureAreDone(allFuture);
        executorService.shutdown();
        System.out.println("Program finished.");
        System.exit(0);
    }
}