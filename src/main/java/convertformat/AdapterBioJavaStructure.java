package convertformat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.biojava.bio.structure.AminoAcid;
import org.biojava.bio.structure.Atom;
import org.biojava.bio.structure.Bond;
import org.biojava.bio.structure.Chain;
import org.biojava.bio.structure.Element;
import org.biojava.bio.structure.Group;
import org.biojava.bio.structure.GroupType;
import org.biojava.bio.structure.Structure;
import org.biojava.bio.structure.StructureException;
//import org.biojava.nbio.structure.secstruc.SecStrucType.SecStrucInfo;
import org.biojava.bio.structure.secstruc.SecStrucType;

import math.AddToMap;
import math.ToolsMath;
import parameters.AlgoParameters;
import structure.EnumMyReaderBiojava;
import structure.ExceptionInMyStructurePackage;
import structure.LPeptideLinkingResidues;
import structure.MyAtom;
import structure.MyAtomIfc;
import structure.MyBond;
import structure.MyBondIfc;
import structure.MyChain;
import structure.MyChainIfc;
import structure.MyMonomer;
import structure.MyMonomerIfc;
import structure.MyMonomerType;
import structure.MyStructure;
import structure.MyStructureIfc;
import structure.MyStructureTools;
import structure.ReadingStructurefileException;

public class AdapterBioJavaStructure {
	private EnumMyReaderBiojava enumMyReaderBiojava;

	private AlgoParameters algoParameters;

	private List<MyChainIfc> aminoChains = new ArrayList<>();
	private List<MyChainIfc> hetatmChains = new ArrayList<>();
	private List<MyChainIfc> nucleotidesChains = new ArrayList<>();

	private List<MyMonomerIfc> tempMyMonomerList = new ArrayList<>();
	private List<MyAtomIfc> tempMyAtomList = new ArrayList<>();

	private Map<MyAtomIfc, Atom> tempMapMyAtomToAtomAsHelperToBuildMyBond = new HashMap<>();
	private Map<Atom, MyAtomIfc> tempMapAtomToMyAtomAsHelperToBuildMyBond = new HashMap<>();

	private List<MyBondIfc> tempMyBondList = new ArrayList<>();

	private List<MyAtomIfc> listAtom1 = new ArrayList<>();
	private List<MyAtomIfc> listAtom2 = new ArrayList<>();

	//-------------------------------------------------------------
	// Constructor
	//-------------------------------------------------------------
	public AdapterBioJavaStructure(AlgoParameters algoParameters){

		this.algoParameters = algoParameters;
	}


	//-------------------------------------------------------------
	// Static Public methods
	//-------------------------------------------------------------
	public static char[] convertSecType(SecStrucType secType) {

		char[] secStruc = "".toCharArray();
		if (secType == null){
			return secStruc;
		}
		System.out.println(secType.name());
		//if (secType.name().isBetaStrand()){
		//	secStruc = "S".toCharArray();
		//}
		//if (secType.isHelixType()){
		//	secStruc = "H".toCharArray();
		//}
		return secStruc;
	}



	//-------------------------------------------------------------
	// Interface & Public methods
	//-------------------------------------------------------------
	public MyStructureIfc getMyStructureAndSkipHydrogens(Structure structure, EnumMyReaderBiojava enumMyReaderBiojava) throws ExceptionInMyStructurePackage, ReadingStructurefileException{

		this.enumMyReaderBiojava = enumMyReaderBiojava;

		MyStructureIfc	myStructure = convertStructureToMyStructure(structure, algoParameters);

		return myStructure;
	}



	public MyStructureIfc convertStructureToMyStructure(Structure structure, AlgoParameters algoParameters) throws ReadingStructurefileException, ExceptionInMyStructurePackage{

		aminoChains.clear();
		hetatmChains.clear();
		nucleotidesChains.clear();

		char[] fourLetterCode = structure.getPDBCode().toCharArray();

		int countOfChains = structure.getChains().size();
		Chain chain;
		List<Group> listGroupsAmino;
		int countOfAmino;
		List<Group> listGroupsHetAtm;
		int countOfHetAtm;
		List<Group> listGroupsNucleotide;
		int countOfNucleotide;

		tempMapMyAtomToAtomAsHelperToBuildMyBond.clear();
		tempMapAtomToMyAtomAsHelperToBuildMyBond.clear();

		for ( int i=0; i<countOfChains ; i++ ){

			chain = structure.getChain(i);

			listGroupsAmino = chain.getAtomGroups(GroupType.AMINOACID);
			cleanListOfGroup(listGroupsAmino);
			countOfAmino=listGroupsAmino.size();
			if (countOfAmino > 0){
				char[] chainType = "amino".toCharArray();
				MyChainIfc aminoChain = createAChainFromAListOfGroups(listGroupsAmino, countOfAmino, algoParameters, chainType);
				if (aminoChain != null){
					aminoChains.add(aminoChain);
				}
			}

			if (aminoChains.size() == 0){
				// then nothing to do, problem in the file
				String message = "Only empty amino chain were parsed for " + String.valueOf(fourLetterCode);
				ReadingStructurefileException exception = new ReadingStructurefileException(message);
				//there still can be hetatom with only one atom because not resolved completely ...
				throw exception;
			}

			listGroupsHetAtm=chain.getAtomGroups(GroupType.HETATM);
			cleanListOfGroup(listGroupsHetAtm);
			countOfHetAtm=listGroupsHetAtm.size();
			if (countOfHetAtm > 0){
				char[] chainType = "hetatm".toCharArray();
				MyChainIfc hetatomChain = createAChainFromAListOfGroups(listGroupsHetAtm, countOfHetAtm, algoParameters, chainType);
				if (hetatomChain != null){
					hetatmChains.add(hetatomChain);
				}
			}

			listGroupsNucleotide=chain.getAtomGroups(GroupType.NUCLEOTIDE);
			cleanListOfGroup(listGroupsNucleotide);
			countOfNucleotide=listGroupsNucleotide.size();
			if (countOfNucleotide > 0){
				char[] chainType = "nucleotide".toCharArray();
				MyChainIfc nucleotideChain = createAChainFromAListOfGroups(listGroupsNucleotide, countOfNucleotide, algoParameters, chainType);
				if (nucleotideChain != null){
					nucleotidesChains.add(nucleotideChain);
				}
			}
		}

		MyStructureIfc myStructure = new MyStructure(MyStructureTools.makeArrayFromList(aminoChains), MyStructureTools.makeArrayFromList(hetatmChains), MyStructureTools.makeArrayFromList(nucleotidesChains), algoParameters);

		myStructure.setFourLetterCode(fourLetterCode);

		integratePolymericResiduesFromHetAtmToAmino(myStructure);
		int countOfBonds = defineBonds(myStructure, structure);

		if (countOfBonds > 0){
			addHydrogens();
		}else{
			System.out.println("countOfBonds = " + countOfBonds);
			System.out.println("because bond orders not set in Structure from BioJava");
			System.out.println("Terminating");
			System.exit(0);
		}

		return myStructure;
	}




	//-------------------------------------------------------------
	// Implementation
	//-------------------------------------------------------------
	private void cleanListOfGroup(List<Group> listGroups){

		// check alternate location in structure there is in 2FA1 GLN A 197
		// I simply get rid of amino acids with alternate location

		// if there is only one atom in a residue I skip the monomer: lets see what happen for 1dgi
		// indirectly I have problems later on with atom with no bonds set
		// I prefer to fix the reason than the consequence

		Iterator<Group> it = listGroups.iterator();

		while (it.hasNext()){
			Group group = it.next();
			if (group.hasAltLoc() == true){


				//it.remove();
				//System.out.println(structure.getPDBCode() + "  " + group.getPDBName() + "  " + group.getResidueNumber() + " has alt loc so removed ");
				//continue;
			}
			if (group.getAtoms().size() == 1){ // only CA
				it.remove();
				//System.out.println(structure.getPDBCode() + "  " + group.getPDBName() + "  " + group.getResidueNumber() + " has only one atom so removed  " + group.getAtoms().get(0).getFullName());
			}
		}
	}



	private void addHydrogens(){

		// I dont have the bonded atoms by distance
		// I use the atom name



	}



	private MyChainIfc createAChainFromAListOfGroups(List<Group> listGroups, int countOfGroups, AlgoParameters algoParameters, char[] chainType) {

		tempMyMonomerList.clear();
		int countOfAtoms;
		Group currentGroup;

		for (int j=0; j < countOfGroups; j++){

			tempMyAtomList.clear();
			currentGroup = listGroups.get(j);
			countOfAtoms = currentGroup.getAtoms().size();

			Atom atom = null;
			//System.out.println(countOfAtoms + " read atom");
			for (int k=0; k<countOfAtoms; k++){

				try {
					atom = currentGroup.getAtom(k);
				} catch (StructureException e){
					continue;
				}
				// TODO Figure out if it is nice to skip H as I also delete them when I build the MyStructure
				// I would say I should skip the one from exp files as not reliable
				if (atom.getElement().equals(Element.H)){
					continue;
				}

				char[] atomElement = atom.getElement().toString().toCharArray();
				char[] atomName = atom.getName().toCharArray();
				float[] coords = ToolsMath.convertToFloatArray(atom.getCoords());
				int originalAtomID = atom.getPDBserial();
				MyAtomIfc myAtom;
				try {
					myAtom = new MyAtom(atomElement, coords, atomName, originalAtomID);
				} catch (ExceptionInMyStructurePackage e) {
					continue;
				}
				tempMyAtomList.add(myAtom);

				tempMapMyAtomToAtomAsHelperToBuildMyBond.put(myAtom, atom);
				tempMapAtomToMyAtomAsHelperToBuildMyBond.put(atom, myAtom);
			}

			MyAtomIfc[] myAtoms = tempMyAtomList.toArray(new MyAtomIfc[tempMyAtomList.size()]);

			char[] threeLetterCode = getThreeLetterCode(currentGroup);
			if (String.valueOf(threeLetterCode).equals("UNK")){
				continue;
			}
			int residueId = currentGroup.getResidueNumber().getSeqNum();

			char insertionLetter = 0;
			if (  currentGroup.getResidueNumber().getInsCode() != null ){
				insertionLetter = currentGroup.getResidueNumber().getInsCode().toString().toCharArray()[0];
			}

			//char[] secStruc = new char[1];
			//if (currentGroup instanceof AminoAcid){
			//	AminoAcid aa = (AminoAcid)currentGroup;
			//	SecStrucInfo readsecStruc = (SecStrucInfo) aa.getProperty(Group.SEC_STRUC);
			//	SecStrucType secType = null;
			//	if (readsecStruc != null) {
			//		secType = readsecStruc.getType();
			//	}
			//	secStruc = convertSecType(secType);
			//}
			if (currentGroup instanceof AminoAcid){
				AminoAcid aa = (AminoAcid)currentGroup;
				Map<String, Object> properties = aa.getProperties();
				System.out.println("No properties so no secstruc found in " + aa.getPDBName());
			}

			char[] secStruc = "".toCharArray();
			MyMonomerType monomerType = MyStructureTools.convertType(currentGroup.getType());
			MyMonomerIfc myMonomer;
			try {
				myMonomer = new MyMonomer(myAtoms, threeLetterCode, residueId, monomerType, insertionLetter, secStruc);
			} catch (ExceptionInMyStructurePackage e) {
				continue;
			}
			addParentReference(myMonomer, myAtoms);
			tempMyMonomerList.add(myMonomer);
		}

		if (tempMyMonomerList.size() == 0){
			return null;
		}
		MyMonomerIfc[] myMonomers = tempMyMonomerList.toArray(new MyMonomerIfc[tempMyMonomerList.size()]);

		char[] chainId = listGroups.get(0).getChainId().toCharArray();
		MyChainIfc myChain = new MyChain(myMonomers, chainId);
		addParentReference(myChain, myMonomers);

		return myChain;
	}



	private int defineBonds(MyStructureIfc myStructure, Structure structure){

		int countOfBond = 0;
		for (MyChainIfc myChain: myStructure.getAllChains()){
			int count = defineBonds(myChain);
			countOfBond += count;
		}
		return countOfBond;
	}



	private int defineBonds(MyChainIfc myChain){

		int countOfBond = 0;
		for (MyMonomerIfc myMonomer: myChain.getMyMonomers()){

			for (MyAtomIfc myAtom: myMonomer.getMyAtoms()){
				Atom atom = tempMapMyAtomToAtomAsHelperToBuildMyBond.get(myAtom);
				tempMyBondList.clear();

				int sourceBondCount = 0; //atom.getBonds().size();
				for (Bond bond: atom.getBonds()){

					Atom bondAtomA = bond.getAtomA();
					Atom bondAtomB = bond.getAtomB();
					if (bondAtomA.getElement().equals(Element.H) || bondAtomB.getElement().equals(Element.H)){
						continue; // as I skiped Hydrogens I must skip as well bonds to hydrogens
					}
					if (bondAtomA != atom && bondAtomB != atom){
						System.out.println("Fatal to help debugging : a bond is ill defined in Structure " + bondAtomA.getName() + "  " + bondAtomB.getName());
						continue;
					}

					sourceBondCount +=1;

					MyAtomIfc bondedAtom = null;
					if (bondAtomA == atom){
						bondedAtom = tempMapAtomToMyAtomAsHelperToBuildMyBond.get(bondAtomB);
					}
					if (bondAtomB == atom){
						bondedAtom = tempMapAtomToMyAtomAsHelperToBuildMyBond.get(bondAtomA);
					}
					if (bondedAtom == null){
						System.out.println("Fatal to help debugging : unknown reason " + bondAtomA.getName() + "  " + bondAtomB.getName());
						continue;
					}
					int bondOrder = bond.getBondOrder();
					MyBondIfc myBond;
					try {
						myBond = new MyBond(bondedAtom, bondOrder);
					} catch (ExceptionInMyStructurePackage e) {
						continue;
					}
					countOfBond += 1;
					tempMyBondList.add(myBond);
				}

				MyBondIfc[] myBonds = tempMyBondList.toArray(new MyBondIfc[tempMyBondList.size()]);
				int outputBondCount = myBonds.length;
				//System.out.println(sourceBondCount + "  " + outputBondCount);
				if (sourceBondCount != outputBondCount){
					System.out.println("Error in defineBonds ");
					continue;
				}
				myAtom.setBonds(myBonds);
			}
		}

		// need to add the bonds between N and C
		//int sourceBondCount = atom.getBonds().size();
		//if (atom.getFullName().equals(" C  ")){
		//	System.out.println();
		//}
		// I dont assume consecutive C to N
		// TODO FMM make that static
		listAtom1.clear();
		listAtom2.clear();
		for (MyMonomerIfc myMonomer: myChain.getMyMonomers()){
			for (MyAtomIfc myAtom: myMonomer.getMyAtoms()){
				if (Arrays.equals(myAtom.getAtomName(), "N".toCharArray())){
					listAtom2.add(myAtom);
				}
				if (Arrays.equals(myAtom.getAtomName(), "C".toCharArray())){
					listAtom1.add(myAtom);
				}
			}
		}

		for (MyAtomIfc atomC: listAtom1){
			for (MyAtomIfc atomN: listAtom2){
				float distance = ToolsMath.computeDistance(atomC.getCoords(), atomN.getCoords());
				if (distance < 1.5){
					MyBondIfc myBondCtoN;
					try {
						myBondCtoN = new MyBond(atomN, 1);
						atomC.addBond(myBondCtoN);
						countOfBond += 1;
					} catch (ExceptionInMyStructurePackage e) {
					}

					MyBondIfc myBondNtoC;
					try {
						myBondNtoC = new MyBond(atomC, 1);
						atomN.addBond(myBondNtoC);
						countOfBond += 1;
					} catch (ExceptionInMyStructurePackage e) {
					}
				}
			}
		}

		// like in 101M and I dont know why some atoms are not connected
		// It is very costly all atom to all atom

		return countOfBond;
	}



	private char[] getThreeLetterCode(Group currentGroup) {
		char[] threeLetterCode = null;
		if (currentGroup.getChemComp().getThree_letter_code() != null){
			threeLetterCode = currentGroup.getChemComp().getThree_letter_code().toCharArray();
		}else{
			threeLetterCode = currentGroup.getPDBName().toCharArray();
		}
		return threeLetterCode;
	}



	private void addParentReference(MyMonomerIfc myMonomer, MyAtomIfc[] myAtoms){

		for (MyAtomIfc atom: myAtoms){
			atom.setParent(myMonomer);
		}
	}



	private void addParentReference(MyChainIfc myChain, MyMonomerIfc[] myMonomers){

		for (MyMonomerIfc monomer: myMonomers){
			monomer.setParent(myChain);
		}
	}



	private void integratePolymericResiduesFromHetAtmToAmino(MyStructureIfc myStructure){

		// detect of gaps before an existing residue
		Map<String, Set<Integer>> mapChainAndResidueIDBeforeAGap = findGapsInAminoChains(myStructure);

		for (MyChainIfc chainHetatm: myStructure.getAllHetatmchains()){
			for (MyMonomerIfc candidateMonomerForInsertion: chainHetatm.getMyMonomers()){

				boolean tryToInsertIt = isThatMonomerDefinedAsPolymeric(candidateMonomerForInsertion);
				if (tryToInsertIt == true){
					tryToInsertMonomer(myStructure, mapChainAndResidueIDBeforeAGap, candidateMonomerForInsertion, chainHetatm);
				}
			}
		}
	}



	private static boolean isThatMonomerDefinedAsPolymeric(MyMonomerIfc monomer){
		for ( LPeptideLinkingResidues polymericResidue: LPeptideLinkingResidues.values() ){

			if (String.valueOf(monomer.getThreeLetterCode()).equals(polymericResidue.getThreeLetterCode())){
				return true;
			}
		}
		return false;
	}



	private void tryToInsertMonomer(MyStructureIfc myStructure, Map<String, Set<Integer>> mapChainAndResidueIDBeforeAGap, MyMonomerIfc candidateMonomerForInsertion, MyChainIfc chainHetatm) {

		int residueIdOfMonomerToInsert = candidateMonomerForInsertion.getResidueID();
		boolean insertIt = isThatMonomerChainIdAndResidueIdFitsInAGapOfAnAminoChain(myStructure, mapChainAndResidueIDBeforeAGap, candidateMonomerForInsertion);

		MyChainIfc chain = myStructure.getAminoMyChain(candidateMonomerForInsertion.getParent().getChainId());

		if (chain == null){ // For some PDB where some chains are not typical amino chains there could be a problem
			return;
		}
		int lastResId = chain.getMyMonomerByRank(chain.getMyMonomers().length-1).getResidueID();
		if (insertIt == false && residueIdOfMonomerToInsert > lastResId){
			insertIt = true;
		}


		if (insertIt == true){
			tempMyMonomerList.clear();
			tempMyMonomerList.addAll(Arrays.asList(chain.getMyMonomers()));
			MyMonomerIfc monomerJustBefore = findMonomerJustBefore(residueIdOfMonomerToInsert, chain);
			int indexOfMonomer = tempMyMonomerList.indexOf(monomerJustBefore);
			System.out.println("Inserted Monomer from " + String.valueOf(candidateMonomerForInsertion.getParent().getChainId()) + "   " + Arrays.toString(candidateMonomerForInsertion.getThreeLetterCode()) + candidateMonomerForInsertion.getResidueID());

			tempMyMonomerList.add(indexOfMonomer + 1, candidateMonomerForInsertion);

			MyMonomerIfc[] myMonomers = tempMyMonomerList.toArray(new MyMonomerIfc[tempMyMonomerList.size()]);
			MyChainIfc chainWithMonomerInserted = new MyChain(myMonomers, chain.getChainId());
			updateMyMonomerParentReference(chainWithMonomerInserted);
			myStructure.setAminoChain(chainWithMonomerInserted.getChainId(), chainWithMonomerInserted);

			// remove it from former chain 
			chainHetatm.removeMyMonomer(candidateMonomerForInsertion);

		}
	}



	private static void updateMyMonomerParentReference(MyChainIfc myChain){

		for (MyMonomerIfc myMonomer: myChain.getMyMonomers()){
			myMonomer.setParent(myChain);
		}
	}



	private static MyMonomerIfc findMonomerJustBefore(int monomerId, MyChainIfc chain){

		for (int i=chain.getMyMonomers().length-1; i>=0; i--){
			MyMonomerIfc monomer = chain.getMyMonomerByRank(i);
			if (monomer.getResidueID() < monomerId){
				return monomer;
			}
		}
		return null;
	}



	private static boolean isThatMonomerChainIdAndResidueIdFitsInAGapOfAnAminoChain(MyStructureIfc myStructure, Map<String, Set<Integer>> mapChainAndResidueIDBeforeAGap, MyMonomerIfc monomer){

		int residueIdOfMonomerToInsert = monomer.getResidueID();
		char[] chainId = monomer.getParent().getChainId();
		if (mapChainAndResidueIDBeforeAGap.containsKey(String.valueOf(chainId))){
			//
			Set<Integer> residuesBeforeGapsInChain = mapChainAndResidueIDBeforeAGap.get(String.valueOf(chainId));
			if (residuesBeforeGapsInChain.contains(residueIdOfMonomerToInsert-1)){
				return true;
			}
		}
		return false;
	}



	private static Map<String, Set<Integer>> findGapsInAminoChains(MyStructureIfc myStructure) {

		Map <String, Set<Integer>> mapChainAndResidueIDBeforeAGap = new HashMap<>();
		for (MyChainIfc chain: myStructure.getAllAminochains()){

			// residues withan id > 0 and before first element are considered as a gap
			// maybe silly but works for 2qlj E
			int residueIdOfFirstMonomerInChain = chain.getMyMonomerByRank(0).getResidueID();
			if (residueIdOfFirstMonomerInChain > 1){
				for (int j=1; j<residueIdOfFirstMonomerInChain; j++){
					AddToMap.addElementToAMapOfSet(mapChainAndResidueIDBeforeAGap, String.valueOf(chain.getChainId()), j);
				}
			}

			for (int i=1; i < chain.getMyMonomers().length; i++){
				MyMonomerIfc currentMonomer = chain.getMyMonomerByRank(i); // guess they are sorted according to residue ID
				MyMonomerIfc previousMonomer = chain.getMyMonomerByRank(i-1);
				if (currentMonomer.getResidueID() > previousMonomer.getResidueID() + 1){
					//
					//System.out.println("Found a gap in amino chain " + chain.getChainId() + " after position " +  previousMonomer.getResidueID());
					// I should add all id in between !!!
					int startId = previousMonomer.getResidueID();
					int endId = currentMonomer.getResidueID();
					for (int j=startId; j<endId-1; j++){
						AddToMap.addElementToAMapOfSet(mapChainAndResidueIDBeforeAGap, String.valueOf(chain.getChainId()), j);
					}
				}
			}
		}
		return mapChainAndResidueIDBeforeAGap;
	}
}
