package io;


import org.biojava.nbio.structure.Structure;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by Fabrice on 05/09/16.
 */
public interface BiojavaReaderIfc {

    Structure read(Path pathToFile, String pathToChemcompFolder) throws IOException;
    Structure readFrom(String fourLetterCode, String pathToDividedPDBFolder, String pathToChemcompFolder) throws IOException;

    }
