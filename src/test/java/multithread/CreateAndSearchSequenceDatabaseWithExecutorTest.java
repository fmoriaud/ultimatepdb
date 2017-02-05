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

import database.CreateAndSearchSequenceDatabaseWithExecutor;
import io.Tools;
import org.junit.Ignore;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Fabrice on 06/11/16.
 */
public class CreateAndSearchSequenceDatabaseWithExecutorTest {

    @Ignore
    @Test
    public void testUpdateDbMultithreadedTest() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();
        algoParameters.setSHAPE_COMPARISON_THREAD_COUNT(6);

        CreateAndSearchSequenceDatabaseWithExecutor createAndSearchSequenceDatabaseWithExecutor = new CreateAndSearchSequenceDatabaseWithExecutor(algoParameters, Tools.testTableName);
        createAndSearchSequenceDatabaseWithExecutor.createDatabase();

        // Read an entry from it
        String sequence1di9 = createAndSearchSequenceDatabaseWithExecutor.returnSequenceInDbifFourLetterCodeAndChainfoundInDatabase("1DI9", "A");

        // Check sequence length
        assertNotNull(sequence1di9);
        assertEquals((sequence1di9.length() / 3), 348);

        createAndSearchSequenceDatabaseWithExecutor.shutdownDb();
    }
}
