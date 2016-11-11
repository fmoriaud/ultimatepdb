package database;

/**
 * Created by Fabrice on 11/11/16.
 */
public interface CreateAndSearchSequenceDatabaseIfc {

    void createDatabase();
    void updateDatabaseKeepingFourLetterCodeEntries();
    void updateDatabaseAndOverride();
    String returnSequenceInDbifFourLetterCodeAndChainfoundInDatabase(String fourLetterCode, String chainName);
    void shutdownDb();

}
