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

import io.IOTools;
import mystructure.MyChainIfc;
import mystructure.MyMonomerType;
import mystructure.MyStructureIfc;
import parameters.AlgoParameters;

import java.sql.*;

public class DatabaseTools {
    //-------------------------------------------------------------
    // Static variables
    //-------------------------------------------------------------
    public static final int maxCharInVarchar = 30000;
    private static String dbURL = "jdbc:derby:myDB;create=true;user=me;password=mine";
    private static Connection connection = null;


    //-------------------------------------------------------------
    // Static methods
    //-------------------------------------------------------------
    public static Connection getConnection() {

        if (connection != null) {
            return connection;
        }

        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            //Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();

            connection = DriverManager.getConnection(dbURL);
            return connection;
        } catch (Exception except) {
            except.printStackTrace();
        }
        return null;
    }


    public static Connection getNewConnection() {

        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            //Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();

            Connection newConnection = DriverManager.getConnection(dbURL);
            return newConnection;
        } catch (Exception except) {
            except.printStackTrace();
        }
        return null;
    }


    public static void shutdown() {
        try {
            if (connection != null) {
                //DriverManager.getConnection("jdbc:derby:;shutdown=true");

                //DriverManager.getConnection(dbURL + ";shutdown=true");
                DriverManager.getConnection(dbURL);
                connection.close();
                connection = null;
            }
        } catch (SQLException sqlExcept) {
            //sqlExcept.printStackTrace();
        }
    }


    public static void createDBandTableSequence(Connection connection, String sequenceTableName) {

        try {
            Statement stmt = connection.createStatement();
            String sql = "DROP TABLE " + sequenceTableName;
            stmt.execute(sql);
            System.out.println("Drop all tables from myDB " + sequenceTableName + " !");
            stmt.close();
        } catch (SQLException e1) {
            System.out.println("Table sequence is not existing so cannot be droped");
        }

        // check if table exists if not create it
        try {
            Statement stmt = connection.createStatement();
            //String createTableSql = "CREATE TABLE " + sequenceTableName + " (fourLettercode varchar(4), chainId varchar(1), sequenceString varchar(" + maxCharInVarchar + "), lastmodificationtime timestamp )";
            String createTableSql = "CREATE TABLE " + sequenceTableName + " (fourLettercode varchar(4), chainId varchar(2), chainType varchar(2), "
                    + "sequenceString varchar(" + maxCharInVarchar + "), PRIMARY KEY (fourLettercode, chainId) ) ";
            //System.out.println(createTableSql);
            stmt.executeUpdate(createTableSql);
            System.out.println("created table " + sequenceTableName + " in myDB !");
            stmt.close();
        } catch (SQLException e1) {
            System.out.println("Table " + sequenceTableName + " already exists in myDB !");
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



    /**
     * Analyse a PDB file and enter in DB
     * If no chains are found an empty entry is added with the four letter code
     * @param connexion
     * @param override
     * @param fourLetterCode
     * @param algoParameters
     * @param sequenceTableName
     * @return
     */
    public static boolean addInSequenceDB(Connection connexion, boolean override, String fourLetterCode, AlgoParameters algoParameters, String sequenceTableName) {

        boolean alreadyFound = isFourLetterCodeAlreadyFoundInDB(connexion, fourLetterCode, sequenceTableName);
        if (alreadyFound == true && override == false) {
            return false;
        }

        if (alreadyFound == true && override == true) {
            removeAllEntriesForThisFourLetterCode(connexion, fourLetterCode, sequenceTableName);
        }

        String fourLetterCodeLowerCase = fourLetterCode.toLowerCase();
        MyStructureIfc myStructure = IOTools.getMyStructureIfc(algoParameters, fourLetterCodeLowerCase.toCharArray());

        if (myStructure == null) {
            return false;
        }
        MyChainIfc[] chainsForShapeBuilding = myStructure.getAllChainsRelevantForShapeBuilding();

        int numberChainsForThisPDB = 0;

        Chains:
        for (MyChainIfc chain : chainsForShapeBuilding) {

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

            if (sequence.length() > DatabaseTools.maxCharInVarchar) {
                String truncatedSequence = sequence.substring(0, DatabaseTools.maxCharInVarchar);
                sequence = truncatedSequence;
            }

            numberChainsForThisPDB += enterInDb(connexion, fourLetterCode, sequenceTableName, numberChainsForThisPDB, chainType, chainName, sequence);
        }
        if (numberChainsForThisPDB == 0){
            enterInDb(connexion, fourLetterCode, sequenceTableName, numberChainsForThisPDB, "".toCharArray(), "".toCharArray(), "");
        }
        return true;
    }


    private static int enterInDb(Connection connexion, String fourLetterCode, String sequenceTableName, int numberChainsForThisPDB, char[] chainType, char[] chainName, String sequence) {
        try {
            String insertTableSQL = "INSERT INTO " + sequenceTableName + " "
                    + "(fourLettercode, chainId, chainType, sequenceString) VALUES"
                    + "(?,?,?,?)";
            PreparedStatement preparedStatement = connexion.prepareStatement(insertTableSQL);
            preparedStatement.setString(1, String.valueOf(fourLetterCode));
            preparedStatement.setString(2, String.valueOf(chainName));
            preparedStatement.setString(3, String.valueOf(chainType));
            preparedStatement.setString(4, sequence);

            int ok = preparedStatement.executeUpdate();
            preparedStatement.close();
            System.out.println(ok + " raw created " + String.valueOf(fourLetterCode) + "  " + String.valueOf(chainName) + "  " + String.valueOf(chainType)); // + " " + sequence);
            numberChainsForThisPDB += 1;
        } catch (SQLException e1) {
            System.out.println("Failed to enter entry in " + sequenceTableName + " table ");
            return 0;
        }
        return 1;
    }


    public static boolean isFourLetterCodeAlreadyFoundInDB(Connection connexion, String fourLetterCode, String sequenceTableName) {

        Statement stmt = null;
        try {
            stmt = connexion.createStatement();
        } catch (SQLException e) {
            return false;
        }
        String findEntry = "SELECT * from " + sequenceTableName + " WHERE fourLettercode = '" + fourLetterCode + "'";

        ResultSet resultFindEntry = null;
        try {
            resultFindEntry = stmt.executeQuery(findEntry);
        } catch (SQLException e) {
            return false;
        }
        int foundEntriesCount = 0;

        try {
            if (resultFindEntry.next()) {
                foundEntriesCount += 1;
            }
        } catch (SQLException e) {
            try {
                stmt.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            return false;
        }

        if (foundEntriesCount != 0) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }


    public static boolean removeAllEntriesForThisFourLetterCode(Connection connexion, String fourLetterCode, String sequenceTableName) {

        Statement stmt = null;
        try {
            stmt = connexion.createStatement();
        } catch (SQLException e) {
            return false;
        }
        String deleteEntry = "DELETE * from " + sequenceTableName + " WHERE fourLettercode = '" + fourLetterCode + "'";

        ResultSet value = null;
        try {
            value = stmt.executeQuery(deleteEntry);
            stmt.close();
        } catch (SQLException e) {
            try {
                stmt.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            return false;
        }
        return true;
    }
}