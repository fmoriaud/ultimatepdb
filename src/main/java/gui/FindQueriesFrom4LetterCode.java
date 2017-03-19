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
package gui;

import io.IOTools;
import mystructure.MyChainIfc;
import mystructure.MyMonomerIfc;
import mystructure.MyStructureIfc;
import parameters.AlgoParameters;

import java.util.ArrayList;
import java.util.List;

public class FindQueriesFrom4LetterCode {
    //------------------------------------------------------------------------------
    // DATA MEMBERS
    //------------------------------------------------------------------------------
    private String fourLetterCode;
    private AlgoParameters algoParameters;



    //------------------------------------------------------------------------------
    // CONSTRUCTORS
    //------------------------------------------------------------------------------
    public FindQueriesFrom4LetterCode(String fourLetterCode, AlgoParameters algoParameters) {

        this.fourLetterCode = fourLetterCode;
        this.algoParameters = algoParameters;
    }



    //-------------------------------------------------------------
    // Public & Override methods
    //-------------------------------------------------------------
    public String[] getChains(){

        List<String> aminoChain = new ArrayList<>();

        MyStructureIfc myStructure = IOTools.getMyStructureIfc(algoParameters, fourLetterCode.toCharArray());
        MyChainIfc[] aminoChains = myStructure.getAllAminochains();

        for (MyChainIfc chain: aminoChains){
            aminoChain.add(String.valueOf(chain.getChainId()));
        }
        return aminoChain.toArray(new String[aminoChain.size()]);
    }
}
