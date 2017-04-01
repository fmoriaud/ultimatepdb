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
import io.Tools;
import mystructure.*;
import org.biojava.nbio.structure.Structure;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;
import shapeBuilder.ShapeBuildingException;
import ultiJmol1462.MyJmolScripts;
import ultiJmol1462.Protonate;
import ultiJmol1462.Results;
import ultiJmol1462.ScriptCommandOnUltiJmol;

import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 22/09/16.
 */
public class ScriptCommandOnUltiJmolTest {


    @Test
    public void computeHydrogensEnergyAfterHydrogenMinimization() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        int initialCount = algoParameters.ultiJMolBuffer.getSize();

        // Prepare input as environment of MSQ in 1di9
        String fourLetterCode = "1di9";
        BiojavaReader reader = new BiojavaReader(algoParameters);
        Structure mmcifStructure1di9 = null;
        try {
            mmcifStructure1di9 = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder).getValue();
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }
        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure1di9 = null;
        try {
            mystructure1di9 = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure1di9);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
            assertTrue(false);
        }
        MyMonomerIfc msqLigand = mystructure1di9.getHeteroChain("A".toCharArray()).getMyMonomerFromResidueId(500);
        MyChainIfc[] neighbors = msqLigand.getNeighboringAminoMyMonomerByRepresentativeAtomDistance();

        Cloner cloner2 = new Cloner(neighbors, algoParameters);
        MyStructureIfc target = cloner2.getClone();


        Protonate protonate = new Protonate(target, algoParameters);
        try {
            protonate.compute();
        } catch (ExceptionInScoringUsingBioJavaJMolGUI | ShapeBuildingException exceptionInScoringUsingBioJavaJMolGUI) {
            assertTrue(false);
        }

        MyStructureIfc protonatedTarget = protonate.getProtonatedMyStructure();
        protonatedTarget.setFourLetterCode("1di9".toCharArray());


        String moleculeV3000 = protonatedTarget.toV3000();

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String script = MyJmolScripts.getScriptMinimizationOnlyHydrogens();
        ScriptCommandOnUltiJmol scriptCommandOnUltiJmol = new ScriptCommandOnUltiJmol(script, moleculeV3000, algoParameters, null);
        try {
            scriptCommandOnUltiJmol.execute();
        } catch (ExceptionInScoringUsingBioJavaJMolGUI exceptionInScoringUsingBioJavaJMolGUI) {
            assertTrue(false); // Should not happen. Only if energy during minimization failed to be obtained
        }
        Map<Results, Object> results = scriptCommandOnUltiJmol.getResults();


        boolean convergenceReached = (boolean) results.get(Results.CONVERGENCE_REACHED);
        Float finalEnergy = (Float) results.get(Results.FINAL_ENERGY);

        assertTrue(convergenceReached);
        // not reproducible
        System.out.println("finalEnergy = " + finalEnergy);
        // also possible finalEnergy = 5460.785
        //assertEquals(finalEnergy, 597.0f, 50.0f);

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