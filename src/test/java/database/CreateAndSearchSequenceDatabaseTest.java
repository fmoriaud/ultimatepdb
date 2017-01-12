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
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CreateAndSearchSequenceDatabaseTest {

    @Test
    public void testCreateDatabaseTest() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        CreateAndSearchSequenceDatabase createAndSearchSequenceDatabase = new CreateAndSearchSequenceDatabase(algoParameters, Tools.testTableName);
        createAndSearchSequenceDatabase.createDatabase();

        // Read an entry from it
        String sequence1di9 = createAndSearchSequenceDatabase.returnSequenceInDbifFourLetterCodeAndChainfoundInDatabase("1DI9", "A");

        // Check sequence length
        assertNotNull(sequence1di9);
        assertEquals((sequence1di9.length() / 3), 348);

        createAndSearchSequenceDatabase.shutdownDb();
    }
}

