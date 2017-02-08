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
import hits.HitTools;
import io.BiojavaReader;
import io.ExceptionInIOPackage;
import io.Tools;
import mystructure.*;
import org.biojava.nbio.structure.Structure;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;
import shapeBuilder.ShapeBuildingException;
import ultiJmol1462.Protonate;
import ultiJmol1462.ResultsUltiJMolMinimizedHitLigandOnTarget;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 18/09/16.
 */
public class MyJmolToolsMinimizeTest {


    @Test
    public void testScoreByMinimizingLigandOnFixedReceptor() throws IOException, ParsingConfigFileException, ExceptionInMyStructurePackage {

        // Get a structure with a ligand
        String fourLetterCode = "1di9";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        int initialCount = algoParameters.ultiJMolBuffer.getSize();

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
            assertTrue(false);
        }

        MyMonomerIfc msqLigand = mystructure.getHeteroChain("A".toCharArray()).getMyMonomerFromResidueId(500);
        Cloner cloner = new Cloner(msqLigand, algoParameters);
        MyStructureIfc myStructureMadeWithLigand = cloner.getClone();

        Protonate protonate = new Protonate(myStructureMadeWithLigand, algoParameters);
        try {
            protonate.compute();
        } catch (ExceptionInScoringUsingBioJavaJMolGUI | ShapeBuildingException exceptionInScoringUsingBioJavaJMolGUI) {
            assertTrue(false);
        }

        MyStructureIfc protonatedLigand = protonate.getProtonatedMyStructure();

        MyChainIfc[] neighbors = msqLigand.getNeighboringAminoMyMonomerByRepresentativeAtomDistance();
        Cloner cloner2 = new Cloner(neighbors, algoParameters);
        MyStructureIfc target = cloner2.getClone();
        //MyStructureIfc target = new MyStructure(neighbors[0], algoParameters);

        Protonate protonate2 = new Protonate(target, algoParameters);
        try {
            protonate2.compute();
        } catch (ExceptionInScoringUsingBioJavaJMolGUI | ShapeBuildingException exceptionInScoringUsingBioJavaJMolGUI) {
            assertTrue(false);
        }
        MyStructureIfc protonatedTarget = protonate2.getProtonatedMyStructure();

        // notcmodifying ligand coordinates

        // minimze ligand in original structure
        ResultsUltiJMolMinimizedHitLigandOnTarget resultsUltiJMolMinimizedHitLigandOnTarget = null;
        try {
            resultsUltiJMolMinimizedHitLigandOnTarget = HitTools.scoreByMinimizingLigandOnFixedReceptor(algoParameters, protonatedLigand, protonatedTarget);
        } catch (ExceptionInScoringUsingBioJavaJMolGUI exceptionInScoringUsingBioJavaJMolGUI) {
            assertTrue(false);
        }
        assertTrue(resultsUltiJMolMinimizedHitLigandOnTarget != null);
        assertTrue(resultsUltiJMolMinimizedHitLigandOnTarget.getCountOfLongDistanceChange() == 0);
        assertTrue(resultsUltiJMolMinimizedHitLigandOnTarget.getInteractionEFinal() < 0);
        assertTrue(Math.abs(resultsUltiJMolMinimizedHitLigandOnTarget.getInteractionEFinal()) > 20 && Math.abs(resultsUltiJMolMinimizedHitLigandOnTarget.getInteractionEFinal()) < 30);
        assertTrue(resultsUltiJMolMinimizedHitLigandOnTarget.getLigandStrainedEnergy() > 0);
        assertTrue(Math.abs(resultsUltiJMolMinimizedHitLigandOnTarget.getLigandStrainedEnergy()) > 20 && Math.abs(resultsUltiJMolMinimizedHitLigandOnTarget.getLigandStrainedEnergy()) < 40);
        assertTrue(resultsUltiJMolMinimizedHitLigandOnTarget.getRmsdLigand() > 0.1 && resultsUltiJMolMinimizedHitLigandOnTarget.getRmsdLigand() < 0.3);

        // interactionEnergy = 222.08408
        // ligandFullyRelaxedEnergy = 13.304225

        //interactionEnergy = -26.642128
        //ligandFullyRelaxedEnergy = 13.304225

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
