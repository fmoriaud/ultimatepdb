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
package io;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.MDLV3000Reader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;


/**
 * Created by Fabrice on 07/09/16.
 */
public class CdkTools {
    //-------------------------------------------------------------
    // Static methods
    //-------------------------------------------------------------
    public static IAtomContainer readV3000molFile(String path) {

        IAtomContainer mol = null;
        try {
            MDLV3000Reader reader = new MDLV3000Reader(new FileInputStream(path));
            mol = reader.readMolecule(DefaultChemObjectBuilder.getInstance());
        } catch (CDKException | FileNotFoundException e) {
            e.printStackTrace();
        }
        return mol;
    }
}
