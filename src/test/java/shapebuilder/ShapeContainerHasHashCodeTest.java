package shapebuilder;

import database.HashTablesTools;
import io.IOTools;
import io.Tools;
import mystructure.MyStructureIfc;
import org.apache.commons.math3.util.Pair;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;
import protocols.ShapeContainerDefined;
import protocols.ShapecontainerDefinedBySegmentOfChain;
import shape.ShapeContainerIfc;
import shapeBuilder.ShapeBuildingException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertTrue;

public class ShapeContainerHasHashCodeTest {


    @Test
    public void testSerializeShape() throws IOException, ParsingConfigFileException {
        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        int initialCount = algoParameters.ultiJMolBuffer.getSize();

        char[] fourLetterCode = "2ce8".toCharArray();
        char[] chainId = "X".toCharArray();
        int startingRankId = 2;
        int peptideLength = 4;
        ShapeContainerDefined shapecontainerDefined = new ShapecontainerDefinedBySegmentOfChain(fourLetterCode, chainId, startingRankId, peptideLength, algoParameters);
        ShapeContainerIfc shape = null;
        try {
            shape = shapecontainerDefined.getShapecontainer();
        } catch (
                ShapeBuildingException e) {
            assertTrue(false);
        }

        String hashFromShape = shape.getPdbFileHash();
        String hashFromStructureToBuildShape = shape.getMyStructureUsedToComputeShape().getPdbFileHash();

        Pair<String, MyStructureIfc> pathAndMyStructure = IOTools.getMyStructureIfc(algoParameters, fourLetterCode);
        String path = pathAndMyStructure.getKey();

        String pdbFileHashFromPath = null;
        try {
            pdbFileHashFromPath = HashTablesTools.getMD5hash(path);
        } catch (NoSuchAlgorithmException e) {
           assertTrue(false);
        }

        assertTrue(hashFromShape.equals(hashFromStructureToBuildShape));
        assertTrue(hashFromShape.equals(pdbFileHashFromPath));


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
