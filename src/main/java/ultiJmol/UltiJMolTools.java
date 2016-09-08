package ultiJmol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import math.AddToMap;
import org.jmol.api.MinimizerInterface;
import org.jmol.minimize.Minimizer;
import parameters.AlgoParameters;
import shapeBuilder.ShapeBuildingException;
import structure.ExceptionInMyStructurePackage;
import structure.MyAtom;
import structure.MyAtomIfc;
import structure.MyBond;
import structure.MyBondIfc;
import structure.MyChainIfc;
import structure.MyMonomerIfc;
import structure.MyStructure;
import structure.MyStructureConstants;
import structure.MyStructureIfc;
import structure.MyStructureTools;

public class UltiJMolTools {
	public static MyStructureIfc protonateStructure(String inputStructureV3000, AlgoParameters algoParameters) throws ShapeBuildingException{

		MyStructureIfc inputStructure = null;
		try {
			inputStructure = new MyStructure(inputStructureV3000, algoParameters);
		} catch (ExceptionInMyStructurePackage e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		inputStructure.setFourLetterCode("XXXX".toCharArray());
		MyStructureIfc protonatedStructure = protonateStructure(inputStructure, algoParameters);
		return protonatedStructure;
	}



	public static MyStructureIfc protonateStructure(MyStructureIfc inputStructure, AlgoParameters algoParameters) throws ShapeBuildingException{

		MyStructureIfc protonatedStructure = null;
		try {
			protonatedStructure = protonateStructureUsingJMolUFF(inputStructure, algoParameters);
		} catch (ExceptionInScoringUsingBioJavaJMolGUI e) {
			String message = "protonateStructureUsingJMolUFFandStoreBonds failed in Shape Building " + String.valueOf(inputStructure.getFourLetterCode());
			ShapeBuildingException shapeBuildingException= new ShapeBuildingException(message);
			throw shapeBuildingException;
		}

		return protonatedStructure;
	}



	public static ResultsUltiJMolMinimizeSideChain minimizeSideChainOfAProtonatedMyStructure(AlgoParameters algoParameters, MyStructureIfc myStructureInput, char[] chainid, int residueID, char[] monomerTochangeThreeLettercode) throws ExceptionInScoringUsingBioJavaJMolGUI{

		ResultsUltiJMolMinimizeSideChain resultsUltiJMolMinimizeSideChain = null;

		MyStructureIfc clonedMyStructure = null;
		try {
			clonedMyStructure = myStructureInput.cloneWithSameObjects();
		} catch (ExceptionInMyStructurePackage e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		MyChainIfc chain = clonedMyStructure.getAminoMyChain(chainid);
		MyMonomerIfc monomerToModify = chain.getMyMonomerFromResidueId(residueID);

		UltiJMol ultiJMol = null;

		try {
			ultiJMol = algoParameters.ultiJMolBuffer.get();
			float energyTargetBefore = UltiJMolTools.loadMyStructureInBiojavaMinimizeHydrogensComputeEnergyUFF(clonedMyStructure, ultiJMol, algoParameters);
			System.out.println("EnergyTargetBefore = " + energyTargetBefore);

			// make V3000 and identify atomIds to minimize based on coords
			String structureProtonatedV3000 = clonedMyStructure.toV3000();
			List<MyAtomIfc> atomsToMinimize = new ArrayList<>();
			atomsToMinimize.addAll(Arrays.asList(monomerToModify.getMyAtoms()));

			List<Integer> atomids = UltiJMolTools.findAtomIds(structureProtonatedV3000, atomsToMinimize, algoParameters);

			// should always work as continuous but unsure

			String selectResidue = "";
			for (int i=0; i<atomids.size(); i++){
				if (i != atomids.size() - 1){
					selectResidue += "atomno = " + atomids.get(i) + " or ";
				}else{
					selectResidue += "atomno = " + atomids.get(i);
				}

			}

			//String selectResidue2 = "atomno > " + (atomids.get(0)-1) + " and atomno < " + (atomids.get(atomids.size()-1)+1);


			ultiJMol.jmolviewerForUlti.evalString("set forcefield \"UFF\"\n" + "set minimizationsteps 50\n");
			ultiJMol.jmolviewerForUlti.evalString("set logLevel 0\nset undo ON\n set echo off\n set useMinimizationThread ON\n");


			String selectTarget = "all and not (" + selectResidue + ")"; //= "atomno < " + atomids.get(0) + " or atomno > " + atomids.get(atomids.size()-1);
			ultiJMol.jmolviewerForUlti.evalString("spacefill 400");

			ultiJMol.jmolviewerForUlti.evalString("select " + selectTarget);
			//ultiJMol.jmolviewerForUlti.evalString("select " + selectTarget);
			ultiJMol.jmolviewerForUlti.evalString("spacefill 200");

			//ultiJMol.jmolviewerForUlti.evalString("minimize FIX {" + selectTarget + "} select {*}\n");

			Minimize minimize = new Minimize(ultiJMol, selectTarget, algoParameters);
			minimize.compute();
			Float energyComplexFinal = minimize.getEnergyComplexFinal();
			Float receptorFixedLigandOptimizedEStart = minimize.getReceptorFixedLigandOptimizedEStart();
			int countIteration = minimize.getCountIteration();
			boolean receptorFixedLigandOptimizedConvergenceReached = minimize.isReceptorFixedLigandOptimizedConvergenceReached();


			String structureWithMinimizedSideChain = ultiJMol.viewerForUlti.getModelExtract("*", true, false, "V3000");
			// what I need is the energy of the side chain
			// so I should delete all but this and get the energy
			// now I do stuff to compute rmsd
			ultiJMol.jmolviewerForUlti.evalString("delete (" + selectTarget + ") \n");
			Thread.sleep(1000L);
			String sidechainMinizedWithCutBond = ultiJMol.viewerForUlti.getModelExtract("*", true, false, "V3000");

			// but has a cut bond, not good for evaluating forcefield energy ...
			MyStructureIfc sidechainMinizedReprotonated = UltiJMolTools.protonateStructure(sidechainMinizedWithCutBond, algoParameters);


			ultiJMol.jmolviewerForUlti.evalString("zap");
			float energyMinimizedSideChainStrained =  UltiJMolTools.computeEnergyForInPutV3000(ultiJMol, algoParameters, sidechainMinizedReprotonated.toV3000(), true);
			float energyMinimizedFullyMinimized =  UltiJMolTools.computeEnergyForInPutV3000(ultiJMol, algoParameters, sidechainMinizedReprotonated.toV3000(), false);
			Thread.sleep(1000L);
			System.out.println("E sideChainStrained = " + energyMinimizedSideChainStrained);
			System.out.println("E sideChain Relaxed = " + energyMinimizedFullyMinimized);

			float strainedEnergy = energyMinimizedSideChainStrained - energyMinimizedFullyMinimized; // if strained then energyMinimizedLigand > energyPeptideRelaxed so positive result
			System.out.println("E strained = " + strainedEnergy);

			// I would then need a new MyStructure cloned from original one
			// I would only change the coodinates of modified atom
			MyStructureIfc clonedMyStructureToReturn = myStructureInput.cloneWithSameObjects();
			MyChainIfc chainToModifyFinal = clonedMyStructure.getAminoMyChain(chainid);
			MyMonomerIfc monomerToModifyFinal = chain.getMyMonomerFromResidueId(residueID);

			MyStructureIfc myStructureFromV3000 = new MyStructure(structureWithMinimizedSideChain, algoParameters);

			int currentIdInModifiedMonomer = 0;
			for (int atomId: atomids){
				MyAtomIfc atomWithMinimizedCoords = myStructureFromV3000.getAllAminochains()[0].getMyMonomers()[0].getMyAtoms()[atomId];
				MyAtomIfc atom = monomerToModifyFinal.getMyAtoms()[currentIdInModifiedMonomer];
				atom.setCoords(atomWithMinimizedCoords.getCoords().clone());
				currentIdInModifiedMonomer += 1;
			}
			resultsUltiJMolMinimizeSideChain = new ResultsUltiJMolMinimizeSideChain(clonedMyStructureToReturn, strainedEnergy);

		} catch(Exception e){

			System.out.println("Exception in  !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			ultiJMol.frameForUlti.dispose(); // it is destroyed so not returned to factory
			try {
				algoParameters.ultiJMolBuffer.put(new UltiJMol());
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String message = "Exception in ";
			ExceptionInScoringUsingBioJavaJMolGUI exception = new ExceptionInScoringUsingBioJavaJMolGUI(message);
			throw exception;
		}

		try {
			ultiJMol.jmolviewerForUlti.evalString("zap");
			try {
				Thread.sleep(2000L);
			} catch (InterruptedException e) {

			}
			algoParameters.ultiJMolBuffer.put(ultiJMol);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return resultsUltiJMolMinimizeSideChain;
	}



	public static ResultsUltiJMolMinimizedHitLigandOnTarget scoreByMinimizingLigandOnFixedReceptor(AlgoParameters algoParameters, 
			MyStructureIfc peptide, MyStructureIfc target) throws ExceptionInScoringUsingBioJavaJMolGUI {

		ResultsUltiJMolMinimizedHitLigandOnTarget hitScore = null;
		UltiJMol ultiJMol = null; 

		try {
			ultiJMol = algoParameters.ultiJMolBuffer.get();

			float energyTargetBefore = loadMyStructureInBiojavaMinimizeHydrogensComputeEnergyUFF(target, ultiJMol, algoParameters);
			System.out.println("ET0 = " + energyTargetBefore);
			String targetAfterHyfrogenMinimizationV3000 = ultiJMol.viewerForUlti.getModelExtract("*", true, false, "V3000");

			// Peptide of a hit is not proptonated by definition
			float energyPeptideBefore = loadMyStructureInBiojavaMinimizeHydrogensComputeEnergyUFF(peptide, ultiJMol, algoParameters);
			System.out.println("EL0 = " + energyPeptideBefore);
			String peptideAfterHyfrogenMinimizationV3000 = ultiJMol.viewerForUlti.getModelExtract("*", true, false, "V3000");


			int firstAtomNumberPeptide = mergeTwoV3000FileReturnIdOfFirstAtomMyStructure2AndLoadInViewer(targetAfterHyfrogenMinimizationV3000, peptideAfterHyfrogenMinimizationV3000, algoParameters, ultiJMol);

			String selectTarget = "atomno > 0 and atomno < " + firstAtomNumberPeptide;
			String selectLigand = "{atomno > " + (firstAtomNumberPeptide - 1) + "}";

			//String select = "select *";
			ultiJMol.jmolviewerForUlti.evalString("select {" + selectTarget + "}");
			ultiJMol.jmolviewerForUlti.evalString("spacefill 400");
			ultiJMol.jmolviewerForUlti.evalString("select " + selectLigand);
			ultiJMol.jmolviewerForUlti.evalString("spacefill 200");

			Minimize minimize = new Minimize(ultiJMol, selectTarget, algoParameters);
			minimize.compute();
			Float energyComplexFinal = minimize.getEnergyComplexFinal();
			Float receptorFixedLigandOptimizedEStart = minimize.getReceptorFixedLigandOptimizedEStart();
			int countIteration = minimize.getCountIteration();
			boolean receptorFixedLigandOptimizedConvergenceReached = minimize.isReceptorFixedLigandOptimizedConvergenceReached();

			// now I do stuff to compute rmsd
			ultiJMol.jmolviewerForUlti.evalString("delete (" + selectTarget + ") \n");
			Thread.sleep(1000L);
			String peptideAfterMinimizeFixingSelectionV3000 = ultiJMol.viewerForUlti.getModelExtract("*", true, false, "V3000");

			ultiJMol.jmolviewerForUlti.evalString("zap");
			double energyMinimizedLigand =  computeEnergyForInPutV3000(ultiJMol, algoParameters, peptideAfterMinimizeFixingSelectionV3000, true);

			Thread.sleep(1000L);
			System.out.println("Elf = " + energyMinimizedLigand);

			// relqx completely ligqnd to hqve strained energy
			float energyPeptideRelaxed = loadMyStructureInBiojavaMinimizeAllComputeEnergyUFF(peptide, ultiJMol, algoParameters);

			double strainedEnergy = energyMinimizedLigand - energyPeptideRelaxed; // if strained then energyMinimizedLigand > energyPeptideRelaxed so positive result
			System.out.println("EstrainedLigand = " + strainedEnergy);

			double eInterfinal = energyComplexFinal - energyTargetBefore - energyMinimizedLigand;
			double eCorrected = eInterfinal + energyMinimizedLigand;
			System.out.println("Einter = " + eInterfinal);
			System.out.println("Einter + EstrainedLigand = " + eCorrected);

			ComputeRmsd computeRmsd = new ComputeRmsd(peptideAfterMinimizeFixingSelectionV3000, peptideAfterHyfrogenMinimizationV3000, algoParameters);

			float rmsd = computeRmsd.getRmsd();
			int countOfLongDistanceChange = computeRmsd.getCountOfLongDistanceChange();
			hitScore = new ResultsUltiJMolMinimizedHitLigandOnTarget(receptorFixedLigandOptimizedEStart, energyComplexFinal, countIteration, receptorFixedLigandOptimizedConvergenceReached, rmsd, countOfLongDistanceChange, eInterfinal, eCorrected);

		} catch(Exception e){

			System.out.println("Exception in scoreByMinimizingLigandOnFixedReceptor !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			ultiJMol.frameForUlti.dispose(); // it is destroyed so not returned to factory
			try {
				algoParameters.ultiJMolBuffer.put(new UltiJMol());
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String message = "Exception in scoreByMinimizingLigandOnFixedReceptor";
			ExceptionInScoringUsingBioJavaJMolGUI exception = new ExceptionInScoringUsingBioJavaJMolGUI(message);
			throw exception;
		}

		try {
			ultiJMol.jmolviewerForUlti.evalString("zap");
			try {
				Thread.sleep(2000L);
			} catch (InterruptedException e) {

			}
			algoParameters.ultiJMolBuffer.put(ultiJMol);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hitScore;

	}



	public static Float getEnergyBiojavaJmolNewCode(UltiJMol ultiJMol, AlgoParameters algoParameters) throws ExceptionInScoringUsingBioJavaJMolGUI{

		Float energy = waitMinimizationEnergyAvailable(2, ultiJMol);
		if (energy == null){
			String message = "waitMinimizationEnergyAvailable failed";
			ExceptionInScoringUsingBioJavaJMolGUI exception = new ExceptionInScoringUsingBioJavaJMolGUI(message);
			throw exception;
		}
		return energy;
	}




	//-------------------------------------------------------------
	// Implementation
	//-------------------------------------------------------------
	private static MyStructureIfc protonateStructureUsingJMolUFF(MyStructureIfc myStructureInput, AlgoParameters algoParameters) throws ExceptionInScoringUsingBioJavaJMolGUI{

		MyStructureIfc clonedMyStructureThatCouldHaveHydrogens = null;
		try {
			clonedMyStructureThatCouldHaveHydrogens = myStructureInput.cloneWithSameObjects();
		} catch (ExceptionInMyStructurePackage e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		UltiJMol ultiJMol = null;
		String readV3000;
		try{
			ultiJMol = algoParameters.ultiJMolBuffer.get();
			//(ultiJMol, myStructure, algoParameters, filenameV3000);


			//deleteFileIfExist(outputFileName);
			addHydrogensInJMolUsingUFF(ultiJMol, clonedMyStructureThatCouldHaveHydrogens, algoParameters);
			readV3000 = ultiJMol.viewerForUlti.getModelExtract("*", true, false, "V3000");

		} catch(Exception e){
			System.out.println("Exception in protonation !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			ultiJMol.frameForUlti.dispose(); // it is destroyed so not returned to factory
			try {
				algoParameters.ultiJMolBuffer.put(new UltiJMol());
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
			String message = "Exception in protonation";
			ExceptionInScoringUsingBioJavaJMolGUI exception = new ExceptionInScoringUsingBioJavaJMolGUI(message);
			throw exception;
		}
		try {
			ultiJMol.jmolviewerForUlti.evalString("zap");
			try {
				Thread.sleep(2000L);
			} catch (InterruptedException e) {

			}
			algoParameters.ultiJMolBuffer.put(ultiJMol);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		MyStructureIfc myStructureWithBondsAndHydrogenAtoms = null;
		try {
			myStructureWithBondsAndHydrogenAtoms = new MyStructure(readV3000, algoParameters);
		} catch (ExceptionInMyStructurePackage e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		myStructureWithBondsAndHydrogenAtoms.setFourLetterCode(myStructureInput.getFourLetterCode());
		addHydrogenInformation(clonedMyStructureThatCouldHaveHydrogens, myStructureWithBondsAndHydrogenAtoms);

		return clonedMyStructureThatCouldHaveHydrogens;
	}



	private static void addHydrogensInJMolUsingUFF(UltiJMol ultiJmol, MyStructureIfc myStructure, AlgoParameters algoParameters) throws ExceptionInScoringUsingBioJavaJMolGUI {

		try{
			ultiJmol.jmolviewerForUlti.openStringInline(myStructure.toV3000());

			String selectString = "hydrogen";
			ultiJmol.jmolviewerForUlti.evalString("delete " + selectString);

			try {
				Thread.sleep(1000L);
			} catch (InterruptedException e) {

			}

			ultiJmol.jmolviewerForUlti.evalString("set forcefield \"UFF\"\n" + "set minimizationsteps 20\n");
			ultiJmol.jmolviewerForUlti.evalString("minimize energy ADDHYDROGENS\n");

			try {
				Thread.sleep(1000L);
			} catch (InterruptedException e) {
			}

			int maxCountIteration = 200;
			int countIteration = 0;
			while (ultiJmol.viewerForUlti.areHydrogenAdded() == false){
				countIteration +=1;
				if (countIteration > maxCountIteration){
					String message = "problem in addHydrogensInJMolUsingUFF : too many iteration to wait for adding hydrogens";
					ExceptionInScoringUsingBioJavaJMolGUI exception = new ExceptionInScoringUsingBioJavaJMolGUI(message);
					throw exception;
				}
				try {
					Thread.sleep(2000L);
				} catch (InterruptedException e) {
				}
			}
			try { // I think important as there is some time needed after adding h and minimization them
				// it works thanks to q hqck in Jmol project
				Thread.sleep(3000L);
			} catch (InterruptedException e) {

			}
		} catch(Exception e){
			e.printStackTrace();
			String message = "add hydrogens and minimized failed at ";
			ExceptionInScoringUsingBioJavaJMolGUI exception = new ExceptionInScoringUsingBioJavaJMolGUI(message);
			throw exception;
		}
	}



	private static void addHydrogenInformation(MyStructureIfc myStructureThatCouldHaveHydrogens, MyStructureIfc myStructureWithBondsAndHydrogenAtoms){

		// remove hydrogens and bond to hydrogens: sometimes there are and also for Xray structure
		//		for (MyChainIfc chain: myStructure.getAllChains()){
		//			for (MyMonomerIfc monomer: chain.getMyMonomers()){
		//				
		//			}
		//		}
		MyStructureTools.removeAllExplicitHydrogens(myStructureThatCouldHaveHydrogens);
		Map<MyAtomIfc, MyAtomIfc> mapToGetCorrespondingAtomWithInformation = buildCorrespondanceHeavyAtomsMapBasedOnAtomXYZ(myStructureWithBondsAndHydrogenAtoms, myStructureThatCouldHaveHydrogens);

		// I loop on existing bonds and create bonds with new references
		// I do it here as it is a special MyStructure with only one chain: so not good to put that in a library and not needed
		MyChainIfc chain = myStructureWithBondsAndHydrogenAtoms.getAllAminochains()[0];
		MyMonomerIfc onlyMonomerInMyStructure = chain.getMyMonomers()[0];

		for (MyAtomIfc atom: onlyMonomerInMyStructure.getMyAtoms()){

			if (Arrays.equals(atom.getElement(), "H".toCharArray())){
				continue;
			}
			// translate this bonds into myStructure
			MyAtomIfc atomInMyStructure = mapToGetCorrespondingAtomWithInformation.get(atom);

			MyBondIfc[] bonds = atom.getBonds();
			if (bonds == null){ // there are atoms without any bonds
				continue;
			}
			List<MyBondIfc> correspondingBonds = new ArrayList<>();
			for (MyBondIfc bond: bonds){
				if (Arrays.equals(bond.getBondedAtom().getElement(), "H".toCharArray())){ // it wont work to create bonds with H as H are not in correspondance map
					continue;
				}
				MyAtomIfc atombondedInMyStructure = mapToGetCorrespondingAtomWithInformation.get(bond.getBondedAtom());
				MyBondIfc correspondingBond = null;
				try {
					correspondingBond = new MyBond(atombondedInMyStructure, bond.getBondOrder());
				} catch (ExceptionInMyStructurePackage e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				correspondingBonds.add(correspondingBond);
			}
			MyBondIfc[] correspondingBondsArray = correspondingBonds.toArray(new MyBondIfc[correspondingBonds.size()]);
			atomInMyStructure.setBonds(correspondingBondsArray);

		}

		// Now I add Hydrogens
		int atomCountWithoutHydrogen = onlyMonomerInMyStructure.getMyAtoms().length;
		int countAtom = atomCountWithoutHydrogen;
		Map<MyAtomIfc, List<MyAtomIfc>> mapHeavyAtomAndHydrogen = buildMapHeavyAtomAndHydrogen(myStructureWithBondsAndHydrogenAtoms);

		int heavyAtomCount = 1;
		for (Entry<MyAtomIfc, List<MyAtomIfc>> entry: mapHeavyAtomAndHydrogen.entrySet()){


			MyAtomIfc heavyAtomInMyStructure = mapToGetCorrespondingAtomWithInformation.get(entry.getKey());
			if (heavyAtomInMyStructure == null){
				System.out.println();
				continue;
			}


			int hydrogenCountForThisHeavyAtom = 1;

			for (MyAtomIfc hydrogenAtom: entry.getValue()){

				//String atomName = "H" + hydrogenCountForThisHeavyAtom + String.valueOf(heavyAtomInMyStructure.getAtomName());

				countAtom += 1;
				char[] heavyAtomNameToUse;
				if (heavyAtomInMyStructure.getAtomName().length == 0){
					heavyAtomNameToUse = ("_" + String.valueOf(heavyAtomCount)).toCharArray(); // structure coming from V3000 doesnt have atom names !!
				}else{
					heavyAtomNameToUse = heavyAtomInMyStructure.getAtomName();
				}
				String atomName = hydrogenCountForThisHeavyAtom + String.valueOf(heavyAtomNameToUse);
				MyAtomIfc newHydrogen = null;
				try {
					newHydrogen = new MyAtom("H".toCharArray(), hydrogenAtom.getCoords(), atomName.toCharArray(), countAtom);
				} catch (ExceptionInMyStructurePackage e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// need parent
				newHydrogen.setParent(heavyAtomInMyStructure.getParent());

				// need bond
				MyBondIfc bondHtoHeavyAtom = null;
				try {
					bondHtoHeavyAtom = new MyBond(heavyAtomInMyStructure, 1);
				} catch (ExceptionInMyStructurePackage e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				MyBondIfc[] bondsHtoHeavyAtom = new MyBondIfc[1];
				bondsHtoHeavyAtom[0] = bondHtoHeavyAtom;
				newHydrogen.setBonds(bondsHtoHeavyAtom);


				MyBondIfc bondHeavyAtomToH = null;
				try {
					bondHeavyAtomToH = new MyBond(newHydrogen, 1);
				} catch (ExceptionInMyStructurePackage e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				heavyAtomInMyStructure.addBond(bondHeavyAtomToH);

				// need to add the H in the right monomer
				MyMonomerIfc monomerToWhichHydrogenBelongs = newHydrogen.getParent();
				monomerToWhichHydrogenBelongs.addAtom(newHydrogen);

				// char[] element, float[] coords, char[] atomName, int originalAtomId
				hydrogenCountForThisHeavyAtom +=1;
			}
		}
	}



	private static Map<MyAtomIfc, List<MyAtomIfc>> buildMapHeavyAtomAndHydrogen(MyStructureIfc myStructureWithBondsAndHydrogenAtoms){

		Map<MyAtomIfc, List<MyAtomIfc>> mapHeavyAtomAndHydrogens = new HashMap<>();
		MyChainIfc chain = myStructureWithBondsAndHydrogenAtoms.getAllAminochains()[0];
		MyMonomerIfc onlyMonomerInMyStructure = chain.getMyMonomers()[0];

		for (MyAtomIfc atom: onlyMonomerInMyStructure.getMyAtoms()){
			if (Arrays.equals(atom.getElement(), "H".toCharArray())){
				MyAtomIfc heavyAtom = atom.getBonds()[0].getBondedAtom();
				if (!Arrays.equals(heavyAtom.getElement(), "H".toCharArray())){ // that happen with 1A5H
					AddToMap.addElementToAMapOfList(mapHeavyAtomAndHydrogens, heavyAtom, atom);
				}
			}
		}
		return mapHeavyAtomAndHydrogens;
	}



	private static Map<MyAtomIfc, MyAtomIfc> buildCorrespondanceHeavyAtomsMapBasedOnAtomXYZ(MyStructureIfc myStructure1, MyStructureIfc myStructure2){

		Map<MyAtomIfc, MyAtomIfc> matchingAtoms = new HashMap<>();

		for (MyChainIfc chain1 : myStructure1.getAllChains()){
			for (MyMonomerIfc monomer1 : chain1.getMyMonomers()){
				A: for (MyAtomIfc atom1 : monomer1.getMyAtoms()){

					if (Arrays.equals(atom1.getElement(), "H".toCharArray())){
						continue;
					}
					float[] coords1 = atom1.getCoords();

					for (MyChainIfc chain2 : myStructure2.getAllChains()){
						for (MyMonomerIfc monomer2 : chain2.getMyMonomers()){
							for (MyAtomIfc atom2 : monomer2.getMyAtoms()){
								if (Arrays.equals(atom2.getElement(), "H".toCharArray())){
									continue;
								}
								float[] coords2 = atom2.getCoords();

								boolean matching = doCoordinatesMAtchForSameAtomToOnlyNumericalError(coords1, coords2);
								if (matching == true){
									matchingAtoms.put(atom1, atom2);
									continue A;
								}
							}
						}
					}
				}
			}
		}
		return matchingAtoms;
	}



	public static boolean doCoordinatesMAtchForSameAtomToOnlyNumericalError(float[] coords1, float[] coords2){

		float numericalError = 0.1f;
		for (int i=0; i<3; i++){
			if (Math.abs(coords1[i]-coords2[i]) > numericalError){
				return false;
			}
		}
		return true;
	}



	public static Float loadMyStructureInBiojavaMinimizeHydrogensComputeEnergyUFF(MyStructureIfc myStructure, UltiJMol ultiJmol, AlgoParameters algoParameters) throws ExceptionInScoringUsingBioJavaJMolGUI, InterruptedException{

		ultiJmol.jmolviewerForUlti.evalString("zap");
		String myStructureV3000 = myStructure.toV3000();

		return computeEnergyForInPutV3000(ultiJmol, algoParameters, myStructureV3000, true);
	}



	private static Float loadMyStructureInBiojavaMinimizeAllComputeEnergyUFF(MyStructureIfc myStructure, UltiJMol ultiJmol, AlgoParameters algoParameters) throws ExceptionInScoringUsingBioJavaJMolGUI, InterruptedException{

		ultiJmol.jmolviewerForUlti.evalString("zap");
		String myStructureV3000 = myStructure.toV3000();

		return computeEnergyForInPutV3000(ultiJmol, algoParameters, myStructureV3000, false);
	}



	public static Float loadV3000FileInBiojavaMinimizeAllComputeEnergyUFF(String myStructureV3000, UltiJMol ultiJmol, AlgoParameters algoParameters) throws ExceptionInScoringUsingBioJavaJMolGUI, InterruptedException{

		ultiJmol.jmolviewerForUlti.evalString("zap");
		return computeEnergyForInPutV3000(ultiJmol, algoParameters, myStructureV3000, false);
	}



	public static Float computeEnergyForInPutV3000(UltiJMol ultiJMol, AlgoParameters algoParameters,
			String myStructureV3000, boolean onlyHydrogen) throws InterruptedException,
	ExceptionInScoringUsingBioJavaJMolGUI {
		Float energy;
		Thread.sleep(1000L);
		ultiJMol.jmolviewerForUlti.openStringInline(myStructureV3000);

		// What about minimizing a bit the hydrogens
		boolean convergenceReached = false;



		//ultiJMol.jmolviewerForUlti.evalString("spacefill 200");

		ultiJMol.jmolviewerForUlti.evalString("set forcefield \"UFF\"\n" + "set minimizationsteps 50\n");
		ultiJMol.jmolviewerForUlti.evalString("set logLevel 0\nset undo ON\n set echo off\n set useMinimizationThread ON\n");

		energy = 1E8f;
		//System.out.println("startenergy = " + energy);
		String selectString = "";
		if (onlyHydrogen == true){
			selectString = "not { hydrogen }";
			//ultiJMol.jmolviewerForUlti.evalString("select " + selectString);
			ultiJMol.jmolviewerForUlti.evalString("minimize FIX {" + selectString + "} select {*}\n");
		}else{
			ultiJMol.jmolviewerForUlti.evalString("minimize select {*}\n");
			//ultiJMol.jmolviewerForUlti.evalString("select " + selectString);
		}


		int countIteration = 0;
		int maxIteration = 20;
		boolean goAhead = true;
		while (countIteration <= maxIteration && goAhead == true){

			Thread.sleep(4000L);

			Float currentEnergy = getEnergyBiojavaJmolNewCode(ultiJMol, algoParameters);
			if (currentEnergy == null){
				System.out.println();
			}
			countIteration +=1;

			System.out.println("currentEnergy = " + currentEnergy);
			// when too high then I should give up 
			if (currentEnergy > 1E8){
				//System.out.println("Minimization is aborted as energy is > 1E8 ");
				//return null;
			}

			if (Math.abs(currentEnergy - energy) < 5.0){
				goAhead = false;
			}
			energy = currentEnergy;
		}

		System.out.println("did " + countIteration + " iterations");
		Thread.sleep(1000L);

		if (countIteration <= maxIteration == false){
			convergenceReached = false;
		}else{
			convergenceReached = true;
		}

		energy = getEnergyBiojavaJmolNewCode(ultiJMol, algoParameters);
		ultiJMol.jmolviewerForUlti.evalString("minimize stop");

		// ??
		ultiJMol.jmolviewerForUlti.evalString("minimize FIX {*}");
		ultiJMol.jmolviewerForUlti.evalString("select {*}\n");

		//		}finally{
		//			StaticObjects.biojavaJmol.evalString("zap");
		//		}
		return energy;
	}



	private static int mergeTwoV3000FileReturnIdOfFirstAtomMyStructure2AndLoadInViewer(String structureV3000, String peptideV3000, AlgoParameters algoParameters, UltiJMol ultiJMol) throws ExceptionInScoringUsingBioJavaJMolGUI{

		MyStructureIfc myStructureFile1 = null;
		try {
			myStructureFile1 = new MyStructure(structureV3000, algoParameters);
		} catch (ExceptionInMyStructurePackage e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int countAtomFile1 = myStructureFile1.getAllAminochains()[0].getMyMonomers()[0].getMyAtoms().length;

		MyStructureIfc myStructureFile2 = null;
		try {
			myStructureFile2 = new MyStructure(peptideV3000, algoParameters);
		} catch (ExceptionInMyStructurePackage e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int countAtomFile2 = myStructureFile2.getAllAminochains()[0].getMyMonomers()[0].getMyAtoms().length;

		MyStructureIfc mergedMyStructure = new MyStructure(myStructureFile1.getAllAminochains()[0], myStructureFile2.getAllAminochains()[0], algoParameters);
		int countAtomOutput = mergedMyStructure.getAllAminochains()[0].getMyMonomers()[0].getMyAtoms().length +  mergedMyStructure.getAllAminochains()[1].getMyMonomers()[0].getMyAtoms().length;

		//System.out.println(countAtomFile1 + " + " + countAtomFile2 + " = " + countAtomOutput );
		if ((countAtomFile1 + countAtomFile2) != countAtomOutput){
			String message = ("countAtomFile1 + countAtomFile2 is not equal to countAtomOutput");
			ExceptionInScoringUsingBioJavaJMolGUI exception = new ExceptionInScoringUsingBioJavaJMolGUI(message);
			throw exception;
		}

		ultiJMol.jmolviewerForUlti.evalString("zap");
		ultiJMol.jmolviewerForUlti.openStringInline(mergedMyStructure.toV3000());

		return countAtomFile1 + 1;
	}



	private static Float waitMinimizationEnergyAvailable(int waitTimeSeconds, UltiJMol ultiJMol) throws ExceptionInScoringUsingBioJavaJMolGUI {

		int maxIteration = 20;
		int countIteration = 0;

		MinimizerInterface minimizer = ultiJMol.viewerForUlti.getMinimizer(true);

		while( minimizer == null || minimizer.getMinimizationEnergy() == null){
			try {
				Thread.sleep(waitTimeSeconds * 1000);
				countIteration += 1;
				System.out.println(countIteration);
				//System.out.println(countIteration);
				if (countIteration > maxIteration){
					String message = "Wait for Minimization Energy to be available failed because too many iterations :  ";
					ExceptionInScoringUsingBioJavaJMolGUI exception = new ExceptionInScoringUsingBioJavaJMolGUI(message);
					throw exception;
				}
			} catch (InterruptedException e) {
				String message = "Wait for Minimization Energy to be available failed because of Exception";
				ExceptionInScoringUsingBioJavaJMolGUI exception = new ExceptionInScoringUsingBioJavaJMolGUI(message);
				throw exception;
			}
			minimizer = ultiJMol.viewerForUlti.getMinimizer(true);
		}
		return minimizer.getMinimizationEnergy();
	}



	public static List<Integer> findAtomIds(String structureV3000, List<MyAtomIfc> atomsToFindIdsOf, AlgoParameters algoParameters){

		List<Integer> atomIds = new ArrayList<>();
		MyStructure mystructureFromV3000 = null;
		try {
			mystructureFromV3000 = new MyStructure(structureV3000, algoParameters);
		} catch (ExceptionInMyStructurePackage e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // I know this one is linear to V3000

		for (MyAtomIfc atomToFind: atomsToFindIdsOf){
			if (MyStructureTools.isAtomFromBackBoneOtherThanCA(atomToFind) == true){
				continue;
			}
			float[] coordsToFindId = atomToFind.getCoords();
			Integer foundId = findId(mystructureFromV3000, coordsToFindId);
			atomIds.add(foundId);
		}
		return atomIds;
	}



	public static List<Integer> findAtomIdsWithBackbone(String structureV3000, List<MyAtomIfc> atomsToFindIdsOf, AlgoParameters algoParameters){

		List<Integer> atomIds = new ArrayList<>();
		MyStructure mystructureFromV3000 = null;
		try {
			mystructureFromV3000 = new MyStructure(structureV3000, algoParameters);
		} catch (ExceptionInMyStructurePackage e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // I know this one is linear to V3000

		for (MyAtomIfc atomToFindId: atomsToFindIdsOf){

			float[] coordsToFindId = atomToFindId.getCoords();
			Integer foundId = findId(mystructureFromV3000, coordsToFindId);
			atomIds.add(foundId);
		}
		return atomIds;
	}



	private static Integer findId(MyStructure mystructureFromV3000, float[] coordsToFindId){
		List<Integer> foundId = new ArrayList<>();
		float threshold = MyStructureConstants.TOLERANCE_FOR_SAME_COORDINATES;
		int currentId = 0;
		Atoms: for (MyAtomIfc atom: mystructureFromV3000.getAllAminochains()[0].getMyMonomers()[0].getMyAtoms()){
			currentId += 1;
			for (int i=0; i<3; i++){
				float candidateCoord = atom.getCoords()[i];
				float coordToFind = coordsToFindId[i];
				if (Math.abs(candidateCoord - coordToFind) > threshold){
					continue Atoms; // because unmatch
				}
			}
			// so it matches
			foundId.add(currentId);	
		}
		if (foundId.size() != 1){
			System.out.println("Unbearable error in findId");
			System.exit(0);
		}
		return foundId.get(0);
	}
}
