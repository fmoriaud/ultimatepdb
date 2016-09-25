package myjmol;

import convertformat.AdapterBioJavaStructure;
import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import io.BiojavaReader;
import io.Tools;
import math.ToolsMath;
import mystructure.*;
import org.biojava.nbio.structure.Structure;
import org.junit.Ignore;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;
import shapeBuilder.ShapeBuildingException;
import ultiJmol1462.GetEnergy;
import ultiJmol1462.MyJmol1462;
import ultiJmol1462.MyJmolTools;
import ultiJmol1462.ScriptCommandOnUltiJmol;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 22/09/16.
 */
public class ScriptCommandOnUltiJmolTest {


    @Test
    public void computeHydrogensEnergyAfterHydrogenMinimization() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();

        // Prepare input as environment of MSQ in 1di9
        String fourLetterCode = "1di9";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure1di9 = null;
        try {
            mmcifStructure1di9 = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException e) {
            assertTrue(false);
        }
        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure1di9 = null;
        try {
            mystructure1di9 = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure1di9, EnumMyReaderBiojava.BioJava_MMCIFF);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException e) {
            assertTrue(false);
        }
        MyMonomerIfc msqLigand = mystructure1di9.getHeteroChain("A".toCharArray()).getMyMonomerFromResidueId(500);
        MyChainIfc[] neighbors = msqLigand.getNeighboringAminoMyMonomerByRepresentativeAtomDistance();
        MyStructureIfc target = new MyStructure(neighbors[0], algoParameters);
        MyStructureIfc protonatedTarget = null;
        try {
            protonatedTarget = MyJmolTools.protonateStructure(target, algoParameters);
            protonatedTarget.setFourLetterCode("1di9".toCharArray());
        } catch (ShapeBuildingException e) {
            assertTrue(false);
        }
        String moleculeV3000 = protonatedTarget.toV3000();

        String script = MyJmolTools.getScriptMinimizationOnlyHydrogens();
        ScriptCommandOnUltiJmol scriptCommandOnUltiJmol = new ScriptCommandOnUltiJmol(script, moleculeV3000, algoParameters, null);
        try {
            scriptCommandOnUltiJmol.execute();
        } catch (ExceptionInScoringUsingBioJavaJMolGUI exceptionInScoringUsingBioJavaJMolGUI) {
            assertTrue(false); // Should not happen. Only if energy during minimization failed to be obtained
        }
        Map<String, Object> results = scriptCommandOnUltiJmol.getResults();


        boolean convergenceReached = (boolean) results.get("convergence reached");
        Float finalEnergy = (Float) results.get("final energy");

        assertTrue(convergenceReached);
        // not reproducible
        assertEquals(finalEnergy, 5479.0f, 400.0f);
    }


    // Only to keep old code
    @Ignore
    @Test
    public void findInteractionEnergy() throws IOException, ParsingConfigFileException {


        // Get a structure proteonated and a ligand protonated
        String fourLetterCode = "1a9u";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException e) {
            assertTrue(false);
        }

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure, EnumMyReaderBiojava.BioJava_MMCIFF);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException e) {
            assertTrue(false);
        }

        MyMonomerIfc msqLigand = mystructure.getHeteroChain("A".toCharArray()).getMyMonomerFromResidueId(800);

        // translate ligand for checking

/*
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
*/
        Cloner cloner = new Cloner(msqLigand, algoParameters);
        MyStructureIfc myStructureMadeWithLigand = cloner.getClone();

        MyStructureIfc protonatedLigand = null;
        try {
            protonatedLigand = MyJmolTools.protonateStructure(myStructureMadeWithLigand, algoParameters);
            protonatedLigand.setFourLetterCode("1di9".toCharArray());
        } catch (ShapeBuildingException e) {
            assertTrue(false);
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

        MyJmol1462 ultiJMol = null;
        try {
            ultiJMol = algoParameters.ultiJMolBuffer.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // Minimize full ligand in with target all fixed
        // Get energy

        MyStructureIfc mergedMyStructure = MyStructureTools.mergeTwoV3000FileReturnIdOfFirstAtomMyStructure2AndLoadInViewer(protonatedTarget.toV3000(), protonatedLigand.toV3000(), algoParameters);


        int atomCountTarget = MyStructureTools.getAtomCount(protonatedTarget);

        /*

        String script = MyJmolTools.getScriptMinimizationWholeLigandTargetFixed(atomCountTarget);
        String postScript = MyJmolTools.getPostScriptDeleteTarget(atomCountTarget);
        ScriptCommandOnUltiJmol scriptCommandOnUltiJmolComplex = new ScriptCommandOnUltiJmol(script, postScript, mergedMyStructure.toV3000(), ultiJMol);
        try {
            scriptCommandOnUltiJmolComplex.execute();
        } catch (ExceptionInScoringUsingBioJavaJMolGUI exceptionInScoringUsingBioJavaJMolGUI) {
            exceptionInScoringUsingBioJavaJMolGUI.printStackTrace();
            assertTrue(false);
        }
        Map<String, Object> results = scriptCommandOnUltiJmolComplex.getResults();
        boolean receptorFixedLigandOptimizedConvergenceReached = (boolean) results.get("convergence reached");
        Float energyLigandfullyRelaxedIn = (Float) results.get("final energy");
        System.out.println("energyLigandfullyRelaxedIn = " + energyLigandfullyRelaxedIn);

        String ligandMinimizedV3000 = (String) results.get("structureV3000 minimzed part");

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Get energy of ligan in
        script = MyJmolTools.getScriptMinimizationOnlyHydrogens();
        ScriptCommandOnUltiJmol scriptCommandOnUltiJmolLigandInHydrogens = new ScriptCommandOnUltiJmol(script, "", ligandMinimizedV3000, ultiJMol);
        try {
            scriptCommandOnUltiJmolLigandInHydrogens.execute();
        } catch (ExceptionInScoringUsingBioJavaJMolGUI exceptionInScoringUsingBioJavaJMolGUI) {
            assertTrue(false);
        }
        results = scriptCommandOnUltiJmolLigandInHydrogens.getResults();
        Float energyLigandHydrogensOnlyRelaxedTakeout = (Float) results.get("final energy");
        System.out.println("energyLigandHydrogensOnlyRelaxedTakeout = " + energyLigandHydrogensOnlyRelaxedTakeout);


        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Minimize hydrogen ligand out
        // Get energy
        script = MyJmolTools.getScriptMinimizationAll();
        ScriptCommandOnUltiJmol scriptCommandOnUltiJmolLigand = new ScriptCommandOnUltiJmol(script, "", protonatedLigand.toV3000(), ultiJMol);
        try {
            scriptCommandOnUltiJmolLigand.execute();
        } catch (ExceptionInScoringUsingBioJavaJMolGUI exceptionInScoringUsingBioJavaJMolGUI) {
            assertTrue(false);
        }
        results = scriptCommandOnUltiJmolLigand.getResults();
        Float energyLigandfullyRelaxedOut = (Float) results.get("final energy");
        System.out.println("energyLigandfullyRelaxedOut = " + energyLigandfullyRelaxedOut);
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Minimize full ligand out
        // Minimize hydrogen ligand out
        // Get energy
        script = MyJmolTools.getScriptMinimizationOnlyHydrogens();
        ScriptCommandOnUltiJmol scriptCommandOnUltiJmolLigandWhole = new ScriptCommandOnUltiJmol(script, "", protonatedLigand.toV3000(), ultiJMol);
        try {
            scriptCommandOnUltiJmolLigandWhole.execute();
        } catch (ExceptionInScoringUsingBioJavaJMolGUI exceptionInScoringUsingBioJavaJMolGUI) {
            assertTrue(false);
        }
        results = scriptCommandOnUltiJmolLigandWhole.getResults();
        Float energyLigandHydrogensOnlyRelaxedOut = (Float) results.get("final energy");
        System.out.println("energyLigandHydrogensOnlyRelaxedOut = " + energyLigandHydrogensOnlyRelaxedOut);
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // try target only hydrogen minimized
        script = MyJmolTools.getScriptMinimizationOnlyHydrogens();
        ScriptCommandOnUltiJmol scriptCommandOnUltiJmolTargetHydrogens = new ScriptCommandOnUltiJmol(script, "", protonatedTarget.toV3000(), ultiJMol);
        try {
            scriptCommandOnUltiJmolTargetHydrogens.execute();
        } catch (ExceptionInScoringUsingBioJavaJMolGUI exceptionInScoringUsingBioJavaJMolGUI) {
            assertTrue(false);
        }
        results = scriptCommandOnUltiJmolTargetHydrogens.getResults();
        Float energyTargetHydrogensOnlyRelaxed = (Float) results.get("final energy");
        System.out.println("energyTargetHydrogensOnlyRelaxed = " + energyTargetHydrogensOnlyRelaxed);
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // Try to minimize whole ligand and target hydrogens

        script = MyJmolTools.getScriptMinimizationWholeLigandAndTargetHydrogens(atomCountTarget);
        postScript = MyJmolTools.getPostScriptDeleteTarget(atomCountTarget);
        ScriptCommandOnUltiJmol scriptCommandOnUltiJmolComplexTargetHydrogensFree = new ScriptCommandOnUltiJmol(script, postScript, mergedMyStructure.toV3000(), ultiJMol);
        try {
            scriptCommandOnUltiJmolComplexTargetHydrogensFree.execute();
        } catch (ExceptionInScoringUsingBioJavaJMolGUI exceptionInScoringUsingBioJavaJMolGUI) {
            exceptionInScoringUsingBioJavaJMolGUI.printStackTrace();
            assertTrue(false);
        }
        results = scriptCommandOnUltiJmolComplexTargetHydrogensFree.getResults();
        boolean receptorFixedLigandOptimizedConvergenceReachedHydrogenTargetFree = (boolean) results.get("convergence reached");
        Float energyLigandfullyRelaxedInTargetHydrogensFree = (Float) results.get("final energy");
        System.out.println("energyLigandfullyRelaxedInTargetHydrogensFree = " + energyLigandfullyRelaxedInTargetHydrogensFree);

        String ligandMinimizedV3000HydrogensTargetFree = (String) results.get("structureV3000 minimzed part");


        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // find out atoms of target closeby to ligand
        // sidechains having an atom at less than a threshold of 2.0 A ??
*/
        List<Integer> atomIds = atomIdsTargetCloseToLigand(protonatedTarget, protonatedLigand, algoParameters);
        String script = MyJmolTools.getScriptMinimizationWholeLigandAndTargetAtomCloseBy(atomCountTarget, atomIds);

        ScriptCommandOnUltiJmol scriptCommandOnUltiJmolComplexTargetAtomCloseByFree = new ScriptCommandOnUltiJmol(script, mergedMyStructure.toV3000(), ultiJMol, atomCountTarget);
        try {
            scriptCommandOnUltiJmolComplexTargetAtomCloseByFree.execute();
        } catch (ExceptionInScoringUsingBioJavaJMolGUI exceptionInScoringUsingBioJavaJMolGUI) {
            exceptionInScoringUsingBioJavaJMolGUI.printStackTrace();
            assertTrue(false);
        }
        Map<String, Object> results = scriptCommandOnUltiJmolComplexTargetAtomCloseByFree.getResults();
        boolean receptorFixedLigandOptimizedConvergenceReachedHydrogenTargetFree = (boolean) results.get("convergence reached");
        Float energyLigandfullyRelaxedInTargetHydrogensFree = (Float) results.get("final energy");
        System.out.println("energyLigandfullyRelaxedInTargetHydrogensFree = " + energyLigandfullyRelaxedInTargetHydrogensFree);
        System.out.println("Convergence reached : " + receptorFixedLigandOptimizedConvergenceReachedHydrogenTargetFree);
        String ligandFromMinimizedComplex = (String) results.get("ligand");
        String targetFromMinimizedComplex = (String) results.get("target");
        String complexFromMinimizedComplex = (String) results.get("structureV3000");


        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        script = MyJmolTools.getScriptMinimizationAll();
        GetEnergy getEnergyTarget = new GetEnergy(script, targetFromMinimizedComplex, ultiJMol);
        try {
            getEnergyTarget.execute();
        } catch (ExceptionInScoringUsingBioJavaJMolGUI exceptionInScoringUsingBioJavaJMolGUI) {
            exceptionInScoringUsingBioJavaJMolGUI.printStackTrace();
        }
        results = getEnergyTarget.getResults();
        float targetFromMinimizedComplexEnergy = (float) results.get("initial energy");

        System.out.println("targetFromMinimizedComplexEnergy = " + targetFromMinimizedComplexEnergy);

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        script = MyJmolTools.getScriptMinimizationAll();
        GetEnergy getEnergyComplex = new GetEnergy(script, complexFromMinimizedComplex, ultiJMol);
        try {
            getEnergyComplex.execute();
        } catch (ExceptionInScoringUsingBioJavaJMolGUI exceptionInScoringUsingBioJavaJMolGUI) {
            exceptionInScoringUsingBioJavaJMolGUI.printStackTrace();
        }
        results = getEnergyComplex.getResults();
        float complexFromMinimizedComplexEnergy = (float) results.get("initial energy");

        System.out.println("complexFromMinimizedComplexEnergy = " + complexFromMinimizedComplexEnergy);

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        script = MyJmolTools.getScriptMinimizationAll();
        GetEnergy getEnergy = new GetEnergy(script, ligandFromMinimizedComplex, ultiJMol);
        try {
            getEnergy.execute();
        } catch (ExceptionInScoringUsingBioJavaJMolGUI exceptionInScoringUsingBioJavaJMolGUI) {
            exceptionInScoringUsingBioJavaJMolGUI.printStackTrace();
        }
        results = getEnergy.getResults();
        float ligandFromMinimizedComplexEnergy = (float) results.get("initial energy");

        System.out.println("ligandFromMinimizedComplexEnergy = " + ligandFromMinimizedComplexEnergy);


        float energyInteraction = complexFromMinimizedComplexEnergy - targetFromMinimizedComplexEnergy - ligandFromMinimizedComplexEnergy;

        System.out.println("energyInteraction = " + energyInteraction);
    }


    private List<Integer> atomIdsTargetCloseToLigand(MyStructureIfc protonatedTarget, MyStructureIfc protonatedLigand, AlgoParameters algoParameters) {

        MyStructureIfc myStructureFile1 = null;
        try {
            myStructureFile1 = new MyStructure(protonatedTarget.toV3000(), algoParameters);
        } catch (ExceptionInMyStructurePackage e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        MyStructureIfc myStructureFile2 = null;
        try {
            myStructureFile2 = new MyStructure(protonatedLigand.toV3000(), algoParameters);
        } catch (ExceptionInMyStructurePackage e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        float thresholdDistance = 5.0f;
        // atomIds in Target
        // So as when I merge, I add first target, that will work for the selection

        // I assume first atom has id = 1
        List<Integer> atomIds = new ArrayList<>();

        int idAtomTarget = 0;
        for (MyAtomIfc atomTarget : myStructureFile1.getAllChains()[0].getMyMonomers()[0].getMyAtoms()) {

            idAtomTarget += 1;
            for (MyAtomIfc atomLigand : myStructureFile2.getAllChains()[0].getMyMonomers()[0].getMyAtoms()) {

                float distance = ToolsMath.computeDistance(atomTarget.getCoords(), atomLigand.getCoords());
                if (distance < thresholdDistance && !atomIds.contains(idAtomTarget)) {
                    atomIds.add(idAtomTarget);
                }
            }
        }


        return atomIds;
    }
}