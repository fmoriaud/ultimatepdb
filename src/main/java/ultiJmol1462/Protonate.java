package ultiJmol1462;

import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import jmolgui.UltiJmol1462;
import math.AddToMap;
import mystructure.*;
import org.jmol.minimize.Minimizer;
import parameters.AlgoParameters;

import java.util.*;

/**
 * Created by Fabrice on 28/09/16.
 */
public class Protonate {
    // -------------------------------------------------------------------
    // Class variables
    // -------------------------------------------------------------------
    private MyStructureIfc myStructure;
    private AlgoParameters algoParameters;

    private Map<Results, Object> results = new LinkedHashMap<>();
    private MyStructureIfc protonatedMyStructure;


    // -------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------

    /**
     * Assume there are no hydrogens
     * Uses a MyJmol from algoParameters
     *
     * @param myStructure
     * @param algoParameters
     */
    public Protonate(MyStructureIfc myStructure, AlgoParameters algoParameters) {
        this.myStructure = myStructure;
        this.algoParameters = algoParameters;
    }


    // -------------------------------------------------------------------
    // Public && Interface method
    // -------------------------------------------------------------------
    public void compute() throws ExceptionInScoringUsingBioJavaJMolGUI {


        SafeUltiJmolUsage safeUltiJmolUsage = new SafeUltiJmolUsage(algoParameters);
        ProtonateTask protonateTask = new ProtonateTask(myStructure, algoParameters);
        safeUltiJmolUsage.setDoMyJmolTaskIfc(protonateTask);

        safeUltiJmolUsage.run();
        results = safeUltiJmolUsage.getResults();

        protonatedMyStructure = (MyStructureIfc) results.get(Results.PROTONATED_STRUCTURE);

    }


    // -------------------------------------------------------------------
    // Getter and Setter
    // -------------------------------------------------------------------
    public MyStructureIfc getProtonatedMyStructure() {
        return protonatedMyStructure;
    }

    public Map<Results, Object> getResults() {
        return results;
    }
}
