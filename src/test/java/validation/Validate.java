package validation;

import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import hits.Hit;
import hits.HitPeptideWithQueryPeptide;
import hits.HitTools;
import io.ReadTextFile;
import io.Tools;
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

/**
 * Created by Fabrice on 10.12.16.
 */
public class Validate {


    @Test
    public void validate() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
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


                        } catch (NullResultFromAComparisonException e) {
                            e.printStackTrace();
                        } catch (ExceptionInScoringUsingBioJavaJMolGUI exceptionInScoringUsingBioJavaJMolGUI) {
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
    }

}
