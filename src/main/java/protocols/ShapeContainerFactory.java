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

import mystructure.MyMonomerIfc;
import mystructure.MyStructureIfc;
import parameters.AlgoParameters;
import parameters.QueryAtomDefinedByIds;
import shape.ShapeContainerIfc;
import shapeBuilder.EnumShapeReductor;
import shapeBuilder.ShapeBuilder;
import shapeBuilder.ShapeBuildingException;

import java.util.List;

/**
 * This factory is the recommanded way to by=uild a ShapeContainer.
 */
public class ShapeContainerFactory {


    /**
     * @param enumShapeReductor
     * @param myStructureGlobal is not protonated
     * @param algoParameters
     * @return
     */
    public static ShapeContainerIfc getShapeAroundAChain(EnumShapeReductor enumShapeReductor, MyStructureIfc myStructureGlobal, AlgoParameters algoParameters,
                                                         char[] chainId) throws ShapeBuildingException {

        ShapeBuilder ShapeBuilder = new ShapeBuilder(myStructureGlobal, algoParameters, enumShapeReductor);
        ShapeContainerIfc shapeContainer = ShapeBuilder.getShapeAroundAChain(chainId);

        return shapeContainer;
    }


    public static ShapeContainerIfc getShapeAroundASegmentOfChainUsingStartingMyMonomerPositionInChain(EnumShapeReductor enumShapeReductor, MyStructureIfc myStructureGlobal, AlgoParameters algoParameters,
                                                                                                       char[] chainId, int startingRankId, int peptideLength) throws ShapeBuildingException {

        ShapeBuilder ShapeBuilder = new ShapeBuilder(myStructureGlobal, algoParameters, enumShapeReductor);
        ShapeContainerIfc shapeContainer = ShapeBuilder.getShapeAroundASegmentOfChainUsingStartingMyMonomerPositionInChain(chainId, startingRankId, peptideLength);
        return shapeContainer;
    }


    public static ShapeContainerIfc getShapeAroundAHetAtomLigand(EnumShapeReductor enumShapeReductor, MyStructureIfc myStructureGlobal, AlgoParameters algoParameters,
                                                                 char[] hetAtomsLigandId, int occurrenceId) throws ShapeBuildingException {

        ShapeBuilder ShapeBuilder = new ShapeBuilder(myStructureGlobal, algoParameters, enumShapeReductor);
        ShapeContainerIfc shapeContainer = ShapeBuilder.getShapeAroundAHetAtomLigand(hetAtomsLigandId, occurrenceId);
        return shapeContainer;
    }


    public static ShapeContainerIfc getShapeAroundAtomDefinedByIds(EnumShapeReductor enumShapeReductor, MyStructureIfc myStructureGlobal, AlgoParameters algoParameters,
                                                                   List<QueryAtomDefinedByIds> listAtomDefinedByIds, List<String> chainToIgnore) throws ShapeBuildingException {

        ShapeBuilder ShapeBuilder = new ShapeBuilder(myStructureGlobal, algoParameters, enumShapeReductor);
        ShapeContainerIfc shapeContainer = ShapeBuilder.getShapeAroundAtomDefinedByIds(listAtomDefinedByIds, chainToIgnore);
        return shapeContainer;
    }


    public static ShapeContainerIfc getShapeAroundForeignLigand(EnumShapeReductor enumShapeReductor, MyStructureIfc myStructureLocalQuery, List<MyMonomerIfc> foreignMonomerToExclude, MyStructureIfc rotatedLigandOrPeptide, AlgoParameters algoParameters) {

        ShapeBuilder ShapeBuilder = new ShapeBuilder(myStructureLocalQuery, algoParameters, enumShapeReductor);
        ShapeContainerIfc shapeContainer = ShapeBuilder.getShapeAroundForeignLigand(foreignMonomerToExclude, rotatedLigandOrPeptide);
        return shapeContainer;
    }
}
