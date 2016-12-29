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
package mystructure;

public interface MyMonomerIfc {

	MyAtomIfc[] getMyAtoms();
	MyChainIfc[] getNeighboringAminoMyMonomerByRepresentativeAtomDistance();
	void setNeighboringAminoMyMonomerByRepresentativeAtomDistance(MyChainIfc[] neighboringMyMonomerByRepresentativeAtomDistance);
	MyMonomerIfc[] getNeighboringMyMonomerByBond();
	void setNeighboringMyMonomerByBond(MyMonomerIfc[] neighboringMyMonomerByBond);
	char[] getThreeLetterCode();
	int getResidueID();
	char[] getType();
	void setType(char[] type);
	boolean isWasHetatm();
	void setWasHetatm(boolean wasHetatm);
	char getInsertionLetter();
	char getAltLocGroup();
	MyChainIfc getParent();
	void setParent(MyChainIfc parent);
	MyAtomIfc getMyAtomFromMyAtomName(char[] atomName);
	MyAtomIfc getAtomById(int atomId);
	void addAtom(MyAtomIfc atom);
	void setMyAtoms(MyAtomIfc[] myAtoms);
	void deleteAtomAndbonds(MyAtomIfc atom);
}
