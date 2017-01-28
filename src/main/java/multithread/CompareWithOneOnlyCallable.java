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
package multithread;

import mystructure.MyStructureIfc;
import parameters.AlgoParameters;
import protocols.ProtocolTools;
import protocols.ShapeContainerDefined;
import shape.ShapeContainerIfc;

import java.util.concurrent.Callable;

public class CompareWithOneOnlyCallable implements Callable<Boolean> {
    //-------------------------------------------------------------
    // Class members
    //-------------------------------------------------------------
    private final ShapeContainerIfc shapeContainerQuery;
    private final ShapeContainerDefined shapeContainerDefinedTarget;
    private final AlgoParameters algoParameters;
    private final boolean minimizeAllIfTrueOrOnlyOneIfFalse;


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------

    /**
     * Compare a ShapeContainer already built to a ShapeContainerDefined which needs to read mmCIF file and build Shapecontainer
     *
     * @param minimizeAllIfTrueOrOnlyOneIfFalse
     * @param shapeContainerQuery
     * @param shapeContainerDefined
     * @param algoParameters
     */
    public CompareWithOneOnlyCallable(boolean minimizeAllIfTrueOrOnlyOneIfFalse, ShapeContainerIfc shapeContainerQuery, ShapeContainerDefined shapeContainerDefined, AlgoParameters algoParameters) {

        this.shapeContainerQuery = shapeContainerQuery;
        this.shapeContainerDefinedTarget = shapeContainerDefined;
        this.algoParameters = algoParameters;
        this.minimizeAllIfTrueOrOnlyOneIfFalse = minimizeAllIfTrueOrOnlyOneIfFalse;
    }


    /*
    public CompareWithOneOnlyCallable(boolean minimizeAllIfTrueOrOnlyOneIfFalse, ShapeContainerIfc shapeContainerQuery, MyStructureIfc myStructureTarget, ShapeContainerDefined shapeContainerDefined, AlgoParameters algoParameters) {

        this.shapeContainerQuery = shapeContainerQuery;
        this.shapeContainerDefinedTarget = shapeContainerDefined;
        this.algoParameters = algoParameters;
        this.myStructureTarget = myStructureTarget;
        this.minimizeAllIfTrueOrOnlyOneIfFalse = minimizeAllIfTrueOrOnlyOneIfFalse;
    }
*/

    //-------------------------------------------------------------
    // Public & Override methods
    //-------------------------------------------------------------
    @Override
    public Boolean call() throws Exception {

        //ShapeContainerIfc targetShape = null;
        // if (myStructureTarget != null) {
        //     targetShape = shapeContainerDefinedTarget.getShapecontainer(myStructureTarget);
        // } else {
        ShapeContainerIfc targetShape = shapeContainerDefinedTarget.getShapecontainer();
        //}

        //System.out.println("Finish Built a shape container");
        ProtocolTools.compareCompleteCheckAndWriteToResultFolder(minimizeAllIfTrueOrOnlyOneIfFalse, shapeContainerQuery, targetShape, algoParameters);

        return true;
    }
}
