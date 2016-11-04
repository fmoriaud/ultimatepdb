package protocols;

import io.IOTools;
import mystructure.MyStructureIfc;
import parameters.AlgoParameters;
import shape.ShapeContainerIfc;
import shapeBuilder.EnumShapeReductor;
import shapeBuilder.ShapeBuildingException;

import java.util.Random;
import java.util.logging.Level;


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

        long t = System.nanoTime();
        ControllerLoger.logger.log(Level.INFO, "&&&&& Start Read " + String.valueOf(fourLetterCode) + " " + t);
        MyStructureIfc myStructure = IOTools.getMyStructureIfc(algoParameters, fourLetterCode);

        ControllerLoger.logger.log(Level.INFO, "&&&&&& Finished Read " + String.valueOf(fourLetterCode) + " " + t);
        ShapeContainerIfc shapecontainer = null;
        try {
            ControllerLoger.logger.log(Level.INFO,"&&&&&& Start shape container " + String.valueOf(fourLetterCode) + " " + t);
            shapecontainer = ShapeContainerFactory.getShapeAroundAChain(EnumShapeReductor.CLUSTERING, myStructure, algoParameters, chainId);
            ControllerLoger.logger.log(Level.INFO,"&&&&&& Made shape container " + String.valueOf(fourLetterCode) + " " + t);
        } catch (ShapeBuildingException e) {
            e.printStackTrace();
        }

        return shapecontainer;
    }

    @Override
    public ShapeContainerIfc getShapecontainer(MyStructureIfc myStructure) {

        long t = System.nanoTime();

        ShapeContainerIfc shapecontainer = null;
        try {
            ControllerLoger.logger.log(Level.INFO,"&&&&&& Start shape container " + String.valueOf(fourLetterCode) + " " + t);
            shapecontainer = ShapeContainerFactory.getShapeAroundAChain(EnumShapeReductor.CLUSTERING, myStructure, algoParameters, chainId);
            ControllerLoger.logger.log(Level.INFO,"&&&&&& Made shape container " + String.valueOf(fourLetterCode) + " " + t);
        } catch (ShapeBuildingException e) {
            e.printStackTrace();
        }
        return shapecontainer;
    }

    public char[] getFourLetterCode() {
        return fourLetterCode;
    }
}
