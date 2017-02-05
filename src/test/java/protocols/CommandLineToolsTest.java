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

import io.BiojavaReaderFromPDBFolderTest;
import mystructure.EnumMyReaderBiojava;
import org.junit.Test;
import parameters.AlgoParameters;

import java.net.URL;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 01/09/16.
 */
public class CommandLineToolsTest {


    @Test
    public void generateModifiedAlgoParametersTest() {

        URL url = BiojavaReaderFromPDBFolderTest.class.getClassLoader().getResource("ultimate.xml");
        AlgoParameters algoParameters = null;
        try {
            algoParameters = CommandLineTools.generateModifiedAlgoParameters(url.getPath(), EnumMyReaderBiojava.BioJava_MMCIFF);
        } catch (ParsingConfigFileException e) {
            assertTrue(false);
        }

        // TODO write an exhaustive comparison of the resulting algoParameters and the xml file content
        assertTrue(algoParameters.getQUERY_PDB_FOUR_LETTER_CODE().equals("1NLN"));
    }

}
