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
package ultiJmol1462;

import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import math.MathTools;
import mystructure.*;
import parameters.AlgoParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Fabrice on 25/09/16.
 */
public class ScoreLigandInTargetUsingMolecularForceField {
    // -------------------------------------------------------------------
    // Class variables
    // -------------------------------------------------------------------
    private MyStructureIfc protonatedTarget;
    private MyStructureIfc protonatedLigand;
    private AlgoParameters algoParameters;

    private float interactionEnergy;
    private float rmsdOfLigandBeforeAndAfterMinimization;
    private int countOfLongDistanceChange;
    private float strainedEnergy;

    private boolean allconvergenceReached = true;


    // -------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------
    public ScoreLigandInTargetUsingMolecularForceField(MyStructureIfc protonatedTarget, MyStructureIfc protonatedLigand, AlgoParameters algoParameters) {

        this.algoParameters = algoParameters;
        this.protonatedTarget = protonatedTarget;
        this.protonatedLigand = protonatedLigand;
    }


    // -------------------------------------------------------------------
    // Public && Interface method
    // -------------------------------------------------------------------
    public void run() throws ExceptionInScoringUsingBioJavaJMolGUI {
        doMolecularForcefieldTasks();

    }


    //-------------------------------------------------------------
    // Implementation
    //-------------------------------------------------------------
    private void doMolecularForcefieldTasks() throws ExceptionInScoringUsingBioJavaJMolGUI {

        MyStructureIfc mergedMyStructure = MyStructureTools.mergeTwoV3000FileReturnIdOfFirstAtomMyStructure2AndLoadInViewer(protonatedTarget.toV3000(), protonatedLigand.toV3000(), algoParameters);
        int atomCountTarget = MyStructureTools.getAtomCount(protonatedTarget);

        List<Integer> atomIds = atomIdsTargetCloseToLigand(protonatedTarget, protonatedLigand, algoParameters);

        String script = MyJmolScripts.getScriptMinimizationWholeLigandAndTargetAtomCloseBy(atomCountTarget, atomIds);

        ScriptCommandOnUltiJmol scriptCommandOnUltiJmolComplexTargetAtomCloseByFree = new ScriptCommandOnUltiJmol(script, mergedMyStructure.toV3000(), algoParameters, atomCountTarget);

        scriptCommandOnUltiJmolComplexTargetAtomCloseByFree.execute();

        Map<Results, Object> results = scriptCommandOnUltiJmolComplexTargetAtomCloseByFree.getResults();
        boolean convergenReached = (boolean) results.get(Results.CONVERGENCE_REACHED);

        if (convergenReached == false) {
            allconvergenceReached = false;
        }
        System.out.println("Convergence reached : " + convergenReached);
        String ligandFromMinimizedComplex = (String) results.get(Results.LIGAND);
        String targetFromMinimizedComplex = (String) results.get(Results.TARGET);
        String complexFromMinimizedComplex = (String) results.get(Results.STRUCTURE_V3000);

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        script = MyJmolScripts.getScriptMinimizationAll();

        GetEnergy getEnergyTarget = new GetEnergy(script, targetFromMinimizedComplex, algoParameters);

        getEnergyTarget.execute();

        if (getEnergyTarget.isConvergenceReached() == false) {
            allconvergenceReached = false;
        }
        results = getEnergyTarget.getResults();
        float targetFromMinimizedComplexEnergy = (float) results.get(Results.INITIAL_ENERGY);
        System.out.println("targetFromMinimizedComplexEnergy = " + targetFromMinimizedComplexEnergy);

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        script = MyJmolScripts.getScriptMinimizationAll();
        GetEnergy getEnergyComplex = new GetEnergy(script, complexFromMinimizedComplex, algoParameters);

        getEnergyComplex.execute();

        if (getEnergyComplex.isConvergenceReached() == false) {
            allconvergenceReached = false;
        }

        results = getEnergyComplex.getResults();
        float complexFromMinimizedComplexEnergy = (float) results.get(Results.INITIAL_ENERGY);
        System.out.println("complexFromMinimizedComplexEnergy = " + complexFromMinimizedComplexEnergy);

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        script = MyJmolScripts.getScriptMinimizationAll();
        GetEnergy getEnergy = new GetEnergy(script, ligandFromMinimizedComplex, algoParameters);

        getEnergy.execute();
        if (getEnergy.isConvergenceReached() == false) {
            allconvergenceReached = false;
        }

        results = getEnergy.getResults();
        float ligandFromMinimizedComplexEnergy = (float) results.get(Results.INITIAL_ENERGY);
        System.out.println("ligandFromMinimizedComplexEnergy = " + ligandFromMinimizedComplexEnergy);


        interactionEnergy = complexFromMinimizedComplexEnergy - targetFromMinimizedComplexEnergy - ligandFromMinimizedComplexEnergy;
        System.out.println("interactionEnergy = " + interactionEnergy);

        // RMSD between initial ligand and minimized ligand
        ComputeRmsd computeRmsd = new ComputeRmsd(protonatedLigand.toV3000(), ligandFromMinimizedComplex, algoParameters);

        rmsdOfLigandBeforeAndAfterMinimization = computeRmsd.getRmsd();
        countOfLongDistanceChange = computeRmsd.getCountOfLongDistanceChange();

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // strained energy
        script = MyJmolScripts.getScriptMinimizationAll();
        ScriptCommandOnUltiJmol scriptCommandOnUltiJmolLigand = new ScriptCommandOnUltiJmol(script, protonatedLigand.toV3000(), algoParameters, null);
        scriptCommandOnUltiJmolLigand.execute();

        results = scriptCommandOnUltiJmolLigand.getResults();

        boolean convergenceReached = (Boolean) results.get(Results.CONVERGENCE_REACHED);
        if (convergenceReached == false) {
            allconvergenceReached = false;
        }
        String ligandFullyRelaxedV3000 = (String) results.get(Results.STRUCTURE_V3000);

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        script = MyJmolScripts.getScriptMinimizationAll();
        GetEnergy getEnergy2 = new GetEnergy(script, ligandFullyRelaxedV3000, algoParameters);
        getEnergy2.execute();

        if (getEnergy2.isConvergenceReached() == false) {
            allconvergenceReached = false;
        }
        results = getEnergy2.getResults();
        float ligandFullyRelaxedEnergy = (float) results.get(Results.INITIAL_ENERGY);
        System.out.println("ligandFullyRelaxedEnergy = " + ligandFullyRelaxedEnergy);

        strainedEnergy = ligandFromMinimizedComplexEnergy - ligandFullyRelaxedEnergy;
    }


    private List<Integer> atomIdsTargetCloseToLigand(MyStructureIfc protonatedTarget, MyStructureIfc protonatedLigand, AlgoParameters algoParameters) {

        MyStructureIfc myStructureFile1 = null;
        try {
            myStructureFile1 = new MyStructure(protonatedTarget.toV3000());
        } catch (ExceptionInMyStructurePackage e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        MyStructureIfc myStructureFile2 = null;
        try {
            myStructureFile2 = new MyStructure(protonatedLigand.toV3000());
        } catch (ExceptionInMyStructurePackage e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        float thresholdDistance = 5.0f;

        List<Integer> atomIds = new ArrayList<>();

        int idAtomTarget = 0;
        for (MyAtomIfc atomTarget : myStructureFile1.getAllChains()[0].getMyMonomers()[0].getMyAtoms()) {

            idAtomTarget += 1;
            for (MyAtomIfc atomLigand : myStructureFile2.getAllChains()[0].getMyMonomers()[0].getMyAtoms()) {

                float distance = MathTools.computeDistance(atomTarget.getCoords(), atomLigand.getCoords());
                if (distance < thresholdDistance && !atomIds.contains(idAtomTarget)) {
                    atomIds.add(idAtomTarget);
                }
            }
        }
        return atomIds;
    }


    // -------------------------------------------------------------------
    // Getter and Setter
    // -------------------------------------------------------------------
    public float getInteractionEnergy() {
        return interactionEnergy;
    }

    public float getRmsdOfLigandBeforeAndAfterMinimization() {
        return rmsdOfLigandBeforeAndAfterMinimization;
    }

    public int getCountOfLongDistanceChange() {
        return countOfLongDistanceChange;
    }

    public float getStrainedEnergy() {
        return strainedEnergy;
    }

    public boolean isAllconvergenceReached() {
        return allconvergenceReached;
    }
}
