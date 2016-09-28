package myjmol;

import convertformat.AdapterBioJavaStructure;
import io.BiojavaReader;
import io.Tools;
import mystructure.*;
import org.biojava.nbio.structure.Structure;
import org.junit.Ignore;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;
import shapeBuilder.ShapeBuildingException;
import ultiJmol1462.MyJmol1462;
import ultiJmol1462.MyJmolTools;
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
        } catch (IOException e) {
            assertTrue(false);
        }

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 1);

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure, EnumMyReaderBiojava.BioJava_MMCIFF);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException e) {
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

        MyStructureIfc protonatedLigand = null;
        try {
            protonatedLigand = MyJmolTools.protonateStructure(myStructureMadeWithLigand, algoParameters);
            protonatedLigand.setFourLetterCode("1di9".toCharArray());
        } catch (ShapeBuildingException e) {
            assertTrue(false);
        }

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        MyChainIfc[] neighbors = msqLigand.getNeighboringAminoMyMonomerByRepresentativeAtomDistance();
        MyStructureIfc target = new MyStructure(neighbors[0], algoParameters);
        MyStructureIfc protonatedTarget = null;
        try {
            protonatedTarget = MyJmolTools.protonateStructure(target, algoParameters);
            protonatedTarget.setFourLetterCode("1di9".toCharArray());
        } catch (ShapeBuildingException e) {
            assertTrue(false);
        }

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ScoreLigandInTargetUsingMolecularForceField score = new ScoreLigandInTargetUsingMolecularForceField(protonatedTarget, protonatedLigand, algoParameters);
        score.run();

        assertTrue(score.isConvergenReached());
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

        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 1);
        try {
            algoParameters.ultiJMolBuffer.get().frame.dispose();
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
        } catch (IOException e) {
            assertTrue(false);
        }

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 1);

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure, EnumMyReaderBiojava.BioJava_MMCIFF);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException e) {
            assertTrue(false);
        }

        MyMonomerIfc msqLigand = mystructure.getHeteroChain("A".toCharArray()).getMyMonomerFromResidueId(800);

        // translate ligand for checking
        Cloner cloner = new Cloner(msqLigand, algoParameters);
        MyStructureIfc myStructureMadeWithLigand = cloner.getClone();

        MyStructureIfc protonatedLigand = null;
        try {
            protonatedLigand = MyJmolTools.protonateStructure(myStructureMadeWithLigand, algoParameters);
            protonatedLigand.setFourLetterCode("1di9".toCharArray());
        } catch (ShapeBuildingException e) {
            assertTrue(false);
        }

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        MyChainIfc[] neighbors = msqLigand.getNeighboringAminoMyMonomerByRepresentativeAtomDistance();
        MyStructureIfc target = new MyStructure(neighbors[0], algoParameters);
        MyStructureIfc protonatedTarget = null;
        try {
            protonatedTarget = MyJmolTools.protonateStructure(target, algoParameters);
            protonatedTarget.setFourLetterCode("1di9".toCharArray());
        } catch (ShapeBuildingException e) {
            assertTrue(false);
        }

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ScoreLigandInTargetUsingMolecularForceField score = new ScoreLigandInTargetUsingMolecularForceField(protonatedTarget, protonatedLigand, algoParameters);
        score.run();

        assertTrue(score.isConvergenReached());

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
         */

        assertTrue(interactionEnergy < 0);
        assertTrue(Math.abs(interactionEnergy) > 15 && Math.abs(interactionEnergy) < 25);

        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 1);
        try {
            algoParameters.ultiJMolBuffer.get().frame.dispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 0);

    }
}