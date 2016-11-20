package multithread;

import mystructure.MyStructureIfc;
import parameters.AlgoParameters;
import protocols.ProtocolTools;
import protocols.ShapeContainerDefined;
import shape.ShapeContainerIfc;

import java.util.concurrent.Callable;


/**
 * Created by Fabrice on 31/10/16.
 */
public class CompareWithOneOnlyCallable implements Callable<Boolean> {
    //-------------------------------------------------------------
    // Class members
    //-------------------------------------------------------------
    private final ShapeContainerIfc shapeContainerQuery;
    private final ShapeContainerDefined shapeContainerDefinedTarget;
    private final AlgoParameters algoParameters;
    private MyStructureIfc myStructureTarget;
    private final boolean minimizeAllIfTrueOrOnlyOneIfFalse;

    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------

    /**
     * Compare a ShapeContainer already built to a ShapeContainerDefined which needs to read mmCIF file and build Shapecontainer
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


    public CompareWithOneOnlyCallable(boolean minimizeAllIfTrueOrOnlyOneIfFalse, ShapeContainerIfc shapeContainerQuery, MyStructureIfc myStructureTarget, ShapeContainerDefined shapeContainerDefined, AlgoParameters algoParameters) {

        this.shapeContainerQuery = shapeContainerQuery;
        this.shapeContainerDefinedTarget = shapeContainerDefined;
        this.algoParameters = algoParameters;
        this.myStructureTarget = myStructureTarget;
        this.minimizeAllIfTrueOrOnlyOneIfFalse = minimizeAllIfTrueOrOnlyOneIfFalse;
    }


    //-------------------------------------------------------------
    // Public & Override methods
    //-------------------------------------------------------------

    @Override
    public Boolean call() throws Exception {

        ShapeContainerIfc targetShape = null;
        if (myStructureTarget != null) {
            targetShape = shapeContainerDefinedTarget.getShapecontainer(myStructureTarget);
        } else {
            targetShape = shapeContainerDefinedTarget.getShapecontainer();
        }

        System.out.println("Finish Built a shape container");
        ProtocolTools.compareAndWriteToResultFolder(minimizeAllIfTrueOrOnlyOneIfFalse, shapeContainerQuery, targetShape, algoParameters);

        return true;
    }
}
