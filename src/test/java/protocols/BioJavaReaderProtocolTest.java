package protocols;

import io.BiojavaReaderFromPathToMmcifFileTest;
import io.Tools;
import org.biojava.nbio.structure.ExperimentalTechnique;
import org.biojava.nbio.structure.Structure;
import org.junit.Ignore;
import org.junit.Test;
import parameters.AlgoParameters;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 13/09/16.
 */
public class BioJavaReaderProtocolTest {

    @Test
    public void readFileNeutronDiffractionThatThrowABiojavaException() throws ParsingConfigFileException, IOException {

        URL url = BiojavaReaderFromPathToMmcifFileTest.class.getClassLoader().getResource("5e5j.cif.gz");
        Structure mmcifStructure = null;
        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();
        try {
            BioJavaReaderProtocol reader = new BioJavaReaderProtocol();
            mmcifStructure = reader.read(Paths.get(url.getPath().toString()), algoParameters.getPATH_TO_CHEMCOMP_FOLDER());
        } catch (IOException e) {
            assertTrue(false);
        }
        assertTrue(mmcifStructure == null);
    }

    @Test
    public void readFileProteinFiberDiffractionThatThrowABiojavaException() throws ParsingConfigFileException, IOException {

        URL url = BiojavaReaderFromPathToMmcifFileTest.class.getClassLoader().getResource("2zwh.cif.gz");
        Structure mmcifStructure = null;
        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();
        try {
            BioJavaReaderProtocol reader = new BioJavaReaderProtocol();
            mmcifStructure = reader.read(Paths.get(url.getPath().toString()), algoParameters.getPATH_TO_CHEMCOMP_FOLDER());
        } catch (IOException e) {
            assertTrue(false);
        }
        assertTrue(mmcifStructure == null);
    }

    @Test
    public void readFileHybridThatThrowABiojavaException() throws ParsingConfigFileException, IOException {

        URL url = BiojavaReaderFromPathToMmcifFileTest.class.getClassLoader().getResource("5ebj.cif.gz");
        Structure mmcifStructure = null;
        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();
        try {
            BioJavaReaderProtocol reader = new BioJavaReaderProtocol();
            mmcifStructure = reader.read(Paths.get(url.getPath().toString()), algoParameters.getPATH_TO_CHEMCOMP_FOLDER());
        } catch (IOException e) {
            assertTrue(false);
        }
        assertTrue(mmcifStructure == null);
    }

}