package calibration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import alteratepdbfile.AlterMyStructureTools;
import alteratepdbfile.AlteredResiduesCoordinates;
import alteratepdbfile.AlteringMyStructureException;
import database.SequenceTools;
import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import hits.Hit;
import io.WriteTextFile;
import math.ToolsDistance;
import parameters.AlgoParameters;
import parameters.QueryAtomDefinedByIds;
import pointWithProperties.PointIfc;
import protocols.ControllerLoger;
import protocols.ProtocolToolsToHandleInputFilesAndShapeComparisons;
import shape.ShapeContainerAtomIdsWithinShapeWithPeptide;
import shape.ShapeContainerIfc;
import shapeBuilder.ShapeBuilderConstructorAtomIdsWithinShape;
import shapeBuilder.ShapeBuildingException;
import shapeBuilder.ShapeBuildingTools;
import mystructure.EnumMyReaderBiojava;
import mystructure.ExceptionInMyStructurePackage;
import mystructure.MyAtomIfc;
import mystructure.MyChainIfc;
import mystructure.MyMonomerIfc;
import mystructure.MyStructureIfc;
import mystructure.MyStructureTools;
import ultiJmol1462.ResultsUltiJMolMinimizeSideChain;
import ultiJmol1462.ResultsUltiJMolMinimizedHitLigandOnTarget;
import ultiJmol1462.UltiJMolTools;


public class CompareShapeWithSimulatedShapes {
	//------------------------
	// Class variables
	//------------------------
	private AlgoParameters algoParameters;
	private EnumMyReaderBiojava enumMyReaderBiojava;

	private AlteredResiduesCoordinates alteredResiduesCoordinates;

	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public CompareShapeWithSimulatedShapes(AlgoParameters algoParameters, EnumMyReaderBiojava enumMyReaderBiojava){

		this.algoParameters = algoParameters;
		this.enumMyReaderBiojava = enumMyReaderBiojava;
		this.alteredResiduesCoordinates = new AlteredResiduesCoordinates();
	}


	// -------------------------------------------------------------------
	// Public methods
	// -------------------------------------------------------------------
	public void run() throws ShapeBuildingException, ExceptionInScoringUsingBioJavaJMolGUI{


		// compute a shape and have the local structure
		String fourLetterCodeQuery = "1TW6";
		String chainIdLigand = "C";
		String chainIdTarget = "A";
		MyStructureIfc myStructureGlobalBrut;
		try {
			myStructureGlobalBrut = ShapeBuildingTools.getMyStructure(fourLetterCodeQuery.toCharArray(), algoParameters, enumMyReaderBiojava);
		} catch (ShapeBuildingException e2) {
			System.out.println("Query PDB file failed to read ... program terminated ");
			return;
		}
		//ShapeBuilderConstructorWholeChain queryConstructor = new ShapeBuilderConstructorWholeChain(fourLetterCodeQuery.toCharArray(), chainIdLigand.toCharArray(), algoParameters, enumMyReaderBiojava);

		ShapeContainerIfc queryShape = makeShapeContainer(myStructureGlobalBrut);

		if (queryShape instanceof ShapeContainerAtomIdsWithinShapeWithPeptide){
			ShapeContainerAtomIdsWithinShapeWithPeptide queryShapeClass = (ShapeContainerAtomIdsWithinShapeWithPeptide) queryShape;
			queryShapeClass.setPeptide(myStructureGlobalBrut.getAminoMyChain("C".toCharArray()));
		}

		queryShape.exportMiniShapeColoredToPDBFile("queryMinishape", algoParameters);
		queryShape.exportShapeColoredToPDBFile("queryShape", algoParameters);

		// from local structure
		MyStructureIfc structureLocal = queryShape.getMyStructureUsedToComputeShape();

		MyChainIfc chain = structureLocal.getAminoMyChain("A".toCharArray());
		for (MyMonomerIfc monomer: chain.getMyMonomers()){
			boolean ok = checkIfResidueLikeExpectedIgnoringHydrogens(structureLocal, chain.getChainId(), monomer.getResidueID(), monomer.getThreeLetterCode());
			if (ok == false){
				System.out.println();
			}
		}


		// find spots to mutate
		// there are the one having an atom at least than x angstrom
		double maxDistance = algoParameters.getDISTANCE_FROM_PEPTIDE_TO_WHICH_INTERACTINGPROTEIN_IS_CONSIDERED();

		List<PointIfc> listOfPointsFromChainLigand = MyStructureTools.makeQueryPointsFromMyChainIfc(myStructureGlobalBrut.getAminoMyChain(chainIdLigand.toCharArray()));

		List<MyMonomerIfc> monomersToMutate = identifyMonomerWithAtLeastOneAtomCloseToLigand(structureLocal, maxDistance, listOfPointsFromChainLigand);

		if (monomersToMutate.size() == 0){
			System.out.println("Was nothing to mutate as too far away");
			return;
		}

		// Loop !!

		int countResidueModified = 0;

		for (MyMonomerIfc monomerToMutate: monomersToMutate){

			countResidueModified +=1;
			int monomerToMutateResidueId = monomerToMutate.getResidueID();
			char[] monomerToMutateChainId = monomerToMutate.getParent().getChainId();
			char[] monomerToMutateThreeLetterCode = monomerToMutate.getThreeLetterCode();

			List<String> replacementResidues = SequenceTools.generateNonEquivalentResidues(String.valueOf(monomerToMutateThreeLetterCode));

			int subCount = 0;
			for (String eqRes: replacementResidues){

				MyStructureIfc clonedStructure;
				try {
					clonedStructure = myStructureGlobalBrut.cloneWithSameObjects();
				} catch (ExceptionInMyStructurePackage e1) {
					e1.printStackTrace();
					continue;
				}

				subCount +=1;
				try {
					AlterMyStructureTools.changeOneResidue(clonedStructure, 
							monomerToMutateChainId, monomerToMutateResidueId, monomerToMutateThreeLetterCode,
							eqRes.toCharArray(), alteredResiduesCoordinates);
					boolean ok = checkIfResidueLikeExpectedIgnoringHydrogens(clonedStructure, monomerToMutateChainId, 
							monomerToMutateResidueId, eqRes.toCharArray());
					if (ok == false){
						System.out.println();
					}

				} catch (AlteringMyStructureException e) {
					e.printStackTrace();
					return;
				}
				ShapeContainerIfc targetShape = makeShapeContainer(clonedStructure);

				// what about minimizing sidechain
				// need to protonate but weird as it was done in chani preparatino TODO clean that
				MyStructureIfc protonatedStructure = UltiJMolTools.protonateStructure(clonedStructure, algoParameters);
				ResultsUltiJMolMinimizeSideChain resultsUltiJMolMinimizeSideChain = UltiJMolTools.minimizeSideChainOfAProtonatedMyStructure(algoParameters, protonatedStructure, monomerToMutateChainId, monomerToMutateResidueId, monomerToMutateThreeLetterCode);
				// only to be used as a filter to go ahead or not, if too strained then skip it
				System.out.println("strainedEnergySideChainAfterMinimization = " + resultsUltiJMolMinimizeSideChain.getStrainedEnergySideChainAfterMinimization());
				float strainedEnergy = resultsUltiJMolMinimizeSideChain.getStrainedEnergySideChainAfterMinimization();

				if (targetShape instanceof ShapeContainerAtomIdsWithinShapeWithPeptide){
					MyChainIfc peptideLigand = clonedStructure.getAminoMyChain("C".toCharArray());
					ShapeContainerAtomIdsWithinShapeWithPeptide targetShapeClass = (ShapeContainerAtomIdsWithinShapeWithPeptide) targetShape;
					targetShapeClass.setPeptide(peptideLigand);
				}

				String alteredLocalProteinInV3000 = targetShape.getMyStructureUsedToComputeShape().toV3000();
				//WriteTextFile.writeTextFile(alteredLocalProteinInV3000, "C://Users//fabrice//Documents//ultimate//alteredStructure//alteredProt" + countResidueModified + "_" + subCount + ".mol");
				WriteTextFile.writeTextFile(alteredLocalProteinInV3000, "//Users//fabrice//Documents//ultimate//alteredStructure//alteredProt" + countResidueModified + "_" + subCount + ".mol");


				Hit hit = ProtocolToolsToHandleInputFilesAndShapeComparisons.compareQueryToOneShape(queryShape, targetShape, algoParameters);
				System.out.println(targetShape);
				if (hit == null){
					String message = "Results alter Residue. From " + String.valueOf(monomerToMutateChainId) + " " + String.valueOf(monomerToMutateThreeLetterCode) + " " + monomerToMutateResidueId + " to " 
							+ eqRes + " : failed to lead to a hit" + " strained energy = " + strainedEnergy;
					ControllerLoger.logger.log(Level.INFO, message.toString());
					continue;
				}
				ResultsUltiJMolMinimizedHitLigandOnTarget hitScore = hit.getHitScore();
				double cost = hit.getResultsFromEvaluateCost().getCost();
				float coverage = hit.getResultsFromEvaluateCost().getCoverage();
				double rmsdLigand = hitScore.getRmsdLigand();

				// TODO Log it!
				String message = "Results alter Residue. From " + String.valueOf(monomerToMutateChainId) + " " + String.valueOf(monomerToMutateThreeLetterCode) + " " + monomerToMutateResidueId 
						+ " to " + eqRes + " : cost = " + cost + " coverage = " + coverage + " rmsdLigand " + rmsdLigand  + " strained energy = " + strainedEnergy;
				ControllerLoger.logger.log(Level.INFO, message.toString());

				//WriteTextFile.writeTextFile(alteredProteinInV3000, "//Users//Fabrice//Documents//ultimate//alteredProt.mol");

			}
		}
	}


	private ShapeContainerIfc makeShapeContainer(MyStructureIfc myStructure) throws ShapeBuildingException {

		QueryAtomDefinedByIds atom = new QueryAtomDefinedByIds(String.valueOf(myStructure.getFourLetterCode()), "C", 3, "CA");
		List<QueryAtomDefinedByIds> listQueryAtom = new ArrayList<>();
		listQueryAtom.add(atom);

		List<String> chainToIgnore = new ArrayList<>();
		chainToIgnore.add("C");
		ShapeBuilderConstructorAtomIdsWithinShape targetConstructor = new ShapeBuilderConstructorAtomIdsWithinShape(myStructure, listQueryAtom, 8.0, algoParameters, enumMyReaderBiojava, chainToIgnore);
		ShapeContainerIfc targetShape = targetConstructor.getShapeContainer();
		return targetShape;
	}


	private List<MyMonomerIfc> identifyMonomerWithAtLeastOneAtomCloseToLigand(MyStructureIfc structureLocal, double maxDistance, List<PointIfc> listOfPointsFromChainLigand) {

		List<char[]> backBoneAtomName = MyStructureTools.getBackBoneAtomNames();
		List<MyMonomerIfc> monomertoKeep = new ArrayList<>();
		for (MyChainIfc chain: structureLocal.getAllAminochains()){
			A: for (MyMonomerIfc monomer: chain.getMyMonomers()){
				for (MyAtomIfc atom: monomer.getMyAtoms()){
					if (MyStructureTools.isInList(backBoneAtomName, atom.getAtomName())){
						continue;
					}
					float smallestdidtance = ToolsDistance.computeSmallestDistanceBetweenAPointAndListOfPoints(atom.getCoords(), listOfPointsFromChainLigand);
					if (smallestdidtance < maxDistance){
						monomertoKeep.add(monomer);
						continue A;
					}
				}
			}
		}
		return monomertoKeep;
	}


	private static boolean checkIfResidueLikeExpectedIgnoringHydrogens(MyStructureIfc myStructure, char[] chainId, 
			int residueId, char[] expectedThreeLetterCode){

		MyChainIfc chain = myStructure.getAminoMyChain("A".toCharArray());
		MyMonomerIfc monomerToCheck = chain.getMyMonomerFromResidueId(residueId);

		if (!Arrays.equals(monomerToCheck.getThreeLetterCode(), expectedThreeLetterCode)){
			return false;
		}

		AlteredResiduesCoordinates alteredResiduesCoordinates = new AlteredResiduesCoordinates();
		Map<String, Map<String, float[]>> templateCoords = alteredResiduesCoordinates.getTemplateCoords();

		Map<String, float[]> templateForThisResidue = templateCoords.get(String.valueOf(expectedThreeLetterCode));

		int heavyAtomCount = 0;
		// check if candidate monomer containes all atom from template
		MyAtomIfc[] atoms = monomerToCheck.getMyAtoms();
		for (MyAtomIfc atom: atoms){
			if (MyStructureTools.isHydrogen(atom)){
				continue;
			}
			heavyAtomCount += 1;
			char[] atomName = atom.getAtomName();
			if (!templateForThisResidue.containsKey(String.valueOf(atomName))){
				return false;
			}
		}

		if (templateForThisResidue.size() != heavyAtomCount){
			return false;
		}

		return true;
	}


}
