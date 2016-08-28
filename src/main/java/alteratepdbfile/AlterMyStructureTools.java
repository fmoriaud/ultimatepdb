package alteratepdbfile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import math.ProcrustesAnalysisIfc;
import math.ToolsMath;
import scorePairing.ScorePairingWithStaticMethods;
import shapeCompare.PairingTools;
import shapeCompare.ProcrustesAnalysis;
import structure.ExceptionInMyStructurePackage;
import structure.MyAtom;
import structure.MyAtomIfc;
import structure.MyBond;
import structure.MyBondIfc;
import structure.MyChainIfc;
import structure.MyMonomer;
import structure.MyMonomerIfc;
import structure.MyMonomerType;
import structure.MyStructureIfc;
import structure.MyStructureTools;

public class AlterMyStructureTools {


	public static List<MyMonomerIfc> generateSideChainRotamers(){
		
		List<MyMonomerIfc> rotamers = new ArrayList<>();
		
		// for each bond defined as rotatable
		// generate all rotamers so 3 per rotatable bond
		// not obvious as not all reasonable like a lysine all bended !!
		
		return rotamers;
	}
	
	
	
	public static void changeOneResidue(MyStructureIfc inputStructure, 
			char[] chainid, int residueID, char[] monomerTochangeThreeLettercode,
			char[] newResidue3LetterCode, AlteredResiduesCoordinates alteredResiduesCoordinates) throws AlteringMyStructureException{


		// find it
		MyChainIfc chain = inputStructure.getAminoMyChain(chainid);
		MyMonomerIfc monomerToChangeFromClonedStructure = chain.getMyMonomerFromResidueId(residueID);

		if (Arrays.equals(monomerToChangeFromClonedStructure.getThreeLetterCode(), monomerTochangeThreeLettercode)){
			System.out.println("ok : from : " + String.valueOf(monomerTochangeThreeLettercode));
		}else{
			System.out.println("Pas ok : from : " + String.valueOf(monomerTochangeThreeLettercode));
		}

		// make a new one
		MyMonomerIfc modifiedMyMonomer = convertMyMonomer(monomerToChangeFromClonedStructure, newResidue3LetterCode, alteredResiduesCoordinates);

		chain.replaceMonomer(monomerToChangeFromClonedStructure, modifiedMyMonomer);
		inputStructure.setAminoChain(monomerToChangeFromClonedStructure.getParent().getChainId(), chain);

		// renumber atoms as it is needed for bonds to be created but I know why
		MyStructureTools.renumberAllAtomIds(inputStructure);
	}




	//-------------------------------------------------------------
	// Implementation
	//-------------------------------------------------------------
	private static MyMonomerIfc convertMyMonomer(MyMonomerIfc inputMonomer, char[] newResidue3LetterCode,
			AlteredResiduesCoordinates alteredResiduesCoordinates) throws AlteringMyStructureException{

		List<MyAtomIfc> backboneAtomAndCB = generateListBackboneAtomUsingOriginAtomAndOriginBondsExceptCAtoSideChain(inputMonomer);

		Map<String, float[]> newMonomerCoords = alteredResiduesCoordinates.getTemplateCoords().get(String.valueOf(newResidue3LetterCode));

		if (newMonomerCoords == null){
			System.out.println();
		}
		List<MyAtomIfc> newAtoms = generateNewAtoms(inputMonomer, newResidue3LetterCode, backboneAtomAndCB, newMonomerCoords);

		List<MyAtomIfc> allAtoms = new ArrayList<>();
		allAtoms.addAll(backboneAtomAndCB);
		allAtoms.addAll(newAtoms);

		MyAtomIfc[] myAtoms = allAtoms.toArray(new MyAtomIfc[allAtoms.size()]);

		MyMonomerIfc newMyMonomer = null;
		try {
			newMyMonomer = new MyMonomer(myAtoms, newResidue3LetterCode, inputMonomer.getResidueID(), MyMonomerType.getEnumType(inputMonomer.getType()), inputMonomer.getInsertionLetter(), inputMonomer.getSecStruc());
		} catch (ExceptionInMyStructurePackage e1) {
			String message ="convertMyMonomer failed because of unknown MyMonomerType";
			AlteringMyStructureException e = new AlteringMyStructureException(message);
			throw e;
		}

		newMyMonomer.setParent(inputMonomer.getParent());
		MyStructureTools.setAtomParentReference(newMyMonomer);

		//		for (MyAtomIfc atom: newMyMonomer.getMyAtoms()){
		//			removeBondsOnlyWithinMonomer(newMyMonomer, atom);
		//		}

		// create bonds
		List<String> bondsAsString = alteredResiduesCoordinates.getTemplateBonds().get(String.valueOf(newResidue3LetterCode));
		for (String bondAsString: bondsAsString){

			StringTokenizer tok = new StringTokenizer(bondAsString, ","); 
			List<String> splittedLine = new ArrayList<>();
			while ( tok.hasMoreElements() )  
			{  
				String next = (String) tok.nextElement();
				splittedLine.add(next);
			}
			String atomName1 = null;
			String atomName2 = null;
			int bondOrder = 0;
			try {
				atomName1 = splittedLine.get(0);
				atomName2 = splittedLine.get(1);
				bondOrder = Integer.valueOf(splittedLine.get(2));
			}catch(Exception e){
				System.out.println("Problem in parsing line : " + bondAsString);
				continue;
			}

			MyAtomIfc atom1 = newMyMonomer.getMyAtomFromMyAtomName(atomName1.toCharArray());
			MyAtomIfc atom2 = newMyMonomer.getMyAtomFromMyAtomName(atomName2.toCharArray());
			MyBondIfc newBondForAtom1 = null;
			try {
				newBondForAtom1 = new MyBond(atom2, bondOrder);
				atom1.addBond(newBondForAtom1);
				MyBondIfc newBondForAtom2 = new MyBond(atom1, bondOrder);
				atom2.addBond(newBondForAtom2);
			} catch (ExceptionInMyStructurePackage e) {
				String message ="convertMyMonomer failed because of invalid bond order";
				AlteringMyStructureException e2 = new AlteringMyStructureException(message);
				throw e2;
			}
			
		}


		// I need to fix the bonds on the side chain


		return newMyMonomer;
	}



	private static List<MyAtomIfc> generateNewAtoms(MyMonomerIfc inputMonomer, char[] newResidue3LetterCode,
			List<MyAtomIfc> backboneAtom, Map<String, float[]> newMonomerCoords)
					throws AlteringMyStructureException {

		List<MyAtomIfc> newAtoms = new ArrayList<>();
		// I find translation and rotation to best align: using Procrustes
		// input is a mapping of points: the one in coomon from backbone


		List<MyAtomIfc> backboneAtomWithoutO = new ArrayList<>();
		for (MyAtomIfc atom: backboneAtom){
			if (Arrays.equals(atom.getAtomName(), "O".toCharArray())){
				continue;
			}
			backboneAtomWithoutO.add(atom);
		}

		RealMatrix matrixModel = prepareInputWithAtomsForProcrustesAnalysis(backboneAtomWithoutO); // Model terminology in procrustes. The reference
		RealMatrix matrixCandidate = prepare(newResidue3LetterCode, backboneAtomWithoutO, newMonomerCoords);
		RealVector barycenterModel = computeBarycenter(matrixModel);
		RealVector barycenterCandidate = computeBarycenter(matrixCandidate);

		RealVector translationVectorToTranslateShape2ToOrigin = barycenterCandidate.mapMultiply(-1.0);
		RealVector translationVectorToTranslateShape2ToShape1 = barycenterModel.subtract(barycenterCandidate);

		ScorePairingWithStaticMethods.translateBarycenterListOfPointToOrigin(matrixModel, barycenterModel);
		ScorePairingWithStaticMethods.translateBarycenterListOfPointToOrigin(matrixCandidate, barycenterCandidate);

		ProcrustesAnalysisIfc procrustesAnalysis = new ProcrustesAnalysis();
		procrustesAnalysis.run(matrixModel, matrixCandidate);
		//double residual = procrustesAnalysis.getResidual();
		//System.out.println("residual = " + residual);
		// create the missing atoms and change their coords by applying trans and rot
		// To make it easier I add atoms which are not already in tempMyAtomList
		A: for (Entry<String, float[]> entry: newMonomerCoords.entrySet()){
			String atomName = entry.getKey();

			for (MyAtomIfc atom: backboneAtom){
				if (String.valueOf(atom.getAtomName()).equals(atomName)){
					continue A;
				}
			}
			// atom not found so create it
			char[] atomElementFromCoordinates = null;
			if (atomName.startsWith("C")){
				atomElementFromCoordinates = "C".toCharArray();
			}
			if (atomName.startsWith("O")){
				atomElementFromCoordinates = "O".toCharArray();
			}
			if (atomName.startsWith("N")){
				atomElementFromCoordinates = "N".toCharArray();
			}
			if (atomName.startsWith("S")){
				atomElementFromCoordinates = "S".toCharArray();
			}
			if (atomElementFromCoordinates == null){
				String message = "One atom name in coordiantes template has unguessable element type " + inputMonomer.toString();
				throw new AlteringMyStructureException(message);
			}
			float[] coords = new float[3];
			for (int i=0; i<3; i++){
				coords[i] = entry.getValue()[i];
			}
			RealVector coordsVector = new ArrayRealVector(ToolsMath.convertToDoubleArray(coords));
			RealVector newPointCoords = PairingTools.alignPointFromShape2toShape1(translationVectorToTranslateShape2ToOrigin, translationVectorToTranslateShape2ToShape1, procrustesAnalysis.getRotationMatrix(), coordsVector);

			// I need to translate and rotate them 
			// atom id is not set: dont know if usefull
			MyAtomIfc newAtom;
			try {
				newAtom = new MyAtom(atomElementFromCoordinates, ToolsMath.convertToFloatArray(newPointCoords.toArray()), atomName.toCharArray(), 0);
			} catch (ExceptionInMyStructurePackage e) {
				String message ="convertMyMonomer failed because of invalid atom symbol";
				AlteringMyStructureException e2 = new AlteringMyStructureException(message);
				throw e2;
			}
			newAtom.setBonds(null);
			newAtoms.add(newAtom);
		}
		return newAtoms;
	}



	private static List<MyAtomIfc> generateListBackboneAtomUsingOriginAtomAndOriginBondsExceptCAtoSideChain(MyMonomerIfc inputMonomer)
			throws AlteringMyStructureException {
		// I should find the backbone ones
		MyAtomIfc carbonAlpha = inputMonomer.getMyAtomFromMyAtomName("CA".toCharArray());
		//MyAtomIfc carbonBeta = inputMonomer.getMyAtomFromMyAtomName("CB".toCharArray());
		MyAtomIfc carbonCarbonyl = inputMonomer.getMyAtomFromMyAtomName("C".toCharArray());
		MyAtomIfc oxygenCarbonyl = inputMonomer.getMyAtomFromMyAtomName("O".toCharArray());
		MyAtomIfc nitrogen = inputMonomer.getMyAtomFromMyAtomName("N".toCharArray());

		if (carbonAlpha == null || carbonCarbonyl == null || oxygenCarbonyl == null || nitrogen == null){
			String message = "One or more backbone atoms not found in monomer " + inputMonomer.toString();
			throw new AlteringMyStructureException(message);
		}

		removeCalphaBondsToOthersthanBackBone(carbonAlpha);
		//removeCbetaBondsToOthersthanBackBone(carbonBeta);

		List<MyAtomIfc> tempMyAtomList = new ArrayList<>();
		tempMyAtomList.add(nitrogen);
		tempMyAtomList.add(carbonAlpha);
		tempMyAtomList.add(carbonCarbonyl);
		tempMyAtomList.add(oxygenCarbonyl);
		//tempMyAtomList.add(carbonBeta);
		return tempMyAtomList;
	}



	private static void removeCalphaBondsToOthersthanBackBone(MyAtomIfc carbonAlpha) {
		MyBondIfc[] bondsCalpha = carbonAlpha.getBonds();
		List<MyBondIfc> bondsToKeep = new ArrayList<>();
		for (MyBondIfc bond: bondsCalpha){
			char[] atomName = bond.getBondedAtom().getAtomName();
			if (Arrays.equals(atomName, "C".toCharArray()) || Arrays.equals(atomName, "N".toCharArray())){
				bondsToKeep.add(bond);
			}
		}
		MyBondIfc[] newBonds = bondsToKeep.toArray(new MyBondIfc[bondsToKeep.size()]);
		carbonAlpha.setBonds(newBonds);
	}



	private static void removeCbetaBondsToOthersthanBackBone(MyAtomIfc carbonBeta) {

		MyBondIfc[] bondsCalpha = carbonBeta.getBonds();
		List<MyBondIfc> bondsToKeep = new ArrayList<>();
		for (MyBondIfc bond: bondsCalpha){
			char[] atomName = bond.getBondedAtom().getAtomName();
			if (Arrays.equals(atomName, "CA".toCharArray())){
				bondsToKeep.add(bond);
			}
		}
		MyBondIfc[] newBonds = bondsToKeep.toArray(new MyBondIfc[bondsToKeep.size()]);
		carbonBeta.setBonds(newBonds);
	}



	private static void removeBondsOnlyWithinMonomer(MyMonomerIfc inputMonomer, MyAtomIfc atom){

		MyBondIfc[] bonds = atom.getBonds();

		List<MyBondIfc> bondToBeKept = new ArrayList<>();
		for (MyBondIfc bond: bonds){
			MyMonomerIfc parentMonomerBondedAtom = bond.getBondedAtom().getParent();
			if (parentMonomerBondedAtom != inputMonomer){
				bondToBeKept.add(bond);
			}
		}
		MyBondIfc[] bondsToBeKept = bondToBeKept.toArray(new MyBondIfc[bondToBeKept.size()]);
		atom.setBonds(bondsToBeKept);
	}


	private static RealVector computeBarycenter(RealMatrix matrix){

		RealVector sumVector = new ArrayRealVector(new double[3]);
		sumVector.setEntry(0, 0.0);
		sumVector.setEntry(1, 0.0);
		sumVector.setEntry(2, 0.0);

		int countOfPoints = matrix.getColumnDimension();
		for (int i=0; i< countOfPoints; i++){
			sumVector.addToEntry(0, matrix.getEntry(0, i));
			sumVector.addToEntry(1, matrix.getEntry(1, i));
			sumVector.addToEntry(2, matrix.getEntry(2, i));
		}
		RealVector barycenterVector = sumVector.mapDivide((double) countOfPoints);
		return barycenterVector;
	}


	private static RealMatrix prepare(char[] newResidue3LetterCode, List<MyAtomIfc> backboneAtoms,
			Map<String, float[]> newMonomerCoords) throws AlteringMyStructureException {

		List<float[]> correspondingAtomCoordsCandidate = new ArrayList<>(); // Candidate terminology in procrustes, i.e the one that will be aligned
		for (MyAtomIfc atom: backboneAtoms){
			String atomName = String.valueOf(atom.getAtomName());
			if ( newMonomerCoords.get(atomName) == null){
				System.out.println();
			}
			float[] candidateAtomCoords = newMonomerCoords.get(atomName);

			if (candidateAtomCoords == null){
				String message = "coordinates not found in template for this atom " + atomName + "  " + String.valueOf(newResidue3LetterCode);
				throw new AlteringMyStructureException(message);
			}
			correspondingAtomCoordsCandidate.add(candidateAtomCoords);
		}
		RealMatrix matrixCandidate = prepareInputForProcrustesAnalysis(correspondingAtomCoordsCandidate);
		return matrixCandidate;
	}



	private static RealMatrix prepareInputForProcrustesAnalysis(List<float[]> listCoords){

		double[][]  matrixPointsDouble = new double[3][listCoords.size()];
		RealMatrix matrixPoints = new Array2DRowRealMatrix(matrixPointsDouble);

		for (int i=0; i<listCoords.size(); i++){

			for (int j=0; j<3; j++){
				matrixPoints.setEntry(j, i, listCoords.get(i)[j]);
			}
		}
		return matrixPoints;
	}



	private static RealMatrix prepareInputWithAtomsForProcrustesAnalysis(List<MyAtomIfc> atoms){

		double[][]  matrixPointsDouble = new double[3][atoms.size()];
		RealMatrix matrixPoints = new Array2DRowRealMatrix(matrixPointsDouble);

		for (int i=0; i<atoms.size(); i++){
			MyAtomIfc atom = atoms.get(i);
			float[] coords = atom.getCoords();
			for (int j=0; j<3; j++){
				matrixPoints.setEntry(j, i, coords[j]);
			}
		}
		return matrixPoints;
	}
}