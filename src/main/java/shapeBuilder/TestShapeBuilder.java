package shapeBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;

import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import parameters.AlgoParameters;
import protocols.CommandLineException;
import protocols.CommandLineTools;
import protocols.ControllerLoger;
import protocols.OptimizerFormater;
import protocols.ParsingConfigFileException;
import shape.ShapeContainer;
import shape.ShapeContainerWithLigand;
import shape.ShapeContainerWithPeptide;
import mystructure.EnumMyReaderBiojava;
import mystructure.ExceptionInMyStructurePackage;
import mystructure.MyStructureIfc;
import mystructure.ReadingStructurefileException;

public class TestShapeBuilder {

	public static void main(String[] args) throws ExceptionInScoringUsingBioJavaJMolGUI, ReadingStructurefileException, ExceptionInMyStructurePackage, CommandLineException, ParsingConfigFileException, ShapeBuildingException {

		EnumMyReaderBiojava enumMyReaderBiojava = EnumMyReaderBiojava.BioJava_MMCIFF;
		AlgoParameters algoParameters = CommandLineTools.analyzeArgs(args, enumMyReaderBiojava);

		//StaticObjects.biojavaJmol.getFrame().setVisible(true);

		FileHandler fh = null;
		try {
			fh = new FileHandler(algoParameters.getPATH_TO_RESULT_FILES() + "log_Project_bidon.txt");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		fh.setFormatter(new OptimizerFormater());  
		ControllerLoger.logger.addHandler(fh);
		algoParameters.setQUERY_PDB_FOUR_LETTER_CODE("2QLJ");
		MyStructureIfc query = null ; // IOTools.getMyStructures(algoParameters.getQUERY_PDB_FOUR_LETTER_CODE().toCharArray(), algoParameters, enumMyReaderBiojava, StructureReaderMode.ReadyForShapeComputation);
		System.out.println(query.getFourLetterCode());

		System.out.println("Structure read successfully");

		ShapeBuilder shapeBuilderRefactored = new ShapeBuilder(query, algoParameters, EnumShapeReductor.CLUSTERING);
		
		long startTimeMs = System.currentTimeMillis();
		ShapeContainerWithLigand shape = shapeBuilderRefactored.getShapeAroundAHetAtomLigand("CIT".toCharArray(), 1);
		long taskTimeMs  = System.currentTimeMillis( ) - startTimeMs;
		System.out.println("computational time for HetAtmLigand " + (double) taskTimeMs / (1000.0 * 60.0 ) + " minutes");
		//shape.exportShapeColoredToPDBFile("shapeFromCIT", algoParameters);
		//shape.exportMiniShapeColoredToPDBFile("miniShapeFromCIT", algoParameters);
		
		startTimeMs = System.currentTimeMillis();
		int peptideLength = 4;
		ShapeContainerWithPeptide shapeContainerWithPeptide = shapeBuilderRefactored.getShapeAroundASegmentOfChainUsingStartingMyMonomerPositionInChain("E".toCharArray(), 0, peptideLength);
		shapeContainerWithPeptide.exportShapeToPDBFile("TestShapeBuildershapeSegment_All_C", algoParameters);
		shapeContainerWithPeptide.exportMiniShapeToPDBFile("TestShapeBuilderSegmentminishape_All_C", algoParameters);
		shapeContainerWithPeptide.exportMiniShapeColoredToPDBFile("TestShapeBuilderSegmentminishape_Colored", algoParameters);
		taskTimeMs  = System.currentTimeMillis( ) - startTimeMs;
		System.out.println("computational time for Segment " + (double) taskTimeMs / (1000.0 * 60.0 ) + " minutes");
		
		startTimeMs = System.currentTimeMillis();
		shapeContainerWithPeptide = shapeBuilderRefactored.getShapeAroundAChain("E".toCharArray());
		shapeContainerWithPeptide.exportShapeToPDBFile("TestShapeBuildershapeWholeChain_All_C", algoParameters);
		shapeContainerWithPeptide.exportMiniShapeToPDBFile("TestShapeBuilderWholeChainminishape_All_C", algoParameters);
		shapeContainerWithPeptide.exportMiniShapeColoredToPDBFile("TestShapeBuilderWholeChainminishape_Colored", algoParameters);
		taskTimeMs  = System.currentTimeMillis( ) - startTimeMs;
		System.out.println("computational time for whole chain " + (double) taskTimeMs / (1000.0 * 60.0 ) + " minutes");
		
		startTimeMs = System.currentTimeMillis();
		List<String> chainToIgnore = new ArrayList<>();
		ShapeContainer shapeContainer = shapeBuilderRefactored.getShapeAroundAtomDefinedByIds(algoParameters.getQUERY_ATOMS_DEFINED_BY_IDS(), chainToIgnore);
		shapeContainer.exportShapeToPDBFile("TestShapeBuildershapeLennard_All_C", algoParameters);
		shapeContainer.exportMiniShapeToPDBFile("TestShapeBuilderLennardminishape_All_C", algoParameters);
		shapeContainer.exportMiniShapeColoredToPDBFile("TestShapeBuilderLennardminishape_Colored", algoParameters);
		taskTimeMs  = System.currentTimeMillis( ) - startTimeMs;
		System.out.println("computational time for LJ " + (double) taskTimeMs / (1000.0 * 60.0 ) + " minutes");
	}
}
