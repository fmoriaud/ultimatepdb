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
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;
import protocols.ProtocolTools;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class Controller {
    //------------------------------------------------------------------------------
    // DATA MEMBERS
    //------------------------------------------------------------------------------
    // TODO put indexPDBFileInFolder in algoParameters
    private AlgoParameters algoParameters;


    /**
     * The Controller takes all GUI input and process them
     * So JUnit tests can be done on the Controller
     */
    //------------------------------------------------------------------------------
    // CONSTRUCTORS
    //------------------------------------------------------------------------------
    public Controller() {

        try {
            algoParameters = ProtocolTools.prepareAlgoParameters();
        } catch (ParsingConfigFileException e) {
            e.printStackTrace();
        }

    }

    //-------------------------------------------------------------
    // Public & Override methods
    //-------------------------------------------------------------
    public Integer countPDBFiles(String pathToPDBFolder) {

        Map<String, List<Path>> indexPDBFileInFolder = IOTools.indexPDBFileInFolder(pathToPDBFolder);
        if (indexPDBFileInFolder == null) {
            return 0;
        }
        return indexPDBFileInFolder.size();
    }


    public AlgoParameters getAlgoParameters() {

        return algoParameters;
    }
}
