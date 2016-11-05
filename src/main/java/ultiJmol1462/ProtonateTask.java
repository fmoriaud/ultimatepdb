package ultiJmol1462;

import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import jmolgui.UltiJmol1462;
import math.AddToMap;
import mystructure.*;
import org.jmol.minimize.Minimizer;
import parameters.AlgoParameters;

import java.util.*;

/**
 * Created by Fabrice on 05/11/16.
 */
public class ProtonateTask implements DoMyJmolTaskIfc {
    // -------------------------------------------------------------------
    // Class variables
    // -------------------------------------------------------------------
    private MyStructureIfc myStructure;
    private AlgoParameters algoParameters;
    private MyStructureIfc protonatedMyStructure;
    private Map<String, Object> results = new LinkedHashMap<>();




    // -------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------
    public ProtonateTask(MyStructureIfc myStructure, AlgoParameters algoParameters) {

        this.myStructure = myStructure;
        this.algoParameters = algoParameters;
    }




    // -------------------------------------------------------------------
    // Public && Interface method
    // -------------------------------------------------------------------
    @Override
    public boolean doAndReturnConvergenceStatus(UltiJmol1462 ultiJmol) {

        Cloner cloner = new Cloner(myStructure, algoParameters);
        protonatedMyStructure = cloner.getClone();

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            return false;
        }
        String myStructureV3000 = myStructure.toV3000();
        ultiJmol.jmolPanel.openStringInline(myStructureV3000);

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            return false;
        }

        String script = MyJmolScripts.getScriptAddHydrogens();
        ultiJmol.jmolPanel.evalString(script);

        boolean convergenceReached = false;

        Float energy = 1E8f;
        int countIteration = 0;
        int maxIteration = 20;
        boolean goAhead = true;
        while (countIteration <= maxIteration && goAhead == true && ultiJmol.jmolPanel.getViewer().areHydrogenAdded() == false) {

            try {
                Thread.sleep(4000L);
            } catch (InterruptedException e) {
                return false;
            }

            countIteration += 1;
            // Energy there is a relative indicator
            // Only relates to what is unfixed in the minimization
            float currentEnergy = 0;
            try {
                currentEnergy = getEnergyBiojavaJmolNewCode(ultiJmol);
            } catch (ExceptionInScoringUsingBioJavaJMolGUI exceptionInScoringUsingBioJavaJMolGUI) {
                return false;
            }
            System.out.println("currentEnergy = " + currentEnergy);

            // when too high then I should give up
            if (currentEnergy > 1E8) {
                return false;
            }

            if (Math.abs(currentEnergy - energy) < 5.0) {
                goAhead = false;
            }
            energy = currentEnergy;
        }

        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            return false;
        }

        String readV3000 = ultiJmol.jmolPanel.getViewer().getData("*", "V3000");
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            return false;
        }

        MyStructureIfc myStructureWithBondsAndHydrogenAtoms = null;
        try {
            myStructureWithBondsAndHydrogenAtoms = new MyStructure(readV3000);
        } catch (ExceptionInMyStructurePackage e) {
            return false;
        }
        myStructureWithBondsAndHydrogenAtoms.setFourLetterCode(myStructure.getFourLetterCode());
        addHydrogenInformation(protonatedMyStructure, myStructureWithBondsAndHydrogenAtoms);

        results.put("protonatedMyStructure", protonatedMyStructure);
        return true;
    }



    @Override
    public Map<String, Object> getResults() {
        return results;
    }




    //-------------------------------------------------------------
    // Implementation
    //-------------------------------------------------------------
    private Float getEnergyBiojavaJmolNewCode(UltiJmol1462 ultiJMol) throws ExceptionInScoringUsingBioJavaJMolGUI {

        Float energy = waitMinimizationEnergyAvailable(2, ultiJMol);
        if (energy == null) {
            String message = "waitMinimizationEnergyAvailable failed";
            ExceptionInScoringUsingBioJavaJMolGUI exception = new ExceptionInScoringUsingBioJavaJMolGUI(message);
            throw exception;
        }
        return energy;
    }


    private Float waitMinimizationEnergyAvailable(int waitTimeSeconds, UltiJmol1462 ultiJMol) throws ExceptionInScoringUsingBioJavaJMolGUI {

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


    private void addHydrogenInformation(MyStructureIfc myStructureThatCouldHaveHydrogens, MyStructureIfc myStructureWithBondsAndHydrogenAtoms) {

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
        for (Map.Entry<MyAtomIfc, List<MyAtomIfc>> entry : mapHeavyAtomAndHydrogen.entrySet()) {


            MyAtomIfc heavyAtomInMyStructure = mapToGetCorrespondingAtomWithInformation.get(entry.getKey());
            if (heavyAtomInMyStructure == null) {
                System.out.println("weird ...");
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


    private Map<MyAtomIfc, List<MyAtomIfc>> buildMapHeavyAtomAndHydrogen(MyStructureIfc myStructureWithBondsAndHydrogenAtoms) {

        Map<MyAtomIfc, List<MyAtomIfc>> mapHeavyAtomAndHydrogens = new LinkedHashMap<>();
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


    private Map<MyAtomIfc, MyAtomIfc> buildCorrespondanceHeavyAtomsMapBasedOnAtomXYZ(MyStructureIfc myStructure1, MyStructureIfc myStructure2) {

        Map<MyAtomIfc, MyAtomIfc> matchingAtoms = new LinkedHashMap<>();

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


    private boolean doCoordinatesMAtchForSameAtomToOnlyNumericalError(float[] coords1, float[] coords2) {

        float numericalError = 0.1f;
        for (int i = 0; i < 3; i++) {
            if (Math.abs(coords1[i] - coords2[i]) > numericalError) {
                return false;
            }
        }
        return true;
    }

}
