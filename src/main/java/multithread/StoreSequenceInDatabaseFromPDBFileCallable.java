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
package multithread;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import database.SequenceTools;
import parameters.AlgoParameters;
import shapeBuilder.ShapeBuildingTools;
import mystructure.EnumMyReaderBiojava;
import mystructure.MyChainIfc;
import mystructure.MyStructureIfc;

public class StoreSequenceInDatabaseFromPDBFileCallable implements Runnable {
    // -------------------------------------------------------------------
    // Class variables
    // -------------------------------------------------------------------
    private Connection connexion;
    private String fourLetterCode;
    private String chainName;
    private AlgoParameters algoParameters;
    private EnumMyReaderBiojava enumMyReaderBiojava;
    private int maxCharInVarchar;


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public StoreSequenceInDatabaseFromPDBFileCallable(String fourLetterCode, String chainName, Connection connexion,
                                                      AlgoParameters algoParameters, EnumMyReaderBiojava enumMyReaderBiojava,
                                                      int maxCharInVarchar) {

        this.fourLetterCode = fourLetterCode;
        this.chainName = chainName;
        this.maxCharInVarchar = maxCharInVarchar;
        this.connexion = connexion;
        this.enumMyReaderBiojava = enumMyReaderBiojava;
        this.algoParameters = algoParameters;
    }


    //-------------------------------------------------------------
    // Public & Override methods
    //-------------------------------------------------------------
    @Override
    public void run() {
        MyStructureIfc structureForSequenceExtraction = null;

        try {
            structureForSequenceExtraction = ShapeBuildingTools.getMyStructure(fourLetterCode.toCharArray(), algoParameters, enumMyReaderBiojava);
        } catch (Exception e) { //ShapeBuildingException
            //e.printStackTrace();
            return;
        }
        if (structureForSequenceExtraction == null) {
            return;
        }
        // generate sequence
        MyChainIfc chain = structureForSequenceExtraction.getAminoMyChain(chainName.toCharArray());
        if (chain == null) {
            System.out.println("failed to read so no sequence entered : structureForSequenceExtraction.getAminoMyChain " + fourLetterCode + "  " + chainName);
            return;
        }

        String sequence = SequenceTools.generateSequence(chain);

        if (sequence.length() > maxCharInVarchar) {
            String truncatedSequence = sequence.substring(0, maxCharInVarchar);
            sequence = truncatedSequence;
        }

        try {
            String insertTableSQL = "INSERT INTO sequence"
                    + "(fourLettercode, chainId, sequenceString) VALUES"
                    + "(?,?,?)";
            PreparedStatement preparedStatement = connexion.prepareStatement(insertTableSQL);
            preparedStatement.setString(1, fourLetterCode);
            preparedStatement.setString(2, chainName);
            preparedStatement.setString(3, sequence);

            int ok = preparedStatement.executeUpdate();
            //System.out.println(ok + " raw updated " + fourLetterCode + "  " + chainName + " " + sequence);

        } catch (SQLException e1) {
            //System.out.println("Failed to enter entry in sequence table ");
        }
    }
}
