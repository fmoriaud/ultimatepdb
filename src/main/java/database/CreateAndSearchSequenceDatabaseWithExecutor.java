package database;

import io.IOTools;
import multithread.CompareWithOneOnlyCallable;
import multithread.StoreInSequenceDbPDBFileCallable;
import parameters.AlgoParameters;
import protocols.ControllerLoger;
import protocols.ParsingConfigFileException;
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
import java.util.logging.Level;

/**
 * Created by Fabrice on 06/11/16.
 */
public class CreateAndSearchSequenceDatabaseWithExecutor {

    private Connection connexion;
    private AlgoParameters algoParameters;

    public CreateAndSearchSequenceDatabaseWithExecutor(AlgoParameters algoParameters) {

        this.connexion = DatabaseTools.getConnection();
        this.algoParameters = algoParameters;
    }


    public void createDatabase() {

        DatabaseTools.createDBandTableSequence(connexion);
        updateDatabase();
    }

    public void updateDatabase() {

        Map<String, List<Path>> indexPDBFileInFolder = IOTools.indexPDBFileInFolder(algoParameters.getPATH_TO_REMEDIATED_PDB_MMCIF_FOLDER());

        int consumersCount = algoParameters.getSHAPE_COMPARISON_THREAD_COUNT();
        final ExecutorService executorService = ProtocolTools.getExecutorServiceForComparisons(consumersCount);
        int timeSecondsToWaitIfQueueIsFullBeforeAddingMore = 60;

        List<StoreInSequenceDbPDBFileCallable> callablesToLauch = new ArrayList<>();
        for (Map.Entry<String, List<Path>> entry : indexPDBFileInFolder.entrySet()) {
            String fourLetterCode = entry.getKey();
            DoMyDbTaskIfc doMyDbTaskIfc = new AddandNotOverrideExistingTask(algoParameters, fourLetterCode);
            StoreInSequenceDbPDBFileCallable callable = new StoreInSequenceDbPDBFileCallable(doMyDbTaskIfc, connexion);
            callablesToLauch.add(callable);
        }

        List<Future<Boolean>> allFuture = new ArrayList<>();
        for (StoreInSequenceDbPDBFileCallable callableToLauch : callablesToLauch) {

            try {

                Future<Boolean> future = executorService.submit(callableToLauch);
                if (future == null){
                    System.out.println();
                }
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


    public String returnSequenceInDbifFourLetterCodeAndChainfoundInDatabase(String fourLetterCode, String chainName) {

        return DatabaseTools.returnSequenceInDbifFourLetterCodeAndChainfoundInDatabase(connexion, fourLetterCode, chainName);
    }

    public void shutdownDb() {

        DatabaseTools.shutdown();
    }
}
