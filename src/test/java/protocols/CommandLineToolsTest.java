package protocols;

import io.BiojavaReaderFromPDBFolderTest;
import org.junit.Test;
import parameters.AlgoParameters;
import mystructure.EnumMyReaderBiojava;

import java.net.URL;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 01/09/16.
 */
public class CommandLineToolsTest {


    @Test
    public void generateModifiedAlgoParametersTest(){

        URL url = BiojavaReaderFromPDBFolderTest.class.getClassLoader().getResource("ultimate.xml");
        AlgoParameters algoParameters = null;
        try {
            algoParameters = CommandLineTools.generateModifiedAlgoParameters(url.getPath(), EnumMyReaderBiojava.BioJava_MMCIFF);
        } catch (ParsingConfigFileException e){
        assertTrue(false);
        }

        // TODO write an exhaustive comparison of the resulting algoParameters and the xml file content
        assertTrue(algoParameters.getQUERY_PDB_FOUR_LETTER_CODE().equals("1NLN"));
    }

}
