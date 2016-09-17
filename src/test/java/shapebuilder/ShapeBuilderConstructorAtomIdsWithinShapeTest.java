package shapebuilder;

import convertformat.AdapterBioJavaStructure;
import io.BiojavaReaderFromPathToMmcifFileTest;
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
import shape.ShapeContainerIfc;
import shapeBuilder.ShapeBuilderConstructorAtomIdsWithinShape;
import shapeBuilder.ShapeBuilderConstructorHetAtm;
import shapeBuilder.ShapeBuilderConstructorIfc;
import shapeBuilder.ShapeBuildingException;
import tools.ToolsForTests;

import java.io.IOException;
import java.net.URL;
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

        MyStructureIfc mystructure = ToolsForTests.getMyStructureIfc(algoParameters, "1di9.cif.gz");

        List<QueryAtomDefinedByIds> listAtomDefinedByIds = new ArrayList<>();
        String fourLetterCode = "1di9";
        String chainQuery = "A";
        int residueId = 168;
        String atomName = "OD2";
        float radiusForQueryAtomsDefinedByIds = 8;
        QueryAtomDefinedByIds queryAtomDefinedByIds = new QueryAtomDefinedByIds(fourLetterCode, chainQuery, residueId, atomName, radiusForQueryAtomsDefinedByIds);
        listAtomDefinedByIds.add(queryAtomDefinedByIds);

        List<String> chainToIgnore = new ArrayList<>();
        ShapeBuilderConstructorIfc shapeBuilder = new ShapeBuilderConstructorAtomIdsWithinShape(mystructure, listAtomDefinedByIds, chainToIgnore, algoParameters);
        ShapeContainerIfc shape = null;
        try {
            shape = shapeBuilder.getShapeContainer();
        } catch (
                ShapeBuildingException e) {
            assertTrue(false);
        }
        // don't know if it is good, it is as it is now.
        assertTrue(shape.getShape().getSize() == 2335);
        assertTrue(shape.getMiniShape().size() == 43);
    }


    @Test
    public void testShapeBuilderConstructorDNARNA() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();

        MyStructureIfc mystructure = ToolsForTests.getMyStructureIfc(algoParameters, "394d.cif.gz");

        List<QueryAtomDefinedByIds> listAtomDefinedByIds = new ArrayList<>();
        String fourLetterCode = "394d";
        String chainQuery = "A";
        int residueId = 4;
        String atomName = "N2";
        float radiusForQueryAtomsDefinedByIds = 8;
        QueryAtomDefinedByIds queryAtomDefinedByIds = new QueryAtomDefinedByIds(fourLetterCode, chainQuery, residueId, atomName, radiusForQueryAtomsDefinedByIds);
        listAtomDefinedByIds.add(queryAtomDefinedByIds);

        List<String> chainToIgnore = new ArrayList<>();
        ShapeBuilderConstructorIfc shapeBuilder = new ShapeBuilderConstructorAtomIdsWithinShape(mystructure, listAtomDefinedByIds, chainToIgnore, algoParameters);
        ShapeContainerIfc shape = null;
        try {
            shape = shapeBuilder.getShapeContainer();
        } catch (
                ShapeBuildingException e) {
            assertTrue(false);
        }
        // don't know if it is good, it is as it is now.
        assertTrue(shape.getShape().getSize() == 1424);
        assertTrue(shape.getMiniShape().size() == 12);
    }
}
