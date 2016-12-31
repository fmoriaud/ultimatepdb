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
package parameters;

public class QueryAtomDefinedByIds {
    //-------------------------------------------------------------
    // Class variables
    //-------------------------------------------------------------
    private String fourLetterCode;
    private String chainQuery;
    private int residueId;
    private String atomName;
    private float radiusForQueryAtomsDefinedByIds;


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public QueryAtomDefinedByIds(String fourLetterCode, String chainQuery, int residueId, String atomName, float radiusForQueryAtomsDefinedByIds) {
        this.fourLetterCode = fourLetterCode;
        this.chainQuery = chainQuery;
        this.residueId = residueId;
        this.atomName = atomName;
        this.radiusForQueryAtomsDefinedByIds = radiusForQueryAtomsDefinedByIds;
    }


    // -------------------------------------------------------------------
    // Override
    // -------------------------------------------------------------------
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(fourLetterCode + " " + chainQuery + " " + residueId + " " + atomName + " " + radiusForQueryAtomsDefinedByIds);
        return sb.toString();
    }


    // -------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------
    public String getFourLetterCode() {
        return fourLetterCode;
    }


    public String getChainQuery() {
        return chainQuery;
    }


    public int getResidueId() {
        return residueId;
    }


    public String getAtomName() {
        return atomName;
    }

    public float getRadiusForQueryAtomsDefinedByIds() {
        return radiusForQueryAtomsDefinedByIds;
    }
}
