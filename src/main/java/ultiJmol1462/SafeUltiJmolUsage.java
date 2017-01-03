package ultiJmol1462;

import jmolgui.UltiJmol1462;
import parameters.AlgoParameters;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Fabrice on 05/11/16.
 */
public class SafeUltiJmolUsage {
    // -------------------------------------------------------------------
    // Class variables
    // -------------------------------------------------------------------

    private DoMyJmolTaskIfc doMyJmolTaskIfc;
    private Map<Results, Object> results = new LinkedHashMap<>();
    private AlgoParameters algoParameters;

    private boolean convergenceReached = true;


    public SafeUltiJmolUsage(AlgoParameters algoParameters) {

        this.algoParameters = algoParameters;

    }


    public void run() {

        UltiJmol1462 ultiJmol = null;
        try {
            ultiJmol = algoParameters.ultiJMolBuffer.get();


            Boolean convergenceStatus = doMyJmolTaskIfc.doAndReturnConvergenceStatus(ultiJmol);
            results.putAll(doMyJmolTaskIfc.getResults());
            results.put(Results.CONVERGENCE_REACHED, convergenceStatus);

            //results.put("status", "success");
        } catch (Exception e) {

            results.put(Results.STATUS, "ultiJmol crash");
            // If exception then ultijmol is disposed and a new one is put in the buffer
            System.out.println("Exception in  SafeUltiJmolUsage " + doMyJmolTaskIfc.getName());
            ultiJmol.frame.dispose(); // it is destroyed so not returned to factory
            try {
                algoParameters.ultiJMolBuffer.put(new UltiJmol1462());
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            // write in result that is was an error
            return;

        }

        try {
            ultiJmol.jmolPanel.evalString("zap");
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {

            }
            algoParameters.ultiJMolBuffer.put(ultiJmol);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public void setDoMyJmolTaskIfc(DoMyJmolTaskIfc doMyJmolTaskIfc) {
        this.doMyJmolTaskIfc = doMyJmolTaskIfc;
    }

    public Map<Results, Object> getResults() {
        return results;
    }

}
