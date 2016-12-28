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

import java.util.LinkedHashMap;
import java.util.Vector;

public class CartesianProductListOfList {
    // -------------------------------------------------------------------
    // Class variables
    // -------------------------------------------------------------------
    private LinkedHashMap<String, Vector<Double>> dataStructure;


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public CartesianProductListOfList(LinkedHashMap<String, Vector<Double>> dataStructure) {
        this.dataStructure = dataStructure;
    }


    //-------------------------------------------------------------
    // Interface & Public methods
    //-------------------------------------------------------------
    public Double[][] allUniqueCombinations() {
        int n = dataStructure.keySet().size();
        int solutions = 1;

        for (Vector<Double> vector : dataStructure.values()) {
            solutions *= vector.size();
        }

        Double[][] allCombinations = new Double[solutions + 1][];
        //allCombinations[0] = dataStructure.keySet().toArray(new Double[n]);

        for (int i = 0; i < solutions; i++) {
            Vector<Double> combination = new Vector<>(n);
            int j = 1;
            for (Vector<Double> vec : dataStructure.values()) {
                combination.add(vec.get((i / j) % vec.size()));
                j *= vec.size();
            }
            allCombinations[i + 1] = combination.toArray(new Double[n]);
        }

        return allCombinations;
    }
}