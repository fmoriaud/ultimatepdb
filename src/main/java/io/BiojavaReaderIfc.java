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


import org.biojava.nbio.structure.Structure;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by Fabrice on 05/09/16.
 */
public interface BiojavaReaderIfc {

    Structure read(Path pathToFile, String pathToChemcompFolder) throws IOException, ExceptionInIOPackage;
    Structure readFromPDBFolder(String fourLetterCode, String pathToDividedPDBFolder, String pathToChemcompFolder) throws IOException, ExceptionInIOPackage;
    }
