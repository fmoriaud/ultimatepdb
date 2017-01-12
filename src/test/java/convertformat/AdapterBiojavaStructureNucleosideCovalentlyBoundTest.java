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
package convertformat;

import io.BiojavaReader;
import io.ExceptionInIOPackage;
import io.Tools;
import mystructure.*;
import org.biojava.nbio.structure.Group;
import org.biojava.nbio.structure.GroupType;
import org.biojava.nbio.structure.Structure;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class AdapterBiojavaStructureNucleosideCovalentlyBoundTest {

    @Test
    public void testGenerateSequenceFromMyStructureWithProblemInStoringInSequenceDB() throws ParsingConfigFileException, IOException {

        String fourLetterCode = "5a07";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
            assertTrue(false);
        }

        Group mmcifGDP = mmcifStructure.getChain(0).getAtomGroup(395);
        assertTrue(mmcifGDP.getPDBName().equals("GDP"));
        GroupType type = mmcifGDP.getType();
        assertTrue(type == GroupType.NUCLEOTIDE);

        assertTrue(mystructure.getAllAminochains().length == 2);
        // Empty nucleosides chains should had been removed
        assertTrue(mystructure.getAllNucleosidechains().length == 0);

        MyMonomerIfc myStructureGDP = mystructure.getAminoChain(0).getMyMonomerByRank(395);
        assertTrue(Arrays.equals(myStructureGDP.getThreeLetterCode(), "GDP".toCharArray()));
        assertTrue(Arrays.equals(myStructureGDP.getType(), MyMonomerType.NUCLEOTIDE.getType()));
        assertTrue(myStructureGDP.isWasHetatm() == false);
    }
}