package protocols;

import io.IOTools;
import mystructure.MyStructureIfc;
import parameters.AlgoParameters;
import shape.ShapeContainerIfc;
import shapeBuilder.EnumShapeReductor;
import shapeBuilder.ShapeBuildingException;


public class ShapecontainerDefinedByHetatm implements ShapeContainerDefined {


    //-------------------------------------------------------------
    // Class members
    //-------------------------------------------------------------
    private char[] fourLetterCode;
    private AlgoParameters algoParameters;
    private char[] hetAtomsLigandId;
    private int occurrenceId;
    private MyStructureIfc myStructure;

    //-------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------
    public ShapecontainerDefinedByHetatm(char[] fourLetterCode, AlgoParameters algoParameters, char[] hetAtomsLigandId, int occurrenceId) {
        this.fourLetterCode = fourLetterCode;
        this.algoParameters = algoParameters;
        this.hetAtomsLigandId = hetAtomsLigandId;
        this.occurrenceId = occurrenceId;
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

        ShapeContainerIfc shapecontainer = ShapeContainerFactory.getShapeAroundAHetAtomLigand(EnumShapeReductor.CLUSTERING, myStructure, algoParameters, hetAtomsLigandId, occurrenceId);
        return shapecontainer;
    }

    @Override
    public ShapeContainerIfc getShapecontainer(MyStructureIfc myStructureTarget) throws ShapeBuildingException {

        this.myStructure = myStructureTarget;
        ShapeContainerIfc shapecontainer = ShapeContainerFactory.getShapeAroundAHetAtomLigand(EnumShapeReductor.CLUSTERING, myStructureTarget, algoParameters, hetAtomsLigandId, occurrenceId);
        return shapecontainer;
    }

    @Override
    public MyStructureIfc getMyStructure() {
        return myStructure;
    }
}
