package database;

import convertformat.AdapterBioJavaStructure;
import convertformat.ExceptionInConvertFormat;
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

import static junit.framework.TestCase.assertFalse;
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
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
            assertTrue(false);
        }


        String sequence = SequenceTools.generateSequence(mystructure.getAllNucleosidechains()[0]);
        assertTrue(sequence.equals(" DC DC DA DG DA DCUMP DG DA DAMG1 DAUMP5CMUMP DG DG"));
    }


    @Test
    public void testGenerateSequenceFromMyStructureProteinWithHetatmInserted() throws ParsingConfigFileException, IOException {


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
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
            assertTrue(false);
        }

        String sequence = SequenceTools.generateSequence(mystructure.getAminoChain(0));

        assertTrue(sequence.equals("ALASERSERSERPHELYSALAALAASPLEUGLNLEUGLUMETTHRTHRPHETHRASPHISMETLEUMETVALGLUTRPASNASPLYSGLYTRPGLYGLNPROARGILEGLNPROPHEGLNASNLEUTHRLEUHISPROALASERSERSERLEUHISTYRSERLEUGLNLEUPHEGLUGLYMETLYSALAPHELYSGLYLYSASPGLNGLNVALARGLEUPHEARGPROTRPLEUASNMETASPARGMETLEUARGSERALAMETARGLEUOCSLEUPROSERPHEASPLYSLEUGLULEULEUGLUCYSILEARGARGLEUILEGLUVALASPLYSASPTRPVALPROASPALAALAGLYTHRSERLEUTYOVALARGPROVALLEUILEGLYASNGLUPROSERLEUGLYVALSERGLNPROARGARGALALEULEUPHEVALILELEUCYSPROVALGLYALATYRPHEPROGLYGLYSERVALTHRPROVALSERLEULEUALAASPPROALAPHEILEARGALATRPVALGLYGLYVALGLYASNTYRLYSLEUGLYGLYASNTYRGLYPROTHRVALLEUVALGLNGLNGLUALALEULYSARGGLYCYSGLUGLNVALLEUTRPLEUTYRGLYPROASPHISGLNLEUTHRGLUVALGLYTHRMETASNILEPHEVALTYRTRPTHRHISGLUASPGLYVALLEUGLULEUVALTHRPROPROLEUASNGLYVALILELEUPROGLYVALVALARGGLNSERLEULEUASPMETALAGLNTHRTRPGLYGLUPHEARGVALVALGLUARGTHRILETHRMETLYSGLNLEULEUARGALALEUGLUGLUGLYARGVALARGGLUVALPHEGLYSERGLYTHRALACYSGLNVALCYSPROVALHISARGILELEUTYRLYSASPARGASNLEUHISILEPROTHRMETGLUASNGLYPROGLULEUILELEUARGPHEGLNLYSGLULEULYSGLUILEGLNTYRGLYILEARGALAHISGLUTRPMETPHEPROVALPLP"));
    }


    @Test
    public void testGenerateSequenceFromMyStructureWithProblemInStoringInSequenceDB() throws ParsingConfigFileException, IOException {


        String fourLetterCode = "5a07";
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
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
            assertTrue(false);
        }

        // TODO GDP to be inserted in amino chain as only one and I guess covalently bound
        // Then I will have only two aminochains and two hetatm chains
        // And then no problem in inserting

        MyChainIfc[] myChains = mystructure.getAllChainsRelevantForShapeBuilding();
        assertTrue(myChains.length == 2);
        assertFalse(Arrays.equals(myChains[0].getChainId(), myChains[1].getChainId()));

        String sequenceA = SequenceTools.generateSequence(myChains[0]);
        String sequenceB = SequenceTools.generateSequence(myChains[1]);

        assertTrue(sequenceA.equals("ASPHISASPALAGLUVALLEUASPSERILEMETASPARGLEUHISGLUPROLEUTYRGLULYSASPTHRPHEASPPROASNGLUVALLEUALAGLUASNLYSGLNLEUTYRGLUGLUPHELEULEUGLNGLUILESERGLUPROLYSVALASPASNLEUVALARGSERGLYASPPROLEUALAGLYLYSALALYSGLYTHRILELEUSERLEUVALARGASNSERASPLEUGLUASPILEILESERSERILEGLNGLNLEUGLUGLUGLUTYRASNLYSASNPHEGLYTYRPROTYRTHRPHELEUASNASPGLUGLUPHETHRASPGLUPHELYSASPGLYILELYSSERILELEUPROLYSASPARGVALVALGLUPHEGLYTHRILEGLYPROASPASNTRPASNMETPROASPSERILEASPARGGLUARGTYRASPGLNGLUMETASPLYSMETSERLYSGLUASNILEGLNTYRALAGLUVALGLUSERTYRHISASNMETCYSARGPHETYRSERLYSGLUPHETYRHISHISPROLEULEUSERLYSTYRLYSTYRVALTRPARGLEUGLUPROASNVALASNPHETYRCYSLYSILEASNTYRASPVALPHEGLNPHEMETASNLYSASNASPLYSILETYRGLYPHEVALLEUASNLEUTYRASPSERPROGLNTHRILEGLUTHRLEUTRPTHRSERTHRMETASPPHEVALGLUGLUHISPROASNTYRLEUASNVALASNGLYALAPHEALATRPLEULYSASPASNSERGLNASNPROLYSASNTYRASPTYRTHRGLNGLYTYRSERTHRCYSHISPHETRPTHRASNPHEGLUILEVALASPLEUASPPHELEUARGSERGLUPROTYRGLULYSTYRMETGLNTYRLEUGLUGLULYSGLYGLYPHETYRTYRGLUARGTRPGLYASPALAPROVALARGSERLEUALALEUALALEUPHEALAASPLYSSERSERILEHISTRPPHEARGASPILEGLYTYRHISHISTHRPROTYRTHRASNCYSPROTHRCYSPROALAASPSERASPARGCYSASNGLYASNCYSVALPROGLYLYSPHETHRPROTRPSERASPLEUASPASNGLNASNCYSGLNALATHRTRPILEARGHISSERMETSERGLUGLUGLULEUGLUMETTYRGDP"));
        assertTrue(sequenceB.equals("HISASPALAGLUVALLEUASPSERILEMETASPARGLEUHISGLUPROLEUTYRGLULYSASPTHRPHEASPPROASNGLUVALLEUALAGLUASNLYSGLNLEUTYRGLUGLUPHELEULEUGLNGLUILESERGLUPROLYSVALASPASNLEUVALARGSERGLYASPPROLEUALAGLYLYSALALYSGLYTHRILELEUSERLEUVALARGASNSERASPLEUGLUASPILEILESERSERILEGLNGLNLEUGLUGLUGLUTYRASNLYSASNPHEGLYTYRPROTYRTHRPHELEUASNASPGLUGLUPHETHRASPGLUPHELYSASPGLYILELYSSERILELEUPROLYSASPARGVALVALGLUPHEGLYTHRILEGLYPROASPASNTRPASNMETPROASPSERILEASPARGGLUARGTYRASPGLNGLUMETASPLYSMETSERLYSGLUASNILEGLNTYRALAGLUVALGLUSERTYRHISASNMETCYSARGPHETYRSERLYSGLUPHETYRHISHISPROLEULEUSERLYSTYRLYSTYRVALTRPARGLEUGLUPROASNVALASNPHETYRCYSLYSILEASNTYRASPVALPHEGLNPHEMETASNLYSASNASPLYSILETYRGLYPHEVALLEUASNLEUTYRASPSERPROGLNTHRILEGLUTHRLEUTRPTHRSERTHRMETASPPHEVALGLUGLUHISPROASNTYRLEUASNVALASNGLYALAPHEALATRPLEULYSASPASNSERGLNASNPROLYSASNTYRASPTYRTHRGLNGLYTYRSERTHRCYSHISPHETRPTHRASNPHEGLUILEVALASPLEUASPPHELEUARGSERGLUPROTYRGLULYSTYRMETGLNTYRLEUGLUGLULYSGLYGLYPHETYRTYRGLUARGTRPGLYASPALAPROVALARGSERLEUALALEUALALEUPHEALAASPLYSSERSERILEHISTRPPHEARGASPILEGLYTYRHISHISTHRPROTYRTHRASNCYSPROTHRCYSPROALAASPSERASPARGCYSASNGLYASNCYSVALPROGLYLYSPHETHRPROTRPSERASPLEUASPASNGLNASNCYSGLNALATHRTRPILEARGHISSERMETSERGLUGLUGLULEUGLUMETTYRGDP"));

    }


    @Test
    public void testGenerateSequenceFromMyStructureWithProblemInStoringInSequenceDBBecauseChainIdHasTwoLetters() throws ParsingConfigFileException, IOException {


        String fourLetterCode = "5a9z";
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
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
            assertTrue(false);
        }


        // This file is now in resources so will be used in Database building test where it should be handled
        MyChainIfc[] myChains = mystructure.getAllChainsRelevantForShapeBuilding();
        assertTrue(myChains.length == 54);
        assertFalse(Arrays.equals(myChains[0].getChainId(), myChains[1].getChainId()));

        assertTrue(Arrays.equals(myChains[0].getChainId(), "AC".toCharArray()));
        String sequence = SequenceTools.generateSequence(myChains[0]);

        assertTrue(sequence.equals("PROLYSHISGLYLYSARGTYRARGALALEULEUGLULYSVALASPPROASNLYSILETYRTHRILEASPGLUALAALAHISLEUVALLYSGLULEUALATHRALALYSPHEASPGLUTHRVALGLUVALHISALALYSLEUGLYILEASPPROARGARGSERASPGLNASNVALARGGLYTHRVALSERLEUPROHISGLYLEUGLYLYSGLNVALARGVALLEUALAILEALALYSGLYGLULYSILELYSGLUALAGLUGLUALAGLYALAASPTYRVALGLYGLYGLUGLUILEILEGLNLYSILELEUASPGLYTRPMETASPPHEASPALAVALVALALATHRPROASPVALMETGLYALAVALGLYSERLYSLEUGLYARGILELEUGLYPROARGGLYLEULEUPROASNPROLYSALAGLYTHRVALGLYPHEASNILEGLYGLUILEILEARGGLUILELYSALAGLYARGILEGLUPHEARGASNASPLYSTHRGLYALAILEHISALAPROVALGLYLYSALASERPHEPROPROGLULYSLEUALAASPASNILEARGALAPHEILEARGALALEUGLUALAHISLYSPROGLUGLYALALYSGLYTHRPHELEUARGSERVALTYRVALTHRTHRTHRMETGLYPROSERVALARGILEASNPROHISSER"));
    }
}
