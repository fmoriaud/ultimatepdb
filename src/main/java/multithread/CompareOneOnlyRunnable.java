package multithread;

import mystructure.MyStructureIfc;
import parameters.AlgoParameters;
import protocols.ProtocolTools;
import protocols.ShapeContainerDefined;
import shape.ShapeContainerIfc;

/**
 * Created by Fabrice on 31/10/16.
 */
public class CompareOneOnlyRunnable implements Runnable {
    //-------------------------------------------------------------
    // Class members
    //-------------------------------------------------------------
    private final ShapeContainerIfc shapeContainerQuery;
    private final ShapeContainerDefined shapeContainerDefined;
    private final AlgoParameters algoParameters;
    private MyStructureIfc myStructureTarget;

    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public CompareOneOnlyRunnable(ShapeContainerIfc shapeContainerQuery, ShapeContainerDefined shapeContainerDefined, AlgoParameters algoParameters) {

        this.shapeContainerQuery = shapeContainerQuery;
        this.shapeContainerDefined = shapeContainerDefined;
        this.algoParameters = algoParameters;

    }


    public CompareOneOnlyRunnable(ShapeContainerIfc shapeContainerQuery, MyStructureIfc myStructureTarget, ShapeContainerDefined shapeContainerDefined, AlgoParameters algoParameters) {

        this.shapeContainerQuery = shapeContainerQuery;
        this.shapeContainerDefined = shapeContainerDefined;
        this.algoParameters = algoParameters;
        this.myStructureTarget = myStructureTarget;
    }


    //-------------------------------------------------------------
    // Public & Override methods
    //-------------------------------------------------------------
    @Override
    public void run() {

        ShapeContainerIfc targetShape = null;
        if (myStructureTarget != null){
            targetShape = shapeContainerDefined.getShapecontainer(myStructureTarget);
        } else{
            targetShape = shapeContainerDefined.getShapecontainer();
        }

        System.out.println("Finish Built a shape container");
        ProtocolTools.compareAndWriteToResultFolder(shapeContainerQuery, targetShape, algoParameters);
    }
}
