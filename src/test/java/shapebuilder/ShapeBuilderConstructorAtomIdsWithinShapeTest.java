package shapebuilder;

import convertformat.AdapterBioJavaStructure;
import io.BiojavaReader;
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
import protocols.ShapeContainerFactory;
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

        String fourLetterCode = "1di9";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException e) {
            assertTrue(false);
        }
        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure, EnumMyReaderBiojava.BioJava_MMCIFF);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException e) {
            assertTrue(false);
        }

        List<QueryAtomDefinedByIds> listAtomDefinedByIds = new ArrayList<>();
        String chainQuery = "A";
        int residueId = 168;
        String atomName = "OD2";
        float radiusForQueryAtomsDefinedByIds = 8;
        QueryAtomDefinedByIds queryAtomDefinedByIds = new QueryAtomDefinedByIds(fourLetterCode, chainQuery, residueId, atomName, radiusForQueryAtomsDefinedByIds);
        listAtomDefinedByIds.add(queryAtomDefinedByIds);

        List<String> chainToIgnore = new ArrayList<>();
        ShapeContainerIfc shape = null;
        try {
            shape = ShapeContainerFactory.getShapeAroundAtomDefinedByIds(EnumShapeReductor.CLUSTERING, mystructure, algoParameters, listAtomDefinedByIds, chainToIgnore);
        } catch (ShapeBuildingException e) {
            e.printStackTrace();
        }

        // don't know if it is good, it is as it is now.
        assertTrue(shape.getShape().getSize() == 2335);
        assertTrue(shape.getMiniShape().size() == 43);
    }


    @Test
    public void testShapeBuilderConstructorDNARNA() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();

        String fourLetterCode = "394d";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException e) {
            assertTrue(false);
        }
        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure, EnumMyReaderBiojava.BioJava_MMCIFF);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException e) {
            assertTrue(false);
        }

        List<QueryAtomDefinedByIds> listAtomDefinedByIds = new ArrayList<>();
        String chainQuery = "A";
        int residueId = 4;
        String atomName = "N2";
        float radiusForQueryAtomsDefinedByIds = 8;
        QueryAtomDefinedByIds queryAtomDefinedByIds = new QueryAtomDefinedByIds(fourLetterCode, chainQuery, residueId, atomName, radiusForQueryAtomsDefinedByIds);
        listAtomDefinedByIds.add(queryAtomDefinedByIds);

        List<String> chainToIgnore = new ArrayList<>();
        ShapeContainerIfc shape = null;
        try {
            shape = ShapeContainerFactory.getShapeAroundAtomDefinedByIds(EnumShapeReductor.CLUSTERING, mystructure, algoParameters, listAtomDefinedByIds, chainToIgnore);
        } catch (ShapeBuildingException e) {
            e.printStackTrace();
        }

        // don't know if it is good, it is as it is now.
        assertTrue(shape.getShape().getSize() == 1424);
        assertTrue(shape.getMiniShape().size() == 12);
    }
}
