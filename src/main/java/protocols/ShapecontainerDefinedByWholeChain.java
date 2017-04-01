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

import io.IOTools;
import mystructure.MyStructureIfc;
import parameters.AlgoParameters;
import shape.ShapeContainerIfc;
import shapeBuilder.EnumShapeReductor;
import shapeBuilder.ShapeBuildingException;

import java.util.logging.Level;


public class ShapecontainerDefinedByWholeChain implements ShapeContainerDefined {
    //-------------------------------------------------------------
    // Class members
    //-------------------------------------------------------------
    private char[] fourLetterCode;
    private char[] chainId;
    private AlgoParameters algoParameters;
    private MyStructureIfc myStructure;


    //-------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------
    public ShapecontainerDefinedByWholeChain(char[] fourLetterCode, char[] chainId, AlgoParameters algoParameters) {
        this.fourLetterCode = fourLetterCode;
        this.chainId = chainId;
        this.algoParameters = algoParameters;

    }


    //-------------------------------------------------------------
    // Public & Override methods
    //-------------------------------------------------------------
    @Override
    public ShapeContainerIfc getShapecontainer() throws ShapeBuildingException {

        long t = System.nanoTime();
        ControllerLoger.logger.log(Level.INFO, "&&&&& Start Read " + String.valueOf(fourLetterCode) + " " + t);
        myStructure = IOTools.getMyStructureIfc(algoParameters, fourLetterCode).getValue();

        if (myStructure == null) {
            ShapeBuildingException exception = new ShapeBuildingException("Failed to ShapecontainerDefinedBySegmentOfChain because of null MyStructure");
            throw exception;
        }
        ControllerLoger.logger.log(Level.INFO, "&&&&&& Finished Read " + String.valueOf(fourLetterCode) + " " + t);
        ShapeContainerIfc shapecontainer = null;

        ControllerLoger.logger.log(Level.INFO, "&&&&&& Start shape container " + String.valueOf(fourLetterCode) + " " + t);
        shapecontainer = ShapeContainerFactory.getShapeAroundAChain(EnumShapeReductor.CLUSTERING, myStructure, algoParameters, chainId);
        ControllerLoger.logger.log(Level.INFO, "&&&&&& Made shape container " + String.valueOf(fourLetterCode) + " " + t);

        return shapecontainer;
    }

    @Override
    public ShapeContainerIfc getShapecontainer(MyStructureIfc myStructure) throws ShapeBuildingException {

        this.myStructure = myStructure;
        long t = System.nanoTime();

        ControllerLoger.logger.log(Level.INFO, "&&&&&& Start shape container " + String.valueOf(fourLetterCode) + " " + t);
        ShapeContainerIfc shapecontainer = ShapeContainerFactory.getShapeAroundAChain(EnumShapeReductor.CLUSTERING, myStructure, algoParameters, chainId);
        ControllerLoger.logger.log(Level.INFO, "&&&&&& Made shape container " + String.valueOf(fourLetterCode) + " " + t);

        return shapecontainer;
    }

    @Override
    public MyStructureIfc getMyStructure() {
        return myStructure;
    }

    public char[] getFourLetterCode() {
        return fourLetterCode;
    }
}
