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

public class ShapecontainerDefinedBySegmentOfChain implements ShapeContainerDefined {
    //-------------------------------------------------------------
    // Class members
    //-------------------------------------------------------------
    private char[] fourLetterCode;
    private char[] chainId;
    private int startingRankId;
    private int peptideLength;
    private AlgoParameters algoParameters;
    private MyStructureIfc myStructure;

    //-------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------
    public ShapecontainerDefinedBySegmentOfChain(char[] fourLetterCode, char[] chainId, int startingRankId, int peptideLength, AlgoParameters algoParameters) {
        this.fourLetterCode = fourLetterCode;
        this.chainId = chainId;
        this.startingRankId = startingRankId;
        this.peptideLength = peptideLength;
        this.algoParameters = algoParameters;
    }


    //-------------------------------------------------------------
    // Public & Override methods
    //-------------------------------------------------------------
    @Override
    public ShapeContainerIfc getShapecontainer() throws ShapeBuildingException {

        myStructure = IOTools.getMyStructureIfc(algoParameters, fourLetterCode).getValue();
        if (myStructure == null) {
            ShapeBuildingException exception = new ShapeBuildingException("Failed to ShapecontainerDefinedBySegmentOfChain because of null MyStructure");
            throw exception;
        }
        ShapeContainerIfc shapecontainer = ShapeContainerFactory.getShapeAroundASegmentOfChainUsingStartingMyMonomerPositionInChain(EnumShapeReductor.CLUSTERING, myStructure, algoParameters, chainId, startingRankId, peptideLength);

        return shapecontainer;
    }


    @Override
    public ShapeContainerIfc getShapecontainer(MyStructureIfc myStructureTarget) throws ShapeBuildingException {

        this.myStructure = myStructureTarget;
        ShapeContainerIfc shapecontainer = ShapeContainerFactory.getShapeAroundASegmentOfChainUsingStartingMyMonomerPositionInChain(EnumShapeReductor.CLUSTERING, myStructureTarget, algoParameters, chainId, startingRankId, peptideLength);

        return shapecontainer;
    }

    @Override
    public MyStructureIfc getMyStructure() {
        return myStructure;
    }
}
