package math;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AddToMap {


	public static <B, A> void addElementToAMapOfSet(Map<A, Set<B>> inputMap, A chainElementToAdd, B elementToAdd){

		if (inputMap.containsKey(chainElementToAdd)){
			inputMap.get(chainElementToAdd).add(elementToAdd);
		}else{
			Set<B> newSet = new HashSet<>();
			newSet.add(elementToAdd);
			inputMap.put(chainElementToAdd, newSet);
		}
	}


	
	public static <B, A> void addElementToAMapOfList(Map<A, List<B>> inputMap, A chainElementToAdd, B elementToAdd){

		if (inputMap.containsKey(chainElementToAdd)){
			inputMap.get(chainElementToAdd).add(elementToAdd);
		}else{
			List<B> newList = new ArrayList<>();
			newList.add(elementToAdd);
			inputMap.put(chainElementToAdd, newList);
		}
	}
}
