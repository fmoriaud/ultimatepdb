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
import io.Tools;
import org.junit.Ignore;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CurrentDbGetInfos {

    @Ignore
    @Test
    public void getContentInfosBigDB() throws IOException, ParsingConfigFileException {

        System.out.println();
        System.out.println("getContentInfosBigDB");

        Connection connexion = DatabaseTools.getConnection();

        Statement stmt = null;
        try {
            stmt = connexion.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String findEntry = "SELECT * from " + SequenceTools.tableName;

        ResultSet resultFindEntry = null;
        try {
            resultFindEntry = stmt.executeQuery(findEntry);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Set<String> uniqueFourLetterCode = new HashSet<>();
        int entriesCount = 0;
        try {
            while (resultFindEntry.next()) {

                String fourLetterCode = resultFindEntry.getString(1);
                uniqueFourLetterCode.add(fourLetterCode);

                entriesCount += 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("uniqueFourLetterCode count = " + uniqueFourLetterCode.size());
        System.out.println("total entries count = " + entriesCount);

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        Map<String, List<Path>> indexPDBFileInFolder = IOTools.indexPDBFileInFolder(algoParameters.getPATH_TO_REMEDIATED_PDB_MMCIF_FOLDER());

        System.out.println("PDB Four Letter codes in Big DB count = " + indexPDBFileInFolder.size());

        // TODO Add test to see how many of intra chains of length 5 ?
        // But that wont incude if invalid residue with not all atoms ?

        DatabaseTools.shutdown();
    }


    @Test
    public void getContentInfosTestDB() throws IOException, ParsingConfigFileException {

        System.out.println();
        System.out.println("getContentInfosTestDB");
        Connection connexion = DatabaseTools.getConnection();

        Statement stmt = null;
        try {
            stmt = connexion.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String findEntry = "SELECT * from " + Tools.testTableName;

        ResultSet resultFindEntry = null;
        try {
            resultFindEntry = stmt.executeQuery(findEntry);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Set<String> uniqueFourLetterCode = new HashSet<>();
        int entriesCount = 0;
        try {
            while (resultFindEntry.next()) {

                String fourLetterCode = resultFindEntry.getString(1);
                uniqueFourLetterCode.add(fourLetterCode);

                entriesCount += 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("uniqueFourLetterCode count = " + uniqueFourLetterCode.size());
        System.out.println("total entries count = " + entriesCount);

        DatabaseTools.shutdown();
    }
}
