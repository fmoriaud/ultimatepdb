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
	private final ShapeContainerIfc targetShape;
	private final AlgoParameters algoParameters;




	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public CompareOneOnlyRunnable( ShapeContainerIfc queryShape, ShapeContainerIfc targetShape, AlgoParameters algoParameters){

		this.queryShape = queryShape;
		this.targetShape = targetShape;
		this.algoParameters = algoParameters;
	}




	// -------------------------------------------------------------------
	// Public methods
	// -------------------------------------------------------------------
	@Override
	public void run() {
		try{
			boolean comparingWasOK = compare();
		} catch(Exception e){
		}

	}




	// -------------------------------------------------------------------
	// Implementation
	// -------------------------------------------------------------------
	private boolean compare(){

		try {
			ProtocolToolsToHandleInputFilesAndShapeComparisons.compareQueryToOneShape(queryShape, targetShape, algoParameters);

		} catch (ShapeBuildingException e) {
			System.out.println(" comparing failed : " + e.getMessage());
			return false;
		}
		return true;
	}
}
