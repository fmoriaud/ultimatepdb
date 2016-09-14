package shape;

import java.util.List;

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
	public ShapeContainerAtomIdsWithinShape(List<QueryAtomDefinedByIds> listAtomDefinedByIds, CollectionOfPointsWithPropertiesIfc shape, List<PointIfc> listPointDefininingLigandUsedToComputeShape, MyStructureIfc myStructureUsedToComputeShape, AlgoParameters algoParameters) {
		super(shape, listPointDefininingLigandUsedToComputeShape,
				myStructureUsedToComputeShape, algoParameters);
		this.listAtomDefinedByIds = listAtomDefinedByIds;
	}



	public List<QueryAtomDefinedByIds> getListAtomDefinedByIds() {
		return listAtomDefinedByIds;
	}

}
