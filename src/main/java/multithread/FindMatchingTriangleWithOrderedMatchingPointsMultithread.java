package multithread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveTask;

import parameters.AlgoParameters;
import pointWithProperties.PointWithPropertiesIfc;
import pointWithProperties.StrikingPropertiesTools;
import shape.ShapeContainerIfc;
import shapeCompare.PairingAndNullSpaces;
import shapeReduction.TriangleInteger;

public class FindMatchingTriangleWithOrderedMatchingPointsMultithread extends RecursiveTask<List<PairingAndNullSpaces>>{
	//------------------------
	// Class variables
	//------------------------
	private static final long serialVersionUID = 1L;
	private List<TriangleInteger> listTriangleShape1;
	private List<TriangleInteger> listTriangleShape2;
	private AlgoParameters algoParameters;
	private ShapeContainerIfc shapeContainer1;
	private ShapeContainerIfc shapeContainer2;

	private int start;
	private int end;
	private int threshold;




	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public FindMatchingTriangleWithOrderedMatchingPointsMultithread(int start, int end, int threshold, List<TriangleInteger> listTriangleShape1, List<TriangleInteger> listTriangleShape2, ShapeContainerIfc shapeContainer1, ShapeContainerIfc shapeContainer2, AlgoParameters algoParameters){

		this.start = start;
		this.end = end;
		this.threshold = threshold;
		this.algoParameters = algoParameters;
		this.shapeContainer1 = shapeContainer1;
		this.shapeContainer2 = shapeContainer2;
		this.listTriangleShape1 = listTriangleShape1;
		this.listTriangleShape2 = listTriangleShape2;
	}



	@Override
	protected List<PairingAndNullSpaces> compute() {

		List<PairingAndNullSpaces> listPairingFromMatchingTriangle = new ArrayList<>();

		if (end - start < threshold){
			//System.out.println("do " + (end - start));
			for (int i = start; i <= end; i++){

				TriangleInteger triangle1 = listTriangleShape1.get(i);

				A: for (int j = 0; j < listTriangleShape2.size(); j++){

					TriangleInteger triangle2 = listTriangleShape2.get(j);

					boolean[][] arrayMatching = getMatchingVertices(triangle1, triangle2);

					// if one of the vertices matches no other vertices then continue because that is needed
					for (int k=0; k<3; k++){
						boolean[] matching = arrayMatching[k];
						boolean foundAMatchInThisLine = false;
						for (int l=0; l<3; l++){
							if (matching[l] == true){
								foundAMatchInThisLine = true;
							}
						}
						if (foundAMatchInThisLine == false){
							continue A;
						}
					}

					List<int[]> listMatchingWithIds = buildAllPossibleMatchingWith3points(arrayMatching, triangle1, triangle2);

					if (listMatchingWithIds.size() == 0){
						continue A;
					}

					for (int[] listIds: listMatchingWithIds){

						PairingAndNullSpaces pairingAndNullSpaces = generatePairingAndNullSpaceFromTwoMatchingTriangle(triangle1, triangle2, listIds);
						if (listPairingFromMatchingTriangle.size() < 100000) {
							listPairingFromMatchingTriangle.add(pairingAndNullSpaces);
						}
					}
				}
			}
		} else{
			//System.out.println("fork " + (end - start));
			int midway = (end - start) / 2 + start;
			FindMatchingTriangleWithOrderedMatchingPointsMultithread findMatchinTriangleTask1 = new FindMatchingTriangleWithOrderedMatchingPointsMultithread(start, midway, threshold, listTriangleShape1, listTriangleShape2, shapeContainer1, shapeContainer2, algoParameters);
			findMatchinTriangleTask1.fork();

			FindMatchingTriangleWithOrderedMatchingPointsMultithread findMatchinTriangleTask2 = new FindMatchingTriangleWithOrderedMatchingPointsMultithread( midway + 1, end, threshold, listTriangleShape1, listTriangleShape2, shapeContainer1, shapeContainer2, algoParameters);
			listPairingFromMatchingTriangle.addAll(findMatchinTriangleTask2.compute());
			listPairingFromMatchingTriangle.addAll(findMatchinTriangleTask1.join());
		}

		return listPairingFromMatchingTriangle;
	}



	private boolean[][] getMatchingVertices(TriangleInteger triangle1, TriangleInteger triangle2){

		int[] array1TriangleIdAndPointId = triangle1.getArrayTriangleIdAndPointId();
		int[] array2TriangleIdAndPointId = triangle2.getArrayTriangleIdAndPointId();

		boolean[][] arrayOfMatchingPoint = new boolean[3][3];

		for (int i=0; i<3; i++){

			int currentTriangle1 = array1TriangleIdAndPointId[i];

			for (int j=0; j<3; j++){
				int currentTriangle2 = array2TriangleIdAndPointId[j];

				PointWithPropertiesIfc point1withProperties = shapeContainer1.getMiniShape().get(currentTriangle1);
				PointWithPropertiesIfc point2withProperties = shapeContainer2.getMiniShape().get(currentTriangle2);
				boolean arePointsMatching = StrikingPropertiesTools.evaluatePointsMatchingWithAtLeastOneProperty(point1withProperties, point2withProperties);

				if (arePointsMatching == true){
					arrayOfMatchingPoint[i][j] = true;
				}
			}
		}

		return arrayOfMatchingPoint;
	}



	private List<int[]> buildAllPossibleMatchingWith3points(boolean[][] arrayMatching, TriangleInteger triangle1, TriangleInteger triangle2) {

		List<int[]> listMatchingWithIds = new ArrayList<>();

		for (int i=0; i<3; i++){
			for (int j=0; j<3; j++){
				for (int k=0; k<3; k++){
					if ((arrayMatching[0][i] == true) && (arrayMatching[1][j] == true) && (arrayMatching[0][k] == true)){
						int[] listIds = new int[3];
						listIds[0] = i;
						listIds[1] = j;
						listIds[2] = k;

						if (isValid(listIds) && hasValidAngleAndEdgeLength(listIds, triangle1, triangle2)){
							listMatchingWithIds.add(listIds);
						}
					}
				}
			}
		}

		return listMatchingWithIds;
	}



	private boolean hasValidAngleAndEdgeLength(int[] listIds, TriangleInteger triangle1, TriangleInteger triangle2){

		double[] arrayTriangle1Angles = triangle1.getArrayTriangleIdAndCorrespondingAngles();
		double[] arrayTriangle2Angles = triangle2.getArrayTriangleIdAndCorrespondingAngles();

		double[] arrayTriangle1Edge =  triangle1.getArrayTriangleIdAndOppositeEdgeLength();
		double[] arrayTriangle2Edge =  triangle2.getArrayTriangleIdAndOppositeEdgeLength();

		for (int k=0; k<3 ; k++){
			double angle1 = arrayTriangle1Angles[k];
			Integer correspondingId = listIds[k];

			double angle2 = arrayTriangle2Angles[correspondingId];
			//System.out.println(angle1 + "  " + angle2);
			if ( Math.abs(angle1 - angle2) > algoParameters.getANGLE_DIFF_TOLERANCE()){
				return false;
			}
		}

		for (int k=0; k<3 ; k++){
			double edge1 = arrayTriangle1Edge[k];
			Integer correspondingId = listIds[k];

			double edge2 = arrayTriangle2Edge[correspondingId];
			//System.out.println(edge1 + "  " + edge2);
			if ( Math.abs(edge1 - edge2) > algoParameters.getEDGE_DIFF_TOLERANCE()){
				return false;
			}
		}
		return true;
	}



	private boolean isValid(int[] listIds){

		boolean zeroFound = false;
		boolean oneFound = false;
		boolean twoFound = false;

		for (int i=0; i<3; i++){
			if (listIds[i] == 0){
				zeroFound = true;
			}
			if (listIds[i] == 1){
				oneFound = true;
			}
			if (listIds[i] == 2){
				twoFound = true;
			}
		}
		if (zeroFound == true && oneFound == true && twoFound == true){
			return true;
		}
		return false;
	}



	private PairingAndNullSpaces generatePairingAndNullSpaceFromTwoMatchingTriangle(TriangleInteger triangle1, TriangleInteger triangle2, int[] listIds) {
		Map<Integer,Integer> pairing = new HashMap<>();

		List<Integer> nullSpaceOfMap1 = new ArrayList<>();
		nullSpaceOfMap1.addAll(shapeContainer1.getMiniShape().keySet());
		List<Integer> nullSpaceOfMap2 = new ArrayList<>();
		nullSpaceOfMap2.addAll(shapeContainer2.getMiniShape().keySet());

		Integer pointIdFromTriangle1vertice1 = triangle1.getArrayTriangleIdAndPointId()[0];
		Integer correspondingId1 = triangle2.getArrayTriangleIdAndPointId()[listIds[0]];
		pairing.put(pointIdFromTriangle1vertice1.intValue(), correspondingId1.intValue());

		Integer pointIdFromTriangle1vertice2 = triangle1.getArrayTriangleIdAndPointId()[1];
		Integer correspondingId2 = triangle2.getArrayTriangleIdAndPointId()[listIds[1]];
		pairing.put(pointIdFromTriangle1vertice2.intValue(), correspondingId2.intValue());

		Integer pointIdFromTriangle1vertice3 = triangle1.getArrayTriangleIdAndPointId()[2];
		Integer correspondingId3 = triangle2.getArrayTriangleIdAndPointId()[listIds[2]];
		pairing.put(pointIdFromTriangle1vertice3.intValue(), correspondingId3.intValue());

		nullSpaceOfMap1.remove(pointIdFromTriangle1vertice1);
		nullSpaceOfMap1.remove(pointIdFromTriangle1vertice2);
		nullSpaceOfMap1.remove(pointIdFromTriangle1vertice3);

		nullSpaceOfMap2.remove(correspondingId1);
		nullSpaceOfMap2.remove(correspondingId2);
		nullSpaceOfMap2.remove(correspondingId3);

		PairingAndNullSpaces pairingAndNullSpaces = new PairingAndNullSpaces(pairing, nullSpaceOfMap1, nullSpaceOfMap2);
		return pairingAndNullSpaces;
	}
}
