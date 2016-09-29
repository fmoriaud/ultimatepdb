package database;

import mystructure.ExceptionInMyStructurePackage;
import mystructure.ReadingStructurefileException;
import org.junit.Test;
import protocols.ParsingConfigFileException;

import java.io.IOException;

/**
 * Created by Fabrice on 29/09/16.
 */
public class UpdateSequenceDatabaseTest {

    @Test
    public void testBuildDatabaseFromTestFolder() {

        UpdateSequenceDatabase updateSequenceDatabase = new UpdateSequenceDatabase();
        updateSequenceDatabase.buildDatabase();

    }

}

