package pointWithProperties;

import java.util.List;

import pointWithProperties.Enum.PropertyName;


public interface PointWithPropertiesIfc {


	Float get(PropertyName propertyName);
	void put(PropertyName propertyName, Float propertyValue);
	PointIfc getCoords();
	void setCoords(PointIfc coords);
	List<StrikingProperties> getStrikingProperties();
	void setStrikingProperties(List<StrikingProperties> strikingProperties);
	float getDistanceToLigand();
	void setDistanceToLigand(float distanceToLigand);
	float getElectronProbability();
	void setElectronProbability(float electronProbability);

	StrikingProperties getMiniShapeStrikingProperty();
	void setMiniShapeStrikingProperty(StrikingProperties miniShapeStrikingProperty);
}
