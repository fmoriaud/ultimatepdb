package shape;

import java.util.ArrayList;
import java.util.List;

import mystructure.MyMonomerIfc;
import parameters.AlgoParameters;
import parameters.QueryAtomDefinedByIds;
import pointWithProperties.CollectionOfPointsWithPropertiesIfc;
import pointWithProperties.PointIfc;
import shapeCompare.ResultsFromEvaluateCost;
import mystructure.MyChainIfc;
import mystructure.MyStructureIfc;

public class ShapeContainerAtomIdsWithinShapeWithPeptide extends ShapeContainerAtomIdsWithinShape implements HasPeptideIfc{
	//------------------------
	// Class variables
	//------------------------
	private MyChainIfc peptide;




	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------	
	public ShapeContainerAtomIdsWithinShapeWithPeptide(List<QueryAtomDefinedByIds> listAtomDefinedByIds,
			CollectionOfPointsWithPropertiesIfc shape,
			List<PointIfc> listPointDefininingLigandUsedToComputeShape,
			MyStructureIfc myStructureUsedToComputeShape, List<MyMonomerIfc> foreignMonomerToExclude,
			AlgoParameters algoParameters) {

		super(listAtomDefinedByIds, shape, listPointDefininingLigandUsedToComputeShape, myStructureUsedToComputeShape, foreignMonomerToExclude, algoParameters);
	}




	// -------------------------------------------------------------------
	// Public methods
	// -------------------------------------------------------------------
	@Override
	public MyChainIfc getPeptide() {
		return peptide;
	}



	public void setPeptide(MyChainIfc peptide) {
		this.peptide = peptide;
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
}
