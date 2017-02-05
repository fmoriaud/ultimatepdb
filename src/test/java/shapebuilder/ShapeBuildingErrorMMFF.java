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

import io.Tools;
import org.junit.Ignore;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;
import protocols.ShapeContainerDefined;
import protocols.ShapecontainerDefinedBySegmentOfChain;
import shape.ShapeContainerIfc;
import shapeBuilder.ShapeBuildingException;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class ShapeBuildingErrorMMFF {

    @Test
    @Ignore
    /**
     * Still sending to console: could not setup force field MMFF. But a shpe is generated so not too bad.
     */
    public void couldNotGetMMFF() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        int initialCount = algoParameters.ultiJMolBuffer.getSize();

        char[] fourLetterCodeTarget = "3a0b".toCharArray();
        char[] chainIdTarget = "E".toCharArray();
        int startingRankId = 10;
        int peptideLength = 5;
        ShapeContainerDefined shapecontainerDefinedTarget = new ShapecontainerDefinedBySegmentOfChain(fourLetterCodeTarget, chainIdTarget, startingRankId, peptideLength, algoParameters);
        ShapeContainerIfc targetShape = null;
        try {
            targetShape = shapecontainerDefinedTarget.getShapecontainer();
        } catch (ShapeBuildingException e) {
            assertTrue(false);
        }

        //targetShape.exportShapeColoredToPDBFile("testWithHeme", algoParameters);

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
