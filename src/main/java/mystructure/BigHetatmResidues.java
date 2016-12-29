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

import java.util.Arrays;

/**
 * Residues defined here as BigHetatmResidues are considered as part of the environnement, not as a ligand
 * All fixed prosthetic groups should be defined here
 */
public enum BigHetatmResidues {

    HEM("HEM");

    private String threeLetterCode;

    BigHetatmResidues(String threeLetterCode) {
        this.threeLetterCode = threeLetterCode;
    }

    public char[] getThreeLetterCode() {
        return threeLetterCode.toCharArray();
    }


    /**
     * Return true is the myMonomer is a big residue
     * Big residue is defined by the three letter code, currently only HEM
     *
     * @param myMonomer
     * @return
     */
    public static boolean isMyMonomerABigResidue(MyMonomerIfc myMonomer) {

        for (BigHetatmResidues bigHetatmResidues : BigHetatmResidues.values()) {
            if (Arrays.equals(bigHetatmResidues.threeLetterCode.toCharArray(), myMonomer.getThreeLetterCode())) {
                return true;
            }
        }
        return false;
    }
}
