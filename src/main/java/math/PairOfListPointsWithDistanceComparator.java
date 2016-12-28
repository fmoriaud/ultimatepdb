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
package math;

import java.util.Comparator;

import shapeReduction.PairOfListPointsWithDistance;

public class PairOfListPointsWithDistanceComparator implements Comparator<PairOfListPointsWithDistance>{

	@Override
	public int compare(PairOfListPointsWithDistance pair1, PairOfListPointsWithDistance pair2) {

		if (pair1.getDistance() < pair2.getDistance()){
			return -1;
		}
		if (pair1.getDistance() > pair2.getDistance()){
			return 1;
		}
		return 0;
	}
}