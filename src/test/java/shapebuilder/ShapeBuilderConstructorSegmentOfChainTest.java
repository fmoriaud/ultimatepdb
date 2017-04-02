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
package shapebuilder;

import database.HashTablesTools;
import database.HitInSequenceDb;
import database.SequenceTools;
import io.Tools;
import mystructure.MyAtomIfc;
import mystructure.MyBondIfc;
import mystructure.MyChainIfc;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;
import protocols.ShapeContainerDefined;
import protocols.ShapecontainerDefinedBySegmentOfChain;
import shape.ShapeContainerIfc;
import shape.ShapeContainerWithPeptide;
import shapeBuilder.ShapeBuildingException;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class ShapeBuilderConstructorSegmentOfChainTest {

    @Test
    public void testShapeBuilderConstructorLinearPeptide() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();

        char[] fourLetterCode = "2ce8".toCharArray();
        char[] chainId = "X".toCharArray();
        int startingRankId = 2;
        int peptideLength = 4;
        ShapeContainerDefined shapecontainerDefined = new ShapecontainerDefinedBySegmentOfChain(fourLetterCode, chainId, startingRankId, peptideLength, algoParameters);
        ShapeContainerIfc shape = null;
        try {
            shape = shapecontainerDefined.getShapecontainer();
        } catch (ShapeBuildingException e) {
            assertTrue(false);
        }

        int initialCount = algoParameters.ultiJMolBuffer.getSize();

        assertTrue(shape.getShape().getSize() == 666);
        assertTrue(shape.getMiniShape().size() == 46);

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


    @Test
    public void testShapeBuilderConstructor() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();

        char[] fourLetterCode = "2yjd".toCharArray();
        char[] chainId = "C".toCharArray();
        int startingRankId = 3;
        int peptideLength = 4;
        ShapeContainerDefined shapecontainerDefined = new ShapecontainerDefinedBySegmentOfChain(fourLetterCode, chainId, startingRankId, peptideLength, algoParameters);
        ShapeContainerIfc shape = null;
        try {
            shape = shapecontainerDefined.getShapecontainer();
        } catch (ShapeBuildingException e) {
            assertTrue(false);
        }

        int initialCount = algoParameters.ultiJMolBuffer.getSize();

        // don't know if it is good, it is as it is now.
        // especially because ACE and NH2 were moved...
        assertTrue(shape.getShape().getSize() == 868);
        assertTrue(shape.getMiniShape().size() == 82);

        // Check ligand peptide bonds
        ShapeContainerWithPeptide shapeContainerWithPeptide = (ShapeContainerWithPeptide) shape;
        MyChainIfc ligand = shapeContainerWithPeptide.getPeptide();

        // Check peptide bonds
        MyAtomIfc n1 = ligand.getMyMonomers()[1].getMyAtomFromMyAtomName("N".toCharArray());
        MyAtomIfc c0 = ligand.getMyMonomers()[0].getMyAtomFromMyAtomName("C".toCharArray());
        boolean foundPeptideBond = false;
        for (MyBondIfc bond : n1.getBonds()) {
            if (bond.getBondedAtom() == c0) {
                foundPeptideBond = true;
            }
        }
        assertTrue(foundPeptideBond);

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


    @Test
    public void testShapeBuilderConstructorProblemeticCase() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        int initialCount = algoParameters.ultiJMolBuffer.getSize();

        char[] fourLetterCode = "4ddg".toCharArray();
        char[] chainId = "B".toCharArray();
        int startingRankId = 278;
        int peptideLength = 5;
        ShapeContainerDefined shapecontainerDefined = new ShapecontainerDefinedBySegmentOfChain(fourLetterCode, chainId, startingRankId, peptideLength, algoParameters);
        ShapeContainerIfc shape = null;
        try {
            shape = shapecontainerDefined.getShapecontainer();
        } catch (ShapeBuildingException e) {
            assertTrue(false);
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



    @Test
    public void testShapeBuilderConstructorOtherProblemeticCaseConvergenceFailedInProtonationNotSolved() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        int initialCount = algoParameters.ultiJMolBuffer.getSize();

        char[] fourLetterCode = "5cuf".toCharArray();
        char[] chainId = "D".toCharArray();
        int startingRankId = 275;
        int peptideLength = 5;
        ShapeContainerDefined shapecontainerDefined = new ShapecontainerDefinedBySegmentOfChain(fourLetterCode, chainId, startingRankId, peptideLength, algoParameters);
        ShapeContainerIfc shape = null;
        try {
            shape = shapecontainerDefined.getShapecontainer();
        } catch (ShapeBuildingException e) {
            assertTrue(true);
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

    @Test
    public void testCaseThatThrewAnException() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        int initialCount = algoParameters.ultiJMolBuffer.getSize();

        char[] fourLetterCode = "5it7".toCharArray();
        char[] chainId = "OO".toCharArray();
        int startingRankId = 5;
        int peptideLength = 5;

        ShapeContainerDefined shapecontainerDefinedQuery = new ShapecontainerDefinedBySegmentOfChain(fourLetterCode, chainId, startingRankId, peptideLength, algoParameters);
        ShapeContainerIfc targetShape = null;
        try {
            targetShape = shapecontainerDefinedQuery.getShapecontainer();
        } catch (ShapeBuildingException e) {
            assertTrue(false);
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


    @Test
    public void testRankId() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        int initialCount = algoParameters.ultiJMolBuffer.getSize();

        char[] fourLetterCode = "2ce8".toCharArray();
        char[] chainId = "X".toCharArray();
        int startingRankId = 2;
        int peptideLength = 4;
        ShapeContainerDefined shapecontainerDefined = new ShapecontainerDefinedBySegmentOfChain(fourLetterCode, chainId, startingRankId, peptideLength, algoParameters);
        ShapeContainerIfc shape = null;
        try {
            shape = shapecontainerDefined.getShapecontainer();
        } catch (ShapeBuildingException e) {
            assertTrue(false);
        }
        ShapeContainerWithPeptide shapeWithPeptide = (ShapeContainerWithPeptide) shape;
        List<char[]> sequence = shapeWithPeptide.getPeptideSequence();

        StringBuilder sb = new StringBuilder();
        for (char[] threeLetterCode : sequence) {
            sb.append(String.valueOf(threeLetterCode));
        }
        String peptideSequence = sb.toString();

        int minLength = 4;
        int maxLength = 1000;

        boolean useSimilarSequences = false;
        List<HitInSequenceDb> sequenceHit = SequenceTools.find(HashTablesTools.tableSequenceTestName, HashTablesTools.tableSequenceFailureTestName, minLength, maxLength, peptideSequence, useSimilarSequences);
        List<Integer> rankIds = sequenceHit.get(0).getListRankIds();
        int matchingRankId = rankIds.get(0);

        assertTrue(matchingRankId == startingRankId);

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
