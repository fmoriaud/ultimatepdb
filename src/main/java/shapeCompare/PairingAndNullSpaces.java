package shapeCompare;

import java.util.List;
import java.util.Map;

public class PairingAndNullSpaces {


	private List<Integer> nullSpaceOfMap1;
	private List<Integer> nullSpaceOfMap2;
	
	private Map<Integer,Integer> pairing;
	


	public PairingAndNullSpaces(Map<Integer,Integer> pairing, List<Integer> nullSpaceOfMap1,
			List<Integer> nullSpaceOfMap2	){

		this.nullSpaceOfMap1 = nullSpaceOfMap1;
		this.nullSpaceOfMap2 = nullSpaceOfMap2;
		this.pairing = pairing;

	}




	// -------------------------------------------------------------------
	// Overide
	// -------------------------------------------------------------------
	@Override
	public String toString() {
		return String.format("pair count = " + pairing.size() + " null space map 1 count = " + nullSpaceOfMap1.size() + " null space map 2 count = " + nullSpaceOfMap2.size());
	}

	
	
	
	// -------------------------------------------------------------------
	// Getters and Setters
	// -------------------------------------------------------------------
	public List<Integer> getNullSpaceOfMap1() {
		return nullSpaceOfMap1;
	}
	public List<Integer> getNullSpaceOfMap2() {
		return nullSpaceOfMap2;
	}
	public void setNullSpaceOfMap1(List<Integer> nullSpaceOfMap1) {
		this.nullSpaceOfMap1 = nullSpaceOfMap1;
	}
	public void setNullSpaceOfMap2(List<Integer> nullSpaceOfMap2) {
		this.nullSpaceOfMap2 = nullSpaceOfMap2;
	}
	public Map<Integer, Integer> getPairing() {
		return pairing;
	}

	public void setPairing(Map<Integer, Integer> pairing) {
		this.pairing = pairing;
	}

}
