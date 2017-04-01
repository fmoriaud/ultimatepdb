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
package io;

import org.biojava.nbio.structure.Structure;
import org.junit.Ignore;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class BiojavaReaderGiveupOnTooBigFilesTest {

    // The file 3j3y cant be pushed to github
    // But if added locally to the test resources it can be run
    @Ignore
    @Test
    public void testReadFromPDBFolderProtein() throws ParsingConfigFileException, IOException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        String fourLetterCode = "3j3y";
        BiojavaReader reader = new BiojavaReader(algoParameters);
        Structure mmcifStructure = null;
        boolean exceptionThrown = false;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder).getValue();
        } catch (IOException | ExceptionInIOPackage e) {
            exceptionThrown = true;
            assertTrue(e.getMessage().contains("File too big to be handled. Size ="));
        }
        assertTrue(exceptionThrown);

    }
}