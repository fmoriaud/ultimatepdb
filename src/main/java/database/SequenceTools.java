package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import mystructure.MyChainIfc;
import mystructure.MyMonomerIfc;

public class SequenceTools {

	public static String generateSequence(MyChainIfc chain){

		StringBuffer stringBuffer = new StringBuffer();
		for (MyMonomerIfc monomer: chain.getMyMonomers()){
			String threeLetterCode = String.valueOf(monomer.getThreeLetterCode());
			if (threeLetterCode.length() == 1){
				threeLetterCode = "  " + threeLetterCode;
			}
			if (threeLetterCode.length() == 2){
				threeLetterCode = " " + threeLetterCode;
			}
			if (threeLetterCode.length() > 3){
				System.out.println("residue 3 letter code has 4 or more ...");
				System.exit(0);
			}
			stringBuffer.append(threeLetterCode);
			//stringBuffer.append(" ");
		}
		String sequence = stringBuffer.toString();
		return sequence;
	}



	public static List<String> getHydrophobicResiduesList(){

		List<String> hydrophobicResidues = new ArrayList<>();
		hydrophobicResidues.add("GLY");
		hydrophobicResidues.add("ALA");
		hydrophobicResidues.add("VAL");
		hydrophobicResidues.add("LEU");
		hydrophobicResidues.add("ILE");
		hydrophobicResidues.add("MET");
		hydrophobicResidues.add("SEM");
		hydrophobicResidues.add("PHE");
		hydrophobicResidues.add("TRP");

		return hydrophobicResidues;
	}



	public static List<String> getAllResiduesList(){

		List<String> listAllResidues = new ArrayList<>();
		listAllResidues.add("TRP");
		listAllResidues.add("PHE");
		listAllResidues.add("TYR");
		listAllResidues.add("ILE");
		listAllResidues.add("VAL");
		listAllResidues.add("LEU");
		listAllResidues.add("MET");
		listAllResidues.add("ASP");
		listAllResidues.add("GLU");
		listAllResidues.add("ALA");
		listAllResidues.add("PRO");
		listAllResidues.add("HIS");
		listAllResidues.add("LYS");
		listAllResidues.add("ARG");
		listAllResidues.add("SER");
		listAllResidues.add("THR");
		listAllResidues.add("ASN");
		listAllResidues.add("GLN");

		return listAllResidues;
	}


	public static List<String> generateNonEquivalentResidues(String inputResidue){

		List<String> eqResidues = generateEquivalentResidues(inputResidue);
		List<String> allResidues = getAllResiduesList();
		for (String eqRes: eqResidues){
			allResidues.remove(eqRes);
		}
		return allResidues;
	}



	public static List<String> generateEquivalentResidues(String inputResidue){

		List<String> equivalentResidues = new ArrayList<>();

		equivalentResidues.add(inputResidue);

		switch (inputResidue) {

		case "TRP":  equivalentResidues.add("PHE");
		equivalentResidues.add("TYR");
		return equivalentResidues;

		case "PHE":  equivalentResidues.add("TRP");
		equivalentResidues.add("TYR");
		equivalentResidues.add("ILE");
		return equivalentResidues;

		case "TYR":  equivalentResidues.add("TRP");
		equivalentResidues.add("PHE");
		return equivalentResidues;

		case "ILE":  equivalentResidues.add("PHE");
		equivalentResidues.add("VAL");
		equivalentResidues.add("LEU");
		equivalentResidues.add("MET");
		return equivalentResidues;

		case "VAL":  equivalentResidues.add("ILE");
		equivalentResidues.add("ALA");
		return equivalentResidues;

		case "LEU":  equivalentResidues.add("ILE");
		equivalentResidues.add("MET");
		return equivalentResidues;

		case "MET":  equivalentResidues.add("ILE");
		equivalentResidues.add("LEU");
		return equivalentResidues;

		case "ASP":  equivalentResidues.add("GLU");
		return equivalentResidues;

		case "GLU":  equivalentResidues.add("ASP");
		return equivalentResidues;

		case "ALA":  equivalentResidues.add("PRO");
		equivalentResidues.add("VAL");
		equivalentResidues.add("THR");
		return equivalentResidues;

		case "PRO":  equivalentResidues.add("ALA");
		return equivalentResidues;

		case "HIS":  equivalentResidues.add("LYS");
		equivalentResidues.add("ARG");
		return equivalentResidues;

		case "LYS":  equivalentResidues.add("HIS");
		equivalentResidues.add("ARG");
		return equivalentResidues;

		case "ARG":  equivalentResidues.add("HIS");
		equivalentResidues.add("LYS");
		return equivalentResidues;

		case "SER":  equivalentResidues.add("THR");
		return equivalentResidues;

		case "THR":  equivalentResidues.add("SER");
		equivalentResidues.add("ALA");
		return equivalentResidues;

		case "ASN":  equivalentResidues.add("GLN");
		return equivalentResidues;

		case "GLN":  equivalentResidues.add("ASN");
		return equivalentResidues;

		}

		return equivalentResidues;
	}



	public static List<List<String>> generateEquivalentResidues(){

		List<List<String>> equivalentResidues = new ArrayList<>();

		List<String> list1 = getHydrophobicResiduesList();
		equivalentResidues.add(list1);

		List<String> list2 = new ArrayList<>();
		list2.add("SER");
		list2.add("CYS");
		list2.add("THR");
		equivalentResidues.add(list2);

		List<String> list3 = new ArrayList<>();
		list3.add("PHE");
		list3.add("TYR");
		list3.add("TRP");
		equivalentResidues.add(list3);

		List<String> list4 = new ArrayList<>();
		list4.add("HIS");
		list4.add("LYS");
		list4.add("ARG");
		equivalentResidues.add(list4);

		List<String> list5 = new ArrayList<>();
		list5.add("ASP");
		list5.add("ASJ");
		list5.add("GLU");
		list5.add("ASN");
		list5.add("GLN");
		equivalentResidues.add(list5);

		return equivalentResidues;
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