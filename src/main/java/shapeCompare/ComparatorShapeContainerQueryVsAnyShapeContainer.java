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
package shapeCompare;

import fingerprint.CannotCompareDistributionException;
import fingerprint.DistributionComparisonTools;
import hits.Hit;
import parameters.AlgoParameters;
import scorePairing.CheckDistanceToOutside;
import shape.ShapeContainerIfc;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ComparatorShapeContainerQueryVsAnyShapeContainer {
    //-------------------------------------------------------------
    // Class variables
    //-------------------------------------------------------------
    ShapeContainerIfc shapeContainerQuery;
    ShapeContainerIfc shapeContainerAnyShape;
    AlgoParameters algoParameters;


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public ComparatorShapeContainerQueryVsAnyShapeContainer(ShapeContainerIfc shapeContainerQuery, ShapeContainerIfc shapeContainerAnyShape, AlgoParameters algoParameters) {

        this.shapeContainerQuery = shapeContainerQuery;
        this.shapeContainerAnyShape = shapeContainerAnyShape;
        this.algoParameters = algoParameters;
    }


    // -------------------------------------------------------------------
    // Public && Interface method
    // -------------------------------------------------------------------
    public List<Hit> computeResults() throws NullResultFromAComparisonException {

        try {
            float tanimotoHistogramProperties = DistributionComparisonTools.computeSubDistributionTanimoto(shapeContainerQuery.getHistogramStrikingProperties(), shapeContainerAnyShape.getHistogramStrikingProperties());
            float tanimotoHistogramProperties2 = DistributionComparisonTools.computeSubDistributionTanimoto(shapeContainerQuery.getHistogramD2(), shapeContainerAnyShape.getHistogramD2());
            //float distance = DistributionComparisonTools.computeDistance(shapeContainerQuery.getHistogramStrikingProperties(), shapeContainerAnyShape.getHistogramStrikingProperties());
            //float distance2 = DistributionComparisonTools.computeDistance(shapeContainerQuery.getHistogramD2(), shapeContainerAnyShape.getHistogramD2());

            System.out.println("getHistogramStrikingProperties getHistogramD2");
            System.out.println("fingerprint = " + tanimotoHistogramProperties + "  " + tanimotoHistogramProperties2);
            //System.out.println("distance = " + distance + "  " + distance2);
            if (tanimotoHistogramProperties < 0.4 || tanimotoHistogramProperties2 < 0.6) {
                //List<Hit> emptyHitList = new ArrayList<>();
                System.out.println("comparison skipped because of Fingerprint ");
                //return emptyList;
            }

        } catch (CannotCompareDistributionException e2) {
            e2.printStackTrace();

        }

        List<ResultsFromEvaluateCost> resultsExtendedPairing = CompareTools.compare(shapeContainerQuery, shapeContainerAnyShape, algoParameters);

        Iterator<ResultsFromEvaluateCost> it = resultsExtendedPairing.iterator();
        while (it.hasNext()) {

            ResultsFromEvaluateCost nextResult = it.next();
            float fractionNeededOnQuery = algoParameters.getFRACTION_NEEDED_ON_QUERY();
            float ratioPairedPointInQuery = nextResult.getRatioPairedPointInQuery();

            CheckDistanceToOutside checkDistanceToOutside = new CheckDistanceToOutside(nextResult.getPairingAndNullSpaces(), shapeContainerQuery, shapeContainerAnyShape);

            boolean isDistanceToOutsideOk = checkDistanceToOutside.isDistanceOk();

            if (ratioPairedPointInQuery < fractionNeededOnQuery || isDistanceToOutsideOk == false) {
                it.remove();
                continue;
            }
        }

        List<Hit> hitsExtendedPairing = PairingTools.generateHitsListFromResultList(resultsExtendedPairing, shapeContainerAnyShape, shapeContainerQuery, algoParameters);
        Collections.sort(hitsExtendedPairing, new PairingTools.LowestCostHitComparator());

        return hitsExtendedPairing;
    }
}
