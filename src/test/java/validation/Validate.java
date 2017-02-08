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
package validation;

import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import hits.Hit;
import hits.HitPeptideWithQueryPeptide;
import hits.HitTools;
import io.ReadTextFile;
import io.Tools;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.Ignore;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;
import protocols.ShapeContainerDefined;
import protocols.ShapecontainerDefinedBySegmentOfChain;
import protocols.ShapecontainerDefinedByWholeChain;
import shape.ShapeContainerIfc;
import shapeBuilder.ShapeBuildingException;
import shapeCompare.CompareCompleteCheck;
import shapeCompare.NullResultFromAComparisonException;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class Validate {


    @Ignore
    @Test
    public void statsOnHits() throws IOException, ParsingConfigFileException {

        // Hypothesis
        // above a given coverage of query
        // then the cost is significant to find good hits, because cost is relative, not absolute.

        // Should use the coverage and score of the CompleteCheck ?

        // So using cost and coverage, can I split results in two parts: one enriched with low rmsd backbone

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        int initialCount = algoParameters.ultiJMolBuffer.getSize();

        algoParameters.setPATH_TO_RESULT_FILES("//Users//Fabrice//Documents//validate//");

        // input of a result file
        //String pathToResultFile = "//Users//Fabrice//Documents//resultsPubli//1be9//log_Project copy.txt";
        String pathToResultFile = "//Users//Fabrice//Documents//resultsPubli//1be9//log_Project_14122016.txt";


        String resultFileContent = ReadTextFile.readTextFile(pathToResultFile);
        // find hits with rmsd backbone less than 1.0A, for instance

        double minCost = Double.MAX_VALUE;
        double maxCost = Double.MIN_VALUE;

        // Get a DescriptiveStatistics instance
        DescriptiveStatistics stats = new DescriptiveStatistics();


        String[] lines = resultFileContent.split("\\n");
        A:
        for (int i = 0; i < lines.length; i++) {

            String line = lines[i];
            String fourLetterCode = null;
            String chainId = null;
            Integer rankId = null;
            Double cost = null;
            Double RatioPairedPointInQuery = null;

            // find first line
            // get what is needed for a Hit
            if (line.contains("PDB =")) {

                String[] lineContentPDB = lines[i].split(" ");
                for (int j = 0; j < lineContentPDB.length; j++) {
                    if (lineContentPDB[j].equals("PDB")) {
                        fourLetterCode = lineContentPDB[j + 2];
                    }
                    if (lineContentPDB[j].equals("chain")) {
                        chainId = lineContentPDB[j + 3];
                    }
                    if (lineContentPDB[j].equals("index")) {
                        rankId = Integer.valueOf(lineContentPDB[j + 2]);
                    }
                    if (lineContentPDB[j].equals("cost")) {
                        cost = Double.valueOf(lineContentPDB[j + 2]);
                    }

                }

                // Analyse second line
                String[] secondLineContent = lines[i + 1].split(" ");
                for (int j = 0; j < secondLineContent.length; j++) {
                    if (secondLineContent[j].equals("RatioPairedPointInQuery")) {
                        RatioPairedPointInQuery = Double.valueOf(secondLineContent[j + 2]);
                        if (RatioPairedPointInQuery < 0.85) {
                            //continue A;
                        }
                    }
                }


                // Analyse third line
                String[] thirsLineContent = lines[i + 2].split(" ");
                for (int j = 0; j < thirsLineContent.length; j++) {
                    if (thirsLineContent[j].equals("RmsdBackbone")) {
                        Double rmsdBackbone = Double.valueOf(thirsLineContent[j + 2]);


                        if (rmsdBackbone > 1.0) {
                            continue A;
                        }
                        if (cost < minCost) {
                            minCost = cost;
                        }
                        if (cost > maxCost) {
                            maxCost = cost;
                        }
                        stats.addValue(cost);

                        System.out.println("From file : " + fourLetterCode + " " + chainId + " " + rankId + " " + "rmsdBackbone = " + rmsdBackbone + " cost = " + cost + " RatioPairedPointInQuery = " + RatioPairedPointInQuery);

                        //System.out.println(fourLetterCode + " " + chainId + " " + rankId + "");

                    }
                }


            }
        }

        System.out.println("minCost = " + minCost + " maxCost = " + maxCost);
        // Compute some statistics
        double mean = stats.getMean();
        double std = stats.getStandardDeviation();
        double median = stats.getPercentile(50);
        System.out.println("mean = " + mean + " std = " + std + " median = " + median);

        // With current
        // coverage any
        // rmsdbackbone < 1.0
        //minCost = 2.0627570851507445E-4 maxCost = 0.06361719435097728
        //mean = 0.04331737193190198 std = 0.015712292942489853 median = 0.04640045794123616

        // coverage > 85%
        // rmsdbackbone < 1.0
        //minCost = 2.0627570851507445E-4 maxCost = 0.06021795808354686
        // mean = 0.03166813617652109 std = 0.021620985818557835 median = 0.035794879526032934


        // coverage any
        // rmsdbackbone > 1.0
        //minCost = 0.02243371593981088 maxCost = 0.10234928242929589
        //mean = 0.05783326077520913 std = 0.011907010252593919 median = 0.05521930805200214

        // coverage > 85%
        // rmsdbackbone > 1.0
        //minCost = 0.02243371593981088 maxCost = 0.02243371593981088
        //mean = 0.02243371593981088 std = 0.0 median = 0.02243371593981088

        int finalCount = algoParameters.ultiJMolBuffer.getSize();
        assertTrue(finalCount == initialCount);
        try {
            for (int i = 0; i < initialCount; i++) {
                algoParameters.ultiJMolBuffer.get().frame.dispose();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 0);
    }

    /**
     * Test when good hit are identified
     * As I have changed the scoring, that might be different.
     *
     * @throws IOException
     * @throws ParsingConfigFileException
     */
    @Ignore
    @Test
    public void validate() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        int initialCount = algoParameters.ultiJMolBuffer.getSize();

        algoParameters.setPATH_TO_RESULT_FILES("//Users//Fabrice//Documents//validate//");
        ShapeContainerDefined shapeContainerbuilder = new ShapecontainerDefinedByWholeChain("1be9".toCharArray(), "B".toCharArray(), algoParameters);
        ShapeContainerIfc queryShape = null;
        try {
            queryShape = shapeContainerbuilder.getShapecontainer();
        } catch (ShapeBuildingException e) {
            e.printStackTrace();
            System.exit(0);
        }

        String fileNameQuery = "query";
        queryShape.exportShapeColoredToPDBFile(fileNameQuery, algoParameters);

        int peptideLenght = 5;
        // input of a result file
        String pathToResultFile = "//Users//Fabrice//Documents//resultsPubli//1be9//log_Project_2weeksRun.txt";
        String resultFileContent = ReadTextFile.readTextFile(pathToResultFile);
        // find hits with rmsd backbone less than 1.0A, for instance

        String[] lines = resultFileContent.split("\\n");
        A:
        for (int i = 0; i < lines.length; i++) {

            String line = lines[i];
            String fourLetterCode = null;
            String chainId = null;
            Integer rankId = null;

            // find first line
            // get what is needed for a Hit
            if (line.contains("PDB =")) {
                String[] lineContentPDB = lines[i].split(" ");
                for (int j = 0; j < lineContentPDB.length; j++) {
                    if (lineContentPDB[j].equals("PDB")) {
                        fourLetterCode = lineContentPDB[j + 2];
                    }
                    if (lineContentPDB[j].equals("chain")) {
                        chainId = lineContentPDB[j + 3];
                    }
                    if (lineContentPDB[j].equals("index")) {
                        rankId = Integer.valueOf(lineContentPDB[j + 2]);
                    }

                }
                String[] lineContent = lines[i + 2].split(" ");
                for (int j = 0; j < lineContent.length; j++) {
                    if (lineContent[j].equals("RmsdBackbone")) {
                        Double rmsdBackbone = Double.valueOf(lineContent[j + 2]);
                        if (rmsdBackbone > 1.0) {
                            continue A;
                        }

                        System.out.println("From file : " + fourLetterCode + " " + chainId + " " + rankId + " " + rmsdBackbone);
                    }
                }


                // build the shape
                ShapeContainerDefined shapecontainerBuilderTarget = new ShapecontainerDefinedBySegmentOfChain(fourLetterCode.toLowerCase().toCharArray(), chainId.toCharArray(), rankId, peptideLenght, algoParameters);
                ShapeContainerIfc targetShape = null;
                try {
                    targetShape = shapecontainerBuilderTarget.getShapecontainer();
                } catch (ShapeBuildingException e) {
                    continue A;
                }


                CompareCompleteCheck compareCompleteCheck = new CompareCompleteCheck(queryShape, targetShape, algoParameters);
                List<Hit> hits = null;
                try {
                    hits = compareCompleteCheck.computeResults();
                } catch (NullResultFromAComparisonException e) {
                    return;
                }

                // minimize first hit
                if (hits != null && hits.size() > 0) {

                    Hit hitToConsider = hits.get(0);

                    if (hitToConsider instanceof HitPeptideWithQueryPeptide) {
                        HitPeptideWithQueryPeptide hitPeptideWithQueryPeptide = (HitPeptideWithQueryPeptide) hitToConsider;

                        float rmsdBackBoneRecomputed = hitPeptideWithQueryPeptide.getRmsdBackboneWhencomparingPeptideToPeptide();
                        System.out.println("Recomputed rmsdBackBoneRecomputed = " + rmsdBackBoneRecomputed);

                        String fileNameHit = "hit_" + fourLetterCode + "_" + chainId + "_" + rankId;
                        targetShape.exportRotatedShapeColoredToPDBFile(fileNameHit, algoParameters, hitToConsider.getResultsFromEvaluateCost());

                        try {
                            HitTools.minimizeHitInQuery(hitToConsider, queryShape, targetShape, algoParameters);

                        } catch (ExceptionInScoringUsingBioJavaJMolGUI | ShapeBuildingException | NullResultFromAComparisonException exceptionInScoringUsingBioJavaJMolGUI) {
                            exceptionInScoringUsingBioJavaJMolGUI.printStackTrace();
                            continue A;
                        }
                    }

                }

            }

            // line1 PDB =
            // line+2 RmsdBackbone = 3.8216481
            // recompute them and export colored shape

            // do visual inspection


        }

        int finalCount = algoParameters.ultiJMolBuffer.getSize();
        assertTrue(finalCount == initialCount);
        try {
            for (int i = 0; i < initialCount; i++) {
                algoParameters.ultiJMolBuffer.get().frame.dispose();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 0);
    }

}
