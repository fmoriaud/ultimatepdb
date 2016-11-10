package multithread;

import database.DoMyDbTaskIfc;

import java.sql.Connection;
import java.util.concurrent.Callable;

/**
 * Created by Fabrice on 06/11/16.
 */
public class StoreInSequenceDbPDBFileCallable implements Callable<Boolean> {
    //-------------------------------------------------------------
    // Class members
    //-------------------------------------------------------------
    private DoMyDbTaskIfc doMyDbTaskIfc;
    private Connection connexion;
    private boolean override;

    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public StoreInSequenceDbPDBFileCallable(DoMyDbTaskIfc doMyDbTaskIfc, Connection connexion) {

        this.doMyDbTaskIfc = doMyDbTaskIfc;
        this.connexion = connexion;
    }


    //-------------------------------------------------------------
    // Public & Override methods
    //-------------------------------------------------------------

    @Override
    public Boolean call() throws Exception {

        boolean success = doMyDbTaskIfc.doAndReturnSuccessValue(connexion);
        return success;
    }
}
