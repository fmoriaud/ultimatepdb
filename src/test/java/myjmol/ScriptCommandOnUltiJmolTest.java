package myjmol;

import convertformat.AdapterBioJavaStructure;
import io.BiojavaReader;
import io.Tools;
import mystructure.*;
import org.biojava.nbio.structure.Structure;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;
import shapeBuilder.ShapeBuildingException;
import ultiJmol1462.MyJmolTools;
import ultiJmol1462.ScriptCommandOnUltiJmol;

import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 22/09/16.
 */
public class ScriptCommandOnUltiJmolTest {


    @Test
    public void computeEnergyAfterHydrogenMinimization() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();

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

        String script = MyJmolTools.getScriptMinimization();
        ScriptCommandOnUltiJmol scriptCommandOnUltiJmol = new ScriptCommandOnUltiJmol(script, moleculeV3000, algoParameters);
        scriptCommandOnUltiJmol.execute();
        Map<String, Object> results = scriptCommandOnUltiJmol.getResults();


        boolean convergenceReached = (boolean) results.get("convergence reached");
        Float finalEnergy = (Float) results.get("final energy");
        assertTrue(convergenceReached);
        // It is not reproducible the minimization final energy
        assertEquals(finalEnergy, 5000.0f, 500.0f);
    }
}
