package shapeBuilder;

import shape.ShapeContainerIfc;

public interface ShapeBuilderConstructorIfc {

	ShapeContainerIfc getShapeContainer() throws ShapeBuildingException;
}
