package pointWithProperties;

import java.util.List;

public class CollectionOfPointsWithProperties implements CollectionOfPointsWithPropertiesIfc{
	//-------------------------------------------------------------
	// Class variables
	//-------------------------------------------------------------
	private PointWithPropertiesIfc[] collectionOfPointWithProperties;




	//-------------------------------------------------------------
	// Constructor
	//-------------------------------------------------------------
	public CollectionOfPointsWithProperties(List<PointWithPropertiesIfc> listPointsWithProperties){
		PointWithPropertiesIfc[] collectionOfPointsWithProperties = listPointsWithProperties.toArray(new PointWithProperties[listPointsWithProperties.size()]);
		this.collectionOfPointWithProperties = collectionOfPointsWithProperties;
	}




	//-------------------------------------------------------------
	// Interface & Public methods
	//-------------------------------------------------------------
	@Override
	public PointWithPropertiesIfc getPointFromId(int i) {
		if (i < collectionOfPointWithProperties.length){
			return collectionOfPointWithProperties[i];
		}
		return null;
	}

	
	
	@Override
	public int getSize() {
		int size = this.collectionOfPointWithProperties.length;
		return size;
	}



	@Override public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(" Size: " + collectionOfPointWithProperties.length);
		return result.toString();
	}
}
