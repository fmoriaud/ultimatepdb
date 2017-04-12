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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;


public class PointWithProperties implements PointWithPropertiesIfc, Serializable {
    //-------------------------------------------------------------
    // Class variables
    //-------------------------------------------------------------
    private PointIfc coords;
    private List<StrikingProperties> strikingProperties;
    private float distanceToLigand;
    private float electronProbability;
    private HashMap<PropertyName, Float> propertiesValues;


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public PointWithProperties() {
        this.propertiesValues = new LinkedHashMap<>();
    }


    // -------------------------------------------------------------------
    // Public Interface
    // -------------------------------------------------------------------
    @Override
    public Float get(PropertyName propertyName) {
        return this.propertiesValues.get(propertyName);
    }


    @Override
    public void put(PropertyName propertyName, Float propertyValue) {
        this.propertiesValues.put(propertyName, propertyValue);
    }

    @Override
    public PointIfc getCoords() {
        return coords;
    }


    @Override
    public void setCoords(PointIfc coords) {
        this.coords = coords;
    }


    @Override
    public List<StrikingProperties> getStrikingProperties() {
        return strikingProperties;
    }


    @Override
    public void setStrikingProperties(List<StrikingProperties> strikingProperties) {
        this.strikingProperties = strikingProperties;
    }


    @Override
    public float getDistanceToLigand() {
        return distanceToLigand;
    }


    @Override
    public void setDistanceToLigand(float distanceToLigand) {
        this.distanceToLigand = distanceToLigand;
    }


    @Override
    public float getElectronProbability() {
        return electronProbability;
    }


    @Override
    public void setElectronProbability(float electronProbability) {
        this.electronProbability = electronProbability;
    }


    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");

        result.append(distanceToLigand + " A ");
        result.append(" Proba = " + electronProbability);
        result.append(" " + this.getStrikingProperties());
        result.append("}");

        return result.toString();
    }
}
