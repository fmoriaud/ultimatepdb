package database;

import java.util.ArrayList;
import java.util.List;

import structure.MyChainIfc;
import structure.MyMonomerIfc;

public class SequenceTools {

	public static String generateSequence(MyChainIfc chain){

		StringBuffer stringBuffer = new StringBuffer();
		for (MyMonomerIfc monomer: chain.getMyMonomers()){
			stringBuffer.append(String.valueOf(monomer.getThreeLetterCode()));
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
}
