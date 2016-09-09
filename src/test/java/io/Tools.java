package io;

import org.biojava.bio.structure.Structure;
import org.junit.rules.TemporaryFolder;
import parameters.AlgoParameters;
import protocols.CommandLineTools;
import protocols.ParsingConfigFileException;
import mystructure.EnumMyReaderBiojava;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 05/09/16.
 */
public class Tools {


    /**
     * Tested method to get a PDB file from path
     * The chemcomp are automatically downloaded
     * @param url
     * @throws ParsingConfigFileException
     * @throws IOException
     */
    public static Structure getStructure(URL url, String pathToTempPDBFolder) throws ParsingConfigFileException, IOException {
        Path path = null;
        try {
            path = Paths.get(url.toURI());
        } catch (URISyntaxException e1) {
            assertTrue(false);
        }
        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders(pathToTempPDBFolder);
        Structure structure = null;
        BiojavaReaderIfc reader = new BiojavaReaderUsingChemcompFolder(algoParameters);
        structure = reader.read(path);
        return structure;
    }


    public static AlgoParameters generateModifiedAlgoParametersForTestWithTestFolders(String pathToTempPDBFolder) throws ParsingConfigFileException, IOException{

        URL url = BiojavaReaderTest.class.getClassLoader().getResource("ultimate.xml");
        AlgoParameters algoParameters = CommandLineTools.generateModifiedAlgoParameters(url.getPath(), EnumMyReaderBiojava.BioJava_MMCIFF);

        algoParameters.setPATH_TO_REMEDIATED_PDB_MMCIF_FOLDER(pathToTempPDBFolder);
        algoParameters.setPATH_TO_CHEMCOMP_FOLDER(pathToTempPDBFolder);
        return algoParameters;
    }
}
