package protocols;

import io.IOTools;
import mystructure.MyStructureIfc;
import parameters.AlgoParameters;
import shape.ShapeContainerIfc;
import shapeBuilder.EnumShapeReductor;
import shapeBuilder.ShapeBuildingException;

/**
 * Created by Fabrice on 31/10/16.
 */
public class ShapecontainerDefinedBySegmentOfChain implements ShapeContainerDefined {
    //-------------------------------------------------------------
    // Class members
    //-------------------------------------------------------------
    private char[] fourLetterCode;
    private char[] chainId;
    private int startingRankId;
    private int peptideLength;
    private AlgoParameters algoParameters;
    private MyStructureIfc myStructure;

    //-------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------
    public ShapecontainerDefinedBySegmentOfChain(char[] fourLetterCode, char[] chainId, int startingRankId, int peptideLength, AlgoParameters algoParameters) {
        this.fourLetterCode = fourLetterCode;
        this.chainId = chainId;
        this.startingRankId = startingRankId;
        this.peptideLength = peptideLength;
        this.algoParameters = algoParameters;
    }


    //-------------------------------------------------------------
    // Public & Override methods
    //-------------------------------------------------------------
    @Override
    public ShapeContainerIfc getShapecontainer() throws ShapeBuildingException {

        myStructure = IOTools.getMyStructureIfc(algoParameters, fourLetterCode);
        if (myStructure == null) {
            ShapeBuildingException exception = new ShapeBuildingException("Failed to ShapecontainerDefinedBySegmentOfChain because of null MyStructure");
            throw exception;
        }
        ShapeContainerIfc shapecontainer = ShapeContainerFactory.getShapeAroundASegmentOfChainUsingStartingMyMonomerPositionInChain(EnumShapeReductor.CLUSTERING, myStructure, algoParameters, chainId, startingRankId, peptideLength);

        return shapecontainer;
    }


    @Override
    public ShapeContainerIfc getShapecontainer(MyStructureIfc myStructureTarget) throws ShapeBuildingException {

        this.myStructure = myStructureTarget;
        ShapeContainerIfc shapecontainer = ShapeContainerFactory.getShapeAroundASegmentOfChainUsingStartingMyMonomerPositionInChain(EnumShapeReductor.CLUSTERING, myStructureTarget, algoParameters, chainId, startingRankId, peptideLength);

        return shapecontainer;
    }

    @Override
    public MyStructureIfc getMyStructure() {
        return myStructure;
    }
}
