package ultiJmol1462;

import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import jmolgui.UltiJmol1462;
import org.jmol.minimize.Minimizer;
import parameters.AlgoParameters;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Fabrice on 05/11/16.
 */
public class ScriptCommandOnUltiJmolTask implements DoMyJmolTaskIfc {
    // -------------------------------------------------------------------
    // Class variables
    // -------------------------------------------------------------------
    private String script;
    private String moleculeV3000;
    private Integer atomCountTarget;

    private boolean scriptIsMinimizing = false;
    private Map<Results, Object> results = new LinkedHashMap<>();

    private String name;

    // -------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------
    public ScriptCommandOnUltiJmolTask(String script, String moleculeV3000, Integer atomCountTarget) {

        this.script = script;
        this.moleculeV3000 = moleculeV3000;
        this.atomCountTarget = atomCountTarget;
        this.name = "ScriptCommandOnUltiJmolTask";
    }

    // -------------------------------------------------------------------
    // Public && Interface method
    // -------------------------------------------------------------------
    @Override
    public boolean doAndReturnConvergenceStatus(UltiJmol1462 ultiJmol) {

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ultiJmol.jmolPanel.openStringInline(moleculeV3000);

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (script.contains("minimize")) {
            scriptIsMinimizing = true;
        }

        ultiJmol.jmolPanel.evalString(script);

        if (scriptIsMinimizing == true) {
            boolean convergenceReached = false;

            Float energy = 1E8f;
            int countIteration = 0;
            int maxIteration = 50;
            boolean goAhead = true;
            while (countIteration <= maxIteration && goAhead == true) {

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

            //System.out.println("did " + countIteration + " iterations");
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                return false;
            }

            if (countIteration <= maxIteration == false) {
                convergenceReached = false;
            } else {
                convergenceReached = true;
            }
            Float finalEnergy = null;
            try {
                finalEnergy = waitMinimizationEnergyAvailable(2, ultiJmol);
            } catch (ExceptionInScoringUsingBioJavaJMolGUI exceptionInScoringUsingBioJavaJMolGUI) {
                exceptionInScoringUsingBioJavaJMolGUI.printStackTrace();
            }

            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                return false;
            }

            // as i dont know what is does in jmol, I take the energy before stoping
            ultiJmol.jmolPanel.evalString("minimize stop");
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                return false;
            }

            results.put(Results.CONVERGENCE_REACHED, convergenceReached);
            results.put(Results.FINAL_ENERGY, finalEnergy);

            String structureV3000 = ultiJmol.jmolPanel.getViewer().getData("*", "V3000");
            results.put(Results.STRUCTURE_V3000, structureV3000);

            ultiJmol.jmolPanel.evalString("minimize clear");

            // do the sperate export of target and ligand when atomCountTarget is not null
            if (atomCountTarget != null) {


                String selectStringTarget = "atomno > 0 and atomno < " + (atomCountTarget + 1);
                String deleteTargetCommand = ("delete (" + selectStringTarget + ") \n");
                ultiJmol.jmolPanel.evalString(deleteTargetCommand);

                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                }
                String ligand = ultiJmol.jmolPanel.getViewer().getData("*", "V3000");
                results.put(Results.LIGAND, ligand);

                ultiJmol.jmolPanel.openStringInline(moleculeV3000);

                String selectStringLigand = "{atomno > " + (atomCountTarget) + "}";
                String deleteLigandCommand = ("delete (" + selectStringLigand + ") \n");
                ultiJmol.jmolPanel.evalString(deleteLigandCommand);


                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                }
                String target = ultiJmol.jmolPanel.getViewer().getData("*", "V3000");
                results.put(Results.TARGET, target);
            }
        }
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


    private static Float waitMinimizationEnergyAvailable(int waitTimeSeconds, UltiJmol1462 ultiJMol) throws ExceptionInScoringUsingBioJavaJMolGUI {

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
}
