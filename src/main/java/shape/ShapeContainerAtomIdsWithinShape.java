package shape;

import java.util.List;

import mystructure.MyMonomerIfc;
import parameters.AlgoParameters;
import parameters.QueryAtomDefinedByIds;
import pointWithProperties.CollectionOfPointsWithPropertiesIfc;
import pointWithProperties.PointIfc;
import mystructure.MyStructureIfc;

public class ShapeContainerAtomIdsWithinShape extends ShapeContainer implements ShapeContainerIfc{
	//------------------------
	// Class variables
	//------------------------
	private List<QueryAtomDefinedByIds> listAtomDefinedByIds;


	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public ShapeContainerAtomIdsWithinShape(List<QueryAtomDefinedByIds> listAtomDefinedByIds, CollectionOfPointsWithPropertiesIfc shape, List<PointIfc> listPointDefininingLigandUsedToComputeShape, MyStructureIfc myStructureUsedToComputeShape, List<MyMonomerIfc> foreignMonomerToExclude, AlgoParameters algoParameters) {
		super(shape, listPointDefininingLigandUsedToComputeShape,
				myStructureUsedToComputeShape, foreignMonomerToExclude, algoParameters);
		this.listAtomDefinedByIds = listAtomDefinedByIds;
	}



	public List<QueryAtomDefinedByIds> getListAtomDefinedByIds() {
		return listAtomDefinedByIds;
	}

	@Override
	public List<MyMonomerIfc> getForeignMonomerToExclude() {
		return null;
	}
}
