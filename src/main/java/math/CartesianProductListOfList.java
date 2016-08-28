package math;

import java.util.LinkedHashMap;
import java.util.Vector;

public class CartesianProductListOfList {
	//------------------------
	// Class variables
	//------------------------
	private LinkedHashMap<String, Vector<Double>> dataStructure;




	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public CartesianProductListOfList(LinkedHashMap<String, Vector<Double>> dataStructure){
		this.dataStructure = dataStructure;
	}



	public Double[][] allUniqueCombinations(){
		int n = dataStructure.keySet().size();
		int solutions = 1;

		for(Vector<Double> vector : dataStructure.values()) {
			solutions *= vector.size();            
		}

		Double[][] allCombinations = new Double[solutions + 1][];
		//allCombinations[0] = dataStructure.keySet().toArray(new Double[n]);

		for(int i = 0; i < solutions; i++) {
			Vector<Double> combination = new Vector<>(n);
			int j = 1;
			for(Vector<Double> vec : dataStructure.values()) {
				combination.add(vec.get((i/j)%vec.size()));
				j *= vec.size();
			}
			allCombinations[i + 1] = combination.toArray(new Double[n]);
		}

		return allCombinations;
	}
}