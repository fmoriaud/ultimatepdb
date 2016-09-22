package ultiJmol1462;

import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import org.jmol.minimize.Minimizer;
import parameters.AlgoParameters;

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

    private MyJmol1462 ultiJmol;
    private Map<String, Object> results = new LinkedHashMap<>();

    private boolean scriptIsMinimizing = false;

    // -------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------

    /**
     * Uses an existing ultiJmol that will be cleared
     *
     * @param script
     * @param moleculeV3000
     * @param ultiJmol
     */
    public ScriptCommandOnUltiJmol(String script, String moleculeV3000, MyJmol1462 ultiJmol) {

        this.script = script;
        this.moleculeV3000 = moleculeV3000;
        this.ultiJmol = ultiJmol;
        ultiJmol.jmolPanel.evalString("zap");
    }


    /**
     * Uses a newly created Jmol
     *
     * @param script
     * @param moleculeV3000
     * @param algoParameters
     */
    public ScriptCommandOnUltiJmol(String script, String moleculeV3000, AlgoParameters algoParameters) {

        this.script = script;
        this.moleculeV3000 = moleculeV3000;
        try {
            ultiJmol = algoParameters.ultiJMolBuffer.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    // -------------------------------------------------------------------
    // Public && Interface method
    // -------------------------------------------------------------------
    public void execute() {

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ultiJmol.jmolPanel.openStringInline(moleculeV3000);

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

                Float currentEnergy = null;
                try {
                    currentEnergy = getEnergyBiojavaJmolNewCode(ultiJmol);
                } catch (ExceptionInScoringUsingBioJavaJMolGUI exceptionInScoringUsingBioJavaJMolGUI) {
                    exceptionInScoringUsingBioJavaJMolGUI.printStackTrace();
                }
                if (currentEnergy == null) {
                    System.out.println();
                }
                countIteration += 1;

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

            System.out.println("did " + countIteration + " iterations");
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

            try {
                energy = getEnergyBiojavaJmolNewCode(ultiJmol);
            } catch (ExceptionInScoringUsingBioJavaJMolGUI exceptionInScoringUsingBioJavaJMolGUI) {
                exceptionInScoringUsingBioJavaJMolGUI.printStackTrace();
            }
            ultiJmol.jmolPanel.evalString("minimize stop");

            // ??
            ultiJmol.jmolPanel.evalString("minimize FIX {*}");
            ultiJmol.jmolPanel.evalString("select {*}\n");

            results.put("convergence reached", convergenceReached);
            results.put("final energy", energy);
        }
    }


    //-------------------------------------------------------------
    // Implementation
    //-------------------------------------------------------------
    private Float getEnergyBiojavaJmolNewCode(MyJmol1462 ultiJMol) throws ExceptionInScoringUsingBioJavaJMolGUI {

        Float energy = waitMinimizationEnergyAvailable(2, ultiJMol);
        if (energy == null) {
            String message = "waitMinimizationEnergyAvailable failed";
            ExceptionInScoringUsingBioJavaJMolGUI exception = new ExceptionInScoringUsingBioJavaJMolGUI(message);
            throw exception;
        }
        return energy;
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




    //------------------------
    // Getter and Setter
    //------------------------
    public Map<String, Object> getResults() {
        return results;
    }

}
