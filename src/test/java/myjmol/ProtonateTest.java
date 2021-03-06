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
package myjmol;

import convertformat.AdapterBioJavaStructure;
import convertformat.ExceptionInConvertFormat;
import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import io.BiojavaReader;
import io.ExceptionInIOPackage;
import io.IOTools;
import io.Tools;
import mystructure.*;
import org.apache.commons.math3.util.Pair;
import org.biojava.nbio.structure.Structure;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;
import shapeBuilder.ShapeBuildingException;
import ultiJmol1462.Protonate;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 28/09/16.
 */
public class ProtonateTest {

    @Test
    public void testProteinStructureWhichIsAlreadyProtonated() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        int initialCount = algoParameters.ultiJMolBuffer.getSize();
        String fourLetterCode = "2n0u";
        Pair<String, MyStructureIfc> pathAndMyStructure = IOTools.getMyStructureIfc(algoParameters, fourLetterCode.toCharArray());


        Protonate protonate = new Protonate(pathAndMyStructure.getValue(), algoParameters);
        try {
            protonate.compute();
        } catch (ExceptionInScoringUsingBioJavaJMolGUI | ShapeBuildingException exceptionInScoringUsingBioJavaJMolGUI) {
            exceptionInScoringUsingBioJavaJMolGUI.printStackTrace();
        }
        MyStructureIfc protonatedMyStructure = protonate.getProtonatedMyStructure();

        MyChainIfc chainBeforeProtonation = pathAndMyStructure.getValue().getAminoMyChain("A".toCharArray());
        MyMonomerIfc gln23BeforeProtonation = chainBeforeProtonation.getMyMonomerFromResidueId(23);
        assertTrue(gln23BeforeProtonation.getMyAtoms().length == 9);

        MyChainIfc chainAfterProtonation = protonatedMyStructure.getAminoMyChain("A".toCharArray());
        MyMonomerIfc gln23AfterProtonation = chainAfterProtonation.getMyMonomerFromResidueId(23);
        assertTrue(gln23AfterProtonation.getMyAtoms().length == 18);

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


    @Test
    public void testRNADNA() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();

        String fourLetterCode = "394d";

        int initialCount = algoParameters.ultiJMolBuffer.getSize();
        Pair<String, MyStructureIfc> pathAndMyStructure = IOTools.getMyStructureIfc(algoParameters, fourLetterCode.toCharArray());

        Protonate protonate = new Protonate(pathAndMyStructure.getValue(), algoParameters);
        try {
            protonate.compute();
        } catch (ExceptionInScoringUsingBioJavaJMolGUI | ShapeBuildingException exceptionInScoringUsingBioJavaJMolGUI) {
            exceptionInScoringUsingBioJavaJMolGUI.printStackTrace();
        }
        MyStructureIfc protonatedMyStructure = protonate.getProtonatedMyStructure();

        MyChainIfc chainBeforeProtonation = pathAndMyStructure.getValue().getNucleosideChain("B".toCharArray());
        MyMonomerIfc dc11BBeforeProtonation = chainBeforeProtonation.getMyMonomerFromResidueId(11);
        assertTrue(dc11BBeforeProtonation.getMyAtoms().length == 16);

        MyChainIfc chainAfterProtonation = protonatedMyStructure.getNucleosideChain("B".toCharArray());
        MyMonomerIfc dc11BAfterProtonation = chainAfterProtonation.getMyMonomerFromResidueId(11);
        assertTrue(dc11BAfterProtonation.getMyAtoms().length == 29);

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


    @Test
    public void testProtonateStructureWhichHasAHetAtomGroupThatWasInsertedInchain() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();

        String fourLetterCode = "5b59";
        Pair<String, MyStructureIfc> pathAndMyStructure = IOTools.getMyStructureIfc(algoParameters, fourLetterCode.toCharArray());

        int initialCount = algoParameters.ultiJMolBuffer.getSize();

        Protonate protonate = new Protonate(pathAndMyStructure.getValue(), algoParameters);
        try {
            protonate.compute();
        } catch (ExceptionInScoringUsingBioJavaJMolGUI | ShapeBuildingException exceptionInScoringUsingBioJavaJMolGUI) {
            exceptionInScoringUsingBioJavaJMolGUI.printStackTrace();
        }
        MyStructureIfc protonatedMyStructure = protonate.getProtonatedMyStructure();

        MyChainIfc chainBeforeProtonation = pathAndMyStructure.getValue().getAminoMyChain("A".toCharArray());
        MyMonomerIfc kto201BeforeProtonation = chainBeforeProtonation.getMyMonomerFromResidueId(201);
        assertTrue(kto201BeforeProtonation.getMyAtoms().length == 26);

        MyChainIfc chainAfterProtonation = protonatedMyStructure.getAminoMyChain("A".toCharArray());
        MyMonomerIfc kto201AfterProtonation = chainAfterProtonation.getMyMonomerFromResidueId(201);
        assertTrue(kto201AfterProtonation.getMyAtoms().length == 49);

        // Test if atom names are correct
        MyAtomIfc atomH1C15 = kto201AfterProtonation.getMyAtomFromMyAtomName("H1C15".toCharArray());
        MyAtomIfc atomH2C15 = kto201AfterProtonation.getMyAtomFromMyAtomName("H2C15".toCharArray());
        assertTrue(atomH1C15 != null);
        assertTrue(atomH2C15 != null);

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
