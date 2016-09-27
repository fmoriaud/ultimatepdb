package myjmol;

import convertformat.AdapterBioJavaStructure;
import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import io.BiojavaReader;
import io.Tools;
import mystructure.*;
import org.biojava.nbio.structure.Structure;
import org.junit.Ignore;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;
import shapeBuilder.ShapeBuildingException;
import ultiJmol1462.MyJmolTools;
import ultiJmol1462.ResultsUltiJMolMinimizedHitLigandOnTarget;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 18/09/16.
 */
public class MyJmolToolsMinimizeTest {


    // Not reproducible .... the interaction energy there are two possible values
    @Ignore
    @Test
    public void testScoreByMinimizingLigandOnFixedReceptor() throws IOException, ParsingConfigFileException, ExceptionInMyStructurePackage {

        // Get a structure with a ligand
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
        MyStructureIfc myStructureMadeWithLigand = cloner.getClone();

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
        // notcmodifying ligand coordinates

        // minimze ligand in original structure
        ResultsUltiJMolMinimizedHitLigandOnTarget results = null;
        try {
            results = MyJmolTools.scoreByMinimizingLigandOnFixedReceptor(algoParameters, protonatedLigand, protonatedTarget);
        } catch (ExceptionInScoringUsingBioJavaJMolGUI exceptionInScoringUsingBioJavaJMolGUI) {
            exceptionInScoringUsingBioJavaJMolGUI.printStackTrace();
        }
        assertTrue(results != null);
        assertTrue(results.getCountOfLongDistanceChange() == 0);
        assertTrue(results.getInteractionEFinal() < 0);
        assertTrue(Math.abs(results.getInteractionEFinal()) > 20 && Math.abs(results.getInteractionEFinal()) < 30);
        assertTrue(results.getLigandStrainedEnergy() > 0);
        assertTrue(Math.abs(results.getLigandStrainedEnergy()) > 20 && Math.abs(results.getLigandStrainedEnergy()) < 40);
        assertTrue(results.getRmsdLigand() > 0.1 && results.getRmsdLigand() < 0.3);

        // interactionEnergy = 222.08408
        // ligandFullyRelaxedEnergy = 13.304225

        //interactionEnergy = -26.642128
        //ligandFullyRelaxedEnergy = 13.304225

        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 1);
        try {
            algoParameters.ultiJMolBuffer.get().frame.dispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 0);
    }
}
