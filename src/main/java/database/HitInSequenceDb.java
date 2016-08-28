package database;

import java.util.List;

public class HitInSequenceDb {
	//------------------------
	// Class variables
	//------------------------
	private List<Integer> listRankIds;
	private String fourLetterCode;
	private String chainIdFromDB;
	private int peptideLength;





	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public HitInSequenceDb(List<Integer> listRankIds, String fourLetterCode, String chainIdFromDB, int peptideLength){
		this.listRankIds = listRankIds;
		this.fourLetterCode = fourLetterCode;
		this.chainIdFromDB = chainIdFromDB;
		this.peptideLength = peptideLength;
	}




	// -------------------------------------------------------------------
	// Getter & Setter
	// -------------------------------------------------------------------
	public List<Integer> getListRankIds() {
		return listRankIds;
	}


	public String getFourLetterCode() {
		return fourLetterCode;
	}


	public String getChainIdFromDB() {
		return chainIdFromDB;
	}


	public int getPeptideLength() {
		return peptideLength;
	}
}