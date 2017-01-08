package ultiJmol1462;

import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import jmolgui.UltiJmol1462;

import java.util.Map;

/**
 * Created by Fabrice on 05/11/16.
 */
public interface DoMyJmolTaskIfc {


    boolean doAndReturnConvergenceStatus(UltiJmol1462 ultiJmol) throws ExceptionInScoringUsingBioJavaJMolGUI;
    Map<Results, Object> getResults();
    String getName();
}
