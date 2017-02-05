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
package shapeCompare;

import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import hits.Hit;
import hits.HitPeptideWithQueryPeptide;
import hits.HitTools;
import io.Tools;
import mystructure.ExceptionInMyStructurePackage;
import mystructure.ReadingStructurefileException;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.CommandLineException;
import protocols.ParsingConfigFileException;
import protocols.ShapeContainerDefined;
import protocols.ShapecontainerDefinedBySegmentOfChain;
import shape.ShapeContainerIfc;
import shapeBuilder.ShapeBuildingException;
import ultiJmol1462.ResultsUltiJMolMinimizedHitLigandOnTarget;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class AutoShapeSegmentOfChainTest {

    @Test
    public void testAutoCompareShapeFromSegmentOfChain() throws ExceptionInScoringUsingBioJavaJMolGUI, ReadingStructurefileException, ExceptionInMyStructurePackage, CommandLineException, ParsingConfigFileException, ShapeBuildingException, IOException {


        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        int initialCount = algoParameters.ultiJMolBuffer.getSize();

        String fourLetterCode = "2yjd";
        char[] chainId = "C".toCharArray();
        int startingRankId = 3;
        int peptideLength = 4;
        ShapeContainerDefined shapeContainerbuilderQuery = new ShapecontainerDefinedBySegmentOfChain(fourLetterCode.toCharArray(), chainId, startingRankId, peptideLength, algoParameters);
        ShapeContainerIfc shapeQuery = null;
        try {
            shapeQuery = shapeContainerbuilderQuery.getShapecontainer();
        } catch (ShapeBuildingException e) {
            assertTrue(false);
        }

        ShapeContainerDefined shapeContainerbuilderTarget = new ShapecontainerDefinedBySegmentOfChain(fourLetterCode.toCharArray(), chainId, startingRankId, peptideLength, algoParameters);
        ShapeContainerIfc shapeTarget = null;
        try {
            shapeTarget = shapeContainerbuilderTarget.getShapecontainer();
        } catch (ShapeBuildingException e) {
            assertTrue(false);
        }


        ComparatorShapeContainerQueryVsAnyShapeContainer comparatorShape = new ComparatorShapeContainerQueryVsAnyShapeContainer(shapeQuery, shapeTarget, algoParameters);
        List<Hit> listBestHitForEachAndEverySeed = null;
        try {
            listBestHitForEachAndEverySeed = comparatorShape.computeResults();

        } catch (NullResultFromAComparisonException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // first hit
        // InteractionEFinal = 6.20062255859375
        // rmsd ligand = 0.9117103815078735 // rmsdLigand = 4.2638924E-16
        // ligand stained energy = 133.43154907226562
        // count longer than 2A change = 4
        try {
            Hit hit = listBestHitForEachAndEverySeed.get(0);
            HitTools.minimizeHitInQuery(hit, shapeQuery, shapeTarget, algoParameters);
            ResultsUltiJMolMinimizedHitLigandOnTarget hitScore = hit.getResultsUltiJMolMinimizedHitLigandOnTarget();
            assertTrue(hitScore != null);
            assertTrue(hitScore.getInteractionEFinal() > -25 && hitScore.getInteractionEFinal() < -15);
            assertTrue(Math.abs(hitScore.getLigandStrainedEnergy()) > 125 && Math.abs(hitScore.getLigandStrainedEnergy()) < 135);
            assertTrue(hitScore.getRmsdLigand() < 0.7 && hitScore.getRmsdLigand() > 0.5);
            assertTrue(hitScore.getCountOfLongDistanceChange() == 0);
            HitPeptideWithQueryPeptide hitPeptideWithQueryPeptide = (HitPeptideWithQueryPeptide) hit;
            assertTrue(hitPeptideWithQueryPeptide.getRmsdBackboneWhencomparingPeptideToPeptide() < 0.2);

        } catch (NullResultFromAComparisonException e) {
            e.printStackTrace();
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
