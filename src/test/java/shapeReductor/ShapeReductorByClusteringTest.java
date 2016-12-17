package shapeReductor;

import org.junit.Test;
import pointWithProperties.PointWithProperties;
import pointWithProperties.PointWithPropertiesIfc;
import pointWithProperties.StrikingProperties;
import shapeReduction.ShapeReductorTools;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 13.12.16.
 */
public class ShapeReductorByClusteringTest {

    // Test also if two in common, then any point is returned

    @Test
    public void testReturnPointWithLowerPriorityHydrophobeAromatic() {

        StrikingProperties commonStrikingProperty = StrikingProperties.HYDROPHOBE;

        PointWithPropertiesIfc pointWithPropertiesWithLess = new PointWithProperties();
        List<StrikingProperties> strikingProperties1 = new ArrayList<>();
        strikingProperties1.add(commonStrikingProperty);
        PointWithPropertiesIfc pointWithPropertiesWithMore = new PointWithProperties();
        List<StrikingProperties> strikingProperties2 = new ArrayList<>();
        strikingProperties2.add(commonStrikingProperty);

        pointWithPropertiesWithLess.setStrikingProperties(strikingProperties1);
        strikingProperties2.add(StrikingProperties.AROMATICRING);
        pointWithPropertiesWithMore.setStrikingProperties(strikingProperties2);

        PointWithPropertiesIfc pointWithLowerPriority = ShapeReductorTools.returnPointWithLowerPriorityWhenThereIsAMatchingProperty(pointWithPropertiesWithLess, pointWithPropertiesWithMore);
        assertTrue(pointWithLowerPriority == pointWithPropertiesWithLess);
    }

    @Test
    public void testReturnPointWithLowerPriorityHydrophobeAromatic_Swapped() {

        StrikingProperties commonStrikingProperty = StrikingProperties.HYDROPHOBE;

        PointWithPropertiesIfc pointWithPropertiesWithLess = new PointWithProperties();
        List<StrikingProperties> strikingProperties1 = new ArrayList<>();
        strikingProperties1.add(commonStrikingProperty);
        PointWithPropertiesIfc pointWithPropertiesWithMore = new PointWithProperties();
        List<StrikingProperties> strikingProperties2 = new ArrayList<>();
        strikingProperties2.add(commonStrikingProperty);

        pointWithPropertiesWithLess.setStrikingProperties(strikingProperties1);
        strikingProperties2.add(StrikingProperties.AROMATICRING);
        pointWithPropertiesWithMore.setStrikingProperties(strikingProperties2);

        PointWithPropertiesIfc pointWithLowerPriority = ShapeReductorTools.returnPointWithLowerPriorityWhenThereIsAMatchingProperty(pointWithPropertiesWithMore, pointWithPropertiesWithLess);
        assertTrue(pointWithLowerPriority == pointWithPropertiesWithLess);
    }


    @Test
    public void testReturnPointWithLowerPriorityHDonnorHAcceptor() {

        StrikingProperties commonStrikingProperty = StrikingProperties.HBOND_ACCEPTOR;

        PointWithPropertiesIfc pointWithPropertiesWithLess = new PointWithProperties();
        List<StrikingProperties> strikingProperties1 = new ArrayList<>();
        strikingProperties1.add(commonStrikingProperty);
        PointWithPropertiesIfc pointWithPropertiesWithMore = new PointWithProperties();
        List<StrikingProperties> strikingProperties2 = new ArrayList<>();
        strikingProperties2.add(commonStrikingProperty);

        pointWithPropertiesWithLess.setStrikingProperties(strikingProperties1);
        strikingProperties2.add(StrikingProperties.HBOND_DONNOR);
        pointWithPropertiesWithMore.setStrikingProperties(strikingProperties2);

        PointWithPropertiesIfc pointWithLowerPriority = ShapeReductorTools.returnPointWithLowerPriorityWhenThereIsAMatchingProperty(pointWithPropertiesWithMore, pointWithPropertiesWithLess);
        assertTrue(pointWithLowerPriority == pointWithPropertiesWithLess);
    }


    @Test
    public void testReturnPointWithLowerPriorityHDonnorHAcceptor_Swapped() {

        StrikingProperties commonStrikingProperty = StrikingProperties.HBOND_ACCEPTOR;

        PointWithPropertiesIfc pointWithPropertiesWithLess = new PointWithProperties();
        List<StrikingProperties> strikingProperties1 = new ArrayList<>();
        strikingProperties1.add(commonStrikingProperty);
        PointWithPropertiesIfc pointWithPropertiesWithMore = new PointWithProperties();
        List<StrikingProperties> strikingProperties2 = new ArrayList<>();
        strikingProperties2.add(commonStrikingProperty);

        pointWithPropertiesWithLess.setStrikingProperties(strikingProperties1);
        strikingProperties2.add(StrikingProperties.HBOND_DONNOR);
        pointWithPropertiesWithMore.setStrikingProperties(strikingProperties2);

        PointWithPropertiesIfc pointWithLowerPriority = ShapeReductorTools.returnPointWithLowerPriorityWhenThereIsAMatchingProperty(pointWithPropertiesWithLess, pointWithPropertiesWithMore);
        assertTrue(pointWithLowerPriority == pointWithPropertiesWithLess);
    }

    @Test
    public void testReturnPointWithLowerPriorityHAcceptorHDonnor() {

        StrikingProperties commonStrikingProperty = StrikingProperties.HBOND_DONNOR;

        PointWithPropertiesIfc pointWithPropertiesWithLess = new PointWithProperties();
        List<StrikingProperties> strikingProperties1 = new ArrayList<>();
        strikingProperties1.add(commonStrikingProperty);
        PointWithPropertiesIfc pointWithPropertiesWithMore = new PointWithProperties();
        List<StrikingProperties> strikingProperties2 = new ArrayList<>();
        strikingProperties2.add(commonStrikingProperty);

        pointWithPropertiesWithLess.setStrikingProperties(strikingProperties1);
        strikingProperties2.add(StrikingProperties.HBOND_ACCEPTOR);
        pointWithPropertiesWithMore.setStrikingProperties(strikingProperties2);

        PointWithPropertiesIfc pointWithLowerPriority = ShapeReductorTools.returnPointWithLowerPriorityWhenThereIsAMatchingProperty(pointWithPropertiesWithLess, pointWithPropertiesWithMore);
        assertTrue(pointWithLowerPriority == pointWithPropertiesWithLess);
    }
}
