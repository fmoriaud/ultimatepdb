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
import jmolgui.UltiJmol1462;
import math.AddToMap;
import mystructure.*;
import org.jmol.minimize.Minimizer;
import parameters.AlgoParameters;

import java.util.*;

public class ProtonateTask implements DoMyJmolTaskIfc {
    // -------------------------------------------------------------------
    // Class variables
    // -------------------------------------------------------------------
    private MyStructureIfc myStructure;
    private AlgoParameters algoParameters;
    private MyStructureIfc protonatedMyStructure;
    private Map<Results, Object> results = new LinkedHashMap<>();
    private String name;


    // -------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------
    public ProtonateTask(MyStructureIfc myStructure, AlgoParameters algoParameters) {

        this.myStructure = myStructure;
        this.algoParameters = algoParameters;
        this.name = "ProtonateTask";
    }


    // -------------------------------------------------------------------
    // Public && Interface method
    // -------------------------------------------------------------------
    @Override
    public boolean doAndReturnConvergenceStatus(UltiJmol1462 ultiJmol) throws ExceptionInScoringUsingBioJavaJMolGUI {

        Cloner cloner = new Cloner(myStructure, algoParameters);
        protonatedMyStructure = cloner.getClone();

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            return false;
        }
        String myStructureV3000 = myStructure.toV3000();
        ultiJmol.jmolPanel.openStringInline(myStructureV3000);
        System.out.println("myStructureV3000.length() = " + myStructureV3000.length());
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            return false;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("set forcefield \"MMFF94\"\n" + "set minimizationsteps 20\n");
        sb.append("minimize energy ADDHYDROGENS\n");

        String script = sb.toString();

        ultiJmol.jmolPanel.evalString(script);

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            return false;
        }

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
            float currentEnergy = getEnergyBiojavaJmolNewCode(ultiJmol);

            System.out.println("currentEnergy = " + currentEnergy);

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

        results.put(Results.PROTONATED_STRUCTURE, protonatedMyStructure);
        return true;
    }


    @Override
    public Map<Results, Object> getResults() {
        return results;
    }


    @Override
    public String getName() {
        return name;
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

        MyStructureTools.removeAllExplicitHydrogens(myStructureThatCouldHaveHydrogens);
        Map<MyAtomIfc, MyAtomIfc> mapToGetCorrespondingAtomWithInformation = buildCorrespondanceHeavyAtomsMapBasedOnAtomXYZ(myStructureWithBondsAndHydrogenAtoms, myStructureThatCouldHaveHydrogens);

        MyChainIfc chain = myStructureWithBondsAndHydrogenAtoms.getAllAminochains()[0];
        MyMonomerIfc onlyMonomerInMyStructure = chain.getMyMonomers()[0];

        for (MyAtomIfc atom : onlyMonomerInMyStructure.getMyAtoms()) {

            if (Arrays.equals(atom.getElement(), "H".toCharArray())) {
                continue;
            }
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

        for (Map.Entry<MyAtomIfc, List<MyAtomIfc>> entry : mapHeavyAtomAndHydrogen.entrySet()) {

            MyAtomIfc heavyAtomInMyStructure = mapToGetCorrespondingAtomWithInformation.get(entry.getKey());
            if (heavyAtomInMyStructure == null) {
                System.out.println("weird ...");
                continue;
            }

            int hydrogenCountForThisHeavyAtom = 1;

            for (MyAtomIfc hydrogenAtom : entry.getValue()) {

                countAtom += 1;
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
