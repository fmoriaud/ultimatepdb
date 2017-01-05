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
