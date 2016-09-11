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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Recursive listing with SimpleFileVisitor in JDK 7.
 */
public class FileListingVisitor {


    //------------------------
    // Class variables
    //------------------------
    private Map<String, List<Path>> indexFiles = new LinkedHashMap<>();


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public FileListingVisitor(String path) throws IOException {

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
            System.out.println("Processing file:" + aFile);

            String fourLetterCode = makeFourLetterCodeFromFileNameForMmcifGzFiles(aFile.getFileName().toString());
            if (fourLetterCode != null) {
                AddToMap.addElementToAMapOfList(indexFiles, fourLetterCode, aFile);
                System.out.println("ok file:" + aFile);
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
    private String makeFourLetterCodeFromFileNameForMmcifGzFiles(String fileName) {

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
        return splitFileName[0];
    }
}
