/*
Author:
      Fabrice Moriaud <fmoriaud@ultimatepdb.org>

  Copyright (c) 2016 Fabrice Moriaud

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
  */
package shapeBuilder;

import fingerprint.ShapeFingerprint;
import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import math.MathTools;
import math.ToolsDistance;
import multithread.ComputeLennardJonesRecursiveTask;
import multithread.ComputeShapePointsMultiThread;
import mystructure.AtomProperties.AtomHAcceptorDescriptors;
import mystructure.AtomProperties.AtomHDonnorDescriptors;
import mystructure.*;
import parameters.AlgoParameters;
import parameters.QueryAtomDefinedByIds;
import pointWithProperties.*;
import shape.*;
import shapeReduction.*;
import ultiJmol1462.Protonate;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ForkJoinPool;

public class ShapeBuilder {
    //-------------------------------------------------------------
    // Class variables
    //-------------------------------------------------------------
    private final MyStructureIfc myStructureGlobalBrut;
    private EnumShapeReductor enumShapeReductor;

    private boolean debug = true;
    private List<PointWithPropertiesIfc> listShrinkedShapePoints = new ArrayList<>();
    private AlgoParameters algoParameters;
    private String pdbFileHash;

    //-------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------
    public ShapeBuilder(MyStructureIfc myStructureGlobal, AlgoParameters algoParameters, EnumShapeReductor enumShapeReductor, String pdbFileHash) {

        this.myStructureGlobalBrut = myStructureGlobal;
        this.algoParameters = algoParameters;
        this.enumShapeReductor = enumShapeReductor;
        this.algoParameters = algoParameters;
        this.pdbFileHash = pdbFileHash;
    }


    //-------------------------------------------------------------
    // Interface & Public methods
    //-------------------------------------------------------------
    public ShapeContainerWithPeptide getShapeAroundAChain(char[] chainId) throws ShapeBuildingException { // whole chain query

        StructureLocalToBuildAnyShape structureLocalToBuildAnyShape = new StructureLocalToBuildAnyShape(myStructureGlobalBrut, chainId, algoParameters);
        MyStructureIfc myStructureLocal = structureLocalToBuildAnyShape.getMyStructureLocal();
        if (myStructureLocal == null) {
            return null;
        }

        MyChainIfc ligand = structureLocalToBuildAnyShape.getLigand();

        Cloner cloner = new Cloner(ligand, algoParameters);
        MyStructureIfc myStructureLigand = cloner.getClone();

        Protonate protonate = new Protonate(myStructureLigand, algoParameters);
        try {
            protonate.compute();
        } catch (ExceptionInScoringUsingBioJavaJMolGUI exceptionInScoringUsingBioJavaJMolGUI) {
            ShapeBuildingException exception = new ShapeBuildingException("Protonate myStructureLigand in getShapeAroundAChain failed");
            throw exception;
        }
        MyStructureIfc protonatedLigand = protonate.getProtonatedMyStructure();

        Protonate protonate2 = new Protonate(myStructureLocal, algoParameters);
        try {
            protonate2.compute();
        } catch (ExceptionInScoringUsingBioJavaJMolGUI exceptionInScoringUsingBioJavaJMolGUI) {
            ShapeBuildingException exception = new ShapeBuildingException("Protonate myStructureLocal in getShapeAroundAChain failed");
            throw exception;
        }
        MyStructureIfc myStructureLocalProtonated = protonate2.getProtonatedMyStructure();

        List<PointIfc> listOfPointsFromChainLigand = MyStructureTools.makeQueryPointsFromMyChainIfc(ligand);
        Box box = makeBoxOutOfLocalStructure(myStructureLocalProtonated);
        CollectionOfPointsWithPropertiesIfc shapeCollectionPoints = computeShape(listOfPointsFromChainLigand, myStructureLocalProtonated, box, algoParameters);

        ShapeContainerWithPeptide shapeContainerWithPeptide = buildShapeContainerWithPeptide(myStructureLocalProtonated, listOfPointsFromChainLigand, algoParameters, shapeCollectionPoints, protonatedLigand, 0, structureLocalToBuildAnyShape.getMonomerToDiscard());
        shapeContainerWithPeptide.setFourLetterCode(myStructureGlobalBrut.getFourLetterCode());
        return shapeContainerWithPeptide;
    }


    public ShapeContainerWithPeptide getShapeAroundASegmentOfChainUsingStartingMyMonomerPositionInChain(char[] chainId, int startingRankId, int peptideLength) throws ShapeBuildingException { // part chain query

        StructureLocalToBuildAnyShape structureLocalToBuildAnyShape = new StructureLocalToBuildAnyShape(myStructureGlobalBrut, chainId, startingRankId, peptideLength, algoParameters);
        MyStructureIfc myStructureLocal = structureLocalToBuildAnyShape.getMyStructureLocal();
        if (myStructureLocal == null) {
            return null;
        }

        Protonate protonate = new Protonate(myStructureLocal, algoParameters);
        try {
            protonate.compute();
        } catch (ExceptionInScoringUsingBioJavaJMolGUI exceptionInScoringUsingBioJavaJMolGUI) {
            ShapeBuildingException exception = new ShapeBuildingException("Protonate myStructureLocal in getShapeAroundAChain failed");
            throw exception;
        }
        MyStructureIfc myStructureLocalProtonated = protonate.getProtonatedMyStructure();


        MyChainIfc ligand = structureLocalToBuildAnyShape.getLigand();
        Cloner cloner = new Cloner(ligand, algoParameters);
        MyStructureIfc myStructureLigand = cloner.getClone();

        Protonate protonate2 = new Protonate(myStructureLigand, algoParameters);
        try {
            protonate2.compute();
        } catch (ExceptionInScoringUsingBioJavaJMolGUI exceptionInScoringUsingBioJavaJMolGUI) {
            ShapeBuildingException exception = new ShapeBuildingException("Protonate myStructureLigand in getShapeAroundAChain failed");
            throw exception;
        }
        MyStructureIfc protonatedLigand = protonate2.getProtonatedMyStructure();

        List<PointIfc> listOfPointsFromChainLigand = MyStructureTools.makeQueryPointsFromMyStructureIfc(protonatedLigand);

        Box box = makeBoxOutOfLocalStructure(myStructureLocalProtonated);
        CollectionOfPointsWithPropertiesIfc shapeCollectionPoints = computeShape(listOfPointsFromChainLigand, myStructureLocalProtonated, box, algoParameters);

        ShapeContainerWithPeptide shapeContainerWithPeptide = buildShapeContainerWithPeptide(myStructureLocalProtonated, listOfPointsFromChainLigand, algoParameters, shapeCollectionPoints, protonatedLigand, startingRankId, structureLocalToBuildAnyShape.getMonomerToDiscard());
        shapeContainerWithPeptide.setFourLetterCode(myStructureGlobalBrut.getFourLetterCode());
        return shapeContainerWithPeptide;
    }


    public ShapeContainerWithLigand getShapeAroundAHetAtomLigand(char[] hetAtomsLigandId, int occurrenceId) throws ShapeBuildingException {

        StructureLocalToBuildAnyShape structureLocalToBuildAnyShape = new StructureLocalToBuildAnyShape(myStructureGlobalBrut, hetAtomsLigandId, occurrenceId, algoParameters);
        MyStructureIfc myStructureLocal = structureLocalToBuildAnyShape.getMyStructureLocal();
        if (myStructureLocal == null) {
            return null;
        }

        MyMonomerIfc hetAtomsGroup = structureLocalToBuildAnyShape.getLigand().getMyMonomers()[0];
        Cloner cloner = new Cloner(hetAtomsGroup, algoParameters);
        MyStructureIfc myStructureLigand = cloner.getClone();

        Protonate protonate = new Protonate(myStructureLigand, algoParameters);
        try {
            protonate.compute();
        } catch (ExceptionInScoringUsingBioJavaJMolGUI exceptionInScoringUsingBioJavaJMolGUI) {
            ShapeBuildingException exception = new ShapeBuildingException("Protonate myStructureLigand in getShapeAroundAChain failed");
            throw exception;
        }
        MyStructureIfc protonatedLigand = protonate.getProtonatedMyStructure();

        Protonate protonate2 = new Protonate(myStructureLocal, algoParameters);
        try {
            protonate2.compute();
        } catch (ExceptionInScoringUsingBioJavaJMolGUI exceptionInScoringUsingBioJavaJMolGUI) {
            ShapeBuildingException exception = new ShapeBuildingException("Protonate myStructureLocal in getShapeAroundAChain failed");
            throw exception;
        }
        MyStructureIfc myStructureLocalProtonated = protonate2.getProtonatedMyStructure();

        List<PointIfc> listOfPointsFromChainLigand = MyStructureTools.makeQueryPointsFromMyStructureIfc(protonatedLigand);
        Box box = makeBoxOutOfLocalStructure(myStructureLocalProtonated);
        CollectionOfPointsWithPropertiesIfc shapeCollectionPoints = computeShape(listOfPointsFromChainLigand, myStructureLocalProtonated, box, algoParameters);

        ShapeContainerWithLigand shapeContainerWithLigand = buildShapeContainerWithLigand(myStructureLocalProtonated, listOfPointsFromChainLigand, algoParameters, shapeCollectionPoints, protonatedLigand, occurrenceId, structureLocalToBuildAnyShape.getMonomerToDiscard());
        shapeContainerWithLigand.setFourLetterCode(myStructureGlobalBrut.getFourLetterCode());
        return shapeContainerWithLigand;
    }


    public ShapeContainerAtomIdsWithinShapeWithPeptide getShapeAroundAtomDefinedByIds(List<QueryAtomDefinedByIds> listAtomDefinedByIds, List<String> chainToIgnore) throws ShapeBuildingException { // LennardJones query


        StructureLocalToBuildAnyShape structureLocalToBuildAnyShape = new StructureLocalToBuildAnyShape(myStructureGlobalBrut, listAtomDefinedByIds, algoParameters, chainToIgnore);
        MyStructureIfc myStructureLocal = structureLocalToBuildAnyShape.getMyStructureLocal();
        if (myStructureLocal == null) {
            return null;
        }

        Protonate protonate = new Protonate(myStructureLocal, algoParameters);
        try {
            protonate.compute();
        } catch (ExceptionInScoringUsingBioJavaJMolGUI exceptionInScoringUsingBioJavaJMolGUI) {
            ShapeBuildingException exception = new ShapeBuildingException("Protonate myStructureLocal in getShapeAroundAChain failed");
            throw exception;
        }
        MyStructureIfc myStructureLocalProtonated = protonate.getProtonatedMyStructure();


        Box box = makeBoxOutOfLocalStructure(myStructureLocalProtonated);


        List<PointIfc> listOfPointsFromChainLigandFromLennarJones = computeListOfPointsWithLennardJones(box, myStructureLocal, listAtomDefinedByIds);

        CollectionOfPointsWithPropertiesIfc shapeCollectionPoints = computeShape(listOfPointsFromChainLigandFromLennarJones, myStructureLocal, box, algoParameters);
        ShapeContainerAtomIdsWithinShapeWithPeptide shapeContainerAtomIdsWithinShapeWithPeptide = buildShapeContainerFromAtomIdsWithinShape(myStructureLocal, listOfPointsFromChainLigandFromLennarJones, algoParameters, shapeCollectionPoints, listAtomDefinedByIds, structureLocalToBuildAnyShape.getMonomerToDiscard());

        shapeContainerAtomIdsWithinShapeWithPeptide.setFourLetterCode(myStructureGlobalBrut.getFourLetterCode());
        return shapeContainerAtomIdsWithinShapeWithPeptide;
    }


    /**
     * Assumes myStructureGlobalBrut is already protonated
     *
     * @param foreignMonomerToExclude
     * @param rotatedLigandOrPeptide
     * @return
     */
    public ShapeContainerIfc getShapeAroundForeignLigand(List<MyMonomerIfc> foreignMonomerToExclude, MyStructureIfc rotatedLigandOrPeptide) {

        StructureLocalToBuildAnyShape structureLocalToBuildAnyShape = null;
        try {
            structureLocalToBuildAnyShape = new StructureLocalToBuildAnyShape(myStructureGlobalBrut, foreignMonomerToExclude, rotatedLigandOrPeptide, algoParameters);
        } catch (ShapeBuildingException e) {
            e.printStackTrace();
        }
        MyStructureIfc myStructureLocal = structureLocalToBuildAnyShape.getMyStructureLocal();

        List<PointIfc> listOfPointsFromChainLigand = MyStructureTools.makeQueryPointsFromMyStructureIfc(rotatedLigandOrPeptide);
        Box box = makeBoxOutOfLocalStructure(myStructureLocal);
        CollectionOfPointsWithPropertiesIfc shapeCollectionPoints = computeShape(listOfPointsFromChainLigand, myStructureLocal, box, algoParameters);

        ShapeContainerWithPeptide shapeContainerWithPeptide = buildShapeContainerWithPeptide(myStructureLocal, listOfPointsFromChainLigand, algoParameters, shapeCollectionPoints, rotatedLigandOrPeptide, 0, structureLocalToBuildAnyShape.getMonomerToDiscard());
        shapeContainerWithPeptide.setFourLetterCode(myStructureGlobalBrut.getFourLetterCode());
        return shapeContainerWithPeptide;
    }


    //-------------------------------------------------------------
    // Implementation
    //-------------------------------------------------------------
    private List<PointIfc> computeListOfPointsWithLennardJones(Box box, MyStructureIfc localMyStructure, List<QueryAtomDefinedByIds> listQueryAtomDefinedByIds) {

        List<PointIfc> listOfPointsWithLennardJones = new ArrayList<>();
        List<PointIfc> listCoordsCenterQuery = findMyPointAtomContainingAtomsDefinedByIds(myStructureGlobalBrut, listQueryAtomDefinedByIds);

        // Buils a List Of point
        List<float[]> listPositions = new ArrayList<>();

        for (int i = 0; i < box.getCountOfPointsInXDirection(); i++) {
            for (int j = 0; j < box.getCountOfPointsInYDirection(); j++) {
                for (int k = 0; k < box.getCountOfPointsInZDirection(); k++) {

                    float[] atPosition = new float[3];
                    atPosition[0] = box.getMinX() + (float) i * algoParameters.getCELL_DIMENSION_OF_THE_PROBABILITY_MAP_ANGSTROM();
                    atPosition[1] = box.getMinY() + (float) j * algoParameters.getCELL_DIMENSION_OF_THE_PROBABILITY_MAP_ANGSTROM();
                    atPosition[2] = box.getMinZ() + (float) k * algoParameters.getCELL_DIMENSION_OF_THE_PROBABILITY_MAP_ANGSTROM();

                    double minDistance = 1.0;
                    double maxDistance = algoParameters.getDISTANCE_FROM_PEPTIDE_TO_WHICH_INTERACTINGPROTEIN_IS_CONSIDERED();

                    double minDistanceOfThisPoint = Double.MAX_VALUE;
                    for (MyChainIfc chain : localMyStructure.getAllChainsRelevantForShapeBuilding()) {
                        for (MyMonomerIfc monomer : chain.getMyMonomers()) {
                            for (MyAtomIfc atom : monomer.getMyAtoms()) {
                                double distance = MathTools.computeDistance(atom.getCoords(), atPosition);
                                if (distance < minDistanceOfThisPoint) {
                                    minDistanceOfThisPoint = distance;
                                }
                            }
                        }
                    }

                    if ((minDistanceOfThisPoint > minDistance) && (minDistanceOfThisPoint < maxDistance)) {
                        listPositions.add(atPosition);
                    }
                }
            }
        }

        // Multithreaded computation of LJ grid
        int countOfSubpacket = algoParameters.getSUB_THREAD_COUNT_FORK_AND_JOIN();
        ForkJoinPool pool = new ForkJoinPool();
        ComputeLennardJonesRecursiveTask computeLennardJonesMultiThread = new ComputeLennardJonesRecursiveTask(listPositions, 0, listPositions.size() - 1, listPositions.size() / countOfSubpacket, localMyStructure, algoParameters);
        listOfPointsWithLennardJones = pool.invoke(computeLennardJonesMultiThread);
        pool.shutdownNow();

        List<PointIfc> listOfPointsWithLennardJonesQuery = new ArrayList<>();

        // Extract a sub part of the LN points

        List<float[]> listV2 = new ArrayList<>();
        for (PointIfc coordQuery : listCoordsCenterQuery) {
            listV2.add(coordQuery.getCoords());
        }
        for (QueryAtomDefinedByIds queryAtomDefinedByIds : listQueryAtomDefinedByIds) {

            for (PointIfc pointsWithLennardJones : listOfPointsWithLennardJones) {

                float[] coordsLNPoint = pointsWithLennardJones.getCoords();
                float distance = MathTools.computeDistance(coordsLNPoint, listV2);

                if (distance < queryAtomDefinedByIds.getRadiusForQueryAtomsDefinedByIds()) {
                    listOfPointsWithLennardJonesQuery.add(pointsWithLennardJones);
                }
            }
        }

        return listOfPointsWithLennardJonesQuery;
    }


    private List<PointIfc> findMyPointAtomContainingAtomsDefinedByIds(MyStructureIfc myStructure, List<QueryAtomDefinedByIds> atomsDefinedByIds) {

        List<PointIfc> pointsCorrespondingToAtomsDefinedById = new ArrayList<>();

        for (QueryAtomDefinedByIds atomDefinedByIds : atomsDefinedByIds) {

            MyAtomIfc foundMyAtom = null;
            char[] chainIdToFind = atomDefinedByIds.getChainQuery().toCharArray();
            MyChainIfc[] chains = myStructure.getAllChainsRelevantForShapeBuilding();
            A:
            for (MyChainIfc foundMyChain : chains) {
                if (Arrays.equals(foundMyChain.getChainId(), chainIdToFind)) {
                    int residueIdToFind = atomDefinedByIds.getResidueId();
                    MyMonomerIfc foundMyMonomer = foundMyChain.getMyMonomerFromResidueId(residueIdToFind);
                    char[] atomNameToFind = atomDefinedByIds.getAtomName().toCharArray();
                    foundMyAtom = foundMyMonomer.getMyAtomFromMyAtomName(atomNameToFind);
                    break A;
                }
            }

            if (foundMyAtom == null) {
                System.out.println("atom not found : " + String.valueOf(foundMyAtom.getAtomName()));
                continue;
            }
            PointIfc pointAtom = new Point(foundMyAtom.getCoords());
            pointsCorrespondingToAtomsDefinedById.add(pointAtom);
        }
        return pointsCorrespondingToAtomsDefinedById;
    }


    public Box makeBoxOutOfLocalStructure(MyStructureIfc myStructure) {

        Box box = new Box(myStructure, algoParameters);
        return box;
    }


    //-------------------------------------------------------------
    // Implementation
    //-------------------------------------------------------------
    private CollectionOfPointsWithPropertiesIfc computeShape(List<PointIfc> listOfLigandPoints, MyStructureIfc myStructureShape, Box box, AlgoParameters algoParameters) {

        double maxDistanceBetweenGridPointAndLigand = algoParameters.getDISTANCE_FROM_PEPTIDE_TO_WHICH_INTERACTINGPROTEIN_IS_CONSIDERED();

        List<HBondDefinedWithAtoms> hBonds = intraStructureHBondDetector(myStructureShape);
        List<HBondDefinedWithAtoms> dehydrons = buildDehydrons(hBonds);

        List<float[]> listPositionWhereToComputeProperties = new ArrayList<>();
        List<Float> listMinDistanceOfThisGridPointToAnyAtomOfPeptide = new ArrayList<>();

        for (int i = 0; i < box.getCountOfPointsInXDirection(); i++) {
            for (int j = 0; j < box.getCountOfPointsInYDirection(); j++) {
                for (int k = 0; k < box.getCountOfPointsInZDirection(); k++) {

                    float[] atPosition = new float[3];
                    atPosition[0] = box.getMinX() + (float) i * algoParameters.getCELL_DIMENSION_OF_THE_PROBABILITY_MAP_ANGSTROM();
                    atPosition[1] = box.getMinY() + (float) j * algoParameters.getCELL_DIMENSION_OF_THE_PROBABILITY_MAP_ANGSTROM();
                    atPosition[2] = box.getMinZ() + (float) k * algoParameters.getCELL_DIMENSION_OF_THE_PROBABILITY_MAP_ANGSTROM();

                    float minDistanceOfThisGridPointToAnyAtomOfPeptide = ToolsDistance.computeSmallestDistanceBetweenAPointAndListOfPoints(atPosition, listOfLigandPoints);

                    if (minDistanceOfThisGridPointToAnyAtomOfPeptide < maxDistanceBetweenGridPointAndLigand) {
                        listPositionWhereToComputeProperties.add(atPosition);
                        listMinDistanceOfThisGridPointToAnyAtomOfPeptide.add(minDistanceOfThisGridPointToAnyAtomOfPeptide);
                    }
                }
            }
        }

        int countOfSubpacket = algoParameters.getSUB_THREAD_COUNT_FORK_AND_JOIN();
        int threshold = listPositionWhereToComputeProperties.size() / countOfSubpacket + 1;
        if (threshold < 2) {
            threshold = 2;
        }
        ForkJoinPool pool = new ForkJoinPool();

        ComputeShapePointsMultiThread computeShapePointsMultiThread = new ComputeShapePointsMultiThread(listPositionWhereToComputeProperties,
                0, listPositionWhereToComputeProperties.size() - 1, threshold, myStructureShape, listOfLigandPoints, algoParameters, dehydrons);


        listShrinkedShapePoints = pool.invoke(computeShapePointsMultiThread);
        pool.shutdownNow();

        CollectionOfPointsWithPropertiesIfc collectionOfPointsWithProperties = new CollectionOfPointsWithProperties(listShrinkedShapePoints);

        return collectionOfPointsWithProperties;
    }


    private List<HBondDefinedWithAtoms> buildDehydrons(List<HBondDefinedWithAtoms> hBonds) {

        List<HBondDefinedWithAtoms> dehydrons = new ArrayList<>();

        for (HBondDefinedWithAtoms hbond : hBonds) {

            int countHydrophobicAtom = ShapeBuildingTools.getCountOfHydrophobicAtomsInTheNeighborhoodOfMyAtomForDehydronsUseOnly(hbond.getMyAtomHydrogen(), algoParameters);
            if (countHydrophobicAtom <= algoParameters.getDEHYDRON_CUTOFF_COUNT_OF_HYDROPHOBIC_ATOM_SURRONDING_HBOND()) {
                dehydrons.add(hbond);
            }
        }
        return dehydrons;
    }


    private ShapeContainerAtomIdsWithinShapeWithPeptide buildShapeContainerFromAtomIdsWithinShape(MyStructureIfc myStructureShape, List<PointIfc> listOfPointsFromChainLigand, AlgoParameters algoParameters, CollectionOfPointsWithPropertiesIfc shapeCollectionPoints, List<QueryAtomDefinedByIds> listAtomDefinedByIds, List<MyMonomerIfc> monomerToDiscard) {

        CollectionOfPointsWithPropertiesIfc shrinkedShapeBasedOnDistanceToLigand = simplifyShape(algoParameters, shapeCollectionPoints);

        ShapeContainerAtomIdsWithinShapeWithPeptide shape = new ShapeContainerAtomIdsWithinShapeWithPeptide(listAtomDefinedByIds, shrinkedShapeBasedOnDistanceToLigand, listOfPointsFromChainLigand, myStructureShape, monomerToDiscard, myStructureShape.getPdbFileHash());

        prepareShapeContainer(listOfPointsFromChainLigand, algoParameters, shrinkedShapeBasedOnDistanceToLigand, shape);
        return shape;
    }


    private ShapeContainerWithPeptide buildShapeContainerWithPeptide(MyStructureIfc myStructureShape, List<PointIfc> listOfPointsFromChainLigand, AlgoParameters algoParameters, CollectionOfPointsWithPropertiesIfc shapeCollectionPoints, MyStructureIfc peptide, int startingIndex, List<MyMonomerIfc> monomerToDiscard) {

        CollectionOfPointsWithPropertiesIfc shrinkedShapeBasedOnDistanceToLigand = simplifyShape(algoParameters, shapeCollectionPoints);

        ShapeContainerWithPeptide shape = new ShapeContainerWithPeptide(shrinkedShapeBasedOnDistanceToLigand, listOfPointsFromChainLigand, myStructureShape, monomerToDiscard, peptide, startingIndex, pdbFileHash);

        prepareShapeContainer(listOfPointsFromChainLigand, algoParameters, shrinkedShapeBasedOnDistanceToLigand, shape);
        return shape;
    }


    private ShapeContainerWithLigand buildShapeContainerWithLigand(MyStructureIfc myStructureShape, List<PointIfc> listOfPointsFromChainLigand, AlgoParameters algoParameters, CollectionOfPointsWithPropertiesIfc shapeCollectionPoints, MyStructureIfc myMonomer, int occurenceId, List<MyMonomerIfc> monomerToDiscard) {

        CollectionOfPointsWithPropertiesIfc shrinkedShapeBasedOnDistanceToLigand = simplifyShape(algoParameters, shapeCollectionPoints);

        ShapeContainerWithLigand shape = new ShapeContainerWithLigand(shrinkedShapeBasedOnDistanceToLigand, listOfPointsFromChainLigand, myStructureShape, monomerToDiscard, algoParameters, myMonomer, occurenceId, pdbFileHash);

        prepareShapeContainer(listOfPointsFromChainLigand, algoParameters, shrinkedShapeBasedOnDistanceToLigand, shape);
        return shape;
    }


    private void prepareShapeContainer(List<PointIfc> listOfPointsFromChainLigand, AlgoParameters algoParameters, CollectionOfPointsWithPropertiesIfc shrinkedShapeBasedOnDistanceToLigand, ShapeContainer shape) {

        Map<Integer, PointWithPropertiesIfc> shrinkedMiniShape = buildMinishape(listOfPointsFromChainLigand, algoParameters, shrinkedShapeBasedOnDistanceToLigand);
        shape.setMiniShape(shrinkedMiniShape);

        List<TriangleInteger> listTriangleOfPointsFromMinishape = computeListTriangleOfPointsFromMinishape(shrinkedMiniShape, algoParameters);
        shape.setListTriangleOfPointsFromMinishape(listTriangleOfPointsFromMinishape);

        ShapeFingerprint shapeFingerprint = new ShapeFingerprint(shrinkedMiniShape);
        shapeFingerprint.compute();
        List<Integer> histogramStrikingProperties = shapeFingerprint.getHistogramStrikingProperties();
        shape.setHistogramStrikingProperties(histogramStrikingProperties);
        List<Integer> histogramD2 = shapeFingerprint.getHistogramD2();
        shape.setHistogramD2(histogramD2);

        if (debug == true) {
            // check integrity
            // minishape points should have the same id as in shape
            for (Entry<Integer, PointWithPropertiesIfc> entry : shrinkedMiniShape.entrySet()) {

                int pointIdInMinishape = entry.getKey();
                PointWithPropertiesIfc pointWithPropertiesFromMinishape = entry.getValue();
                PointWithPropertiesIfc pointWithPropertiesFromShape = shrinkedShapeBasedOnDistanceToLigand.getPointFromId(pointIdInMinishape);
                if (pointWithPropertiesFromMinishape != pointWithPropertiesFromShape) {
                    System.out.println("problem pointIdInMinishape = " + pointIdInMinishape + "  points in shape = " + shrinkedShapeBasedOnDistanceToLigand.getSize());
                }
            }
        }
    }


    private List<TriangleInteger> computeListTriangleOfPointsFromMinishape(Map<Integer, PointWithPropertiesIfc> miniShape, AlgoParameters algoParameters) {

        GenerateTriangles generateTriangles = new GenerateTriangles(miniShape, algoParameters);
        List<TriangleInteger> listTriangleOfPointsFromMinishape = generateTriangles.generateTriangles();

        Set<TriangleInteger> treesetOfTriangle = new HashSet<>();
        for (TriangleInteger triangleInteger : listTriangleOfPointsFromMinishape) {
            treesetOfTriangle.add(triangleInteger);
        }
        List<TriangleInteger> listTriangleInteger = new ArrayList<>();
        for (TriangleInteger triangleInteger : treesetOfTriangle) {
            listTriangleInteger.add(triangleInteger);
        }
        listTriangleOfPointsFromMinishape = listTriangleInteger;
        return listTriangleOfPointsFromMinishape;
    }


    private Map<Integer, PointWithPropertiesIfc> buildMinishape(List<PointIfc> listOfPointsFromChainLigand, AlgoParameters algoParameters, CollectionOfPointsWithPropertiesIfc shapeCollectionPoints) {

        Map<Integer, PointWithPropertiesIfc> miniShape = null;
        if (enumShapeReductor == EnumShapeReductor.CLUSTERING) {
            ShapeReductorIfc shapeReductor = new ShapeReductorByClustering(shapeCollectionPoints, algoParameters);
            miniShape = shapeReductor.computeReducedCollectionOfPointsWithProperties();
        }
        if (enumShapeReductor == EnumShapeReductor.SELECTING) {
            ShapeReductorIfc shapeReductor = new ShapeReductorBySelectingPoints(shapeCollectionPoints, algoParameters);
            miniShape = shapeReductor.computeReducedCollectionOfPointsWithProperties();
        }
        return miniShape;
    }


    private CollectionOfPointsWithPropertiesIfc simplifyShape(AlgoParameters algoParameters, CollectionOfPointsWithPropertiesIfc shapeCollectionPoints) {

        CollectionOfPointsWithPropertiesIfc shrinkedShapeForNONEPoint = removePointsOfStrikingPropertiesNoneIfCloseEnoughToAnotherPointWithAnyStrikingPropertiesNotNone(shapeCollectionPoints);

        CollectionOfPointsWithPropertiesIfc shrinkedShapeBasedOnDistanceToLigand = shrinkShapeAccordingToFinalMaxDistanceToLigand(shrinkedShapeForNONEPoint, algoParameters);
        return shrinkedShapeBasedOnDistanceToLigand;
    }


    private CollectionOfPointsWithPropertiesIfc shrinkShapeAccordingToFinalMaxDistanceToLigand(CollectionOfPointsWithPropertiesIfc shape, AlgoParameters algoParameters) {

        listShrinkedShapePoints.clear();
        for (int i = 0; i < shape.getSize(); i++) {
            PointWithPropertiesIfc pointWithtProperties = shape.getPointFromId(i);
            double distanceToLigand = pointWithtProperties.getDistanceToLigand();
            if (distanceToLigand < algoParameters.getDISTANCE_FROM_PEPTIDE_TO_WHICH_INTERACTINGPROTEIN_IS_SHORTENED()) {
                listShrinkedShapePoints.add(pointWithtProperties);
            }
        }

        CollectionOfPointsWithPropertiesIfc shrinkedShape = new CollectionOfPointsWithProperties(listShrinkedShapePoints);
        return shrinkedShape;
    }


    private CollectionOfPointsWithPropertiesIfc removePointsOfStrikingPropertiesNoneIfCloseEnoughToAnotherPointWithAnyStrikingPropertiesNotNone(CollectionOfPointsWithPropertiesIfc shape) {

        double threshold = algoParameters.getTHRESHOLD_DISTANCE_TO_KEEP_NEIGHBORING_NONE_STRIKING_PROPERTY();
        double distance;
        Set<Integer> setSomeNonePointsToRemove = new HashSet<>();

        A:
        for (int i = 0; i < shape.getSize(); i++) {

            List<StrikingProperties> listStrikingPropertiesForThisPoint = shape.getPointFromId(i).getStrikingProperties();
            if ((listStrikingPropertiesForThisPoint.size() == 1) && listStrikingPropertiesForThisPoint.get(0).equals(StrikingProperties.NONE)) {

                for (int j = 0; j < shape.getSize(); j++) {
                    if (i == j) {
                        continue;
                    }
                    List<StrikingProperties> listStrikingPropertiesForThisNeighBohrpoint = shape.getPointFromId(j).getStrikingProperties();
                    if ((listStrikingPropertiesForThisNeighBohrpoint.size() != 0) && (!listStrikingPropertiesForThisNeighBohrpoint.contains(StrikingProperties.NONE))) {

                        distance = MathTools.computeDistance(shape.getPointFromId(i).getCoords().getCoords(), shape.getPointFromId(j).getCoords().getCoords());
                        if (distance < threshold) {
                            setSomeNonePointsToRemove.add(i);
                            continue A;
                        }
                    }
                }
            }
        }
        List<PointWithPropertiesIfc> newList = new ArrayList<>();
        for (int i = 0; i < shape.getSize(); i++) {
            if (!setSomeNonePointsToRemove.contains(i)) {
                newList.add(shape.getPointFromId(i));
            }
        }

        CollectionOfPointsWithPropertiesIfc newShape = new CollectionOfPointsWithProperties(newList);
        return newShape;
    }


    private List<HBondDefinedWithAtoms> intraStructureHBondDetector(MyStructureIfc myStructure) {

        List<MyAtomIfc> donnors = identifyDonnorsBasedOnAtomNameDefinition(myStructure);
        List<MyAtomIfc> acceptors = identifyAcceptorsBasedOnAtomNameDefinition(myStructure);

        List<HBondDefinedWithAtoms> hbonds = identifyHbonds(donnors, acceptors);

        return hbonds;
    }


    private List<HBondDefinedWithAtoms> identifyHbonds(List<MyAtomIfc> donnors, List<MyAtomIfc> acceptors) {

        float thresholdDistanceHydrogenToAcceptor = 1.9f;

        List<HBondDefinedWithAtoms> hbonds = new ArrayList<>();
        for (MyAtomIfc donnor : donnors) {
            if (donnor.getBonds() != null) {
                for (MyBondIfc bond : donnor.getBonds()) {
                    MyAtomIfc bondedAtom = bond.getBondedAtom();
                    if (String.valueOf(bondedAtom.getElement()).equals("H")) {
                        for (MyAtomIfc acceptor : acceptors) {
                            identifyHbonds(thresholdDistanceHydrogenToAcceptor, hbonds, donnor, acceptor, bondedAtom);
                        }
                    }
                }
            }
        }
        return hbonds;
    }


    private void identifyHbonds(float thresholdDistanceHydrogenToAcceptor, List<HBondDefinedWithAtoms> hbonds, MyAtomIfc donnor, MyAtomIfc acceptor, MyAtomIfc hydrogen) {

        if (donnor.getParent() != acceptor.getParent()) { // Intra skipped because of Tyr OH to its own O
            float distanceBetweenHydrogenAndAcceptor = MathTools.computeDistance(acceptor.getCoords(), hydrogen.getCoords());
            if (distanceBetweenHydrogenAndAcceptor < thresholdDistanceHydrogenToAcceptor) {
                //PairOfMyAtomWithMyMonomerAndMychainReferences pairOfMyAtom = new PairOfMyAtomWithMyMonomerAndMychainReferences(donnor.getMyAtom(), acceptor.getMyAtom(), donnor.getMyMonomer(), acceptor.getMyMonomer());
                HBondDefinedWithAtoms hBondDefinedWithAtoms = new HBondDefinedWithAtoms(donnor, acceptor, hydrogen);
                hbonds.add(hBondDefinedWithAtoms);
            }
        }
    }


    private List<MyAtomIfc> identifyDonnorsBasedOnAtomNameDefinition(MyStructureIfc myStructure) {
        List<MyAtomIfc> donnors = new ArrayList<>();

        for (MyChainIfc chain : myStructure.getAllChainsRelevantForShapeBuilding()) {
            for (MyMonomerIfc monomer : chain.getMyMonomers()) {
                for (MyAtomIfc atom : monomer.getMyAtoms()) {
                    if (isAtomADonnor(monomer, atom) == true) {
                        donnors.add(atom);
                    }
                }
            }
        }
        return donnors;
    }


    private boolean isAtomADonnor(MyMonomerIfc monomer, MyAtomIfc atom) {

        for (AtomHDonnorDescriptors atomHDonnorDescriptors : AtomHDonnorDescriptors.values()) {
            if (isAtomHDonnorDescriptorsMatchMyAtom(atomHDonnorDescriptors, monomer, atom)) {
                return true;
            }
        }
        return false;
    }


    private boolean isAtomHDonnorDescriptorsMatchMyAtom(AtomHDonnorDescriptors atomHDonnorDescriptors, MyMonomerIfc monomer, MyAtomIfc atom) {

        double thresholdHbondDonnor = 0.1;

        if (!atomHDonnorDescriptors.getAtomName().equals(String.valueOf(atom.getAtomName()))) {
            return false;
        }

        if (!atomHDonnorDescriptors.getResidueName().equals(String.valueOf(monomer.getThreeLetterCode()))) {
            return false;
        }

        if (atomHDonnorDescriptors.getHdonnor() < thresholdHbondDonnor) {
            return false;
        }

        return true;
    }


    private List<MyAtomIfc> identifyAcceptorsBasedOnAtomNameDefinition(MyStructureIfc myStructure) {
        List<MyAtomIfc> acceptors = new ArrayList<>();

        for (MyChainIfc chain : myStructure.getAllChainsRelevantForShapeBuilding()) {
            for (MyMonomerIfc monomer : chain.getMyMonomers()) {
                for (MyAtomIfc atom : monomer.getMyAtoms()) {

                    if (isAtomAnAcceptor(monomer, atom) == true) {
                        acceptors.add(atom);
                    }
                }
            }
        }
        return acceptors;
    }


    private boolean isAtomAnAcceptor(MyMonomerIfc monomer, MyAtomIfc atom) {

        for (AtomHAcceptorDescriptors atomHAcceptorDescriptors : AtomHAcceptorDescriptors.values()) {
            if (isAtomHAcceptorDescriptorsMatchMyAtom(atomHAcceptorDescriptors, monomer, atom)) {
                return true;
            }
        }
        return false;
    }


    private boolean isAtomHAcceptorDescriptorsMatchMyAtom(AtomHAcceptorDescriptors atomHAcceptorDescriptors, MyMonomerIfc monomer, MyAtomIfc atom) {

        double thresholdHbondAcceptor = 0.1;

        if (!atomHAcceptorDescriptors.getAtomName().equals(String.valueOf(atom.getAtomName()))) {
            return false;
        }

        if (!atomHAcceptorDescriptors.getResidueName().equals(String.valueOf(monomer.getThreeLetterCode()))) {
            return false;
        }

        if (atomHAcceptorDescriptors.getHacceptor() < thresholdHbondAcceptor) {
            return false;
        }

        return true;
    }
}