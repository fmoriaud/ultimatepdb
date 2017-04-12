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

import database.HashTablesTools;
import mystructure.MyStructureIfc;
import org.apache.commons.math3.util.Pair;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertTrue;

public class ReadFilesGetsHashCodeTest {

    @Test
    public void readFileAndCheckHashCode() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        String fourLetterCode = "1di9";
        Pair<String, MyStructureIfc> pathAndMyStructure = IOTools.getMyStructureIfc(algoParameters, fourLetterCode.toCharArray());

        String path = pathAndMyStructure.getKey();
        System.out.println(path);
        assertTrue(path.contains(Tools.testFolderName)); // check if file really coming from test folder

        String pdbFileHashFromPathFromMyStructure = pathAndMyStructure.getValue().getPdbFileHash();
        try {
            String pdbFileHashFromPath = HashTablesTools.getMD5hash(path);
            assertTrue(pdbFileHashFromPath.equals(pdbFileHashFromPathFromMyStructure));
        } catch (NoSuchAlgorithmException e) {
            assertTrue(false);
        }
    }
}
