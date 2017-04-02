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

import java.io.*;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class Controller {

    public static String pathToSerFile = "//Users//Fabrice//Documents//ultimate//index.ser";
    /**
     * The indexing of files is serialized and saved to disk
     * In case the user wants to reuse it assuming the files were not changed
     */
    //------------------------------------------------------------------------------
    // DATA MEMBERS
    //------------------------------------------------------------------------------
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
    public int updatePDBFileFoldersAndIndexing(String pathToPDBFolder, boolean useSerfileIfExists) {


        Map<String, List<MMcifFileInfos>> indexPDBFileInFolder = null;
        // check if map is serialized
        File file = new File(pathToSerFile);
        if (useSerfileIfExists && file.exists()) {

            try {
                FileInputStream fin = new FileInputStream(file.getAbsolutePath());
                ObjectInputStream ois = new ObjectInputStream(fin);
                indexPDBFileInFolder = (Map<String, List<MMcifFileInfos>>) ois.readObject();
                System.out.println("From ser file " + indexPDBFileInFolder.size());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            indexPDBFileInFolder = IOTools.indexPDBFileInFolder(pathToPDBFolder);
            System.out.println("From reindexing " + indexPDBFileInFolder.size());
        }

        // serialize map
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(
                    new FileOutputStream(pathToSerFile));
            oos.writeObject(indexPDBFileInFolder);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        algoParameters.setIndexPDBFileInFolder(indexPDBFileInFolder);

        algoParameters.setPATH_TO_REMEDIATED_PDB_MMCIF_FOLDER(pathToPDBFolder);

        if (algoParameters.getIndexPDBFileInFolder() == null) {
            return 0;
        }

        return algoParameters.getIndexPDBFileInFolder().size();
    }


    // -------------------------------------------------------------------
    // Getter and Setter
    // -------------------------------------------------------------------
    public AlgoParameters getAlgoParameters() {

        return algoParameters;
    }
}
