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

import parameters.AlgoParameters;

import java.sql.Connection;

/**
 * Created by Fabrice on 06/11/16.
 */
public class AddInSequenceDB implements DoMyDbTaskIfc {
    //-------------------------------------------------------------
    // Class variables
    //-------------------------------------------------------------
    private AlgoParameters algoParameters;
    private String fourLetterCode;
    private boolean override;
    private String sequenceTableName;


    //-------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------
    public AddInSequenceDB(AlgoParameters algoParameters, String fourLetterCode, boolean override, String sequenceTableName) {
        this.algoParameters = algoParameters;
        this.fourLetterCode = fourLetterCode;
        this.override = override;
        this.sequenceTableName = sequenceTableName;
    }


    //-------------------------------------------------------------
    // Interface & Public methods
    //-------------------------------------------------------------
    @Override
    public boolean doAndReturnSuccessValue(Connection connexion) {

        return DatabaseTools.addInSequenceDB(connexion, override, fourLetterCode, algoParameters, sequenceTableName);
    }
}
