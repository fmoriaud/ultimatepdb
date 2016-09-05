package io;

import org.biojava.bio.structure.Structure;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by Fabrice on 05/09/16.
 */
public interface BiojavaReaderIfc {

    Structure read(Path path) throws IOException;
}
