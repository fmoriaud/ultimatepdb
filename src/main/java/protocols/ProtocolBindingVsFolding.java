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
package protocols;

import database.HashTablesTools;
import database.HitInSequenceDb;
import database.SequenceTools;
import mystructure.*;
import parameters.AlgoParameters;
import shape.ShapeContainerIfc;
import shape.ShapeContainerWithPeptide;
import shapeBuilder.ShapeBuildingException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.FileHandler;

public class ProtocolBindingVsFolding {
    //-------------------------------------------------------------
    // Class variables
    //-------------------------------------------------------------
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
        protocol.run();
    }


    // -------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------
    private void run() throws ParsingConfigFileException {

        algoParameters = ProtocolTools.prepareAlgoParameters();

        FileHandler fh = null;
        try {
            fh = new FileHandler(algoParameters.getPATH_TO_RESULT_FILES() + ControllerLoger.LOGGER_FILE_NAME);
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

        List<char[]> sequenceToFind = null;
        if (queryShape instanceof ShapeContainerWithPeptide) {
            ShapeContainerWithPeptide shapeContainerWithPeptide = (ShapeContainerWithPeptide) queryShape;
            sequenceToFind = shapeContainerWithPeptide.getPeptideSequence();
        } else {
            System.out.println("queryShape is not instanceof ShapeContainerWithPeptide. Program terminated.");
            System.exit(0);
        }
        // Find same sequence occurences in sequence DB
        //String sequenceToFind = String.valueOf(sequenceToFind);
        //"LYSGLNTHRSERVAL"; // 1be9
        //String sequenceToFind = "HISLYSILELEUHISARGLEULEUGLNASPSER"; // 3erd
        // String sequenceToFind = "METPHESERILEASPASNILELEUALA";
        // Only hit in DB is 2Q14 ILE, TYR, SER, ILE, GLU, ASN, PHE, LEU, THR
        // And it is a hit which not fit in the target following minimization
        //String sequenceToFind = "METPHESERILE";

        String sequenceToFindAsString = ProtocolTools.makeSequenceString(sequenceToFind);

        int peptideLength = sequenceToFindAsString.length() / 3;

        //int minLength = targetDefinedBySegmentOfChainBasedOnSequenceMotif.getMinLength();
        //int maxLength = targetDefinedBySegmentOfChainBasedOnSequenceMotif.getMaxLength();
        boolean useSimilarSequences = false;

        List<HitInSequenceDb> hitsInDatabase = SequenceTools.find(HashTablesTools.tableSequenceName, HashTablesTools.tableSequenceFailureName, peptideLength, 1000, sequenceToFindAsString, useSimilarSequences);


        if (queryShape instanceof ShapeContainerWithPeptide) {

            ShapeContainerWithPeptide query = (ShapeContainerWithPeptide) queryShape;
            MyChainIfc ligand = query.getPeptide();
            List<HitInSequenceDb> hitsInDatabaseUsingInteractions = SequenceTools.findUsingQueryPeptide(ligand, peptideLength, 1000, sequenceToFindAsString, algoParameters);
            System.out.println("Found " + hitsInDatabaseUsingInteractions.size() + "  sequence hits in the Sequence Database using contacts");
            System.out.println("Found " + hitsInDatabase.size() + "  sequence hits in the Sequence Database using equivalent");

            hitsInDatabase = hitsInDatabaseUsingInteractions;
        }

        String fourLetterCodeTarget;
        String chainIdFromDB;

        //List<HitInSequenceDb> hitsEnrichedOnTop = putGoodHitsOnTopOfList(hitsInDatabase, getHitWithGoodRmsdBackbone());

        /*
        for (HitInSequenceDb hitInSequenceDb : hitsInDatabase) {
            if (hitInSequenceDb.getFourLetterCode().toLowerCase().equals("5cuf") || hitInSequenceDb.getFourLetterCode().toLowerCase().equals("3cfe")){
                System.out.println(hitInSequenceDb.toString());
            }
        }
        */
        ProtocolTools.executeComparisons(queryShape, peptideLength, hitsInDatabase, algoParameters);

        System.out.println("Program finished.");
        System.exit(0);
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