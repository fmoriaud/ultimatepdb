package java.structure;

import org.junit.Test;
import structure.*;

import structure.TestTools;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 29/08/16.
 */
public class GenerateNeighboringMonomerUsedForShapeGenerationTest {

    @Test
    public void testMonomersAreNeighbors() {

        MyStructureIfc myStructure = null;
        try {
            // currently two chains with two monomers each with three atom each
            myStructure = TestTools.buildValidMyStructure(MyMonomerType.AMINOACID);
        } catch (ExceptionInMyStructurePackage e1) {
            assertTrue(false);
        }

        float minDistanceToBeneighbor = 10;
        for (int i=1; i<10; i++){

            float y = 0f;
            float z = 0f;
            float x = 0f;

            // set coordinates to representative myAtom
            // set their x,y,z on a x axis, so distance is 1D and therefore easy to check
            List<Float> xCoordinates = new ArrayList<>();
            for (MyChainIfc chain: myStructure.getAllChains()){
                for (MyMonomerIfc monomer: chain.getMyMonomers()){
                    MyAtomIfc repAtom = MyStructureTools.getRepresentativeMyAtom(monomer);

                    x += minDistanceToBeneighbor * 2 / i;
                    float[] coords = new float[3];
                    coords[0] = x;
                    coords[1] = y;
                    coords[2] = z;
                    repAtom.setCoords(coords);
                    xCoordinates.add(x);
                }
            }

            // clone in order that neighbors are computed (coordinates were changed so neighbors are wrong)
            MyStructureIfc myStructureCloned = null;
            try {
                myStructureCloned = myStructure.cloneWithSameObjects();
            } catch (ExceptionInMyStructurePackage e) {
                e.printStackTrace();
            }


            GeneratorNeighboringMonomerUsedForShapeGeneration generator = new GeneratorNeighboringMonomerUsedForShapeGeneration(myStructureCloned, minDistanceToBeneighbor);

            MyChainIfc[] aminoChains = myStructureCloned.getAllAminochains();
            int id = 0;
            for (MyChainIfc chain: aminoChains){
                for (MyMonomerIfc startingMonomer: chain.getMyMonomers()){
                    MyChainIfc[] neighbors = generator.computeAminoNeighborsOfAGivenResidue(startingMonomer);

                    int expected = expectedNeighborCount(id, xCoordinates, minDistanceToBeneighbor);
                    int actual = TestTools.countMyMonomer(neighbors);

                    assertTrue(expected == actual);
                    id += 1;
                }
            }
        }
    }


    private int expectedNeighborCount(int id, List<Float> xCoordinates, double threshold){

        int neighborCount = 0;
        Float currentX = xCoordinates.get(id);
        for (Float x: xCoordinates){
            float dist = Math.abs(x-currentX);
            if (dist > 0.0001 && dist < threshold){
                neighborCount += 1;
            }
        }
        return neighborCount;
    }
}
