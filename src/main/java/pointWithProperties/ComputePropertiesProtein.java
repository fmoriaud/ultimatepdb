package pointWithProperties;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import math.ToolsMath;
import parameters.AlgoParameters;
import mystructure.AtomProperties;
import mystructure.HBondDefinedWithAtoms;
import mystructure.MyAtomIfc;
import mystructure.MyBondIfc;
import mystructure.MyChainIfc;
import mystructure.MyMonomerIfc;
import mystructure.MyStructureIfc;

public class ComputePropertiesProtein implements ComputePropertiesIfc {
    //------------------------
    // Class variables
    //------------------------
    private MyStructureIfc structureShape;
    private AlgoParameters algoParameters;
    private Map<MyAtomIfc, Float> mapMinDistanceAtomToLigand;
    private List<HBondDefinedWithAtoms> dehydrons;

    private Float charge;
    private Float hydrophobicity;
    private Float hAcceptor;
    private Float hDonnor;
    private Float dehydron;
    private Float aromaticring;

    private MyAtomIfc atomClosest;
    private float distanceToAtomClosest;


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public ComputePropertiesProtein(MyStructureIfc structure, AlgoParameters algoParameters, List<HBondDefinedWithAtoms> dehydrons, List<PointIfc> listOfLigandPoints) {
        this.structureShape = structure;
        this.algoParameters = algoParameters;
        this.dehydrons = dehydrons;
        this.mapMinDistanceAtomToLigand = computeMapDistanceOfAtomToLigand(listOfLigandPoints, structure, algoParameters);
    }


    // -------------------------------------------------------------------
    // Public Interface
    // -------------------------------------------------------------------
    @Override
    public boolean compute(float[] atPosition) {

        distanceToAtomClosest = searchAtomClosest(atPosition);
        // FMM refactor: It is a bit weird atomClosest is also set by method above but it is not clear
        double fwhm = AtomProperties.findFwhmForMyAtom(atomClosest);

        if (distanceToAtomClosest <= fwhm) {

            this.charge = AtomProperties.findChargeForMyAtom(atomClosest);
            this.hydrophobicity = AtomProperties.findHydrophobicityForMyAtom(atomClosest);
            this.hDonnor = AtomProperties.findHydrogenOfHDonnorForMyAtom(atomClosest);
            this.hAcceptor = AtomProperties.findhAcceptorForMyAtom(atomClosest);
            this.dehydron = findDehydronForMyAtom(atomClosest);
            this.aromaticring = AtomProperties.findAromaticRingForMyAtom(atomClosest);

            if ((hDonnor != null && hDonnor > 0.1) || (hAcceptor != null && hAcceptor > 0.1)) { // for performance needed only if donnor or acceptor not 0

                boolean orientationOK = checkIfDonnorOrAcceptorAtomArePointingTowardsLigand(atomClosest);
                if (orientationOK == false) {
                    if (hDonnor != null) {
                        hDonnor = 0.0f;
                    }
                    if (hAcceptor != null) {
                        hAcceptor = 0.0f;
                    }
                } else {
                    // TODO make a map as otherwise we search several time the same as they are several grid points around one atom
                    int countOfHydrophobicAtomRelevantForHbond = getCountOfHydrophobicAtomsInTheNeighborhoodOfMyAtomAndCloseToLigand(atomClosest, mapMinDistanceAtomToLigand, algoParameters);
                    //System.out.println("countOfHydrophobicAtomRelevantForHbond = " + countOfHydrophobicAtomRelevantForHbond);

                    tuneHydrogenBondingAccordingToHydrophobicAtomsAround();
                }
            }
        } else {
            return false;
        }
        return true;
    }


    // -------------------------------------------------------------------
    // Private & Implementation Methods
    // -------------------------------------------------------------------
    private void tuneHydrogenBondingAccordingToHydrophobicAtomsAround() {

        int countOfHydrophobicAtomRelevantForHbond = getCountOfHydrophobicAtomsInTheNeighborhoodOfMyAtomAndCloseToLigand(atomClosest, mapMinDistanceAtomToLigand, algoParameters);

        float factor = 1.0f;
        if (countOfHydrophobicAtomRelevantForHbond < 3){
            factor = 0.0f;
        }

        if (this.hDonnor != null) {
            this.hDonnor = this.hDonnor * factor;
        }

        if (this.hAcceptor != null) {
            this.hAcceptor = this.hAcceptor * factor;
        }
    }

    private Map<MyAtomIfc, Float> computeMapDistanceOfAtomToLigand(List<PointIfc> listOfLigandPoints, MyStructureIfc myStructure, AlgoParameters algoParameters) {

        Map<MyAtomIfc, Float> tempMapMinDistanceAtomToLigand = new HashMap<>();

        for (MyChainIfc chain : myStructure.getAllChainsRelevantForShapeBuilding()) {
            treatChain(listOfLigandPoints, tempMapMinDistanceAtomToLigand, chain);
        }
        return tempMapMinDistanceAtomToLigand;
    }


    private void treatChain(List<PointIfc> listOfLigandPoints, Map<MyAtomIfc, Float> tempMapMinDistanceAtomToLigand, MyChainIfc chain) {

        for (MyMonomerIfc monomer : chain.getMyMonomers()) {

            for (MyAtomIfc atom : monomer.getMyAtoms()) {
                float minDistanceOfThisGridPointToAnyAtomOfPeptide = computeSmallestDistanceBetweenAPointAndListOfPoints(atom.getCoords(), listOfLigandPoints);
                tempMapMinDistanceAtomToLigand.put(atom, minDistanceOfThisGridPointToAnyAtomOfPeptide);
            }
        }
    }


    public float computeSmallestDistanceBetweenAPointAndListOfPoints(float[] atPosition, List<PointIfc> listOfPointsWithLennardJonesQuery) {

        float minDistance = Float.MAX_VALUE;
        for (PointIfc pointsWithLennardJones : listOfPointsWithLennardJonesQuery) {

            float[] atomPosition = pointsWithLennardJones.getCoords();
            float distance = ToolsMath.computeDistance(atomPosition, atPosition);
            if (distance < minDistance) {
                minDistance = distance;
            }
        }
        return minDistance;
    }


    private int getCountOfHydrophobicAtomsInTheNeighborhoodOfMyAtomAndCloseToLigand(MyAtomIfc myAtom, Map<MyAtomIfc, Float> mapMinDistanceAtomToLigand, AlgoParameters algoParameters) {

        int countHydrophobicAtom = 0;
        for (MyChainIfc chainNeighbor : myAtom.getParent().getNeighboringAminoMyMonomerByRepresentativeAtomDistance()) {
            for (MyMonomerIfc monomerNeighbor : chainNeighbor.getMyMonomers()) {
                for (MyAtomIfc atomNeighbor : monomerNeighbor.getMyAtoms()) {

                    if (isMyAtomHydrophobic(atomNeighbor)) {
                        float distance = ToolsMath.computeDistance(myAtom.getCoords(), atomNeighbor.getCoords());
                        if (distance < algoParameters.getCUTOFF_DISTANCE_FORHYDROPHOBIC_AROUND_HBOND()) {
                            if (!mapMinDistanceAtomToLigand.containsKey(atomNeighbor)) {
                                continue;
                            }
                            float distanceToLigand = mapMinDistanceAtomToLigand.get(atomNeighbor);
                            if (distanceToLigand < algoParameters.getDISTANCE_FROM_PEPTIDE_TO_WHICH_INTERACTINGPROTEIN_IS_CONSIDERED()) {
                                countHydrophobicAtom += 1;
                            }
                        }
                    }
                }
            }
        }
        return countHydrophobicAtom;
    }


    private boolean isMyAtomHydrophobic(MyAtomIfc myAtom) {

        Float hydrophobicity = AtomProperties.findHydrophobicityForMyAtom(myAtom);
        if (hydrophobicity != null && hydrophobicity > 0.9f) {
            return true;
        }
        return false;
    }


    private float searchAtomClosest(float[] atPosition) {

        float minDistance = Float.MAX_VALUE;

        for (MyChainIfc chain : structureShape.getAllChainsRelevantForShapeBuilding()) {
            for (MyMonomerIfc monomer : chain.getMyMonomers()) {
                for (MyAtomIfc atom : monomer.getMyAtoms()) {
                    float[] atomPosition = atom.getCoords();
                    float distance = ToolsMath.computeDistance(atomPosition, atPosition);

                    if (distance < minDistance) {
                        minDistance = distance;
                        this.atomClosest = atom;
                    }
                }
            }
        }
        return minDistance;
    }


    private Float findDehydronForMyAtom(MyAtomIfc atomClosest) {

        for (HBondDefinedWithAtoms dehydron : dehydrons) {

            // check if closest atom is part of this dehydron
            MyAtomIfc atom1 = dehydron.getMyAtomAcceptor();
            MyAtomIfc atom2 = dehydron.getMyAtomDonor();
            MyAtomIfc atom3 = dehydron.getMyAtomHydrogen();

            if ((atomClosest == atom1) || (atomClosest == atom2) || (atomClosest == atom3)) {
                return 1.0f;
            }
        }

        return 0.0f;
    }


    private boolean checkIfDonnorOrAcceptorAtomArePointingTowardsLigand(MyAtomIfc atomClosest) {

        if (!mapMinDistanceAtomToLigand.containsKey(atomClosest)) {
            System.out.println("!mapMinDistanceAtomToLigand.containsKey(atomClosest)");
        }
        float distToBeTheSortest = mapMinDistanceAtomToLigand.get(atomClosest);
        MyBondIfc[] bonds = atomClosest.getBonds();
        for (MyBondIfc bond : bonds) {
            MyAtomIfc bondedAtom = bond.getBondedAtom();
            if (!Arrays.equals(bondedAtom.getElement(), "H".toCharArray())) { // OH is a donnor but an acceptor as well
                if (!mapMinDistanceAtomToLigand.containsKey(bondedAtom)) {
                    return false; // cant say as it is out of shape so I say not good
                }
                float distBondedAtom = mapMinDistanceAtomToLigand.get(bondedAtom);
                if (distBondedAtom < distToBeTheSortest) {
                    return false;
                }
            }
        }
        return true;
    }


    //------------------------
    // Getter and Setter
    //------------------------
    @Override
    public Float getCharge() {
        return charge;
    }

    @Override
    public Float getHydrophobicity() {
        return hydrophobicity;
    }

    @Override
    public Float gethAcceptor() {
        return hAcceptor;
    }

    @Override
    public Float gethDonnor() {
        return hDonnor;
    }

    @Override
    public Float getDehydron() {
        return dehydron;
    }

    @Override
    public Float getAromaticRing() {
        return aromaticring;
    }

    public Map<MyAtomIfc, Float> getMapMinDistanceAtomToLigand() {
        return mapMinDistanceAtomToLigand;
    }
}