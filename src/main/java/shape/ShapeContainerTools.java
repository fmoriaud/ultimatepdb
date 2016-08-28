package shape;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hits.HitTools;
import parameters.AlgoParameters;
import shapeCompare.ResultsFromEvaluateCost;
import structure.ExceptionInMyStructurePackage;
import structure.MyAtomIfc;
import structure.MyBondIfc;
import structure.MyChainIfc;
import structure.MyMonomerIfc;
import structure.MyStructureTools;

public class ShapeContainerTools {

	public static List<String> makeContentPDBFilePeptide(AlgoParameters algoParameters, MyChainIfc peptide){
		List<String> contentPDBFilePeptide = new ArrayList<>();
		List<String> secondaryStructureLines = generateSecStrucLines(peptide);
		contentPDBFilePeptide.addAll(secondaryStructureLines);

		for (MyMonomerIfc monomer: peptide.getMyMonomers()){
			contentPDBFilePeptide.addAll(generateLinesFromMyMonomer(monomer, algoParameters, peptide.getChainId()));
		}
		contentPDBFilePeptide.addAll(generateConnectLines(peptide));
		return contentPDBFilePeptide;
	}



	public static List<String> makeContentRotatedPDBFilePeptide(AlgoParameters algoParameters, ResultsFromEvaluateCost result, MyChainIfc peptide){

		MyChainIfc peptideRotated = HitTools.returnCloneRotatedPeptide(peptide, result, algoParameters);

		List<String> contentPDBFilePeptide = new ArrayList<>();
		List<String> secondaryStructureLines = generateSecStrucLines(peptide);
		contentPDBFilePeptide.addAll(secondaryStructureLines);

		for (MyMonomerIfc monomer: peptideRotated.getMyMonomers()){
			contentPDBFilePeptide.addAll(generateLinesFromMyMonomer(monomer, algoParameters, peptide.getChainId()));
		}
		contentPDBFilePeptide.addAll(generateConnectLines(peptide));
		return contentPDBFilePeptide;
	}



	// this method could be merged to the one below but maybe I'll get different needs
	// for peptide with hetatm residues and hetatmligands
	public static List<String> generateConnectLines(MyMonomerIfc hetatmLigand){

		List<String> connectLines = new ArrayList<>();

		if (! Arrays.equals(hetatmLigand.getType(), "hetatm".toCharArray())){
			return connectLines; // CONNECT lines only for hetatm
		}
		for (MyAtomIfc myAtom: hetatmLigand.getMyAtoms()){
			MyBondIfc[] bonds = myAtom.getBonds();
			for (MyBondIfc bond: bonds){
				String connectLine = "CONECT" + 
						formatStringAlignedRight ( Integer.toString( myAtom.getOriginalAtomId() ), 5 ) +
						formatStringAlignedRight ( Integer.toString( bond.getBondedAtom().getOriginalAtomId() ), 5 );
				connectLines.add(connectLine);
			}
			// CONECT 1023 3769
		}
		return connectLines;
	}



	private static List<String> generateConnectLines(MyChainIfc myChain){

		List<String> connectLines = new ArrayList<>();

		for (MyMonomerIfc monomer: myChain.getMyMonomers()){
			if (! Arrays.equals(monomer.getType(), "hetatm".toCharArray())){
				continue; // CONNECT lines only for hetatm
			}
			for (MyAtomIfc myAtom: monomer.getMyAtoms()){
				MyBondIfc[] bonds = myAtom.getBonds();
				for (MyBondIfc bond: bonds){
					if (bond.getBondedAtom() != null){
						String connectLine = "CONECT" + 
								formatStringAlignedRight ( Integer.toString( myAtom.getOriginalAtomId() ), 5 ) +
								formatStringAlignedRight ( Integer.toString( bond.getBondedAtom().getOriginalAtomId() ), 5 );
						connectLines.add(connectLine);
					}else{
						System.out.println("problem generateConnectLines");
						System.out.println();
					}
				}
				// CONECT 1023 3769
			}
		}
		return connectLines;
	}



	public static List<String> generateLinesFromMyMonomer(MyMonomerIfc myMonomer, AlgoParameters algoParameters, char[] chainId){
		List<String> listOfLines = new ArrayList<>();
		MyMonomerIfc monomer;
		try {
			monomer = MyStructureTools.returnCloneMonomer(myMonomer, algoParameters);
		} catch (ExceptionInMyStructurePackage e1) {
			e1.printStackTrace();
			return listOfLines;
		}
		
		try{
			listOfLines = makePDBFileTestOutputFromAMonomer(monomer, 0, chainId);
		} catch(Exception e){
			e.printStackTrace();
			System.out.println();
		}

		return listOfLines;
	}



	private static List<String> makePDBFileTestOutputFromAMonomer(MyMonomerIfc monomer, int atomCount, char[] chainId) {

		int monomerSize = monomer.getMyAtoms().length;

		List<String> listOfLines = new ArrayList<>();

		String chainName = null;

		if (monomer.getParent() != null){
			chainName = String.valueOf(monomer.getParent().getChainId());
		}else{
			chainName = String.valueOf(chainId); // if not stop here then remove chainId
		}

		String threeLetterCode = String.valueOf(monomer.getThreeLetterCode());
		int residueID = monomer.getResidueID();

		char insertionLetterChar = monomer.getInsertionLetter();
		String insertionLetter = " ";
		if (insertionLetterChar != 0){
			insertionLetter = String.valueOf(monomer.getInsertionLetter());
		}

		char[] type = monomer.getType();
		boolean lastAtom = false; // TODO no TER line done but should be but where to do so ??
		for (MyAtomIfc atom: monomer.getMyAtoms()){
			listOfLines.addAll(makePDBFileTestOutputFromAnAtom( atom, lastAtom, chainName, threeLetterCode, residueID, insertionLetter, type));
		}
		return listOfLines;
	}



	private static List<String> makePDBFileTestOutputFromAnAtom(MyAtomIfc atom, boolean lastAtom, String chainName, String threeLetterCode, int residueID, String insertionLetter, char[] type) {

		List<String> listLines = new ArrayList<String>();
		String line = "";
		// ATOM     28  C   LEU A 818       1.556  48.862   0.394  1.00 60.44           C
		if (Arrays.equals(type, "amino".toCharArray())){
			line = "ATOM  ";
		}
		if (Arrays.equals(type, "hetatm".toCharArray())){
			line = "HETATM";
		}
		line += formatStringAlignedRight ( Integer.toString( atom.getOriginalAtomId() ), 5 );
		line += " ";
		line += formatStringAlignedLeft ( String.valueOf(atom.getAtomName()) , 4);
		//System.out.println( formatStringAlignedLeft ( getAtomType( atom ),4 ));
		line += " "; // 17 is alternate location
		String residueType = formatStringAlignedRight ( threeLetterCode, 3 ) ;
		//System.out.println("residue type = " + residueType);
		line +=residueType ;

		line += " "; // 21 is empty
		//System.out.println("Chain ID = " + chainID);
		line += chainName ;
		//System.out.println("Chain ID when about to create ATOM line  = " + chainID);
		String residueIDString = formatStringAlignedRight ( Integer.toString(residueID), 4 );
		line += residueIDString;

		//System.out.println(line);
		// 27 is Code for insertion of residues
		line += insertionLetter; 
		line += "   "; // 3 spaces
		// 7 char per number
		// I put 3 digit after coma and align right on 7
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
		decimalFormatSymbols.setDecimalSeparator('.');
		DecimalFormat myFormatter = new DecimalFormat("0.000");
		myFormatter.setDecimalFormatSymbols(decimalFormatSymbols);

		String output = myFormatter.format(atom.getCoords()[0]);
		line += formatStringAlignedRight (output, 8);
		output = myFormatter.format(atom.getCoords()[1]);
		line += formatStringAlignedRight (output, 8);
		output = myFormatter.format(atom.getCoords()[2]);
		line += formatStringAlignedRight (output, 8);

		line += "      "; // 6 spaces for occupancy 
		line += "      "; // 6 spaces for temp factor 
		line += "          "; // 10 spaces for nada
		output = String.valueOf(atom.getElement());
		line += formatStringAlignedRight (output, 2);

		listLines.add(line);

		if (lastAtom == true){
			String lastToFinishTER = formatStringAlignedRight ( threeLetterCode, 3 ) + " " + chainName + residueIDString;
			listLines.add(lastToFinishTER);
		}
		return listLines;
	}



	private static List<String> generateSecStrucLines(MyChainIfc inputChain){

		List<Integer> helixIndices = new ArrayList<>();
		List<Integer> strandIndices = new ArrayList<>();
		List<Integer> turnIndices = new ArrayList<>();

		for (MyMonomerIfc monomer: inputChain.getMyMonomers()){

			char[] secStruct = monomer.getSecStruc();

			if (Arrays.equals(secStruct, "H".toCharArray())){
				helixIndices.add(monomer.getResidueID());

			}else{
				if (Arrays.equals(secStruct, "S".toCharArray())){
					strandIndices.add(monomer.getResidueID());
				}else{
					if (Arrays.equals(secStruct, "T".toCharArray())){
						turnIndices.add(monomer.getResidueID());
					}
				}
			}
		}

		List<List<Integer>> listsStartEndHelices = buildListStartEnd(helixIndices);
		List<List<Integer>> listsStartEndStrand = buildListStartEnd(strandIndices);
		List<List<Integer>> listsStartEndTurn = buildListStartEnd(turnIndices);

		List<String> secondaryStructureLines = new ArrayList<>();


		int countHelices = 1;
		for (List<Integer> startEndHelices: listsStartEndHelices){

			String generatedLine = generateHelixLine(startEndHelices, countHelices, inputChain);
			secondaryStructureLines.add("HELIX " + generatedLine);
			countHelices += 1;
		}

		int countStrand = 1;
		for (List<Integer> startEndStrand: listsStartEndStrand){

			String generatedLine = generateSheetLine(startEndStrand, countStrand, inputChain);
			secondaryStructureLines.add("SHEET " + generatedLine);
			countStrand += 1;
		}
		//STRAND   2   2 TRP A   94  ILE A   96  1                                  3
		//HELIX   11  11 ALA C   37  LYS C   39  5                                   3    
		//SHEET    1   A 5 SER A  50  ILE A  55  0 

		//		int countTurn = 1;
		//		for (List<Integer> startEndTurn: listsStartEndTurn){
		//
		//			String generatedLine = generateLine(startEndTurn, countTurn, inputChain);
		//			secondaryStructureLines.add("TURN  " + generatedLine);
		//			countStrand += 1;
		//		}
		//secondaryStructureLines.add("HELIX    1   9 ASP B   55  LEU B   60  1");


		return secondaryStructureLines;
	}



	private static String generateSheetLine(List<Integer> startEndHelices, int countHelices, MyChainIfc inputChain){

		StringBuffer sb = new StringBuffer();
		sb.append(" ");
		sb.append(formatStringAlignedRight(String.valueOf(countHelices), 3));
		sb.append(" ");
		sb.append(formatStringAlignedRight(String.valueOf(countHelices), 3)); // idem 
		sb.append(" 1"); // number of strands in sheet. I put one but i dont know
		sb.append(" ");
		MyMonomerIfc monomerStart = inputChain.getMyMonomerFromResidueId(startEndHelices.get(0));
		sb.append(String.valueOf(monomerStart.getThreeLetterCode()));
		sb.append(" ");
		sb.append(String.valueOf(inputChain.getChainId()));
		//sb.append(" ");
		sb.append(formatStringAlignedRight(String.valueOf(monomerStart.getResidueID()), 4));
		sb.append(" "); // insertioncode
		sb.append(" "); 
		MyMonomerIfc monomerEnd = inputChain.getMyMonomerFromResidueId(startEndHelices.get(1));
		sb.append(String.valueOf(monomerEnd.getThreeLetterCode()));
		sb.append(" ");
		sb.append(String.valueOf(inputChain.getChainId()));
		//sb.append(" ");
		sb.append(formatStringAlignedRight(String.valueOf(monomerEnd.getResidueID()), 4));
		sb.append(" "); // insCode
		sb.append(" 0"); // Sense of strand with respect to previous strand in the sheet. 0 if first strand, 1 if parallel, -1 if anti-parallel.

		return sb.toString();
	}



	private static String generateHelixLine(List<Integer> startEndHelices, int countHelices, MyChainIfc inputChain){

		StringBuffer sb = new StringBuffer();
		sb.append(" ");
		sb.append(formatStringAlignedRight(String.valueOf(countHelices), 3));
		sb.append(" ");
		sb.append(formatStringAlignedRight(String.valueOf(countHelices), 3)); // idem 
		sb.append(" ");
		MyMonomerIfc monomerStart = inputChain.getMyMonomerFromResidueId(startEndHelices.get(0));
		sb.append(String.valueOf(monomerStart.getThreeLetterCode()));
		sb.append(" ");
		sb.append(String.valueOf(inputChain.getChainId()));
		sb.append(" ");
		sb.append(formatStringAlignedRight(String.valueOf(monomerStart.getResidueID()), 4));
		sb.append(" "); // initIcode
		sb.append(" "); 
		MyMonomerIfc monomerEnd = inputChain.getMyMonomerFromResidueId(startEndHelices.get(1));
		sb.append(String.valueOf(monomerEnd.getThreeLetterCode()));
		sb.append(" ");
		sb.append(String.valueOf(inputChain.getChainId()));
		sb.append(" ");
		sb.append(formatStringAlignedRight(String.valueOf(monomerEnd.getResidueID()), 4));
		sb.append(" ");

		String helixClass = " 1";
		sb.append(helixClass);
		sb.append("                               "); 
		sb.append(formatStringAlignedRight(String.valueOf(startEndHelices.get(1) - startEndHelices.get(0) + 1), 5));

		return sb.toString();
	}



	private static List<List<Integer>> buildListStartEnd(List<Integer> secStrucIndices) {

		List<List<Integer>> listsStartEnd = new ArrayList<>();
		if (secStrucIndices.size() > 1){
			int start = secStrucIndices.get(0);
			int end;
			for (int i=1; i<secStrucIndices.size(); i++){
				if (secStrucIndices.get(i) > secStrucIndices.get(i-1) + 1){
					// end of secStruc but a new one is coming
					end = secStrucIndices.get(i-1);
					store(listsStartEnd, start, end);
					start = secStrucIndices.get(i);
					continue;
				}
				if ( i == secStrucIndices.size()-1){
					// end
					end = secStrucIndices.get(secStrucIndices.size()-1);
					store(listsStartEnd, start, end);
					break;
				}
			}
		}
		return listsStartEnd;
	}


	private static void store(List<List<Integer>> listsStartEnd, int start, int end){

		if (listsStartEnd.size() > 0){
			if (listsStartEnd.get(listsStartEnd.size()-1).contains(end)){
				return;
			}
		}
		List<Integer> listStartEnd = new ArrayList<>();
		listStartEnd.add(start);
		listStartEnd.add(end);
		listsStartEnd.add(listStartEnd);	
	}



	public static List<char[]> generatePeptideSequence(MyChainIfc peptide) {
		List<char[]> peptideSequence = new ArrayList<>();
		for (MyMonomerIfc monomer: peptide.getMyMonomers()){
			peptideSequence.add(monomer.getThreeLetterCode());
		}
		return peptideSequence;
	}

	

	private static String formatStringAlignedRight ( String input , int finalLength) {

		String returnString = "";

		int countOfSpacesToAdd = finalLength - input.length() ;
		for ( int i=0 ; i < countOfSpacesToAdd ; i++ ) {
			returnString += " ";
		}
		returnString += input ;
		return returnString;
	}



	private static String formatStringAlignedLeft ( String input , int finalLength) {

		String returnString = "";

		returnString += input.replaceAll(" ", ""); 
		int countOfSpacesToAdd = finalLength - input.length() ;
		for ( int i=0 ; i < countOfSpacesToAdd ; i++ ) {
			returnString += " ";
		}
		return returnString;
	}
}
