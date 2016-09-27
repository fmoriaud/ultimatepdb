package myjmol;

import convertformat.AdapterBioJavaStructure;
import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import io.BiojavaReader;
import io.Tools;
import math.ToolsMath;
import mystructure.*;
import org.biojava.nbio.structure.Structure;
import org.junit.Ignore;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;
import shapeBuilder.ShapeBuildingException;
import ultiJmol1462.GetEnergy;
import ultiJmol1462.MyJmol1462;
import ultiJmol1462.MyJmolTools;
import ultiJmol1462.ScriptCommandOnUltiJmol;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 22/09/16.
 */
public class ScriptCommandOnUltiJmolTest {


    @Test
    public void computeHydrogensEnergyAfterHydrogenMinimization() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 1);

        // Prepare input as environment of MSQ in 1di9
        String fourLetterCode = "1di9";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure1di9 = null;
        try {
            mmcifStructure1di9 = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException e) {
            assertTrue(false);
        }
        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure1di9 = null;
        try {
            mystructure1di9 = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure1di9, EnumMyReaderBiojava.BioJava_MMCIFF);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException e) {
            assertTrue(false);
        }
        MyMonomerIfc msqLigand = mystructure1di9.getHeteroChain("A".toCharArray()).getMyMonomerFromResidueId(500);
        MyChainIfc[] neighbors = msqLigand.getNeighboringAminoMyMonomerByRepresentativeAtomDistance();
        MyStructureIfc target = new MyStructure(neighbors[0], algoParameters);
        MyStructureIfc protonatedTarget = null;
        try {
            protonatedTarget = MyJmolTools.protonateStructure(target, algoParameters);
            protonatedTarget.setFourLetterCode("1di9".toCharArray());
        } catch (ShapeBuildingException e) {
            assertTrue(false);
        }
        String moleculeV3000 = protonatedTarget.toV3000();

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String script = MyJmolTools.getScriptMinimizationOnlyHydrogens();
        ScriptCommandOnUltiJmol scriptCommandOnUltiJmol = new ScriptCommandOnUltiJmol(script, moleculeV3000, algoParameters, null);
        try {
            scriptCommandOnUltiJmol.execute();
        } catch (ExceptionInScoringUsingBioJavaJMolGUI exceptionInScoringUsingBioJavaJMolGUI) {
            assertTrue(false); // Should not happen. Only if energy during minimization failed to be obtained
        }
        Map<String, Object> results = scriptCommandOnUltiJmol.getResults();


        boolean convergenceReached = (boolean) results.get("convergence reached");
        Float finalEnergy = (Float) results.get("final energy");

        assertTrue(convergenceReached);
        // not reproducible
        System.out.println("finalEnergy = " + finalEnergy);
        // also possible finalEnergy = 5460.785
        //assertEquals(finalEnergy, 597.0f, 50.0f);

        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 1);
        try {
            algoParameters.ultiJMolBuffer.get().frame.dispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 0);

    }



    private List<Integer> atomIdsTargetCloseToLigand(MyStructureIfc protonatedTarget, MyStructureIfc protonatedLigand, AlgoParameters algoParameters) {

        MyStructureIfc myStructureFile1 = null;
        try {
            myStructureFile1 = new MyStructure(protonatedTarget.toV3000(), algoParameters);
        } catch (ExceptionInMyStructurePackage e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        MyStructureIfc myStructureFile2 = null;
        try {
            myStructureFile2 = new MyStructure(protonatedLigand.toV3000(), algoParameters);
        } catch (ExceptionInMyStructurePackage e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        float thresholdDistance = 5.0f;
        // atomIds in Target
        // So as when I merge, I add first target, that will work for the selection

        // I assume first atom has id = 1
        List<Integer> atomIds = new ArrayList<>();

        int idAtomTarget = 0;
        for (MyAtomIfc atomTarget : myStructureFile1.getAllChains()[0].getMyMonomers()[0].getMyAtoms()) {

            idAtomTarget += 1;
            for (MyAtomIfc atomLigand : myStructureFile2.getAllChains()[0].getMyMonomers()[0].getMyAtoms()) {

                float distance = ToolsMath.computeDistance(atomTarget.getCoords(), atomLigand.getCoords());
                if (distance < thresholdDistance && !atomIds.contains(idAtomTarget)) {
                    atomIds.add(idAtomTarget);
                }
            }
        }


        return atomIds;
    }
}