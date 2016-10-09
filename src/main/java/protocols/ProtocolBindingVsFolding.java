package protocols;

import convertformat.AdapterBioJavaStructure;
import convertformat.ExceptionInConvertFormat;
import database.HitInSequenceDb;
import database.SequenceTools;
import genericBuffer.GenericBuffer;
import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import hits.Hit;
import io.BiojavaReader;
import io.ExceptionInIOPackage;
import math.ProcrustesAnalysisIfc;
import math.ToolsMath;
import mystructure.*;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.biojava.nbio.core.sequence.location.template.Location;
import org.biojava.nbio.structure.Structure;
import parameters.AlgoParameters;
import shape.HasPeptideIfc;
import shape.ShapeContainerIfc;
import shape.ShapeContainerWithLigand;
import shapeBuilder.EnumShapeReductor;
import shapeBuilder.ShapeBuildingException;
import shapeCompare.ComparatorShapeContainerQueryVsAnyShapeContainer;
import shapeCompare.NullResultFromAComparisonException;
import shapeCompare.PairingTools;
import shapeCompare.ProcrustesAnalysis;
import ultiJmol1462.MyJmolTools;
import ultiJmol1462.Protonate;
import ultiJmol1462.ResultsUltiJMolMinimizedHitLigandOnTarget;
import ultimatepdb.UltiJmol1462;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fabrice on 02/10/16.
 */
public class ProtocolBindingVsFolding {
    //------------------------
    // Class variables
    //------------------------
    private String queryFourLetterCode;
    private String peptideChainId;

    private AlgoParameters algoParameters;


    //-------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------
    public ProtocolBindingVsFolding(String queryFourLetterCode, String peptideChainId) {

        this.queryFourLetterCode = queryFourLetterCode;
        this.peptideChainId = peptideChainId;
    }


    // -------------------------------------------------------------------
    // Public && Interface method
    // -------------------------------------------------------------------
    public static void main(String[] args) throws ParsingConfigFileException {

        ProtocolBindingVsFolding protocol = new ProtocolBindingVsFolding("2ce8", "X");
        // SEQRES   1 X    9  MET PHE SER ILE ASP ASN ILE LEU ALA

        protocol.run();
    }


    public void run() throws ParsingConfigFileException {

        prepareAlgoParameters();
        // build the query
        ShapeContainerIfc queryShape = buildQueryShape();


        // Find same sequence occurences in sequence DB
        String sequenceToFind = "METPHESERILEASPASNILELEUALA";
        //String sequenceToFind = "METPHESERILE";

        int peptideLength = sequenceToFind.length() / 3;

        //int minLength = targetDefinedBySegmentOfChainBasedOnSequenceMotif.getMinLength();
        //int maxLength = targetDefinedBySegmentOfChainBasedOnSequenceMotif.getMaxLength();
        boolean useSimilarSequences = true;

        List<HitInSequenceDb> hitsInDatabase = SequenceTools.find(peptideLength, 1000, sequenceToFind, useSimilarSequences);

        System.out.println("Found " + hitsInDatabase.size() + "  sequence hits in the Sequence Database");
        String fourLetterCodeTarget;
        String chainIdFromDB;
        for (HitInSequenceDb hitInSequenceDb : hitsInDatabase) {

            fourLetterCodeTarget = hitInSequenceDb.getFourLetterCode();
            chainIdFromDB = hitInSequenceDb.getChainIdFromDB();
            List<Integer> listRankIds = hitInSequenceDb.getListRankIds();

            BiojavaReader reader = new BiojavaReader();
            Structure mmcifStructure = null;
            try {
                mmcifStructure = reader.readFromPDBFolder(fourLetterCodeTarget.toLowerCase(), algoParameters.getPATH_TO_REMEDIATED_PDB_MMCIF_FOLDER(), algoParameters.getPATH_TO_CHEMCOMP_FOLDER());
            } catch (IOException | ExceptionInIOPackage e) {

            }
            AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
            MyStructureIfc mystructure = null;
            try {
                mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure, EnumMyReaderBiojava.BioJava_MMCIFF);
            } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {

            }

            char[] chainId = chainIdFromDB.toCharArray();

            for (int i = 0; i < listRankIds.size(); i++) {

                Integer matchingRankId = listRankIds.get(i);

                ShapeContainerIfc targetShape = null;
                try {
                    targetShape = ShapeContainerFactory.getShapeAroundASegmentOfChainUsingStartingMyMonomerPositionInChain(EnumShapeReductor.CLUSTERING, mystructure, algoParameters, chainId, matchingRankId, peptideLength);

                } catch (ShapeBuildingException e) {
                    e.printStackTrace();
                }

                System.out.println(fourLetterCodeTarget + " " + chainIdFromDB + " " + matchingRankId + " " + peptideLength + " : ");
                ComparatorShapeContainerQueryVsAnyShapeContainer comparatorShape = new ComparatorShapeContainerQueryVsAnyShapeContainer(queryShape, targetShape, algoParameters);
                List<Hit> listBestHitForEachAndEverySeed = null;
                try {
                    listBestHitForEachAndEverySeed = comparatorShape.computeResults();
                    for (Hit hit : listBestHitForEachAndEverySeed) {
                        System.out.println("Minimizing ... " + hit.toString());
                        minimizeHitInQuery(hit, queryShape, targetShape);
                    }

                } catch (NullResultFromAComparisonException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
            // Put in a callable the shape building and comparison

            // Feed an executor

        }
    }


    // -------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------
    private void minimizeHitInQuery(Hit hit, ShapeContainerIfc queryShape, ShapeContainerIfc targetShape) throws NullResultFromAComparisonException {

        double cost = hit.getResultsFromEvaluateCost().getCost();
        System.out.println("distance residual = " + hit.getResultsFromEvaluateCost().getDistanceResidual());
        System.out.println("currentBestHit.getResultsFromEvaluateCost().getCost() = " + cost);

        //if (cost < 1.00 || (rmsdLigand != null && rmsdLigand < 5.0)){
        Float rmsdLigand = computeRmsdBackboneAtomBetweenHitPeptideAndQueryLigandDefinigQuery(hit, queryShape, algoParameters);

        int pairedPointCount = hit.getResultsFromEvaluateCost().getPairingAndNullSpaces().getPairing().size();
        int unpairedPointHit = hit.getResultsFromEvaluateCost().getPairingAndNullSpaces().getNullSpaceOfMap2().size();
        float ratioPairedPointToHitPoints = (float) pairedPointCount / ((float) pairedPointCount + (float) unpairedPointHit);

        if (targetShape instanceof ShapeContainerWithLigand || (rmsdLigand != null && rmsdLigand < 100.0)) {
            // scoring with Jmol forcefield

            ResultsUltiJMolMinimizedHitLigandOnTarget hitScore = null;
            if (algoParameters.isOPTIMIZE_HIT_GEOMETRY() == true) {
                try {

                    MyChainIfc peptideOrLigand = null;

                    if (hit.getShapeContainer() instanceof HasPeptideIfc) {
                        HasPeptideIfc queryShapeWithPeptide = (HasPeptideIfc) hit.getShapeContainer();
                        peptideOrLigand = queryShapeWithPeptide.getPeptide();
                    }
                    if (hit.getShapeContainer() instanceof ShapeContainerWithLigand) {
                        ShapeContainerWithLigand shapeContainerWithLigand = (ShapeContainerWithLigand) hit.getShapeContainer();
                        peptideOrLigand = new MyChain(shapeContainerWithLigand.getHetatmLigand(), shapeContainerWithLigand.getHetatmLigand().getParent().getChainId()); // returnCloneRotatedPeptide(hetAtomInChain, hit.getResultsFromEvaluateCost());
                    }

                    MyStructureIfc myStructurePeptide = new MyStructure(peptideOrLigand, algoParameters);

                    Protonate protonate = new Protonate(myStructurePeptide, algoParameters);
                    try {
                        protonate.compute();
                    } catch (ExceptionInScoringUsingBioJavaJMolGUI exceptionInScoringUsingBioJavaJMolGUI) {
                        exceptionInScoringUsingBioJavaJMolGUI.printStackTrace();
                    }

                    MyStructureIfc preparedPeptide = protonate.getProtonatedMyStructure();


                    MyStructureIfc clonedRotatedPeptide = null;
                    try {
                        clonedRotatedPeptide = preparedPeptide.cloneWithSameObjectsRotatedCoords(hit.getResultsFromEvaluateCost());
                    } catch (ExceptionInMyStructurePackage exceptionInMyStructurePackage) {
                        exceptionInMyStructurePackage.printStackTrace();
                    }

                    MyStructureIfc structureQueryComputeshape = queryShape.getMyStructureUsedToComputeShape();

                    Protonate protonate2 = null;
                    try {
                        protonate2 = new Protonate(structureQueryComputeshape.cloneWithSameObjects(), algoParameters);
                    } catch (ExceptionInMyStructurePackage exceptionInMyStructurePackage) {
                        exceptionInMyStructurePackage.printStackTrace();
                    }

                    protonate2.compute();

                    MyStructureIfc preparedQuery = protonate2.getProtonatedMyStructure();

                    try {
                        hitScore = MyJmolTools.scoreByMinimizingLigandOnFixedReceptor(algoParameters, clonedRotatedPeptide, preparedQuery);
                    } catch (ExceptionInScoringUsingBioJavaJMolGUI exceptionInScoringUsingBioJavaJMolGUI) {
                        exceptionInScoringUsingBioJavaJMolGUI.printStackTrace();
                    }

                    if (hitScore != null) {
                        System.out.println("InteractionEFinal = " + hitScore.getInteractionEFinal());
                        System.out.println("rmsd ligand = " + hitScore.getRmsdLigand());
                        System.out.println("ligand stained energy = " + hitScore.getLigandStrainedEnergy());
                        System.out.println("RatioPairedPointToHitPoints = " + hitScore.getRatioPairedPointToHitPoints());
                        System.out.println("count longer than 2A change = " + hitScore.getCountOfLongDistanceChange());
                    }

                } catch (ExceptionInScoringUsingBioJavaJMolGUI e) {

                    // TODO FMM here I should restart the JmolGUI like when protonation failed in shape builder: is it done ?
                    System.out.println("HitTools.scoreHitWithinQuery " + e.getMessage());
                    String message = "scoreHitWithinQuery throws exception";
                    NullResultFromAComparisonException ex = new NullResultFromAComparisonException(message);
                    throw ex;
                }
                if (hitScore == null) {
                    String message = "hitscore is null";
                    NullResultFromAComparisonException ex = new NullResultFromAComparisonException(message);
                    throw ex;
                }
            } else {

                hitScore = new ResultsUltiJMolMinimizedHitLigandOnTarget(0, 0.0f, 0f, 0.0f);
            }

            hitScore.setRatioPairedPointToHitPoints(ratioPairedPointToHitPoints);

            if (rmsdLigand != null) {
                System.out.println("rmsdLigand = " + rmsdLigand);
                hitScore.setRmsdLigand(rmsdLigand);
            }

            //				boolean isShapeOverlapOK = checkIfQuerySignificantlyCovered(currentBestHit.getResultsFromEvaluateCost(), hitScore);
            //
            //				if (isShapeOverlapOK == false){
            //					return false;
            //				}

            hit.setHitScore(hitScore);
        }
    }


    private ShapeContainerIfc buildQueryShape() {

        char[] chainId = peptideChainId.toCharArray();

        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(queryFourLetterCode, algoParameters.getPATH_TO_REMEDIATED_PDB_MMCIF_FOLDER(), algoParameters.getPATH_TO_CHEMCOMP_FOLDER());
        } catch (IOException | ExceptionInIOPackage e) {

        }

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure, EnumMyReaderBiojava.BioJava_MMCIFF);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {

        }

        ShapeContainerIfc shapecontainer = null;
        try {
            shapecontainer = ShapeContainerFactory.getShapeAroundAChain(EnumShapeReductor.CLUSTERING, mystructure, algoParameters, chainId);
        } catch (ShapeBuildingException e) {
            e.printStackTrace();
        }

        return shapecontainer;
    }


    private void prepareAlgoParameters() throws ParsingConfigFileException {
        URL url = ProtocolBindingVsFolding.class.getClassLoader().getResource("ultimate.xml");
        algoParameters = CommandLineTools.generateModifiedAlgoParameters(url.getPath(), EnumMyReaderBiojava.BioJava_MMCIFF);
        algoParameters.ultiJMolBuffer = new GenericBuffer<UltiJmol1462>(algoParameters.getSHAPE_COMPARISON_THREAD_COUNT());
        algoParameters.procrustesAnalysisBuffer = new GenericBuffer<ProcrustesAnalysisIfc>(algoParameters.getSHAPE_COMPARISON_THREAD_COUNT());
        for (int i = 0; i < algoParameters.getSHAPE_COMPARISON_THREAD_COUNT(); i++) {
            ProcrustesAnalysisIfc procrustesAnalysis = new ProcrustesAnalysis(algoParameters);
            try {
                algoParameters.procrustesAnalysisBuffer.put(procrustesAnalysis);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        for (int i = 0; i < algoParameters.getSHAPE_COMPARISON_THREAD_COUNT(); i++) {
            UltiJmol1462 ultiJMol = new UltiJmol1462();
            try {
                algoParameters.ultiJMolBuffer.put(ultiJMol);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    private Float computeRmsdBackboneAtomBetweenHitPeptideAndQueryLigandDefinigQuery(Hit currentBestHit, ShapeContainerIfc queryShape, AlgoParameters algoParameters) {

        boolean isQueryShapeContainerHasPeptideIfc = queryShape instanceof HasPeptideIfc;
        boolean isHitShapeContainerHasPeptideIfc = currentBestHit.getShapeContainer() instanceof HasPeptideIfc;

        boolean canBeComputed = isQueryShapeContainerHasPeptideIfc && isHitShapeContainerHasPeptideIfc;
        if (!canBeComputed) {
            return null;
        }

        HasPeptideIfc queryShapeWithPeptide = (HasPeptideIfc) queryShape;
        MyChainIfc peptideUsedToBuiltTheQuery = queryShapeWithPeptide.getPeptide();

        ShapeContainerIfc targetshape = currentBestHit.getShapeContainer();
        HasPeptideIfc currentBestHitWithPeptide = (HasPeptideIfc) targetshape;
        MyChainIfc peptideCurrentBestHit = currentBestHitWithPeptide.getPeptide();

        if (peptideUsedToBuiltTheQuery != null) {

            List<MyAtomIfc> backboneAtomPeptideQuery = extractBackBoneAtoms(peptideUsedToBuiltTheQuery, algoParameters);
            List<MyAtomIfc> backboneAtomPeptideHit = extractBackBoneAtoms(peptideCurrentBestHit, algoParameters);
            // put hit in ref frame of query
            List<double[]> coordinatesHit = new ArrayList<>();
            for (MyAtomIfc atomHit : backboneAtomPeptideHit) {
                RealVector newPointCoords = PairingTools.alignPointFromShape2toShape1(currentBestHit.getResultsFromEvaluateCost(), new ArrayRealVector(ToolsMath.convertToDoubleArray(atomHit.getCoords())));
                coordinatesHit.add(newPointCoords.toArray());
            }
            List<double[]> coordinatesQuery = new ArrayList<>();
            for (MyAtomIfc atomQuery : backboneAtomPeptideQuery) {
                coordinatesQuery.add(ToolsMath.convertToDoubleArray(atomQuery.getCoords()));
            }

            List<double[]> smallestChainCoords = coordinatesHit;
            List<double[]> longestChainCoords = coordinatesQuery;
            List<MyAtomIfc> smallestChain = backboneAtomPeptideHit;
            List<MyAtomIfc> longestChain = backboneAtomPeptideQuery;

            if (backboneAtomPeptideHit.size() > backboneAtomPeptideQuery.size()) {
                smallestChain = backboneAtomPeptideQuery;
                longestChain = backboneAtomPeptideHit;
                smallestChainCoords = coordinatesQuery;
                longestChainCoords = coordinatesHit;
            }
            // 10
            // 6
            // pos 0 to pos 4 as start

            List<Integer> posibleStart = new ArrayList<>();

            int countPossibleOverlays = longestChain.size() - smallestChain.size() + 1;
            A:
            for (int j = 0; j <= countPossibleOverlays; j++) {

                for (int k = 0; k < smallestChain.size(); k++) {
                    MyAtomIfc currentAtomLongestchain = longestChain.get(k + j);
                    // if any mismatch in atom name I skip the current comparaison
                    //System.out.println(String.valueOf(smallestChain.get(k).getAtomName()) + " compared to " + String.valueOf(currentAtomLongestchain.getAtomName()));
                    if (!String.valueOf(smallestChain.get(k).getAtomName()).equals(String.valueOf(currentAtomLongestchain.getAtomName()))) {
                        continue A;
                    }
                }
                posibleStart.add(j);
            }
            //System.out.println("posibleStart : " + posibleStart);

            // for each possible start I compute the rmsd
            float minRmsd = Float.MAX_VALUE;
            for (int j = 0; j < posibleStart.size(); j++) {

                double rmsd = 0.0;
                for (int k = 0; k < smallestChain.size(); k++) {
                    double[] currentAtomSmallestchain = smallestChainCoords.get(k);
                    double[] currentAtomLongestchain = longestChainCoords.get(k + posibleStart.get(j));
                    double contribRmsd = ToolsMath.computeDistance(currentAtomSmallestchain, currentAtomLongestchain);
                    rmsd += contribRmsd * contribRmsd;
                }
                rmsd = rmsd / smallestChain.size();
                float finalRmsd = (float) Math.sqrt(rmsd);
                if (finalRmsd < minRmsd) {
                    minRmsd = finalRmsd;
                }
            }

            return minRmsd;
        }
        return null;
    }


    private List<MyAtomIfc> extractBackBoneAtoms(MyChainIfc peptideUsedToBuiltTheQuery, AlgoParameters algoParameters) {

        List<MyAtomIfc> backboneAtomToReturn = new ArrayList<>();
        for (MyMonomerIfc monomer : peptideUsedToBuiltTheQuery.getMyMonomers()) {
            MyAtomIfc atomN = null;
            MyAtomIfc atomC = null;
            MyAtomIfc atomCA = null;
            MyAtomIfc atomO = null;

            for (MyAtomIfc atom : monomer.getMyAtoms()) {
                if (String.valueOf(atom.getAtomName()).equals("N")) {
                    atomN = atom;
                    continue;
                }
                if (String.valueOf(atom.getAtomName()).equals("C")) {
                    atomC = atom;
                    continue;
                }
                if (String.valueOf(atom.getAtomName()).equals("CA")) {
                    atomCA = atom;
                    continue;
                }
                if (String.valueOf(atom.getAtomName()).equals("O")) {
                    atomO = atom;
                    continue;
                }
            }
            if (atomN != null && atomC != null && atomCA != null && atomO != null) {

                backboneAtomToReturn.add(atomN);
                backboneAtomToReturn.add(atomC);
                backboneAtomToReturn.add(atomCA);
                backboneAtomToReturn.add(atomO);
                //System.out.println(String.valueOf(atom.getAtomName()) + " is kept ");
            }
        }
        return backboneAtomToReturn;
    }
}