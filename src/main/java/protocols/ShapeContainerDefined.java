package protocols;

import mystructure.MyStructureIfc;
import shape.ShapeContainerIfc;

/**
 * Created by Fabrice on 31/10/16.
 */
public interface ShapeContainerDefined {


    ShapeContainerIfc getShapecontainer();
    ShapeContainerIfc getShapecontainer(MyStructureIfc myStructureTarget);

}
