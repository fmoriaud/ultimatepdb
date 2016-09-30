package database;

import convertformat.AdapterBioJavaStructure;
import io.BiojavaReader;
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
 * Created by Fabrice on 30/09/16.
 */
public class SequenceToolsTest {


    @Test
    public void testGenerateSequenceFromMyStructureWithThreeUMPcovalentToNucleosides() throws ParsingConfigFileException, IOException {

        String fourLetterCode = "229d";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException e) {
            assertTrue(false);
        }

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure, EnumMyReaderBiojava.BioJava_MMCIFF);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException e) {
            assertTrue(false);
        }


        String sequence = SequenceTools.generateSequence(mystructure.getAllNucleosidechains()[0]);
        assertTrue(sequence.equals(" DC DC DA DG DA DCUMP DG DA DAMG1 DAUMP5CMUMP DG DG"));
    }


    @Test
    public void testGenerateSequenceFromMyStructureWith() throws ParsingConfigFileException, IOException {


        String fourLetterCode = "2hhf";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException e) {
            assertTrue(false);
        }

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure, EnumMyReaderBiojava.BioJava_MMCIFF);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException e) {
            assertTrue(false);
        }

        String sequence = SequenceTools.generateSequence(mystructure.getAminoChain(0));

        assertTrue(sequence.equals("ALASERSERSERPHELYSALAALAASPLEUGLNLEUGLUMETTHRTHRPHETHRASPHISMETLEUMETVALGLUTRPASNASPLYSGLYTRPGLYGLNPROARGILEGLNPROPHEGLNASNLEUTHRLEUHISPROALASERSERSERLEUHISTYRSERLEUGLNLEUPHEGLUGLYMETLYSALAPHELYSGLYLYSASPGLNGLNVALARGLEUPHEARGPROTRPLEUASNMETASPARGMETLEUARGSERALAMETARGLEUOCSLEUPROSERPHEASPLYSLEUGLULEULEUGLUCYSILEARGARGLEUILEGLUVALASPLYSASPTRPVALPROASPALAALAGLYTHRSERLEUTYOVALARGPROVALLEUILEGLYASNGLUPROSERLEUGLYVALSERGLNPROARGARGALALEULEUPHEVALILELEUCYSPROVALGLYALATYRPHEPROGLYGLYSERVALTHRPROVALSERLEULEUALAASPPROALAPHEILEARGALATRPVALGLYGLYVALGLYASNTYRLYSLEUGLYGLYASNTYRGLYPROTHRVALLEUVALGLNGLNGLUALALEULYSARGGLYCYSGLUGLNVALLEUTRPLEUTYRGLYPROASPHISGLNLEUTHRGLUVALGLYTHRMETASNILEPHEVALTYRTRPTHRHISGLUASPGLYVALLEUGLULEUVALTHRPROPROLEUASNGLYVALILELEUPROGLYVALVALARGGLNSERLEULEUASPMETALAGLNTHRTRPGLYGLUPHEARGVALVALGLUARGTHRILETHRMETLYSGLNLEULEUARGALALEUGLUGLUGLYARGVALARGGLUVALPHEGLYSERGLYTHRALACYSGLNVALCYSPROVALHISARGILELEUTYRLYSASPARGASNLEUHISILEPROTHRMETGLUASNGLYPROGLULEUILELEUARGPHEGLNLYSGLULEULYSGLUILEGLNTYRGLYILEARGALAHISGLUTRPMETPHEPROVALPLP"));
    }
}
