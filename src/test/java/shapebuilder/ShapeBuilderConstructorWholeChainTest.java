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
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;
import protocols.ShapeContainerDefined;
import protocols.ShapecontainerDefinedByWholeChain;
import shape.ShapeContainerIfc;
import shapeBuilder.ShapeBuildingException;

import java.io.IOException;

import static org.junit.Assert.assertTrue;


/**
 * Created by Fabrice on 11/09/16.
 */
public class ShapeBuilderConstructorWholeChainTest {

    @Test
    public void testShapeBuilderConstructor() throws IOException, ParsingConfigFileException {


        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        int initialCount = algoParameters.ultiJMolBuffer.getSize();

        String fourLetterCode = "2yjd";
        char[] chainId = "C".toCharArray();
        ShapeContainerDefined shapeContainerbuilder = new ShapecontainerDefinedByWholeChain(fourLetterCode.toCharArray(), chainId, algoParameters);
        ShapeContainerIfc shape = null;
        try {
            shape = shapeContainerbuilder.getShapecontainer();
        } catch (ShapeBuildingException e) {
            assertTrue(false);
        }

        // don't know if it is good, it is as it is now.
        assertTrue(shape.getShape().getSize() == 459);
        assertTrue(shape.getMiniShape().size() == 61);

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
