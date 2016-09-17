package pointWithProperties;

import convertformat.AdapterBioJavaStructure;
import io.BiojavaReaderFromPathToMmcifFileTest;
import io.Tools;
import mystructure.*;
import org.biojava.nbio.structure.Structure;
import org.junit.Test;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;
import shapeBuilder.ShapeBuildingException;
import ultiJmol1462.MyJmolTools;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 17/09/16.
 */
public class ComputePropertiesPocketTest {


    @Test
    public void testWithDC_HDonor() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();

        URL url = BiojavaReaderFromPathToMmcifFileTest.class.getClassLoader().getResource("2kh4.cif.gz");
        Structure mmcifStructure = null;
        try {
            mmcifStructure = Tools.getStructure(url);
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

        MyStructureIfc protonatedStructure = null;
        try {
            protonatedStructure = MyJmolTools.protonateStructure(mystructure, algoParameters);
        } catch (ShapeBuildingException e) {
            assertTrue(false);
        }

        List< HBondDefinedWithAtoms > dehydrons = new ArrayList<>();
        List<PointIfc> listOfLigandPoints = new ArrayList<>();
        PointIfc ligandPoint = new Point(14.6f, 23.1f, 7.2f);
        listOfLigandPoints.add(ligandPoint);
        ComputePropertiesPocket computePropertiesPocket = new ComputePropertiesPocket(protonatedStructure, algoParameters, dehydrons, listOfLigandPoints);

        MyChainIfc nucleoChain = protonatedStructure.getAllNucleosidechains()[0];

        // Test for a donnor atom
        MyAtomIfc dt2H1N3 = nucleoChain.getMyMonomerByRank(1).getMyAtomFromMyAtomName("H1N3".toCharArray());
        float[] positionWhereToCompute = shiftABit(dt2H1N3.getCoords());
        boolean atomClosestWithDistanceLessThanFwhm = computePropertiesPocket.compute(positionWhereToCompute);

        assertTrue(atomClosestWithDistanceLessThanFwhm);
        assertEquals(computePropertiesPocket.gethDonnor(), 0.25, 0.0001);
    }



    @Test
    public void testWithDG_HDonor() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();

        URL url = BiojavaReaderFromPathToMmcifFileTest.class.getClassLoader().getResource("394d.cif.gz");
        Structure mmcifStructure = null;
        try {
            mmcifStructure = Tools.getStructure(url);
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

        MyStructureIfc protonatedStructure = null;
        try {
            protonatedStructure = MyJmolTools.protonateStructure(mystructure, algoParameters);
        } catch (ShapeBuildingException e) {
            assertTrue(false);
        }

        List< HBondDefinedWithAtoms > dehydrons = new ArrayList<>();
        List<PointIfc> listOfLigandPoints = new ArrayList<>();
        PointIfc ligandPoint = new Point(1.1f, -2.9f, 22.1f);
        listOfLigandPoints.add(ligandPoint);
        ComputePropertiesPocket computePropertiesPocket = new ComputePropertiesPocket(protonatedStructure, algoParameters, dehydrons, listOfLigandPoints);

        MyChainIfc nucleoChain = protonatedStructure.getAllNucleosidechains()[0];

        // Test for a donnor atom
        MyAtomIfc dg4H1N2 = nucleoChain.getMyMonomerByRank(3).getMyAtomFromMyAtomName("H1N2".toCharArray());
        float[] positionWhereToCompute = shiftABit(dg4H1N2.getCoords());
        boolean atomClosestWithDistanceLessThanFwhm = computePropertiesPocket.compute(positionWhereToCompute);

        assertTrue(atomClosestWithDistanceLessThanFwhm);
        assertEquals(computePropertiesPocket.gethDonnor(), 0.25, 0.0001);
    }


    private float[] shiftABit(float[] coords) {

        float[] shiftedCoords = new float[3];
        shiftedCoords[0] = coords[0] + 0.01f;
        shiftedCoords[1] = coords[1] + 0.01f;
        shiftedCoords[2] = coords[2] + 0.01f;

        return shiftedCoords;
    }

}