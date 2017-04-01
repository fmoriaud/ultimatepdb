/*
Author:
      Fabrice Moriaud <fmoriaud@ultimatepdb.org>

  Copyright (c) 2016 Fabrice Moriaud

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
  */
package database;

import convertformat.AdapterBioJavaStructure;
import convertformat.ExceptionInConvertFormat;
import io.BiojavaReader;
import io.ExceptionInIOPackage;
import io.Tools;
import mystructure.ExceptionInMyStructurePackage;
import mystructure.MyChainIfc;
import mystructure.MyStructureIfc;
import mystructure.ReadingStructurefileException;
import org.biojava.nbio.structure.Structure;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;
import protocols.ShapeContainerDefined;
import protocols.ShapecontainerDefinedByWholeChain;
import shape.ShapeContainerIfc;
import shape.ShapeContainerWithPeptide;
import shapeBuilder.ShapeBuildingException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

public class SequenceToolsTest {

    @Test
    public void testGenerateSequenceFromMyStructureWithThreeUMPcovalentToNucleosides() throws ParsingConfigFileException, IOException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        String fourLetterCode = "229d";
        BiojavaReader reader = new BiojavaReader(algoParameters);
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder).getValue();
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
            assertTrue(false);
        }


        String sequence = SequenceTools.generateSequence(mystructure.getAllNucleosidechains()[0]);
        assertTrue(sequence.equals(" DC DC DA DG DA DCUMP DG DA DAMG1 DAUMP5CMUMP DG DG"));
    }


    @Test
    public void testGenerateSequenceFromMyStructureProteinWithHetatmInserted() throws ParsingConfigFileException, IOException {


        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        String fourLetterCode = "2hhf";
        BiojavaReader reader = new BiojavaReader(algoParameters);
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder).getValue();
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
            assertTrue(false);
        }

        String sequence = SequenceTools.generateSequence(mystructure.getAminoChain(0));

        assertTrue(sequence.equals("ALASERSERSERPHELYSALAALAASPLEUGLNLEUGLUMETTHRTHRPHETHRASPHISMETLEUMETVALGLUTRPASNASPLYSGLYTRPGLYGLNPROARGILEGLNPROPHEGLNASNLEUTHRLEUHISPROALASERSERSERLEUHISTYRSERLEUGLNLEUPHEGLUGLYMETLYSALAPHELYSGLYLYSASPGLNGLNVALARGLEUPHEARGPROTRPLEUASNMETASPARGMETLEUARGSERALAMETARGLEUOCSLEUPROSERPHEASPLYSLEUGLULEULEUGLUCYSILEARGARGLEUILEGLUVALASPLYSASPTRPVALPROASPALAALAGLYTHRSERLEUTYOVALARGPROVALLEUILEGLYASNGLUPROSERLEUGLYVALSERGLNPROARGARGALALEULEUPHEVALILELEUCYSPROVALGLYALATYRPHEPROGLYGLYSERVALTHRPROVALSERLEULEUALAASPPROALAPHEILEARGALATRPVALGLYGLYVALGLYASNTYRLYSLEUGLYGLYASNTYRGLYPROTHRVALLEUVALGLNGLNGLUALALEULYSARGGLYCYSGLUGLNVALLEUTRPLEUTYRGLYPROASPHISGLNLEUTHRGLUVALGLYTHRMETASNILEPHEVALTYRTRPTHRHISGLUASPGLYVALLEUGLULEUVALTHRPROPROLEUASNGLYVALILELEUPROGLYVALVALARGGLNSERLEULEUASPMETALAGLNTHRTRPGLYGLUPHEARGVALVALGLUARGTHRILETHRMETLYSGLNLEULEUARGALALEUGLUGLUGLYARGVALARGGLUVALPHEGLYSERGLYTHRALACYSGLNVALCYSPROVALHISARGILELEUTYRLYSASPARGASNLEUHISILEPROTHRMETGLUASNGLYPROGLULEUILELEUARGPHEGLNLYSGLULEULYSGLUILEGLNTYRGLYILEARGALAHISGLUTRPMETPHEPROVALPLP"));
    }


    @Test
    public void testGenerateSequenceFromMyStructureWithProblemInStoringInSequenceDB() throws ParsingConfigFileException, IOException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        String fourLetterCode = "5a07";
        BiojavaReader reader = new BiojavaReader(algoParameters);
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder).getValue();
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure);
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

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        String fourLetterCode = "5a9z";
        BiojavaReader reader = new BiojavaReader(algoParameters);
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder).getValue();
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure);
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


    @Test
    public void testFindContacts() throws IOException, ParsingConfigFileException, ShapeBuildingException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        int initialCount = algoParameters.ultiJMolBuffer.getSize();
        char[] fourLetterCode = "1be9".toCharArray();
        char[] chainId = "B".toCharArray();
        ShapeContainerDefined shapeContainerDefined = new ShapecontainerDefinedByWholeChain(fourLetterCode, chainId, algoParameters);
        ShapeContainerIfc shapeContainer = shapeContainerDefined.getShapecontainer();

        ShapeContainerWithPeptide query = (ShapeContainerWithPeptide) shapeContainer;
        MyChainIfc ligand = query.getPeptide();

        List<QueryMonomerToTargetContactType> contacts = SequenceTools.findContacts(ligand, algoParameters);

        List<QueryMonomerToTargetContactType> expectedValues = new ArrayList<>();
        expectedValues.add(QueryMonomerToTargetContactType.NONE);
        expectedValues.add(QueryMonomerToTargetContactType.BACKBONE_ONLY);
        expectedValues.add(QueryMonomerToTargetContactType.SIDECHAIN);
        expectedValues.add(QueryMonomerToTargetContactType.SIDECHAIN);
        expectedValues.add(QueryMonomerToTargetContactType.SIDECHAIN);

        int count = 0;
        for (QueryMonomerToTargetContactType type : contacts) {
            assertTrue(type.equals(expectedValues.get(count)));
            count += 1;
        }

        int finalCount = algoParameters.ultiJMolBuffer.getSize();
        assertTrue(finalCount == initialCount);
        try {
            for (int i = 0; i < initialCount; i++) {
                algoParameters.ultiJMolBuffer.get().frame.dispose();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 0);
    }
}