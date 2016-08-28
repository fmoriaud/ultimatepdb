package structure;

import java.util.HashMap;
import java.util.Map;

public class EnumResidues {
	
	public enum HetatmResidues{
		HOH ("HOH", "O"),
		CL ("CL ", "Cl"),
		ACT ("ACT", "CH3"),
		ACE ("ACE", "CH3");
		

		private final String threeLetterCode ;
		private final String backBoneAtomName ;


		HetatmResidues ( String threeLetterCode, String backBoneAtomName) {
			this.threeLetterCode = threeLetterCode;
			this.backBoneAtomName = backBoneAtomName;

		}



		public String getbackBoneAtomName() {
			return backBoneAtomName;
		}



		public String getThreeLetterCode() {
			return threeLetterCode;
		}

	}

	public enum Residues {
		// from http://en.wikipedia.org/wiki/Amino_acid
		// Amino Acid	3-Letter[106] 1-Letter[106]	Side-chain polarity[106] Side-chain charge (pH 7.4)[106] Hydropathy index[107]
		// PMID 15189153.
		// [106] Hausman, Robert E.; Cooper, Geoffrey M. (2004). The cell: a molecular approach. Washington, D.C: ASM Press. p. 51. ISBN 0-87893-214-3.
		// [107] Kyte J, Doolittle RF (May 1982). "A simple method for displaying the hydropathic character of a protein". Journal of Molecular Biology 157 (1): 105�32. doi:10.1016/0022-2836(82)90515-0. PMID 7108955

		ALANINE ( "ALA" , "A" ,	"nonpolar" , "neutral" , "CB" ) ,		
		ARGININE ( "ARG" ,	"R" , "polar" ,	"positive" , "CG" ) ,		
		ASPARAGINE  ( "ASN" , "N" ,	"polar" , "neutral" , "CG" ) ,		
		ASPARTIC (	"ASP" ,	"D"	, "polar" ,	"negative"	, "CG") ,		
		CYSTEINE ( "CYS" , "C" , "nonpolar" , "neutral" , "CB" ) ,	
		GLUTAMIC ( "GLU" ,	"E" ,	"polar" ,	"negative" , "CB" ) ,		
		GLUTAMINE (	"GLN" ,	"Q" , "polar" ,	"neutral" , "CG" ) ,		
		GLYCINE	( "GLY" ,	"G" , "nonpolar" , "neutral" , "CA" ) ,		
		HISTIDINE	( "HIS" , "H" ,	"polar" ,	"neutral" , "CG" ) ,
		// HISTIDINEPOSITIVE	( "HIS" , "H" ,	"polar" ,	"positive" , "CG" ) ,
		ISOLEUCINE (	"ILE" ,	"I" ,	"nonpolar" ,	"neutral" , "CG1" ) ,		
		LEUCINE (	"LEU" ,	"L" ,	"nonpolar" ,	"neutral" , "CG" ) ,		
		LYSINE	( "LYS" ,	"K" ,	"polar" ,	"positive" , "CE" ) ,		
		METHIONINE	( "MET" ,	"M" ,	"nonpolar" ,	"neutral" , "CG" ) ,		
		PHENYLALANINE (	"PHE" ,	"F" ,	"nonpolar" ,	"neutral" , "CZ" ) ,
		PROLINE	( "PRO" , 	"P" ,	"nonpolar" ,	"neutral" , "CB" ) ,		
		SERINE	( "SER" ,	"S" , 	"polar" , 	"neutral" , "CB") ,		
		THREONINE (	"THR" ,	"T" ,	"polar" ,	"neutral" , "CB" ) ,		
		TRYPTOPHAN	( "TRP" ,	"W" , 	"nonpolar" ,	"neutral" , "CE3" ) ,
		TYROSINE	( "TYR" ,	"Y" ,	"polar" , 	"neutral" , "CZ" ) ,
		VALINE	( "VAL" ,	"V" ,	"nonpolar" ,	"neutral" , "CB" ) ,
		CYTIDINE ( "C", "X" , "NA" , "neutral" , "P") ,
		DEOXYCYTIDINE ( "DC", "c" , "NA" , "NA" , "P") ,
		DEOXYGUANOSINE ( "DG", "g" , "NA" , "NA" , "P") ,
		DEOXYADENOSINE ( "DA", "a" , "NA" , "NA" , "P") ,
		DEOXYINOSINE( "DI", "i" , "NA" , "NA" , "P") ,
		THYMIDINE ( "DT", "t" , "NA" , "NA" , "P") ,
		GUANOSINE ( "G", "G" , "NA" , "NA" , "P") ,
		ADENOSINE ( "A", "A" , "NA" , "NA" , "P") ,
		URIDINE ( "U", "U" , "NA" , "NA" , "P") ,
		INOSINIC  ( "I", "I" , "NA" , "NA" , "P") ;
		private final String threeLetterCode ;
		private final String oneLetterCode ;
		private final String polarity ;
		private final String charge ;
		private final String atomRepresentingAResidue;
		// Reverse-lookup map for getting a day from an abbreviation
		private static final Map<String, Residues> lookup = new HashMap<String, Residues>();

		static {
			for (Residues residue : Residues.values())
				lookup.put(residue.threeLetterCode, residue);
		}

		Residues ( String threeLetterCode, String oneLetterCode, String polarity, String charge, String atomRepresentingAResidue) {
			this.threeLetterCode = threeLetterCode;
			this.oneLetterCode = oneLetterCode;
			this.polarity = polarity;
			this.charge = charge;
			this.atomRepresentingAResidue = atomRepresentingAResidue;
		}



		public static Residues get(String threeLetterCode) {
			return lookup.get(threeLetterCode);
		}

		public String getOneLetterCode() {
			return oneLetterCode;
		}

		public String getAtomRepresentingAResidue() {
			return atomRepresentingAResidue;
		}

		public String getThreeLetterCode() {
			return threeLetterCode;
		}
	}
}
