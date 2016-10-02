package mystructure;

import java.util.ArrayList;
import java.util.List;

import math.ToolsMathAppliedToObjects;

public class GeneratorNeighboringMonomer {
    //------------------------
    // Class variables
    //------------------------
    private MyChainIfc[][] myChains;
    private double minDistanceToBeNeighbors;

    private List<MyMonomerIfc> tempListMyMonomer = new ArrayList<>();
    private List<MyChainIfc> tempListMyChain = new ArrayList<>();


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------

    public GeneratorNeighboringMonomer(double minDistanceToBeNeighbors, MyChainIfc[]... myChains) {
        this.minDistanceToBeNeighbors = minDistanceToBeNeighbors;
        this.myChains = myChains;
    }


    // -------------------------------------------------------------------
    // Public & Interface Methods
    // -------------------------------------------------------------------

    /**
     * Return neighbors of a given MyMonomer in the MyStructure defined in Constructor
     *
     * @param startingMonomer : the monomer for which neighbors are computed can be in the MyStructure or not.
     * @return neighbors are organized in MyChains, keeping the same MyChain as in the MyStructure in constructor
     */
    public MyChainIfc[] computeAminoNeighborsOfAGivenResidue(MyMonomerIfc startingMonomer) {

        tempListMyChain.clear();

        for (MyChainIfc[] myChainSub : myChains) {
            for (MyChainIfc myChain : myChainSub) {
                treatchains(startingMonomer, myChain);
            }
        }

        MyChainIfc[] neighborsOfThisMonomer = tempListMyChain.toArray(new MyChainIfc[tempListMyChain.size()]);
        return neighborsOfThisMonomer;
        //}
        //return null;
    }


    private void treatchains(MyMonomerIfc startingMonomer, MyChainIfc myChain) {

        if (startingMonomer.getMyAtoms().length == 0) {
            return;
        }
        double distance;
        tempListMyMonomer.clear();
        for (MyMonomerIfc myMonomer : myChain.getMyMonomers()) {

            if (myMonomer.getMyAtoms().length == 0) {
                continue;
            }

            distance = ToolsMathAppliedToObjects.computeDistanceBetweenTwoResidues(startingMonomer, myMonomer);

            if ((distance < minDistanceToBeNeighbors - 0.001) && (distance > 0.001)) {
                //System.out.println("distance = " + distance);
                tempListMyMonomer.add(myMonomer);
            }
        }
        if (tempListMyMonomer.size() != 0) {
            MyMonomerIfc[] monomers = tempListMyMonomer.toArray(new MyMonomerIfc[tempListMyMonomer.size()]);
            MyChainIfc chainWithNeighbors = new MyChain(monomers, myChain.getChainId());
            tempListMyChain.add(chainWithNeighbors);
        }
    }
}
