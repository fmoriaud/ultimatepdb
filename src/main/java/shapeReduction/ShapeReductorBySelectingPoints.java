package shapeReduction;

import parameters.AlgoParameters;
import pointWithProperties.CollectionOfPointsWithPropertiesIfc;
import pointWithProperties.PointWithPropertiesIfc;

import java.util.Map;

/**
 * Created by Fabrice on 26/09/16.
 */
public class ShapeReductorBySelectingPoints implements ShapeReductorIfc {
    // -------------------------------------------------------------------
    // Class variables
    // -------------------------------------------------------------------
    private CollectionOfPointsWithPropertiesIfc shapeCollectionPoints;
    private AlgoParameters algoParameters;




    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public ShapeReductorBySelectingPoints(CollectionOfPointsWithPropertiesIfc shapeCollectionPoints, AlgoParameters algoParameters) {
        this.shapeCollectionPoints = shapeCollectionPoints;
        this.algoParameters = algoParameters;
    }




    // -------------------------------------------------------------------
    // Public Interface
    // -------------------------------------------------------------------
    @Override
    public Map<Integer, PointWithPropertiesIfc> computeReducedCollectionOfPointsWithProperties() {
        return null;
    }
}
