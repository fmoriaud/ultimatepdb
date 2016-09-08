package multithread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

import math.ToolsMath;
import parameters.AlgoParameters;
import pointWithProperties.LennardJonesTools;
import pointWithProperties.LennardJonesTools.LennarJonesGromacs;
import pointWithProperties.PointIfc;
import pointWithProperties.PointsWithLennardJones;
import mystructure.MyAtomIfc;
import mystructure.MyChainIfc;
import mystructure.MyMonomerIfc;
import mystructure.MyStructureIfc;

public class ComputeLennardJonesMultiThread extends RecursiveTask<List<PointIfc>>{

	private static final long serialVersionUID = 1L;


	private final List<float[]> listPositions;
	private int start;
	private int end;
	private int threshold;

	private final MyStructureIfc myStructure;
	private final AlgoParameters algoParameters;


	public ComputeLennardJonesMultiThread(List<float[]> listPositions, int start, int end, int threshold, MyStructureIfc myStructure, AlgoParameters algoParameters){
		this.listPositions = listPositions;
		this.start = start;
		this.end = end;
		this.threshold = threshold;
		this.myStructure = myStructure;
		this.algoParameters = algoParameters;
	}



	@Override
	protected List<PointIfc> compute() {

		List<PointIfc> listOfPointsWithLennardJones = new ArrayList<>();

		if (end - start < threshold){

			for (int i = start; i <= end; i++){

				float[] atPosition = listPositions.get(i);

				Float lennardJones = computeLennardJones(myStructure, atPosition, algoParameters);

				if (lennardJones != null){
					//System.out.println("point LJ added");
					listOfPointsWithLennardJones.add(new PointsWithLennardJones(atPosition, lennardJones));
				}

			}

		} else{
			int midway = (end - start) / 2 + start;
			ComputeLennardJonesMultiThread findMaxTask1 = new ComputeLennardJonesMultiThread(listPositions, start, midway, threshold, myStructure, algoParameters);
			findMaxTask1.fork();

			ComputeLennardJonesMultiThread findMaxTask2 = new ComputeLennardJonesMultiThread(listPositions, midway + 1, end, threshold, myStructure, algoParameters);
			listOfPointsWithLennardJones.addAll(findMaxTask2.compute());
			listOfPointsWithLennardJones.addAll(findMaxTask1.join());
			
		}
		
		return listOfPointsWithLennardJones;
	}

	

	private Float computeLennardJones(MyStructureIfc structure, float[] atPosition, AlgoParameters algoParameters){

		float lennardJonesToReturn = 0.0f ;
		for(MyChainIfc chain: myStructure.getAllChainsRelevantForShapeBuilding()){
			for (MyMonomerIfc monomer : chain.getMyMonomers()){
				for (MyAtomIfc atom: monomer.getMyAtoms()){
					if (!String.valueOf(atom.getElement()).equals("H")){
						lennardJonesToReturn += computeLennardJones(atom, monomer, atPosition);
					}
				}
			}
		}

		//if ((lennardJonesToReturn < -0.0032) && (lennardJonesToReturn > -0.0038)){
			if ((lennardJonesToReturn < algoParameters.getLENNARD_JONES_CUTOFF_MAX()) && (lennardJonesToReturn > algoParameters.getLENNARD_JONES_CUTOFF_MIN())){
			//System.out.println("on grid : " + lennardJonesToReturn + "  " + atPosition[0] + "  " + atPosition[1] + "  " + atPosition[2]);
			return lennardJonesToReturn;
		}
		return null;
	}



	private double computeLennardJones(MyAtomIfc atom, MyMonomerIfc monomer, float[] atPosition){

		double r = ToolsMath.computeDistance( atom.getCoords(), atPosition);

		LennarJonesGromacs lennarJonesGromacs = LennardJonesTools.getLennardJones(String.valueOf(atom.getAtomName()), String.valueOf(monomer.getThreeLetterCode()));

		if (lennarJonesGromacs == null){
			System.out.println("unparametrized atom from PDB " + String.valueOf(atom.getAtomName()) + "  " + String.valueOf(monomer.getThreeLetterCode()));
			return 0.0;
		}

		double lennardJonesA = lennarJonesGromacs.getA();
		double lennardJonesC = lennarJonesGromacs.getC();

		double valueToReturn = lennardJonesA / Math.pow(r, 12.0) - lennardJonesC / Math.pow(r, 6.0);

		return  valueToReturn;
	}
}
