package fingerprint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pointWithProperties.CollectionOfPointsWithPropertiesIfc;
import pointWithProperties.PointIfc;
import pointWithProperties.PointWithPropertiesIfc;
import pointWithProperties.PointsTools;
import pointWithProperties.StrikingProperties;
import pointWithProperties.StrikingPropertiesTools;

public class ShapeFingerprint {
	// -------------------------------------------------------------------
	// Class variables
	// -------------------------------------------------------------------
	private Map<Integer, PointWithPropertiesIfc> miniShape;
	private CollectionOfPointsWithPropertiesIfc shape;
	private List<Integer> histogramStrikingProperties;
	private List<Integer> histogramD2;



	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public ShapeFingerprint(Map<Integer, PointWithPropertiesIfc> shape){
		this.miniShape = shape;
	}


	public ShapeFingerprint(CollectionOfPointsWithPropertiesIfc shape){
		this.shape = shape;
	}


	// -------------------------------------------------------------------
	// Public Interface
	// -------------------------------------------------------------------
	public void compute(){
		this.histogramStrikingProperties = computeHistogramStrikingPropertiesWithMinishape(); // computeHistogramStrikingPropertiesWithMinishape();
		this.histogramD2 = computeHistogramD2WithMinishape(); // computeHistogramD2WithMinishape();
	}




	// -------------------------------------------------------------------
	// Implementation
	// -------------------------------------------------------------------
	private List<Integer> computeHistogramStrikingPropertiesWithMinishape(){

		List<Integer> histogramStrikingProperties = new ArrayList<>();

		StrikingProperties[] strikingProperties = StrikingProperties.values();
		for (StrikingProperties strikingProperty: strikingProperties){
			if (! strikingProperty.equals(StrikingProperties.NONE)){
				Map<Integer, PointWithPropertiesIfc> points = StrikingPropertiesTools.extractPointsHavingTheProperty(miniShape, strikingProperty);
				histogramStrikingProperties.add(points.size());
			}
		}
		return histogramStrikingProperties;
	}



	private List<Integer> computeHistogramD2WithMinishape(){

		List<Integer> histogramD2 = new ArrayList<>();
		List<PointIfc> points = PointsTools.createListOfPointIfcFromShape(miniShape);

		List<Float> computelListDistanceBetweenTwoLists = SimilarityTools.computelListDistanceBetweenTwoLists(points, points, 2.0);
		int sizeList = 10;
		float distanceStep = (float) 20.0f / sizeList;
		float startAt = (float) 2.0;
		List<Integer> distributionDistance = SimilarityTools.binValues(computelListDistanceBetweenTwoLists, sizeList, distanceStep, startAt);
		histogramD2.addAll(distributionDistance);

		return histogramD2;
	}




	// -------------------------------------------------------------------
	// Getter & Setter
	// -------------------------------------------------------------------
	public List<Integer> getHistogramStrikingProperties() {
		return histogramStrikingProperties;
	}

	public List<Integer> getHistogramD2() {
		return histogramD2;
	}
}
