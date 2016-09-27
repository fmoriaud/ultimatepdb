package mystructure;

import convertformat.AdapterBioJavaStructure;
import io.BiojavaReader;
import io.CdkTools;
import io.Tools;
import io.WriteTextFile;
import org.biojava.nbio.structure.Structure;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.openscience.cdk.interfaces.IAtomContainer;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 20/09/16.
 */
public class ClonerTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testClonerWithMyMonomerAndConvertionToV3000() throws ParsingConfigFileException, IOException, ReadingStructurefileException, ExceptionInMyStructurePackage {

        String fourLetterCode = "1di9";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException e) {
            assertTrue(false);
        }

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 1);

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure, EnumMyReaderBiojava.BioJava_MMCIFF);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException e) {
            assertTrue(false);
        }


        MyMonomerIfc msqLigand = mystructure.getHeteroChain("A".toCharArray()).getMyMonomerFromResidueId(500);
        Cloner cloner = new Cloner(msqLigand, algoParameters);
        MyStructureIfc myStructureFromAMyMonomer = cloner.getClone();
        String myStructureV3000 = myStructureFromAMyMonomer.toV3000();

        // write to a temp text file
        String pathToTempFolder = folder.getRoot().getAbsolutePath();
        String pathTOWriteV3000Molfile = pathToTempFolder + "//v3000test.mol";
        WriteTextFile.writeTextFile(myStructureV3000, pathTOWriteV3000Molfile);

        // read it with cdk and check atom and bond count

        IAtomContainer mol = CdkTools.readV3000molFile(pathTOWriteV3000Molfile);
        int atomCount = MyStructureTools.getAtomCount(myStructureFromAMyMonomer);
        int bondCount = TestTools.getBondCount(myStructureFromAMyMonomer);
        assertTrue(mol.getAtomCount() == atomCount);
        assertTrue(mol.getBondCount() * 2 == bondCount);

        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 1);
        try {
            algoParameters.ultiJMolBuffer.get().frame.dispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 0);

    }

}
