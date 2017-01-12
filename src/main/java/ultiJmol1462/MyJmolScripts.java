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

import java.util.List;

public class MyJmolScripts {
    //-------------------------------------------------------------
    // Static methods
    //-------------------------------------------------------------
    public static String getScriptMinimizationAll() {

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
}

