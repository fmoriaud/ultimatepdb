package mystructure;

import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import math.AddToMap;
import math.ToolsMath;
import parameters.AlgoParameters;
import shapeCompare.PairingTools;
import shapeCompare.ResultsFromEvaluateCost;


public class MyStructure implements MyStructureIfc {
    //-------------------------------------------------------------
    // Class variables
    //-------------------------------------------------------------
    private MyChainIfc[] myAminoChains;
    private MyChainIfc[] myHetatmChains;
    private MyChainIfc[] myNucleotideChains;

    private char[] fourLetterCode;
    private ExpTechniquesEnum expTechnique;
    private HBondDefinedByAtomAndMonomer[] hbonds;
    private PairOfMyAtomWithMyMonomerAndMychainReferences[] disulfideBridges;

    private List<MyChainIfc> tempChainList = new ArrayList<>();
    private List<MyMonomerIfc> tempMonomerList = new ArrayList<>();

    private FileTime lastModificationTime;



    //-------------------------------------------------------------
    // Constructors
    //-------------------------------------------------------------

    /**
     * Constructor for Cloner. It doesn't do anything
     * @param expTechnique
     * @param algoParameters
     * @throws ExceptionInMyStructurePackage
     */
    public MyStructure(ExpTechniquesEnum expTechnique, AlgoParameters algoParameters, MyChainIfc[] myAminoChains, MyChainIfc[] myHetatmChains, MyChainIfc[] myNucleotideChains) throws ExceptionInMyStructurePackage {

        if (algoParameters == null) {
            throw new ExceptionInMyStructurePackage("MyStructure cannot be built with a null AlgoParameters");
        }
        this.expTechnique = expTechnique;
        this.myAminoChains = myAminoChains;
        this.myHetatmChains = myHetatmChains;
        this.myNucleotideChains = myNucleotideChains;
    }

    /**
     * Constructor with all chains well defined as input
     * All needed structural information is computed in the constructors: MyMonomers neighbors by distance
     * and by bonds
     *
     * @param myAminoChains
     * @param myHetatmChains
     * @param myNucleotideChains
     * @throws ExceptionInMyStructurePackage
     */
    public MyStructure(MyChainIfc[] myAminoChains, MyChainIfc[] myHetatmChains, MyChainIfc[] myNucleotideChains, ExpTechniquesEnum expTechnique, AlgoParameters algoParameters) throws ExceptionInMyStructurePackage {

        if (algoParameters == null) {
            throw new ExceptionInMyStructurePackage("MyStructure cannot be built with a null AlgoParameters");
        }

        if (myAminoChains == null || myHetatmChains == null || myNucleotideChains == null) {
            throw new ExceptionInMyStructurePackage("MyStructure cannot be built with a null MyChain[]");
        }
        if (myAminoChains.length == 0 && myNucleotideChains.length == 0 && myHetatmChains.length == 0) {
            throw new ExceptionInMyStructurePackage("MyStructure cannot be built if all MyChain[] are empty");
        }

        this.myAminoChains = myAminoChains;
        this.myHetatmChains = myHetatmChains;
        this.myNucleotideChains = myNucleotideChains;
        this.expTechnique = expTechnique;
        computeStructuralInformation(this, algoParameters);
        fixParents(this);
    }


    /**
     * Factory method for convenience building when only chains with a given MyMonomerType
     *
     * @param mychains
     * @param myMonomerType
     * @param algoParameters
     * @return
     * @throws ExceptionInMyStructurePackage
     */
    public static MyStructure getMyStructure(MyChainIfc[] mychains, MyMonomerType myMonomerType, ExpTechniquesEnum expTechnique, AlgoParameters algoParameters) throws ExceptionInMyStructurePackage {

        MyChainIfc[] mychainsEmpty1 = new MyChainIfc[0];
        MyChainIfc[] mychainsEmpty2 = new MyChainIfc[0];

        switch (myMonomerType) {
            case AMINOACID:
                return new MyStructure(mychains, mychainsEmpty1, mychainsEmpty2, expTechnique, algoParameters);
            case HETATM:
                return new MyStructure(mychainsEmpty1, mychains, mychainsEmpty2, expTechnique, algoParameters);
            case NUCLEOTIDE:
                return new MyStructure(mychainsEmpty1, mychainsEmpty2, mychains, expTechnique, algoParameters);
            default:
                return null;
        }
    }



    public MyStructure(MyChainIfc chain1, MyChainIfc chain2, AlgoParameters algoParameters) {

        // I keep them all in the same chain as it could be a peptide already clean with polymeric hetatm inserted

        MyChainIfc[] emptyArray = new MyChainIfc[0];
        myHetatmChains = emptyArray;
        MyChainIfc[] emptyArray2 = new MyChainIfc[0];
        myNucleotideChains = emptyArray2;

        this.myAminoChains = new MyChainIfc[2];
        this.myAminoChains[0] = chain1;
        this.myAminoChains[1] = chain2;
        this.fourLetterCode = "XXXX".toCharArray();

        computeStructuralInformation(this, algoParameters);
        fixParents(this);
    }


    public MyStructure(String readV3000) throws ExceptionInMyStructurePackage {

        makeStructureFromV3000(readV3000);

        // need removeHydrogenAndcomputeStructuralInformations(algoParameters); ?
        fixParents(this);
    }


    //-------------------------------------------------------------
    // Interface & Public methods
    //-------------------------------------------------------------
    @Override
    public MyChainIfc getAminoChain(int i) {
        return myAminoChains[i];
    }

    @Override
    public MyChainIfc getNucleosideChain(int i) {
        return myNucleotideChains[i];
    }

    @Override
    public MyChainIfc getHetatmChain(int i) {
        return myHetatmChains[i];
    }


    @Override
    public MyChainIfc[] getAllAminochains() {
        return myAminoChains;
    }


    @Override
    public MyChainIfc[] getAllHetatmchains() {
        return myHetatmChains;
    }


    @Override
    public MyChainIfc[] getAllNucleosidechains() {
        return myNucleotideChains;
    }


    @Override
    public MyChainIfc[] getAllChains() {
        tempChainList.clear();
        if (this.getAllAminochains() != null) {
            tempChainList.addAll(Arrays.asList(this.getAllAminochains()));
        }
        if (this.getAllHetatmchains() != null) {
            tempChainList.addAll(Arrays.asList(this.getAllHetatmchains()));
        }
        if (this.getAllNucleosidechains() != null) {
            tempChainList.addAll(Arrays.asList(this.getAllNucleosidechains()));
        }
        MyChainIfc[] chains = tempChainList.toArray(new MyChainIfc[tempChainList.size()]);
        return chains;
    }


    /**
     * Returns the MyChains which are relevant for Shape building
     * That includes all amino chains, all nucleosides chains, and some of heteroatomchains if it is
     * a big enough residue
     */
    @Override
    public synchronized MyChainIfc[] getAllChainsRelevantForShapeBuilding() { // the synchronized is needed but maybe I should code differently to avoid it

        tempChainList.clear();
        if (this.getAllAminochains() != null && this.getAllAminochains().length > 0) {
            tempChainList.addAll(Arrays.asList(this.getAllAminochains()));
        }

        if (this.getAllHetatmchains() != null && this.getAllHetatmchains().length > 0) {
            for (MyChainIfc chain : this.getAllHetatmchains()) {

                tempMonomerList.clear();
                for (MyMonomerIfc myMonomer : chain.getMyMonomers()) {
                    if (BigHetatmResidues.isMyMonomerABigResidue(myMonomer) == true) {
                        tempMonomerList.add(myMonomer);
                    }
                }
                if (tempMonomerList.size() > 0) {
                    MyMonomerIfc[] monomers = tempMonomerList.toArray(new MyMonomerIfc[tempMonomerList.size()]);
                    MyChainIfc newChain = new MyChain(monomers, chain.getChainId());
                    tempChainList.add(newChain);
                }
            }
        }

        if (this.getAllNucleosidechains() != null && this.getAllNucleosidechains().length > 0) {
            tempChainList.addAll(Arrays.asList(this.getAllNucleosidechains()));
        }

        Iterator<MyChainIfc> it = tempChainList.iterator();
        while (it.hasNext()) {
            MyChainIfc nextChain = it.next();
            if (nextChain == null) {
                it.remove(); // dont know why but there are null chain created
            }
        }

        MyChainIfc[] chains = tempChainList.toArray(new MyChainIfc[tempChainList.size()]);
        return chains;
    }


    @Override
    public synchronized void removeChain(char[] chainId) {

        tempChainList.clear();
        if (this.getAllAminochains() != null && this.getAllAminochains().length > 0) {
            tempChainList.addAll(Arrays.asList(this.getAllAminochains()));
        }
        Iterator<MyChainIfc> it = tempChainList.iterator();
        while (it.hasNext()) {
            MyChainIfc nextChain = it.next();
            if (String.valueOf(nextChain.getChainId()).equals(String.valueOf(chainId))) {
                it.remove(); // dont know why but there are null chain created
            }
        }

        this.myAminoChains = MyStructureTools.makeArrayFromListMyChains(tempChainList);
    }


    @Override
    public MyChainIfc getAminoMyChain(char[] chainId) {
        for (MyChainIfc myChain : myAminoChains) {
            if (String.valueOf(myChain.getChainId()).equals(String.valueOf(chainId))) {
                return myChain;
            }
        }
        return null;
    }


    @Override
    public MyChainIfc getNucleosideChain(char[] chainId) {
        for (MyChainIfc myChain : myNucleotideChains) {
            if (String.valueOf(myChain.getChainId()).equals(String.valueOf(chainId))) {
                return myChain;
            }
        }
        return null;
    }


    @Override
    public MyChainIfc getHeteroChain(char[] chainId) {
        for (MyChainIfc myChain : myHetatmChains) {
            if (String.valueOf(myChain.getChainId()).equals(String.valueOf(chainId))) {
                return myChain;
            }
        }
        return null;
    }


    @Override
    public void setAminoChain(char[] chainId, MyChainIfc myNewChain) {
        tempChainList.clear(); // I use a list because I didnt succeed in changing an element of the array
        tempChainList.addAll(Arrays.asList(this.getAllAminochains()));

        for (int i = 0; i < tempChainList.size(); i++) {
            if (String.valueOf(tempChainList.get(i).getChainId()).equals(String.valueOf(chainId))) {
                tempChainList.set(i, myNewChain);
            }
        }
        this.myAminoChains = MyStructureTools.makeArrayFromListMyChains(tempChainList);
    }


    @Override
    public int getAminoChainCount() {
        return myAminoChains.length;
    }


    @Override
    public int getAminoMonomercount() {
        int aminoMonomercount = 0;
        for (MyChainIfc chain : this.getAllAminochains()) {
            aminoMonomercount += chain.getMyMonomers().length;
        }

        return aminoMonomercount;
    }


    @Override
    public int indexOfAnAminoChain(MyChainIfc myChainToSearch) {
        for (int i = 0; i < getAminoChainCount(); i++) {
            if (myAminoChains[i].equals(myChainToSearch)) {
                return i;
            }
        }
        return -1;
    }


    public char[] getFourLetterCode() {
        return fourLetterCode;
    }


    public void setFourLetterCode(char[] fourLetterCode) {
        this.fourLetterCode = fourLetterCode;
    }


    /**
     * Convert this MyStructure to a V3000 molecular format file
     *
     * @return V3000 file as a String
     */
    @Override
    public String toV3000() {

        MyStructureToV3000 myStructureToV3000 = new MyStructureToV3000(this);
        String myStructureV3000 = myStructureToV3000.getV3000();

        return myStructureV3000;
    }


    public HBondDefinedByAtomAndMonomer[] getHbonds() {
        return hbonds;
    }


    public void setHbonds(HBondDefinedByAtomAndMonomer[] hbonds) {
        this.hbonds = hbonds;
    }


    public PairOfMyAtomWithMyMonomerAndMychainReferences[] getDisulfideBridges() {
        return disulfideBridges;
    }


    public void setDisulfideBridges(
            PairOfMyAtomWithMyMonomerAndMychainReferences[] disulfideBridges) {
        this.disulfideBridges = disulfideBridges;
    }


    public FileTime getLastModificationTime() {
        return lastModificationTime;
    }


    public void setLastModificationTime(FileTime lastModificationTime) {
        this.lastModificationTime = lastModificationTime;
    }




    //-------------------------------------------------------------
    // Implementation
    //-------------------------------------------------------------

    /**
     * Make sure all parents are set correctly
     *
     * @param myStructure
     */
    private void fixParents(MyStructureIfc myStructure) {
        for (MyChainIfc chain : myStructure.getAllChains()) {
            for (MyMonomerIfc monomer : chain.getMyMonomers()) {
                monomer.setParent(chain);
                for (MyAtomIfc atom : monomer.getMyAtoms()) {
                    atom.setParent(monomer);
                }
            }
        }
    }



    private void computeStructuralInformation(MyStructureIfc myStructure, AlgoParameters algoParameters) {
        fixParents(myStructure);
        MyStructureTools.computeAndStoreNeighBorhingAminoMonomersByDistanceBetweenRepresentativeMyAtom(myStructure, algoParameters);
        MyStructureTools.computeAndStoreNeighboringMonomersByBond(myStructure);
    }



    private void makeStructureFromV3000(String readV3000) throws ExceptionInMyStructurePackage {

        String lines[] = readV3000.split("\\r?\\n");

        boolean collectingAtoms = false;
        List<MyAtomIfc> listAtoms = new ArrayList<>();

        boolean collectingBonds = false;
        List<String> bondLines = new ArrayList<>();
        char insertionLetter = 0;

        for (String line : lines) {
            if (line.equals("M  V30 BEGIN ATOM")) {
                collectingAtoms = true;
                continue;
            }

            if (collectingAtoms == true) {
                if (line.equals("M  V30 END ATOM")) {
                    collectingAtoms = false;
                    continue;
                }
                MyAtomIfc atom;
                try {
                    atom = parseLineToMyAtom(line);
                } catch (ExceptionInMyStructurePackage e) {
                    continue;
                }
                listAtoms.add(atom);
            }

            if (line.equals("M  V30 BEGIN BOND")) {
                collectingBonds = true;
                continue;
            }
            if (collectingBonds == true) {
                if (line.equals("M  V30 END BOND")) {
                    collectingBonds = false;
                    continue;
                }
                bondLines.add(line);
            }
        }

        MyAtomIfc[] atoms = listAtoms.toArray(new MyAtomIfc[listAtoms.size()]);

        MyMonomer[] monomers = new MyMonomer[1];
        monomers[0] = new MyMonomer(atoms, "XXX".toCharArray(), 0, MyMonomerType.AMINOACID, false, insertionLetter, " ".toCharArray()[0]);

        // As the only one monomer is built I can add bonds
        Map<MyAtomIfc, List<MyBondIfc>> mapBonds = new HashMap<>();
        for (String bondLine : bondLines) {

            String regexp = "[\\s,;\\t]+";
            String tokens[] = bondLine.split(regexp);
            //int bondId = Integer.valueOf(tokens[2]);
            int bondOrder = Integer.valueOf(tokens[3]);
            int bondedAtomId1 = Integer.valueOf(tokens[4]);
            int bondedAtomId2 = Integer.valueOf(tokens[5]);

            MyAtomIfc bondedAtom1 = monomers[0].getAtomById(bondedAtomId1);
            MyAtomIfc bondedAtom2 = monomers[0].getAtomById(bondedAtomId2);

            MyBondIfc bond;
            try {
                bond = new MyBond(bondedAtom2, bondOrder);
            } catch (ExceptionInMyStructurePackage e) {
                continue;
            }
            AddToMap.addElementToAMapOfList(mapBonds, bondedAtom1, bond);

            MyBondIfc bond2;
            try {
                bond2 = new MyBond(bondedAtom1, bondOrder);
            } catch (ExceptionInMyStructurePackage e) {
                continue;
            }
            AddToMap.addElementToAMapOfList(mapBonds, bondedAtom2, bond2);

        }
        for (Entry<MyAtomIfc, List<MyBondIfc>> entry : mapBonds.entrySet()) {
            MyBondIfc[] bonds = entry.getValue().toArray(new MyBondIfc[entry.getValue().size()]);
            entry.getKey().setBonds(bonds);
        }

        MyChainIfc newChain = new MyChain(monomers, "X".toCharArray());

        List<MyChainIfc> chains = new ArrayList<>();
        chains.add(newChain);
        this.myAminoChains = MyStructureTools.makeArrayFromListMyChains(chains);

        this.myHetatmChains = new MyChainIfc[0];
        this.myNucleotideChains = new MyChainIfc[0];
    }


    private static MyAtomIfc parseLineToMyAtom(String line) throws ExceptionInMyStructurePackage {

        String regexp = "[\\s,;\\t]+";
        String lines[] = line.split(regexp);
        int atomId = Integer.valueOf(lines[2]);
        String atomSymbol = lines[3];
        float[] coords = new float[3];
        coords[0] = Float.valueOf(lines[4]);
        coords[1] = Float.valueOf(lines[5]);
        coords[2] = Float.valueOf(lines[6]);
        //System.out.println(atomId + " " + atomSymbol + " " + coords[0] + " " + coords[1] + " " + coords[2]);

        MyAtomIfc newAtom = new MyAtom(atomSymbol.toCharArray(), coords, "".toCharArray(), atomId);
        return newAtom;
    }



    private void removeMonomer(MyChainIfc[] chains, MyMonomerIfc monomerToRemove) {

        for (MyChainIfc myChain : chains) {
            for (MyMonomerIfc monomer : myChain.getMyMonomers()) {
                if (monomer == monomerToRemove) {
                    MyMonomerIfc[] newMonomers = new MyMonomerIfc[myChain.getMyMonomers().length - 1];
                    int currentBondId = 0;
                    for (MyMonomerIfc oldMyMonomer : myChain.getMyMonomers()) {
                        if (oldMyMonomer == monomerToRemove) {
                            continue;
                        }
                        newMonomers[currentBondId] = oldMyMonomer;
                        currentBondId += 1;
                    }
                    myChain.setMyMonomers(newMonomers);
                }
            }
        }
    }


    //-------------------------------------------------------------
    // Getters and Setters
    //-------------------------------------------------------------
    public ExpTechniquesEnum getExpTechnique() {
        return expTechnique;
    }

}
