package shape;

import java.util.List;
import java.util.Map;

import mystructure.MyMonomerIfc;
import parameters.AlgoParameters;
import pointWithProperties.CollectionOfPointsWithPropertiesIfc;
import pointWithProperties.PointWithPropertiesIfc;
import shapeCompare.ResultsFromEvaluateCost;
import shapeReduction.TriangleInteger;
import mystructure.MyStructureIfc;

public interface ShapeContainerIfc {


	String makeEndFileName();
	void exportShapeToPDBFile(String fileName, AlgoParameters algoParameters);
	
	void exportRotatedShapeToPDBFile(String fileName, AlgoParameters algoParameters, ResultsFromEvaluateCost result);
	void exportShapeColoredToPDBFile(String fileName, AlgoParameters algoParameters);
	void exportRotatedShapeColoredToPDBFile(String fileName, AlgoParameters algoParameters, ResultsFromEvaluateCost result);
	void exportMiniShapeToPDBFile(String fileName, AlgoParameters algoParameters);
	void exportRotatedMiniShapeToPDBFile(String fileName, AlgoParameters algoParameters, ResultsFromEvaluateCost result);
	void exportMiniShapeColoredToPDBFile(String fileName, AlgoParameters algoParameters);
	void exportRotatedMiniShapeColoredToPDBFile(String fileName, AlgoParameters algoParameters, ResultsFromEvaluateCost result);

	char[] getFourLetterCode();
	CollectionOfPointsWithPropertiesIfc getShape();
	Map<Integer, PointWithPropertiesIfc> getMiniShape();
	MyStructureIfc getMyStructureUsedToComputeShape();
	List<Integer> getHistogramStrikingProperties();
	List<Integer> getHistogramD2();
	List<TriangleInteger> getListTriangleOfPointsFromMinishape();
	PointWithPropertiesIfc get(int idPoint);

	List<MyMonomerIfc> getForeignMonomerToExclude();


}
