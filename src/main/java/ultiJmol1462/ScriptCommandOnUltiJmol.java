package ultiJmol1462;

import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import jmolgui.UltiJmol1462;
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
    private AlgoParameters algoParameters;

    private UltiJmol1462 ultiJmol;
    private Map<String, Object> results = new LinkedHashMap<>();

    private Integer atomCountTarget;

    private boolean scriptIsMinimizing = false;


    // -------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------
    /**
     * Uses a MyJmol from algoParameters
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
        this.atomCountTarget = atomCountTarget;
    }


    // -------------------------------------------------------------------
    // Public && Interface method
    // -------------------------------------------------------------------

    public void execute() throws ExceptionInScoringUsingBioJavaJMolGUI {


        SafeUltiJmolUsage safeUltiJmolUsage = new SafeUltiJmolUsage(algoParameters);
        ScriptCommandOnUltiJmolTask scriptCommandOnUltiJmolTask = new ScriptCommandOnUltiJmolTask(script, moleculeV3000, atomCountTarget);
        safeUltiJmolUsage.setDoMyJmolTaskIfc(scriptCommandOnUltiJmolTask);

        safeUltiJmolUsage.run();
        results = safeUltiJmolUsage.getResults();

    }


    //------------------------
    // Getter and Setter
    //------------------------
    public Map<String, Object> getResults() {
        return results;
    }

}
