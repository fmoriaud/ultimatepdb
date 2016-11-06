package multithread;

import mystructure.MyStructureIfc;
import parameters.AlgoParameters;
import protocols.ProtocolTools;
import protocols.ShapeContainerDefined;
import shape.ShapeContainerIfc;
import shapeBuilder.ShapeBuildingException;

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
    private boolean minimizeAllIfTrueOrOnlyOneIfFalse = true;

    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public CompareOneOnlyRunnable(boolean minimizeAllIfTrueOrOnlyOneIfFalse, ShapeContainerIfc shapeContainerQuery, ShapeContainerDefined shapeContainerDefined, AlgoParameters algoParameters) {

        this.shapeContainerQuery = shapeContainerQuery;
        this.shapeContainerDefined = shapeContainerDefined;
        this.algoParameters = algoParameters;
        this.minimizeAllIfTrueOrOnlyOneIfFalse = minimizeAllIfTrueOrOnlyOneIfFalse;

    }


    public CompareOneOnlyRunnable(boolean minimizeAllIfTrueOrOnlyOneIfFalse, ShapeContainerIfc shapeContainerQuery, MyStructureIfc myStructureTarget, ShapeContainerDefined shapeContainerDefined, AlgoParameters algoParameters) {

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
    public void run() {

        ShapeContainerIfc targetShape = null;
        if (myStructureTarget != null){
            try {
                targetShape = shapeContainerDefined.getShapecontainer(myStructureTarget);
            } catch (ShapeBuildingException e) {
                return;
            }
        } else{
            try {
                targetShape = shapeContainerDefined.getShapecontainer();
            } catch (ShapeBuildingException e) {
                return;
            }
        }

        System.out.println("Finish Built a shape container");
        ProtocolTools.compareAndWriteToResultFolder(minimizeAllIfTrueOrOnlyOneIfFalse, shapeContainerQuery, targetShape, algoParameters);
    }
}
