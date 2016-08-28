package pointWithProperties;

import structure.EnumResidues;

public class LennardJonesTools {

	//------------------------
	// Enum
	//------------------------
	public enum LennarJonesGromacs{

		alj_O     (0.16612,   0.879228),
		alj_O2    (0.16612,   0.879228),
		alj_OW    (0.17683,   0.636394),
		alj_OH     (0.1721,   0.880903),
		alj_OS    (0.16837,   0.711756),
		alj_CT     (0.1908,   0.458036),
		alj_CA     (0.1908,   0.360065),
		alj_CM     (0.1908,   0.360065),
		alj_C      (0.1908,   0.360065),
		alj_N      (0.1824,   0.711756),
		alj_S         (0.2,     1.0467),
		alj_SH        (0.2,     1.0467),
		alj_P        (0.21,    0.83736),
		alj_IM      (0.247,    0.41868),
		alj_Li     (0.1137,  0.0766184),
		alj_IP     (0.1868,  0.0115974),
		alj_K      (0.2658, 0.00137327),
		alj_Rb     (0.2956, 0.000711756),
		alj_Cs     (0.3395, 0.000337456),
		alj_I       (0.235,    1.67472),
		alj_F       (0.175,   0.255395),
		alj_IB        (0.5,   0.41868);


		private final double a ;
		private final double c ;


		LennarJonesGromacs ( double a, double c) {
			this.a = a;
			this.c = c;
		}


		public double getA() {
			return a;
		}


		public double getC() {
			return c;
		}
	}


	public static LennarJonesGromacs getLennardJones(String atomName, String residueName){

		if (EnumResidues.Residues.get(residueName) == null){
			LennarJonesGromacs lennarJones = getLennardJonesByFirstLetterInAtomName(atomName);
			return lennarJones;
		}else{
			LennarJonesGromacs lennarJonesGromacs = getLennardJonesAtomTypeFromPDBAtomName(atomName, residueName);
			return lennarJonesGromacs;
		}

	}



	private static LennarJonesGromacs getLennardJonesByFirstLetterInAtomName(String atomName){

		String firstLetter = atomName.substring(0, 1);
		switch (firstLetter) {
		// TODO make it better
		case "C":	return LennarJonesGromacs.alj_C; // sp2 C carbonyl group
		case "N":  	return LennarJonesGromacs.alj_N; // sp2 nitrogen in amide groups
		case "O":  	return LennarJonesGromacs.alj_O; // carbonyl group oxygen
		case "P":  	return LennarJonesGromacs.alj_P; //
		case "S":  	return LennarJonesGromacs.alj_S; //
		case "I":  	return LennarJonesGromacs.alj_I; //
		case "F":  	return LennarJonesGromacs.alj_F; //
		 
		default: return null;
		}
	}



	private static LennarJonesGromacs getLennardJonesAtomTypeFromPDBAtomName(String atomName, String residueName){

		switch (atomName) {

		case "C":	return LennarJonesGromacs.alj_C; // sp2 C carbonyl group
		case "CA":	return LennarJonesGromacs.alj_CT; // sp3 aliphatic C
		case "CB":	return LennarJonesGromacs.alj_CT; // sp3 aliphatic C
		case "N":  	return LennarJonesGromacs.alj_N; // sp2 nitrogen in amide groups
		case "O":  	return LennarJonesGromacs.alj_O; // carbonyl group oxygen

		//case "CG1":	return LennarJonesGromacs.alj_CT; // sp3 aliphatic C
		//case "CG2": return LennarJonesGromacs.alj_CT; // sp3 aliphatic C
		//case "CE1":	return LennarJonesGromacs.alj_C; // sp2
		//case "CE2":	return LennarJonesGromacs.alj_C; // sp2
		//case "OG1": return LennarJonesGromacs.alj_OH; // oxygen in hydroxyl group
		//case "OH": return LennarJonesGromacs.alj_OH; // oxygen in hydroxyl group

		case "OE1":{
			if (residueName.equals("GLN")){
				return LennarJonesGromacs.alj_O; // C=O in Gln as amide
			}
			if (residueName.equals("GLU")){
				return LennarJonesGromacs.alj_O2; // carboxyl and phosphate group oxygen
			}
			return null;
		}

		case "OE2":{
			if (residueName.equals("GLU")){
				return LennarJonesGromacs.alj_O2; // carboxyl and phosphate group oxygen
			}
			return null;
		}

		case "OD1":{
			if (residueName.equals("ASN")){
				return LennarJonesGromacs.alj_O; // C=O in Gln as amide
			}
			if (residueName.equals("ASP")){
				return LennarJonesGromacs.alj_O2; // C=O in Gln as amide
			}
			//			if (residueName.equals("GLU")){
			//				return LennarJonesGromacs.alj_O2; // carboxyl and phosphate group oxygen
			//			}
			return null;
		}

		case "OD2": {
			if (residueName.equals("ASP")){
				return LennarJonesGromacs.alj_O2; // carboxyl and phosphate group oxygen
			}
			return null;
		} 

		case "OXT": {
			if (residueName.equals("LEU")){
				return LennarJonesGromacs.alj_O2; // carboxyl and phosphate group oxygen
			} 
			if (residueName.equals("LYS")){
				return LennarJonesGromacs.alj_O2; // carboxyl and phosphate group oxygen
			} 
			return null;
		} 

		case "OG":{
			if (residueName.equals("SER")){
				return LennarJonesGromacs.alj_OH; // oxygen in hydroxyl group 
			}
			return null;
		}

		case "OH":{
			if (residueName.equals("TYR")){
				return LennarJonesGromacs.alj_OH; // oxygen in hydroxyl group 
			}
			if (residueName.equals("PTR")){
				return LennarJonesGromacs.alj_O2; // oxygen in hydroxyl group 
			}
			return null;
		}

		case "OG1":{
			if (residueName.equals("THR")){
				return LennarJonesGromacs.alj_OH; // oxygen in hydroxyl group 
			}
			return null;
		}


		case "O1P":{

			if (residueName.equals("PTR")){
				return LennarJonesGromacs.alj_O2; // sulphur in cystine 
			}
			return null;
		}

		case "O2P":{

			if (residueName.equals("PTR")){
				return LennarJonesGromacs.alj_O2; // sulphur in cystine 
			}
			return null;
		}

		case "O3P":{

			if (residueName.equals("PTR")){
				return LennarJonesGromacs.alj_O2; // sulphur in cystine 
			}
			return null;
		}

		case "P":{

			if (residueName.equals("PTR")){
				return LennarJonesGromacs.alj_P; // sulphur in cystine 
			}
			return null;
		}


		case "SG":{
			// TODO detect if sulfide linkage but i would need the IAtom and its neighbors
			//S       32.06    0  A alj_S   ; sulphur in disulfide linkage
			//SH      32.06    0  A alj_SH  ; sulphur in cystine
			if (residueName.equals("CYS")){
				return LennarJonesGromacs.alj_SH; // sulphur in cystine 
			}
			if (residueName.equals("CSS")){
				return LennarJonesGromacs.alj_S; // sulphur in cystine 
			}
			return null;
		}

		case "SD":{
			if (residueName.equals("MET")){
				return LennarJonesGromacs.alj_S; // like in disulfide linkage but not clear
			}
			if (residueName.equals("CSS")){
				return LennarJonesGromacs.alj_SH; // like in disulfide linkage but not clear
			}
			return null;
		}

		case "NH1":{
			if (residueName.equals("ARG")){
				return LennarJonesGromacs.alj_N; // sp2 nitrogen
			}
			return null;
		}

		case "NH2":{
			if (residueName.equals("ARG")){
				return LennarJonesGromacs.alj_N; // sp2 nitrogen
			}
			return null;
		}

		case "NE":{
			if (residueName.equals("ARG")){
				return LennarJonesGromacs.alj_N; // sp2 nitrogen
			}
			return null;
		}

		case "NE2": {
			if (residueName.equals("GLN")){
				return LennarJonesGromacs.alj_N; // sp2 nitrogen in amide groups
			}
			if (residueName.equals("HIS")){
				return LennarJonesGromacs.alj_N; // sp2 nitrogen in amide groups
			}
			return null;
		}

		case "ND1": {
			if (residueName.equals("HIS")){
				return LennarJonesGromacs.alj_N; // sp2 nitrogen in amide groups
			}
			return null;
		}

		case "ND2": {
			if (residueName.equals("ASN")){
				return LennarJonesGromacs.alj_N; // sp2 nitrogen in amide groups
			}
			return null;
		}

		case "NE1": {
			if (residueName.equals("TRP")){
				return LennarJonesGromacs.alj_N; // sp2 N in 5 memb.ring w/H atom (HIS)
			}
			return null;
		}

		case "NZ": {
			if (residueName.equals("LYS")){
				return LennarJonesGromacs.alj_N; // sp3 N for charged amino groups (Lys, etc)
			}
			return null;
		}

		case "CZ":{
			if (residueName.equals("ARG") || residueName.equals("PTR") || residueName.equals("TYR") || residueName.equals("PHE")){
				return LennarJonesGromacs.alj_C; // sp2
			}
			return null;
		}

		case "CG2": {
			if (residueName.equals("THR")){
				return LennarJonesGromacs.alj_CT; // sp3 aliphatic C
			}
			if (residueName.equals("VAL")){
				return LennarJonesGromacs.alj_CT; // sp3 aliphatic C
			}
			if (residueName.equals("ILE")){
				return LennarJonesGromacs.alj_CT; // sp3 aliphatic C
			}
			return null;
		}

		case "CG1": {
			if (residueName.equals("ILE")){
				return LennarJonesGromacs.alj_CT; // sp3 aliphatic C
			}
			if (residueName.equals("VAL")){
				return LennarJonesGromacs.alj_CT; // sp3 aliphatic C
			}
			return null;
		}

		case "CD1":	{
			if (residueName.equals("TYR") || residueName.equals("PHE") || residueName.equals("PTR")){
				return LennarJonesGromacs.alj_C; // sp2
			}
			if (residueName.equals("TRP")){
				return LennarJonesGromacs.alj_C; // sp2
			}
			if (residueName.equals("LEU")){
				return LennarJonesGromacs.alj_CT; // sp3 aliphatic C
			}
			if (residueName.equals("ILE")){
				return LennarJonesGromacs.alj_CT; // sp3 aliphatic C
			}
			return null;
		}

		case "CD2":	{
			if (residueName.equals("TYR") || residueName.equals("PHE") || residueName.equals("PTR")){
				return LennarJonesGromacs.alj_C; // sp2
			}
			if (residueName.equals("TRP")){
				return LennarJonesGromacs.alj_C; // sp2
			}
			if (residueName.equals("HIS")){
				return LennarJonesGromacs.alj_C; // sp2
			}
			if (residueName.equals("LEU")){
				return LennarJonesGromacs.alj_CT; // sp3 aliphatic C
			}
			return null;
		}	

		case "CG": {
			if (residueName.equals("TYR")){
				return LennarJonesGromacs.alj_C; // sp2
			}
			if (residueName.equals("PTR")){
				return LennarJonesGromacs.alj_C; // sp2
			}
			if (residueName.equals("ARG")){
				return LennarJonesGromacs.alj_CT; // sp3 aliphatic C
			}

			if (residueName.equals("PHE")){
				return LennarJonesGromacs.alj_C; // sp2
			}
			if (residueName.equals("GLN")){
				return LennarJonesGromacs.alj_CT; // sp3 aliphatic C
			}
			if (residueName.equals("GLU")){
				return LennarJonesGromacs.alj_CT; // sp3 aliphatic C
			}
			if (residueName.equals("LEU")){
				return LennarJonesGromacs.alj_CT; // sp3 aliphatic C
			}
			if (residueName.equals("ASN")){
				return LennarJonesGromacs.alj_C; // sp2
			}
			if (residueName.equals("ASP")){
				return LennarJonesGromacs.alj_C; // sp2
			}
			if (residueName.equals("HIS")){
				return LennarJonesGromacs.alj_C; // sp2
			}
			if (residueName.equals("MET")){
				return LennarJonesGromacs.alj_CT; // sp3 aliphatic C
			}
			if (residueName.equals("LYS")){
				return LennarJonesGromacs.alj_CT; // sp3 aliphatic C
			}
			if (residueName.equals("PRO")){
				return LennarJonesGromacs.alj_CT; // sp3 aliphatic C
			}
			if (residueName.equals("TRP")){
				return LennarJonesGromacs.alj_C; // sp2
			}
			return null;
		}

		case "CE1": {
			if (residueName.equals("PHE") || residueName.equals("PTR")){
				return LennarJonesGromacs.alj_C; //  sp2 C pure aromatic (benzene)
			}
			if (residueName.equals("TYR")){
				return LennarJonesGromacs.alj_C; //  sp2 C pure aromatic (benzene)
			}
			if (residueName.equals("HIS")){
				return LennarJonesGromacs.alj_C; //  sp2 C pure aromatic (benzene)
			}
			return null;
		}

		case "CE2": {
			if (residueName.equals("PHE") || residueName.equals("PTR") ){
				return LennarJonesGromacs.alj_C; //  sp2 C pure aromatic (benzene)
			}
			if (residueName.equals("TYR")){
				return LennarJonesGromacs.alj_C; //  sp2 C pure aromatic (benzene)
			}
			if (residueName.equals("TRP")){
				return LennarJonesGromacs.alj_C; //  sp2 C pure aromatic (benzene)
			}
			return null;
		}

		case "CE3": {
			if (residueName.equals("TRP")){
				return LennarJonesGromacs.alj_C; //  sp2 C pure aromatic (benzene)
			}
			return null;
		}

		case "CZ2": {
			if (residueName.equals("TRP")){
				return LennarJonesGromacs.alj_C; //  sp2 C pure aromatic (benzene)
			}
			return null;
		}

		case "CZ3": {
			if (residueName.equals("TRP")){
				return LennarJonesGromacs.alj_C; //  sp2 C pure aromatic (benzene)
			}
			return null;
		}

		case "CH2": {
			if (residueName.equals("TRP")){
				return LennarJonesGromacs.alj_C; //  sp2 C pure aromatic (benzene)
			}
			return null;
		}

		case "CD": {
			if (residueName.equals("PRO")){
				return LennarJonesGromacs.alj_CT; // sp3 aliphatic C
			}
			if (residueName.equals("ARG")){
				return LennarJonesGromacs.alj_CT; // sp3 aliphatic C
			}
			if (residueName.equals("LYS")){
				return LennarJonesGromacs.alj_CT; // sp3 aliphatic C
			}
			if (residueName.equals("GLN")){
				return LennarJonesGromacs.alj_C; //  sp2
			}
			if (residueName.equals("GLU")){
				return LennarJonesGromacs.alj_C; //  sp2
			}
			return null;
		}

		case "CE": {
			if (residueName.equals("LYS")){
				return LennarJonesGromacs.alj_CT; // sp3 aliphatic C
			}
			if (residueName.equals("MET")){
				return LennarJonesGromacs.alj_CT; // sp3 aliphatic C
			}
			return null;
		}


		default: return null;

		}



		//		
		//		  C       12.01    0  A alj_C   ; sp2 C carbonyl group 
		//		  CA      12.01    0  A alj_C   ; sp2 C pure aromatic (benzene)
		//		  CB      12.01    0  A alj_C   ; sp2 aromatic C, 5&6 membered ring junction
		//		  CC      12.01    0  A alj_C   ; sp2 aromatic C, 5 memb. ring HIS
		//		  CK      12.01    0  A alj_C   ; sp2 C 5 memb.ring in purines
		//		  CM      12.01    0  A alj_C   ; sp2 C  pyrimidines in pos. 5 & 6
		//		  CN      12.01    0  A alj_C   ; sp2 C aromatic 5&6 memb.ring junct.(TRP)
		//		  CQ      12.01    0  A alj_C   ; sp2 C in 5 mem.ring of purines between 2 N
		//		  CR      12.01    0  A alj_C   ; sp2 arom as CQ but in HIS
		//		  CT      12.01    0  A alj_CT  ; sp3 aliphatic C
		//		  CV      12.01    0  A alj_C   ; sp2 arom. 5 memb.ring w/1 N and 1 H (HIS)
		//		  CW      12.01    0  A alj_C   ; sp2 arom. 5 memb.ring w/1 N-H and 1 H (HIS)
		//		  C*      12.01    0  A alj_C   ; sp2 arom. 5 memb.ring w/1 subst. (TRP)
		//		  C0      40.08    0  A alj_C0  ; calcium
		//		  F          19    0  A alj_F   ; fluorine
		//		  H       1.008    0  A alj_H   ; H bonded to nitrogen atoms
		//		  HC      1.008    0  A alj_HC  ; H aliph. bond. to C without electrwd.group
		//		  H1      1.008    0  A alj_H1  ; H aliph. bond. to C with 1 electrwd. group
		//		  H2      1.008    0  A alj_H2  ; H aliph. bond. to C with 2 electrwd.groups
		//		  H3      1.008    0  A alj_H3  ; H aliph. bond. to C with 3 eletrwd.groups
		//		  HA      1.008    0  A alj_HA  ; H arom. bond. to C without elctrwd. groups
		//		  H4      1.008    0  A alj_H4  ; H arom. bond. to C with 1 electrwd. group
		//		  H5      1.008    0  A alj_H5  ; H arom. bond. to C with 2 electrwd. groups
		//		  HO      1.008    0  A alj_HO  ; hydroxyl group
		//		  HS      1.008    0  A alj_HS  ; hydrogen bonded to sulphur
		//		  HW      1.008    0  A alj_HW  ; H in TIP3P water
		//		  HP      1.008    0  A alj_HP  ; H bonded to C next to positively charged gr
		//		  I       126.9    0  A alj_I   ; iodine
		//		  IM      35.45    0  A alj_IM  ; assumed to be Cl-
		//		  IP      22.99    0  A alj_IP  ; assumed to be Na+
		//		  IB        131    0  A alj_IB  ; 'big ion w/ waters' for vacuum (Na+, 6H2O)
		//		  MG     24.305    0  A alj_MG  ; magnesium
		//		  N       14.01    0  A alj_N   ; sp2 nitrogen in amide groups
		//		  NA      14.01    0  A alj_N   ; sp2 N in 5 memb.ring w/H atom (HIS)
		//		  NB      14.01    0  A alj_N   ; sp2 N in 5 memb.ring w/LP (HIS,ADE,GUA)
		//		  NC      14.01    0  A alj_N   ; sp2 N in 6 memb.ring w/LP (ADE,GUA)
		//		  N2      14.01    0  A alj_N   ; sp2 N in amino groups
		//		  N3      14.01    0  A alj_N   ; sp3 N for charged amino groups (Lys, etc)
		//		  N*      14.01    0  A alj_N   ; sp2 N 
		//		  O          16    0  A alj_O   ; carbonyl group oxygen
		//		  OW         16    0  A alj_OW  ; oxygen in TIP3P water
		//		  OH         16    0  A alj_OH  ; oxygen in hydroxyl group
		//		  OS         16    0  A alj_OS  ; ether and ester oxygen
		//		  O2         16    0  A alj_O2  ; carboxyl and phosphate group oxygen
		//		  P       30.97    0  A alj_P   ; phosphate
		//		  S       32.06    0  A alj_S   ; sulphur in disulfide linkage
		//		  SH      32.06    0  A alj_SH  ; sulphur in cystine
		//		  CU      63.55    0  A alj_CU  ; copper
		//		  FE         55    0  A alj_FE  ; iron
		//		  Li       6.94    0  A alj_Li  ; lithium
		//		  K        39.1    0  A alj_K   ; potassium
		//		  Rb      85.47    0  A alj_Rb  ; rubidium
		//		  Cs     132.91    0  A alj_Cs  ; cesium

	}
}
