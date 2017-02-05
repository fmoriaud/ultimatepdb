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
import protocols.ShapecontainerDefinedByHetatm;
import shape.ShapeContainerIfc;
import shapeBuilder.ShapeBuildingException;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 13/09/16.
 */
public class ShapeBuilderConstructorHetAtmTest {

    @Test
    public void testShapeBuilderConstructor() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        int initialCount = algoParameters.ultiJMolBuffer.getSize();

        String fourLetterCode = "1di9";
        char[] hetatmLigandThreeLetterCode = "MSQ".toCharArray();
        int occurenceId = 1;
        ShapeContainerDefined shapeContainerbuilder = new ShapecontainerDefinedByHetatm(fourLetterCode.toCharArray(), algoParameters, hetatmLigandThreeLetterCode, occurenceId);
        ShapeContainerIfc shape = null;
        try {
            shape = shapeContainerbuilder.getShapecontainer();
        } catch (ShapeBuildingException e) {
            assertTrue(false);
        }

        // don't know if it is good, it is as it is now.
        System.out.println(shape.getShape().getSize());
        System.out.println(shape.getMiniShape().size());
        assertTrue(shape.getShape().getSize() == 805);
        assertTrue(shape.getMiniShape().size() == 54);

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
