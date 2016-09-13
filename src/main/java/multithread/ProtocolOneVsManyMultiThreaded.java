package multithread;

import java.util.ArrayList;
import java.util.List;

import parameters.AlgoParameters;
import parameters.TargetDefinedByHetAtm;
import parameters.TargetDefinedBySegmentOfChainBasedOnSegmentLength;
import parameters.TargetDefinedBySegmentOfChainBasedOnSequenceMotif;
import parameters.TargetDefinedByWholeChain;
import parameters.TargetsIfc;
import protocols.ProtocolIfc;
import shape.ShapeContainerAtomIdsWithinShapeWithPeptide;
import shape.ShapeContainerIfc;
import shapeBuilder.ShapeBuilderConstructorAtomIdsWithinShape;
import shapeBuilder.ShapeBuilderConstructorHetAtm;
import shapeBuilder.ShapeBuilderConstructorIfc;
import shapeBuilder.ShapeBuilderConstructorSegmentOfChain;
import shapeBuilder.ShapeBuilderConstructorWholeChain;
import shapeBuilder.ShapeBuildingException;
import shapeBuilder.ShapeBuildingTools;
import mystructure.EnumMyReaderBiojava;
import mystructure.MyChainIfc;
import mystructure.MyStructureIfc;

public class ProtocolOneVsManyMultiThreaded implements ProtocolIfc{
	//------------------------
	// Class variables
	//------------------------
	private AlgoParameters algoParameters;
	private EnumMyReaderBiojava enumMyReaderBiojava;


	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public ProtocolOneVsManyMultiThreaded(AlgoParameters algoParameters, EnumMyReaderBiojava enumMyReaderBiojava){

		this.algoParameters = algoParameters;
		this.enumMyReaderBiojava = enumMyReaderBiojava;
	}




	// -------------------------------------------------------------------
	// Public methods
	// -------------------------------------------------------------------
	@Override
	public void run() {

		MyStructureIfc myStructureGlobalBrut;
		try {
			myStructureGlobalBrut = ShapeBuildingTools.getMyStructure(algoParameters.getQUERY_PDB_FOUR_LETTER_CODE().toCharArray(), algoParameters, enumMyReaderBiojava);
		} catch (ShapeBuildingException e2) {
			System.out.println("Query PDB file failed to read ... program terminated ");
			return;
		}

		// QueryConstructor from Ultimate.xml
		String queryType = algoParameters.getQUERY_TYPE();
		ShapeBuilderConstructorIfc queryConstructor = null;
		switch (queryType) {
		case "WHOLE_CHAIN":
			// Warning might not work anymore as I have put myStructureGlobalBrut instead of old constructore that rebuilt it
			queryConstructor = new ShapeBuilderConstructorWholeChain(myStructureGlobalBrut, algoParameters.getQUERY_CHAIN_ID().toCharArray(), algoParameters);
			;
			break;
		case "SEGMENT_OF_CHAIN":  
			queryConstructor = new ShapeBuilderConstructorSegmentOfChain(myStructureGlobalBrut, algoParameters.getQUERY_CHAIN_ID().toCharArray(), algoParameters.getSTARTING_RANK_ID(), algoParameters.getPEPTIDE_LENGTH(), algoParameters);
			;
			break;
		case "HETATM":
			queryConstructor = new ShapeBuilderConstructorHetAtm(myStructureGlobalBrut, algoParameters.getQUERY_PDB_THREE_LETTER_CODE().toCharArray(), algoParameters.getOCCURENCE_ID(), algoParameters);
			;
			break;
		case "ATOMIDS_WITHIN_SHAPE":
			queryConstructor = new ShapeBuilderConstructorAtomIdsWithinShape(algoParameters.getQUERY_PDB_FOUR_LETTER_CODE().toCharArray(), algoParameters.getQUERY_ATOMS_DEFINED_BY_IDS(), algoParameters.getRADIUS_FOR_QUERY_ATOMS_DEFINED_BY_IDS(), algoParameters, algoParameters.getCHAIN_TO_IGNORE());
			;
			break;
		}

		List<TargetDefinedByHetAtm> listTargetDefinedByHetAtm = algoParameters.getLIST_TargetDefinedByHetAtm();
		List<TargetDefinedBySegmentOfChainBasedOnSegmentLength> listTargetDefinedBySegmentOfChainBasedOnSegmentLength = algoParameters.getLIST_TargetDefinedBySegmentOfChainBasedOnSegmentLength();
		List<TargetDefinedBySegmentOfChainBasedOnSequenceMotif> listTargetDefinedBySegmentOfChainBasedOnSequenceMotif = algoParameters.getLIST_TargetDefinedBySegmentOfChainBasedOnSequenceMotif();
		List<TargetDefinedByWholeChain> listTargetDefinedByWholeChain = algoParameters.getLIST_TargetDefinedByWholeChain();

		List<TargetsIfc> allTargetsDefiners = new ArrayList<>();
		allTargetsDefiners.addAll(listTargetDefinedByHetAtm);
		allTargetsDefiners.addAll(listTargetDefinedBySegmentOfChainBasedOnSegmentLength);
		allTargetsDefiners.addAll(listTargetDefinedBySegmentOfChainBasedOnSequenceMotif);
		allTargetsDefiners.addAll(listTargetDefinedByWholeChain);




		// build query
		ShapeContainerIfc queryShape;
		try {
			queryShape = queryConstructor.getShapeContainer();
		} catch (ShapeBuildingException e2) {
			System.out.println("Query failed to build ... program terminated ");
			return;
		}

		// Put a peptide in that query but it is only a work around for this query with atomids to get rmsd computed
		if (queryShape instanceof ShapeContainerAtomIdsWithinShapeWithPeptide){
			ShapeContainerAtomIdsWithinShapeWithPeptide shapeContainerAtomIdsWithinShapeWithPeptide = (ShapeContainerAtomIdsWithinShapeWithPeptide) queryShape;
			MyChainIfc peptideQuery = myStructureGlobalBrut.getAminoMyChain("B".toCharArray());
			shapeContainerAtomIdsWithinShapeWithPeptide.setPeptide(peptideQuery);
			queryShape = shapeContainerAtomIdsWithinShapeWithPeptide;
		}

		queryShape.exportMiniShapeColoredToPDBFile("queryMinishape", algoParameters);
		queryShape.exportShapeColoredToPDBFile("queryShape", algoParameters);
		// sort according to PDB file
		// use the trick if last PDB loaded then dont load again or not  to respect user order ?

		CompareWithExecutor compareAll = new CompareWithExecutor(queryShape, allTargetsDefiners, algoParameters, enumMyReaderBiojava);
		compareAll.run();


		System.out.println("Program finished ");
		System.exit(0);
	}
}
