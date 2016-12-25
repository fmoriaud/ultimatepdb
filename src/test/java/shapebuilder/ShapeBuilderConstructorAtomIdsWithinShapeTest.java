package shapebuilder;

import convertformat.AdapterBioJavaStructure;
import convertformat.ExceptionInConvertFormat;
import io.BiojavaReader;
import io.ExceptionInIOPackage;
import io.Tools;
import mystructure.EnumMyReaderBiojava;
import mystructure.ExceptionInMyStructurePackage;
import mystructure.MyStructureIfc;
import mystructure.ReadingStructurefileException;
import org.biojava.nbio.structure.Structure;
import org.junit.Test;
import parameters.AlgoParameters;
import parameters.QueryAtomDefinedByIds;
import protocols.ParsingConfigFileException;
import protocols.ShapeContainerDefined;
import protocols.ShapeContainerFactory;
import protocols.ShapecontainerDefinedByAroundAtomDefinedByIds;
import shape.ShapeContainerIfc;
import shapeBuilder.EnumShapeReductor;
import shapeBuilder.ShapeBuilderConstructorIfc;
import shapeBuilder.ShapeBuildingException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 14/09/16.
 */
public class ShapeBuilderConstructorAtomIdsWithinShapeTest {

    @Test
    public void testShapeBuilderConstructorProtein() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        int initialCount = algoParameters.ultiJMolBuffer.getSize();

        List<QueryAtomDefinedByIds> listAtomDefinedByIds = new ArrayList<>();
        String chainQuery = "A";
        int residueId = 168;
        String atomName = "OD2";
        float radiusForQueryAtomsDefinedByIds = 8;
        String fourLetterCode = "1di9";
        QueryAtomDefinedByIds queryAtomDefinedByIds = new QueryAtomDefinedByIds(fourLetterCode, chainQuery, residueId, atomName, radiusForQueryAtomsDefinedByIds);
        listAtomDefinedByIds.add(queryAtomDefinedByIds);

        List<String> chainToIgnore = new ArrayList<>();

        ShapeContainerDefined shapecontainerDefined = new ShapecontainerDefinedByAroundAtomDefinedByIds(fourLetterCode.toCharArray(), algoParameters, listAtomDefinedByIds, chainToIgnore);
        ShapeContainerIfc shape = null;
        try {
            shape = shapecontainerDefined.getShapecontainer();
        } catch (ShapeBuildingException e) {
            e.printStackTrace();
        }

        // don't know if it is good, it is as it is now.
        assertTrue(shape.getShape().getSize() == 1802);
        assertTrue(shape.getMiniShape().size() == 43);

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


    @Test
    public void testShapeBuilderConstructorDNARNA() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        int initialCount = algoParameters.ultiJMolBuffer.getSize();

        List<QueryAtomDefinedByIds> listAtomDefinedByIds = new ArrayList<>();
        String chainQuery = "A";
        int residueId = 4;
        String atomName = "N2";
        float radiusForQueryAtomsDefinedByIds = 8;
        String fourLetterCode = "394d";
        QueryAtomDefinedByIds queryAtomDefinedByIds = new QueryAtomDefinedByIds(fourLetterCode, chainQuery, residueId, atomName, radiusForQueryAtomsDefinedByIds);
        listAtomDefinedByIds.add(queryAtomDefinedByIds);

        List<String> chainToIgnore = new ArrayList<>();

        ShapeContainerDefined shapecontainerDefined = new ShapecontainerDefinedByAroundAtomDefinedByIds(fourLetterCode.toCharArray(), algoParameters, listAtomDefinedByIds, chainToIgnore);
        ShapeContainerIfc shape = null;
        try {
            shape = shapecontainerDefined.getShapecontainer();
        } catch (ShapeBuildingException e) {
            e.printStackTrace();
        }

        // don't know if it is good, it is as it is now.
        assertTrue(shape.getShape().getSize() == 1096);
        assertTrue(shape.getMiniShape().size() == 11);

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
