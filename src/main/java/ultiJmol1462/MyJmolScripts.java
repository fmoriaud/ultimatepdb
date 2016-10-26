package ultiJmol1462;

/**
 * Created by Fabrice on 28/09/16.
 */

import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import jmolgui.UltiJmol1462;
import parameters.AlgoParameters;

import java.util.List;

import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import parameters.AlgoParameters;
import ultiJmol1462.MyJmolTools;

import java.util.List;

/**
 * Created by Fabrice on 28/09/16.
 */
public class MyJmolScripts {

    public static Float getEnergyBiojavaJmolNewCode(UltiJmol1462 ultiJMol, AlgoParameters algoParameters) throws ExceptionInScoringUsingBioJavaJMolGUI {

        Float energy = MyJmolTools.waitMinimizationEnergyAvailable(2, ultiJMol);
        if (energy == null) {
            String message = "waitMinimizationEnergyAvailable failed";
            ExceptionInScoringUsingBioJavaJMolGUI exception = new ExceptionInScoringUsingBioJavaJMolGUI(message);
            throw exception;
        }
        return energy;
    }


    public static String getScriptMinimizationAll() {

        // Build script
        boolean onlyHydrogen = false;
        StringBuilder sb = new StringBuilder();
        sb.append("set forcefield \"MMFF94\"\n" + "set minimizationsteps 50\n");
        sb.append("set logLevel 0\nset undo ON\n set echo off\n set useMinimizationThread ON\n");
        String selectString = "";
        if (onlyHydrogen == true) {
            selectString = "not { hydrogen }";
            sb.append("minimize FIX {" + selectString + "} select {*}\n");
        } else {
            sb.append("minimize select {*}\n");
        }
        String script = sb.toString();

        return script;
    }


    public static String getScriptMinimizationOnlyHydrogens() {

        // Build script
        boolean onlyHydrogen = true;
        StringBuilder sb = new StringBuilder();
        sb.append("set forcefield \"MMFF94\"\n" + "set minimizationsteps 50\n");
        sb.append("set logLevel 0\nset undo ON\n set echo off\n set useMinimizationThread ON\n");
        String selectString = "";
        if (onlyHydrogen == true) {
            selectString = "not { hydrogen }";
            sb.append("minimize FIX {" + selectString + "} select {*}\n");
        } else {
            sb.append("minimize select {*}\n");
        }
        String script = sb.toString();

        return script;
    }


    public static String getScriptMinimizationWholeLigandTargetFixed(int atomCountTarget) {

        // Build script
        StringBuilder sb = new StringBuilder();
        sb.append("set forcefield \"MMFF94\"\n" + "set minimizationsteps 50\n");
        sb.append("set logLevel 0\nset undo ON\n set echo off\n set useMinimizationThread ON\n");
        String selectStringTarget = "atomno > 0 and atomno < " + (atomCountTarget + 1);
        String selectLigand = "{atomno > " + (atomCountTarget) + "}";
        sb.append("select {" + selectStringTarget + "}\n");
        sb.append("spacefill 50\n");
        sb.append("select {" + selectLigand + "}\n");
        sb.append("spacefill 400\n");
        sb.append("minimize FIX {" + selectStringTarget + "} select {*}\n");

        String script = sb.toString();

        return script;
    }


    public static String getScriptAddHydrogens() {

        StringBuilder sb = new StringBuilder();
        sb.append("set forcefield \"MMFF94\"\n" + "set minimizationsteps 20\n");
        sb.append("minimize energy ADDHYDROGENS\n");

        String script = sb.toString();
        return script;
    }


    public static String getScriptMinimizationWholeLigandAndTargetHydrogens(int atomCountTarget) {

        // Build script
        StringBuilder sb = new StringBuilder();
        sb.append("set forcefield \"MMFF94\"\n" + "set minimizationsteps 50\n");
        sb.append("set logLevel 0\nset undo ON\n set echo off\n set useMinimizationThread ON\n");
        String selectStringTargetNonHydrogens = "atomno > 0 and atomno < " + (atomCountTarget + 1) + " and not { hydrogen }";
        String selectStringTargetHydrogens = "atomno > 0 and atomno < " + (atomCountTarget + 1) + " and { hydrogen }";

        String selectLigand = "{atomno > " + (atomCountTarget) + "}";
        sb.append("select {" + selectStringTargetNonHydrogens + "}\n");
        sb.append("spacefill 50\n");
        sb.append("select {" + selectLigand + "}\n");
        sb.append("spacefill 400\n");
        sb.append("select {" + selectStringTargetHydrogens + "}\n");
        sb.append("spacefill 400\n");
        sb.append("minimize FIX {" + selectStringTargetNonHydrogens + "} select {*}\n");

        String script = sb.toString();

        return script;
    }


    public static String getScriptMinimizationWholeLigandAndTargetAtomCloseBy(int atomCountTarget, List<Integer> atomIds) {

        // Build script
        StringBuilder sb = new StringBuilder();
        sb.append("set forcefield \"MMFF94\"\n" + "set minimizationsteps 50\n");
        sb.append("set logLevel 0\nset undo ON\n set echo off\n set useMinimizationThread ON\n");
        String selectStringTargetNonHydrogens = "atomno > 0 and atomno < " + (atomCountTarget + 1) + " and not { hydrogen }";
        String selectStringTargetHydrogens = "atomno > 0 and atomno < " + (atomCountTarget + 1) + " and { hydrogen }";

        StringBuilder sbAtomIds = new StringBuilder();
        for (int i = 0; i < atomIds.size(); i++) {
            sbAtomIds.append("atomno = " + atomIds.get(i));
            if (i != atomIds.size() - 1) {
                sbAtomIds.append(" or ");
            }
        }

        String selectStringTargetNotToFix = sbAtomIds.toString();
        String selectStringTargetToFix = "atomno > 0 and atomno < " + (atomCountTarget + 1) + " and not { " + selectStringTargetNotToFix + " }";

        if (atomIds.isEmpty()) {
            selectStringTargetToFix = "atomno > 0 and atomno < " + (atomCountTarget + 1);
            selectStringTargetNotToFix = null;
        }

        String selectLigand = "{atomno > " + (atomCountTarget) + "}";
        sb.append("select {" + selectStringTargetToFix + "}\n");
        sb.append("spacefill 50\n");
        sb.append("select {" + selectLigand + "}\n");
        sb.append("spacefill 200\n");
        if (selectStringTargetNotToFix != null) {
            sb.append("select {" + selectStringTargetNotToFix + "}\n");
            sb.append("spacefill 100\n");
        }
        sb.append("minimize FIX {" + selectStringTargetToFix + "} select {*}\n");

        String script = sb.toString();

        return script;
    }


    public static String getPostScriptDeleteTarget(int atomCountTarget) {

        StringBuilder sb = new StringBuilder();
        String selectStringTarget = "atomno > 0 and atomno < " + (atomCountTarget + 1);

        sb.append("delete (" + selectStringTarget + ") \n");

        return sb.toString();
    }


    public static String getPostScriptDeleteLigand(int atomCountTarget) {

        StringBuilder sb = new StringBuilder();
        String selectLigand = "{atomno > " + (atomCountTarget) + "}";
        sb.append("delete (" + selectLigand + ") \n");

        return sb.toString();
    }

}

