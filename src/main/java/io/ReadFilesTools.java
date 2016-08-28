package io;

import org.biojava.nbio.structure.io.FileParsingParameters;
import org.biojava.nbio.structure.io.MMCIFFileReader;
import org.biojava.nbio.structure.io.mmcif.DownloadChemCompProvider;

import parameters.AlgoParameters;

public class ReadFilesTools {



	// TODO to be tested, suspicious now as I have updated the BioJava library
	public static MMCIFFileReader prepareMmcifReader(AlgoParameters algoParameters){


		MMCIFFileReader mMCIFileReader = new MMCIFFileReader();
		mMCIFileReader.setAutoFetch(false);
		mMCIFileReader.setPath(algoParameters.getPATH_TO_REMEDIATED_PDB_MMCIF_FOLDER());
		//mMCIFileReader.setPdbDirectorySplit(true);

		FileParsingParameters params = new FileParsingParameters();
		params.setAlignSeqRes(false);
		params.setParseSecStruc(false);
		//params.setLoadChemCompInfo(true);

		//DownloadChemCompProvider.setPath(algoParameters.getPATH_TO_CHEMCOMP_FOLDER());
		DownloadChemCompProvider c = new DownloadChemCompProvider();
		c.setDownloadAll(true);
		c.checkDoFirstInstall();
		//		params.setParseBioAssembly(true);
		//		System.out.println("params.isParseBioAssembly() = " + params.isParseBioAssembly());

		//StructureIO.setPdbPath(algoParameters.getPATH_TO_TEMP_PDB_FILES());

		mMCIFileReader.setFileParsingParameters(params);

		return mMCIFileReader;
	}
}
