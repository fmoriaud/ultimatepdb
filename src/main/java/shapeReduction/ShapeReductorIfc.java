package shapeReduction;

import pointWithProperties.PointWithPropertiesIfc;

import java.util.Map;

/**
 * Created by Fabrice on 26/09/16.
 */
public interface ShapeReductorIfc {

    Map<Integer, PointWithPropertiesIfc> computeReducedCollectionOfPointsWithProperties();
}
