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
    private final ShapeContainerDefined shapeContainerDefined;
    private final AlgoParameters algoParameters;
    private MyStructureIfc myStructureTarget;
    private boolean minimizeAllIfTrueOrOnlyOneIfFalse = true;

    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public CompareWithOneOnlyCallable(boolean minimizeAllIfTrueOrOnlyOneIfFalse, ShapeContainerIfc shapeContainerQuery, ShapeContainerDefined shapeContainerDefined, AlgoParameters algoParameters) {

        this.shapeContainerQuery = shapeContainerQuery;
        this.shapeContainerDefined = shapeContainerDefined;
        this.algoParameters = algoParameters;
        this.minimizeAllIfTrueOrOnlyOneIfFalse = minimizeAllIfTrueOrOnlyOneIfFalse;

    }


    public CompareWithOneOnlyCallable(boolean minimizeAllIfTrueOrOnlyOneIfFalse, ShapeContainerIfc shapeContainerQuery, MyStructureIfc myStructureTarget, ShapeContainerDefined shapeContainerDefined, AlgoParameters algoParameters) {

        this.shapeContainerQuery = shapeContainerQuery;
        this.shapeContainerDefined = shapeContainerDefined;
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
            targetShape = shapeContainerDefined.getShapecontainer(myStructureTarget);
        } else {
            targetShape = shapeContainerDefined.getShapecontainer();
        }

        System.out.println("Finish Built a shape container");
        ProtocolTools.compareAndWriteToResultFolder(minimizeAllIfTrueOrOnlyOneIfFalse, shapeContainerQuery, targetShape, algoParameters);

        return true;
    }
}
