package convertformat;

import org.biojava.nbio.structure.Structure;
import org.junit.Test;
import parameters.AlgoParameters;
import structure.ExceptionInMyStructurePackage;
import structure.MyStructureIfc;
import structure.ReadingStructurefileException;
import tools.TestTools;

import convertformat.AdapterBioJavaStructure;
/**
 * Created by Fabrice.Moriaud on 02.09.2016.
 */
public class AdapterBiojavaStructureTest {

    @Test
    public void test() {

        Structure cifStructure = TestTools.readMmcifFileFromResources("1di9.cif.gz");
        AlgoParameters algoParameters = TestTools.getAlgoParameters();
        AdapterBioJavaStructure adapter = new AdapterBioJavaStructure(algoParameters);

        try {
            MyStructureIfc myStructure = adapter.convertStructureToMyStructure(cifStructure, algoParameters);


        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException e) {
        }
    }
}
