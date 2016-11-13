package protocols;

import database.HitInSequenceDb;
import database.SequenceTools;
import multithread.CompareOneOnlyRunnable;
import multithread.CompareWithOneOnlyCallable;
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
import java.util.logging.Level;

/**
 * Created by Fabrice on 31/10/16.
 */
public class ProtocolBindingVsFoldingWithExecutor {
    //-------------------------------------------------------------
    // Class members
    //-------------------------------------------------------------
    private ShapeContainerDefined query;
    private List<ShapeContainerDefined> targets;
    private static AlgoParameters algoParameters;


    //-------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------
    public ProtocolBindingVsFoldingWithExecutor(ShapeContainerDefined query, List<ShapeContainerDefined> targets) {

        this.query = query;
        this.targets = targets;
    }


    // -------------------------------------------------------------------
    // Public && Interface method
    // -------------------------------------------------------------------
    public static void main(String[] args) throws ParsingConfigFileException {

        algoParameters = ProtocolTools.prepareAlgoParameters();

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

        ShapeContainerDefined query = new ShapecontainerDefinedByWholeChain("1be9".toCharArray(), "B".toCharArray(), algoParameters);
        List<ShapeContainerDefined> targets = buildTargets();

        ProtocolBindingVsFoldingWithExecutor protocol = new ProtocolBindingVsFoldingWithExecutor(query, targets);
        // SEQRES   1 X    9  MET PHE SER ILE ASP ASN ILE LEU ALA

        protocol.run();
    }


    public void run() throws ParsingConfigFileException {

        int consumersCount = algoParameters.getSHAPE_COMPARISON_THREAD_COUNT();
        final ExecutorService executorService = ProtocolTools.getExecutorServiceForComparisons(consumersCount);
        int timeSecondsToWaitIfQueueIsFullBeforeAddingMore = 60;

        ShapeContainerIfc queryShape = null;
        try {
            queryShape = query.getShapecontainer();
        } catch (ShapeBuildingException e) {
            e.printStackTrace();
            System.exit(0);
        }

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


    }


    private static List<ShapeContainerDefined> buildTargets() {

        List<ShapeContainerDefined> targets = new ArrayList<>();

        String sequenceToFind = "LYSGLNTHRSERVAL";
        int peptideLength = sequenceToFind.length() / 3;

        boolean useSimilarSequences = false;
        List<HitInSequenceDb> hitsInDatabase = SequenceTools.find(SequenceTools.tableName, peptideLength, 1000, sequenceToFind, useSimilarSequences);
        System.out.println("Found " + hitsInDatabase.size() + "  sequence hits in the Sequence Database");

        String fourLetterCodeTarget;
        String chainIdFromDB;

        for (HitInSequenceDb hitInSequenceDb : hitsInDatabase) {

            fourLetterCodeTarget = hitInSequenceDb.getFourLetterCode();
            chainIdFromDB = hitInSequenceDb.getChainIdFromDB();
            List<Integer> listRankIds = hitInSequenceDb.getListRankIds();

            for (int i = 0; i < listRankIds.size(); i++) {

                Integer startingRankId = listRankIds.get(i);
                ShapeContainerDefined target = new ShapecontainerDefinedBySegmentOfChain(fourLetterCodeTarget.toCharArray(), chainIdFromDB.toCharArray(), startingRankId, peptideLength, algoParameters);
                targets.add(target);

            }
        }
        return targets;
    }

}
