package hits;

import org.biojava.nbio.structure.Element;

import pointWithProperties.StrikingProperties;

public enum StrikingPropertyToElement {

	HYDROPHOBE (StrikingProperties.HYDROPHOBE, Element.C),
	POSITIVE_CHARGE (StrikingProperties.POSITIVE_CHARGE, Element.N),
	NEGATIVE_CHARGE (StrikingProperties.NEGATIVE_CHARGE, Element.O),
	HBOND_DONNOR (StrikingProperties.HBOND_DONNOR, Element.Cl),
	HBOND_ACCEPTOR (StrikingProperties.HBOND_ACCEPTOR, Element.S),
	DEHYDRON (StrikingProperties.DEHYDRON, Element.Br),
	AROMATICRING (StrikingProperties.AROMATICRING, Element.H),
	NONE (StrikingProperties.NONE, Element.I);
	
	private final StrikingProperties strikingProperties ;
	private final Element element ;
	
	StrikingPropertyToElement (StrikingProperties strikingProperties, Element element){
		this.strikingProperties = strikingProperties;
		this.element = element;
	}
	
	
	
	public static Element getAtomSymbol(StrikingProperties strikingProperties){
		
		for (StrikingPropertyToElement strikingPropertyToAtomSymbol: StrikingPropertyToElement.values()){
			if (strikingProperties.equals(strikingPropertyToAtomSymbol.strikingProperties)){
				return strikingPropertyToAtomSymbol.element;
			}
		}
		return Element.Ag;
	}
}
