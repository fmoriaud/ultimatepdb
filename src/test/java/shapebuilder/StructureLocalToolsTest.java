package shapebuilder;

import convertformat.AdapterBioJavaStructure;
import convertformat.ExceptionInConvertFormat;
import io.BiojavaReader;
import io.ExceptionInIOPackage;
import io.Tools;
import mystructure.*;
import org.biojava.nbio.structure.Structure;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;
import shapeBuilder.StructureLocalTools;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 11/10/16.
 */
public class StructureLocalToolsTest {

    @Test
    public void testMethodMakeChainSegmentOutOfAChainUsingBondingInformation() throws IOException, ParsingConfigFileException {

        char[] chainId = "C".toCharArray();
        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 1);

        String fourLetterCode = "2yjd";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure, EnumMyReaderBiojava.BioJava_MMCIFF);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
            assertTrue(false);
        }

        // Chain C is a peptide bound and has 13 monomers
        MyChainIfc inputChain = mystructure.getAminoMyChain("C".toCharArray());
        assertTrue(inputChain.getMyMonomers().length == 13);

        int rankIdinChain = 0;
        int peptideLength = 3;
        MyChainIfc segmentOfChain = StructureLocalTools.makeChainSegmentOutOfAChainUsingBondingInformation(inputChain, rankIdinChain, peptideLength, algoParameters);
        assertTrue(segmentOfChain.getMyMonomers().length == 3);
        // In this chain the monomers id goes from 0 to 12 so easy to test
        for (int i = 0; i < segmentOfChain.getMyMonomers().length; i++) {
            assertTrue(segmentOfChain.getMyMonomers()[i].getResidueID() == i);
        }

    }


    @Test
    public void testMethodfindTipsSegmentOfChain() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 1);

        String fourLetterCode = "2yjd";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure, EnumMyReaderBiojava.BioJava_MMCIFF);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
            assertTrue(false);
        }

        // Chain C is a peptide bound and has 13 monomers
        char[] chainId = "C".toCharArray();
        MyChainIfc wholeChain = mystructure.getAminoMyChain(chainId);
        MyChainIfc ligand = null;
        int startingRankId = 0;
        int peptideLength = 4;
        int tipMonoMerDistance = 2;
       // List<MyMonomerIfc> tipMonomers = StructureLocalTools.findTipsSegmentOfChain(wholeChain, ligand, startingRankId, peptideLength, tipMonoMerDistance);
        System.out.println();

    }

}
