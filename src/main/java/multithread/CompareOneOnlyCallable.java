package multithread;

import hits.Hit;
import parameters.AlgoParameters;
import shape.ShapeContainerIfc;
import shapeCompare.ComparatorShapeContainerQueryVsAnyShapeContainer;
import shapeCompare.NullResultFromAComparisonException;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by Fabrice on 02/10/16.
 */

/**
 * Do a shape comparison within a Callable
 */
public class CompareOneOnlyCallable implements Callable<List<Hit>> {
    //------------------------
    // Class variables
    //------------------------
    private final ShapeContainerIfc shapeContainerQuery;
    private final ShapeContainerIfc shapeContainerAnyShape;
    private final AlgoParameters algoParameters;


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public CompareOneOnlyCallable(ShapeContainerIfc shapeContainerQuery, ShapeContainerIfc shapeContainerAnyShape, AlgoParameters algoParameters) {

        this.shapeContainerQuery = shapeContainerQuery;
        this.shapeContainerAnyShape = shapeContainerAnyShape;
        this.algoParameters = algoParameters;
    }


    // -------------------------------------------------------------------
    // Override methods
    // -------------------------------------------------------------------
    @Override
    public List<Hit> call() throws Exception {
        ComparatorShapeContainerQueryVsAnyShapeContainer comparatorShape = new ComparatorShapeContainerQueryVsAnyShapeContainer(shapeContainerQuery, shapeContainerAnyShape, algoParameters);
        List<Hit> listBestHitForEachAndEverySeed = null;
        try {
            listBestHitForEachAndEverySeed = comparatorShape.computeResults();

        } catch (NullResultFromAComparisonException e) {

            NullResultFromAComparisonException exception = new NullResultFromAComparisonException(e.getMessage());
            throw exception;
        }

        return listBestHitForEachAndEverySeed;
    }


    // -------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------
}
