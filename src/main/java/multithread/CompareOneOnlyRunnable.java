package multithread;

import parameters.AlgoParameters;
import protocols.ProtocolTools;
import shape.ShapeContainerIfc;

/**
 * Created by Fabrice on 31/10/16.
 */
public class CompareOneOnlyRunnable implements Runnable{
    //-------------------------------------------------------------
    // Class members
    //-------------------------------------------------------------
    private final ShapeContainerIfc shapeContainerQuery;
    private final ShapeContainerIfc shapeContainerAnyShape;
    private final AlgoParameters algoParameters;


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public CompareOneOnlyRunnable(ShapeContainerIfc shapeContainerQuery, ShapeContainerIfc shapeContainerAnyShape, AlgoParameters algoParameters) {

        this.shapeContainerQuery = shapeContainerQuery;
        this.shapeContainerAnyShape = shapeContainerAnyShape;
        this.algoParameters = algoParameters;
    }



    //-------------------------------------------------------------
    // Public & Override methods
    //-------------------------------------------------------------
    @Override
    public void run() {

        ProtocolTools.compareAndWriteToResultFolder(shapeContainerQuery, shapeContainerAnyShape, algoParameters);
    }
}
