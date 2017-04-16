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

import convertformat.AdapterBioJavaStructure;
import convertformat.ExceptionInConvertFormat;
import database.HitInSequenceDb;
import genericBuffer.GenericBuffer;
import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import hits.Hit;
import hits.HitPeptideWithQueryPeptide;
import hits.HitTools;
import io.BiojavaReader;
import io.ExceptionInIOPackage;
import io.IOTools;
import jmolgui.UltiJmol1462;
import math.ProcrustesAnalysisIfc;
import multithread.CompareWithOneOnlyCallable;
import mystructure.EnumMyReaderBiojava;
import mystructure.ExceptionInMyStructurePackage;
import mystructure.MyStructureIfc;
import mystructure.ReadingStructurefileException;
import org.apache.commons.math3.util.Pair;
import org.biojava.nbio.structure.Structure;
import parameters.AlgoParameters;
import shape.ShapeContainerIfc;
import shapeBuilder.ShapeBuildingException;
import shapeCompare.CompareCompleteCheck;
import shapeCompare.NullResultFromAComparisonException;
import shapeCompare.ProcrustesAnalysis;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Level;

public class ProtocolTools {
    // -------------------------------------------------------------------
    // Static Methods
    // -------------------------------------------------------------------
    public static List<CompareWithOneOnlyCallable> getCallablesFromTargets(AlgoParameters algoParameters, ShapeContainerIfc queryShape, List<ShapeContainerDefined> targets, boolean minimizeAllIfTrueOrOnlyOneIfFalse) {
        List<CompareWithOneOnlyCallable> callablesToLauch = new ArrayList<>();
        for (ShapeContainerDefined target : targets) {
            CompareWithOneOnlyCallable compare = new CompareWithOneOnlyCallable(minimizeAllIfTrueOrOnlyOneIfFalse, queryShape, target, algoParameters);
            callablesToLauch.add(compare);
        }
        return callablesToLauch;
    }


    public static List<Future<Boolean>> submitToExecutorAndGetFutures(ExecutorService executorService, int timeSecondsToWaitIfQueueIsFullBeforeAddingMore, List<CompareWithOneOnlyCallable> callablesToLauch) {

        List<Future<Boolean>> allFuture = new ArrayList<>();
        Iterator<CompareWithOneOnlyCallable> it = callablesToLauch.iterator();
        while (it.hasNext()) {
            CompareWithOneOnlyCallable callableToLauch = it.next();
            try {
                Future<Boolean> future = executorService.submit(callableToLauch);
                allFuture.add(future);
                it.remove(); // to release memory as I dont need them anymore
            } catch (RejectedExecutionException e) {
                try {
                    Thread.sleep(timeSecondsToWaitIfQueueIsFullBeforeAddingMore * 1000);
                    continue;
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return allFuture;
    }


    public static int checkIfFutureAreDone(List<Future<Boolean>> allFuture) {

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
        return countOfFutureTrue;
    }


    public static int executeComparisons(ShapeContainerIfc queryShape, int peptideLength, List<HitInSequenceDb> hitsInDatabase, AlgoParameters algoParameters) {
        String fourLetterCodeTarget;
        String chainIdFromDB;

        int comparisonsDoneCount = 0;
        A:
        for (HitInSequenceDb hitInSequenceDb : hitsInDatabase) {

            fourLetterCodeTarget = hitInSequenceDb.getFourLetterCode();
            chainIdFromDB = hitInSequenceDb.getChainIdFromDB();
            List<Integer> listRankIds = hitInSequenceDb.getListRankIds();

            Pair<String, MyStructureIfc> pairPathMyStructure = IOTools.getMyStructureIfc(algoParameters, fourLetterCodeTarget.toCharArray());

            char[] chainId = chainIdFromDB.toCharArray();
            B:
            for (int i = 0; i < listRankIds.size(); i++) {

                Integer matchingRankId = listRankIds.get(i);

                ShapeContainerDefined shapeContainerDefined = new ShapecontainerDefinedBySegmentOfChain(fourLetterCodeTarget.toLowerCase().toCharArray(), chainId, matchingRankId, peptideLength, algoParameters);

                ShapeContainerIfc targetShape = null;
                try {
                    targetShape = shapeContainerDefined.getShapecontainer(pairPathMyStructure.getValue());

                } catch (ShapeBuildingException e) {
                    continue B;
                }
                System.out.println(fourLetterCodeTarget + " " + chainIdFromDB + " " + matchingRankId + " " + peptideLength + " : ");

                boolean minimizeAllIfTrueOrOnlyOneIfFalse = true;
                try {
                    ProtocolTools.compareCompleteCheckAndWriteToResultFolder(minimizeAllIfTrueOrOnlyOneIfFalse, queryShape, targetShape, algoParameters);
                    comparisonsDoneCount += 1;
                } catch (Exception e) {
                    continue B;
                }
            }
        }
        return comparisonsDoneCount;
    }


    public static String makeSequenceString(List<char[]> sequenceToFind) {

        StringBuilder sb = new StringBuilder();
        for (char[] treeLetterCode : sequenceToFind) {
            sb.append(String.valueOf(treeLetterCode));
        }
        return sb.toString();
    }


    public static void compareCompleteCheckAndWriteToResultFolder(boolean minimizeAllIfTrueOrOnlyOneIfFalse, ShapeContainerIfc queryShape, ShapeContainerIfc targetShape, AlgoParameters algoParameters) {

        System.out.println(String.valueOf(targetShape.getFourLetterCode()));
        CompareCompleteCheck compareCompleteCheck = new CompareCompleteCheck(queryShape, targetShape, algoParameters);
        List<Hit> hits = null;
        try {
            hits = compareCompleteCheck.computeResults();
        } catch (NullResultFromAComparisonException e) {
            return;
        }

        int hitRank = -1;
        A:
        for (Hit hit : hits) {
            hitRank += 1;

            try {
                HitTools.minimizeHitInQuery(hit, queryShape, targetShape, algoParameters);
            } catch (NullResultFromAComparisonException e) {
                e.printStackTrace();
            } catch (ExceptionInScoringUsingBioJavaJMolGUI | ShapeBuildingException exceptionInScoringUsingBioJavaJMolGUI) {
                exceptionInScoringUsingBioJavaJMolGUI.printStackTrace();
                continue A;
            }

            if (hit instanceof HitPeptideWithQueryPeptide) {
                HitPeptideWithQueryPeptide hitPeptideWithQueryPeptide = (HitPeptideWithQueryPeptide) hit;
                String message = hit.toString() + " RmsdBackbone = " + hitPeptideWithQueryPeptide.getRmsdBackboneWhencomparingPeptideToPeptide() + " Rank = " + hitRank;
                ControllerLoger.logger.log(Level.INFO, message);
            }

            if (minimizeAllIfTrueOrOnlyOneIfFalse == false) {
                break;
            }
        }

    }


    public static AlgoParameters prepareAlgoParameters() throws ParsingConfigFileException {

        URL url = ProtocolBindingVsFolding.class.getClassLoader().getResource("ultimate.xml");
        AlgoParameters algoParameters = CommandLineTools.generateModifiedAlgoParameters(url.getPath(), EnumMyReaderBiojava.BioJava_MMCIFF);
        algoParameters.ultiJMolBuffer = new GenericBuffer<UltiJmol1462>(algoParameters.getSHAPE_COMPARISON_THREAD_COUNT() * 2);
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
        for (int i = 0; i < (algoParameters.getSHAPE_COMPARISON_THREAD_COUNT() * 2); i++) {
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


    /*
    public static ExecutorService getExecutorService(int consumersCount) {

        ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(consumersCount);
        return threadPoolExecutor;
    }
*/


    public static ExecutorService getExecutorService(int consumersCount) {
        int corePoolSize = consumersCount; // no need to keep idle ones
        long keepAliveTime = 500000000; // no need to terminate if thread gets no job, that
        // could happen when searching database for a potetial hit, that could last as long
        // as the time to search the whole system
        int maxCountRunnableInBoundQueue = 50; // 10000;

        /*
        ExecutorService threadPoolExecutor =
                new ThreadPoolExecutor(
                        corePoolSize,
                        consumersCount,
                        keepAliveTime,
                        TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<Runnable>(maxCountRunnableInBoundQueue)
                );
                */

        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
        ExecutorService threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, consumersCount,
                keepAliveTime, TimeUnit.MILLISECONDS,
                queue);
        return threadPoolExecutor;


    }
}
