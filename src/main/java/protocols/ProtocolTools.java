package protocols;

import genericBuffer.GenericBuffer;
import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import hits.Hit;
import hits.HitPeptideWithQueryPeptide;
import hits.HitTools;
import jmolgui.UltiJmol1462;
import math.ProcrustesAnalysisIfc;
import mystructure.EnumMyReaderBiojava;
import parameters.*;
import shape.ShapeContainerIfc;
import shapeCompare.ComparatorShapeContainerQueryVsAnyShapeContainer;
import shapeCompare.NullResultFromAComparisonException;
import shapeCompare.ProcrustesAnalysis;

import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Created by Fabrice on 31/10/16.
 */
public class ProtocolTools {


    public static void compareAndWriteToResultFolder(ShapeContainerIfc queryShape, ShapeContainerIfc targetShape, AlgoParameters algoParameters) {
        ComparatorShapeContainerQueryVsAnyShapeContainer comparatorShape = new ComparatorShapeContainerQueryVsAnyShapeContainer(queryShape, targetShape, algoParameters);
        List<Hit> listBestHitForEachAndEverySeed = null;

        try {
            listBestHitForEachAndEverySeed = comparatorShape.computeResults();
        } catch (NullResultFromAComparisonException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("listBestHitForEachAndEverySeed size in Tools = " + listBestHitForEachAndEverySeed.size());
        int hitRank = -1;
        A:
        for (Hit hit : listBestHitForEachAndEverySeed) {
            hitRank += 1;

            System.out.println("Minimizing ... " + hit.toString());
            try {
                HitTools.minimizeHitInQuery(hit, queryShape, targetShape, algoParameters);
            } catch (NullResultFromAComparisonException e) {
                e.printStackTrace();
            } catch (ExceptionInScoringUsingBioJavaJMolGUI exceptionInScoringUsingBioJavaJMolGUI) {
                exceptionInScoringUsingBioJavaJMolGUI.printStackTrace();
                continue A;
            }

            HitPeptideWithQueryPeptide hitPeptideWithQueryPeptide = (HitPeptideWithQueryPeptide) hit;
            String message = hit.toString() + " RmsdBackbone = " + hitPeptideWithQueryPeptide.getRmsdBackboneWhencomparingPeptideToPeptide() + " Rank = " + hitRank;
            ControllerLoger.logger.log(Level.INFO, message);
        }
    }



    public static AlgoParameters prepareAlgoParameters() throws ParsingConfigFileException {

        URL url = ProtocolBindingVsFolding.class.getClassLoader().getResource("ultimate.xml");
        AlgoParameters algoParameters = CommandLineTools.generateModifiedAlgoParameters(url.getPath(), EnumMyReaderBiojava.BioJava_MMCIFF);
        algoParameters.ultiJMolBuffer = new GenericBuffer<UltiJmol1462>(algoParameters.getSHAPE_COMPARISON_THREAD_COUNT());
        algoParameters.procrustesAnalysisBuffer = new GenericBuffer<ProcrustesAnalysisIfc>(algoParameters.getSHAPE_COMPARISON_THREAD_COUNT());

        for (int i = 0; i < algoParameters.getSHAPE_COMPARISON_THREAD_COUNT(); i++) {
            ProcrustesAnalysisIfc procrustesAnalysis = new ProcrustesAnalysis(algoParameters);
            try {
                algoParameters.procrustesAnalysisBuffer.put(procrustesAnalysis);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        for (int i = 0; i < algoParameters.getSHAPE_COMPARISON_THREAD_COUNT(); i++) {
            UltiJmol1462 ultiJMol = new UltiJmol1462();
            try {
                algoParameters.ultiJMolBuffer.put(ultiJMol);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return algoParameters;
    }




    public static ExecutorService getExecutorServiceForComparisons(int consumersCount) {
        int corePoolSize = 0; // no need to keep idle ones
        long keepAliveTime = 500000000; // no need to terminate if thread gets no job, that
        // could happen when searching database for a potetial hit, that could last as long
        // as the time to search the whole system
        int maxCountRunnableInBoundQueue = 10000; // 10000;

        ExecutorService threadPoolExecutor =
                new ThreadPoolExecutor(
                        corePoolSize,
                        consumersCount,
                        keepAliveTime,
                        TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<Runnable>(maxCountRunnableInBoundQueue)
                );

        return threadPoolExecutor;
    }

}
