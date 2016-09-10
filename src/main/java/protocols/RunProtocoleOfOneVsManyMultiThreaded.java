package protocols;

import java.io.IOException;
import java.util.logging.FileHandler;

import database.UpdateSequenceDatabaseMultithreaded;
import genericBuffer.GenericBuffer;
import genericBuffer.MyStructureBuffer;
import math.ProcrustesAnalysisIfc;
import multithread.ProtocolOneVsManyMultiThreaded;
import parameters.AlgoParameters;
import shapeCompare.ProcrustesAnalysis;
import mystructure.EnumMyReaderBiojava;
import ultiJmol1462.UltiJmol1462;

public class RunProtocoleOfOneVsManyMultiThreaded {

	public static void main(String[] args) throws CommandLineException, ParsingConfigFileException {
		EnumMyReaderBiojava enumMyReaderBiojava = EnumMyReaderBiojava.BioJava_MMCIFF;
		AlgoParameters algoParameters = CommandLineTools.analyzeArgs(args, enumMyReaderBiojava);

		algoParameters.ultiJMolBuffer = new GenericBuffer<UltiJmol1462>(algoParameters.getSHAPE_COMPARISON_THREAD_COUNT());
		algoParameters.procrustesAnalysisBuffer = new GenericBuffer<ProcrustesAnalysisIfc>(algoParameters.getSHAPE_COMPARISON_THREAD_COUNT());
		algoParameters.myStructureBuffer = new MyStructureBuffer(algoParameters.getSHAPE_COMPARISON_THREAD_COUNT() * 2);
		algoParameters.listOfPDBFiles = ProtocolToolsToHandleInputFilesAndShapeComparisons.makeAListOfInputPDBFilesRecursivelyFromInputControllerFolder(algoParameters, enumMyReaderBiojava);

		for (int i=0; i<algoParameters.getSHAPE_COMPARISON_THREAD_COUNT(); i++){
			ProcrustesAnalysisIfc procrustesAnalysis = new ProcrustesAnalysis(algoParameters);
			try {
				algoParameters.procrustesAnalysisBuffer.put(procrustesAnalysis);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for (int i=0; i<algoParameters.getSHAPE_COMPARISON_THREAD_COUNT(); i++){
			UltiJmol1462 ultiJMol = new UltiJmol1462();
			try {
				algoParameters.ultiJMolBuffer.put(ultiJMol);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


		if (algoParameters.isBUILD_SEQ_DB()){
			UpdateSequenceDatabaseMultithreaded updateSequenceDatabase = new UpdateSequenceDatabaseMultithreaded(algoParameters, enumMyReaderBiojava);
			try {
				updateSequenceDatabase.updateDatabase();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println("Database created. Exit Program.");
			System.exit(0);
		}

		FileHandler fh = null;
		try {
			fh = new FileHandler(algoParameters.getPATH_TO_RESULT_FILES() + "log_Project.txt");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//		System.out.println("wait 1 minute ... ");
		//		try {
		//			Thread.sleep(60000);
		//		} catch (InterruptedException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}

		fh.setFormatter(new OptimizerFormater());  
		ControllerLoger.logger.addHandler(fh);

		ProtocolOneVsManyMultiThreaded protocolOneVsManyMultiThreaded = new ProtocolOneVsManyMultiThreaded(algoParameters, enumMyReaderBiojava);
		protocolOneVsManyMultiThreaded.run();

		//System.exit(0);
	}



	
}
