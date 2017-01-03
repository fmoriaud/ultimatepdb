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
public class GetEnergyTask implements DoMyJmolTaskIfc {

    // -------------------------------------------------------------------
    // Class variables
    // -------------------------------------------------------------------
    private String moleculeV3000;
    private String script;

    private Map<Results, Object> results = new LinkedHashMap<>();
    private String name;



    // -------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------
    public GetEnergyTask(String script, String moleculeV3000) {

        this.script = script;
        this.moleculeV3000 = moleculeV3000;
        this.name = "GetEnergyTask";
    }


    // -------------------------------------------------------------------
    // Public && Interface method
    // -------------------------------------------------------------------
    @Override
    public boolean doAndReturnConvergenceStatus(UltiJmol1462 ultiJmol) {


        ultiJmol.jmolPanel.openStringInline(moleculeV3000);

        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
           return false;
        }


        String newScript = script.replace("set minimizationsteps 50", "set minimizationsteps 0");

        ultiJmol.jmolPanel.evalString(newScript);

        if (!script.contains("minimize")) {
            return false;
        }

        Float energyAsInitialAsPossible = null;
        try {
            energyAsInitialAsPossible = waitMinimizationEnergyAvailable(ultiJmol);
        } catch (ExceptionInScoringUsingBioJavaJMolGUI exceptionInScoringUsingBioJavaJMolGUI) {
            exceptionInScoringUsingBioJavaJMolGUI.printStackTrace();
            return false;
        }
        if (energyAsInitialAsPossible == null) {
            return false;
        }
        results.put(Results.INITIAL_ENERGY, energyAsInitialAsPossible);
        // Whatever is the minimize script which contains what to fix and that matters for the energy
        ultiJmol.jmolPanel.evalString("minimize clear");

        try {
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            return false;
        }

        ultiJmol.jmolPanel.evalString("minimize energy");
        try {
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            return false;
        }

        ultiJmol.jmolPanel.evalString("show minimization");

        try {
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            return false;
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
    private Float waitMinimizationEnergyAvailable(UltiJmol1462 ultiJmol) throws ExceptionInScoringUsingBioJavaJMolGUI {

        int maxIteration = 20;
        int countIteration = 0;

        long waitTimeMillisecond = 1000;
        Minimizer minimizer = ultiJmol.jmolPanel.getViewer().getMinimizer(true);

        // if not ok jmol returns 0.0 which is very unlikely
        while (minimizer == null || minimizer.getMinimizationEnergy() == null || Math.abs(minimizer.getMinimizationEnergy()) < 0.01) {
            try {
                Thread.sleep(waitTimeMillisecond);
                countIteration += 1;
                //System.out.println(countIteration);
                //System.out.println(countIteration);
                if (countIteration > maxIteration) {
                    return null;
                }
            } catch (InterruptedException e) {
                return null;
            }
            minimizer = ultiJmol.jmolPanel.getViewer().getMinimizer(true);
        }
        return minimizer.getMinimizationEnergy();
    }
}
