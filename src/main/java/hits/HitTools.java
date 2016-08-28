package hits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import math.ToolsMath;
import parameters.AlgoParameters;
import pointWithProperties.CollectionOfPointsWithProperties;
import pointWithProperties.CollectionOfPointsWithPropertiesIfc;
import pointWithProperties.Point;
import pointWithProperties.PointIfc;
import pointWithProperties.PointWithProperties;
import pointWithProperties.PointWithPropertiesIfc;
import shapeCompare.PairingTools;
import shapeCompare.ResultsFromEvaluateCost;
import structure.ExceptionInMyStructurePackage;
import structure.MyAtomIfc;
import structure.MyChainIfc;
import structure.MyMonomerIfc;
import structure.MyStructure;
import structure.MyStructureIfc;
import structure.MyStructureTools;

public class HitTools {

	public static Set<char[]> makeListOfChainId(MyStructureIfc myStructure){
		Set<char[]> setChainIds = new HashSet<>();

		for (MyChainIfc chain: myStructure.getAllChains()){
			setChainIds.add(chain.getChainId());
		}
		return setChainIds;	
	}



	public static MyStructureIfc restrictNeighbors(MyStructureIfc myStructureUsedToComputeShape, MyStructureIfc peptide, AlgoParameters algoParameters){

		Set<MyMonomerIfc> myMonomerToKeep = new HashSet<>();

		// I guess I cant rely on neighbors by monomer
		List<PointIfc> pointsLigands = MyStructureTools.makeQueryPointsFromMyChainIfc(peptide.getAminoChain(0));

		for (MyChainIfc chain: myStructureUsedToComputeShape.getAllChainsRelevantForShapeBuilding()){
			A: for (MyMonomerIfc myMonomer: chain.getMyMonomers()){
				//float[] representativeCoords = ToolsMathAppliedToObjects.getCoordinatesOfRepresentativeAtom(myMonomer);
				for (MyAtomIfc atom: myMonomer.getMyAtoms()){
					for (PointIfc pointLigand: pointsLigands){
						float distance = ToolsMath.computeDistance(pointLigand.getCoords(), atom.getCoords());
						if (distance < algoParameters.getMIN_DISTANCE_TO_BE_NEIBHOR_IN_JMOL_MINIMIZATION()){
							myMonomerToKeep.add(myMonomer); // monomer is kept is at least one of its atom is closer that threshold to
							// any of the ligand, then we should be closer to interaction distances
							continue A;
						}
					}
				}
			}
		}

		// I keep monomers which are in close distance to ligand
		MyStructureIfc restrictedToNeighbors = null;
		try {
			restrictedToNeighbors = myStructureUsedToComputeShape.cloneWithSameObjectsWhileKeepingOnlyMyMonomerInThisSet(myMonomerToKeep);
		} catch (ExceptionInMyStructurePackage e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return restrictedToNeighbors;
	}



//	public static HitScore scoreHitWithinQuery(Hit hit, MyStructureIfc myStructureQueryUsedToComputeShape, AlgoParameters algoParameters) throws ExceptionInScoringUsingBioJavaJMolGUI, ShapeBuildingException{
//
//		MyChainIfc peptideOrLigand = null;
//
//		if (hit.getShapeContainer() instanceof HasPeptideIfc){
//			HasPeptideIfc queryShapeWithPeptide = (HasPeptideIfc) hit.getShapeContainer();
//			peptideOrLigand = queryShapeWithPeptide.getPeptide();
//		}
//		if (hit.getShapeContainer() instanceof ShapeContainerWithLigand){
//			ShapeContainerWithLigand shapeContainerWithLigand = (ShapeContainerWithLigand) hit.getShapeContainer();
//			peptideOrLigand = new MyChain(shapeContainerWithLigand.getHetatmLigand(), shapeContainerWithLigand.getHetatmLigand().getParent().getChainId()); // returnCloneRotatedPeptide(hetAtomInChain, hit.getResultsFromEvaluateCost());	
//		}
//
//		MyStructureIfc myStructurePeptide = new MyStructure(peptideOrLigand, algoParameters);
//
//
//		MyStructureIfc preparedPeptide = UltiJMolTools.protonateStructure(myStructurePeptide, algoParameters); // c'est ca qui deconne a mettre trop d'hydrogene
//
//		MyStructureIfc clonedRotated = preparedPeptide.cloneWithSameObjectsRotatedCoords(hit.getResultsFromEvaluateCost());
//
//		//restrictNeighbors could be applied on something like the query because hits are in the vicinity of the query of course
//		MyStructureIfc myStructureForJMol = restrictNeighbors(myStructureQueryUsedToComputeShape, clonedRotated, algoParameters);
//
//		//		if (myStructureForJMol == null){
//		//			String message = "restrictNeighbors failed to return any neighbors for ligand "; 
//		//			ExceptionInScoringUsingBioJavaJMolGUI exception = new ExceptionInScoringUsingBioJavaJMolGUI(message);
//		//			throw exception;
//		//		}
//
//		HitScore hitScore = UltiJMolTools.scoreByMinimizingLigandOnFixedReceptor(algoParameters, clonedRotated, myStructureForJMol);
//
//		if (hitScore != null){
//			System.out.println("Estart = " + hitScore.getReceptorFixedLigandOptimizedEStart());
//			System.out.println("Efinal = " + hitScore.getReceptorFixedLigandOptimizedEFinal());
//			System.out.println("iteration = " + hitScore.getReceptorFixedLigandOptimizedCountOfIteration());
//			System.out.println("convergence reached = " + hitScore.isReceptorFixedLigandOptimizedConvergenceReached());
//			System.out.println("rmsd before/after opt. = " + hitScore.getReceptorFixedLigandOptimizedRmsdBeforeAndAfterOptimization());
//			System.out.println("count longer than 2A change = " + hitScore.getCountOfLongDistanceChange());
//		}
//		return hitScore;
//	}



	public static Map<Integer, PointWithPropertiesIfc> returnCloneRotatedMiniShape(Map<Integer, PointWithPropertiesIfc> miniShape,ResultsFromEvaluateCost result){

		Map<Integer, PointWithPropertiesIfc> newMiniShape = new HashMap<>();
		for (Entry<Integer, PointWithPropertiesIfc> entry: miniShape.entrySet()){
			PointWithPropertiesIfc newPoint = rotateAndClonePointWithProperties(entry.getValue(), result);
			newMiniShape.put(entry.getKey(), newPoint);
		}
		return newMiniShape;
	}



	public static CollectionOfPointsWithPropertiesIfc returnCloneRotatedShape(CollectionOfPointsWithPropertiesIfc shape, ResultsFromEvaluateCost result){

		List<PointWithPropertiesIfc> listNewpoints = new ArrayList<>();
		for (int i=0; i<shape.getSize(); i++){
			PointWithPropertiesIfc point = shape.getPointFromId(i);
			PointWithPropertiesIfc newPoint = rotateAndClonePointWithProperties(point, result);
			listNewpoints.add(newPoint);
		}

		CollectionOfPointsWithPropertiesIfc newShape = new CollectionOfPointsWithProperties(listNewpoints);
		return newShape;
	}



	private static PointWithPropertiesIfc rotateAndClonePointWithProperties(PointWithPropertiesIfc inputPoint, ResultsFromEvaluateCost result){

		RealVector coordsVector = new ArrayRealVector(ToolsMath.convertToDoubleArray(inputPoint.getCoords().getCoords()));
		RealVector newPointCoords = PairingTools.alignPointFromShape2toShape1(result, coordsVector);
		PointWithPropertiesIfc newPoint = new PointWithProperties();
		newPoint.setCoords(new Point(ToolsMath.convertToFloatArray(newPointCoords.toArray())));
		newPoint.setStrikingProperties(inputPoint.getStrikingProperties());
		return newPoint;
	}



	public static MyChainIfc returnCloneRotatedPeptide(MyChainIfc inputchain, ResultsFromEvaluateCost result, AlgoParameters algoParameters){


		MyStructureIfc myStructure = new MyStructure(inputchain,algoParameters);
		MyStructureIfc myStructureRotated = null;
		try {
			myStructureRotated = myStructure.cloneWithSameObjectsRotatedCoords(result);
		} catch (ExceptionInMyStructurePackage e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return myStructureRotated.getAminoChain(0);
		}
		return null;
	}
}
