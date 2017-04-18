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

import database.HitInSequenceDb;
import io.Tools;
import org.junit.Test;
import parameters.AlgoParameters;
import shape.ShapeContainerIfc;
import shapeBuilder.ShapeBuildingException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;

import static org.junit.Assert.assertTrue;

public class ProtocolBindingVsFoldingTest {

    /**
     * Reproducing a case that throws an exception. The computation must continue to next entry
     * <p>
     * 5CUF A 5 273
     * 5CUF B 5 273
     * 5CUF C 5 275
     * 5CUF D 5 275 Exception thrown here
     * 5CUF E 5 274
     *
     * @throws IOException
     * @throws ParsingConfigFileException
     */
    @Test
    public void protocolThatThrewException() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();

        int initialCount = algoParameters.ultiJMolBuffer.getSize();

        List<HitInSequenceDb> hitsInDatabase = new ArrayList<>();
        String fourLetterCode = "5CUF";
        int peptideLength = 5;

        List<Integer> listRankIds1 = new ArrayList<>();
        listRankIds1.add(275);
        String chainIdFromDB1 = "D";
        HitInSequenceDb HitInSequenceDb1 = new HitInSequenceDb(listRankIds1, fourLetterCode, chainIdFromDB1, peptideLength);
        hitsInDatabase.add(HitInSequenceDb1);

        List<Integer> listRankIds2 = new ArrayList<>();
        listRankIds2.add(275);
        String chainIdFromDB2 = "C";
        HitInSequenceDb HitInSequenceDb2 = new HitInSequenceDb(listRankIds2, fourLetterCode, chainIdFromDB2, peptideLength);
        hitsInDatabase.add(HitInSequenceDb2);

        ShapeContainerDefined shapeContainerbuilder = new ShapecontainerDefinedByWholeChain("1be9".toCharArray(), "B".toCharArray(), algoParameters);
        ShapeContainerIfc queryShape = null;
        try {
            queryShape = shapeContainerbuilder.getShapecontainer();
        } catch (ShapeBuildingException e) {
            e.printStackTrace();
        }

        int comparisonsDoneCount = 0;
        try {
            comparisonsDoneCount = ProtocolTools.executeComparisons(queryShape, peptideLength, hitsInDatabase, algoParameters);
        } catch (Exception e) {
            assertTrue(false);
        }

        assertTrue(comparisonsDoneCount == 1); // 1 because: one is not done because it failed but the next one worked.

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
