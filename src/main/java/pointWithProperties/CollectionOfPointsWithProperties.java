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
package pointWithProperties;

import java.io.Serializable;
import java.util.List;

public class CollectionOfPointsWithProperties implements CollectionOfPointsWithPropertiesIfc, Serializable {
    //-------------------------------------------------------------
    // Class variables
    //-------------------------------------------------------------
    private PointWithPropertiesIfc[] collectionOfPointWithProperties;


    //-------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------
    public CollectionOfPointsWithProperties(List<PointWithPropertiesIfc> listPointsWithProperties) {
        PointWithPropertiesIfc[] collectionOfPointsWithProperties = listPointsWithProperties.toArray(new PointWithProperties[listPointsWithProperties.size()]);
        this.collectionOfPointWithProperties = collectionOfPointsWithProperties;
    }


    //-------------------------------------------------------------
    // Interface & Public methods
    //-------------------------------------------------------------
    @Override
    public PointWithPropertiesIfc getPointFromId(int i) {
        if (i < collectionOfPointWithProperties.length) {
            return collectionOfPointWithProperties[i];
        }
        return null;
    }


    @Override
    public int getSize() {
        int size = this.collectionOfPointWithProperties.length;
        return size;
    }


    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(" Size: " + collectionOfPointWithProperties.length);
        return result.toString();
    }
}
