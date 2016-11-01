package protocols;

import io.IOTools;
import mystructure.MyStructureIfc;
import parameters.AlgoParameters;
import shape.ShapeContainerIfc;
import shapeBuilder.EnumShapeReductor;
import shapeBuilder.ShapeBuildingException;


public class ShapecontainerDefinedByWholeChain implements ShapeContainerDefined{
    //-------------------------------------------------------------
    // Class members
    //-------------------------------------------------------------
    private char[] fourLetterCode;
    private char[] chainId;
    private AlgoParameters algoParameters;




    //-------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------
    public ShapecontainerDefinedByWholeChain(char[] fourLetterCode, char[] chainId, AlgoParameters algoParameters){
        this.fourLetterCode = fourLetterCode;
        this.chainId = chainId;
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
            shapecontainer = ShapeContainerFactory.getShapeAroundAChain(EnumShapeReductor.CLUSTERING, myStructure, algoParameters, chainId);
        } catch (ShapeBuildingException e) {
            e.printStackTrace();
        }

        return shapecontainer;
    }
}
