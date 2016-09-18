package myjmol;

import convertformat.AdapterBioJavaStructure;
import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import io.BiojavaReaderFromPathToMmcifFileTest;
import io.Tools;
import mystructure.*;
import org.biojava.nbio.structure.Structure;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;
import shapeBuilder.ShapeBuildingException;
import ultiJmol1462.MyJmolTools;
import ultiJmol1462.ResultsUltiJMolMinimizedHitLigandOnTarget;

import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 18/09/16.
 */
public class MyJmolToolsMinimizeTest {


    @Test
    public void testMinimizeLigand() throws IOException, ParsingConfigFileException, ExceptionInMyStructurePackage {

        // Get a structure with a ligand
        URL url = BiojavaReaderFromPathToMmcifFileTest.class.getClassLoader().getResource("1di9.cif.gz");
        Structure mmcifStructure = mmcifStructure = Tools.getStructure(url);

        URL urlUltimate = BiojavaReaderFromPathToMmcifFileTest.class.getClassLoader().getResource("ultimate.xml");
        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure, EnumMyReaderBiojava.BioJava_MMCIFF);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException e) {
            assertTrue(false);
        }


        MyMonomerIfc msqLigand = mystructure.getHeteroChain("A".toCharArray()).getMyMonomerFromResidueId(500);
        MyStructureIfc myStructureMadeWithLigand = new MyStructure(msqLigand, algoParameters);
        MyStructureIfc protonatedLigand = null;
        try {
            protonatedLigand = MyJmolTools.protonateStructure(myStructureMadeWithLigand, algoParameters);
            protonatedLigand.setFourLetterCode("1di9".toCharArray());
        } catch (ShapeBuildingException e) {
            assertTrue(false);
        }

        MyChainIfc[] neighbors = msqLigand.getNeighboringAminoMyMonomerByRepresentativeAtomDistance();
        MyStructureIfc target = new MyStructure(neighbors[0], algoParameters);
        MyStructureIfc protonatedTarget = null;
        try {
            protonatedTarget = MyJmolTools.protonateStructure(target, algoParameters);
            protonatedTarget.setFourLetterCode("1di9".toCharArray());
        } catch (ShapeBuildingException e) {
            assertTrue(false);
        }
        // modify ligand coordinates


        // minimze ligand in original structure
        ResultsUltiJMolMinimizedHitLigandOnTarget results = null;
        try {
            results = MyJmolTools.scoreByMinimizingLigandOnFixedReceptor(algoParameters, protonatedLigand, protonatedTarget);
        } catch (ExceptionInScoringUsingBioJavaJMolGUI exceptionInScoringUsingBioJavaJMolGUI) {
            exceptionInScoringUsingBioJavaJMolGUI.printStackTrace();
        }
        assertTrue(results != null);
    }

}
