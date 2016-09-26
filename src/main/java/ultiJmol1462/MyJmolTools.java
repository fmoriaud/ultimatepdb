package ultiJmol1462;

import java.util.*;
import java.util.Map.Entry;

import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import math.AddToMap;
import mystructure.*;
import org.jmol.minimize.Minimizer;
import parameters.AlgoParameters;
import shapeBuilder.ShapeBuildingException;

import static junit.framework.Assert.assertTrue;

public class MyJmolTools {

    /**
     * Protonate a MyStructure using Jmol. Hydrogens are added and xyz position are set according to Jmol UUF forcefield.
     *
     * @param inputStructure
     * @param algoParameters is needed to get a Jmol instance
     * @return
     * @throws ShapeBuildingException
     */
    public static MyStructureIfc protonateStructure(MyStructureIfc inputStructure, AlgoParameters algoParameters) throws ShapeBuildingException {

        MyStructureIfc protonatedStructure = null;
        try {
            protonatedStructure = protonateStructureUsingJMolUFF(inputStructure, algoParameters);
        } catch (ExceptionInScoringUsingBioJavaJMolGUI e) {
            String message = "protonateStructureUsingJMolUFFandStoreBonds failed in Shape Building " + String.valueOf(inputStructure.getFourLetterCode());
            ShapeBuildingException shapeBuildingException = new ShapeBuildingException(message);
            throw shapeBuildingException;
        }

        return protonatedStructure;
    }


    /**
     * Need to be tested !!!
     *
     * @param inputStructureV3000
     * @param algoParameters
     * @return
     * @throws ShapeBuildingException
     */
    public static MyStructureIfc protonateStructure(String inputStructureV3000, AlgoParameters algoParameters) throws ShapeBuildingException {

        MyStructureIfc inputStructure = null;
        try {
            inputStructure = new MyStructure(inputStructureV3000, algoParameters);
        } catch (ExceptionInMyStructurePackage e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        inputStructure.setFourLetterCode("XXXX".toCharArray());
        MyStructureIfc protonatedStructure = protonateStructure(inputStructure, algoParameters);
        return protonatedStructure;
    }


    /**
     * Minimize an input of two MyStructureIfc
     *
     * @param algoParameters
     * @param peptide
     * @param target
     * @return
     * @throws ExceptionInScoringUsingBioJavaJMolGUI
     */
    public static ResultsUltiJMolMinimizedHitLigandOnTarget scoreByMinimizingLigandOnFixedReceptor(
            AlgoParameters algoParameters, MyStructureIfc peptide, MyStructureIfc target) throws ExceptionInScoringUsingBioJavaJMolGUI {
        ResultsUltiJMolMinimizedHitLigandOnTarget hitScore = null;
        MyJmol1462 ultiJmol = null;

        try {
            ultiJmol = algoParameters.ultiJMolBuffer.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            ScoreLigandInTargetUsingMolecularForceField score = new ScoreLigandInTargetUsingMolecularForceField(ultiJmol, target, peptide, algoParameters);
            score.run();

            float rmsd = score.getRmsdOfLigandBeforeAndAfterMinimization();
            int longDistanceChangeCount = score.getCountOfLongDistanceChange();
            float strainedEnergy = score.getStrainedEnergy();
            float interactionEnergy = score.getInteractionEnergy();

            hitScore = new ResultsUltiJMolMinimizedHitLigandOnTarget(longDistanceChangeCount, interactionEnergy, strainedEnergy, rmsd);

        } catch (Exception e) {

            System.out.println("Exception in scoreByMinimizingLigandOnFixedReceptor !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            ultiJmol.frame.dispose(); // it is destroyed so not returned to factory
            try {
                algoParameters.ultiJMolBuffer.put(new MyJmol1462());
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            String message = "Exception in scoreByMinimizingLigandOnFixedReceptor";
            ExceptionInScoringUsingBioJavaJMolGUI exception = new ExceptionInScoringUsingBioJavaJMolGUI(message);
            throw exception;
        }

        try {
            ultiJmol.jmolPanel.evalString("zap");
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {

            }
            algoParameters.ultiJMolBuffer.put(ultiJmol);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return hitScore;
    }


    /*
    public static ResultsUltiJMolMinimizeSideChain minimizeSideChainOfAProtonatedMyStructure(AlgoParameters algoParameters, MyStructureIfc myStructureInput, char[] chainid, int residueID, char[] monomerTochangeThreeLettercode) throws ExceptionInScoringUsingBioJavaJMolGUI {

        ResultsUltiJMolMinimizeSideChain resultsUltiJMolMinimizeSideChain = null;

        MyStructureIfc clonedMyStructure = null;
        try {
            clonedMyStructure = myStructureInput.cloneWithSameObjects();
        } catch (ExceptionInMyStructurePackage e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        MyChainIfc chain = clonedMyStructure.getAminoMyChain(chainid);
        MyMonomerIfc monomerToModify = chain.getMyMonomerFromResidueId(residueID);

        MyJmol1462 ultiJMol = null;

        try {
            ultiJMol = algoParameters.ultiJMolBuffer.get();
            float energyTargetBefore = 0; // MyJmolTools.loadMyStructureInBiojavaMinimizeHydrogensComputeEnergyUFF(clonedMyStructure, ultiJMol, algoParameters);
            System.out.println("EnergyTargetBefore = " + energyTargetBefore);

            // make V3000 and identify atomIds to minimize based on coords
            String structureProtonatedV3000 = clonedMyStructure.toV3000();
            List<MyAtomIfc> atomsToMinimize = new ArrayList<>();
            atomsToMinimize.addAll(Arrays.asList(monomerToModify.getMyAtoms()));

            List<Integer> atomids = MyJmolTools.findAtomIds(structureProtonatedV3000, atomsToMinimize, algoParameters);

            // should always work as continuous but unsure

            String selectResidue = "";
            for (int i = 0; i < atomids.size(); i++) {
                if (i != atomids.size() - 1) {
                    selectResidue += "atomno = " + atomids.get(i) + " or ";
                } else {
                    selectResidue += "atomno = " + atomids.get(i);
                }

            }

            //String selectResidue2 = "atomno > " + (atomids.get(0)-1) + " and atomno < " + (atomids.get(atomids.size()-1)+1);


            ultiJMol.jmolPanel.evalString("set forcefield \"UFF\"\n" + "set minimizationsteps 50\n");
            ultiJMol.jmolPanel.evalString("set logLevel 0\nset undo ON\n set echo off\n set useMinimizationThread ON\n");


            String selectTarget = "all and not (" + selectResidue + ")"; //= "atomno < " + atomids.get(0) + " or atomno > " + atomids.get(atomids.size()-1);
            ultiJMol.jmolPanel.evalString("spacefill 400");

            ultiJMol.jmolPanel.evalString("select " + selectTarget);
            //ultiJMol.jmolviewerForUlti.evalString("select " + selectTarget);
            ultiJMol.jmolPanel.evalString("spacefill 200");

            //ultiJMol.jmolviewerForUlti.evalString("minimize FIX {" + selectTarget + "} select {*}\n");

            Minimize minimize = new Minimize(ultiJMol, selectTarget, algoParameters);
            minimize.compute();
            Float energyComplexFinal = minimize.getEnergyComplexFinal();
            Float receptorFixedLigandOptimizedEStart = minimize.getReceptorFixedLigandOptimizedEStart();
            int countIteration = minimize.getCountIteration();
            boolean receptorFixedLigandOptimizedConvergenceReached = minimize.isReceptorFixedLigandOptimizedConvergenceReached();


            String structureWithMinimizedSideChain = ultiJMol.jmolPanel.getViewer().getData("*", "V3000");

            // what I need is the energy of the side chain
            // so I should delete all but this and get the energy
            // now I do stuff to compute rmsd
            ultiJMol.jmolPanel.evalString("delete (" + selectTarget + ") \n");
            Thread.sleep(1000L);
            String sidechainMinizedWithCutBond = ultiJMol.jmolPanel.getViewer().getData("*", "V3000");

            // but has a cut bond, not good for evaluating forcefield energy ...
            MyStructureIfc sidechainMinizedReprotonated = MyJmolTools.protonateStructure(sidechainMinizedWithCutBond, algoParameters);


            ultiJMol.jmolPanel.evalString("zap");
            float energyMinimizedSideChainStrained = 0; // MyJmolTools.computeEnergyForInPutV3000(ultiJMol, algoParameters, sidechainMinizedReprotonated.toV3000(), true);
            float energyMinimizedFullyMinimized = 0; //MyJmolTools.computeEnergyForInPutV3000(ultiJMol, algoParameters, sidechainMinizedReprotonated.toV3000(), false);
            Thread.sleep(1000L);
            System.out.println("E sideChainStrained = " + energyMinimizedSideChainStrained);
            System.out.println("E sideChain Relaxed = " + energyMinimizedFullyMinimized);

            float strainedEnergy = energyMinimizedSideChainStrained - energyMinimizedFullyMinimized; // if strained then energyMinimizedLigand > energyPeptideRelaxed so positive result
            System.out.println("E strained = " + strainedEnergy);

            // I would then need a new MyStructure cloned from original one
            // I would only change the coodinates of modified atom
            MyStructureIfc clonedMyStructureToReturn = myStructureInput.cloneWithSameObjects();
            MyChainIfc chainToModifyFinal = clonedMyStructure.getAminoMyChain(chainid);
            MyMonomerIfc monomerToModifyFinal = chain.getMyMonomerFromResidueId(residueID);

            MyStructureIfc myStructureFromV3000 = new MyStructure(structureWithMinimizedSideChain, algoParameters);

            int currentIdInModifiedMonomer = 0;
            for (int atomId : atomids) {
                MyAtomIfc atomWithMinimizedCoords = myStructureFromV3000.getAllAminochains()[0].getMyMonomers()[0].getMyAtoms()[atomId];
                MyAtomIfc atom = monomerToModifyFinal.getMyAtoms()[currentIdInModifiedMonomer];
                atom.setCoords(atomWithMinimizedCoords.getCoords().clone());
                currentIdInModifiedMonomer += 1;
            }
            resultsUltiJMolMinimizeSideChain = new ResultsUltiJMolMinimizeSideChain(clonedMyStructureToReturn, strainedEnergy);

        } catch (Exception e) {

            System.out.println("Exception in  !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            ultiJMol.frame.dispose(); // it is destroyed so not returned to factory
            try {
                algoParameters.ultiJMolBuffer.put(new MyJmol1462());
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            String message = "Exception in ";
            ExceptionInScoringUsingBioJavaJMolGUI exception = new ExceptionInScoringUsingBioJavaJMolGUI(message);
            throw exception;
        }

        try {
            ultiJMol.jmolPanel.evalString("zap");
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {

            }
            algoParameters.ultiJMolBuffer.put(ultiJMol);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return resultsUltiJMolMinimizeSideChain;
    }
*/

    public static Float getEnergyBiojavaJmolNewCode(MyJmol1462 ultiJMol, AlgoParameters algoParameters) throws ExceptionInScoringUsingBioJavaJMolGUI {

        Float energy = waitMinimizationEnergyAvailable(2, ultiJMol);
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
        sb.append("spacefill 400\n");
        if (selectStringTargetNotToFix != null) {
            sb.append("select {" + selectStringTargetNotToFix + "}\n");
            sb.append("spacefill 400\n");
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


    //-------------------------------------------------------------
    // Implementation
    //-------------------------------------------------------------
    private static MyStructureIfc protonateStructureUsingJMolUFF(MyStructureIfc myStructureInput, AlgoParameters algoParameters) throws ExceptionInScoringUsingBioJavaJMolGUI {

        MyStructureIfc clonedMyStructureThatCouldHaveHydrogens = null;
        try {
            clonedMyStructureThatCouldHaveHydrogens = myStructureInput.cloneWithSameObjects();
        } catch (ExceptionInMyStructurePackage e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }

        MyJmol1462 ultiJMol = null;
        String readV3000;
        try {
            ultiJMol = algoParameters.ultiJMolBuffer.get();
            //(ultiJMol, myStructure, algoParameters, filenameV3000);


            //deleteFileIfExist(outputFileName);
            addHydrogensInJMolUsingUFF(ultiJMol, clonedMyStructureThatCouldHaveHydrogens, algoParameters);
            readV3000 = ultiJMol.jmolPanel.getViewer().getData("*", "V3000");
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {

            }
        } catch (Exception e) {
            System.out.println("Exception in protonation !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            ultiJMol.frame.dispose(); // it is destroyed so not returned to factory
            try {
                algoParameters.ultiJMolBuffer.put(new MyJmol1462());
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            e.printStackTrace();
            String message = "Exception in protonation";
            ExceptionInScoringUsingBioJavaJMolGUI exception = new ExceptionInScoringUsingBioJavaJMolGUI(message);
            throw exception;
        }
        try {
            ultiJMol.jmolPanel.evalString("zap");
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {

            }
            algoParameters.ultiJMolBuffer.put(ultiJMol);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        MyStructureIfc myStructureWithBondsAndHydrogenAtoms = null;
        try {
            myStructureWithBondsAndHydrogenAtoms = new MyStructure(readV3000, algoParameters);
        } catch (ExceptionInMyStructurePackage e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        myStructureWithBondsAndHydrogenAtoms.setFourLetterCode(myStructureInput.getFourLetterCode());
        addHydrogenInformation(clonedMyStructureThatCouldHaveHydrogens, myStructureWithBondsAndHydrogenAtoms);

        return clonedMyStructureThatCouldHaveHydrogens;
    }


    private static void addHydrogensInJMolUsingUFF(MyJmol1462 ultiJmol, MyStructureIfc myStructure, AlgoParameters algoParameters) throws ExceptionInScoringUsingBioJavaJMolGUI {

        try {
            String v3000 = myStructure.toV3000();
            ultiJmol.jmolPanel.openStringInline(v3000);

            String selectString = "hydrogen";
            ultiJmol.jmolPanel.evalString("delete " + selectString);

            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {

            }

            ultiJmol.jmolPanel.evalString("set forcefield \"MMFF94\"\n" + "set minimizationsteps 20\n");
            ultiJmol.jmolPanel.evalString("minimize energy ADDHYDROGENS\n");

            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
            }

            int maxCountIteration = 200;
            int countIteration = 0;
            while (ultiJmol.jmolPanel.getViewer().areHydrogenAdded() == false) {
                countIteration += 1;
                if (countIteration > maxCountIteration) {
                    String message = "problem in addHydrogensInJMolUsingUFF : too many iteration to wait for adding hydrogens";
                    ExceptionInScoringUsingBioJavaJMolGUI exception = new ExceptionInScoringUsingBioJavaJMolGUI(message);
                    throw exception;
                }
                try {
                    Thread.sleep(2000L);
                } catch (InterruptedException e) {
                }
            }
            try { // I think important as there is some time needed after adding h and minimization them
                // it works thanks to q hqck in Jmol project
                Thread.sleep(3000L);
            } catch (InterruptedException e) {

            }
        } catch (Exception e) {
            e.printStackTrace();
            String message = "add hydrogens and minimized failed at ";
            ExceptionInScoringUsingBioJavaJMolGUI exception = new ExceptionInScoringUsingBioJavaJMolGUI(message);
            throw exception;
        }
    }


    private static void addHydrogenInformation(MyStructureIfc myStructureThatCouldHaveHydrogens, MyStructureIfc myStructureWithBondsAndHydrogenAtoms) {

        // remove hydrogens and bond to hydrogens: sometimes there are and also for Xray structure
        //		for (MyChainIfc chain: myStructure.getAllChains()){
        //			for (MyMonomerIfc monomer: chain.getMyMonomers()){
        //
        //			}
        //		}

        MyStructureTools.removeAllExplicitHydrogens(myStructureThatCouldHaveHydrogens);
        Map<MyAtomIfc, MyAtomIfc> mapToGetCorrespondingAtomWithInformation = buildCorrespondanceHeavyAtomsMapBasedOnAtomXYZ(myStructureWithBondsAndHydrogenAtoms, myStructureThatCouldHaveHydrogens);

        // I loop on existing bonds and create bonds with new references
        // I do it here as it is a special MyStructure with only one chain: so not good to put that in a library and not needed
        MyChainIfc chain = myStructureWithBondsAndHydrogenAtoms.getAllAminochains()[0];
        MyMonomerIfc onlyMonomerInMyStructure = chain.getMyMonomers()[0];

        for (MyAtomIfc atom : onlyMonomerInMyStructure.getMyAtoms()) {

            if (Arrays.equals(atom.getElement(), "H".toCharArray())) {
                continue;
            }
            // translate this bonds into myStructure
            MyAtomIfc atomInMyStructure = mapToGetCorrespondingAtomWithInformation.get(atom);

            MyBondIfc[] bonds = atom.getBonds();
            if (bonds == null) { // there are atoms without any bonds
                continue;
            }
            List<MyBondIfc> correspondingBonds = new ArrayList<>();
            for (MyBondIfc bond : bonds) {
                if (Arrays.equals(bond.getBondedAtom().getElement(), "H".toCharArray())) { // it wont work to create bonds with H as H are not in correspondance map
                    continue;
                }
                MyAtomIfc atombondedInMyStructure = mapToGetCorrespondingAtomWithInformation.get(bond.getBondedAtom());
                MyBondIfc correspondingBond = null;
                try {
                    correspondingBond = new MyBond(atombondedInMyStructure, bond.getBondOrder());
                } catch (ExceptionInMyStructurePackage e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                correspondingBonds.add(correspondingBond);
            }
            MyBondIfc[] correspondingBondsArray = correspondingBonds.toArray(new MyBondIfc[correspondingBonds.size()]);
            atomInMyStructure.setBonds(correspondingBondsArray);

        }

        // Now I add Hydrogens
        int atomCountWithoutHydrogen = onlyMonomerInMyStructure.getMyAtoms().length;
        int countAtom = atomCountWithoutHydrogen;
        Map<MyAtomIfc, List<MyAtomIfc>> mapHeavyAtomAndHydrogen = buildMapHeavyAtomAndHydrogen(myStructureWithBondsAndHydrogenAtoms);

        int heavyAtomCount = 1;
        for (Entry<MyAtomIfc, List<MyAtomIfc>> entry : mapHeavyAtomAndHydrogen.entrySet()) {


            MyAtomIfc heavyAtomInMyStructure = mapToGetCorrespondingAtomWithInformation.get(entry.getKey());
            if (heavyAtomInMyStructure == null) {
                System.out.println();
                continue;
            }


            int hydrogenCountForThisHeavyAtom = 1;

            for (MyAtomIfc hydrogenAtom : entry.getValue()) {

                //String atomName = "H" + hydrogenCountForThisHeavyAtom + String.valueOf(heavyAtomInMyStructure.getAtomName());

                countAtom += 1;
                char[] heavyAtomNameToUse;
                if (heavyAtomInMyStructure.getAtomName().length == 0) {
                    heavyAtomNameToUse = ("_" + String.valueOf(heavyAtomCount)).toCharArray(); // structure coming from V3000 doesnt have atom names !!
                } else {
                    heavyAtomNameToUse = heavyAtomInMyStructure.getAtomName();
                }
                char[] hydrogenName = MyStructureTools.makeHydrogenAtomName(hydrogenCountForThisHeavyAtom, String.valueOf(heavyAtomInMyStructure.getAtomName()));


                MyAtomIfc newHydrogen = null;
                try {
                    newHydrogen = new MyAtom("H".toCharArray(), hydrogenAtom.getCoords(), hydrogenName, countAtom);
                } catch (ExceptionInMyStructurePackage e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // need parent
                newHydrogen.setParent(heavyAtomInMyStructure.getParent());

                // need bond
                MyBondIfc bondHtoHeavyAtom = null;
                try {
                    bondHtoHeavyAtom = new MyBond(heavyAtomInMyStructure, 1);
                } catch (ExceptionInMyStructurePackage e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                MyBondIfc[] bondsHtoHeavyAtom = new MyBondIfc[1];
                bondsHtoHeavyAtom[0] = bondHtoHeavyAtom;
                newHydrogen.setBonds(bondsHtoHeavyAtom);


                MyBondIfc bondHeavyAtomToH = null;
                try {
                    bondHeavyAtomToH = new MyBond(newHydrogen, 1);
                } catch (ExceptionInMyStructurePackage e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                heavyAtomInMyStructure.addBond(bondHeavyAtomToH);

                // need to add the H in the right monomer
                MyMonomerIfc monomerToWhichHydrogenBelongs = newHydrogen.getParent();
                monomerToWhichHydrogenBelongs.addAtom(newHydrogen);

                // char[] element, float[] coords, char[] atomName, int originalAtomId
                hydrogenCountForThisHeavyAtom += 1;
            }
        }
    }


    private static Map<MyAtomIfc, List<MyAtomIfc>> buildMapHeavyAtomAndHydrogen(MyStructureIfc myStructureWithBondsAndHydrogenAtoms) {

        Map<MyAtomIfc, List<MyAtomIfc>> mapHeavyAtomAndHydrogens = new HashMap<>();
        MyChainIfc chain = myStructureWithBondsAndHydrogenAtoms.getAllAminochains()[0];
        MyMonomerIfc onlyMonomerInMyStructure = chain.getMyMonomers()[0];

        for (MyAtomIfc atom : onlyMonomerInMyStructure.getMyAtoms()) {
            if (Arrays.equals(atom.getElement(), "H".toCharArray())) {
                MyAtomIfc heavyAtom = atom.getBonds()[0].getBondedAtom();
                if (!Arrays.equals(heavyAtom.getElement(), "H".toCharArray())) { // that happen with 1A5H
                    AddToMap.addElementToAMapOfList(mapHeavyAtomAndHydrogens, heavyAtom, atom);
                }
            }
        }
        return mapHeavyAtomAndHydrogens;
    }


    private static Map<MyAtomIfc, MyAtomIfc> buildCorrespondanceHeavyAtomsMapBasedOnAtomXYZ(MyStructureIfc myStructure1, MyStructureIfc myStructure2) {

        Map<MyAtomIfc, MyAtomIfc> matchingAtoms = new HashMap<>();

        for (MyChainIfc chain1 : myStructure1.getAllChains()) {
            for (MyMonomerIfc monomer1 : chain1.getMyMonomers()) {
                A:
                for (MyAtomIfc atom1 : monomer1.getMyAtoms()) {

                    if (Arrays.equals(atom1.getElement(), "H".toCharArray())) {
                        continue;
                    }
                    float[] coords1 = atom1.getCoords();

                    for (MyChainIfc chain2 : myStructure2.getAllChains()) {
                        for (MyMonomerIfc monomer2 : chain2.getMyMonomers()) {
                            for (MyAtomIfc atom2 : monomer2.getMyAtoms()) {
                                if (Arrays.equals(atom2.getElement(), "H".toCharArray())) {
                                    continue;
                                }
                                float[] coords2 = atom2.getCoords();

                                boolean matching = doCoordinatesMAtchForSameAtomToOnlyNumericalError(coords1, coords2);
                                if (matching == true) {
                                    matchingAtoms.put(atom1, atom2);
                                    continue A;
                                }
                            }
                        }
                    }
                }
            }
        }
        return matchingAtoms;
    }


    public static boolean doCoordinatesMAtchForSameAtomToOnlyNumericalError(float[] coords1, float[] coords2) {

        float numericalError = 0.1f;
        for (int i = 0; i < 3; i++) {
            if (Math.abs(coords1[i] - coords2[i]) > numericalError) {
                return false;
            }
        }
        return true;
    }


    private static Float waitMinimizationEnergyAvailable(int waitTimeSeconds, MyJmol1462 ultiJMol) throws ExceptionInScoringUsingBioJavaJMolGUI {

        int maxIteration = 20;
        int countIteration = 0;

        Minimizer minimizer = ultiJMol.jmolPanel.getViewer().getMinimizer(true);

        while (minimizer == null || minimizer.getMinimizationEnergy() == null) {
            try {
                Thread.sleep(waitTimeSeconds * 1000);
                countIteration += 1;
                System.out.println(countIteration);
                //System.out.println(countIteration);
                if (countIteration > maxIteration) {
                    String message = "Wait for Minimization Energy to be available failed because too many iterations :  ";
                    ExceptionInScoringUsingBioJavaJMolGUI exception = new ExceptionInScoringUsingBioJavaJMolGUI(message);
                    throw exception;
                }
            } catch (InterruptedException e) {
                String message = "Wait for Minimization Energy to be available failed because of Exception";
                ExceptionInScoringUsingBioJavaJMolGUI exception = new ExceptionInScoringUsingBioJavaJMolGUI(message);
                throw exception;
            }
            minimizer = ultiJMol.jmolPanel.getViewer().getMinimizer(true);
        }
        return minimizer.getMinimizationEnergy();
    }


    public static List<Integer> findAtomIds(String structureV3000, List<MyAtomIfc> atomsToFindIdsOf, AlgoParameters algoParameters) {

        List<Integer> atomIds = new ArrayList<>();
        MyStructure mystructureFromV3000 = null;
        try {
            mystructureFromV3000 = new MyStructure(structureV3000, algoParameters);
        } catch (ExceptionInMyStructurePackage e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } // I know this one is linear to V3000

        for (MyAtomIfc atomToFind : atomsToFindIdsOf) {
            if (MyStructureTools.isAtomFromBackBoneOtherThanCA(atomToFind) == true) {
                continue;
            }
            float[] coordsToFindId = atomToFind.getCoords();
            Integer foundId = findId(mystructureFromV3000, coordsToFindId);
            atomIds.add(foundId);
        }
        return atomIds;
    }


    public static List<Integer> findAtomIdsWithBackbone(String structureV3000, List<MyAtomIfc> atomsToFindIdsOf, AlgoParameters algoParameters) {

        List<Integer> atomIds = new ArrayList<>();
        MyStructure mystructureFromV3000 = null;
        try {
            mystructureFromV3000 = new MyStructure(structureV3000, algoParameters);
        } catch (ExceptionInMyStructurePackage e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } // I know this one is linear to V3000

        for (MyAtomIfc atomToFindId : atomsToFindIdsOf) {

            float[] coordsToFindId = atomToFindId.getCoords();
            Integer foundId = findId(mystructureFromV3000, coordsToFindId);
            atomIds.add(foundId);
        }
        return atomIds;
    }


    private static Integer findId(MyStructure mystructureFromV3000, float[] coordsToFindId) {
        List<Integer> foundId = new ArrayList<>();
        float threshold = MyStructureConstants.TOLERANCE_FOR_SAME_COORDINATES;
        int currentId = 0;
        Atoms:
        for (MyAtomIfc atom : mystructureFromV3000.getAllAminochains()[0].getMyMonomers()[0].getMyAtoms()) {
            currentId += 1;
            for (int i = 0; i < 3; i++) {
                float candidateCoord = atom.getCoords()[i];
                float coordToFind = coordsToFindId[i];
                if (Math.abs(candidateCoord - coordToFind) > threshold) {
                    continue Atoms; // because unmatch
                }
            }
            // so it matches
            foundId.add(currentId);
        }
        if (foundId.size() != 1) {
            System.out.println("Unbearable error in findId");
            System.exit(0);
        }
        return foundId.get(0);
    }


    private void generateeparam(String fileName, String fullPath, String type) {
        int width = 100;
        int height = 100;
        Map<String, Object> eparams = new Hashtable<String, Object>();
        eparams.put("type", type);
        if (fileName != null)
            eparams.put("fileName", fileName);
        //if (isCommand || fileName != null)
        //if (fileName != null)
        eparams.put("fullPath", fullPath);
        eparams.put("width", Integer.valueOf(width));
        eparams.put("height", Integer.valueOf(height));
    }

}
