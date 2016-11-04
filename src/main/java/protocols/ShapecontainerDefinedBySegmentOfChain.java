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
    public ShapeContainerIfc getShapecontainer() {

        MyStructureIfc myStructure = IOTools.getMyStructureIfc(algoParameters, fourLetterCode);
        ShapeContainerIfc shapecontainer = null;
        try {
            shapecontainer = ShapeContainerFactory.getShapeAroundASegmentOfChainUsingStartingMyMonomerPositionInChain(EnumShapeReductor.CLUSTERING, myStructure, algoParameters, chainId, startingRankId, peptideLength);
        } catch (ShapeBuildingException e) {
            e.printStackTrace();
        }

        return shapecontainer;
    }

    @Override
    public ShapeContainerIfc getShapecontainer(MyStructureIfc myStructureTarget) {
        return null;
    }
}
