package ultiJmol1462;

import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import jmolgui.UltiJmol1462;
import org.jmol.minimize.Minimizer;
import parameters.AlgoParameters;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Fabrice on 25/09/16.
 */
public class GetEnergy {

    // -------------------------------------------------------------------
    // Class variables
    // -------------------------------------------------------------------
    private String moleculeV3000;
    private AlgoParameters algoParameters;
    private String script;

    private Map<String, Object> results = new LinkedHashMap<>();
    private boolean convergenceReached = true;

    // -------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------

    /**
     * Uses a MyJmol from algoParameters
     * @param script
     * @param moleculeV3000
     * @param algoParameters
     */
    public GetEnergy(String script, String moleculeV3000, AlgoParameters algoParameters) {

        this.script = script;
        this.moleculeV3000 = moleculeV3000;
        this.algoParameters = algoParameters;
    }


    // -------------------------------------------------------------------
    // Public && Interface method
    // -------------------------------------------------------------------
    public void execute() throws ExceptionInScoringUsingBioJavaJMolGUI {


        SafeUltiJmolUsage safeUltiJmolUsage = new SafeUltiJmolUsage(algoParameters);
        GetEnergyTask getEnergyTask = new GetEnergyTask(script, moleculeV3000);
        safeUltiJmolUsage.setDoMyJmolTaskIfc(getEnergyTask);

        safeUltiJmolUsage.run();
        results = safeUltiJmolUsage.getResults();

    }



    //------------------------
    // Getter and Setter
    //------------------------
    public Map<String, Object> getResults() {
        return results;
    }

    public boolean isConvergenceReached() {
        return convergenceReached;
    }
}
