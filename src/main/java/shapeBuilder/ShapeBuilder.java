package shapeBuilder;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ForkJoinPool;

import fingerprint.ShapeFingerprint;
import math.ToolsDistance;
import math.ToolsMath;
import multithread.ComputeLennardJonesRecursiveTask;
import multithread.ComputeShapePointsMultiThread;
import parameters.AlgoParameters;
import parameters.QueryAtomDefinedByIds;
import pointWithProperties.Box;
import pointWithProperties.CollectionOfPointsWithProperties;
import pointWithProperties.CollectionOfPointsWithPropertiesIfc;
import pointWithProperties.Enum.PropertyName;
import pointWithProperties.Point;
import pointWithProperties.PointIfc;
import pointWithProperties.PointWithPropertiesIfc;
import pointWithProperties.StrikingProperties;
import shape.ShapeContainer;
import shape.ShapeContainerAtomIdsWithinShapeWithPeptide;
import shape.ShapeContainerWithLigand;
import shape.ShapeContainerWithPeptide;
import shapeReduction.*;
import mystructure.AtomProperties.AtomHAcceptorDescriptors;
import mystructure.AtomProperties.AtomHDonnorDescriptors;
import mystructure.HBondDefinedWithAtoms;
import mystructure.MyAtomIfc;
import mystructure.MyBondIfc;
import mystructure.MyChain;
import mystructure.MyChainIfc;
import mystructure.MyMonomerIfc;
import mystructure.MyStructureIfc;
import mystructure.MyStructureTools;
import ultiJmol1462.MyJmolTools;

public class ShapeBuilder {
    //-------------------------------------------------------------
    // Class variables
    //-------------------------------------------------------------
    private final MyStructureIfc myStructureGlobalBrut;
    private EnumShapeReductor enumShapeReductor;

    private boolean debug = true;
    private List<PointWithPropertiesIfc> listShrinkedShapePoints = new ArrayList<>();
    private AlgoParameters algoParameters;

    //-------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------
    public ShapeBuilder(MyStructureIfc myStructureGlobal, AlgoParameters algoParameters, EnumShapeReductor enumShapeReductor) {

        this.myStructureGlobalBrut = myStructureGlobal;
        this.algoParameters = algoParameters;
        this.enumShapeReductor = enumShapeReductor;
        this.algoParameters = algoParameters;
    }


    //-------------------------------------------------------------
    // Interface & Public methods
    //-------------------------------------------------------------
    public ShapeContainerWithPeptide getShapeAroundAChain(char[] chainId) throws ShapeBuildingException { // whole chain query

        StructureLocalToBuildShapeWholeChain structureLocalToBuildShapeWholeChain = new StructureLocalToBuildShapeWholeChain(myStructureGlobalBrut, chainId, algoParameters);
        structureLocalToBuildShapeWholeChain.compute();
        MyStructureIfc myStructureLocal = structureLocalToBuildShapeWholeChain.getMyStructureLocal();
        MyChainIfc ligand = structureLocalToBuildShapeWholeChain.getLigand();

        MyStructureIfc myStructureLocalProtonated = MyJmolTools.protonateStructure(myStructureLocal, algoParameters);
        // debug
        //String structureToV3000 = myStructureLocal.toV3000();
        //String pathToFile = algoParameters.getPATH_TO_OUTPUT_PEPTIDES_PDB_FILES() + "structureLocalSegmentOfChain.mol";
        //WriteTextFile.writeTextFile(structureToV3000, pathToFile);
        List<PointIfc> listOfPointsFromChainLigand = MyStructureTools.makeQueryPointsFromMyChainIfc(ligand);
        Box box = makeBoxOutOfLocalStructure(myStructureLocalProtonated);
        CollectionOfPointsWithPropertiesIfc shapeCollectionPoints = computeShape(listOfPointsFromChainLigand, myStructureLocalProtonated, box, algoParameters);

        ShapeContainerWithPeptide shapeContainerWithPeptide = buildShapeContainerWithPeptide(myStructureLocalProtonated, listOfPointsFromChainLigand, algoParameters, shapeCollectionPoints, ligand, 0);
        shapeContainerWithPeptide.setFourLetterCode(myStructureGlobalBrut.getFourLetterCode());
        return shapeContainerWithPeptide;
    }


    public ShapeContainerWithPeptide getShapeAroundASegmentOfChainUsingStartingMyMonomerPositionInChain(char[] chainId, int startingRankId, int peptideLength) throws ShapeBuildingException { // part chain query

        StructureLocalToBuildShapeSegmentOfShape structureLocalToBuildShapeSegmentOfShape = new StructureLocalToBuildShapeSegmentOfShape(myStructureGlobalBrut, chainId, startingRankId, peptideLength, algoParameters);
        structureLocalToBuildShapeSegmentOfShape.compute();
        MyStructureIfc myStructureLocal = structureLocalToBuildShapeSegmentOfShape.getMyStructureLocal();
        MyChainIfc ligand = structureLocalToBuildShapeSegmentOfShape.getLigand();

        MyStructureIfc myStructureLocalProtonated = MyJmolTools.protonateStructure(myStructureLocal, algoParameters);
        // debug
        //String structureToV3000 = myStructureLocal.toV3000();
        //String pathToFile = algoParameters.getPATH_TO_OUTPUT_PEPTIDES_PDB_FILES() + "structureLocalSegmentOfChain.mol";
        //WriteTextFile.writeTextFile(structureToV3000, pathToFile);
        List<PointIfc> listOfPointsFromChainLigand = MyStructureTools.makeQueryPointsFromMyChainIfc(ligand);
        Box box = makeBoxOutOfLocalStructure(myStructureLocalProtonated);
        CollectionOfPointsWithPropertiesIfc shapeCollectionPoints = computeShape(listOfPointsFromChainLigand, myStructureLocalProtonated, box, algoParameters);

        ShapeContainerWithPeptide shapeContainerWithPeptide = buildShapeContainerWithPeptide(myStructureLocalProtonated, listOfPointsFromChainLigand, algoParameters, shapeCollectionPoints, ligand, startingRankId);
        shapeContainerWithPeptide.setFourLetterCode(myStructureGlobalBrut.getFourLetterCode());
        return shapeContainerWithPeptide;
    }


    public ShapeContainerWithLigand getShapeAroundAHetAtomLigand(char[] hetAtomsLigandId, int occurrenceId) throws ShapeBuildingException {

        StructureLocalToBuildShapeHetAtm structureLocalToBuildShapeHetAtm = new StructureLocalToBuildShapeHetAtm(myStructureGlobalBrut, hetAtomsLigandId, occurrenceId, algoParameters);
        structureLocalToBuildShapeHetAtm.compute();
        MyStructureIfc myStructureLocal = structureLocalToBuildShapeHetAtm.getMyStructureLocal();
        MyChainIfc ligand = structureLocalToBuildShapeHetAtm.getLigand();
        MyMonomerIfc hetAtomsGroup = structureLocalToBuildShapeHetAtm.getHetAtomsGroup();

        MyStructureIfc myStructureLocalProtonated = MyJmolTools.protonateStructure(myStructureLocal, algoParameters);
        // debug
        //String structureToV3000 = myStructureLocal.toV3000();
        //String pathToFile = algoParameters.getPATH_TO_OUTPUT_PEPTIDES_PDB_FILES() + "structureLocalSegmentOfChain.mol";
        //WriteTextFile.writeTextFile(structureToV3000, pathToFile);
        List<PointIfc> listOfPointsFromChainLigand = MyStructureTools.makeQueryPointsFromMyChainIfc(ligand);
        Box box = makeBoxOutOfLocalStructure(myStructureLocalProtonated);
        CollectionOfPointsWithPropertiesIfc shapeCollectionPoints = computeShape(listOfPointsFromChainLigand, myStructureLocalProtonated, box, algoParameters);

        ShapeContainerWithLigand shapeContainerWithLigand = buildShapeContainerWithLigand(myStructureLocalProtonated, listOfPointsFromChainLigand, algoParameters, shapeCollectionPoints, hetAtomsGroup, occurrenceId);
        shapeContainerWithLigand.setFourLetterCode(myStructureGlobalBrut.getFourLetterCode());
        return shapeContainerWithLigand;
    }


    public ShapeContainerAtomIdsWithinShapeWithPeptide getShapeAroundAtomDefinedByIds(List<QueryAtomDefinedByIds> listAtomDefinedByIds, List<String> chainToIgnore) throws ShapeBuildingException { // LennardJones query

        // Note it is built with Peptide although it is only an option for later in case we build a query with atomis
        // and still wante to use the peptide to compute rmsd of ligand to the hits

        StructureLocalToBuildShapeAroundAtomDefinedByIds structureLocalToBuildShapeAroundAtomDefinedByIds = new StructureLocalToBuildShapeAroundAtomDefinedByIds(myStructureGlobalBrut, listAtomDefinedByIds, algoParameters, chainToIgnore);
        structureLocalToBuildShapeAroundAtomDefinedByIds.compute();
        MyStructureIfc myStructureLocal = structureLocalToBuildShapeAroundAtomDefinedByIds.getMyStructureLocal();
        if (myStructureLocal == null) {
            return null;
        }
        MyStructureIfc myStructureLocalProtonated = MyJmolTools.protonateStructure(myStructureLocal, algoParameters);
        Box box = makeBoxOutOfLocalStructure(myStructureLocalProtonated);


        List<PointIfc> listOfPointsFromChainLigandFromLennarJones = computeListOfPointsWithLennardJones(box, myStructureLocal, listAtomDefinedByIds);

        CollectionOfPointsWithPropertiesIfc shapeCollectionPoints = computeShape(listOfPointsFromChainLigandFromLennarJones, myStructureLocal, box, algoParameters);
        ShapeContainerAtomIdsWithinShapeWithPeptide shapeContainerAtomIdsWithinShapeWithPeptide = buildShapeContainerFromAtomIdsWithinShape(myStructureLocal, listOfPointsFromChainLigandFromLennarJones, algoParameters, shapeCollectionPoints, listAtomDefinedByIds);

        shapeContainerAtomIdsWithinShapeWithPeptide.setFourLetterCode(myStructureGlobalBrut.getFourLetterCode());
        return shapeContainerAtomIdsWithinShapeWithPeptide;
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

                    // where do we need LN points to be computed
                    // on all grid points

                    // inside computation there is acutoff on the value supposed to capture interesting point
                    // probably not needed tocompute LN points not in between a min and max from Atoms
                    //double minDistance = 0.2;
                    double minDistance = 1.0;
                    double maxDistance = algoParameters.getDISTANCE_FROM_PEPTIDE_TO_WHICH_INTERACTINGPROTEIN_IS_CONSIDERED();

                    double minDistanceOfThisPoint = Double.MAX_VALUE;
                    for (MyChainIfc chain : localMyStructure.getAllChainsRelevantForShapeBuilding()) {
                        for (MyMonomerIfc monomer : chain.getMyMonomers()) {
                            for (MyAtomIfc atom : monomer.getMyAtoms()) {
                                double distance = ToolsMath.computeDistance(atom.getCoords(), atPosition);
                                if (distance < minDistanceOfThisPoint) {
                                    minDistanceOfThisPoint = distance;
                                }
                            }
                        }
                    }

                    if ((minDistanceOfThisPoint > minDistance) && (minDistanceOfThisPoint < maxDistance)) {
                        //if (minDistanceOfThisPoint < maxDistance){
                        //if (minDistanceOfThisPoint < maxDistance){
                        listPositions.add(atPosition);
                    }
                }
            }
        }

        System.out.println("points in the grid  = " + listPositions.size());


        // Multithreaded computation of LJ grid
        int countOfSubpacket = algoParameters.getSUB_THREAD_COUNT_FORK_AND_JOIN();
        ForkJoinPool pool = new ForkJoinPool();
        ComputeLennardJonesRecursiveTask computeLennardJonesMultiThread = new ComputeLennardJonesRecursiveTask(listPositions, 0, listPositions.size() - 1, listPositions.size() / countOfSubpacket, localMyStructure, algoParameters);
        listOfPointsWithLennardJones = pool.invoke(computeLennardJonesMultiThread);
        pool.shutdownNow();

        //System.out.println("Count of Lennard Jones points computed = " + listOfPointsWithLennardJones.size());

        List<PointIfc> listOfPointsWithLennardJonesQuery = new ArrayList<>();

        // Extract a sub part of the LN points

        List<float[]> listV2 = new ArrayList<>();
        for (PointIfc coordQuery : listCoordsCenterQuery) {
            listV2.add(coordQuery.getCoords());
        }
        for (QueryAtomDefinedByIds queryAtomDefinedByIds : listQueryAtomDefinedByIds) {

            for (PointIfc pointsWithLennardJones : listOfPointsWithLennardJones) {

                float[] coordsLNPoint = pointsWithLennardJones.getCoords();
                float distance = ToolsMath.computeDistance(coordsLNPoint, listV2);

                if (distance < queryAtomDefinedByIds.getRadiusForQueryAtomsDefinedByIds()) {
                    listOfPointsWithLennardJonesQuery.add(pointsWithLennardJones);
                }
            }
        }


        //System.out.println("Count of Lennard Jones points in this shape Query Only = " + listOfPointsWithLennardJonesQuery.size());


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


    private List<PointIfc> makeQueryPointsFromMyMonomerIfc(MyMonomerIfc myMonomer) {

        MyChainIfc myChain = new MyChain(myMonomer, myMonomer.getParent().getChainId());
        return MyStructureTools.makeQueryPointsFromMyChainIfc(myChain);
    }


    public Box makeBoxOutOfLocalStructure(MyStructureIfc myStructure) {

        Box box = new Box(myStructure, algoParameters);
        return box;
    }


    private CollectionOfPointsWithPropertiesIfc computeShape(List<PointIfc> listOfLigandPoints, MyStructureIfc myStructureShape, Box box, AlgoParameters algoParameters) {

        double maxDistanceBetweenGridPointAndLigand = algoParameters.getDISTANCE_FROM_PEPTIDE_TO_WHICH_INTERACTINGPROTEIN_IS_CONSIDERED();

        List<HBondDefinedWithAtoms> hBonds = intraStructureHBondDetector(myStructureShape);
        List<HBondDefinedWithAtoms> dehydrons = buildDehydrons(hBonds);

        System.out.println("in myStructureGlobal hbond count = " + hBonds.size() + " dehydron count = " + dehydrons.size());

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

        System.out.println(listPositionWhereToComputeProperties.size() + " points to compute with ComputeShapePointsMultiThread");
        int countOfSubpacket = algoParameters.getSUB_THREAD_COUNT_FORK_AND_JOIN();
        int threshold = listPositionWhereToComputeProperties.size() / countOfSubpacket + 1;
        if (threshold < 2) {
            threshold = 2;
        }
        ForkJoinPool pool = new ForkJoinPool();

        ComputeShapePointsMultiThread computeShapePointsMultiThread = new ComputeShapePointsMultiThread(listPositionWhereToComputeProperties,
                0, listPositionWhereToComputeProperties.size() - 1, threshold, myStructureShape, listOfLigandPoints, algoParameters, dehydrons);


        listShrinkedShapePoints = pool.invoke(computeShapePointsMultiThread);
        System.out.println(listShrinkedShapePoints.size() + "  were actually computed");
        pool.shutdownNow();

        CollectionOfPointsWithPropertiesIfc collectionOfPointsWithProperties = new CollectionOfPointsWithProperties(listShrinkedShapePoints);

        int countHbondDonnor = 0;
        int countHbondAcceptor = 0;
        int countDehydron = 0;
        for (int i = 0; i < collectionOfPointsWithProperties.getSize(); i++) {
            PointWithPropertiesIfc pointWithtProperties = collectionOfPointsWithProperties.getPointFromId(i);
            Float hdonnor = pointWithtProperties.get(PropertyName.HbondDonnor);
            Float hacceptor = pointWithtProperties.get(PropertyName.HbondAcceptor);
            if (hdonnor != null && hdonnor > 0.1) {
                countHbondDonnor += 1;
            }
            if (hacceptor != null && hacceptor > 0.1) {
                countHbondAcceptor += 1;
            }
            Float dehydron = pointWithtProperties.get(PropertyName.Dehydron);

            if (dehydron != null && dehydron > 0.1) {
                countDehydron += 1;
            }
        }

        System.out.println("in this shape striking count : " + countHbondDonnor + " donnor grid points " + countHbondAcceptor + " acceptor grid points ");

        if (countDehydron > 0) {
            System.out.println("in this shape : " + countDehydron + " dehydron grid points ");
        }
        return collectionOfPointsWithProperties;
    }


    private List<HBondDefinedWithAtoms> buildDehydrons(List<HBondDefinedWithAtoms> hBonds) {

        List<HBondDefinedWithAtoms> dehydrons = new ArrayList<>();

        for (HBondDefinedWithAtoms hbond : hBonds) {

            int countHydrophobicAtom = ShapeBuildingTools.getCountOfHydrophobicAtomsInTheNeighborhoodOfMyAtomForDehydronsUseOnly(hbond.getMyAtomHydrogen(), algoParameters);
            //System.out.println(countHydrophobicAtom + "  " + algoParameters.getDEHYDRON_CUTOFF_COUNT_OF_HYDROPHOBIC_ATOM_SURRONDING_HBOND());
            if (countHydrophobicAtom <= algoParameters.getDEHYDRON_CUTOFF_COUNT_OF_HYDROPHOBIC_ATOM_SURRONDING_HBOND()) {
                dehydrons.add(hbond);
            }
        }
        return dehydrons;
    }


    private ShapeContainerAtomIdsWithinShapeWithPeptide buildShapeContainerFromAtomIdsWithinShape(MyStructureIfc myStructureShape, List<PointIfc> listOfPointsFromChainLigand, AlgoParameters algoParameters, CollectionOfPointsWithPropertiesIfc shapeCollectionPoints, List<QueryAtomDefinedByIds> listAtomDefinedByIds) {

        CollectionOfPointsWithPropertiesIfc shrinkedShapeBasedOnDistanceToLigand = simplifyShape(algoParameters, shapeCollectionPoints);

        ShapeContainerAtomIdsWithinShapeWithPeptide shape = new ShapeContainerAtomIdsWithinShapeWithPeptide(listAtomDefinedByIds, shrinkedShapeBasedOnDistanceToLigand, listOfPointsFromChainLigand, myStructureShape, algoParameters);

        prepareShapeContainer(listOfPointsFromChainLigand, algoParameters, shrinkedShapeBasedOnDistanceToLigand, shape);
        return shape;
    }


    private ShapeContainerWithPeptide buildShapeContainerWithPeptide(MyStructureIfc myStructureShape, List<PointIfc> listOfPointsFromChainLigand, AlgoParameters algoParameters, CollectionOfPointsWithPropertiesIfc shapeCollectionPoints, MyChainIfc peptide, int startingIndex) {

        CollectionOfPointsWithPropertiesIfc shrinkedShapeBasedOnDistanceToLigand = simplifyShape(algoParameters, shapeCollectionPoints);

        ShapeContainerWithPeptide shape = new ShapeContainerWithPeptide(shrinkedShapeBasedOnDistanceToLigand, listOfPointsFromChainLigand, myStructureShape, algoParameters, peptide, startingIndex);

        prepareShapeContainer(listOfPointsFromChainLigand, algoParameters, shrinkedShapeBasedOnDistanceToLigand, shape);
        return shape;
    }


    private ShapeContainerWithLigand buildShapeContainerWithLigand(MyStructureIfc myStructureShape, List<PointIfc> listOfPointsFromChainLigand, AlgoParameters algoParameters, CollectionOfPointsWithPropertiesIfc shapeCollectionPoints, MyMonomerIfc myMonomer, int occurenceId) {

        CollectionOfPointsWithPropertiesIfc shrinkedShapeBasedOnDistanceToLigand = simplifyShape(algoParameters, shapeCollectionPoints);

        ShapeContainerWithLigand shape = new ShapeContainerWithLigand(shrinkedShapeBasedOnDistanceToLigand, listOfPointsFromChainLigand, myStructureShape, algoParameters, myMonomer, occurenceId);

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
        // To release RAM
        generateTriangles = null;

        Set<TriangleInteger> treesetOfTriangle = new HashSet<>();
        for (TriangleInteger triangleInteger : listTriangleOfPointsFromMinishape) {
            treesetOfTriangle.add(triangleInteger);
            //System.out.println(triangleInteger.toString());
        }
        List<TriangleInteger> listTriangleInteger = new ArrayList<>();
        for (TriangleInteger triangleInteger : treesetOfTriangle) {
            listTriangleInteger.add(triangleInteger);
        }
        // To release RAM
        treesetOfTriangle = null;
        listTriangleOfPointsFromMinishape = listTriangleInteger;
        return listTriangleOfPointsFromMinishape;
    }


    private Map<Integer, PointWithPropertiesIfc> buildMinishape(List<PointIfc> listOfPointsFromChainLigand, AlgoParameters algoParameters, CollectionOfPointsWithPropertiesIfc shapeCollectionPoints) {

        long startTime = System.currentTimeMillis();
        Map<Integer, PointWithPropertiesIfc> miniShape = null;
        if (enumShapeReductor == EnumShapeReductor.CLUSTERING) {
            ShapeReductorIfc shapeReductor = new ShapeReductorByClustering(shapeCollectionPoints, algoParameters);
            miniShape = shapeReductor.computeReducedCollectionOfPointsWithProperties();
        }
        if (enumShapeReductor == EnumShapeReductor.SELECTING) {
            ShapeReductorIfc shapeReductor = new ShapeReductorBySelectingPoints(shapeCollectionPoints, algoParameters);
            miniShape = shapeReductor.computeReducedCollectionOfPointsWithProperties();
        }

        long compTime = System.currentTimeMillis() - startTime;
        double comptimeSeconds = compTime / 1000.0;

        System.out.println("mini shape size = " + miniShape.size() + " done in " + comptimeSeconds + " s ");
        //Map<Integer, PointWithProperties> shrinkedMiniShape = shrinkMiniShapeAccordingToFinalMaxDistanceToLigand(miniShape, algoParameters);
        //System.out.println("mini shape after = " + shrinkedMiniShape.size());
        return miniShape;
    }


    private CollectionOfPointsWithPropertiesIfc simplifyShape(AlgoParameters algoParameters, CollectionOfPointsWithPropertiesIfc shapeCollectionPoints) {

        System.out.println("before NONE removal : " + shapeCollectionPoints.getSize());
        CollectionOfPointsWithPropertiesIfc shrinkedShapeForNONEPoint = removePointsOfStrikingPropertiesNoneIfCloseEnoughToAnotherPointWithAnyStrikingPropertiesNotNone(shapeCollectionPoints);
        System.out.println("after NONE removal : " + shrinkedShapeForNONEPoint.getSize());

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

                        distance = ToolsMath.computeDistance(shape.getPointFromId(i).getCoords().getCoords(), shape.getPointFromId(j).getCoords().getCoords());
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
            // find Hs
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
        // check if distance acceptor and H donnor

        if (donnor.getParent() != acceptor.getParent()) { // Intra skipped because of Tyr OH to its own O
            float distanceBetweenHydrogenAndAcceptor = ToolsMath.computeDistance(acceptor.getCoords(), hydrogen.getCoords());
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
