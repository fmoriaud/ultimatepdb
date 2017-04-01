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

import convertformat.AdapterBioJavaStructure;
import convertformat.ExceptionInConvertFormat;
import mystructure.ExceptionInMyStructurePackage;
import mystructure.MyStructureIfc;
import mystructure.ReadingStructurefileException;
import org.apache.commons.math3.util.Pair;
import org.biojava.nbio.structure.Structure;
import parameters.AlgoParameters;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class IOTools {
    //-------------------------------------------------------------
    // Static methods
    //-------------------------------------------------------------

    public static Pair<String, MyStructureIfc> getMyStructureIfc(AlgoParameters algoParameters, Path pathToFile) {

        BiojavaReader reader = new BiojavaReader(algoParameters);
        Pair<String, Structure> pairPathStructure = null;
        try {
            pairPathStructure = reader.read(pathToFile, algoParameters.getPATH_TO_CHEMCOMP_FOLDER());
        } catch (IOException | ExceptionInIOPackage e) {
            return null;
        }
        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(pairPathStructure.getValue());
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
            return null;
        }
        Pair<String, MyStructureIfc> pairPathMyStructure = new Pair(pairPathStructure.getKey(), mystructure);
        return pairPathMyStructure;
    }


    /**
     * @param algoParameters
     * @param fourLetterCode that must be lower case
     * @return Can return null
     */
    public static Pair<String, MyStructureIfc> getMyStructureIfc(AlgoParameters algoParameters, char[] fourLetterCode) {
        BiojavaReader reader = new BiojavaReader(algoParameters);
        Pair<String, Structure> pairPathStructure = null;
        try {
            // TODO change to char[]
            pairPathStructure = reader.readFromPDBFolder(String.valueOf(fourLetterCode), algoParameters.getPATH_TO_REMEDIATED_PDB_MMCIF_FOLDER(), algoParameters.getPATH_TO_CHEMCOMP_FOLDER());
        } catch (IOException | ExceptionInIOPackage e) {
            return null;
        }

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(pairPathStructure.getValue());
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
            return null;
        }
        Pair<String, MyStructureIfc> pairPathMyStructure = new Pair(pairPathStructure.getKey(), mystructure);
        return pairPathMyStructure;
    }


    public static Map<String, List<MMcifFileInfos>> indexPDBFileInFolder(String pathToDividedPDBFolder) {

        Map<String, List<MMcifFileInfos>> indexPDBFileInFolder = null;
        try {
            FileListingVisitorForPDBCifGzFiles fileListingVisitor = new FileListingVisitorForPDBCifGzFiles(pathToDividedPDBFolder);
            indexPDBFileInFolder = fileListingVisitor.getIndexFiles();

        } catch (IOException e) {
            System.out.println("FAILURE: in makeAListOfInputPDBFilesRecursivelyFromInputControllerFolder");
            //e.printStackTrace();
        }
        return indexPDBFileInFolder;
    }


    public static Map<String, List<Path>> indexChemcompFileInFolder(String pathToDividedPDBFolder) {

        Map<String, List<Path>> indexPDBFileInFolder = null;
        try {
            FileListingVisitorForChemcompCifGzFiles fileListingVisitor = new FileListingVisitorForChemcompCifGzFiles(pathToDividedPDBFolder);
            indexPDBFileInFolder = fileListingVisitor.getIndexFiles();

        } catch (IOException e) {
            System.out.println("FAILURE: in makeAListOfInputPDBFilesRecursivelyFromInputControllerFolder");
            //e.printStackTrace();
        }
        return indexPDBFileInFolder;
    }
}