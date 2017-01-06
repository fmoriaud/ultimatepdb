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
package shapeBuilder;

import java.util.Comparator;

import mystructure.MyMonomerIfc;

public class MyMonomerIfcComparatorIncreasingResidueId implements Comparator<MyMonomerIfc>{

	@Override
	public int compare(MyMonomerIfc monomer1, MyMonomerIfc monomer2) {
		int monomer1ID = monomer1.getResidueID();
		int monomer2ID = monomer2.getResidueID();

		return (monomer2ID>monomer1ID ? -1 : (monomer1ID==monomer2ID ? 0 : 1));
	}
}
