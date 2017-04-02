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
import parameters.AlgoParameters;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;

public class AddInSequenceDB implements DoMyDbTaskIfc {
    //-------------------------------------------------------------
    // Class variables
    //-------------------------------------------------------------
    private AlgoParameters algoParameters;
    private String tableName;
    private String tableFailureName;

    //-------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------
    public AddInSequenceDB(AlgoParameters algoParameters, String tableName, String tableFailureName) {
        this.algoParameters = algoParameters;
        this.tableName = tableName;
        this.tableFailureName = tableFailureName;
    }


    //-------------------------------------------------------------
    // Interface & Public methods
    //-------------------------------------------------------------
    @Override
    public boolean doAndReturnSuccessValue(Connection connexion, String pathToFile) {

        try {
            String fourLetterCode = FileListingVisitorForPDBCifGzFiles.makeFourLetterCodeUpperCaseFromFileNameForMmcifGzFiles(pathToFile);

            HashTablesTools.addAFile(pathToFile, fourLetterCode, connexion, tableName, tableFailureName, algoParameters);
        } catch (IOException | NoSuchAlgorithmException e) {
            return false;
        }
        return true;
    }
}
