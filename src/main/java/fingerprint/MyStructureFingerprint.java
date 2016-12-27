/*
Author:
      Fabrice Moriaud <fmoriaud@ultimatepdb.org>

  Copyright (c) 2016 Fabrice Moriaud

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
  */
package fingerprint;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.math3.geometry.euclidean.threed.SphericalCoordinates;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import database.SequenceTools;
import math.EquidistributionPhi;
import math.ToolsMath;
import math.ToolsMathAppliedToObjects;
import parameters.AlgoParameters;
import pointWithProperties.Point;
import pointWithProperties.PointIfc;
import pointWithProperties.PointsTools;
import shapeBuilder.ShapeBuildingException;
import shapeBuilder.StructureLocalToBuildAnyShape;
import shapeReduction.PhiThetaInterval;
import shapeReduction.ShapeReductorTools;
import mystructure.MyChainIfc;
import mystructure.MyMonomerIfc;
import mystructure.MyStructureIfc;

public class MyStructureFingerprint {
	// -------------------------------------------------------------------
	// Class variables
	// -------------------------------------------------------------------
	private MyStructureIfc myStructure;
	private AlgoParameters algoParameters;

	private List<Double> splittedPercentageOccupied;
	private List<List<Integer>> splittedHistogramD2OccupiedSolidAngle;



	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public MyStructureFingerprint(MyStructureIfc myStructure, AlgoParameters algoParameters){
		this.myStructure = myStructure;
		this.algoParameters = algoParameters;
	}




	//-------------------------------------------------------------
	// Interface & Public methods
	//-------------------------------------------------------------
	public List<Integer> computeHistogramAminoAcidsTypes(){

		List<List<String>> equivalentResidues = SequenceTools.generateEquivalentResidues();

		List<Integer> histogramAminoAcidsTypes = new ArrayList<>();
		for (int i=0; i<equivalentResidues.size(); i++){
			histogramAminoAcidsTypes.add(0);
		}
		for (MyChainIfc chain: myStructure.getAllChainsRelevantForShapeBuilding()){
			for (MyMonomerIfc monomer: chain.getMyMonomers()){
				String threeLetterCode = String.valueOf(monomer.getThreeLetterCode());
				List<Integer> bins = returnBins(equivalentResidues, threeLetterCode);
				for (Integer binValue: bins){
					int currentVaue = histogramAminoAcidsTypes.get(binValue);
					histogramAminoAcidsTypes.set(binValue, currentVaue + 1);
				}
			}
		}

		return histogramAminoAcidsTypes;
	}



	public List<Integer> computeHistogramSimple(){

		List<Integer> histogramSimple = new ArrayList<>();
		// only total number and hydrophobic count
		// aim is only to identify most similar in terms of cavity opening and its hydrophobicity
		int residueCount = 0;
		int hydrophobicResidueCount = 0;
		List<String> hydrophobicResidues = SequenceTools.getHydrophobicResiduesList();
		for (MyChainIfc chain: myStructure.getAllChainsRelevantForShapeBuilding()){
			for (MyMonomerIfc monomer: chain.getMyMonomers()){
				String threeLetterCode = String.valueOf(monomer.getThreeLetterCode());
				residueCount +=1;
				if (hydrophobicResidues.contains(threeLetterCode)){
					hydrophobicResidueCount += 1;
				}
			}
		}
		histogramSimple.add(residueCount);
		histogramSimple.add(hydrophobicResidueCount);
		return histogramSimple;
	}



	public boolean isItWorthComparing(List<Integer> histogramSimpleQuery, List<Integer> histogramSimpleTarget){

		if (histogramSimpleTarget.get(0) > histogramSimpleQuery.get(0)  * 1.2){
			// if target has 20% or more amino acids in neighbors then it is unlikely to be a good hit
			//System.out.println("skipped total " + histogramSimpleTarget.get(0) + " > " + histogramSimpleQuery.get(0) * 1.2);
			return false;
		}

		double ratioHydrophobicTarget = (double) histogramSimpleTarget.get(1) / (double) histogramSimpleTarget.get(0);
		double ratioHydrophobicQuery = (double) histogramSimpleQuery.get(1) / (double) histogramSimpleQuery.get(0);

		// if target is off of more than 20% on hydrophobicity then it is skipped
		if (Math.abs(ratioHydrophobicTarget - ratioHydrophobicQuery) > 0.2){ // it is tough but I want very similar hits only
			//System.out.println("skipped hydro " + ratioHydrophobicTarget + " != " + ratioHydrophobicQuery);

			return false;
		}
		return true;
	}



	public void computePercentageAndHistogram(MyChainIfc peptide, int splittedSequenceLength){

		splittedPercentageOccupied = new ArrayList<>();
		splittedHistogramD2OccupiedSolidAngle = new ArrayList<>();

		// i want to compute the distribution according to peptide
		// but it is a list now for each and every segment of the used length in the DockingPeptide algo

		// it can be for the query which might be longer than splittedsequeceLength

		// it can be for the splitted peptide and then it is gonna be the same length

		System.out.println("solid angle peptide length = " + peptide.getMyMonomers().length);

		// if it is of the same size then I do the job
		// get the barycenter of the peptide

		if (peptide.getMyMonomers().length == splittedSequenceLength){
			List<PointIfc> pointsPeptide = PointsTools.createListOfPointIfcFromPeptide(peptide);
			PointIfc barycenter = ShapeReductorTools.computeLigandBarycenter(pointsPeptide);

			LinkedHashMap<PhiThetaInterval, List<PointIfc>> groupedResidues = groupResiduesAccordingToSolidAngleAccordingToLocalStructureBarycenter(barycenter, myStructure);
			int countOccupied = 0;
			for (Entry<PhiThetaInterval, List<PointIfc>> entry: groupedResidues.entrySet()){
				if (entry.getValue().size() > 0){
					countOccupied += 1;
				}
			}
			double percentageOccupied = (double) countOccupied / groupedResidues.size();
			List<Integer> histogramD2OccupiedSolidAngle = computeHistogramD2(groupedResidues);
			splittedPercentageOccupied.add(percentageOccupied);
			splittedHistogramD2OccupiedSolidAngle.add(histogramD2OccupiedSolidAngle);
		}else{

			// remember that the docking peptide class which is using this is docking the peptide chain bound to it already in the structure
			for (int i=0; i<peptide.getMyMonomers().length; i++){

				int startId = i;
				int endId = i + splittedSequenceLength;
				if (endId < peptide.getMyMonomers().length + 1){
					
					
					// I need to schrink myStructure to the env of this subpeptide
					StructureLocalToBuildAnyShape structureLocalToBuildAnyShape = null;
					try {
						structureLocalToBuildAnyShape = new StructureLocalToBuildAnyShape(myStructure, peptide.getChainId(), startId, splittedSequenceLength, algoParameters);
					} catch (ShapeBuildingException e) {
						continue;
					}
					MyStructureIfc myStructureLocal = structureLocalToBuildAnyShape.getMyStructureLocal();
					if (myStructureLocal == null) {
						continue;
					}

					// makes the subPeptide barycenter
					MyChainIfc subPeptide = peptide.makeSubchain(startId, splittedSequenceLength);
					List<PointIfc> pointsPeptide = PointsTools.createListOfPointIfcFromPeptide(subPeptide);
					PointIfc barycenter = ShapeReductorTools.computeLigandBarycenter(pointsPeptide);

					LinkedHashMap<PhiThetaInterval, List<PointIfc>> groupedResidues = groupResiduesAccordingToSolidAngleAccordingToLocalStructureBarycenter(barycenter, myStructureLocal);
					int countOccupied = 0;
					for (Entry<PhiThetaInterval, List<PointIfc>> entry: groupedResidues.entrySet()){
						if (entry.getValue().size() > 0){
							countOccupied += 1;
						}
					}
					double percentageOccupied = (double) countOccupied / groupedResidues.size();
					List<Integer> histogramD2OccupiedSolidAngle = computeHistogramD2(groupedResidues);
					splittedPercentageOccupied.add(percentageOccupied);
					splittedHistogramD2OccupiedSolidAngle.add(histogramD2OccupiedSolidAngle);
				}
			}
		}
	}



	//	public void computePercentageAndHistogram(){
	//
	//		LinkedHashMap<PhiThetaInterval, List<PointIfc>> groupedResidues = groupResiduesAccordingToSolidAngleAccordingToLocalStructureBarycenter();
	//
	//		int countOccupied = 0;
	//		for (Entry<PhiThetaInterval, List<PointIfc>> entry: groupedResidues.entrySet()){
	//			if (entry.getValue().size() > 0){
	//				countOccupied += 1;
	//			}
	//		}
	//		percentageOccupied = (double) countOccupied / groupedResidues.size();
	//		histogramD2OccupiedSolidAngle = computeHistogramD2(groupedResidues);
	//	}



	private List<Integer> computeHistogramD2(LinkedHashMap<PhiThetaInterval, List<PointIfc>> groupedResidues){

		List<Integer> histogramD2 = new ArrayList<>();

		List<PointIfc> onePointPerGroup = new ArrayList<>();
		for (Entry<PhiThetaInterval, List<PointIfc>> entry: groupedResidues.entrySet()){
			if (entry.getValue().size() > 0){
				onePointPerGroup.add(entry.getValue().get(0));
			}
		}

		List<Float> computelListDistanceBetweenTwoLists = SimilarityTools.computelListDistanceBetweenTwoLists(onePointPerGroup, onePointPerGroup, 1.0);
		int sizeList = 10;
		float distanceStep = (float) 20.0f / sizeList;
		float startAt = (float) 2.0;
		List<Integer> distributionDistance = SimilarityTools.binValues(computelListDistanceBetweenTwoLists, sizeList, distanceStep, startAt);
		histogramD2.addAll(distributionDistance);

		return histogramD2;
	}




	// -------------------------------------------------------------------
	// Implementation
	// -------------------------------------------------------------------
	private LinkedHashMap<PhiThetaInterval, List<PointIfc>> groupResiduesAccordingToSolidAngleAccordingToLocalStructureBarycenter(PointIfc barycenterShape, MyStructureIfc myStructureHere){ // strongly reusing code of groupPoints

		List<PointIfc> listRepresentativePoints = new ArrayList<>();

		for (MyChainIfc chain: myStructureHere.getAllChainsRelevantForShapeBuilding()){
			for (MyMonomerIfc monomer: chain.getMyMonomers()){
				float[] coords = ToolsMathAppliedToObjects.getCoordinatesOfRepresentativeAtom(monomer);
				PointIfc point = new Point(coords);
				listRepresentativePoints.add(point);
			}
		}

		// defining 36 zones in solid space
		double deltaOnlyForTheta =  Math.PI / 8.0;
		int countOfIncrementAngle = 8;
		// group monomers

		EquidistributionPhi equidistributionPhi = new EquidistributionPhi();
		List<Double> phiValues = equidistributionPhi.getMapCountOfIntervalsAndPointValues().get(countOfIncrementAngle);
		// theta in map ranges from -pi to +pi in agreement with apache spherical coodinates
		List<Double> tethaValues = ShapeReductorTools.doBinningThetaValues(deltaOnlyForTheta, countOfIncrementAngle);


		List<PhiThetaInterval> sectors = generateSector(deltaOnlyForTheta, phiValues, tethaValues);

		// create the Map to return
		LinkedHashMap<PhiThetaInterval, List<PointIfc>> groupPoints = new LinkedHashMap<>();

		Iterator<PhiThetaInterval> it = sectors.iterator();
		while (it.hasNext()){
			PhiThetaInterval sector = it.next();
			List<PointIfc> listPoints = new ArrayList<>();
			groupPoints.put(sector, listPoints);
		}

		for (PointIfc point: listRepresentativePoints){

			float[] pointRelativeToBarycenter = ToolsMath.v1minusV2(point.getCoords(), barycenterShape.getCoords());			
			Vector3D pointRelativeToBarycenterV3d = new Vector3D(pointRelativeToBarycenter[0], pointRelativeToBarycenter[1], pointRelativeToBarycenter[2]);

			SphericalCoordinates pointShericalRelative = new SphericalCoordinates(pointRelativeToBarycenterV3d);

			PhiThetaInterval intervalForThisPoint = getIntervalFromSphericalCoordinates(pointShericalRelative, sectors);

			if (intervalForThisPoint == null){ // it could be that some points doesnt fit so I should make the binning a bit larger I guess
				continue;
			}
			groupPoints.get(intervalForThisPoint).add(point);
		}

		return groupPoints;
	}



	private PhiThetaInterval getIntervalFromSphericalCoordinates(SphericalCoordinates pointShericalRelative, List<PhiThetaInterval> sectors) {

		double phi = pointShericalRelative.getPhi();
		double theta = pointShericalRelative.getTheta();

		for (PhiThetaInterval phiThetaInterval:  sectors){
			double phiMin = phiThetaInterval.getPhiMin();
			double phiMax = phiThetaInterval.getPhiMax();
			double thetaMin = phiThetaInterval.getThetaMin();
			double thetaMax = phiThetaInterval.getThetaMax();

			if ( phi < phiMin || phi > phiMax){
				continue;
			}
			if ( theta < thetaMin || theta > thetaMax){
				continue;
			}

			return phiThetaInterval;

		}
		// sector was not found
		return null;
	}



	private List<PhiThetaInterval> generateSector(double deltaOnlyForTheta, List<Double> phiValues, List<Double> tethaValues) {

		List<PhiThetaInterval> listPhiThetaInterval = new ArrayList<>();

		for (int i=0; i<phiValues.size() - 1; i++ ){  // phi values are from 0 to PI by construction so I dont take PI
			double minPhi = phiValues.get(i);
			double maxPhi = phiValues.get(i+1);

			for (Double tetha: tethaValues){

				double minTheta = tetha;
				double maxTheta = tetha + deltaOnlyForTheta;
				PhiThetaInterval phiThetainterval = new PhiThetaInterval(minPhi, maxPhi, minTheta, maxTheta);
				listPhiThetaInterval.add(phiThetainterval);
			}
		}
		return listPhiThetaInterval;
	}



	private List<Integer> returnBins(List<List<String>> equivalentResidues, String threeLetterCode){

		List<Integer> bins = new ArrayList<>();

		for (int i=0; i<equivalentResidues.size(); i++){
			if (equivalentResidues.get(i).contains(threeLetterCode)){
				bins.add(i);
			}
		}
		return bins;
	}



	// -------------------------------------------------------------------
	// Getter & Setter
	// -------------------------------------------------------------------
	public List<Double> getSplittedPercentageOccupied() {
		return splittedPercentageOccupied;
	}

	public List<List<Integer>> getSplittedHistogramD2OccupiedSolidAngle() {
		return splittedHistogramD2OccupiedSolidAngle;
	}
}
