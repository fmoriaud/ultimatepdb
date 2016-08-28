package pointWithProperties;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import pointWithProperties.Enum.PropertyName;



public class PointWithProperties implements PointWithPropertiesIfc{

	private PointIfc coords;

	private List<StrikingProperties> strikingProperties;
	private StrikingProperties miniShapeStrikingProperty;

	private float distanceToLigand;
	private float electronProbability;

	private HashMap<PropertyName, Float> propertiesValues;



	public PointWithProperties(){

		this.propertiesValues = new LinkedHashMap<>();
	}


	// -------------------------------------------------------------------
	// Public Interface
	// -------------------------------------------------------------------
	@Override
	public Float get(PropertyName propertyName) {
		return this.propertiesValues.get(propertyName);
	}



	@Override
	public void put(PropertyName propertyName, Float propertyValue) {
		this.propertiesValues.put(propertyName, propertyValue);

	}



	@Override
	public PointIfc getCoords() {
		return coords;
	}

	
	
	@Override
	public void setCoords(PointIfc coords) {
		this.coords = coords;
	}

	
	
	@Override
	public List<StrikingProperties> getStrikingProperties() {
		return strikingProperties;
	}
	
	
	
	@Override
	public void setStrikingProperties(List<StrikingProperties> strikingProperties) {
		this.strikingProperties = strikingProperties;
	}
	
	
	
	@Override
	public float getDistanceToLigand() {
		return distanceToLigand;
	}
	
	
	@Override
	public void setDistanceToLigand(float distanceToLigand) {
		this.distanceToLigand = distanceToLigand;
	}
	
	
	
	@Override
	public float getElectronProbability() {
		return electronProbability;
	}
	
	
	
	@Override
	public void setElectronProbability(float electronProbability) {
		this.electronProbability = electronProbability;
	}

	
	
	@Override
	public StrikingProperties getMiniShapeStrikingProperty() {
		return miniShapeStrikingProperty;
	}
	
	
	@Override
	public void setMiniShapeStrikingProperty(StrikingProperties miniShapeStrikingProperty) {
		this.miniShapeStrikingProperty = miniShapeStrikingProperty;
	}

	
	
	@Override public String toString() {
		StringBuilder result = new StringBuilder();
		String NEW_LINE = System.getProperty("line.separator");

		result.append(distanceToLigand + " A ");
		result.append(" Proba = " + electronProbability);
		result.append(" " + this.getStrikingProperties());
		result.append("}");

		return result.toString();
	}




	// -------------------------------------------------------------------
	// Getter & Setter
	// -------------------------------------------------------------------


	


}
