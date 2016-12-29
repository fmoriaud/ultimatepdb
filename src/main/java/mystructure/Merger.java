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

import parameters.AlgoParameters;

public class Merger {
    //-------------------------------------------------------------
    // Class variables
    //-------------------------------------------------------------
    private MyStructureIfc merge;


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public Merger(MyChainIfc myChain1, MyChainIfc myChain2, AlgoParameters algoParameters) {

        // assume two different amino chain
        MyChainIfc[] aminoChains = new MyChainIfc[2];
        aminoChains[0] = myChain1;
        aminoChains[1] = myChain2;

        Cloner cloner = new Cloner(aminoChains, algoParameters);
        merge = cloner.getClone();

    }


    // -------------------------------------------------------------------
    // Getter and Setter
    // -------------------------------------------------------------------
    public MyStructureIfc getMerge() {
        return merge;
    }
}
