package genericBuffer;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.util.Pair;

import structure.MyStructureIfc;


public class MyStructureBuffer {


	private int capacity;
	private List<Pair<String, MyStructureIfc>> queue = new LinkedList<Pair<String, MyStructureIfc>>();


	public MyStructureBuffer(int capacity) {
		this.capacity = capacity;
	}


	public synchronized MyStructureIfc getStructure(char[] fourLettercode){

		String fourLettercodeString = String.valueOf(fourLettercode);
		for (Pair<String, MyStructureIfc> storeElement: queue){
			if (storeElement.getFirst().equals(fourLettercodeString)){
				return storeElement.getValue();
			}
		}
		return null;
	}



	public synchronized void putStructure(MyStructureIfc myStructure){

		boolean alreadyStored = false;
		for (Pair<String, MyStructureIfc> storeElement: queue){
			if (storeElement.getFirst().equals(String.valueOf(myStructure.getFourLetterCode()))){
				alreadyStored = true;
			}
		}
		if (alreadyStored == false){
			if (queue.size() == capacity){
				queue.remove(queue.remove(0));
			}
			Pair<String, MyStructureIfc> newElementToStore = new Pair<>(String.valueOf(myStructure.getFourLetterCode()), myStructure);
			queue.add(newElementToStore);
		}
	}
}
