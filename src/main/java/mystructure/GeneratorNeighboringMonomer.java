package mystructure;

import java.util.ArrayList;
import java.util.Arrays;
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

        List<MyMonomerIfc> foreignMonomerToExclude = new ArrayList<>();
        tempListMyChain.clear();

        for (MyChainIfc[] myChainSub : myChains) {
            for (MyChainIfc myChain : myChainSub) {
                treatchains(startingMonomer, myChain, foreignMonomerToExclude);
            }
        }

        MyChainIfc[] neighborsOfThisMonomer = tempListMyChain.toArray(new MyChainIfc[tempListMyChain.size()]);
        return neighborsOfThisMonomer;
        //}
        //return null;
    }


    public MyChainIfc[] computeAminoNeighborsOfAGivenResidue(MyMonomerIfc startingMonomer, List<MyMonomerIfc> foreignMonomerToExclude) {

        tempListMyChain.clear();

        for (MyChainIfc[] myChainSub : myChains) {
            for (MyChainIfc myChain : myChainSub) {
                treatchains(startingMonomer, myChain, foreignMonomerToExclude);
            }
        }

        MyChainIfc[] neighborsOfThisMonomer = tempListMyChain.toArray(new MyChainIfc[tempListMyChain.size()]);
        return neighborsOfThisMonomer;
        //}
        //return null;
    }


    private void treatchains(MyMonomerIfc startingMonomer, MyChainIfc myChain, List<MyMonomerIfc> foreignMonomerToExclude) {

        if (startingMonomer.getMyAtoms().length == 0) {
            return;
        }
        double distance;
        tempListMyMonomer.clear();
        A: for (MyMonomerIfc myMonomer : myChain.getMyMonomers()) {

            if (myMonomer.getMyAtoms().length == 0) {
                continue;
            }

            distance = ToolsMathAppliedToObjects.computeDistanceBetweenTwoResidues(startingMonomer, myMonomer);

            if ((distance < minDistanceToBeNeighbors - 0.001) && (distance > 0.001)) {
                //System.out.println("distance = " + distance);

                for (MyMonomerIfc monomerToExclude : foreignMonomerToExclude) {
                    int residueID = monomerToExclude.getResidueID();
                    char[] threeLetterCode = monomerToExclude.getThreeLetterCode();
                    char[] type = monomerToExclude.getType();
                    char[] chainId = monomerToExclude.getParent().getChainId();
                    if (Arrays.equals(myChain.getChainId(), chainId)) {
                        if (Arrays.equals(myChain.getMyMonomers()[0].getType(), type)) {
                            if (residueID == myMonomer.getResidueID()) {
                                if (Arrays.equals(threeLetterCode, myMonomer.getThreeLetterCode())) {
                                    System.out.println("Excluded " + monomerToExclude + " which matches " + myMonomer);
                                    continue A; // monomer excluded from neighbors
                                }
                            }
                        }
                    }
                }
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
