package protocols;

import java.io.IOException;
import java.util.logging.FileHandler;

import calibration.CompareShapeWithSimulatedShapes;
import genericBuffer.GenericBuffer;
import genericBuffer.MyStructureBuffer;
import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import math.ProcrustesAnalysisIfc;
import parameters.AlgoParameters;
import shapeBuilder.ShapeBuildingException;
import shapeCompare.ProcrustesAnalysis;
import mystructure.EnumMyReaderBiojava;
import ultiJmol1462.MyJmol1462;

public class RunCalibrationProtocol {

	public static void main(String[] args) throws CommandLineException, ParsingConfigFileException, ShapeBuildingException {
		
		EnumMyReaderBiojava enumMyReaderBiojava = EnumMyReaderBiojava.BioJava_MMCIFF;

		AlgoParameters algoParameters = CommandLineTools.analyzeArgs(args, enumMyReaderBiojava);
		algoParameters.setRADIUS_FOR_QUERY_ATOMS_DEFINED_BY_IDS(7.0);
		algoParameters.ultiJMolBuffer = new GenericBuffer<MyJmol1462>(algoParameters.getSHAPE_COMPARISON_THREAD_COUNT());
		algoParameters.procrustesAnalysisBuffer = new GenericBuffer<ProcrustesAnalysisIfc>(algoParameters.getSHAPE_COMPARISON_THREAD_COUNT());
		algoParameters.myStructureBuffer = new MyStructureBuffer(algoParameters.getSHAPE_COMPARISON_THREAD_COUNT() * 2);

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
			MyJmol1462 ultiJMol = new MyJmol1462();
			try {
				algoParameters.ultiJMolBuffer.put(ultiJMol);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
		fh.setFormatter(new OptimizerFormater());  
		ControllerLoger.logger.addHandler(fh);
		
		CompareShapeWithSimulatedShapes compareShapeWithSimulatedShapes = new CompareShapeWithSimulatedShapes(algoParameters, enumMyReaderBiojava);
		try {
			compareShapeWithSimulatedShapes.run();
		} catch (ExceptionInScoringUsingBioJavaJMolGUI e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Program finished");
	}

}
