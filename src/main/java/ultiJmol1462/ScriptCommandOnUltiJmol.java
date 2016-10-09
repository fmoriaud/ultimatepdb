package ultiJmol1462;

import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import org.jmol.minimize.Minimizer;
import parameters.AlgoParameters;
import ultimatepdb.UltiJmol1462;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Fabrice on 22/09/16.
 */
public class ScriptCommandOnUltiJmol {
    // -------------------------------------------------------------------
    // Class variables
    // -------------------------------------------------------------------
    private String script;
    private String moleculeV3000;
    private AlgoParameters algoParameters;

    private UltiJmol1462 ultiJmol;
    private Map<String, Object> results = new LinkedHashMap<>();

    private Integer atomCountTarget;

    private boolean scriptIsMinimizing = false;

    // -------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------

    /**
     * Uses an existing ultiJmol that will be cleared
     * Minimize what is unfixed in the script
     * Energy returned is related to only what is unfixed
     *
     * @param script
     * @param moleculeV3000
     * @param ultiJmol
     */
    public ScriptCommandOnUltiJmol(String script, String moleculeV3000, UltiJmol1462 ultiJmol, Integer atomCountTarget) {

        this.script = script;
        this.moleculeV3000 = moleculeV3000;
        this.ultiJmol = ultiJmol;
        this.atomCountTarget = atomCountTarget;
        this.algoParameters = algoParameters;
        ultiJmol.jmolPanel.evalString("zap");
    }


    /**
     * Uses a newly created Jmol
     * Minimize what is unfixed in the script
     * Energy returned is related to only what is unfixed
     *
     * @param script
     * @param moleculeV3000
     * @param algoParameters
     */
    public ScriptCommandOnUltiJmol(String script, String moleculeV3000, AlgoParameters algoParameters, Integer atomCountTarget) {

        this.script = script;
        this.moleculeV3000 = moleculeV3000;
        this.algoParameters = algoParameters;
        try {
            ultiJmol = algoParameters.ultiJMolBuffer.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.atomCountTarget = atomCountTarget;
    }


    // -------------------------------------------------------------------
    // Public && Interface method
    // -------------------------------------------------------------------

    public void execute() throws ExceptionInScoringUsingBioJavaJMolGUI {

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
            int maxIteration = 20;
            boolean goAhead = true;
            while (countIteration <= maxIteration && goAhead == true) {

                try {
                    Thread.sleep(4000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                countIteration += 1;
                // Energy there is a relative indicator
                // Only relates to what is unfixed in the minimization
                float currentEnergy = 0;
                try {
                    currentEnergy = getEnergyBiojavaJmolNewCode(ultiJmol);
                } catch (ExceptionInScoringUsingBioJavaJMolGUI exceptionInScoringUsingBioJavaJMolGUI) {
                    throw exceptionInScoringUsingBioJavaJMolGUI;
                }
                System.out.println("currentEnergy = " + currentEnergy);

                // when too high then I should give up
                if (currentEnergy > 1E8) {
                    //System.out.println("Minimization is aborted as energy is > 1E8 ");
                    //return null;
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
                e.printStackTrace();
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
                e.printStackTrace();
            }

            // as i dont know what is does in jmol, I take the energy before stoping
            ultiJmol.jmolPanel.evalString("minimize stop");
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            results.put("convergence reached", convergenceReached);
            //results.put("final energy", energy);


            // ?????????????????
            //ultiJmol.jmolPanel.evalString("minimize energy");

            //System.out.println("final energy = " + finalEnergy);
            results.put("final energy", finalEnergy);

            String structureV3000 = ultiJmol.jmolPanel.getViewer().getData("*", "V3000");
            results.put("structureV3000", structureV3000);

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
                results.put("ligand", ligand);

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
                results.put("target", target);
            }
        }

        boolean success = MyJmolTools.putBackUltiJmolInBufferAndIfFailsPutNewOne(ultiJmol, algoParameters);
        System.out.println(" success = " + success);
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


    //------------------------
    // Getter and Setter
    //------------------------
    public Map<String, Object> getResults() {
        return results;
    }

}
