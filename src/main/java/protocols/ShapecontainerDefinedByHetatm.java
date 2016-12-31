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


public class ShapecontainerDefinedByHetatm implements ShapeContainerDefined {
    //-------------------------------------------------------------
    // Class members
    //-------------------------------------------------------------
    private char[] fourLetterCode;
    private AlgoParameters algoParameters;
    private char[] hetAtomsLigandId;
    private int occurrenceId;
    private MyStructureIfc myStructure;


    //-------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------
    public ShapecontainerDefinedByHetatm(char[] fourLetterCode, AlgoParameters algoParameters, char[] hetAtomsLigandId, int occurrenceId) {
        this.fourLetterCode = fourLetterCode;
        this.algoParameters = algoParameters;
        this.hetAtomsLigandId = hetAtomsLigandId;
        this.occurrenceId = occurrenceId;
    }


    //-------------------------------------------------------------
    // Public & Override methods
    //-------------------------------------------------------------
    @Override
    public ShapeContainerIfc getShapecontainer() throws ShapeBuildingException {

        myStructure = IOTools.getMyStructureIfc(algoParameters, fourLetterCode);
        if (myStructure == null) {
            ShapeBuildingException exception = new ShapeBuildingException("Failed to ShapecontainerDefinedBySegmentOfChain because of null MyStructure");
            throw exception;
        }

        ShapeContainerIfc shapecontainer = ShapeContainerFactory.getShapeAroundAHetAtomLigand(EnumShapeReductor.CLUSTERING, myStructure, algoParameters, hetAtomsLigandId, occurrenceId);
        return shapecontainer;
    }

    @Override
    public ShapeContainerIfc getShapecontainer(MyStructureIfc myStructureTarget) throws ShapeBuildingException {

        this.myStructure = myStructureTarget;
        ShapeContainerIfc shapecontainer = ShapeContainerFactory.getShapeAroundAHetAtomLigand(EnumShapeReductor.CLUSTERING, myStructureTarget, algoParameters, hetAtomsLigandId, occurrenceId);
        return shapecontainer;
    }

    @Override
    public MyStructureIfc getMyStructure() {
        return myStructure;
    }
}
