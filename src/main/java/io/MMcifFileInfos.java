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

import java.io.Serializable;
import java.nio.file.Path;

public class MMcifFileInfos implements Serializable {
    // -------------------------------------------------------------------
    // Class variables
    // -------------------------------------------------------------------
    private String pathToFile;
    private String hash;


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public MMcifFileInfos(String pathToFile, String hash){

        this.pathToFile = pathToFile;
        this.hash = hash;
    }

    // -------------------------------------------------------------------
    // Getter and Setter
    // -------------------------------------------------------------------
    public String getHash() {
        return hash;
    }

    public String getPathToFile() {
        return pathToFile;
    }
}
