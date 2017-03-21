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

import java.nio.file.Path;
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
    private String sequenceTableName;


    //-------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------
    public CreateAndSearchSequenceDatabase(AlgoParameters algoParameters, String sequenceTableName) {

        this.connexion = DatabaseTools.getConnection();
        this.algoParameters = algoParameters;
        this.sequenceTableName = sequenceTableName;
    }


    // -------------------------------------------------------------------
    // Public & Interface Methods
    // -------------------------------------------------------------------
    @Override
    public void createDatabase() {

        DatabaseTools.createDBandTableSequence(connexion, sequenceTableName);
        updateOveridingExistingDatabase(true);
    }

    @Override
    public void updateDatabaseKeepingFourLetterCodeEntries() {

        updateOveridingExistingDatabase(false);
    }

    @Override
    public void updateDatabaseAndOverride() {

        updateOveridingExistingDatabase(true);
    }

    @Override
    public String returnSequenceInDbifFourLetterCodeAndChainfoundInDatabase(String fourLetterCode, String chainName) {

        return DatabaseTools.returnSequenceInDbifFourLetterCodeAndChainfoundInDatabase(connexion, fourLetterCode, chainName, sequenceTableName);
    }

    @Override
    public void shutdownDb() {

        DatabaseTools.shutdown();
    }


    //-------------------------------------------------------------
    // Implementation
    //-------------------------------------------------------------
    private void updateOveridingExistingDatabase(boolean override) {

        Map<String, List<Path>> indexPDBFileInFolder = IOTools.indexPDBFileInFolder(algoParameters.getPATH_TO_REMEDIATED_PDB_MMCIF_FOLDER());
        algoParameters.setIndexPDBFileInFolder(indexPDBFileInFolder);
        for (Map.Entry<String, List<Path>> entry : indexPDBFileInFolder.entrySet()) {

            String fourLetterCode = entry.getKey();
            DatabaseTools.addInSequenceDB(connexion, override, fourLetterCode, algoParameters, sequenceTableName);
        }
    }


    private void generateMyStructureAndstoreSequenceInDB(String fourLetterCode) {


        String fourLetterCodeToLowerCase = fourLetterCode.toLowerCase();
        MyStructureIfc myStructure = IOTools.getMyStructureIfc(algoParameters, fourLetterCodeToLowerCase.toCharArray());
        if (myStructure == null) {
            return;
        }

        MyChainIfc[] chainsForShapeBuilding = myStructure.getAllChainsRelevantForShapeBuilding();
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

            try {
                String insertTableSQL = "INSERT INTO sequence"
                        + "(fourLettercode, chainId, chainType, sequenceString) VALUES"
                        + "(?,?,?,?)";
                PreparedStatement preparedStatement = connexion.prepareStatement(insertTableSQL);
                preparedStatement.setString(1, fourLetterCode);
                preparedStatement.setString(2, String.valueOf(chainName));
                preparedStatement.setString(3, String.valueOf(chainType));
                preparedStatement.setString(4, sequence);
                preparedStatement.close();
                int ok = preparedStatement.executeUpdate();

                System.out.println(ok + " raw created " + fourLetterCode + "  " + String.valueOf(chainName) + "  " + String.valueOf(chainType)); // + " " + sequence);

            } catch (SQLException e1) {
                System.out.println("Failed to enter entry in sequence table ");
            }
        }
    }
}