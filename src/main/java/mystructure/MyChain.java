package mystructure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class MyChain implements MyChainIfc {
    //-------------------------------------------------------------
    // Class variables
    //-------------------------------------------------------------
    private MyMonomerIfc[] myMonomers;
    private char[] chainId;
    private List<MyMonomerIfc> tempMyMonomerList = new ArrayList<>();


    // TODO Make this class more solid by including the computation of neighbors when they are asked
    // probably put the context in the request and if the same then don't redo.
    //-------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------

    /**
     * Class to store an ensemble of MyMonomer
     * chainId is set to the one of the first MyMonomer therein, or to default value if not available
     *
     * @param myMonomers: which are in this MyChain
     */
    public MyChain(List<MyMonomerIfc> myMonomers) {

        MyMonomerIfc[] myMonomersArray = myMonomers.toArray(new MyMonomerIfc[myMonomers.size()]);
        this.myMonomers = myMonomersArray;
        if (myMonomers.size() > 0 && myMonomersArray[0].getParent() != null && myMonomersArray[0].getParent().getChainId() != null) {
            this.chainId = myMonomersArray[0].getParent().getChainId();
        } else {
            this.chainId = MyStructureConstants.CHAIN_ID_DEFAULT.toCharArray();
        }
    }


    /**
     * @param myMonomers: which are in this MyChain
     * @param chainId:    Chain ID, best use the one found in PDB files
     */
    public MyChain(MyMonomerIfc[] myMonomers, char[] chainId) {
        this.myMonomers = myMonomers;
        this.chainId = chainId;
    }


    /**
     * Class to store an ensemble of MyMonomer: this constructor is for convenience to built from one MyMonomer
     *
     * @param monomer: which is in this MyChain
     * @param chainId: Chain ID, best use the one found in PDB files
     */
    public MyChain(MyMonomerIfc monomer, char[] chainId) {

        MyMonomerIfc[] monomers = new MyMonomerIfc[1];
        monomers[0] = monomer;
        monomer.setParent(this);
        this.myMonomers = monomers;
        this.chainId = chainId;
    }


    //-------------------------------------------------------------
    // Interface & Public methods
    //-------------------------------------------------------------

    /**
     * For debugging only
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("chain: " + String.valueOf(chainId) + " contains " + myMonomers.length + " monomers");
        return sb.toString();
    }


    /**
     * Return the MyMonomer with the given residueID or null if not found
     */
    @Override
    public MyMonomerIfc getMyMonomerFromResidueId(int residueID) {
        MyMonomerIfc monomerToreturn = null;

        for (MyMonomerIfc monomer : this.getMyMonomers()) {
            if (monomer.getResidueID() == residueID) {
                monomerToreturn = monomer;
                break;
            }
        }
        return monomerToreturn;
    }


    /**
     * Return the MyMonomer with the given rank id as they are stored in the List
     */
    @Override
    public MyMonomerIfc getMyMonomerByRank(int i) {
        if (i > myMonomers.length - 1) {
            return null;
        }
        return myMonomers[i];
    }


    /**
     * Remove the given MyMonomer as being the same object
     */
    @Override
    public void removeMyMonomer(MyMonomerIfc myMonomer) {
        tempMyMonomerList.clear();
        tempMyMonomerList.addAll(Arrays.asList(this.myMonomers));
        Iterator<MyMonomerIfc> it = tempMyMonomerList.iterator();
        while (it.hasNext()) {
            MyMonomerIfc nextMyMonomer = it.next();
            if (nextMyMonomer == myMonomer) {
                it.remove();
                break;
            }
        }
        MyMonomerIfc[] myMonomers = tempMyMonomerList.toArray(new MyMonomerIfc[tempMyMonomerList.size()]);
        this.myMonomers = myMonomers;
    }


    /**
     * Extract a subchain. MyMonomer neighbors are kept the same. MyBonds are kept the same.
     */
    @Override
    public MyChainIfc makeSubchain(int startRankId, int length) {
        MyMonomerIfc[] subChain = new MyMonomerIfc[length];
        for (int i = 0; i < length; i++) {
            subChain[i] = this.getMyMonomerByRank(startRankId + i);
        }
        MyChainIfc subChainToReturn = new MyChain(subChain, this.getChainId());
        return subChainToReturn;
    }


    /**
     * Replace a monomer by another one and update the neighbors
     */
    @Override
    public void replaceMonomer(MyMonomerIfc oldMonomer, MyMonomerIfc newMonomer) {
        // only works if backbone atoms are kept
        // otherwise bonding problems

        int rank = 0;
        for (MyMonomerIfc monomer : this.myMonomers) {
            if (oldMonomer == monomer) {
                newMonomer.setParent(oldMonomer.getParent());
                this.myMonomers[rank] = newMonomer;
                break;
            }
            rank += 1;
        }

        // The neighbors of this monomer are the same as before
        MyChainIfc[] neighbors = oldMonomer.getNeighboringAminoMyMonomerByRepresentativeAtomDistance();
        newMonomer.setNeighboringAminoMyMonomerByRepresentativeAtomDistance(neighbors);

        MyMonomerIfc[] neighborsByBond = oldMonomer.getNeighboringMyMonomerByBond();
        newMonomer.setNeighboringMyMonomerByBond(neighborsByBond);

        // I should update reference
        for (MyChainIfc chain : neighbors) {
            for (MyMonomerIfc monomer : chain.getMyMonomers()) {
                // each and every neighbor
                MyChainIfc[] neighborsOfNeighbor = monomer.getNeighboringAminoMyMonomerByRepresentativeAtomDistance();
                // If I find oldmonomer then I replace with new
                for (MyChainIfc chain2 : neighborsOfNeighbor) {
                    for (int i = 0; i < chain2.getMyMonomers().length; i++) {
                        if (chain2.getMyMonomerByRank(i) == oldMonomer) {
                            chain2.getMyMonomers()[i] = newMonomer;// does it work ???
                        }
                    }
                }
            }
        }

        for (MyMonomerIfc monomer : neighborsByBond) {
            // each and every neighbor
            MyChainIfc[] neighborsOfNeighbor = monomer.getNeighboringAminoMyMonomerByRepresentativeAtomDistance();
            // If I find oldmonomer then I replace with new
            for (MyChainIfc chain2 : neighborsOfNeighbor) {
                for (int i = 0; i < chain2.getMyMonomers().length; i++) {
                    if (chain2.getMyMonomerByRank(i) == oldMonomer) {
                        chain2.getMyMonomers()[i] = newMonomer;// does it work ???
                    }
                }
            }
        }
    }


    @Override
    public void addAtCorrectRank(MyMonomerIfc monomer) {

        if (this.getMyMonomers().length == 0) {
            MyMonomerIfc[] monomers = new MyMonomerIfc[1];
            monomers[0] = monomer;
            this.setMyMonomers(monomers);
        } else {
            // correct rank is in between
            int residueIdToInsert = monomer.getResidueID();

            // insert First
            if (residueIdToInsert < this.getMyMonomers()[0].getResidueID()) {
                MyMonomerIfc[] monomers = new MyMonomerIfc[this.getMyMonomers().length + 1];
                monomers[0] = monomer;
                for (int i = 0; i < this.getMyMonomers().length; i++) {
                    monomers[i + 1] = this.getMyMonomers()[i];
                }
                this.setMyMonomers(monomers);
                return;
            }
            // insert Last
            if (residueIdToInsert > this.getMyMonomers()[this.getMyMonomers().length - 1].getResidueID()) {
                MyMonomerIfc[] monomers = new MyMonomerIfc[this.getMyMonomers().length + 1];
                monomers[monomers.length - 1] = monomer;
                for (int i = 0; i < this.getMyMonomers().length; i++) {
                    monomers[i] = this.getMyMonomers()[i];
                }
                this.setMyMonomers(monomers);
                return;
            }
            // find right position in between
            for (int i = 0; i < this.getMyMonomers().length - 1; i++) {
                int currentResidueId = this.getMyMonomers()[i].getResidueID();
                int nextResidueId = this.getMyMonomers()[i + 1].getResidueID();

                if (residueIdToInsert > currentResidueId && residueIdToInsert < nextResidueId) {

                    // insert in between
                    MyMonomerIfc[] monomers = new MyMonomerIfc[this.getMyMonomers().length + 1];
                    for (int j=0; j<= currentResidueId; j++){
                        monomers[j] = this.getMyMonomers()[j];
                    }
                    monomers[currentResidueId+1] = monomer;

                    for (int j=currentResidueId+2; j<monomers.length-1; j++){
                        monomers[j] = this.getMyMonomers()[j-1];
                    }
                    return;
                }

            }

        }
        System.out.println("Insertion failed");
        System.exit(0);
    }

    @Override
    public void addLastRank(MyMonomerIfc monomer) {
        if (this.getMyMonomers().length == 0) {
            MyMonomerIfc[] monomers = new MyMonomerIfc[1];
            monomers[0] = monomer;
            this.setMyMonomers(monomers);
        } else {
            MyMonomerIfc[] monomers = new MyMonomerIfc[this.getMyMonomers().length + 1];
            monomers[monomers.length - 1] = monomer;
            for (int i = 0; i < this.getMyMonomers().length; i++) {
                monomers[i] = this.getMyMonomers()[i];
            }
            this.setMyMonomers(monomers);
        }
    }


    //-------------------------------------------------------------
    // Getters and Setters
    //-------------------------------------------------------------
    @Override
    public void setMyMonomers(MyMonomerIfc[] myMonomers) {
        this.myMonomers = myMonomers;
    }


    @Override
    public MyMonomerIfc[] getMyMonomers() {
        return myMonomers;
    }


    @Override
    public char[] getChainId() {
        return chainId;
    }


    @Override
    public void setChainId(char[] chainId) {
        this.chainId = chainId;
    }
}
