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
package shape;

import java.util.List;

import mystructure.MyMonomerIfc;
import parameters.AlgoParameters;
import parameters.QueryAtomDefinedByIds;
import pointWithProperties.CollectionOfPointsWithPropertiesIfc;
import pointWithProperties.PointIfc;
import mystructure.MyStructureIfc;

public class ShapeContainerAtomIdsWithinShape extends ShapeContainer implements ShapeContainerIfc {
    //-------------------------------------------------------------
    // Class members
    //-------------------------------------------------------------
    private List<QueryAtomDefinedByIds> listAtomDefinedByIds;


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public ShapeContainerAtomIdsWithinShape(List<QueryAtomDefinedByIds> listAtomDefinedByIds, CollectionOfPointsWithPropertiesIfc shape, List<PointIfc> listPointDefininingLigandUsedToComputeShape, MyStructureIfc myStructureUsedToComputeShape, List<MyMonomerIfc> foreignMonomerToExclude, String pdbFileHash) {
        super(shape, listPointDefininingLigandUsedToComputeShape,
                myStructureUsedToComputeShape, foreignMonomerToExclude, pdbFileHash);
        this.listAtomDefinedByIds = listAtomDefinedByIds;
    }


    //-------------------------------------------------------------
    // Public & Override methods
    //-------------------------------------------------------------
    @Override
    public List<MyMonomerIfc> getForeignMonomerToExclude() {
        return null;
    }


    // -------------------------------------------------------------------
    // Getter and Setter
    // -------------------------------------------------------------------
    public List<QueryAtomDefinedByIds> getListAtomDefinedByIds() {
        return listAtomDefinedByIds;
    }
}
