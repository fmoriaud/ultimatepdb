package multithread;

import parameters.AlgoParameters;
import protocols.ProtocolTools;
import protocols.ShapeContainerDefined;
import shape.ShapeContainerIfc;

/**
 * Created by Fabrice on 31/10/16.
 */
public class CompareOneOnlyRunnable implements Runnable{
    //-------------------------------------------------------------
    // Class members
    //-------------------------------------------------------------
    private final ShapeContainerIfc shapeContainerQuery;
    private final ShapeContainerDefined shapeContainerDefined;
    private final AlgoParameters algoParameters;


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public CompareOneOnlyRunnable(ShapeContainerIfc shapeContainerQuery, ShapeContainerDefined shapeContainerDefined, AlgoParameters algoParameters) {

        this.shapeContainerQuery = shapeContainerQuery;
        this.shapeContainerDefined = shapeContainerDefined;
        this.algoParameters = algoParameters;
    }



    //-------------------------------------------------------------
    // Public & Override methods
    //-------------------------------------------------------------
    @Override
    public void run() {

        ShapeContainerIfc targetShape = shapeContainerDefined.getShapecontainer();
        ProtocolTools.compareAndWriteToResultFolder(shapeContainerQuery, targetShape, algoParameters);
    }
}
