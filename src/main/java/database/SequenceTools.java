package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import math.ToolsDistance;
import math.ToolsMath;
import mystructure.*;
import parameters.AlgoParameters;
import shapeBuilder.StructureLocalTools;

public class SequenceTools {

    /**
     * The sequence DB for protocols
     */
    public static String tableName = "sequence";

    public static String generateSequence(MyChainIfc chain) {

        StringBuffer stringBuffer = new StringBuffer();
        for (MyMonomerIfc monomer : chain.getMyMonomers()) {
            String threeLetterCode = String.valueOf(monomer.getThreeLetterCode());
            if (threeLetterCode.length() == 1) {
                threeLetterCode = "  " + threeLetterCode;
            }
            if (threeLetterCode.length() == 2) {
                threeLetterCode = " " + threeLetterCode;
            }
            if (threeLetterCode.length() > 3) {
                System.out.println("residue 3 letter code has 4 or more ...");
                System.exit(0);
            }
            stringBuffer.append(threeLetterCode);
            //stringBuffer.append(" ");
        }
        String sequence = stringBuffer.toString();
        return sequence;
    }


    public static List<String> getHydrophobicResiduesList() {

        List<String> hydrophobicResidues = new ArrayList<>();
        hydrophobicResidues.add("GLY");
        hydrophobicResidues.add("ALA");
        hydrophobicResidues.add("VAL");
        hydrophobicResidues.add("LEU");
        hydrophobicResidues.add("ILE");
        hydrophobicResidues.add("MET");
        hydrophobicResidues.add("SEM");
        hydrophobicResidues.add("PHE");
        hydrophobicResidues.add("TRP");

        return hydrophobicResidues;
    }


    public static List<String> getAllResiduesList() {

        List<String> listAllResidues = new ArrayList<>();
        listAllResidues.add("TRP");
        listAllResidues.add("PHE");
        listAllResidues.add("TYR");
        listAllResidues.add("ILE");
        listAllResidues.add("VAL");
        listAllResidues.add("LEU");
        listAllResidues.add("MET");
        listAllResidues.add("ASP");
        listAllResidues.add("GLU");
        listAllResidues.add("ALA");
        listAllResidues.add("PRO");
        listAllResidues.add("HIS");
        listAllResidues.add("LYS");
        listAllResidues.add("ARG");
        listAllResidues.add("SER");
        listAllResidues.add("THR");
        listAllResidues.add("ASN");
        listAllResidues.add("GLN");

        return listAllResidues;
    }


    public static List<String> generateNonEquivalentResidues(String inputResidue) {

        List<String> eqResidues = generateEquivalentResidues(inputResidue);
        List<String> allResidues = getAllResiduesList();
        for (String eqRes : eqResidues) {
            allResidues.remove(eqRes);
        }
        return allResidues;
    }


    public static List<String> generateEquivalentResidues(String inputResidue) {

        List<String> equivalentResidues = new ArrayList<>();

        equivalentResidues.add(inputResidue);

        switch (inputResidue) {

            case "TRP":
                equivalentResidues.add("PHE");
                equivalentResidues.add("TYR");
                return equivalentResidues;

            case "PHE":
                equivalentResidues.add("TRP");
                equivalentResidues.add("TYR");
                equivalentResidues.add("ILE");
                return equivalentResidues;

            case "TYR":
                equivalentResidues.add("TRP");
                equivalentResidues.add("PHE");
                return equivalentResidues;

            case "ILE":
                equivalentResidues.add("PHE");
                equivalentResidues.add("VAL");
                equivalentResidues.add("LEU");
                equivalentResidues.add("MET");
                return equivalentResidues;

            case "VAL":
                equivalentResidues.add("ILE");
                equivalentResidues.add("ALA");
                return equivalentResidues;

            case "LEU":
                equivalentResidues.add("ILE");
                equivalentResidues.add("MET");
                return equivalentResidues;

            case "MET":
                equivalentResidues.add("ILE");
                equivalentResidues.add("LEU");
                return equivalentResidues;

            case "ASP":
                equivalentResidues.add("GLU");
                return equivalentResidues;

            case "GLU":
                equivalentResidues.add("ASP");
                return equivalentResidues;

            case "ALA":
                equivalentResidues.add("PRO");
                equivalentResidues.add("VAL");
                equivalentResidues.add("THR");
                return equivalentResidues;

            case "PRO":
                equivalentResidues.add("ALA");
                return equivalentResidues;

            case "HIS":
                equivalentResidues.add("LYS");
                equivalentResidues.add("ARG");
                return equivalentResidues;

            case "LYS":
                equivalentResidues.add("HIS");
                equivalentResidues.add("ARG");
                return equivalentResidues;

            case "ARG":
                equivalentResidues.add("HIS");
                equivalentResidues.add("LYS");
                return equivalentResidues;

            case "SER":
                equivalentResidues.add("THR");
                return equivalentResidues;

            case "THR":
                equivalentResidues.add("SER");
                equivalentResidues.add("ALA");
                return equivalentResidues;

            case "ASN":
                equivalentResidues.add("GLN");
                return equivalentResidues;

            case "GLN":
                equivalentResidues.add("ASN");
                return equivalentResidues;

        }

        return equivalentResidues;
    }


    public static List<List<String>> generateEquivalentResidues() {

        List<List<String>> equivalentResidues = new ArrayList<>();

        List<String> list1 = getHydrophobicResiduesList();
        equivalentResidues.add(list1);

        List<String> list2 = new ArrayList<>();
        list2.add("SER");
        list2.add("CYS");
        list2.add("THR");
        equivalentResidues.add(list2);

        List<String> list3 = new ArrayList<>();
        list3.add("PHE");
        list3.add("TYR");
        list3.add("TRP");
        equivalentResidues.add(list3);

        List<String> list4 = new ArrayList<>();
        list4.add("HIS");
        list4.add("LYS");
        list4.add("ARG");
        equivalentResidues.add(list4);

        List<String> list5 = new ArrayList<>();
        list5.add("ASP");
        list5.add("ASJ");
        list5.add("GLU");
        list5.add("ASN");
        list5.add("GLN");
        equivalentResidues.add(list5);

        return equivalentResidues;
    }


    public static List<HitInSequenceDb> findUsingQueryPeptide(MyChainIfc queryPeptide, int minLength, int maxLength, String sequenceToFind, AlgoParameters algoParameters) {

        List<HitInSequenceDb> hitsInSequenceDb = new ArrayList<>();

        List<QueryMonomerToTargetContactType> contacts = findContacts(queryPeptide, algoParameters);

        List<String> listFourLetterCodeFromDB = new ArrayList<>();
        List<String> listChainIdFromDB = new ArrayList<>();
        List<String> listSequence = new ArrayList<>();

        Connection connexion = DatabaseTools.getNewConnection();
        Statement stmt;
        try {
            stmt = connexion.createStatement();
            String findEntry = "SELECT * from " + SequenceTools.tableName;
            ResultSet resultFindEntry = stmt.executeQuery(findEntry);

            while (resultFindEntry.next()) {

                // check if all ok

                listFourLetterCodeFromDB.add(resultFindEntry.getString(1));
                listChainIdFromDB.add(resultFindEntry.getString(2));
                listSequence.add(resultFindEntry.getString(4));

                if (listSequence.size() != listChainIdFromDB.size() ||
                        listSequence.size() != listFourLetterCodeFromDB.size() ||
                        listChainIdFromDB.size() != listFourLetterCodeFromDB.size()
                        ) {
                    System.out.println("big pb in FinSequenceInDatabaseTools.find() Terminating program");
                    System.out.println();
                    System.exit(0);
                }
            }
        } catch (SQLException e1) {
            System.out.println("Exception in reading whole content of DB. Program terminated");
            System.exit(0);

        }

        int sequenceToFindLength = sequenceToFind.length() / 3;

        for (int i = 0; i < listSequence.size(); i++) {
            String fourLetterCode = listFourLetterCodeFromDB.get(i);


            String chainIdFromDB = listChainIdFromDB.get(i);
            String sequenceFromDB = listSequence.get(i);

            int peptideLength = sequenceFromDB.length() / 3;
            if (peptideLength < minLength || peptideLength > maxLength) {
                continue;
            }

            List<Integer> rankIdList = findRankId(sequenceToFind, sequenceFromDB, contacts);

            if (rankIdList.size() != 0) {

                HitInSequenceDb HitInSequenceDb = new HitInSequenceDb(rankIdList, fourLetterCode, chainIdFromDB, sequenceToFindLength);
                hitsInSequenceDb.add(HitInSequenceDb);
            }
        }

        DatabaseTools.shutdown();
        return hitsInSequenceDb;
    }


    public static List<QueryMonomerToTargetContactType> findContacts(MyChainIfc queryPeptide, AlgoParameters algoParameters) {

        List<QueryMonomerToTargetContactType> contacts = new ArrayList<>();

        // check if monomer has a close contact with backbone atoms
        // 4.5 is maybe too long. 2.5 not enough as not protonated

        // TODO Doesnt work because queryPeptide from shapecontainer was cloned and has no neighbors in target
        // TODO run tests: if they fail because neighbors are missing then I have to find a solution to store protonated ligand
        // TODO with the neighbors which means I still clone by cleaning neighbors but in shape builder I put them back
        float interactionDistanceCutoff = 2.3f;
        //algoParameters.getDISTANCE_FROM_PEPTIDE_TO_WHICH_INTERACTINGPROTEIN_IS_SHORTENED();

        Set<MyMonomerIfc> myMonomerNeighborsByDistanceToRepresentativeAtom = StructureLocalTools.makeMyMonomersLocalAroundAndExcludingMyMonomersFromInputMyChain(queryPeptide);

        for (MyMonomerIfc monomer : queryPeptide.getMyMonomers()) {

            QueryMonomerToTargetContactType currentType = QueryMonomerToTargetContactType.NONE;
            for (MyAtomIfc atomLigand : monomer.getMyAtoms()) {

                boolean fromBackBone = MyStructureTools.isAtomNameFromBackBone(atomLigand.getAtomName());
                //System.out.println(String.valueOf(atomLigand.getAtomName()) + " fromBackBone = " + fromBackBone);

                for (MyMonomerIfc neighborMonomer : myMonomerNeighborsByDistanceToRepresentativeAtom) {
                    for (MyAtomIfc atomTarget : neighborMonomer.getMyAtoms()) {

                        float distance = ToolsMath.computeDistance(atomLigand.getCoords(), atomTarget.getCoords());
                        if (distance < interactionDistanceCutoff) {
                            if (fromBackBone == true && currentType == QueryMonomerToTargetContactType.NONE) {
                                currentType = QueryMonomerToTargetContactType.BACKBONE_ONLY;
                            }
                            if (fromBackBone == false) { // then from sidechain
                                currentType = QueryMonomerToTargetContactType.SIDECHAIN;
                            }
                        }
                    }
                }
            }
            contacts.add(currentType);
        }

        return contacts;
    }

    /**
     * Find Hits in SequenceDB which are segment of sequence chain in the DB
     *
     * @param minLength           min length of sequence chain from the DB
     * @param maxLength           max length of sequence chain from the DB
     * @param sequenceToFind      input sequence as a string of concatenated three letter codes
     * @param useSimilarSequences if true then equivalent residues are considered in the matching
     * @return list of Hit in the DB which have a rankid which is the same as the startingid to make segment of chain
     */
    public static List<HitInSequenceDb> find(int minLength, int maxLength, String sequenceToFind, boolean useSimilarSequences) {

        List<HitInSequenceDb> hitsInSequenceDb = new ArrayList<>();

        List<String> listFourLetterCodeFromDB = new ArrayList<>();
        List<String> listChainIdFromDB = new ArrayList<>();
        List<String> listSequence = new ArrayList<>();

        Connection connexion = DatabaseTools.getNewConnection();
        Statement stmt;
        try {
            stmt = connexion.createStatement();
            String findEntry = "SELECT * from " + SequenceTools.tableName;
            ResultSet resultFindEntry = stmt.executeQuery(findEntry);

            while (resultFindEntry.next()) {

                // check if all ok

                listFourLetterCodeFromDB.add(resultFindEntry.getString(1));
                listChainIdFromDB.add(resultFindEntry.getString(2));
                listSequence.add(resultFindEntry.getString(4));

                if (listSequence.size() != listChainIdFromDB.size() ||
                        listSequence.size() != listFourLetterCodeFromDB.size() ||
                        listChainIdFromDB.size() != listFourLetterCodeFromDB.size()
                        ) {
                    System.out.println("big pb in FinSequenceInDatabaseTools.find() Terminating program");
                    System.out.println();
                    System.exit(0);
                }
            }
        } catch (SQLException e1) {
            System.out.println("Exception in reading whole content of DB. Program terminated");
            System.exit(0);

        }

        int sequenceToFindLength = sequenceToFind.length() / 3;

        for (int i = 0; i < listSequence.size(); i++) {
            String fourLetterCode = listFourLetterCodeFromDB.get(i);


            String chainIdFromDB = listChainIdFromDB.get(i);
            String sequenceFromDB = listSequence.get(i);

            int peptideLength = sequenceFromDB.length() / 3;
            if (peptideLength < minLength || peptideLength > maxLength) {
                continue;
            }

            List<Integer> rankIdList = findRankId(sequenceToFind, sequenceFromDB, useSimilarSequences);

            if (rankIdList.size() != 0) {

                HitInSequenceDb HitInSequenceDb = new HitInSequenceDb(rankIdList, fourLetterCode, chainIdFromDB, sequenceToFindLength);
                hitsInSequenceDb.add(HitInSequenceDb);
            }
        }

        DatabaseTools.shutdown();
        return hitsInSequenceDb;
    }

    public static List<Integer> findRankId(String sequenceToFind, String chainSequence, List<QueryMonomerToTargetContactType> contacts) {

        List<Integer> listMatchingRankId = new ArrayList<>();

        // split chain sequence into three letter codes
        // put the three letters in a list
        List<String> splitSequenceToFind = splitIntoThreeLetterCode(sequenceToFind);
        List<String> splitChainSequence = splitIntoThreeLetterCode(chainSequence);

        // Definition of equivalent
        //List<List<String>> equivalentResidues = SequenceTools.generateEquivalentResidues();


        // go through
        A:
        for (int rankId = 0; rankId < splitChainSequence.size(); rankId++) {
            for (int i = 0; i < splitSequenceToFind.size(); i++) {

                QueryMonomerToTargetContactType contactType = contacts.get(i);

                // is that residue match the first of the sequenceToFind
                if (rankId + i >= splitChainSequence.size()) {
                    // reach end of chain so no match
                    continue A;
                }
                String currentResidueFromChain = splitChainSequence.get(rankId + i);
                String currentResidueFromSequenceToFind = splitSequenceToFind.get(i);

                if (currentResidueFromSequenceToFind.equals("XXX")) {
                    // match for sure
                    continue;
                }
                // check if

                List<String> possibleEquivalent = new ArrayList<>();
                if (contactType.equals(QueryMonomerToTargetContactType.SIDECHAIN)) {
                    possibleEquivalent.add(currentResidueFromSequenceToFind);
                }
                if (contactType.equals(QueryMonomerToTargetContactType.NONE) || contactType.equals(QueryMonomerToTargetContactType.BACKBONE_ONLY)) {
                    // keeps empty the all is possible
                }


                //  possibleEquivalent = SequenceTools.generateEquivalentResidues(currentResidueFromSequenceToFind);
                //} else {
                //   possibleEquivalent = new ArrayList<>();
                //   possibleEquivalent.add(currentResidueFromSequenceToFind);
                // }

                if (possibleEquivalent.isEmpty() || possibleEquivalent.contains(currentResidueFromChain)) {

                    // then we have a match for current i
                    if (i == splitSequenceToFind.size() - 1) {
                        // we have a match here !!!
                        //System.out.println("match !!!");
                        listMatchingRankId.add(rankId);
                    }
                    continue;

                } else {
                    // we have no match so moveon
                    continue A;
                }
            }
            rankId += 1;
        }

        return listMatchingRankId;
    }


    public static List<Integer> findRankId(String sequenceToFind, String chainSequence, boolean useSimilarSequences) {

        List<Integer> listMatchingRankId = new ArrayList<>();

        // split chain sequence into three letter codes
        // put the three letters in a list
        List<String> splitSequenceToFind = splitIntoThreeLetterCode(sequenceToFind);
        List<String> splitChainSequence = splitIntoThreeLetterCode(chainSequence);

        // Definition of equivalent
        //List<List<String>> equivalentResidues = SequenceTools.generateEquivalentResidues();


        // go through
        A:
        for (int rankId = 0; rankId < splitChainSequence.size(); rankId++) {
            for (int i = 0; i < splitSequenceToFind.size(); i++) {

                // is that residue match the first of the sequenceToFind
                if (rankId + i >= splitChainSequence.size()) {
                    // reach end of chain so no match
                    continue A;
                }
                String currentResidueFromChain = splitChainSequence.get(rankId + i);
                String currentResidueFromSequenceToFind = splitSequenceToFind.get(i);

                if (currentResidueFromSequenceToFind.equals("XXX")) {
                    // match for sure
                    continue;
                }
                // check if
                List<String> possibleEquivalent = null;
                if (useSimilarSequences == true) {

                    // TODO Here the logic is different if I use the contacts

                    possibleEquivalent = SequenceTools.generateEquivalentResidues(currentResidueFromSequenceToFind);
                } else {
                    possibleEquivalent = new ArrayList<>();
                    possibleEquivalent.add(currentResidueFromSequenceToFind);
                }
                if (possibleEquivalent.contains(currentResidueFromChain)) {

                    // then we have a match for current i
                    if (i == splitSequenceToFind.size() - 1) {
                        // we have a match here !!!
                        //System.out.println("match !!!");
                        listMatchingRankId.add(rankId);
                    }
                    continue;

                } else {
                    // we have no match so moveon
                    continue A;
                }
            }
            rankId += 1;
        }

        return listMatchingRankId;
    }


    // -------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------
    private static List<String> splitIntoThreeLetterCode(String sequenceToFind) {
        // put the three letters in a list
        List<String> listThreeLetterCode = new ArrayList<>();
        int countResidue = sequenceToFind.length() / 3;
        for (int i = 0; i < countResidue; i++) {
            int start = i * 3;
            String threeLetterCode = sequenceToFind.substring(start, start + 3);
            listThreeLetterCode.add(threeLetterCode);
        }
        return listThreeLetterCode;
    }
}