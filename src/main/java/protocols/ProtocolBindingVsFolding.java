package protocols;

import convertformat.AdapterBioJavaStructure;
import convertformat.ExceptionInConvertFormat;
import database.HitInSequenceDb;
import database.SequenceTools;
import io.BiojavaReader;
import io.ExceptionInIOPackage;
import mystructure.*;
import org.biojava.nbio.structure.Structure;
import parameters.AlgoParameters;
import shape.ShapeContainerIfc;
import shape.ShapeContainerWithPeptide;
import shapeBuilder.ShapeBuildingException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.FileHandler;

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

        ProtocolBindingVsFolding protocol = new ProtocolBindingVsFolding("1be9", "B");
        //ProtocolBindingVsFolding protocol = new ProtocolBindingVsFolding("3erd", "D"); // C leads to poor contacts
        // SEQRES   1 X    9  MET PHE SER ILE ASP ASN ILE LEU ALA

        protocol.run();
    }


    public void run() throws ParsingConfigFileException {

        algoParameters = ProtocolTools.prepareAlgoParameters();

        FileHandler fh = null;
        try {
            fh = new FileHandler(algoParameters.getPATH_TO_RESULT_FILES() + "log_Project.txt");
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        fh.setFormatter(new OptimizerFormater());
        ControllerLoger.logger.addHandler(fh);


        // build the query

        ShapeContainerDefined shapeContainerbuilder = new ShapecontainerDefinedByWholeChain(queryFourLetterCode.toCharArray(), peptideChainId.toCharArray(), algoParameters);
        ShapeContainerIfc queryShape = null;
        try {
            queryShape = shapeContainerbuilder.getShapecontainer();
        } catch (ShapeBuildingException e) {
            e.printStackTrace();
            System.exit(0);
        }


        // Find same sequence occurences in sequence DB
        String sequenceToFind = "LYSGLNTHRSERVAL"; // 1be9
        //String sequenceToFind = "HISLYSILELEUHISARGLEULEUGLNASPSER"; // 3erd
        // String sequenceToFind = "METPHESERILEASPASNILELEUALA";
        // Only hit in DB is 2Q14 ILE, TYR, SER, ILE, GLU, ASN, PHE, LEU, THR
        // And it is a hit which not fit in the target following minimization
        //String sequenceToFind = "METPHESERILE";

        int peptideLength = sequenceToFind.length() / 3;

        //int minLength = targetDefinedBySegmentOfChainBasedOnSequenceMotif.getMinLength();
        //int maxLength = targetDefinedBySegmentOfChainBasedOnSequenceMotif.getMaxLength();
        boolean useSimilarSequences = false;

        List<HitInSequenceDb> hitsInDatabase = SequenceTools.find(SequenceTools.tableName, peptideLength, 1000, sequenceToFind, useSimilarSequences);


        if (queryShape instanceof ShapeContainerWithPeptide) {

            ShapeContainerWithPeptide query = (ShapeContainerWithPeptide) queryShape;
            MyChainIfc ligand = query.getPeptide();
            List<HitInSequenceDb> hitsInDatabaseUsingInteractions = SequenceTools.findUsingQueryPeptide(ligand, peptideLength, 1000, sequenceToFind, algoParameters);
            System.out.println("Found " + hitsInDatabaseUsingInteractions.size() + "  sequence hits in the Sequence Database using contacts");
            System.out.println("Found " + hitsInDatabase.size() + "  sequence hits in the Sequence Database using equivalent");

            hitsInDatabase = hitsInDatabaseUsingInteractions;
        }

        /*
        boolean startoutput = false;
        for (HitInSequenceDb hitInSequenceDb : hitsInDatabase) {
            if (hitInSequenceDb.getFourLetterCode().equals("5IT7")) {
                startoutput = true;
            }
            if (startoutput == true) {
                System.out.println(hitInSequenceDb.getFourLetterCode() + " " + hitInSequenceDb.getChainIdFromDB() + " " + hitInSequenceDb.getListRankIds().get(0));
                System.out.println();
            }
        }
        */
        //List<HitInSequenceDb> hitsInDatabaseMod = new ArrayList<>();
        // hitsInDatabaseMod.add(hitsInDatabase.get(2));
        // hitsInDatabase = hitsInDatabaseMod;

        String fourLetterCodeTarget;
        String chainIdFromDB;

        List<HitInSequenceDb> hitsEnrichedOnTop = putGoodHitsOnTopOfList(hitsInDatabase, getHitWithGoodRmsdBackbone());
        A:
        for (HitInSequenceDb hitInSequenceDb : hitsEnrichedOnTop) {


            fourLetterCodeTarget = hitInSequenceDb.getFourLetterCode();
            chainIdFromDB = hitInSequenceDb.getChainIdFromDB();
            List<Integer> listRankIds = hitInSequenceDb.getListRankIds();

            BiojavaReader reader = new BiojavaReader();
            Structure mmcifStructure = null;
            try {
                mmcifStructure = reader.readFromPDBFolder(fourLetterCodeTarget.toLowerCase(), algoParameters.getPATH_TO_REMEDIATED_PDB_MMCIF_FOLDER(), algoParameters.getPATH_TO_CHEMCOMP_FOLDER());
            } catch (IOException | ExceptionInIOPackage e) {
                continue A;
            }
            AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
            MyStructureIfc mystructure = null;
            try {
                mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure);
            } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
                continue A;
            }

            char[] chainId = chainIdFromDB.toCharArray();
            B:
            for (int i = 0; i < listRankIds.size(); i++) {

                Integer matchingRankId = listRankIds.get(i);

                ShapeContainerDefined shapeContainerDefined = new ShapecontainerDefinedBySegmentOfChain(fourLetterCodeTarget.toLowerCase().toCharArray(), chainId, matchingRankId, peptideLength, algoParameters);

                ShapeContainerIfc targetShape = null;
                try {
                    targetShape = shapeContainerDefined.getShapecontainer(mystructure);

                } catch (ShapeBuildingException e) {
                    continue B;
                }
                System.out.println(fourLetterCodeTarget + " " + chainIdFromDB + " " + matchingRankId + " " + peptideLength + " : ");

                boolean minimizeAllIfTrueOrOnlyOneIfFalse = true;
                try {
                    ProtocolTools.compareCompleteCheckAndWriteToResultFolder(minimizeAllIfTrueOrOnlyOneIfFalse, queryShape, targetShape, algoParameters);
                } catch (Exception e) {
                    continue B;
                }
            }
        }
    }

    private List<HitInSequenceDb> putGoodHitsOnTopOfList(List<HitInSequenceDb> hitsInDatabase, List<String> hitWithGoodRmsdBackbone) {

        List<HitInSequenceDb> newList = new ArrayList<>();

        List<HitInSequenceDb> hitToBeAddedOnTop = new ArrayList<>();

        A:
        for (String goodHit : hitWithGoodRmsdBackbone) {

            String[] splitGoodHit = goodHit.split(" ");
            String dbFourLetterCode = splitGoodHit[0];
            String dbChainId = splitGoodHit[1];
            Integer rankid = Integer.valueOf(splitGoodHit[2]);

            Iterator<HitInSequenceDb> it = hitsInDatabase.iterator();

            while (it.hasNext()) {
                HitInSequenceDb currentdbHit = it.next();

                if (currentdbHit.getFourLetterCode().equals(dbFourLetterCode)) {

                    // Only if only one matching ID, should be enough
                    if (currentdbHit.getChainIdFromDB().equals(dbChainId)) {
                        if (currentdbHit.getListRankIds().size() == 1 && currentdbHit.getListRankIds().get(0) == rankid) {
                            it.remove();
                            hitToBeAddedOnTop.add(currentdbHit);
                            continue A;
                        }
                    }
                }
            }
        }
        newList.addAll(hitToBeAddedOnTop);
        newList.addAll(hitsInDatabase);
        return newList;
    }


    // -------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------

    /**
     * Obtained with a three weeks run on single CPU on my Mac.
     * Cost was a bit different on aromatic & hydrophobe
     *
     * @return
     */
    private List<String> getHitWithGoodRmsdBackbone() {

        List<String> goodHits = new ArrayList<>();
        goodHits.add("2AJZ B 110");
        goodHits.add("1BE9 B 0");
        goodHits.add("4BKO A 110");
        goodHits.add("1BMF D 294");
        goodHits.add("3C5S D 106");
        goodHits.add("3C6S H 106");
        goodHits.add("1C8Y A 2");
        goodHits.add("4CAD K 116");
        goodHits.add("3CBC B 60");
        goodHits.add("3CFD B 111");
        goodHits.add("5CHO A 35");
        goodHits.add("5CHO E 32");
        goodHits.add("5CP7 B 104");
        goodHits.add("1D1S B 284");
        goodHits.add("1D1T C 284");
        goodHits.add("5DBJ A 244");
        goodHits.add("2DTM H 110");
        goodHits.add("3DUR B 112");
        goodHits.add("3DUS B 112");
        goodHits.add("3DUU D 112");
        goodHits.add("3DV6 B 112");
        goodHits.add("2E7W A 64");
        goodHits.add("4EDJ A 76");
        goodHits.add("1EFR F 294");
        goodHits.add("3ET9 F 218");
        goodHits.add("4F2M A 113");
        goodHits.add("3FDS C 63");
        goodHits.add("1FJ1 D 104");
        goodHits.add("1FL3 A 104");
        goodHits.add("1FL5 H 110");
        goodHits.add("1FL6 H 110");
        goodHits.add("4G69 B 1");
        goodHits.add("4GAY A 106");
        goodHits.add("3GGW D 106");
        goodHits.add("4GQP H 109");
        goodHits.add("1H8H D 294");
        goodHits.add("1H8H F 294");
        goodHits.add("2HE2 A 97");
        goodHits.add("2HE2 B 96");
        goodHits.add("3HE3 C 290");
        goodHits.add("2HII A 63");
        goodHits.add("5HK5 F 92");
        goodHits.add("5HK8 C 29");
        goodHits.add("1HTB A 285");
        goodHits.add("4HX6 F 48");
        goodHits.add("2I1N B 97");
        goodHits.add("5I8C A 113");
        goodHits.add("5I8H E 113");
        goodHits.add("2IDK C 79");
        goodHits.add("1ITW D 210");

        return goodHits;
    }

}