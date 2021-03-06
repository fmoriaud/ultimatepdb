/*
Author:
      Fabrice Moriaud <fmoriaud@ultimatepdb.org>

  Copyright (c) 2016 Fabrice Moriaud

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
  */
package shapeReduction;

import org.junit.Test;
import pointWithProperties.PointWithProperties;
import pointWithProperties.PointWithPropertiesIfc;
import pointWithProperties.StrikingProperties;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class ShapeReductorToolsTest {

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
