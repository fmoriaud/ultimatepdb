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
import org.biojava.nbio.structure.io.FileParsingParameters;
import org.biojava.nbio.structure.io.MMCIFFileReader;
import org.biojava.nbio.structure.io.mmcif.DownloadChemCompProvider;
import parameters.AlgoParameters;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class BiojavaReader implements BiojavaReaderIfc {
    // -------------------------------------------------------------------
    // Class variables
    // -------------------------------------------------------------------
    /**
     * Declared as static with a loose instanciation so it it is created only once
     */
    private static DownloadChemCompProvider downloadChemCompProvider = null;

    private AlgoParameters algoParameters;


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------

    /**
     * Class to read a Biojava mmcif file. internet access is needed to get bond orders from cif files.
     * Also work if no internet, then a Biojava default routine is used, not sure results for bond orders will be the same.
     **/
    public BiojavaReader(AlgoParameters algoParameters) {
        this.algoParameters = algoParameters;
    }


    // -------------------------------------------------------------------
    // Public & Interface
    // -------------------------------------------------------------------

    /**
     * Read a mmcif file from the pdb folder
     *
     * @param fourLetterCode
     * @param pathToDividedPDBFolder is used to get the mmcif file corresponding to the given PDB four letter code. Any File Structure is handled:
     *                               usual divided would be handled by Biojava but here all file structure is handled. Case where more than one file
     *                               has the same four letter code in file name, e.g. twice 1di9.cif.gz, both are indexed
     * @param pathToChemcompFolder   is used to get the path to the chemcomp root folder. chemcomp folder
     *                               is automatically created and individual residue cif files are automatically downloaded by Biojava
     *                               individual files stays in the folder after program is terminated.
     * @return
     * @throws IOException
     */
    @Override
    public Structure readFromPDBFolder(String fourLetterCode, String pathToDividedPDBFolder, String pathToChemcompFolder) throws IOException, ExceptionInIOPackage {

        fourLetterCode = fourLetterCode.toUpperCase(); // it can handle uppercase and lowercase
        // done only once
        if (algoParameters.getIndexPDBFileInFolder() == null) {
            algoParameters.setIndexPDBFileInFolder(IOTools.indexPDBFileInFolder(pathToDividedPDBFolder));
        }
        // done only once
        initializeOnceDowloadChemCompProvider(pathToChemcompFolder);

        FileParsingParameters params = getFileParsingParameters();

        Path actualPathToFileInPDBFolder = null;
        if (algoParameters.getIndexPDBFileInFolder().containsKey(fourLetterCode)) {
            // TODO Currently takes the first one found, if more than one the a filter would be needed
            actualPathToFileInPDBFolder = algoParameters.getIndexPDBFileInFolder().get(fourLetterCode).get(0);
        }

        File file = actualPathToFileInPDBFolder.toFile();
        long fileLength = file.length();
        if (fileLength > 20000000) {
            ExceptionInIOPackage exception = new ExceptionInIOPackage("File too big to be handled. Size = " + (fileLength / 1000000) + " MB and max is 20 MB");
            throw exception;
        }
        Structure cifStructure = read(actualPathToFileInPDBFolder, pathToChemcompFolder);
        return cifStructure;
    }


    /**
     * Read a mmcif file from any given path
     * Needed residues cif files are download automatically to chemcomp folder by Biojava
     *
     * @param pathToFile           is the absolute path to the mmcif file to read
     * @param pathToChemcompFolder is used to get the path to the chemcomp root folder. chemcomp folder
     *                             is automatically created and individual residue cif files are automatically downloaded by Biojava
     *                             individual files stays in the folder after program is terminated.
     * @return
     * @throws IOException
     */
    @Override
    public Structure read(Path pathToFile, String pathToChemcompFolder) throws IOException, ExceptionInIOPackage {

        // done only once
        initializeOnceDowloadChemCompProvider(pathToChemcompFolder);
        MMCIFFileReader mMCIFileReader = new MMCIFFileReader();
        FileParsingParameters params = getFileParsingParameters();
        mMCIFileReader.setFileParsingParameters(params);

        File file = pathToFile.toFile();
        long fileLength = file.length();
        if (fileLength > 20000000) {
            ExceptionInIOPackage exception = new ExceptionInIOPackage("File too big to be handled. Size = " + (fileLength / 1000000) + " MB and max is 20 MB");
            throw exception;
        }

        Structure structure = null;
        try {
            structure = mMCIFileReader.getStructure(pathToFile.toString());
        } catch (NumberFormatException e) { // This one can happen like in 5dn6
            ExceptionInIOPackage exception = new ExceptionInIOPackage("NumberFormatException in mMCIFileReader.getStructure()");
            throw exception;
        }

        return structure;
    }


    // -------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------
    private void initializeOnceDowloadChemCompProvider(String pathToChemcompFolder) {
        if (downloadChemCompProvider == null) {
            downloadChemCompProvider = new DownloadChemCompProvider(pathToChemcompFolder);
            // don't know if needed
            downloadChemCompProvider.setDownloadAll(true);

            //downloadChemCompProvider.checkDoFirstInstall();
        }
    }


    private FileParsingParameters getFileParsingParameters() {
        FileParsingParameters params = new FileParsingParameters();
        params.setAlignSeqRes(false);
        params.setParseSecStruc(false);
        //params.setLoadChemCompInfo(true);
        params.setCreateAtomBonds(true);
        return params;
    }


    private static boolean isDirEmpty(final Path directory) throws IOException {
        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory)) {
            return !dirStream.iterator().hasNext();
        }
    }
}