package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FinSequenceInDatabaseTools {
	public static String returnSequenceInDbifFourLetterCodeAndChainfoundInDatabase(String fourLetterCode, String chainName, 
			Connection connexion){
		
		String sequenceInDb = null;
		try {
			Statement stmt = connexion.createStatement();
			String findEntry = "SELECT * from sequence WHERE fourLettercode = '" + fourLetterCode + "' and chainId = '" + chainName + "'" ;
			ResultSet resultFindEntry = stmt.executeQuery(findEntry);
			int foundEntriesCount = 0;
			String fourLetterCodeFromDB;
			String chainIdFromDB;
			if (resultFindEntry.next()){
				foundEntriesCount+=1;

				fourLetterCodeFromDB = resultFindEntry.getString(1);
				chainIdFromDB = resultFindEntry.getString(2);
				sequenceInDb = resultFindEntry.getString(3);
			}

			if (foundEntriesCount != 1){
				System.out.println("problem isFourLetterCodeAndChainfoundInDatabase " + fourLetterCode + "  " + chainName + "  " + foundEntriesCount);
				return null;
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		return sequenceInDb;
	}



	public static List<HitInSequenceDb> find(int minLength, int maxLength, String sequenceToFind, boolean useSimilarSequences){

		List<HitInSequenceDb> hitsInSequenceDb = new ArrayList<>();

		Connection connexion = DatabaseTools.getNewConnection();

		List<String> listFourLetterCodeFromDB = new ArrayList<>();
		List<String> listChainIdFromDB = new ArrayList<>();
		List<String> listSequence = new ArrayList<>();

		Statement stmt;
		try {
			stmt = connexion.createStatement();
			String findEntry = "SELECT * from sequence";
			ResultSet resultFindEntry = stmt.executeQuery(findEntry);

			while(resultFindEntry.next()){

				// check if all ok

				listFourLetterCodeFromDB.add(resultFindEntry.getString(1));
				listChainIdFromDB.add(resultFindEntry.getString(2));
				listSequence.add(resultFindEntry.getString(3));

				if (listSequence.size() != listChainIdFromDB.size() || 
						listSequence.size() != listFourLetterCodeFromDB.size() ||
						listChainIdFromDB.size() != listFourLetterCodeFromDB.size()
						){
					System.out.println("big pb in FinSequenceInDatabaseTools.find() Terminating program");
					System.out.println();
					System.exit(0);
				}
			}
		} catch (SQLException e1) {
			System.out.println("Exception in reading whole content of DB. Program terminated");
			System.exit(0);

		}

		int sequenceToFindLength = sequenceToFind.length() / 3;
		
		for (int i=0 ; i< listSequence.size(); i++){
			String fourLetterCode = listFourLetterCodeFromDB.get(i);

			
			String chainIdFromDB = listChainIdFromDB.get(i);
			String sequenceFromDB = listSequence.get(i);

			int peptideLength = sequenceFromDB.length() / 3;
			if (peptideLength < minLength || peptideLength > maxLength){
				continue;
			}

			List<Integer> rankIdList = findRankId(sequenceToFind, sequenceFromDB, useSimilarSequences);

			if (rankIdList.size() != 0){
				
				HitInSequenceDb HitInSequenceDb = new HitInSequenceDb(rankIdList, fourLetterCode, chainIdFromDB, sequenceToFindLength);
				hitsInSequenceDb.add(HitInSequenceDb);
			}
		}
		return hitsInSequenceDb;
	}



	public static List<Integer> findRankId(String sequenceToFind, String chainSequence, boolean useSimilarSequences){

		List<Integer> listMatchingRankId = new ArrayList<>();

		// split chain sequence into three letter codes
		// put the three letters in a list
		List<String> splitSequenceToFind = splitIntoThreeLetterCode(sequenceToFind);
		List<String> splitChainSequence = splitIntoThreeLetterCode(chainSequence);

		// Definition of equivalent
		//List<List<String>> equivalentResidues = SequenceTools.generateEquivalentResidues();


		// go through 
		A: for (int rankId = 0; rankId < splitChainSequence.size(); rankId ++){
			for (int i=0; i<splitSequenceToFind.size(); i++){

				// is that residue match the first of the sequenceToFind
				if (rankId + i >= splitChainSequence.size()){
					// reach end of chain so no match
					continue A;
				}
				String currentResidueFromChain = splitChainSequence.get(rankId + i);
				String currentResidueFromSequenceToFind = splitSequenceToFind.get(i);

				if (currentResidueFromSequenceToFind.equals("XXX")){
					// match for sure
					continue;
				}
				// check if 
				List<String> possibleEquivalent =null;
				if (useSimilarSequences == true){
					possibleEquivalent = SequenceTools.generateEquivalentResidues(currentResidueFromSequenceToFind);
				}else{
					possibleEquivalent = new ArrayList<>();
					possibleEquivalent.add(currentResidueFromSequenceToFind);
				}
				if (possibleEquivalent.contains(currentResidueFromChain)){

					// then we have a match for current i
					if (i==splitSequenceToFind.size()-1){
						// we have a match here !!!
						//System.out.println("match !!!");
						listMatchingRankId.add(rankId);
					}
					continue;

				}else{
					// we have no match so moveon
					continue A;
				}
			}
			rankId += 1;
		}

		return listMatchingRankId;
	}




	// -------------------------------------------------------------------
	// Implementation
	// -------------------------------------------------------------------
	private static List<String> splitIntoThreeLetterCode(String sequenceToFind) {
		// put the three letters in a list
		List<String> listThreeLetterCode = new ArrayList<>();
		int countResidue = sequenceToFind.length() / 3;
		for (int i=0; i<countResidue; i++){
			int start = i * 3;
			String threeLetterCode = sequenceToFind.substring(start, start +3);
			listThreeLetterCode.add(threeLetterCode);
		}
		return listThreeLetterCode;
	}
}
