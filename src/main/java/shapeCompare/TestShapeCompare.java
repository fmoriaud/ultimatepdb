package shapeCompare;

import java.io.IOException;
import java.util.List;
import java.util.logging.FileHandler;

import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import hits.Hit;
import parameters.AlgoParameters;
import protocols.CommandLineException;
import protocols.CommandLineTools;
import protocols.ControllerLoger;
import protocols.OptimizerFormater;
import protocols.ParsingConfigFileException;
import shape.ShapeContainerWithPeptide;
import shapeBuilder.ShapeBuilder;
import shapeBuilder.ShapeBuildingException;
import structure.EnumMyReaderBiojava;
import structure.ExceptionInMyStructurePackage;
import structure.MyStructureIfc;
import structure.ReadingStructurefileException;
import structure.StructureReaderMode;

public class TestShapeCompare {

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

		MyStructureIfc query = null; // IOTools.getMyStructures(algoParameters.getQUERY_PDB_FOUR_LETTER_CODE().toCharArray(), algoParameters, enumMyReaderBiojava, StructureReaderMode.ReadyForShapeComputation);
		System.out.println(query.getFourLetterCode());

		System.out.println("Structure read successfully");

		ShapeBuilder shapeBuilderRefactored = new ShapeBuilder(query, algoParameters);

		int peptideLength = 4;
		ShapeContainerWithPeptide shapeContainerWithPeptide = shapeBuilderRefactored.getShapeAroundASegmentOfChainUsingStartingMyMonomerPositionInChain("E".toCharArray(), 0, peptideLength);
		shapeContainerWithPeptide.exportShapeToPDBFile("TestShapeBuildershapeSegment_All_C", algoParameters);
		shapeContainerWithPeptide.exportMiniShapeToPDBFile("TestShapeBuilderSegmentminishape_All_C", algoParameters);
		shapeContainerWithPeptide.exportMiniShapeColoredToPDBFile("TestShapeBuilderSegmentminishape_Colored", algoParameters);


		ShapeContainerWithPeptide shapeContainerQuery = shapeBuilderRefactored.getShapeAroundAChain("E".toCharArray());
		shapeContainerQuery.exportShapeToPDBFile("TestShapeBuildershapeWholeChain_All_C", algoParameters);
		shapeContainerQuery.exportMiniShapeToPDBFile("TestShapeBuilderWholeChainminishape_All_C", algoParameters);
		shapeContainerQuery.exportMiniShapeColoredToPDBFile("TestShapeBuilderWholeChainminishape_Colored", algoParameters);

		long startTimeMs = System.currentTimeMillis();

		ComparatorShapeContainerQueryVsAnyShapeFull comparatorShape = new ComparatorShapeContainerQueryVsAnyShapeFull(shapeContainerQuery, shapeContainerWithPeptide, algoParameters);
		try {
			List<Hit> listBestHitForEachAndEverySeed = comparatorShape.computeResults();
			System.out.println();

		} catch (NullResultFromAComparisonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		long taskTimeMs  = System.currentTimeMillis( ) - startTimeMs;
		System.out.println("comparison time for Segment " + (double) taskTimeMs / (1000.0 * 60.0 ) + " minutes");

	}

}
