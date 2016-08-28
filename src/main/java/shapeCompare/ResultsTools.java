package shapeCompare;

import java.util.List;


public class ResultsTools {

	public static List<ResultsFromEvaluateCost> keepTopOfTheList(List<ResultsFromEvaluateCost> listResults, int countOfTopResultsToKeep){
		
		int index = Math.min(countOfTopResultsToKeep, listResults.size());
		List<ResultsFromEvaluateCost>  listTroncated = listResults.subList(0, index);

		return listTroncated;
	}
}
