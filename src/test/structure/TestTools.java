import java.util.ArrayList;
import java.util.List;

import parameters.AlgoParameters;
import structure.*;

public class TestTools {

	public static MyAtomIfc buildValidMyAtomCarbonAlpha(int originalAtomId) throws ExceptionInMyStructurePackage {

		char[] element = "C".toCharArray();
		float[] coords = new float[3];
		char[] atomName = "CA".toCharArray();

		MyAtomIfc myAtom = new MyAtom(element, coords, atomName, originalAtomId);
		return myAtom;
	}



	public static MyAtomIfc buildValidMyAtomCarbonBeta(int originalAtomId) throws ExceptionInMyStructurePackage{

		char[] element = "C".toCharArray();
		float[] coords = new float[3];
		char[] atomName = "CB".toCharArray();

		MyAtomIfc myAtom = new MyAtom(element, coords, atomName, originalAtomId);
		return myAtom;
	}



	public static MyAtomIfc buildValidMyAtomCarbonCarbonyl(int originalAtomId) throws ExceptionInMyStructurePackage{

		char[] element = "C".toCharArray();
		float[] coords = new float[3];
		char[] atomName = "C".toCharArray();

		MyAtomIfc myAtom = new MyAtom(element, coords, atomName, originalAtomId);
		return myAtom;
	}



	public static MyMonomer buildValidMyMonomer(int residueId) throws ExceptionInMyStructurePackage{

		int originalAtomId = 0;

		MyAtomIfc[] myAtoms = new MyAtomIfc[3];
		try {
			MyAtomIfc myAtom1 = buildValidMyAtomCarbonAlpha(originalAtomId);
			MyAtomIfc myAtom2 = buildValidMyAtomCarbonBeta(originalAtomId);
			MyAtomIfc myAtom3 = buildValidMyAtomCarbonCarbonyl(originalAtomId);

			MyBondIfc bondAtom1to2 = new MyBond(myAtom2, 1);
			myAtom1.addBond(bondAtom1to2); // atom 1 is bonded to atom2
			MyBondIfc bondAtom2to1 = new MyBond(myAtom1, 1);
			myAtom2.addBond(bondAtom2to1); // atom 2 is bonded to atom1

			MyBondIfc bondAtom2to3 = new MyBond(myAtom3, 1);
			myAtom2.addBond(bondAtom2to3); // atom 2 is bonded to atom3
			MyBondIfc bondAtom3to2 = new MyBond(myAtom2, 1);
			myAtom3.addBond(bondAtom3to2); // atom 2 is bonded to atom1

			myAtoms[0] = myAtom1;
			myAtoms[1] = myAtom2;
			myAtoms[2] = myAtom3;
		} catch (ExceptionInMyStructurePackage e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		char[] threeLetterCode = "ASP".toCharArray();
		char insertionLetter = 0;
		char[] secStruc = null;

		MyMonomer monomer = new MyMonomer(myAtoms, threeLetterCode, residueId, MyMonomerType.AMINOACID, insertionLetter, secStruc);

		return monomer;
	}


	public static MyStructureIfc buildValidMyStructure(MyMonomerType myMonomerType) throws ExceptionInMyStructurePackage{

		MyMonomerIfc myMonomerA1  = buildValidMyMonomer(1);
		MyMonomerIfc myMonomerA2 = buildValidMyMonomer(2);
	
		List<MyMonomerIfc> myMonomersAlist = new ArrayList<>();
		myMonomersAlist.add(myMonomerA1);
		myMonomersAlist.add(myMonomerA2);
		MyMonomerIfc[] myMonomersA = myMonomersAlist.toArray(new MyMonomerIfc[myMonomersAlist.size()]);
		MyChainIfc myChainA = new MyChain(myMonomersA, "A".toCharArray());
		myMonomerA1.setParent(myChainA);
		myMonomerA2.setParent(myChainA);
		
		// linking the two monomer
		MyAtomIfc lastAtomMonomerA1 = myMonomerA1.getMyAtoms()[myMonomerA1.getMyAtoms().length-1];
		MyAtomIfc firstAtomMonomerA2 = myMonomerA2.getMyAtoms()[0];
		MyBondIfc bondMonomer1Ato2A = new MyBond(firstAtomMonomerA2, 1);
		MyBondIfc bondMonomer2Ato1A = new MyBond(lastAtomMonomerA1, 1);
		lastAtomMonomerA1.addBond(bondMonomer1Ato2A);
		firstAtomMonomerA2.addBond(bondMonomer2Ato1A);
		
		MyMonomerIfc myMonomerB1 =  buildValidMyMonomer(1);
		MyMonomerIfc myMonomerB2 = buildValidMyMonomer(2);
		List<MyMonomerIfc> myMonomersBlist = new ArrayList<>();
		myMonomersBlist.add(myMonomerB1);
		myMonomersBlist.add(myMonomerB2);
		MyMonomerIfc[] myMonomersB = myMonomersBlist.toArray(new MyMonomerIfc[myMonomersBlist.size()]);
		MyChainIfc myChainB = new MyChain(myMonomersB, "B".toCharArray());
		myMonomerB1.setParent(myChainB);
		myMonomerB2.setParent(myChainB);
		
		// linking the two monomer
		MyAtomIfc lastAtomMonomerB1 = myMonomerB1.getMyAtoms()[myMonomerB1.getMyAtoms().length-1];
		MyAtomIfc firstAtomMonomerB2 = myMonomerB2.getMyAtoms()[0];
		MyBondIfc bondMonomer1Bto2B = new MyBond(firstAtomMonomerB2, 1);
		MyBondIfc bondMonomer2Bto1B = new MyBond(lastAtomMonomerB1, 1);
		lastAtomMonomerB1.addBond(bondMonomer1Bto2B);
		firstAtomMonomerB2.addBond(bondMonomer2Bto1B);
		
		MyChainIfc[] mychains = new MyChainIfc[2];
		mychains[0] = myChainA;
		mychains[1] = myChainB;

		AlgoParameters algoParameters = new AlgoParameters();
		MyStructureIfc myStructure = MyStructure.getMyStructure(mychains, myMonomerType, algoParameters);

		// set parent atom
		MyStructureTools.setAtomParentReference(myMonomerA1);
		MyStructureTools.setAtomParentReference(myMonomerA2);
		MyStructureTools.setAtomParentReference(myMonomerB1);
		MyStructureTools.setAtomParentReference(myMonomerB2);
		
		return myStructure;
	}
	
	
	public static int countMyMonomer(MyChainIfc[] neighbors){
		
		int count = 0;
		for (MyChainIfc chain: neighbors){
			count += chain.getMyMonomers().length;
		}
		return count;
	}
}