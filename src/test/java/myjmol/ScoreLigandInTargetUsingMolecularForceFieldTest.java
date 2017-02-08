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
import ultiJmol1462.Protonate;
import ultiJmol1462.ScoreLigandInTargetUsingMolecularForceField;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 25/09/16.
 */
public class ScoreLigandInTargetUsingMolecularForceFieldTest {

    @Test
    public void findInteractionEnergyLigandFarAway() throws IOException, ParsingConfigFileException {

        String fourLetterCode = "1a9u";
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

        MyMonomerIfc msqLigand = mystructure.getHeteroChain("A".toCharArray()).getMyMonomerFromResidueId(800);

        // translate ligand for checking


        MyAtomIfc[] ligandAtoms = msqLigand.getMyAtoms();
        for (MyAtomIfc atom : ligandAtoms) {
            float x = atom.getCoords()[0];
            float y = atom.getCoords()[1];
            float z = atom.getCoords()[2];
            float newX = x + 16.0f;
            float newY = y + 16.0f;
            float newZ = z + 16.0f;
            float[] newCoords = new float[3];
            newCoords[0] = newX;
            newCoords[1] = newY;
            newCoords[2] = newZ;
            atom.setCoords(newCoords);
        }

        Cloner cloner = new Cloner(msqLigand, algoParameters);
        MyStructureIfc myStructureMadeWithLigand = cloner.getClone();

        Protonate protonate = new Protonate(myStructureMadeWithLigand, algoParameters);
        try {
            protonate.compute();
        } catch (ExceptionInScoringUsingBioJavaJMolGUI | ShapeBuildingException exceptionInScoringUsingBioJavaJMolGUI) {
            assertTrue(false);
        }

        MyStructureIfc protonatedLigand = protonate.getProtonatedMyStructure();
        protonatedLigand.setFourLetterCode("1di9".toCharArray());

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        MyChainIfc[] neighbors = msqLigand.getNeighboringAminoMyMonomerByRepresentativeAtomDistance();
        Cloner cloner2 = new Cloner(neighbors, algoParameters);
        MyStructureIfc target = cloner2.getClone();

        Protonate protonate2 = new Protonate(target, algoParameters);
        try {
            protonate2.compute();
        } catch (ExceptionInScoringUsingBioJavaJMolGUI | ShapeBuildingException exceptionInScoringUsingBioJavaJMolGUI) {
            assertTrue(false);
        }

        MyStructureIfc protonatedTarget = protonate2.getProtonatedMyStructure();
        protonatedTarget.setFourLetterCode("1di9".toCharArray());


        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ScoreLigandInTargetUsingMolecularForceField score = new ScoreLigandInTargetUsingMolecularForceField(protonatedTarget, protonatedLigand, algoParameters);
        try {
            score.run();
        } catch (ExceptionInScoringUsingBioJavaJMolGUI exceptionInScoringUsingBioJavaJMolGUI) {
            exceptionInScoringUsingBioJavaJMolGUI.printStackTrace();
            assertTrue(false);
        }

        assertTrue(score.isAllconvergenceReached());
        // 0.101012625
        float rmsd = score.getRmsdOfLigandBeforeAndAfterMinimization();
        assertTrue(rmsd < 0.15 && rmsd > 0.05);

        int longDistanceChangeCount = score.getCountOfLongDistanceChange();
        assertTrue(longDistanceChangeCount == 0);

        // 0.0
        float strainedEnergy = score.getStrainedEnergy();
        assertTrue(strainedEnergy >= 0);
        assertTrue(strainedEnergy < 0.1);

        System.out.println(strainedEnergy);
        float interactionEnergy = score.getInteractionEnergy();
        //interactionEnergy = -0.014221191
        assertTrue(interactionEnergy < 0);
        assertTrue(Math.abs(interactionEnergy) < 0.1);

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
    public void findInteractionEnergyOriginalLigandIn() throws IOException, ParsingConfigFileException {

        String fourLetterCode = "1a9u";
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

        MyMonomerIfc msqLigand = mystructure.getHeteroChain("A".toCharArray()).getMyMonomerFromResidueId(800);

        // translate ligand for checking
        Cloner cloner = new Cloner(msqLigand, algoParameters);
        MyStructureIfc myStructureMadeWithLigand = cloner.getClone();

        Protonate protonate = new Protonate(myStructureMadeWithLigand, algoParameters);
        try {
            protonate.compute();
        } catch (ExceptionInScoringUsingBioJavaJMolGUI | ShapeBuildingException exceptionInScoringUsingBioJavaJMolGUI) {
            assertTrue(false);
        }

        MyStructureIfc protonatedLigand = protonate.getProtonatedMyStructure();
        protonatedLigand.setFourLetterCode("1di9".toCharArray());


        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        MyChainIfc[] neighbors = msqLigand.getNeighboringAminoMyMonomerByRepresentativeAtomDistance();

        Cloner cloner2 = new Cloner(neighbors, algoParameters);
        MyStructureIfc target = cloner2.getClone();

        Protonate protonate2 = new Protonate(target, algoParameters);
        try {
            protonate2.compute();
        } catch (ExceptionInScoringUsingBioJavaJMolGUI | ShapeBuildingException exceptionInScoringUsingBioJavaJMolGUI) {
            assertTrue(false);
        }

        MyStructureIfc protonatedTarget = protonate2.getProtonatedMyStructure();
        protonatedTarget.setFourLetterCode("1di9".toCharArray());

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ScoreLigandInTargetUsingMolecularForceField score = new ScoreLigandInTargetUsingMolecularForceField(protonatedTarget, protonatedLigand, algoParameters);
        try {
            score.run();
        } catch (ExceptionInScoringUsingBioJavaJMolGUI exceptionInScoringUsingBioJavaJMolGUI) {
            exceptionInScoringUsingBioJavaJMolGUI.printStackTrace();
            assertTrue(false);
        }

        assertTrue(score.isAllconvergenceReached());

        //0.21654312
        float rmsd = score.getRmsdOfLigandBeforeAndAfterMinimization();
        assertTrue(rmsd < 0.25 && rmsd > 0.15);

        int longDistanceChangeCount = score.getCountOfLongDistanceChange();
        assertTrue(longDistanceChangeCount == 0);

        float strainedEnergy = score.getStrainedEnergy();
        assertTrue(strainedEnergy >= 0);
        // -126.40125
        assertTrue(strainedEnergy < 130 && strainedEnergy > 120);

        float interactionEnergy = score.getInteractionEnergy();

        //interactionEnergy = -21.591003

        /*

        Good one
        currentEnergy = 3208.0256
currentEnergy = 3187.3818
currentEnergy = 3187.3818
 success = true
Convergence reached : true
 success = true
targetFromMinimizedComplexEnergy = 841.3734
java.lang.ArrayIndexOutOfBoundsException: 0
	at org.jmol.render.BallsRenderer.render(BallsRenderer.java:45)
	at org.jmol.render.ShapeRenderer.renderShape(ShapeRenderer.java:74)
	at org.jmol.render.RepaintManager.render(RepaintManager.java:226)
	at org.jmol.viewer.Viewer.render(Viewer.java:3583)
	at org.jmol.viewer.Viewer.getImage(Viewer.java:3550)
	at org.jmol.viewer.Viewer.getScreenImageBuffer(Viewer.java:3628)
	at org.jmol.viewer.Viewer.renderScreenImageStereo(Viewer.java:3458)
	at org.jmol.viewer.Viewer.renderScreenImage(Viewer.java:3537)
	at org.jmol.awt.Display.renderScreenImage(Display.java:62)
	at org.jmol.awt.Platform.renderScreenImage(Platform.java:81)
	at org.jmol.api.JmolViewer.renderScreenImage(JmolViewer.java:508)
	at ultiJmol1462.JmolPanel.paint(JmolPanel.java:71)
	at javax.swing.JComponent.paintToOffscreen(JComponent.java:5210)
	at javax.swing.RepaintManager$PaintManager.paintDoubleBuffered(RepaintManager.java:1579)
	at javax.swing.RepaintManager$PaintManager.paint(RepaintManager.java:1502)
	at javax.swing.RepaintManager.paint(RepaintManager.java:1272)
	at javax.swing.JComponent._paintImmediately(JComponent.java:5158)
	at javax.swing.JComponent.paintImmediately(JComponent.java:4969)
	at javax.swing.RepaintManager$4.run(RepaintManager.java:831)
	at javax.swing.RepaintManager$4.run(RepaintManager.java:814)
	at java.security.AccessController.doPrivileged(Native Method)
	at java.security.ProtectionDomain$JavaSecurityAccessImpl.doIntersectionPrivilege(ProtectionDomain.java:76)
	at javax.swing.RepaintManager.paintDirtyRegions(RepaintManager.java:814)
	at javax.swing.RepaintManager.paintDirtyRegions(RepaintManager.java:789)
	at javax.swing.RepaintManager.prePaintDirtyRegions(RepaintManager.java:738)
	at javax.swing.RepaintManager.access$1200(RepaintManager.java:64)
	at javax.swing.RepaintManager$ProcessingRunnable.run(RepaintManager.java:1732)
	at java.awt.event.InvocationEvent.dispatch(InvocationEvent.java:311)
	at java.awt.EventQueue.dispatchEventImpl(EventQueue.java:756)
	at java.awt.EventQueue.access$500(EventQueue.java:97)
	at java.awt.EventQueue$3.run(EventQueue.java:709)
	at java.awt.EventQueue$3.run(EventQueue.java:703)
	at java.security.AccessController.doPrivileged(Native Method)
	at java.security.ProtectionDomain$JavaSecurityAccessImpl.doIntersectionPrivilege(ProtectionDomain.java:76)
	at java.awt.EventQueue.dispatchEvent(EventQueue.java:726)
	at java.awt.EventDispatchThread.pumpOneEventForFilters(EventDispatchThread.java:201)
	at java.awt.EventDispatchThread.pumpEventsForFilter(EventDispatchThread.java:116)
	at java.awt.EventDispatchThread.pumpEventsForHierarchy(EventDispatchThread.java:105)
	at java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:101)
	at java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:93)
	at java.awt.EventDispatchThread.run(EventDispatchThread.java:82)
 success = true
complexFromMinimizedComplexEnergy = 1039.2921
 success = true
ligandFromMinimizedComplexEnergy = 219.5097
interactionEnergy = -21.591003
currentEnergy = 93.108505
currentEnergy = 93.108505
 success = true
 success = true
ligandFullyRelaxedEnergy = 93.1085



        Problem
        currentEnergy = 173785.69
currentEnergy = 173766.48
currentEnergy = 173766.48
 success = true
Convergence reached : true
 success = true
targetFromMinimizedComplexEnergy = 182351.17
 success = true
complexFromMinimizedComplexEnergy = 182680.66
 success = true
ligandFromMinimizedComplexEnergy = 219.50972
interactionEnergy = 109.974655
currentEnergy = 93.108505
currentEnergy = 93.108505
 success = true
 success = true
ligandFullyRelaxedEnergy = 93.1085


    another one
    currentEnergy = 3209.5874
currentEnergy = 3187.3818
currentEnergy = 3187.3818
 success = true
Convergence reached : true
 success = true
targetFromMinimizedComplexEnergy = 331815.06
 success = true
complexFromMinimizedComplexEnergy = 332144.56
 success = true
ligandFromMinimizedComplexEnergy = 219.5097
interactionEnergy = 109.990295
currentEnergy = 93.108505
currentEnergy = 93.108505
 success = true
 success = true
ligandFullyRelaxedEnergy = 93.1085

         */


        assertTrue(interactionEnergy < 0);
        assertTrue(Math.abs(interactionEnergy) > 15 && Math.abs(interactionEnergy) < 25);

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