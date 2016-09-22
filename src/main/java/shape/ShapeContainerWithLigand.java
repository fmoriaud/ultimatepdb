package shape;

import java.util.ArrayList;
import java.util.List;

import mystructure.Cloner;
import parameters.AlgoParameters;
import pointWithProperties.CollectionOfPointsWithPropertiesIfc;
import pointWithProperties.PointIfc;
import shapeCompare.ResultsFromEvaluateCost;
import mystructure.MyMonomerIfc;
import mystructure.MyStructureIfc;
import mystructure.MyStructureTools;

public class ShapeContainerWithLigand extends ShapeContainer implements ShapeContainerIfc{
	//------------------------
	// Class variables
	//------------------------
	MyMonomerIfc hetatmLigand;
	int occurenceId;




	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public ShapeContainerWithLigand(CollectionOfPointsWithPropertiesIfc shape, List<PointIfc> listPointDefininingLigandUsedToComputeShape, MyStructureIfc myStructureUsedToComputeShape, AlgoParameters algoParameters, MyMonomerIfc hetatmLigand, int occurenceId){
		super(shape, listPointDefininingLigandUsedToComputeShape, myStructureUsedToComputeShape, algoParameters);

		this.hetatmLigand = hetatmLigand;
		this.occurenceId = occurenceId;
	}




	// -------------------------------------------------------------------
	// Public methods
	// -------------------------------------------------------------------
	@Override
	public String makeEndFileName(){
		
		return String.valueOf(fourLetterCode) + "_" + String.valueOf(hetatmLigand.getParent().getChainId()) + "_" + String.valueOf(hetatmLigand.getThreeLetterCode()) + "_" + occurenceId;
	}
	
	
	
	@Override
	public void exportShapeToPDBFile(String fileName, AlgoParameters algoParameters){

		List<String> contentPDBFile = new ArrayList<>();
		List<String> contentPDBFilePeptide = makeContentPDBFileHetatmLigand(algoParameters);
		contentPDBFile.addAll(contentPDBFilePeptide);

		List<String> contributioncontentPDBFileShape = makeContentPDBFileShape(fileName, algoParameters);
		contentPDBFile.addAll(contributioncontentPDBFileShape);
		writeLinesToPDBFile(contentPDBFile, fileName, algoParameters);
	}



	@Override
	public void exportRotatedShapeToPDBFile(String fileName, AlgoParameters algoParameters, ResultsFromEvaluateCost result){

		List<String> contentPDBFile = new ArrayList<>();
		List<String> contentPDBFilePeptide = makeContentRotatedPDBFileHetatmLigand(algoParameters, result);
		contentPDBFile.addAll(contentPDBFilePeptide);

		List<String> contributioncontentPDBFileShape = makeContentRotatedPDBFileShape(fileName, algoParameters, result);
		contentPDBFile.addAll(contributioncontentPDBFileShape);
		writeLinesToPDBFile(contentPDBFile, fileName, algoParameters);
	}



	@Override
	public void exportShapeColoredToPDBFile(String fileName, AlgoParameters algoParameters){

		List<String> contentPDBFile = new ArrayList<>();
		List<String> contentPDBFilePeptide = makeContentPDBFileHetatmLigand(algoParameters);
		contentPDBFile.addAll(contentPDBFilePeptide);

		List<String> contributioncontentPDBFileShape = makeContentPDBFileColoredShape(fileName, algoParameters);
		contentPDBFile.addAll(contributioncontentPDBFileShape);
		writeLinesToPDBFile(contentPDBFile, fileName, algoParameters);
	}



	@Override
	public void exportRotatedShapeColoredToPDBFile(String fileName, AlgoParameters algoParameters, ResultsFromEvaluateCost result){

		List<String> contentPDBFile = new ArrayList<>();
		List<String> contentPDBFilePeptide = makeContentRotatedPDBFileHetatmLigand(algoParameters, result);
		contentPDBFile.addAll(contentPDBFilePeptide);

		List<String> contributioncontentPDBFileShape = makeContentRotatedPDBFileColoredShape(fileName, algoParameters, result);
		contentPDBFile.addAll(contributioncontentPDBFileShape);
		writeLinesToPDBFile(contentPDBFile, fileName, algoParameters);
	}



	@Override
	public void exportMiniShapeToPDBFile(String fileName, AlgoParameters algoParameters){

		List<String> contentPDBFile = new ArrayList<>();
		List<String> contentPDBFilePeptide = makeContentPDBFileHetatmLigand(algoParameters);
		contentPDBFile.addAll(contentPDBFilePeptide);

		List<String> contributioncontentPDBFileShape = makeContentPDBFileMiniShape(fileName, algoParameters);
		contentPDBFile.addAll(contributioncontentPDBFileShape);
		writeLinesToPDBFile(contentPDBFile, fileName, algoParameters);
	}



	@Override
	public void exportRotatedMiniShapeToPDBFile(String fileName, AlgoParameters algoParameters, ResultsFromEvaluateCost result){

		List<String> contentPDBFile = new ArrayList<>();
		List<String> contentPDBFilePeptide = makeContentRotatedPDBFileHetatmLigand(algoParameters, result);
		contentPDBFile.addAll(contentPDBFilePeptide);

		List<String> contributioncontentPDBFileShape = makeContentRotatedPDBFileMiniShape(fileName, algoParameters, result);
		contentPDBFile.addAll(contributioncontentPDBFileShape);
		writeLinesToPDBFile(contentPDBFile, fileName, algoParameters);
	}



	@Override
	public void exportMiniShapeColoredToPDBFile(String fileName, AlgoParameters algoParameters){

		List<String> contentPDBFile = new ArrayList<>();
		List<String> contentPDBFilePeptide = makeContentPDBFileHetatmLigand(algoParameters);
		contentPDBFile.addAll(contentPDBFilePeptide);

		List<String> contributioncontentPDBFileShape = makeContentPDBFileColoredMiniShape(fileName, algoParameters);
		contentPDBFile.addAll(contributioncontentPDBFileShape);
		writeLinesToPDBFile(contentPDBFile, fileName, algoParameters);
	}



	@Override
	public void exportRotatedMiniShapeColoredToPDBFile(String fileName, AlgoParameters algoParameters, ResultsFromEvaluateCost result){

		List<String> contentPDBFile = new ArrayList<>();
		List<String> contentPDBFilePeptide = makeContentRotatedPDBFileHetatmLigand(algoParameters, result);
		contentPDBFile.addAll(contentPDBFilePeptide);

		List<String> contributioncontentPDBFileShape = makeContentRotatedPDBFileColoredMiniShape(fileName, algoParameters, result);
		contentPDBFile.addAll(contributioncontentPDBFileShape);
		writeLinesToPDBFile(contentPDBFile, fileName, algoParameters);
	}




	// -------------------------------------------------------------------
	// Implementation
	// -------------------------------------------------------------------
	private List<String> makeContentPDBFileHetatmLigand(AlgoParameters algoParameters){
		List<String> contentPDBFilePeptide = new ArrayList<>();

		contentPDBFilePeptide.addAll(ShapeContainerTools.generateLinesFromMyMonomer(this.hetatmLigand, algoParameters, this.hetatmLigand.getParent().getChainId()));

		contentPDBFilePeptide.addAll(ShapeContainerTools.generateConnectLines(this.hetatmLigand));
		return contentPDBFilePeptide;
	}

	
	
	private List<String> makeContentRotatedPDBFileHetatmLigand(AlgoParameters algoParameters, ResultsFromEvaluateCost result){
		List<String> contentPDBFilePeptide = new ArrayList<>();

		Cloner cloner = new Cloner(this.hetatmLigand, algoParameters);
		MyMonomerIfc hetatmLigandRotated = cloner.getRotatedClone(result).getAllChains()[0].getMyMonomers()[0];
		contentPDBFilePeptide.addAll(ShapeContainerTools.generateLinesFromMyMonomer(hetatmLigandRotated, algoParameters, this.hetatmLigand.getParent().getChainId()));

		contentPDBFilePeptide.addAll(ShapeContainerTools.generateConnectLines(hetatmLigandRotated));
		return contentPDBFilePeptide;
	}
	

	
	
	// -------------------------------------------------------------------
	// Getter & Setter
	// -------------------------------------------------------------------
	public MyMonomerIfc getHetatmLigand() {
		return hetatmLigand;
	}



	public int getOccurenceId() {
		return occurenceId;
	}
}
