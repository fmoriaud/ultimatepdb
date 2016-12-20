package shapeReduction;

import io.Tools;
import org.junit.Test;
import parameters.AlgoParameters;
import pointWithProperties.*;
import protocols.ParsingConfigFileException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 17.12.16.
 */
public class ShapeReductorByClusteringTest {


    @Test
    public void testHydrophobePointsInLineInGrid() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        float criticalDistance = algoParameters.getCELL_DIMENSION_OF_THE_PROBABILITY_MAP_ANGSTROM() * (float) Math.sqrt(3) + 0.1f;
        float cellSize = algoParameters.getCELL_DIMENSION_OF_THE_PROBABILITY_MAP_ANGSTROM();

        List<PointWithPropertiesIfc> listPointsWithProperties = new ArrayList<>();
        // cretae points
        for (int i = 0; i < 10; i++) {
            PointWithPropertiesIfc point = new PointWithProperties();
            float[] coords = new float[3];
            coords[0] = (float) i * cellSize;
            coords[1] = 0;
            coords[2] = 0;
            point.setCoords(new Point(coords));

            List<StrikingProperties> properties = new ArrayList<>();
            properties.add(StrikingProperties.HYDROPHOBE);
            point.setStrikingProperties(properties);

            listPointsWithProperties.add(point);
        }

        CollectionOfPointsWithPropertiesIfc points = new CollectionOfPointsWithProperties(listPointsWithProperties);

        ShapeReductorByClustering shapeReductorByClustering = new ShapeReductorByClustering(points, algoParameters);

        Map<Integer, PointWithPropertiesIfc> minishape = shapeReductorByClustering.computeReducedCollectionOfPointsWithProperties();

        // That works nicely
        // 9 Hydrophobes in a row become 2 Hydrophobes in minishape
        // H H H H H H H H H
        //     H       H
        assertTrue(minishape.size() == 2);
        PointWithPropertiesIfc miniPoint2 = minishape.get(2);
        List<StrikingProperties> propertiesMiniPoint0 = miniPoint2.getStrikingProperties();
        assertTrue(propertiesMiniPoint0.size() == 1);
        assertTrue(propertiesMiniPoint0.get(0) == StrikingProperties.HYDROPHOBE);

        PointWithPropertiesIfc miniPoint1 = minishape.get(7);
        List<StrikingProperties> propertiesMiniPoint1 = miniPoint1.getStrikingProperties();
        assertTrue(propertiesMiniPoint1.size() == 1);
        assertTrue(propertiesMiniPoint1.get(0) == StrikingProperties.HYDROPHOBE);
    }


    @Test
    public void testHydrophobeAndAromaticPointsInLineInGrid() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        float criticalDistance = algoParameters.getCELL_DIMENSION_OF_THE_PROBABILITY_MAP_ANGSTROM() * (float) Math.sqrt(3) + 0.1f;
        float cellSize = algoParameters.getCELL_DIMENSION_OF_THE_PROBABILITY_MAP_ANGSTROM();

        List<PointWithPropertiesIfc> listPointsWithProperties = new ArrayList<>();
        // cretae points
        for (int i = 0; i < 10; i++) {
            PointWithPropertiesIfc point = new PointWithProperties();
            float[] coords = new float[3];
            coords[0] = (float) i * cellSize;
            coords[1] = 0;
            coords[2] = 0;
            point.setCoords(new Point(coords));

            List<StrikingProperties> properties = new ArrayList<>();
            properties.add(StrikingProperties.HYDROPHOBE);
            if (i < 3){
                properties.add(StrikingProperties.AROMATICRING);
            }
            point.setStrikingProperties(properties);

            listPointsWithProperties.add(point);
        }

        CollectionOfPointsWithPropertiesIfc points = new CollectionOfPointsWithProperties(listPointsWithProperties);

        ShapeReductorByClustering shapeReductorByClustering = new ShapeReductorByClustering(points, algoParameters);

        Map<Integer, PointWithPropertiesIfc> minishape = shapeReductorByClustering.computeReducedCollectionOfPointsWithProperties();

        // That works nicely
        // H H H H H H H H H
        // A A A H H H H H H
        //   H           H
        //   A
        assertTrue(minishape.size() == 2);
        PointWithPropertiesIfc miniPoint2 = minishape.get(1);
        List<StrikingProperties> propertiesMiniPoint0 = miniPoint2.getStrikingProperties();
        assertTrue(propertiesMiniPoint0.size() == 2);
        assertTrue(propertiesMiniPoint0.contains(StrikingProperties.HYDROPHOBE));
        assertTrue(propertiesMiniPoint0.contains(StrikingProperties.AROMATICRING));

        PointWithPropertiesIfc miniPoint1 = minishape.get(7);
        List<StrikingProperties> propertiesMiniPoint1 = miniPoint1.getStrikingProperties();
        assertTrue(propertiesMiniPoint1.size() == 1);
        assertTrue(propertiesMiniPoint1.get(0) == StrikingProperties.HYDROPHOBE);
    }


    @Test
    public void testHbondAcceptorAndFormalNegativechargeInLineInGrid() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        float criticalDistance = algoParameters.getCELL_DIMENSION_OF_THE_PROBABILITY_MAP_ANGSTROM() * (float) Math.sqrt(3) + 0.1f;
        float cellSize = algoParameters.getCELL_DIMENSION_OF_THE_PROBABILITY_MAP_ANGSTROM();

        List<PointWithPropertiesIfc> listPointsWithProperties = new ArrayList<>();
        // cretae points
        for (int i = 0; i < 10; i++) {
            PointWithPropertiesIfc point = new PointWithProperties();
            float[] coords = new float[3];
            coords[0] = (float) i * cellSize;
            coords[1] = 0;
            coords[2] = 0;
            point.setCoords(new Point(coords));

            List<StrikingProperties> properties = new ArrayList<>();
            properties.add(StrikingProperties.HBOND_ACCEPTOR);
            if (i < 3){
                properties.add(StrikingProperties.NEGATIVE_CHARGE);
            }
            point.setStrikingProperties(properties);

            listPointsWithProperties.add(point);
        }

        CollectionOfPointsWithPropertiesIfc points = new CollectionOfPointsWithProperties(listPointsWithProperties);

        ShapeReductorByClustering shapeReductorByClustering = new ShapeReductorByClustering(points, algoParameters);

        Map<Integer, PointWithPropertiesIfc> minishape = shapeReductorByClustering.computeReducedCollectionOfPointsWithProperties();

        // That works nicely
        // H is HbondAcceptor and A is Negative Charge
        // H H H H H H H H H
        // A A A H H H H H H
        //   H           H
        //   A
        assertTrue(minishape.size() == 2);
        PointWithPropertiesIfc miniPoint2 = minishape.get(1);
        List<StrikingProperties> propertiesMiniPoint0 = miniPoint2.getStrikingProperties();
        assertTrue(propertiesMiniPoint0.size() == 2);
        assertTrue(propertiesMiniPoint0.contains(StrikingProperties.HBOND_ACCEPTOR));
        assertTrue(propertiesMiniPoint0.contains(StrikingProperties.NEGATIVE_CHARGE));

        PointWithPropertiesIfc miniPoint1 = minishape.get(7);
        List<StrikingProperties> propertiesMiniPoint1 = miniPoint1.getStrikingProperties();
        assertTrue(propertiesMiniPoint1.size() == 1);
        assertTrue(propertiesMiniPoint1.get(0) == StrikingProperties.HBOND_ACCEPTOR);
    }


    @Test
    public void testHbondAcceptorAHBaondDonnorndFormalNegativechargeInLineInGrid() throws IOException, ParsingConfigFileException {

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        float criticalDistance = algoParameters.getCELL_DIMENSION_OF_THE_PROBABILITY_MAP_ANGSTROM() * (float) Math.sqrt(3) + 0.1f;
        float cellSize = algoParameters.getCELL_DIMENSION_OF_THE_PROBABILITY_MAP_ANGSTROM();

        List<PointWithPropertiesIfc> listPointsWithProperties = new ArrayList<>();
        // cretae points
        for (int i = 0; i < 10; i++) {
            PointWithPropertiesIfc point = new PointWithProperties();
            float[] coords = new float[3];
            coords[0] = (float) i * cellSize;
            coords[1] = 0;
            coords[2] = 0;
            point.setCoords(new Point(coords));

            List<StrikingProperties> properties = new ArrayList<>();
            properties.add(StrikingProperties.HBOND_ACCEPTOR);
            if (i < 3){
                properties.add(StrikingProperties.NEGATIVE_CHARGE);
            }
            if (i > 4){
                properties.add(StrikingProperties.HBOND_DONNOR);
            }
            point.setStrikingProperties(properties);

            listPointsWithProperties.add(point);
        }

        CollectionOfPointsWithPropertiesIfc points = new CollectionOfPointsWithProperties(listPointsWithProperties);

        ShapeReductorByClustering shapeReductorByClustering = new ShapeReductorByClustering(points, algoParameters);

        Map<Integer, PointWithPropertiesIfc> minishape = shapeReductorByClustering.computeReducedCollectionOfPointsWithProperties();

        // That works nicely
        // H is HbondAcceptor and A is Negative Charge and D is HBOND Donnor
        // H H H H H H H H H
        // A A A H H D D D D
        //   H           H
        //   A           D
        assertTrue(minishape.size() == 2);
        PointWithPropertiesIfc miniPoint2 = minishape.get(1);
        List<StrikingProperties> propertiesMiniPoint0 = miniPoint2.getStrikingProperties();
        assertTrue(propertiesMiniPoint0.size() == 2);
        assertTrue(propertiesMiniPoint0.contains(StrikingProperties.HBOND_ACCEPTOR));
        assertTrue(propertiesMiniPoint0.contains(StrikingProperties.NEGATIVE_CHARGE));

        PointWithPropertiesIfc miniPoint1 = minishape.get(7);
        List<StrikingProperties> propertiesMiniPoint1 = miniPoint1.getStrikingProperties();
        assertTrue(propertiesMiniPoint1.size() == 2);
        assertTrue(propertiesMiniPoint1.get(0) == StrikingProperties.HBOND_ACCEPTOR);
        assertTrue(propertiesMiniPoint1.get(1) == StrikingProperties.HBOND_DONNOR);
    }
}
