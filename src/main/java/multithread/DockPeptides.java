package multithread;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

import database.FinSequenceInDatabaseTools;
import database.HitInSequenceDb;
import fingerprint.CannotCompareDistributionException;
import fingerprint.DistributionComparisonTools;
import fingerprint.MyStructureFingerprint;
import parameters.AlgoParameters;
import parameters.TargetDefinedBySegmentOfChainBasedOnSequenceMotif;
import parameters.TargetsIfc;
import protocols.ShapeContainerFactory;
import shape.HasPeptideIfc;
import shape.ShapeContainerIfc;
import shapeBuilder.*;
import mystructure.EnumMyReaderBiojava;
import mystructure.MyChainIfc;
import mystructure.MyStructureIfc;

public class DockPeptides {
	//------------------------
	// Class variables
	//------------------------
	private TargetsIfc targetDefiner;
	private AlgoParameters algoParameters;
	private  EnumMyReaderBiojava enumMyReaderBiojava;
	private ShapeContainerIfc queryShape;
	private ExecutorService executorService;


	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public DockPeptides(TargetsIfc targetDefiner, AlgoParameters algoParameters, EnumMyReaderBiojava enumMyReaderBiojava, ShapeContainerIfc queryShape, 
			ExecutorService executorService){
		this.targetDefiner = targetDefiner;
		this.algoParameters = algoParameters;
		this.queryShape = queryShape;
		this.executorService = executorService;
		this.enumMyReaderBiojava = enumMyReaderBiojava;
	}




	// -------------------------------------------------------------------
	// Public methods
	// -------------------------------------------------------------------
	public void dock() {


		// that is a protocole that works with a query define by a a peptide towards targets which are peptide environement

		MyChainIfc peptideQuery = null;

		if (queryShape instanceof HasPeptideIfc){
			HasPeptideIfc queryShapeWithPeptide = (HasPeptideIfc) queryShape;
			peptideQuery = queryShapeWithPeptide.getPeptide();
		}else{
			return;
		}


		int timeSecondsToWaitIfQueueIsFullBeforeAddingMore = 60;

		if (!(targetDefiner instanceof TargetDefinedBySegmentOfChainBasedOnSequenceMotif)){
			return;
		}

		// load the structure global because I need to compute segemented query local environement for
		// usage of the StructureFingerprint

		MyStructureIfc myStructureGlobalBrutQuery;
		try {
			myStructureGlobalBrutQuery = ShapeBuildingTools.getMyStructure(algoParameters.getQUERY_PDB_FOUR_LETTER_CODE().toCharArray(), algoParameters, enumMyReaderBiojava);
		} catch (ShapeBuildingException e2) {
			System.out.println("Query PDB file failed to read ... program terminated ");
			return;
		}

		TargetDefinedBySegmentOfChainBasedOnSequenceMotif targetDefinedBySegmentOfChainBasedOnSequenceMotif = (TargetDefinedBySegmentOfChainBasedOnSequenceMotif) targetDefiner;
		String sequenceToFind = targetDefinedBySegmentOfChainBasedOnSequenceMotif.getSequence();


		int splittedSequenceLength = algoParameters.getDOCKING_PEPTIDES_SPLITTING_SEQUENCE_LENGTH(); // sequence is docked by 5 subpart and I exclude overlap
		Map<Integer, List<HitInSequenceDb>> rankedHitsInDatabase = new LinkedHashMap<>();

		// it gives 
		List<String> subSequences = makeSubsequenceOfGivenLengthWithSomeOverlapingIfNeeded(sequenceToFind, splittedSequenceLength);


		System.out.println(subSequences);

		int minLength = targetDefinedBySegmentOfChainBasedOnSequenceMotif.getMinLength();
		int maxLength = targetDefinedBySegmentOfChainBasedOnSequenceMotif.getMaxLength();
		boolean useSimilarSequences = targetDefinedBySegmentOfChainBasedOnSequenceMotif.isUseSimilarSequences();


		int rank=0;
		for (String subSequence: subSequences){
			List<HitInSequenceDb> hitsInDatabase = FinSequenceInDatabaseTools.find(minLength, maxLength, subSequence, useSimilarSequences);
			rankedHitsInDatabase.put(rank, hitsInDatabase);
			rank += 1;
		}

		String fourLetterCode;
		String chainIdFromDB;
		// Then I loop on them and put them in the executor


		MyStructureFingerprint myStructureFingerprintQuery = new MyStructureFingerprint(myStructureGlobalBrutQuery, algoParameters);
		myStructureFingerprintQuery.computePercentageAndHistogram(peptideQuery, splittedSequenceLength);
		List<List<Integer>> listHistogramD2OccupiedSolidAngleQuery = myStructureFingerprintQuery.getSplittedHistogramD2OccupiedSolidAngle();
		List<Double> listPercentageOccupiedQuery = myStructureFingerprintQuery.getSplittedPercentageOccupied();

		for (Entry<Integer, List<HitInSequenceDb>> hitsInMap: rankedHitsInDatabase.entrySet()){

			List<HitInSequenceDb> hitsInSequenceDb = hitsInMap.getValue();

			for (HitInSequenceDb hitInSequenceDb: hitsInSequenceDb){


				fourLetterCode = hitInSequenceDb.getFourLetterCode();
				chainIdFromDB = hitInSequenceDb.getChainIdFromDB();
				List<Integer> listRankIdsFromId0 = hitInSequenceDb.getListRankIds();

				//				System.out.println(fourLetterCode);
				//				if (! fourLetterCode.equals("1NLN")){
				//					continue;
				//				}

				MyStructureIfc myStructureGlobalBrutTarget;
				try {
					myStructureGlobalBrutTarget = ShapeBuildingTools.getMyStructure(fourLetterCode.toCharArray(), algoParameters, enumMyReaderBiojava);
				} catch (ShapeBuildingException e2) {
					//System.out.println();
					continue;
				}

				int peptideLength = splittedSequenceLength;

				for (int i=0; i<listRankIdsFromId0.size(); i++){

					Integer matchingRankId = listRankIdsFromId0.get(i);

					StructureLocalToBuildShapeSegmentOfShape structureLocalToBuildShapeSegmentOfShape = 
							new StructureLocalToBuildShapeSegmentOfShape(myStructureGlobalBrutTarget,
									chainIdFromDB.toCharArray(), matchingRankId, peptideLength, algoParameters);
					try {
						structureLocalToBuildShapeSegmentOfShape.compute();
					} catch (ShapeBuildingException e2) {
						continue;
					}
					MyStructureIfc myStructureLocal = structureLocalToBuildShapeSegmentOfShape.getMyStructureLocal();
					MyChainIfc peptide = structureLocalToBuildShapeSegmentOfShape.getLigand();
					// TODO use it to avoid redoing this but it is a lot so first need to check if usefull

					MyStructureFingerprint myStructureFingerprintTarget = new MyStructureFingerprint(myStructureLocal, algoParameters);
					myStructureFingerprintTarget.computePercentageAndHistogram(peptide, splittedSequenceLength);
					List<List<Integer>> listHistogramD2OccupiedSolidAngleTarget = myStructureFingerprintTarget.getSplittedHistogramD2OccupiedSolidAngle();
					List<Double> listPercentageOccupiedTarget = myStructureFingerprintTarget.getSplittedPercentageOccupied();

					//System.out.println("Target: " + percentageOccupiedTarget);
					//System.out.println(histogramD2OccupiedSolidAngleTarget);

					double thresholdSolidAngleOccupancy = 0.1; // should be enough for 1NLN B to get all hits
					// taget has only one
					double percentageOccupiedTarget = listPercentageOccupiedTarget.get(0);
					List<Integer> HistogramD2OccupiedSolidAngleTarget = listHistogramD2OccupiedSolidAngleTarget.get(0);

					// I need the match with only one of the query as it could be a hit
					// Not perfect is that then the comparison will be done on the wole query shape
					// but i dont see what I would do
					int size = listHistogramD2OccupiedSolidAngleQuery.size();

					boolean foundGoodOne = false;
					for (int j=0; j<size; j++){

						double percentageOccupiedQuery = listPercentageOccupiedQuery.get(j);
						List<Integer> histogramD2OccupiedSolidAngleQuery = listHistogramD2OccupiedSolidAngleQuery.get(j);

						if (Math.abs(percentageOccupiedQuery - percentageOccupiedTarget) > thresholdSolidAngleOccupancy){
							//System.out.println("skipped percentage solid state occupancy ");
							continue;
						}

						try {
							float tanimotoHistogramProperties = DistributionComparisonTools.computeSubDistributionTanimoto(histogramD2OccupiedSolidAngleQuery, HistogramD2OccupiedSolidAngleTarget);
							if (tanimotoHistogramProperties < 0.40){
								//System.out.println("skipped fingerprint d2 distance occupied spaces ");
								continue;
							}
						} catch (CannotCompareDistributionException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
							continue;
						}
						foundGoodOne = true;
						break;
					}

					if (foundGoodOne == true){ // only one good one is enough to allow the comparison as it could be a good one
						ShapeContainerIfc shapecontainer = null;
						try {
							shapecontainer = ShapeContainerFactory.getShapeAroundASegmentOfChainUsingStartingMyMonomerPositionInChain(EnumShapeReductor.CLUSTERING, myStructureGlobalBrutTarget, algoParameters, chainIdFromDB.toCharArray(), matchingRankId, peptideLength);
						} catch (ShapeBuildingException e) {
							e.printStackTrace();
						}
						CompareOneOnlyRunnable compare = new CompareOneOnlyRunnable(queryShape, shapecontainer, algoParameters);
						try{
							executorService.execute(compare);
						}catch (RejectedExecutionException e){
							try {
								Thread.sleep(timeSecondsToWaitIfQueueIsFullBeforeAddingMore * 1000);
								continue;
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
						}
					}
				}
			}
		}
	}




	// -------------------------------------------------------------------
	// Implementation
	// -------------------------------------------------------------------
	private List<String> makeSubsequenceOfGivenLengthWithSomeOverlapingIfNeeded(String sequenceToFind, int splittedSequenceLength) {

		int peptideLength = sequenceToFind.length() / 3;
		int sizeInterval = 3*splittedSequenceLength;

		List<String> subSequences = new ArrayList<>();

		// if I do spaces of one then it is always good
		for (int i=0; i<peptideLength; i++){
			int beginChar = 3*i;
			int endChar = 3*(i+splittedSequenceLength);
			if (endChar <= peptideLength*3){
				String subSequence = sequenceToFind.substring(beginChar, endChar);
				subSequences.add(subSequence);
			}
		} 
		return subSequences;
	}



	private List<String> makeSubsequenceOfGivenLengthAndLastLongerIfNeeded(String sequenceToFind, int splittedSequenceLength) {

		int peptideLength = sequenceToFind.length() / 3;
		int sizeInterval = 3*splittedSequenceLength;

		List<String> subSequences = new ArrayList<>();

		int countOfSubPart = peptideLength / splittedSequenceLength;
		for (int i=0; i <countOfSubPart; i++){
			int begin = sizeInterval*i;
			int end = sizeInterval*(i+1);
			if (i == countOfSubPart - 1){
				end = 3*peptideLength;
			}
			String subSequence = sequenceToFind.substring(begin, end);
			subSequences.add(subSequence);
		}
		return subSequences;
	}
}
