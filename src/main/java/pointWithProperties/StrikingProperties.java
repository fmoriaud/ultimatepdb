package pointWithProperties;

public enum StrikingProperties{
	DEHYDRON ("Dehydron"),
	AROMATICRING ("Aromatic Ring"),
	HYDROPHOBE ("Hydrophobe"),
	POSITIVE_CHARGE ("Positive Charge"),
	NEGATIVE_CHARGE ("Negative Charge"),
	HBOND_DONNOR ("HBond Donnor"),
	HBOND_ACCEPTOR("HBond Acceptor"),
	NONE ("None");

	private String strikingPropertyName;

	StrikingProperties(String strikingPropertyName){
		this.strikingPropertyName = strikingPropertyName;
	}

	@Override public String toString() {
		StringBuilder result = new StringBuilder();

		result.append(strikingPropertyName);

		return result.toString();
	}
	
	
	public String getStrikingPropertyName() {
		return strikingPropertyName;
	}

}
