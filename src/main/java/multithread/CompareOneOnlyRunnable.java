package multithread;

import parameters.AlgoParameters;
import protocols.ProtocolToolsToHandleInputFilesAndShapeComparisons;
import shape.ShapeContainerIfc;
import shapeBuilder.ShapeBuilderConstructorIfc;
import shapeBuilder.ShapeBuildingException;



public class CompareOneOnlyRunnable implements Runnable{
	//------------------------
	// Class variables
	//------------------------
	private final ShapeContainerIfc queryShape;
	private final ShapeBuilderConstructorIfc shapeBuilder;
	private final AlgoParameters algoParameters;




	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public CompareOneOnlyRunnable( ShapeContainerIfc queryShape, ShapeBuilderConstructorIfc shapeBuilder, AlgoParameters algoParameters){

		this.queryShape = queryShape;
		this.shapeBuilder = shapeBuilder;
		this.algoParameters = algoParameters;
	}




	// -------------------------------------------------------------------
	// Public methods
	// -------------------------------------------------------------------
	@Override
	public void run() {
		try{
			boolean comparingWasOK = compare(shapeBuilder);
		} catch(Exception e){
		}

	}




	// -------------------------------------------------------------------
	// Implementation
	// -------------------------------------------------------------------
	private boolean compare(ShapeBuilderConstructorIfc targetShape){

		try {
			ProtocolToolsToHandleInputFilesAndShapeComparisons.compareQueryToOneShape(queryShape, targetShape, algoParameters);

		} catch (ShapeBuildingException e) {
			System.out.println(" comparing failed : " + e.getMessage());
			return false;
		}
		return true;
	}
}
