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
package database;

import io.IOTools;
import io.MMcifFileInfos;
import multithread.StoreInSequenceDbPDBFileCallable;
import parameters.AlgoParameters;
import protocols.ProtocolTools;

import java.nio.file.Path;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

public class CreateAndSearchSequenceDatabaseWithExecutor implements CreateAndSearchSequenceDatabaseIfc {
    //-------------------------------------------------------------
    // Class variables
    //-------------------------------------------------------------
    private Connection connexion;
    private AlgoParameters algoParameters;
    private String tableName;
    private String tableFailureName;

    //-------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------
    public CreateAndSearchSequenceDatabaseWithExecutor(AlgoParameters algoParameters, String tableName, String tableFailureName) {

        this.connexion = HashTablesTools.getConnection();
        this.algoParameters = algoParameters;
        this.tableName = tableName;
        this.tableFailureName = tableFailureName;
    }


    // -------------------------------------------------------------------
    // Public & Interface Methods
    // -------------------------------------------------------------------
    @Override
    public void createDatabase() {

        HashTablesTools.createTables(connexion, tableName, tableFailureName);
        updateOveridingExistingDatabase(true);
    }


    @Override
    public void updateDatabase(String pathToMMcifFiles) {

        HashTablesTools.addFilesToDb(connexion, pathToMMcifFiles, algoParameters, tableName, tableFailureName);
    }


    @Override
    public String returnSequenceInDbifFourLetterCodeAndChainfoundInDatabase(String fourLetterCode, String chainName) {

        return HashTablesTools.returnSequenceInDbifFourLetterCodeAndChainfoundInDatabase(connexion, fourLetterCode, chainName, tableName);
    }


    @Override
    public void shutdownDb() {

        HashTablesTools.shutdown();
    }


    //-------------------------------------------------------------
    // Implementation
    //-------------------------------------------------------------
    private void updateOveridingExistingDatabase(boolean override) {

        Map<String, List<MMcifFileInfos>> indexPDBFileInFolder = IOTools.indexPDBFileInFolder(algoParameters.getPATH_TO_REMEDIATED_PDB_MMCIF_FOLDER());
        algoParameters.setIndexPDBFileInFolder(indexPDBFileInFolder);
        int consumersCount = algoParameters.getSHAPE_COMPARISON_THREAD_COUNT();
        final ExecutorService executorService = ProtocolTools.getExecutorService(consumersCount);
        int timeSecondsToWaitIfQueueIsFullBeforeAddingMore = 60;

        List<StoreInSequenceDbPDBFileCallable> callablesToLauch = new ArrayList<>();
        for (Map.Entry<String, List<MMcifFileInfos>> entry : indexPDBFileInFolder.entrySet()) {

            for (MMcifFileInfos fileInfos : entry.getValue()) {
                DoMyDbTaskIfc doMyDbTaskIfc = new AddInSequenceDB(algoParameters, tableName, tableFailureName);

                StoreInSequenceDbPDBFileCallable callable = new StoreInSequenceDbPDBFileCallable(doMyDbTaskIfc, connexion, fileInfos.getPathToFile());
                callablesToLauch.add(callable);
            }
        }

        List<Future<Boolean>> allFuture = new ArrayList<>();
        for (StoreInSequenceDbPDBFileCallable callableToLauch : callablesToLauch) {

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
                e.printStackTrace();
            }
        }
        executorService.shutdown();
    }
}
