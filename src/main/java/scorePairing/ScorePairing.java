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
package scorePairing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import parameters.AlgoParameters;
import pointWithProperties.CollectionOfPointsWithPropertiesIfc;
import pointWithProperties.PointWithPropertiesIfc;
import shapeCompare.PairingAndNullSpaces;
import shapeCompare.ResultsFromEvaluateCost;

public class ScorePairing {

    //-------------------------------------------------------------
    // Class variables
    //-------------------------------------------------------------
    private Map<Integer, PointWithPropertiesIfc> queryShape;
    private Map<Integer, PointWithPropertiesIfc> hitShape;
    private AlgoParameters algoParameters;


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public ScorePairing(Map<Integer, PointWithPropertiesIfc> queryShape, Map<Integer, PointWithPropertiesIfc> hitShape, AlgoParameters algoParameters) {
        this.queryShape = queryShape;
        this.hitShape = hitShape;
        this.algoParameters = algoParameters;
    }


    // -------------------------------------------------------------------
    // Public && Interface method
    // -------------------------------------------------------------------
    public ScorePairing(CollectionOfPointsWithPropertiesIfc queryShape, CollectionOfPointsWithPropertiesIfc hitShape, AlgoParameters algoParameters) {

        Map<Integer, PointWithPropertiesIfc> tempMapShape1 = new HashMap<>();
        Map<Integer, PointWithPropertiesIfc> tempMapShape2 = new HashMap<>();

        for (int i = 0; i < queryShape.getSize(); i++) {
            tempMapShape1.put(i, queryShape.getPointFromId(i));
        }
        for (int i = 0; i < hitShape.getSize(); i++) {
            tempMapShape2.put(i, hitShape.getPointFromId(i));
        }
        this.queryShape = tempMapShape1;
        this.hitShape = tempMapShape2;
        this.algoParameters = algoParameters;
    }


    public ResultsFromEvaluateCost getCostOfaPairing(PairingAndNullSpaces pairingAndNullSpacesToBeScored) {

        return ScorePairingTools.getCostOfaPairing(pairingAndNullSpacesToBeScored, queryShape, hitShape, algoParameters);
    }


    public List<ResultsFromEvaluateCost> getCostOfaListOfPairing(List<PairingAndNullSpaces> listPairingAndNullSpacesToBeScored) {

        return ScorePairingTools.getCostOfaListOfPairing(listPairingAndNullSpacesToBeScored, queryShape, hitShape, algoParameters);
    }
}
