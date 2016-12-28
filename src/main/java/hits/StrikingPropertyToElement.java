/*
Author:
      Fabrice Moriaud <fmoriaud@ultimatepdb.org>

  Copyright (c) 2016 Fabrice Moriaud

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
  */
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
	AROMATICRING (StrikingProperties.AROMATICRING, Element.P),
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
