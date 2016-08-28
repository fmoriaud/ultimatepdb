package shape;

import java.util.List;

import parameters.AlgoParameters;
import parameters.QueryAtomDefinedByIds;
import pointWithProperties.CollectionOfPointsWithPropertiesIfc;
import pointWithProperties.PointIfc;
import structure.MyStructureIfc;

public class ShapeContainerAtomIdsWithinShape extends ShapeContainer implements ShapeContainerIfc{
	//------------------------
	// Class variables
	//------------------------
	private List<QueryAtomDefinedByIds> listAtomDefinedByIds;
	private double radiusForQueryAtomsDefinedByIds;




	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public ShapeContainerAtomIdsWithinShape(List<QueryAtomDefinedByIds> listAtomDefinedByIds, double radiusForQueryAtomsDefinedByIds, CollectionOfPointsWithPropertiesIfc shape, List<PointIfc> listPointDefininingLigandUsedToComputeShape, MyStructureIfc myStructureUsedToComputeShape, AlgoParameters algoParameters) {
		super(shape, listPointDefininingLigandUsedToComputeShape,
				myStructureUsedToComputeShape, algoParameters);
		this.listAtomDefinedByIds = listAtomDefinedByIds;
		this.radiusForQueryAtomsDefinedByIds = radiusForQueryAtomsDefinedByIds;
	}



	public List<QueryAtomDefinedByIds> getListAtomDefinedByIds() {
		return listAtomDefinedByIds;
	}


	
	public double getRadiusForQueryAtomsDefinedByIds() {
		return radiusForQueryAtomsDefinedByIds;
	}

}
