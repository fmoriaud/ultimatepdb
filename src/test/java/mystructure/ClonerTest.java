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
package mystructure;

import convertformat.AdapterBioJavaStructure;
import convertformat.ExceptionInConvertFormat;
import io.*;
import org.apache.commons.math3.util.Pair;
import org.biojava.nbio.structure.Structure;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.openscience.cdk.interfaces.IAtomContainer;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 20/09/16.
 */
public class ClonerTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testClonerWithMyMonomerAndConvertionToV3000() throws ParsingConfigFileException, IOException, ReadingStructurefileException, ExceptionInMyStructurePackage {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();

        String fourLetterCode = "1di9";
        Pair<String, MyStructureIfc> pathAndMyStructure = IOTools.getMyStructureIfc(algoParameters, fourLetterCode.toCharArray());

        int initialCount = algoParameters.ultiJMolBuffer.getSize();

        MyMonomerIfc msqLigand = pathAndMyStructure.getValue().getHeteroChain("A".toCharArray()).getMyMonomerFromResidueId(500);
        Cloner cloner = new Cloner(msqLigand, algoParameters);
        MyStructureIfc myStructureFromAMyMonomer = cloner.getClone();
        String myStructureV3000 = myStructureFromAMyMonomer.toV3000();

        // write to a temp text file
        String pathToTempFolder = folder.getRoot().getAbsolutePath();
        String pathTOWriteV3000Molfile = pathToTempFolder + "//v3000test.mol";
        WriteTextFile.writeTextFile(myStructureV3000, pathTOWriteV3000Molfile);

        // read it with cdk and check atom and bond count

        IAtomContainer mol = CdkTools.readV3000molFile(pathTOWriteV3000Molfile);
        int atomCount = MyStructureTools.getAtomCount(myStructureFromAMyMonomer);
        int bondCount = TestTools.getBondCount(myStructureFromAMyMonomer);
        assertTrue(mol.getAtomCount() == atomCount);
        assertTrue(mol.getBondCount() * 2 == bondCount);

        int finalCount = algoParameters.ultiJMolBuffer.getSize();
        assertTrue(finalCount == initialCount);
        try {
            for (int i = 0; i < initialCount; i++) {
                algoParameters.ultiJMolBuffer.get().frame.dispose();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 0);

    }

}
