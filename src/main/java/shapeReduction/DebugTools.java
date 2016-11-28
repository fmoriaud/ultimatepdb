package shapeReduction;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class DebugTools {

	public static void printToConsole(Map<PhiThetaRadiusInterval, List<String>> mapGroupPropertiesForThisGroup ){

		for (Entry<PhiThetaRadiusInterval, List<String>> entry: mapGroupPropertiesForThisGroup.entrySet()){
			System.out.println(entry.getKey().toString());
			for(String strikingProperties: entry.getValue()){
				System.out.println(strikingProperties + " ");
			}
		}
	}



	public static void printToConsole(List<List<PhiThetaRadiusInterval>> listOfListOfGroupToMerge){

		int count = 0;
		for (List<PhiThetaRadiusInterval> listToMerge: listOfListOfGroupToMerge){
			System.out.println("nextListToMerge contains " + listToMerge.size() + " sectors");
			for (PhiThetaRadiusInterval phiThetaIntervalWithRinterval : listToMerge){
				System.out.println(phiThetaIntervalWithRinterval.toString());
				count += 1;
			}
		}
		System.out.println("count in listOfListOfGroupToMerge " + count);
	}

}
