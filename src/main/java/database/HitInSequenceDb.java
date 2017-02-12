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
package database;

import java.util.List;

public class HitInSequenceDb {
    //-------------------------------------------------------------
    // Class variables
    //-------------------------------------------------------------
    private List<Integer> listRankIds;
    private String fourLetterCode;
    private String chainIdFromDB;
    private int peptideLength;


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public HitInSequenceDb(List<Integer> listRankIds, String fourLetterCode, String chainIdFromDB, int peptideLength) {
        this.listRankIds = listRankIds;
        this.fourLetterCode = fourLetterCode;
        this.chainIdFromDB = chainIdFromDB;
        this.peptideLength = peptideLength;
    }


    // -------------------------------------------------------------------
    // override
    // -------------------------------------------------------------------
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(fourLetterCode + " " + chainIdFromDB + " " + peptideLength + " ");
        for (Integer rankid : listRankIds) {
            sb.append(rankid + " ");
        }
        return sb.toString();
    }


    // -------------------------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------------------------
    public List<Integer> getListRankIds() {
        return listRankIds;
    }


    public String getFourLetterCode() {
        return fourLetterCode;
    }


    public String getChainIdFromDB() {
        return chainIdFromDB;
    }


    public int getPeptideLength() {
        return peptideLength;
    }
}