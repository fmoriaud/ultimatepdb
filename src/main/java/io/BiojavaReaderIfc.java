package io;


import mystructure.MyStructureIfc;
import org.biojava.nbio.structure.Structure;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by Fabrice on 05/09/16.
 */
public interface BiojavaReaderIfc {

    Structure read(Path pathToFile, String pathToChemcompFolder) throws IOException;
    Structure readFromPDBFolder(String fourLetterCode, String pathToDividedPDBFolder, String pathToChemcompFolder) throws IOException;

    }
