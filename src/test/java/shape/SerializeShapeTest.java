package shape;

import io.MMcifFileInfos;
import io.Tools;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;
import protocols.ShapeContainerDefined;
import protocols.ShapecontainerDefinedBySegmentOfChain;
import shapeBuilder.ShapeBuildingException;

import java.io.*;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class SerializeShapeTest {

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
        } catch (ShapeBuildingException e) {
            assertTrue(false);
        }
        System.out.println("From shape " + shape.getMiniShape().size());
        String pathToSerFile = "//Users/Fabrice//Documents//shape.ser";
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(
                    new FileOutputStream(pathToSerFile));
            oos.writeObject(shape);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file = new File(pathToSerFile);

        ShapeContainerWithPeptide shapeDeSer = null;
        try {
            FileInputStream fin = new FileInputStream(file.getAbsolutePath());
            ObjectInputStream ois = new ObjectInputStream(fin);
            shapeDeSer = (ShapeContainerWithPeptide) ois.readObject();
            System.out.println("From ser file " + shapeDeSer.getMiniShape().size());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
