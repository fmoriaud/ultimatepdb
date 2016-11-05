package protocols;

import database.HitInSequenceDb;
import database.SequenceTools;
import multithread.CompareOneOnlyRunnable;
import parameters.AlgoParameters;
import shape.ShapeContainerIfc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.logging.FileHandler;

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

        ShapeContainerDefined query = new ShapecontainerDefinedByWholeChain("4c2c".toCharArray(), "C".toCharArray(), algoParameters);
        List<ShapeContainerDefined> targets = buildTargets();

        ProtocolBindingVsFoldingWithExecutor protocol = new ProtocolBindingVsFoldingWithExecutor(query, targets);
        // SEQRES   1 X    9  MET PHE SER ILE ASP ASN ILE LEU ALA

        protocol.run();
    }


    public void run() throws ParsingConfigFileException {

        int consumersCount = algoParameters.getSHAPE_COMPARISON_THREAD_COUNT();
        final ExecutorService executorService = ProtocolTools.getExecutorServiceForComparisons(consumersCount);
        int timeSecondsToWaitIfQueueIsFullBeforeAddingMore = 60;

        ShapeContainerIfc queryShape = query.getShapecontainer();

        for (ShapeContainerDefined target : targets) {

            boolean minimizeAllIfTrueOrOnlyOneIfFalse = true;
            CompareOneOnlyRunnable compare = new CompareOneOnlyRunnable(minimizeAllIfTrueOrOnlyOneIfFalse, queryShape, target, algoParameters);

            try {
                executorService.execute(compare);
            } catch (RejectedExecutionException e) {

                try {
                    Thread.sleep(timeSecondsToWaitIfQueueIsFullBeforeAddingMore * 1000);
                    continue;
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }

        while(true){
            try {
                Thread.sleep(100000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        //executorService.shutdown();

    }


    private static List<ShapeContainerDefined> buildTargets() {

        List<ShapeContainerDefined> targets = new ArrayList<>();

        String sequenceToFind = "ALAVALPROALA";
        int peptideLength = sequenceToFind.length() / 3;

        boolean useSimilarSequences = false;
        List<HitInSequenceDb> hitsInDatabase = SequenceTools.find(peptideLength, 1000, sequenceToFind, useSimilarSequences);
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
