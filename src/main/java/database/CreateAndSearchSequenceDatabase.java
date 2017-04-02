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
import parameters.AlgoParameters;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class CreateAndSearchSequenceDatabase implements CreateAndSearchSequenceDatabaseIfc {
    //-------------------------------------------------------------
    // Class variables
    //-------------------------------------------------------------
    private Connection connexion;
    private AlgoParameters algoParameters;
    private String tableName;
    private String tableFailureName;


    //-------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------

    /**
     * Create and update the sequence databases.
     * @param algoParameters
     * @param tableName
     * @param tableFailureName
     */
    public CreateAndSearchSequenceDatabase(AlgoParameters algoParameters, String tableName, String tableFailureName) {

        this.connexion = HashTablesTools.getConnection(tableName, tableFailureName);
        this.algoParameters = algoParameters;
        this.tableName = tableName;
        this.tableFailureName = tableFailureName;
    }


    // -------------------------------------------------------------------
    // Public & Interface Methods
    // -------------------------------------------------------------------
    @Override
    public void createDatabase() {

        HashTablesTools.createTables(connexion, tableName, tableFailureName);
        updateExistingDatabase();
    }

    @Override
    public void updateDatabase() {

        HashTablesTools.createTablesIfTheyDontExists(connexion, tableName, tableFailureName);
        updateExistingDatabase();
    }

    @Override
    public String returnSequenceInDbifFourLetterCodeAndChainfoundInDatabase(String fourLetterCode, String chainName) {

        return HashTablesTools.returnSequenceInDbifFourLetterCodeAndChainfoundInDatabase(connexion, fourLetterCode, chainName, tableName);
    }

    @Override
    public void shutdownDb() {

        HashTablesTools.shutdown();
    }


    //-------------------------------------------------------------
    // Implementation
    //-------------------------------------------------------------
    private void updateExistingDatabase() {

        Map<String, List<MMcifFileInfos>> indexPDBFileInFolder = algoParameters.getIndexPDBFileInFolder();

        for (Map.Entry<String, List<MMcifFileInfos>> entry : indexPDBFileInFolder.entrySet()) {

            for (MMcifFileInfos fileInfos : entry.getValue()) {
                try {
                    String fourLetterCode = FileListingVisitorForPDBCifGzFiles.makeFourLetterCodeUpperCaseFromFileNameForMmcifGzFiles(fileInfos.getPathToFile());
                    HashTablesTools.addAFile(fileInfos.getPathToFile(), fourLetterCode, connexion, tableName, tableFailureName, algoParameters);
                } catch (IOException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}