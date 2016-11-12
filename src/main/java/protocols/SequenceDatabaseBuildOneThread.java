package protocols;

import database.CreateAndSearchSequenceDatabase;
import database.SequenceTools;
import parameters.AlgoParameters;

/**
 * Created by Fabrice on 11/11/16.
 */
public class SequenceDatabaseBuildOneThread {

    // -------------------------------------------------------------------
    // Public && Interface method
    // -------------------------------------------------------------------
    public static void main(String[] args) throws ParsingConfigFileException {

        AlgoParameters algoParameters = ProtocolTools.prepareAlgoParameters();

        CreateAndSearchSequenceDatabase createAndSearchSequenceDatabase = new CreateAndSearchSequenceDatabase(algoParameters, SequenceTools.tableName);
        createAndSearchSequenceDatabase.createDatabase();

        createAndSearchSequenceDatabase.shutdownDb();
    }

}
