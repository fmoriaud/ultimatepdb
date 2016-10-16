package protocols;

import convertformat.AdapterBioJavaStructure;
import convertformat.ExceptionInConvertFormat;
import database.HitInSequenceDb;
import database.SequenceTools;
import genericBuffer.GenericBuffer;
import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import hits.Hit;
import hits.HitTools;
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
       // Only hit in DB is 2Q14 ILE, TYR, SER, ILE, GLU, ASN, PHE, LEU, THR
        // And it is a hit which not fit in the target following minimization
        //String sequenceToFind = "METPHESERILE";

        int peptideLength = sequenceToFind.length() / 3;

        //int minLength = targetDefinedBySegmentOfChainBasedOnSequenceMotif.getMinLength();
        //int maxLength = targetDefinedBySegmentOfChainBasedOnSequenceMotif.getMaxLength();
        boolean useSimilarSequences = true;

        List<HitInSequenceDb> hitsInDatabase = SequenceTools.find(peptideLength, 1000, sequenceToFind, useSimilarSequences);
        //List<HitInSequenceDb> hitsInDatabaseMod = new ArrayList<>();
        // hitsInDatabaseMod.add(hitsInDatabase.get(2));
        // hitsInDatabase = hitsInDatabaseMod;

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
                        HitTools.minimizeHitInQuery(hit, queryShape, targetShape, algoParameters);
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
}