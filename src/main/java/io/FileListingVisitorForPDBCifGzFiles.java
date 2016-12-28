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

import math.AddToMap;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Recursive listing with SimpleFileVisitor in JDK 7.
 */
public class FileListingVisitorForPDBCifGzFiles {
    // -------------------------------------------------------------------
    // Class variables
    // -------------------------------------------------------------------
    private Map<String, List<Path>> indexFiles = new LinkedHashMap<>();


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public FileListingVisitorForPDBCifGzFiles(String path) throws IOException {

        FileVisitor<Path> fileProcessor = new ProcessFile();
        Files.walkFileTree(Paths.get(path), fileProcessor);
    }


    // -------------------------------------------------------------------
    // Public & Interface Methods
    // -------------------------------------------------------------------
    public Map<String, List<Path>> getIndexFiles() {

        return indexFiles;
    }


    // -------------------------------------------------------------------
    // Implementation & Private methods
    // -------------------------------------------------------------------
    private class ProcessFile extends SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult visitFile(Path aFile, BasicFileAttributes aAttrs) throws IOException {

            String fourLetterCode = makeFourLetterCodeUpperCaseFromFileNameForMmcifGzFiles(aFile.getFileName().toString());
            if (fourLetterCode != null) {
                AddToMap.addElementToAMapOfList(indexFiles, fourLetterCode, aFile);
            }

            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path aDir, BasicFileAttributes aAttrs) throws IOException {
            //System.out.println("Processing directory:" + aDir);
            return FileVisitResult.CONTINUE;
        }
    }


    /**
     * Get the four letter code out of the file name: e.g. 1di9.cif.gz returns 1di9
     * Only works for .cif.gz files, otherwise return null
     *
     * @param fileName
     * @return
     */
    private String makeFourLetterCodeUpperCaseFromFileNameForMmcifGzFiles(String fileName) {

        String[] splitFileName = fileName.split("\\.");

        if (splitFileName == null){
            return null;
        }
        if (splitFileName.length != 3) {
            return null;
        }
        if (!splitFileName[1].equals("cif")) {
            return null;
        }
        if (!splitFileName[2].equals("gz")) {
            return null;
        }
        if (splitFileName[0].length() != 4) {
            return null;
        }
        return splitFileName[0].toUpperCase();
    }
}
