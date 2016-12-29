package shapeCompare;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import fingerprint.CannotCompareDistributionException;
import fingerprint.DistributionComparisonTools;
import hits.Hit;
import parameters.AlgoParameters;
import scorePairing.CheckDistanceToOutside;
import shape.ShapeContainerIfc;

public class ComparatorShapeContainerQueryVsAnyShapeContainer {
    //------------------------
    // Constant
    //------------------------
    public boolean debug = false;
    private List<Hit> emptyList = new ArrayList<>();


    //------------------------
    // Class variables
    //------------------------
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

        // Remove hit where hit is not enough matching query based on covergage
        //int before = resultsExtendedPairing.size();
        Iterator<ResultsFromEvaluateCost> it = resultsExtendedPairing.iterator();
        while (it.hasNext()) {

            ResultsFromEvaluateCost nextResult = it.next();
            float fractionNeededOnHit = algoParameters.getFRACTION_NEEDED_ON_QUERY();
            float ratioPairedPointInQuery = nextResult.getRatioPairedPointInQuery();

            CheckDistanceToOutside checkDistanceToOutside = new CheckDistanceToOutside(nextResult.getPairingAndNullSpaces(), shapeContainerQuery, shapeContainerAnyShape);

            boolean isDistanceToOutsideOk = checkDistanceToOutside.isDistanceOk();

            if (ratioPairedPointInQuery < fractionNeededOnHit || isDistanceToOutsideOk == false) {
                it.remove();
                continue;
            }
        }

        List<Hit> hitsExtendedPairing = PairingTools.generateHitsListFromResultList(resultsExtendedPairing, shapeContainerAnyShape, shapeContainerQuery, algoParameters);
        Collections.sort(hitsExtendedPairing, new PairingTools.LowestCostHitComparator());

//		System.out.println("hits : ");
//		for (Hit hit: hitsExtendedPairing){
//			System.out.println(hit.getResultsFromEvaluateCost().getCoverage());
//		}
        if (hitsExtendedPairing.size() > 0) {
            System.out.println("hitsExtendedPairing.size() > 0");
        }
        return hitsExtendedPairing;
    }




//	private boolean isItTooCloseToOneAlreadyThere(List<ResultsFromEvaluateCost> listResults, ResultsFromEvaluateCost candidateResult){
//
//		for (ResultsFromEvaluateCost resultFromList: listResults){
//
//			double distanceR = computeDistanceOnRotMatrix(resultFromList, candidateResult);
//			double distanceT = computeDistanceOnTranslation(resultFromList, candidateResult);
//
//			//System.out.println("distance R = " + distanceR + "  distanceT = " + distanceT);
//			if (distanceT < 2.0 * algoParameters.getCELL_DIMENSION_OF_THE_PROBABILITY_MAP_ANGSTROM() && distanceR < 60.0 / 180.0){ // 2.8 / 6.0
//				return true;
//			}
//		}
//		return false;
//	}


}
