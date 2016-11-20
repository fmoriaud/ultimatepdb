package protocols;

import mystructure.MyStructureIfc;
import shape.ShapeContainerIfc;
import shapeBuilder.ShapeBuildingException;

/**
 * Created by Fabrice on 31/10/16.
 */
public interface ShapeContainerDefined {


    MyStructureIfc getMyStructure();
    ShapeContainerIfc getShapecontainer() throws ShapeBuildingException;
    ShapeContainerIfc getShapecontainer(MyStructureIfc myStructureTarget) throws ShapeBuildingException;

}
