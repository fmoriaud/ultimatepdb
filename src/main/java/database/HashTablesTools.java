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
package database;

import io.FileListingVisitorForPDBCifGzFiles;
import io.IOTools;
import io.MMcifFileInfos;
import mystructure.MyChainIfc;
import mystructure.MyMonomerType;
import mystructure.MyStructureIfc;
import org.apache.commons.math3.util.Pair;
import parameters.AlgoParameters;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.*;

public class HashTablesTools {
    //-------------------------------------------------------------
    // Static variables
    //-------------------------------------------------------------
    private static String dbURL = "jdbc:derby:myDB;create=true;user=me;password=mine";
    private static Connection connection = null;
    public static String tableSequenceName = "sequence";
    public static String tableSequenceTestName = "sequenceTest";
    public static String tableSequenceFailureName = "sequenceFailure";
    public static String tableSequenceFailureTestName = "sequenceFailureTest";
    public static final int maxCharInVarchar = 30000;

    //-------------------------------------------------------------
    // Static methods
    //-------------------------------------------------------------
    public static Connection getConnection(String tableName, String tableFailureName) {

        if (connection != null) {
            return connection;
        }

        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            //Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();

            connection = DriverManager.getConnection(dbURL);

            // if table dont exist then create them
            createTablesIfTheyDontExists(connection, tableName, tableFailureName);
            return connection;
        } catch (Exception except) {
            except.printStackTrace();
        }
        return null;
    }


    public static void createTablesIfTheyDontExists(Connection connection, String tableName, String tableFailureName) {

        ResultSet resultTables = null;
        try {
            Statement stmt = connection.createStatement();
            String findEntry = "SELECT TABLENAME FROM SYS.SYSTABLES WHERE TABLETYPE='T'";
            resultTables = stmt.executeQuery(findEntry);
            if (resultTables.next()) {
                System.out.println("tables exists");
            } else {
                System.out.println("tables dont exists");
                createTables(connection, tableName, tableFailureName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void shutdown() {
        try {
            if (connection != null) {
                DriverManager.getConnection(dbURL);
                connection.close();
                connection = null;
            }
        } catch (SQLException sqlExcept) {
            //sqlExcept.printStackTrace();
        }
    }


    public static String getMD5hash(String pathToFile) throws NoSuchAlgorithmException, IOException {

        byte[] b = Files.readAllBytes(Paths.get(pathToFile));
        byte[] hash = MessageDigest.getInstance("MD5").digest(b);
        String actual = DatatypeConverter.printHexBinary(hash);
        return actual;
    }


    public static int countFilesWhichAreAlreadyIndexedInSequenceDB(String tableName, String tableFailureName, Map<String, List<MMcifFileInfos>> indexPDBFileInFolder) {

        Connection connection = HashTablesTools.getConnection(tableName, tableFailureName);

        // build all hash
        //System.out.println("starting hash list");
        List<String> filesHash = new ArrayList<>();
        for (Map.Entry<String, List<MMcifFileInfos>> entry : indexPDBFileInFolder.entrySet()) {
            for (MMcifFileInfos fileInfos : entry.getValue()) {
                filesHash.add(fileInfos.getHash());

            }
        }
        //System.out.println("finished hash list " + filesHash.size());
        ResultSet resultFindEntryFailureDb = null;
        try {
            Statement stmt = connection.createStatement();
            String findEntry = "SELECT * from " + tableFailureName;
            resultFindEntryFailureDb = stmt.executeQuery(findEntry);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        ResultSet resultFindEntrySequenceDb = null;
        try {
            Statement stmt = connection.createStatement();
            String findEntry = "SELECT * from " + tableName;
            resultFindEntrySequenceDb = stmt.executeQuery(findEntry);

        } catch (SQLException e) {
            e.printStackTrace();
        }


        int countOfFilesAlreadyFoundInFailureHashDb = 0;
        int countOfFilesAlreadyFoundInSequenceDb = 0;

        try {
            System.out.println("starting hgo through failure db");
            while (resultFindEntryFailureDb.next()) {

                String hash = resultFindEntryFailureDb.getString(1);
                if (filesHash.contains(hash)) {
                    // then it is found
                    countOfFilesAlreadyFoundInFailureHashDb += 1;
                }
            }
            System.out.println("starting hgo through sequence db");
            Set<String> uniqueHash = new HashSet<>();

            while (resultFindEntrySequenceDb.next()) {

                String hash = resultFindEntrySequenceDb.getString(1);
                if (filesHash.contains(hash)) {
                    // then it is found
                    uniqueHash.add(hash);

                }
            }
            countOfFilesAlreadyFoundInSequenceDb = uniqueHash.size();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return countOfFilesAlreadyFoundInFailureHashDb + countOfFilesAlreadyFoundInSequenceDb;
    }


    public static void createTables(Connection connection, String tableName, String tableFailureName) {

        dropTable(connection, tableName);
        dropTable(connection, tableFailureName);

        // check if table exists if not create it
        // E95A91AD32BBFB2C7ACCC5E75F48686F
        try {
            Statement stmt = connection.createStatement();
            String createTableSql = "CREATE TABLE " + tableName + " (pdbfilehash varchar(32), fourLettercode varchar(4), chainId varchar(2), chainType varchar(2), sequenceString varchar(" + maxCharInVarchar + "), PRIMARY KEY (pdbfilehash, chainId) ) ";
            System.out.println(createTableSql);
            stmt.executeUpdate(createTableSql);
            System.out.println("created table " + tableName + " in myDB !");
            stmt.close();
        } catch (SQLException e1) {
            System.out.println("Table " + tableName + " already exists in myDB !");
        }
        try {
            Statement stmt = connection.createStatement();
            String createTableSql = "CREATE TABLE " + tableFailureName + " (pdbfilehash varchar(32), fourLettercode varchar(4), PRIMARY KEY (pdbfilehash) ) ";
            System.out.println(createTableSql);
            stmt.executeUpdate(createTableSql);
            System.out.println("created table " + tableName + " in myDB !");
            stmt.close();
        } catch (SQLException e1) {
            System.out.println("Table " + tableName + " already exists in myDB !");
        }
    }


    private static void insertIntoFailureTable(Connection connexion, String tableFailureName, String hash, String fourLetterCode) {

        try {
            String insertTableSQL = "INSERT INTO " + tableFailureName + " "
                    + "(pdbfilehash, fourLettercode) VALUES"
                    + "(?,?)";
            PreparedStatement preparedStatement = connexion.prepareStatement(insertTableSQL);
            preparedStatement.setString(1, hash);
            preparedStatement.setString(2, String.valueOf(fourLetterCode));

            int ok = preparedStatement.executeUpdate();
            preparedStatement.close();
            System.out.println(ok + " raw created in failure table" + String.valueOf(fourLetterCode));
        } catch (SQLException e1) {
            System.out.println("Failed to enter entry in " + tableFailureName + " table ");
        }
    }


    public static boolean addAFile(String pathToFile, String fourLetterCode, Connection connexion, String tableName, String tableFailureName, AlgoParameters algoParameters) throws IOException, NoSuchAlgorithmException {

        String hash = HashTablesTools.getMD5hash(pathToFile);

        // TODO add check to failure db
        boolean alreadyParsed = false;
        try {
            alreadyParsed = fileAlreadyParsedDetectedByHash(connexion, tableName, tableFailureName, hash);
        } catch (SQLException e) {
            return false;
        }
        if (alreadyParsed == true) {
            System.out.println("Already parsed in DB, nothing to do");
            return false;
        }
        Pair<String, MyStructureIfc> pairPathMyStructure = IOTools.getMyStructureIfc(algoParameters, pathToFile);

        if (pairPathMyStructure == null || pairPathMyStructure.getValue() == null) {
            insertIntoFailureTable(connexion, tableFailureName, hash, fourLetterCode);
            return false;
        }
        MyChainIfc[] chainsForShapeBuilding = pairPathMyStructure.getValue().getAllChainsRelevantForShapeBuilding();

        int countEntries = 0;
        Chains: for (MyChainIfc chain : chainsForShapeBuilding) {

            MyMonomerType monomerType = MyMonomerType.getEnumType(chain.getMyMonomers()[0].getType());
            char[] chainType = "  ".toCharArray();
            if (monomerType.equals(MyMonomerType.AMINOACID)) {
                chainType = "AA".toCharArray();
            }
            if (monomerType.equals(MyMonomerType.NUCLEOTIDE)) {
                chainType = "NU".toCharArray();
            }
            if (monomerType.equals(MyMonomerType.HETATM)) {
                continue Chains;
            }
            char[] chainName = chain.getChainId();
            String sequence = SequenceTools.generateSequence(chain);

            if (sequence.length() > HashTablesTools.maxCharInVarchar) {
                String truncatedSequence = sequence.substring(0, HashTablesTools.maxCharInVarchar);
                sequence = truncatedSequence;
            }

            countEntries += enterInPDBfilesHashDB(connexion, hash, fourLetterCode, tableName, chainType, chainName, sequence);
        }
        if (countEntries == 0) {
            insertIntoFailureTable(connexion, tableFailureName, hash, fourLetterCode);
        }
        return true;
    }

    private static int enterInPDBfilesHashDB(Connection connexion, String hash, String fourLetterCode, String tableName, char[] chainType, char[] chainName, String sequence) {
        try {
            String insertTableSQL = "INSERT INTO " + tableName + " "
                    + "(pdbfilehash, fourLettercode, chainId, chainType, sequenceString) VALUES"
                    + "(?,?,?,?,?)";
            PreparedStatement preparedStatement = connexion.prepareStatement(insertTableSQL);
            preparedStatement.setString(1, hash);
            preparedStatement.setString(2, String.valueOf(fourLetterCode));
            preparedStatement.setString(3, String.valueOf(chainName));
            preparedStatement.setString(4, String.valueOf(chainType));
            preparedStatement.setString(5, sequence);

            int ok = preparedStatement.executeUpdate();
            preparedStatement.close();
            System.out.println(ok + " raw created " + String.valueOf(fourLetterCode) + "  " + String.valueOf(chainName) + "  " + String.valueOf(chainType)); // + " " + sequence);
            return 1;
        } catch (SQLException e1) {
            System.out.println("Failed to enter entry in " + tableName + " table ");
            return 0;
        }
    }


    private static boolean fileAlreadyParsedDetectedByHash(Connection connection, String tableName, String tableFailureName, String pdbFileHash) throws SQLException {

        Statement stmt = connection.createStatement();
        String findEntry = "SELECT * from " + tableFailureName + " WHERE pdbfilehash = '" + pdbFileHash + "'";
        ResultSet resultFindEntry = stmt.executeQuery(findEntry);
        int foundEntriesCount = 0;
        String fourLetterCodeFromDB = null;
        if (resultFindEntry.next()) {
            foundEntriesCount += 1;
            fourLetterCodeFromDB = resultFindEntry.getString(2);
        }

        if (foundEntriesCount != 0) {
            //System.out.println("Found in failure table " + fourLetterCodeFromDB + "  " + foundEntriesCount);
            return true;
        }


        stmt = connection.createStatement();
        findEntry = "SELECT * from " + tableName + " WHERE pdbfilehash = '" + pdbFileHash + "'";
        resultFindEntry = stmt.executeQuery(findEntry);
        foundEntriesCount = 0;
        fourLetterCodeFromDB = null;
        String chainIdFromDB = null;
        if (resultFindEntry.next()) {
            foundEntriesCount += 1;

            fourLetterCodeFromDB = resultFindEntry.getString(2);
            chainIdFromDB = resultFindEntry.getString(3);
        }

        if (foundEntriesCount != 0) {
            //System.out.println("duplicate entry " + fourLetterCodeFromDB + "  " + chainIdFromDB + "  " + foundEntriesCount);
            return true;
        }

        return false;
    }


    private static void dropTable(Connection connection, String tableName) {
        try {
            Statement stmt = connection.createStatement();
            String sql = "DROP TABLE " + tableName;
            stmt.execute(sql);
            System.out.println("Drop table from myDB " + tableName + " !");
            stmt.close();
        } catch (SQLException e1) {
            System.out.println("Table " + tableName + " is not existing so cannot be droped");
        }
    }


    public static String returnSequenceInDbifFourLetterCodeAndChainfoundInDatabase(Connection connection, String fourLetterCode, String chainName, String sequenceTableName) {

        String sequenceInDb = null;
        try {
            Statement stmt = connection.createStatement();
            String findEntry = "SELECT * from " + sequenceTableName + " WHERE fourLettercode = '" + fourLetterCode + "' and chainId = '" + chainName + "'";
            ResultSet resultFindEntry = stmt.executeQuery(findEntry);
            int foundEntriesCount = 0;
            String fourLetterCodeFromDB;
            String chainIdFromDB;
            if (resultFindEntry.next()) {
                foundEntriesCount += 1;

                fourLetterCodeFromDB = resultFindEntry.getString(1);
                chainIdFromDB = resultFindEntry.getString(2);
                sequenceInDb = resultFindEntry.getString(4);
            }

            if (foundEntriesCount != 1) {
                System.out.println("problem isFourLetterCodeAndChainfoundInDatabase " + fourLetterCode + "  " + chainName + "  " + foundEntriesCount);
                return null;
            }
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return null;
        }
        return sequenceInDb;
    }
}
