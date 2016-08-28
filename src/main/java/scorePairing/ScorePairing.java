package scorePairing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import parameters.AlgoParameters;
import pointWithProperties.CollectionOfPointsWithPropertiesIfc;
import pointWithProperties.PointWithPropertiesIfc;
import shapeCompare.PairingAndNullSpaces;
import shapeCompare.ResultsFromEvaluateCost;

public class ScorePairing {

	//------------------------
	// Class variables
	//------------------------
	private Map<Integer, PointWithPropertiesIfc> queryShape;
	private Map<Integer, PointWithPropertiesIfc> hitShape;
	private AlgoParameters algoParameters;





	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public ScorePairing(Map<Integer, PointWithPropertiesIfc> queryShape, Map<Integer, PointWithPropertiesIfc> hitShape, AlgoParameters algoParameters){
		this.queryShape = queryShape;
		this.hitShape = hitShape;
		this.algoParameters = algoParameters;
	}



	public ScorePairing(CollectionOfPointsWithPropertiesIfc queryShape, CollectionOfPointsWithPropertiesIfc hitShape, AlgoParameters algoParameters){

		Map<Integer, PointWithPropertiesIfc> tempMapShape1 = new HashMap<>(); 
		Map<Integer, PointWithPropertiesIfc> tempMapShape2 = new HashMap<>(); 

		for (int i=0; i<queryShape.getSize(); i++){
			tempMapShape1.put(i, queryShape.getPointFromId(i));
		}
		for (int i=0; i<hitShape.getSize(); i++){
			tempMapShape2.put(i, hitShape.getPointFromId(i));
		}
		this.queryShape = tempMapShape1;
		this.hitShape = tempMapShape2;
		this.algoParameters = algoParameters;
	}



	// -------------------------------------------------------------------
	// Public && Interface method
	// -------------------------------------------------------------------
	public ResultsFromEvaluateCost getCostOfaPairing(PairingAndNullSpaces pairingAndNullSpacesToBeScored){

		return ScorePairingTools.getCostOfaPairing(pairingAndNullSpacesToBeScored, queryShape, hitShape, algoParameters);
	}



	public List<ResultsFromEvaluateCost> getCostOfaListOfPairing(List<PairingAndNullSpaces> listPairingAndNullSpacesToBeScored){

		return ScorePairingTools.getCostOfaListOfPairing(listPairingAndNullSpacesToBeScored, queryShape, hitShape, algoParameters);
	}




	// -------------------------------------------------------------------
	// Implementation
	// -------------------------------------------------------------------
}
