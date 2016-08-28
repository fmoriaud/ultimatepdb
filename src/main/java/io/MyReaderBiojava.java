package io;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.io.MMCIFFileReader;

import convertformat.AdapterBioJavaStructure;
import parameters.AlgoParameters;
import structure.EnumMyReaderBiojava;
import structure.ExceptionInMyStructurePackage;
import structure.MyReaderIfc;
import structure.MyStructureIfc;
import structure.ReadingStructurefileException;



public class MyReaderBiojava implements MyReaderIfc{
	//------------------------
	// Class variables
	//------------------------
	private EnumMyReaderBiojava enumMyReaderBiojava;
	private AlgoParameters algoParameters;

	private AdapterBioJavaStructure adapterBioJavaStructure;

	private List<String> listProblematicPDBFourLettercode = new ArrayList<>();

	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public MyReaderBiojava(AlgoParameters algoParameters, EnumMyReaderBiojava enumMyReaderBiojava){
		this.algoParameters = algoParameters;
		this.enumMyReaderBiojava = enumMyReaderBiojava;
		this.adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
	}




	// -------------------------------------------------------------------
	// Public & Interface Methods
	// -------------------------------------------------------------------
	@Override
	public synchronized MyStructureIfc read(Path path, char[] fourLetterCode) throws ReadingStructurefileException, ExceptionInMyStructurePackage{

		MyStructureIfc myStructure = null; // algoParameters.myStructureBuffer.getStructure(fourLetterCode);
		
		if (myStructure == null){

			if (enumMyReaderBiojava == EnumMyReaderBiojava.BioJava_MMCIFF){
				Structure structure = readMMCIFFStructureFromFile(path);

				if (structure == null){ 
					// then nothing to do, problem in the file
					String message = "no structure object was parsed for " + String.valueOf(fourLetterCode);
					ReadingStructurefileException exception = new ReadingStructurefileException(message);
					//there still can be hetatom with only one atom because not resolved completely ...
					throw exception;
				}

				myStructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(structure, enumMyReaderBiojava);
				myStructure.setFourLetterCode(fourLetterCode);
				//algoParameters.myStructureBuffer.putStructure(myStructure);
			}
		}

		return myStructure;
	}



	public synchronized Structure readMMCIFFStructureFromFile(Path path){

		//System.out.println("I read this pdb file with PDBReaderBioJava from biojava"); 
		
		MMCIFFileReader mMCIFileReader = ReadFilesTools.prepareMmcifReader(algoParameters);

		Structure structure = null;

		//		System.out.println("path ="  + path);
		//		System.out.println("namecount = " + path.getNameCount());
		//		System.out.println("name 5 = " + path.getName(6) );
		//		System.out.println("fileName = " + path.getFileName());

		String twoLettercode = "";
		int pathNameCount = path.getNameCount();
		for (int i=0; i<pathNameCount; i++){
			Path currentSubPath = path.getName(i);
			if (currentSubPath.toString().contains("cif.gz")){
				twoLettercode = path.getName(i-1).toString();
			}
		}

		String actualPathToRead = algoParameters.getPATH_TO_REMEDIATED_PDB_MMCIF_FOLDER() + twoLettercode + "/" + path.getFileName();
		try {

			if (listProblematicPDBFourLettercode.contains(path.getFileName().toString())){
				structure = null;
			}else{
				System.out.println(path.getFileName());
				structure = mMCIFileReader.getStructure(actualPathToRead);
				int countOfChains = structure.getChains().size();
				if (countOfChains > 300){ // like 3J3Y has 1176 chains and sequence generation for DB is never ending so I skip. TODO Better would be to consider the n first chains
					listProblematicPDBFourLettercode.add(path.getFileName().toString());
					structure = null;
				}
			}
			//System.out.println();

		} catch (IOException e) {
			//e.printStackTrace();
			System.out.println("FAILURE: BioJavaReader failed for this PDB file with MMCIFFileReader");
		} finally{
			mMCIFileReader = null;
		}
		return structure;
	}
}
