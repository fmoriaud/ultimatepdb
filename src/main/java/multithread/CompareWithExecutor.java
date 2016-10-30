package multithread;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import database.DatabaseTools;
import parameters.AlgoParameters;
import parameters.SmartTargetFileLineParser;
import parameters.TargetDefinedByHetAtm;
import parameters.TargetDefinedBySegmentOfChainBasedOnSegmentLength;
import parameters.TargetDefinedBySegmentOfChainBasedOnSequenceMotif;
import parameters.TargetDefinedByWholeChain;
import parameters.TargetsIfc;
import protocols.DockPeptides;
import protocols.ShapeContainerFactory;
import shape.ShapeContainerIfc;
import shapeBuilder.EnumShapeReductor;
import shapeBuilder.ShapeBuildingException;
import shapeBuilder.ShapeBuildingTools;
import mystructure.EnumMyReaderBiojava;
import mystructure.MyStructureIfc;


public class CompareWithExecutor {
    //------------------------
    // Class variables
    //------------------------

    private List<TargetsIfc> targetsDefiners;

    private AlgoParameters algoParameters;
    private EnumMyReaderBiojava enumMyReaderBiojava;
    private ShapeContainerIfc queryShape;

    private Connection connexion;

    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public CompareWithExecutor(ShapeContainerIfc queryShape, List<TargetsIfc> targetsDefiners,
                               AlgoParameters algoParameters, EnumMyReaderBiojava enumMyReaderBiojava) {

        this.queryShape = queryShape;
        this.targetsDefiners = targetsDefiners;
        this.algoParameters = algoParameters;
        this.enumMyReaderBiojava = enumMyReaderBiojava;
        connexion = DatabaseTools.getConnection();
    }


    // -------------------------------------------------------------------
    // Public methods
    // -------------------------------------------------------------------
    public void run() {

        // try to do here a comparison based on neighboring residues and return false
//		queryShape.getMyStructureUsedToComputeShape();
//		MyStructureFingerprint myStructureFingerprint = new MyStructureFingerprint(queryShape.getMyStructureUsedToComputeShape(), algoParameters);
//
//		myStructureFingerprint.computePercentageAndHistogram(null, 0);
//		double percentageOccupiedQuery = myStructureFingerprint.getPercentageOccupied();
//		System.out.println("Query: " + percentageOccupiedQuery);
//		List<Integer> histogramD2OccupiedSolidAngleQuery = myStructureFingerprint.getHistogramD2OccupiedSolidAngle();
//
//		System.out.println(histogramD2OccupiedSolidAngleQuery);

        int consumersCount = algoParameters.getSHAPE_COMPARISON_THREAD_COUNT();


        //final ExecutorService executorService = Executors.newFixedThreadPool(consumersCount);
        final ExecutorService executorService = getExecutorServiceForComparisons(consumersCount);
        int timeSecondsToWaitIfQueueIsFullBeforeAddingMore = 60;


        SmartTargetFileLineParser smartTargetFileLineParser = new SmartTargetFileLineParser(algoParameters, enumMyReaderBiojava);

        // not needed as I use the Callable object now
        //FindSequenceInDatabase findSequenceInDatabase = new FindSequenceInDatabase(algoParameters, enumMyReaderBiojava);

        for (TargetsIfc targetDefiner : targetsDefiners) {

            String fileName = targetDefiner.getFileName();

            if (targetDefiner instanceof TargetDefinedByHetAtm) {

                TargetDefinedByHetAtm targetDefinedByHetAtm = (TargetDefinedByHetAtm) targetDefiner;
                float minMW = targetDefinedByHetAtm.getMinMW();
                float maxMW = targetDefinedByHetAtm.getMaxMW();

                try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
                    String line = br.readLine();

                    while (line != null) {
                        boolean success = smartTargetFileLineParser.parseLineHetAtm(line);
                        if (success == false) {
                            line = br.readLine();
                            continue;
                        }

                        float mw = smartTargetFileLineParser.getMw();
                        String fourLetterCode = smartTargetFileLineParser.getFourLettercode();
                        String threeLetterCode = smartTargetFileLineParser.getThreeLettercode();
                        String chainIdFromDB = smartTargetFileLineParser.getChainName();

                        String sequenceInDb = ""; // need a db SequenceTools.returnSequenceInDbifFourLetterCodeAndChainfoundInDatabase(fourLetterCode, chainIdFromDB, connexion);
                        if (sequenceInDb == null) {
                            line = br.readLine();
                            continue;
                        }

                        int occurenceId = 1;
                        if (mw >= minMW && mw <= maxMW) {

                            MyStructureIfc myStructureGlobalBrut;
                            try {
                                myStructureGlobalBrut = ShapeBuildingTools.getMyStructure(fourLetterCode.toCharArray(), algoParameters, enumMyReaderBiojava);
                            } catch (ShapeBuildingException e2) {
                                line = br.readLine();
                                continue;
                            }

//							StructureLocalToBuildShapeHetAtm structureLocalToBuildShapeHetAtm = new StructureLocalToBuildShapeHetAtm(myStructureGlobalBrut, threeLetterCode.toCharArray(), occurenceId);
//							try {
//								structureLocalToBuildShapeHetAtm.compute();
//							} catch (ShapeBuildingException e2) {
//								line = br.readLine();
//								continue;
//							}
//
//							boolean doComparison = isComparisonWorthAccordingToSolidAngleSimilarity(structureLocalToBuildShapeHetAtm, percentageOccupiedQuery, histogramD2OccupiedSolidAngleQuery);
//							if (doComparison == false){
//								line = br.readLine();
//								continue;
//							}

                            ShapeContainerIfc shapecontainer = null;
                            try {
                               shapecontainer = ShapeContainerFactory.getShapeAroundAHetAtomLigand(EnumShapeReductor.CLUSTERING, myStructureGlobalBrut, algoParameters, threeLetterCode.toCharArray(), occurenceId);
                            } catch (ShapeBuildingException e) {
                                e.printStackTrace();
                            }
                           CompareOneOnlyRunnable compare = new CompareOneOnlyRunnable(queryShape, shapecontainer, algoParameters);
                            try {
                                executorService.execute(compare);
                            } catch (RejectedExecutionException e) {

                                try {
                                    Thread.sleep(timeSecondsToWaitIfQueueIsFullBeforeAddingMore * 1000);
                                    continue;
                                } catch (InterruptedException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                        line = br.readLine();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (targetDefiner instanceof TargetDefinedBySegmentOfChainBasedOnSegmentLength) {

                TargetDefinedBySegmentOfChainBasedOnSegmentLength targetDefinedBySegmentOfChainBasedOnSegmentLength = (TargetDefinedBySegmentOfChainBasedOnSegmentLength) targetDefiner;
                int minLength = targetDefinedBySegmentOfChainBasedOnSegmentLength.getMinLength();
                int maxLength = targetDefinedBySegmentOfChainBasedOnSegmentLength.getMaxLength();
                int segmentLength = targetDefinedBySegmentOfChainBasedOnSegmentLength.getSegmentLength();

                try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
                    String line = br.readLine();

                    A:
                    while (line != null) {
                        boolean success = smartTargetFileLineParser.parseLineChain(line);
                        if (success == false) {
                            line = br.readLine();
                            continue;
                        }

                        String fourLetterCode = smartTargetFileLineParser.getFourLettercode();
                        String chainIdFromDB = smartTargetFileLineParser.getChainName();
                        int chainLengthFromFile = smartTargetFileLineParser.getChainLengthFromFile();

                        String sequenceInDb = ""; // need a db FinSequenceInDatabaseTools.returnSequenceInDbifFourLetterCodeAndChainfoundInDatabase(fourLetterCode, chainIdFromDB, connexion);
                        if (sequenceInDb == null) {
                            line = br.readLine();
                            continue;
                        }
                        //String sequenceFromDB = findSequenceInDatabase.getSequenceFromDB();
                        int sequenceLengthInDB = sequenceInDb.length() / 3;
                        if (sequenceLengthInDB >= minLength && sequenceLengthInDB <= maxLength) {

                            MyStructureIfc myStructureGlobalBrut;
                            try {
                                myStructureGlobalBrut = ShapeBuildingTools.getMyStructure(fourLetterCode.toCharArray(), algoParameters, enumMyReaderBiojava);
                            } catch (ShapeBuildingException e2) {
                                //System.out.println();
                                continue A;
                            }

                            for (int i = 0; i < sequenceLengthInDB - segmentLength; i++) {

//								StructureLocalToBuildShapeSegmentOfShape structureLocalToBuildShapeSegmentOfShape = new StructureLocalToBuildShapeSegmentOfShape(myStructureGlobalBrut, algoParameters, chainIdFromDB.toCharArray(), i, segmentLength);
//								try {
//									structureLocalToBuildShapeSegmentOfShape.compute();
//								} catch (ShapeBuildingException e2) {
//									continue;
//								}
//
//								boolean doComparison = isComparisonWorthAccordingToSolidAngleSimilarity(structureLocalToBuildShapeSegmentOfShape, percentageOccupiedQuery, histogramD2OccupiedSolidAngleQuery);
//								if (doComparison == false){
//									line = br.readLine();
//									continue;
//								}

                                // chainIdFromDB.toCharArray(), i, segmentLength,
                                ShapeContainerIfc shapecontainer = null;
                                try {
                                    shapecontainer = ShapeContainerFactory.getShapeAroundASegmentOfChainUsingStartingMyMonomerPositionInChain(EnumShapeReductor.CLUSTERING, myStructureGlobalBrut, algoParameters, chainIdFromDB.toCharArray(), i, segmentLength);
                                } catch (ShapeBuildingException e) {
                                    e.printStackTrace();
                                }

                                CompareOneOnlyRunnable compare = new CompareOneOnlyRunnable(queryShape, shapecontainer, algoParameters);

                                try {
                                    executorService.execute(compare);
                                } catch (RejectedExecutionException e) {

                                    try {
                                        Thread.sleep(timeSecondsToWaitIfQueueIsFullBeforeAddingMore * 1000);
                                        i--;
                                    } catch (InterruptedException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            }
                        }
                        line = br.readLine();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (targetDefiner instanceof TargetDefinedBySegmentOfChainBasedOnSequenceMotif) {
                TargetDefinedBySegmentOfChainBasedOnSequenceMotif targetDefinedBySegmentOfChainBasedOnSequenceMotif = (TargetDefinedBySegmentOfChainBasedOnSequenceMotif) targetDefiner;

                DockPeptides dockPeptides = new DockPeptides(targetDefinedBySegmentOfChainBasedOnSequenceMotif, algoParameters, enumMyReaderBiojava, queryShape, executorService);
                dockPeptides.dock();
            }

//			if (targetDefiner instanceof TargetDefinedBySegmentOfChainBasedOnSequenceMotif){
//
//				TargetDefinedBySegmentOfChainBasedOnSequenceMotif targetDefinedBySegmentOfChainBasedOnSequenceMotif = (TargetDefinedBySegmentOfChainBasedOnSequenceMotif) targetDefiner;
//
//				String sequenceToFind = targetDefinedBySegmentOfChainBasedOnSequenceMotif.getSequence();
//				int peptideLength = sequenceToFind.length() / 3;
//
//				int minLength = targetDefinedBySegmentOfChainBasedOnSequenceMotif.getMinLength();
//				int maxLength = targetDefinedBySegmentOfChainBasedOnSequenceMotif.getMaxLength();
//				boolean useSimilarSequences = targetDefinedBySegmentOfChainBasedOnSequenceMotif.isUseSimilarSequences();
//
//				List<HitInSequenceDb> hitsInDatabase = FinSequenceInDatabaseTools.find(minLength, maxLength, sequenceToFind, useSimilarSequences);
//
//				System.out.println("Found " + hitsInDatabase.size() + "  sequence hits in the Sequence Database");
//				String fourLetterCode;
//				String chainIdFromDB;
//				for (HitInSequenceDb hitInSequenceDb: hitsInDatabase){
//
//					fourLetterCode = hitInSequenceDb.getFourLetterCode();
//					chainIdFromDB = hitInSequenceDb.getChainIdFromDB();
//					List<Integer> listRankIds = hitInSequenceDb.getListRankIds();
//
//					MyStructureIfc myStructureGlobalBrut;
//					try {
//						myStructureGlobalBrut = ShapeBuildingTools.getMyStructure(fourLetterCode.toCharArray(), algoParameters, enumMyReaderBiojava);
//					} catch (ShapeBuildingException e2) {
//						//System.out.println();
//						continue;
//					}
//
//					for (int i=0; i<listRankIds.size(); i++){
//
//						Integer matchingRankId = listRankIds.get(i);
//
//						StructureLocalToBuildShapeSegmentOfShape structureLocalToBuildShapeSegmentOfShape = new StructureLocalToBuildShapeSegmentOfShape(myStructureGlobalBrut, algoParameters, chainIdFromDB.toCharArray(), matchingRankId, peptideLength);
//						try {
//							structureLocalToBuildShapeSegmentOfShape.compute();
//						} catch (ShapeBuildingException e2) {
//							continue;
//						}
//						MyStructureIfc myStructureLocal = structureLocalToBuildShapeSegmentOfShape.getMyStructureLocal();
//						// TODO use it to avoid redoing this but it is a lot so first need to check if usefull
//
//						MyStructureFingerprint myStructureFingerprintTarget = new MyStructureFingerprint(myStructureLocal);
//
//						myStructureFingerprintTarget.computePercentageOccupiedAngleDistribution();
//						double percentageOccupiedTarget = myStructureFingerprintTarget.getPercentageOccupied();
//						List<Integer> histogramD2OccupiedSolidAngleTarget = myStructureFingerprintTarget.getHistogramD2OccupiedSolidAngle();
//
//						//System.out.println("Target: " + percentageOccupiedTarget);
//						//System.out.println(histogramD2OccupiedSolidAngleTarget);
//
//						if (Math.abs(percentageOccupiedQuery - percentageOccupiedTarget) > 0.1){
//							//System.out.println("skipped percentage solid state occupancy ");
//							continue;
//						}
//
//						try {
//							float tanimotoHistogramProperties = DistributionComparisonTools.computeSubDistributionTanimoto(histogramD2OccupiedSolidAngleQuery, histogramD2OccupiedSolidAngleTarget);
//							if (tanimotoHistogramProperties < 0.40){
//								//System.out.println("skipped fingerprint d2 distance occupied spaces ");
//								continue;
//							}
//						} catch (CannotCompareDistributionException e2) {
//							// TODO Auto-generated catch block
//							e2.printStackTrace();
//						}
//
//
//						ShapeBuilderConstructorIfc shapeBuilder = new ShapeBuilderConstructorSegmentOfChain(myStructureGlobalBrut, fourLetterCode.toCharArray(), chainIdFromDB.toCharArray(), matchingRankId, peptideLength, algoParameters, enumMyReaderBiojava);
//						CompareOneOnlyRunnable compare = new CompareOneOnlyRunnable(queryShape, shapeBuilder, algoParameters);
//						try{
//							executorService.execute(compare);
//						}catch (RejectedExecutionException e){
//							try {
//								Thread.sleep(timeSecondsToWaitIfQueueIsFullBeforeAddingMore * 1000);
//								continue;
//							} catch (InterruptedException e1) {
//								e1.printStackTrace();
//							}
//						}
//					}
//				}
//			}


            if (targetDefiner instanceof TargetDefinedByWholeChain) {

                TargetDefinedByWholeChain targetDefinedByWholeChain = (TargetDefinedByWholeChain) targetDefiner;

                int minLength = targetDefinedByWholeChain.getMinLength();
                int maxLength = targetDefinedByWholeChain.getMaxLength();

                try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
                    String line = br.readLine();

                    while (line != null) {
                        boolean success = smartTargetFileLineParser.parseLineChain(line);
                        if (success == false) {
                            line = br.readLine();
                            continue;
                        }

                        String fourLetterCode = smartTargetFileLineParser.getFourLettercode();
                        String chainIdFromDB = smartTargetFileLineParser.getChainName();
                        int chainLengthFromFile = smartTargetFileLineParser.getChainLengthFromFile();

                        String sequenceInDb = ""; // need a db FinSequenceInDatabaseTools.returnSequenceInDbifFourLetterCodeAndChainfoundInDatabase(fourLetterCode, chainIdFromDB, connexion);
                        if (sequenceInDb == null) {
                            line = br.readLine();
                            continue;
                        }

                        int sequenceLengthInDB = sequenceInDb.length() / 3;
                        if (sequenceLengthInDB >= minLength && sequenceLengthInDB <= maxLength) {

                            MyStructureIfc myStructureGlobalBrut;
                            try {
                                myStructureGlobalBrut = ShapeBuildingTools.getMyStructure(fourLetterCode.toCharArray(), algoParameters, enumMyReaderBiojava);
                            } catch (ShapeBuildingException e2) {
                                line = br.readLine();
                                continue;
                            }
//							StructureLocalToBuildShapeWholeChain structureLocalToBuildShapeWholeChain = new StructureLocalToBuildShapeWholeChain(myStructureGlobalBrut, chainIdFromDB.toCharArray());
//							try {
//								structureLocalToBuildShapeWholeChain.compute();
//							} catch (ShapeBuildingException e2) {
//								line = br.readLine();
//								continue;
//							}
//
//							boolean doComparison = isComparisonWorthAccordingToSolidAngleSimilarity(structureLocalToBuildShapeWholeChain, percentageOccupiedQuery, histogramD2OccupiedSolidAngleQuery);
//							if (doComparison == false){
//								line = br.readLine();
//								continue;
//							}


                            // Might not work anymore as I use a different constructor, before was relaoding
                            ShapeContainerIfc shapecontainer = null;
                            try {
                                shapecontainer = ShapeContainerFactory.getShapeAroundAChain(EnumShapeReductor.CLUSTERING, myStructureGlobalBrut, algoParameters, chainIdFromDB.toCharArray());
                            } catch (ShapeBuildingException e) {
                                e.printStackTrace();
                            }
                            CompareOneOnlyRunnable compare = new CompareOneOnlyRunnable(queryShape, shapecontainer, algoParameters);
                            try {
                                executorService.execute(compare);
                            } catch (RejectedExecutionException e) {

                                try {
                                    Thread.sleep(timeSecondsToWaitIfQueueIsFullBeforeAddingMore * 1000);
                                    continue;
                                } catch (InterruptedException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                        line = br.readLine();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        while (true) {
            try {
                Thread.sleep(100000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        //executorService.shutdown();
    }


//	private boolean isComparisonWorthAccordingToSolidAngleSimilarity(StructureLocalToBuildShapeIfc structureLocalToBuildShape, double percentageOccupiedQuery, List<Integer> histogramD2OccupiedSolidAngleQuery){
//
//		MyStructureIfc myStructureLocal = structureLocalToBuildShape.getMyStructureLocal();
//		// TODO use it to avoid redoing this but it is a lot so first need to check if usefull
//
//		MyStructureFingerprint myStructureFingerprintTarget = new MyStructureFingerprint(myStructureLocal, algoParameters);
//
//		myStructureFingerprintTarget.computePercentageAndHistogram();
//		double percentageOccupiedTarget = myStructureFingerprintTarget.getPercentageOccupied();
//		List<Integer> histogramD2OccupiedSolidAngleTarget = myStructureFingerprintTarget.getHistogramD2OccupiedSolidAngle();
//
//		//System.out.println("Target: " + percentageOccupiedTarget);
//		//System.out.println(histogramD2OccupiedSolidAngleTarget);
//
//		if (Math.abs(percentageOccupiedQuery - percentageOccupiedTarget) > 0.1){
//			//System.out.println("skipped percentage solid state occupancy ");
//			//line = br.readLine();
//			//continue;
//			return false;
//		}
//
//		try {
//			float tanimotoHistogramProperties = DistributionComparisonTools.computeSubDistributionTanimoto(histogramD2OccupiedSolidAngleQuery, histogramD2OccupiedSolidAngleTarget);
//			if (tanimotoHistogramProperties < 0.40){
//				//System.out.println("skipped fingerprint d2 distance occupied spaces ");
//				//line = br.readLine();
//				//continue;
//				return false;
//			}
//		} catch (CannotCompareDistributionException e2) {
//			return false;
//		}
//		return true;
//	}


    // -------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------
    private ExecutorService getExecutorServiceForComparisons(int consumersCount) {
        int corePoolSize = 0; // no need to keep idle ones
        long keepAliveTime = 500000000; // no need to terminate if thread gets no job, that
        // could happen when searching database for a potetial hit, that could last as long
        // as the time to search the whole system
        int maxCountRunnableInBoundQueue = 10000; // 10000;

        ExecutorService threadPoolExecutor =
                new ThreadPoolExecutor(
                        corePoolSize,
                        consumersCount,
                        keepAliveTime,
                        TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<Runnable>(maxCountRunnableInBoundQueue)
                );

        return threadPoolExecutor;
    }
}
