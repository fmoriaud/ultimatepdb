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
import jmolgui.UltiJmol1462;
import math.ProcrustesAnalysisIfc;
import mystructure.EnumMyReaderBiojava;
import mystructure.ExceptionInMyStructurePackage;
import mystructure.MyStructureIfc;
import mystructure.ReadingStructurefileException;
import org.biojava.nbio.structure.Structure;
import parameters.AlgoParameters;
import shape.ShapeContainerIfc;
import shapeBuilder.ShapeBuildingException;
import shapeCompare.CompareCompleteCheck;
import shapeCompare.NullResultFromAComparisonException;
import shapeCompare.ProcrustesAnalysis;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Level;

public class ProtocolTools {
    // -------------------------------------------------------------------
    // Static Methods
    // -------------------------------------------------------------------
    public static int executeComparisons(ShapeContainerIfc queryShape, int peptideLength, List<HitInSequenceDb> hitsInDatabase, AlgoParameters algoParameters) {
        String fourLetterCodeTarget;
        String chainIdFromDB;

        int comparisonsDoneCount = 0;
        A: for (HitInSequenceDb hitInSequenceDb : hitsInDatabase) {

            fourLetterCodeTarget = hitInSequenceDb.getFourLetterCode();
            chainIdFromDB = hitInSequenceDb.getChainIdFromDB();
            List<Integer> listRankIds = hitInSequenceDb.getListRankIds();

            BiojavaReader reader = new BiojavaReader();
            Structure mmcifStructure = null;
            try {
                mmcifStructure = reader.readFromPDBFolder(fourLetterCodeTarget.toLowerCase(), algoParameters.getPATH_TO_REMEDIATED_PDB_MMCIF_FOLDER(), algoParameters.getPATH_TO_CHEMCOMP_FOLDER());
            } catch (IOException | ExceptionInIOPackage e) {
                continue A;
            }
            AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
            MyStructureIfc mystructure = null;
            try {
                mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure);
            } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
                continue A;
            }

            char[] chainId = chainIdFromDB.toCharArray();
            B:
            for (int i = 0; i < listRankIds.size(); i++) {

                Integer matchingRankId = listRankIds.get(i);

                ShapeContainerDefined shapeContainerDefined = new ShapecontainerDefinedBySegmentOfChain(fourLetterCodeTarget.toLowerCase().toCharArray(), chainId, matchingRankId, peptideLength, algoParameters);

                ShapeContainerIfc targetShape = null;
                try {
                    targetShape = shapeContainerDefined.getShapecontainer(mystructure);

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
        int corePoolSize = 0; // no need to keep idle ones
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

        BlockingQueue<Runnable> queue = new SynchronousQueue<>();
        ExecutorService threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, consumersCount,
                keepAliveTime, TimeUnit.MILLISECONDS,
                queue);
        return threadPoolExecutor;


    }
}
