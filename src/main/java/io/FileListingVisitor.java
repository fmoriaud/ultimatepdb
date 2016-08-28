package io;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/** Recursive listing with SimpleFileVisitor in JDK 7. */
public class FileListingVisitor {
	//------------------------
	// Class variables
	//------------------------
	private List<Path> listOfFilesToReturn = new ArrayList<Path>();




	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public FileListingVisitor(String path) throws IOException{

		FileVisitor<Path> fileProcessor = new ProcessFile();
		Files.walkFileTree(Paths.get(path), fileProcessor);

	}




	// -------------------------------------------------------------------
	// Public & Interface Methods
	// -------------------------------------------------------------------
	public List<Path> getListOfFilesToReturn() {
		return listOfFilesToReturn;
	}




	// -------------------------------------------------------------------
	// Implementation & Private methods
	// -------------------------------------------------------------------
	private class ProcessFile extends SimpleFileVisitor<Path> {
		@Override public FileVisitResult visitFile( Path aFile, BasicFileAttributes aAttrs ) throws IOException {
			//System.out.println("Processing file:" + aFile);
			listOfFilesToReturn.add(aFile);

			return FileVisitResult.CONTINUE;
		}

		@Override  public FileVisitResult preVisitDirectory( Path aDir, BasicFileAttributes aAttrs ) throws IOException {
			//System.out.println("Processing directory:" + aDir);
			return FileVisitResult.CONTINUE;
		}
	}




} 
