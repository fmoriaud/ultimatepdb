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
package protocols;

import database.CreateAndSearchSequenceDatabaseWithExecutor;
import database.SequenceTools;
import parameters.AlgoParameters;

/**
 * Created by Fabrice on 11/11/16.
 */
public class SequenceDatabaseBuildMultiThread {

    // -------------------------------------------------------------------
    // Public && Interface method
    // -------------------------------------------------------------------

    /**
     * To build the sequense database. It takes one or two days. This method deletes existing database and builds a new one.
     *
     * @param args
     * @throws ParsingConfigFileException
     */
    public static void main(String[] args) throws ParsingConfigFileException {

        AlgoParameters algoParameters = ProtocolTools.prepareAlgoParameters();
        algoParameters.setSHAPE_COMPARISON_THREAD_COUNT(6);
        CreateAndSearchSequenceDatabaseWithExecutor createAndSearchSequenceDatabaseWithExecutor = new CreateAndSearchSequenceDatabaseWithExecutor(algoParameters, SequenceTools.tableName);
        createAndSearchSequenceDatabaseWithExecutor.createDatabase();

        createAndSearchSequenceDatabaseWithExecutor.shutdownDb();
    }
}