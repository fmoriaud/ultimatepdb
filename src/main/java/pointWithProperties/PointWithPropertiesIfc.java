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

import java.util.List;


public interface PointWithPropertiesIfc {


    Float get(PropertyName propertyName);

    void put(PropertyName propertyName, Float propertyValue);

    PointIfc getCoords();

    void setCoords(PointIfc coords);

    List<StrikingProperties> getStrikingProperties();

    void setStrikingProperties(List<StrikingProperties> strikingProperties);

    float getDistanceToLigand();

    void setDistanceToLigand(float distanceToLigand);

    float getElectronProbability();

    void setElectronProbability(float electronProbability);
}
