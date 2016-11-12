package protocols;

import io.IOTools;
import mystructure.MyStructureIfc;
import parameters.AlgoParameters;
import parameters.QueryAtomDefinedByIds;
import shape.ShapeContainerIfc;
import shapeBuilder.EnumShapeReductor;
import shapeBuilder.ShapeBuildingException;

import java.util.List;

/**
 * Created by Fabrice on 12/11/16.
 */
public class ShapecontainerDefinedByAroundAtomDefinedByIds implements ShapeContainerDefined {

    //-------------------------------------------------------------
    // Class members
    //-------------------------------------------------------------
    private char[] fourLetterCode;
    private AlgoParameters algoParameters;
    private List<QueryAtomDefinedByIds> listAtomDefinedByIds;
    private List<String> chainToIgnore;

    //-------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------
    public ShapecontainerDefinedByAroundAtomDefinedByIds(char[] fourLetterCode, AlgoParameters algoParameters, List<QueryAtomDefinedByIds> listAtomDefinedByIds, List<String> chainToIgnore) {

        this.fourLetterCode = fourLetterCode;
        this.algoParameters = algoParameters;
        this.listAtomDefinedByIds = listAtomDefinedByIds;
        this.chainToIgnore = chainToIgnore;
    }


    //-------------------------------------------------------------
    // Public & Override methods
    //-------------------------------------------------------------
    @Override
    public ShapeContainerIfc getShapecontainer() throws ShapeBuildingException {
        MyStructureIfc myStructure = IOTools.getMyStructureIfc(algoParameters, fourLetterCode);
        if (myStructure == null) {
            ShapeBuildingException exception = new ShapeBuildingException("Failed to ShapecontainerDefinedBySegmentOfChain because of null MyStructure");
            throw exception;
        }

        ShapeContainerIfc shapecontainer = ShapeContainerFactory.getShapeAroundAtomDefinedByIds(EnumShapeReductor.CLUSTERING, myStructure, algoParameters, listAtomDefinedByIds, chainToIgnore);
        return shapecontainer;
    }

    @Override
    public ShapeContainerIfc getShapecontainer(MyStructureIfc myStructureTarget) throws ShapeBuildingException {
        ShapeContainerIfc shapecontainer = ShapeContainerFactory.getShapeAroundAtomDefinedByIds(EnumShapeReductor.CLUSTERING, myStructureTarget, algoParameters, listAtomDefinedByIds, chainToIgnore);
        return shapecontainer;
    }
}
