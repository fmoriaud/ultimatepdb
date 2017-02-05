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

import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import io.Tools;
import mystructure.ExceptionInMyStructurePackage;
import mystructure.ReadingStructurefileException;
import org.junit.Ignore;
import org.junit.Test;
import parameters.AlgoParameters;
import shape.ShapeContainerIfc;
import shapeBuilder.ShapeBuildingException;

import java.io.IOException;
import java.util.logging.FileHandler;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 14/10/16.
 */
public class AutoShapeSegmentOfChainWithProtocolToolsTest {

    @Ignore // till I have less hits minimizing
    @Test
    public void testAutoCompareShapeFromSegmentOfChain() throws ExceptionInScoringUsingBioJavaJMolGUI, ReadingStructurefileException, ExceptionInMyStructurePackage, CommandLineException, ParsingConfigFileException, ShapeBuildingException, IOException {


        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        int initialCount = algoParameters.ultiJMolBuffer.getSize();

        FileHandler fh = null;
        try {
            fh = new FileHandler(algoParameters.getPATH_TO_RESULT_FILES() + ControllerLoger.LOGGER_FILE_NAME);
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        fh.setFormatter(new OptimizerFormater());
        ControllerLoger.logger.addHandler(fh);


        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 1);

        String fourLetterCode = "2yjd";
        char[] chainId = "C".toCharArray();
        int startingRankId = 3;
        int peptideLength = 4;

        ShapeContainerDefined shapeContainerDefinedQuery = new ShapecontainerDefinedBySegmentOfChain(fourLetterCode.toCharArray(), chainId, startingRankId, peptideLength, algoParameters);
        ShapeContainerIfc shapeQuery = null;
        try {
            shapeQuery = shapeContainerDefinedQuery.getShapecontainer();
        } catch (ShapeBuildingException e) {
            e.printStackTrace();
        }

        ShapeContainerDefined shapeContainerDefinedTarget = new ShapecontainerDefinedBySegmentOfChain(fourLetterCode.toCharArray(), chainId, startingRankId, peptideLength, algoParameters);
        ShapeContainerIfc shapeTarget = null;
        try {
            shapeTarget = shapeContainerDefinedTarget.getShapecontainer();
        } catch (ShapeBuildingException e) {
            e.printStackTrace();
        }

        boolean minimizeAllIfTrueOrOnlyOneIfFalse = true;
        ProtocolTools.compareCompleteCheckAndWriteToResultFolder(minimizeAllIfTrueOrOnlyOneIfFalse, shapeQuery, shapeTarget, algoParameters);

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
