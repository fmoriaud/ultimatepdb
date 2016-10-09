package ultiJmol1462;

import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import org.jmol.minimize.Minimizer;
import parameters.AlgoParameters;
import ultimatepdb.UltiJmol1462;

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

    // -------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------

   /*
    public GetEnergy(String script, String moleculeV3000, MyJmol1462 ultiJmol) {

        this.script = script;
        this.moleculeV3000 = moleculeV3000;
        this.ultiJmol = ultiJmol;
        ultiJmol.jmolPanel.evalString("zap");
    }
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

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        UltiJmol1462 ultiJmol = null;
        try {
            ultiJmol = algoParameters.ultiJMolBuffer.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ultiJmol.jmolPanel.openStringInline(moleculeV3000);

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        String newScript = script.replace("set minimizationsteps 50", "set minimizationsteps 0");

        ultiJmol.jmolPanel.evalString(newScript);



        if (!script.contains("minimize")) {
            return;
        }

        Float energyAsInitialAsPossible = waitMinimizationEnergyAvailable(ultiJmol);
        results.put("initial energy", energyAsInitialAsPossible);
        // Whatever is the minimize script which contains what to fix and that matters for the energy
        ultiJmol.jmolPanel.evalString("minimize clear");

        ultiJmol.jmolPanel.evalString("minimize energy");
        ultiJmol.jmolPanel.evalString("show minimization");

        boolean success = MyJmolTools.putBackUltiJmolInBufferAndIfFailsPutNewOne(ultiJmol, algoParameters);
        System.out.println(" success = " + success);

    }


    private Float waitMinimizationEnergyAvailable(UltiJmol1462 ultiJmol) throws ExceptionInScoringUsingBioJavaJMolGUI {

        int maxIteration = 20;
        int countIteration = 0;

        long waitTimeMillisecond = 1000;
        Minimizer minimizer = ultiJmol.jmolPanel.getViewer().getMinimizer(true);

        // if not ok jmol returns 0.0 which is very unlikely
        while (minimizer == null || minimizer.getMinimizationEnergy() == null || Math.abs(minimizer.getMinimizationEnergy()) < 0.01 ) {
            try {
                Thread.sleep(waitTimeMillisecond);
                countIteration += 1;
                //System.out.println(countIteration);
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
            minimizer = ultiJmol.jmolPanel.getViewer().getMinimizer(true);
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
