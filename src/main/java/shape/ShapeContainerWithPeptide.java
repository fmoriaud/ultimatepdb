package shape;

import java.util.ArrayList;
import java.util.List;

import parameters.AlgoParameters;
import pointWithProperties.CollectionOfPointsWithPropertiesIfc;
import pointWithProperties.PointIfc;
import shapeCompare.ResultsFromEvaluateCost;
import structure.MyChainIfc;
import structure.MyStructureIfc;

public class ShapeContainerWithPeptide extends ShapeContainer implements ShapeContainerIfc, HasPeptideIfc{
	//------------------------
	// Class variables
	//------------------------
	MyChainIfc peptide;
	int startingRankId;
	List<char[]> peptideSequence;




	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public ShapeContainerWithPeptide(CollectionOfPointsWithPropertiesIfc shape, List<PointIfc> listPointDefininingLigandUsedToComputeShape, MyStructureIfc myStructureUsedToComputeShape, AlgoParameters algoParameters, MyChainIfc peptide, int startingRankId){
		super(shape, listPointDefininingLigandUsedToComputeShape, myStructureUsedToComputeShape, algoParameters);

		this.startingRankId = startingRankId;
		this.peptide = peptide;
		this.peptideSequence = ShapeContainerTools.generatePeptideSequence(peptide);
	}




	// -------------------------------------------------------------------
	// Public methods
	// -------------------------------------------------------------------
	@Override
	public String makeEndFileName(){

		return String.valueOf(fourLetterCode) + "_" + String.valueOf(peptide.getChainId()) + "_" + startingRankId;
	}



	@Override
	public void exportShapeToPDBFile(String fileName, AlgoParameters algoParameters){

		List<String> contentPDBFile = new ArrayList<>();
		List<String> contentPDBFilePeptide = ShapeContainerTools.makeContentPDBFilePeptide(algoParameters, peptide);
		contentPDBFile.addAll(contentPDBFilePeptide);

		List<String> contributioncontentPDBFileShape = makeContentPDBFileShape(fileName, algoParameters);
		contentPDBFile.addAll(contributioncontentPDBFileShape);
		writeLinesToPDBFile(contentPDBFile, fileName, algoParameters);
	}



	@Override
	public void exportRotatedShapeToPDBFile(String fileName, AlgoParameters algoParameters, ResultsFromEvaluateCost result){

		List<String> contentPDBFile = new ArrayList<>();
		List<String> contentPDBFilePeptide = ShapeContainerTools.makeContentRotatedPDBFilePeptide(algoParameters, result, peptide);
		contentPDBFile.addAll(contentPDBFilePeptide);

		List<String> contributioncontentPDBFileShape = makeContentRotatedPDBFileShape(fileName, algoParameters, result);
		contentPDBFile.addAll(contributioncontentPDBFileShape);
		writeLinesToPDBFile(contentPDBFile, fileName, algoParameters);
	}



	@Override
	public void exportShapeColoredToPDBFile(String fileName, AlgoParameters algoParameters){

		List<String> contentPDBFile = new ArrayList<>();
		List<String> contentPDBFilePeptide = ShapeContainerTools.makeContentPDBFilePeptide(algoParameters, peptide);
		contentPDBFile.addAll(contentPDBFilePeptide);

		List<String> contributioncontentPDBFileShape = makeContentPDBFileColoredShape(fileName, algoParameters);
		contentPDBFile.addAll(contributioncontentPDBFileShape);
		writeLinesToPDBFile(contentPDBFile, fileName, algoParameters);
	}



	@Override
	public void exportRotatedShapeColoredToPDBFile(String fileName, AlgoParameters algoParameters, ResultsFromEvaluateCost result){

		List<String> contentPDBFile = new ArrayList<>();
		List<String> contentPDBFilePeptide = ShapeContainerTools.makeContentRotatedPDBFilePeptide(algoParameters, result, peptide);
		contentPDBFile.addAll(contentPDBFilePeptide);

		List<String> contributioncontentPDBFileShape = makeContentRotatedPDBFileColoredShape(fileName, algoParameters, result);
		contentPDBFile.addAll(contributioncontentPDBFileShape);
		writeLinesToPDBFile(contentPDBFile, fileName, algoParameters);
	}



	@Override
	public void exportMiniShapeToPDBFile(String fileName, AlgoParameters algoParameters){

		List<String> contentPDBFile = new ArrayList<>();
		List<String> contentPDBFilePeptide = ShapeContainerTools.makeContentPDBFilePeptide(algoParameters, peptide);
		contentPDBFile.addAll(contentPDBFilePeptide);

		List<String> contributioncontentPDBFileShape = makeContentPDBFileMiniShape(fileName, algoParameters);
		contentPDBFile.addAll(contributioncontentPDBFileShape);
		writeLinesToPDBFile(contentPDBFile, fileName, algoParameters);
	}



	@Override
	public void exportRotatedMiniShapeToPDBFile(String fileName, AlgoParameters algoParameters, ResultsFromEvaluateCost result){

		List<String> contentPDBFile = new ArrayList<>();
		List<String> contentPDBFilePeptide = ShapeContainerTools.makeContentRotatedPDBFilePeptide(algoParameters, result, peptide);
		contentPDBFile.addAll(contentPDBFilePeptide);

		List<String> contributioncontentPDBFileShape = makeContentRotatedPDBFileMiniShape(fileName, algoParameters, result);
		contentPDBFile.addAll(contributioncontentPDBFileShape);
		writeLinesToPDBFile(contentPDBFile, fileName, algoParameters);
	}



	@Override
	public void exportMiniShapeColoredToPDBFile(String fileName, AlgoParameters algoParameters){

		List<String> contentPDBFile = new ArrayList<>();
		List<String> contentPDBFilePeptide = ShapeContainerTools.makeContentPDBFilePeptide(algoParameters, peptide);
		contentPDBFile.addAll(contentPDBFilePeptide);

		List<String> contributioncontentPDBFileShape = makeContentPDBFileColoredMiniShape(fileName, algoParameters);
		contentPDBFile.addAll(contributioncontentPDBFileShape);
		writeLinesToPDBFile(contentPDBFile, fileName, algoParameters);
	}



	@Override
	public void exportRotatedMiniShapeColoredToPDBFile(String fileName, AlgoParameters algoParameters, ResultsFromEvaluateCost result){

		List<String> contentPDBFile = new ArrayList<>();
		List<String> contentPDBFilePeptide = ShapeContainerTools.makeContentRotatedPDBFilePeptide(algoParameters, result, peptide);
		contentPDBFile.addAll(contentPDBFilePeptide);

		List<String> contributioncontentPDBFileShape = makeContentRotatedPDBFileColoredMiniShape(fileName, algoParameters, result);
		contentPDBFile.addAll(contributioncontentPDBFileShape);
		writeLinesToPDBFile(contentPDBFile, fileName, algoParameters);
	}


	
	@Override
	public MyChainIfc getPeptide() {
		return peptide;
	}

	
	
	public List<char[]> getPeptideSequence() {
		return peptideSequence;
	}

	
	
	public int getStartingRankId() {
		return startingRankId;
	}
}
