package protocols;

import io.IOTools;
import mystructure.MyStructureIfc;
import parameters.AlgoParameters;
import shape.ShapeContainerIfc;
import shapeBuilder.EnumShapeReductor;
import shapeBuilder.ShapeBuildingException;

import java.util.Random;
import java.util.logging.Level;


public class ShapecontainerDefinedByWholeChain implements ShapeContainerDefined {


    //-------------------------------------------------------------
    // Class members
    //-------------------------------------------------------------
    private char[] fourLetterCode;
    private char[] chainId;
    private AlgoParameters algoParameters;
    private MyStructureIfc myStructure;

    //-------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------
    public ShapecontainerDefinedByWholeChain(char[] fourLetterCode, char[] chainId, AlgoParameters algoParameters) {
        this.fourLetterCode = fourLetterCode;
        this.chainId = chainId;
        this.algoParameters = algoParameters;

    }


    //-------------------------------------------------------------
    // Public & Override methods
    //-------------------------------------------------------------
    @Override
    public ShapeContainerIfc getShapecontainer() throws ShapeBuildingException {

        long t = System.nanoTime();
        ControllerLoger.logger.log(Level.INFO, "&&&&& Start Read " + String.valueOf(fourLetterCode) + " " + t);
        myStructure = IOTools.getMyStructureIfc(algoParameters, fourLetterCode);

        if (myStructure == null) {
            ShapeBuildingException exception = new ShapeBuildingException("Failed to ShapecontainerDefinedBySegmentOfChain because of null MyStructure");
            throw exception;
        }
        ControllerLoger.logger.log(Level.INFO, "&&&&&& Finished Read " + String.valueOf(fourLetterCode) + " " + t);
        ShapeContainerIfc shapecontainer = null;

        ControllerLoger.logger.log(Level.INFO, "&&&&&& Start shape container " + String.valueOf(fourLetterCode) + " " + t);
        shapecontainer = ShapeContainerFactory.getShapeAroundAChain(EnumShapeReductor.CLUSTERING, myStructure, algoParameters, chainId);
        ControllerLoger.logger.log(Level.INFO, "&&&&&& Made shape container " + String.valueOf(fourLetterCode) + " " + t);

        return shapecontainer;
    }

    @Override
    public ShapeContainerIfc getShapecontainer(MyStructureIfc myStructure) throws ShapeBuildingException {

        this.myStructure = myStructure;
        long t = System.nanoTime();

        ControllerLoger.logger.log(Level.INFO, "&&&&&& Start shape container " + String.valueOf(fourLetterCode) + " " + t);
        ShapeContainerIfc shapecontainer = ShapeContainerFactory.getShapeAroundAChain(EnumShapeReductor.CLUSTERING, myStructure, algoParameters, chainId);
        ControllerLoger.logger.log(Level.INFO, "&&&&&& Made shape container " + String.valueOf(fourLetterCode) + " " + t);

        return shapecontainer;
    }

    @Override
    public MyStructureIfc getMyStructure() {
        return myStructure;
    }

    public char[] getFourLetterCode() {
        return fourLetterCode;
    }
}
