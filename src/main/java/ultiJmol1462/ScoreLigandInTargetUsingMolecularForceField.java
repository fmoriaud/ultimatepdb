package ultiJmol1462;

import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import math.ToolsMath;
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
    private boolean convergenReached;
    private float rmsdOfLigandBeforeAndAfterMinimization;
    private int countOfLongDistanceChange;
    private float strainedEnergy;

    // -------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------
    /*
    public ScoreLigandInTargetUsingMolecularForceField(MyJmol1462 ultiJmol, MyStructureIfc protonatedTarget, MyStructureIfc protonatedLigand, AlgoParameters algoParameters) {

        this.ultiJmol = ultiJmol;
        this.protonatedTarget = protonatedTarget;
        this.protonatedLigand = protonatedLigand;
        this.algoParameters = algoParameters;

    }
*/


    public ScoreLigandInTargetUsingMolecularForceField(MyStructureIfc protonatedTarget, MyStructureIfc protonatedLigand, AlgoParameters algoParameters) {

        this.algoParameters = algoParameters;
        this.protonatedTarget = protonatedTarget;
        this.protonatedLigand = protonatedLigand;
    }



    // -------------------------------------------------------------------
    // Public && Interface method
    // -------------------------------------------------------------------
    public void run() {

        doMolecularForcefieldTasks();
    }


    //-------------------------------------------------------------
    // Implementation
    //-------------------------------------------------------------
    private void doMolecularForcefieldTasks() {

        MyStructureIfc mergedMyStructure = MyStructureTools.mergeTwoV3000FileReturnIdOfFirstAtomMyStructure2AndLoadInViewer(protonatedTarget.toV3000(), protonatedLigand.toV3000(), algoParameters);
        int atomCountTarget = MyStructureTools.getAtomCount(protonatedTarget);

        List<Integer> atomIds = atomIdsTargetCloseToLigand(protonatedTarget, protonatedLigand, algoParameters);

        String script = MyJmolScripts.getScriptMinimizationWholeLigandAndTargetAtomCloseBy(atomCountTarget, atomIds);

        ScriptCommandOnUltiJmol scriptCommandOnUltiJmolComplexTargetAtomCloseByFree = new ScriptCommandOnUltiJmol(script, mergedMyStructure.toV3000(), algoParameters, atomCountTarget);
        try {
            scriptCommandOnUltiJmolComplexTargetAtomCloseByFree.execute();
        } catch (ExceptionInScoringUsingBioJavaJMolGUI exceptionInScoringUsingBioJavaJMolGUI) {
            exceptionInScoringUsingBioJavaJMolGUI.printStackTrace();

        }
        Map<String, Object> results = scriptCommandOnUltiJmolComplexTargetAtomCloseByFree.getResults();
        convergenReached = (boolean) results.get("convergence reached");

        System.out.println("Convergence reached : " + convergenReached);
        String ligandFromMinimizedComplex = (String) results.get("ligand");
        String targetFromMinimizedComplex = (String) results.get("target");
        String complexFromMinimizedComplex = (String) results.get("structureV3000");

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        script = MyJmolScripts.getScriptMinimizationAll();

        GetEnergy getEnergyTarget = new GetEnergy(script, targetFromMinimizedComplex, algoParameters);
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

        script = MyJmolScripts.getScriptMinimizationAll();
        GetEnergy getEnergyComplex = new GetEnergy(script, complexFromMinimizedComplex, algoParameters);
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

        script = MyJmolScripts.getScriptMinimizationAll();
        GetEnergy getEnergy = new GetEnergy(script, ligandFromMinimizedComplex, algoParameters);
        try {
            getEnergy.execute();
        } catch (ExceptionInScoringUsingBioJavaJMolGUI exceptionInScoringUsingBioJavaJMolGUI) {
            exceptionInScoringUsingBioJavaJMolGUI.printStackTrace();
        }
        results = getEnergy.getResults();
        float ligandFromMinimizedComplexEnergy = (float) results.get("initial energy");
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
        try {
            scriptCommandOnUltiJmolLigand.execute();
        } catch (ExceptionInScoringUsingBioJavaJMolGUI exceptionInScoringUsingBioJavaJMolGUI) {

        }
        results = scriptCommandOnUltiJmolLigand.getResults();
        String ligandFullyRelaxedV3000 = (String) results.get("structureV3000");

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        script = MyJmolScripts.getScriptMinimizationAll();
        GetEnergy getEnergy2 = new GetEnergy(script, ligandFullyRelaxedV3000, algoParameters);
        try {
            getEnergy2.execute();
        } catch (ExceptionInScoringUsingBioJavaJMolGUI exceptionInScoringUsingBioJavaJMolGUI) {
            exceptionInScoringUsingBioJavaJMolGUI.printStackTrace();
        }
        results = getEnergy2.getResults();
        float ligandFullyRelaxedEnergy = (float) results.get("initial energy");
        System.out.println("ligandFullyRelaxedEnergy = " + ligandFullyRelaxedEnergy);

        strainedEnergy = ligandFromMinimizedComplexEnergy - ligandFullyRelaxedEnergy;

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


    //------------------------
    // Getter and Setter
    //------------------------
    public float getInteractionEnergy() {
        return interactionEnergy;
    }


    public boolean isConvergenReached() {
        return convergenReached;
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
}
