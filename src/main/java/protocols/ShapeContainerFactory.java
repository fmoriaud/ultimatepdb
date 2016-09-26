package protocols;

import mystructure.MyStructureIfc;
import parameters.AlgoParameters;
import parameters.QueryAtomDefinedByIds;
import shape.ShapeContainerIfc;
import shapeBuilder.EnumShapeReductor;
import shapeBuilder.ShapeBuilder;
import shapeBuilder.ShapeBuildingException;

import java.util.List;

/**
 * Created by Fabrice on 26/09/16.
 */


// Would be the way to get a shapecontainer
public class ShapeContainerFactory {


    /**
     * @param enumShapeReductor
     * @param myStructureGlobal is not protonated
     * @param algoParameters
     * @return
     */
    public static ShapeContainerIfc getShapeAroundAChain(EnumShapeReductor enumShapeReductor, MyStructureIfc myStructureGlobal, AlgoParameters algoParameters,
                                                         char[] chainId) throws ShapeBuildingException {

        ShapeBuilder ShapeBuilder = new ShapeBuilder(myStructureGlobal, algoParameters, enumShapeReductor);
        ShapeContainerIfc shapeContainer = ShapeBuilder.getShapeAroundAChain(chainId);

        return shapeContainer;
    }


    public static ShapeContainerIfc getShapeAroundASegmentOfChainUsingStartingMyMonomerPositionInChain(EnumShapeReductor enumShapeReductor, MyStructureIfc myStructureGlobal, AlgoParameters algoParameters,
                                                                       char[] chainId, int startingRankId, int peptideLength) throws ShapeBuildingException {

        ShapeBuilder ShapeBuilder = new ShapeBuilder(myStructureGlobal, algoParameters, enumShapeReductor);
        ShapeContainerIfc shapeContainer = ShapeBuilder.getShapeAroundASegmentOfChainUsingStartingMyMonomerPositionInChain(chainId, startingRankId, peptideLength);
        return shapeContainer;
    }


    public static ShapeContainerIfc getShapeAroundAHetAtomLigand(EnumShapeReductor enumShapeReductor, MyStructureIfc myStructureGlobal, AlgoParameters algoParameters,
                                                                 char[] hetAtomsLigandId, int occurrenceId) throws ShapeBuildingException {

        ShapeBuilder ShapeBuilder = new ShapeBuilder(myStructureGlobal, algoParameters, enumShapeReductor);
        ShapeContainerIfc shapeContainer = ShapeBuilder.getShapeAroundAHetAtomLigand(hetAtomsLigandId, occurrenceId);
        return shapeContainer;
    }


    public static ShapeContainerIfc getShapeAroundAtomDefinedByIds(EnumShapeReductor enumShapeReductor, MyStructureIfc myStructureGlobal, AlgoParameters algoParameters,
                                                                   List<QueryAtomDefinedByIds> listAtomDefinedByIds, List<String> chainToIgnore) throws ShapeBuildingException {

        ShapeBuilder ShapeBuilder = new ShapeBuilder(myStructureGlobal, algoParameters, enumShapeReductor);
        ShapeContainerIfc shapeContainer = ShapeBuilder.getShapeAroundAtomDefinedByIds(listAtomDefinedByIds, chainToIgnore);
        return shapeContainer;
    }


    //-------------------------------------------------------------
    // Implementation
    //-------------------------------------------------------------
}
