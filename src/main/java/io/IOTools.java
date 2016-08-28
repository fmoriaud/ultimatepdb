package io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.biojava.nbio.core.util.InputStreamProvider;
import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.io.FileParsingParameters;
import org.biojava.nbio.structure.io.mmcif.MMcifParser;
import org.biojava.nbio.structure.io.mmcif.SimpleMMcifConsumer;
import org.biojava.nbio.structure.io.mmcif.SimpleMMcifParser;

import parameters.AlgoParameters;
import structure.EnumMyReaderBiojava;
import structure.ExceptionInMyStructurePackage;
import structure.MyChainIfc;
import structure.MyMonomerIfc;
import structure.MyReaderIfc;
import structure.MyStructureIfc;
import structure.ReadingStructurefileException;
import structure.StructureReaderMode;

public class IOTools {

	private static EnumMyReaderBiojava lastEnumMyReaderBiojava;
	private static MyReaderBiojava myReaderBiojava;

	/**
	 * Read a MMcif file. Tested with cif.gz
	 * @param path is the full path of the file to read
	 * @return Structure which is a BioJava object to storing the Structure
	 * @throws ExceptionInIOPackage 
	 */
	public static Structure readMMCIFFile(Path path ) throws ExceptionInIOPackage{

		MMcifParser parser = new SimpleMMcifParser();
		SimpleMMcifConsumer consumer = new SimpleMMcifConsumer();

		FileParsingParameters params = new FileParsingParameters();
		params.setAlignSeqRes(false);
		params.setParseSecStruc(false);

		consumer.setFileParsingParameters(params);
		parser.addMMcifConsumer(consumer);

		InputStream inStream = null;
		InputStreamProvider isp = new InputStreamProvider();
		try {
			inStream = isp.getInputStream(path.toFile());
		} catch (IOException e) {
			throw new ExceptionInIOPackage(e.getMessage());
		}

		try {
			parser.parse(new BufferedReader(new InputStreamReader(inStream)));
		} catch (IOException e) {
			throw new ExceptionInIOPackage(e.getMessage());
		}
		Structure cifStructure = consumer.getStructure();
		return cifStructure;
	}

	

	public static boolean isFileOnDividedFoldersMoreRecentThanThisDate(Timestamp lastmodificationTimeInSequenceDatabase, char[] fourLetterCode, AlgoParameters algoParameters, EnumMyReaderBiojava enumMyReaderBiojava){

		try {
			Path pathToRead = findFirstPathCorrespondingToThisFourLetterCode(fourLetterCode, algoParameters, enumMyReaderBiojava);
			BasicFileAttributes attr = Files.readAttributes(pathToRead, BasicFileAttributes.class);
			//			System.out.println("creationTime: " + attr.creationTime());
			//			System.out.println("lastAccessTime: " + attr.lastAccessTime());
			//			System.out.println("lastModifiedTime: " + attr.lastModifiedTime());
			FileTime lastModificationtimeOfFileInDividedFolders = attr.lastModifiedTime();

			// now need to compare FileTime from nio2 to Timestamp from Database
			long timeFromLocalPDB = lastModificationtimeOfFileInDividedFolders.toMillis();
			Timestamp timeStampOfFileFromLocalPDB = new Timestamp(timeFromLocalPDB);

			System.out.println("was " + timeStampOfFileFromLocalPDB + " now is " + lastmodificationTimeInSequenceDatabase);
			boolean weHaveAMoreRecentFile = timeStampOfFileFromLocalPDB.after(lastmodificationTimeInSequenceDatabase);

			if (weHaveAMoreRecentFile == true){
				return true;
			}

		} catch (IOException | ExceptionInMyStructurePackage e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}



	public static MyStructureIfc getMyStructures(char[] fourLetterCode, AlgoParameters algoParameters, EnumMyReaderBiojava enumMyReaderBiojava, StructureReaderMode structureReaderMode) throws ReadingStructurefileException, ExceptionInMyStructurePackage{

		Path pathToRead = findFirstPathCorrespondingToThisFourLetterCode(fourLetterCode, algoParameters, enumMyReaderBiojava);
		MyReaderIfc myReader = getMyReader(algoParameters, enumMyReaderBiojava, structureReaderMode);

		MyStructureIfc myReadStructure = null;
		try{
			myReadStructure = myReader.read(pathToRead, fourLetterCode);

		}catch(ReadingStructurefileException e){
			String message = "ReadingStructurefileException in getMyStructures " + String.valueOf(fourLetterCode) + "  " + e.getMessage();
			ExceptionInMyStructurePackage exception = new ExceptionInMyStructurePackage(message); // overdone but for safety
			throw exception;
		}
		if (myReadStructure == null){ // the reader is not throwing exception
			String message = "getMyStructures failed because of MyReader failure concerning  " + String.valueOf(fourLetterCode);
			ExceptionInMyStructurePackage exception = new ExceptionInMyStructurePackage(message);
			throw exception;
		}
		myReadStructure.setFourLetterCode(fourLetterCode); // hack for biounit files

		return myReadStructure;
	}



	static Path findFirstPathCorrespondingToThisFourLetterCode(char[] fourLetterCode, AlgoParameters algoParameters, EnumMyReaderBiojava enumMyReaderBiojava) throws ExceptionInMyStructurePackage {

		List<Path> pathsToRead = getAPDBFilePathFromListPath(algoParameters.listOfPDBFiles, fourLetterCode);
		// according to choice made I go to the appropriate folder
		if (pathsToRead.size() == 0){
			String message = "getMyStructures failed because there are no such file with name like:  " + String.valueOf(fourLetterCode);
			ExceptionInMyStructurePackage exception = new ExceptionInMyStructurePackage(message);
			throw exception;
		}
		Path onlyFirstPathFound = pathsToRead.get(0);
		return onlyFirstPathFound;
	}



	private static List<Path> getAPDBFilePathFromListPath(List<Path> listPath, char[] fourLetterCode){

		List<Path> listRelevantPath = new ArrayList<>();
		for (Path path: listPath){
			if (path.toString().contains(String.valueOf(fourLetterCode)) || path.toString().contains(String.valueOf(fourLetterCode).toLowerCase())){
				listRelevantPath.add(path);
			}
		}
		return listRelevantPath;
	}



	private static MyReaderIfc getMyReader(AlgoParameters algoParameters, EnumMyReaderBiojava enumMyReaderBiojava, StructureReaderMode structureReaderMode){

		// depending on the path I choose the Reader
		if ( enumMyReaderBiojava.equals(EnumMyReaderBiojava.BioJava_MMCIFF)){
			if (lastEnumMyReaderBiojava != enumMyReaderBiojava){
				lastEnumMyReaderBiojava = enumMyReaderBiojava;
				myReaderBiojava =  new MyReaderBiojava(algoParameters, EnumMyReaderBiojava.BioJava_MMCIFF);
			}
			return myReaderBiojava;
		}
		return null;
	}


	private static void debugCheckIfAllMonomerHAsTheCorrectReferenceToParentThatIsForCheckingInsertedResidues(MyStructureIfc myStructure){

		for (MyChainIfc myChain: myStructure.getAllChains()){
			for (MyMonomerIfc myMonomer: myChain.getMyMonomers()){
				if (myMonomer.getParent() != myChain){
					System.out.println("problem in parent reference");
					System.out.println();
				}
			}
		}
	}
}