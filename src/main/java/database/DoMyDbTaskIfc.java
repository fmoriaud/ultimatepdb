package database;

import java.sql.Connection;

/**
 * Created by Fabrice on 06/11/16.
 */
public interface DoMyDbTaskIfc {

    boolean doAndReturnSuccessValue(Connection connexion);
}
