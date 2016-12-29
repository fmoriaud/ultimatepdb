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

public interface MyAtomIfc {

    float[] getCoords();

    void setCoords(float[] coords);

    char[] getElement();

    char[] getAtomName();

    MyMonomerIfc getParent();

    void setParent(MyMonomerIfc parent);

    MyBondIfc[] getBonds();

    void setBonds(MyBondIfc[] bonds);

    int getOriginalAtomId();

    void setOriginalAtomId(int originalAtomId);

    void addBond(MyBondIfc bond);

    void removeBond(MyBondIfc bond);
}
