package convertformat;

import database.SequenceTools;
import io.BiojavaReader;
import io.ExceptionInIOPackage;
import io.Tools;
import mystructure.*;
import org.biojava.nbio.structure.Group;
import org.biojava.nbio.structure.GroupType;
import org.biojava.nbio.structure.Structure;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 02/10/16.
 */
public class AdapterBiojavaStructureNucleosideCovalentlyBoundTest {


    @Test
    public void testGenerateSequenceFromMyStructureWithProblemInStoringInSequenceDB() throws ParsingConfigFileException, IOException {


        String fourLetterCode = "5a07";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure, EnumMyReaderBiojava.BioJava_MMCIFF);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
            assertTrue(false);
        }

        Group mmcifGDP = mmcifStructure.getChain(0).getAtomGroup(395);
        assertTrue(mmcifGDP.getPDBName().equals("GDP"));
        GroupType type = mmcifGDP.getType();
        assertTrue(type == GroupType.NUCLEOTIDE);

        assertTrue(mystructure.getAllAminochains().length == 2);
        // Empty nucleosides chains should had been removed
        assertTrue(mystructure.getAllNucleosidechains().length == 0);

        MyMonomerIfc myStructureGDP = mystructure.getAminoChain(0).getMyMonomerByRank(395);
        assertTrue(Arrays.equals(myStructureGDP.getThreeLetterCode(), "GDP".toCharArray()));
        assertTrue(Arrays.equals(myStructureGDP.getType(), MyMonomerType.NUCLEOTIDE.getType()));
        assertTrue(myStructureGDP.isWasHetatm() == false);
    }
}
