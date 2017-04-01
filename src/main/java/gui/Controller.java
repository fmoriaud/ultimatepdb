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
import io.MMcifFileInfos;
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
    public int updatePDBFileFoldersAndIndexing(String pathToPDBFolder) {

        Map<String, List<MMcifFileInfos>> indexPDBFileInFolder = IOTools.indexPDBFileInFolder(pathToPDBFolder);
        algoParameters.setIndexPDBFileInFolder(indexPDBFileInFolder);

        algoParameters.setPATH_TO_REMEDIATED_PDB_MMCIF_FOLDER(pathToPDBFolder);

        if (algoParameters.getIndexPDBFileInFolder() == null) {
            return 0;
        }

        return algoParameters.getIndexPDBFileInFolder().size();
    }


    public void updateSequenceDBaccordingtoMMcifFiles() {

        // need to have something to check if the mmcif files indexed was not changed

        // need to know if the parsed PDB files returned nothing so I wouldnt do again if file not changed
    }


    // -------------------------------------------------------------------
    // Getter and Setter
    // -------------------------------------------------------------------
    public AlgoParameters getAlgoParameters() {

        return algoParameters;
    }
}
