package shapeReduction;

import pointWithProperties.PointWithPropertiesIfc;
import pointWithProperties.StrikingProperties;
import pointWithProperties.StrikingPropertiesTools;

import java.util.List;

/**
 * Created by Fabrice on 13.12.16.
 */
public class ShapeReductorTools {

    public static PointWithPropertiesIfc returnPointWithLowerPriorityWhenThereIsAMatchingProperty(PointWithPropertiesIfc point1, PointWithPropertiesIfc point2) {

        List<StrikingProperties> commonStrikingProperties = StrikingPropertiesTools.findCommonStrikingProperties(point1, point2);

        if (commonStrikingProperties.size() == 0) {
            System.out.println("none in common !! " + commonStrikingProperties.size());
            System.out.println();
        }

        if (point1.getStrikingProperties().size() > 2 || point2.getStrikingProperties().size() > 2) {
            System.out.println("More than two " + commonStrikingProperties.size());
            System.out.println();
        }

        if (point1.getStrikingProperties().size() < point2.getStrikingProperties().size()) {
            return point1;
        } else {
            return point2;
        }
    }
}
