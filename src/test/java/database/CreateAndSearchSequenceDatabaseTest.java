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

import io.Tools;
import org.junit.Ignore;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CreateAndSearchSequenceDatabaseTest {

    @Test
    public void testCreateDatabaseHashTest() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();
        CreateAndSearchSequenceDatabase createAndSearchSequenceDatabase = new CreateAndSearchSequenceDatabase(algoParameters, HashTablesTools.tableSequenceTestName, HashTablesTools.tableSequenceFailureTestName);
        createAndSearchSequenceDatabase.createDatabase();
        createAndSearchSequenceDatabase.shutdownDb();

        System.out.println();
        System.out.println("getContentInfosTestDB");

        Connection connexion = HashTablesTools.getConnection(HashTablesTools.tableSequenceTestName, HashTablesTools.tableSequenceFailureTestName);
        Statement stmt = null;
        try {
            stmt = connexion.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String findEntrySequenceDb = "SELECT * from " + HashTablesTools.tableSequenceTestName;

        ResultSet resultFindEntry = null;
        try {
            resultFindEntry = stmt.executeQuery(findEntrySequenceDb);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Set<String> uniqueHashCode = new HashSet<>();
        Set<String> uniqueFourLetterCode = new HashSet<>();
        int entriesCountSequenceDb = 0;
        try {
            while (resultFindEntry.next()) {

                String hashCode = resultFindEntry.getString(1);
                String fourLetterCode = resultFindEntry.getString(2);
                uniqueFourLetterCode.add(fourLetterCode);
                uniqueHashCode.add(hashCode);

                entriesCountSequenceDb += 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        stmt = null;
        try {
            stmt = connexion.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String findEntrySequenceFailureDb = "SELECT * from " + HashTablesTools.tableSequenceFailureTestName;

        resultFindEntry = null;
        try {
            resultFindEntry = stmt.executeQuery(findEntrySequenceFailureDb);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int entriesCountSequenceFailureDb = 0;
        try {
            while (resultFindEntry.next()) {

                entriesCountSequenceFailureDb += 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(HashTablesTools.tableSequenceTestName + " :");
        System.out.println("Total entries hash & chain = " + entriesCountSequenceDb);
        System.out.println("uniqueFourLetterCode = " + uniqueFourLetterCode.size());
        System.out.println("uniqueHashCode = " + uniqueHashCode.size());
        System.out.print("entriesCountSequenceFailureDb = " + entriesCountSequenceFailureDb);

        assertEquals(uniqueFourLetterCode.size(), uniqueHashCode.size());
        HashTablesTools.shutdown();
    }
}

