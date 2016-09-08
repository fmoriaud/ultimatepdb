package shapeBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import parameters.AlgoParameters;
import parameters.QueryAtomDefinedByIds;
import mystructure.ExceptionInMyStructurePackage;
import mystructure.MyAtomIfc;
import mystructure.MyChain;
import mystructure.MyChainIfc;
import mystructure.MyMonomerIfc;
import mystructure.MyStructureIfc;

public class StructureLocalToBuildShapeAroundAtomDefinedByIds implements StructureLocalToBuildShapeIfc{
	//-------------------------------------------------------------
	// Class variables
	//-----------------------------------------------------
	private MyStructureIfc myStructureGlobalBrut;
	private List<QueryAtomDefinedByIds> queryAtomsDefinedByIds;
	private List<String> chainToIgnore;
	private MyStructureIfc myStructureLocal;
	private AlgoParameters algoParameters;


	
	
	//-------------------------------------------------------------
	// Constructor
	//-------------------------------------------------------------
	public StructureLocalToBuildShapeAroundAtomDefinedByIds(MyStructureIfc myStructureGlobalBrut,
			List<QueryAtomDefinedByIds> queryAtomsDefinedByIds, AlgoParameters algoParameters, List<String> chainToIgnore){

		this.chainToIgnore = chainToIgnore;
		this.myStructureGlobalBrut = myStructureGlobalBrut;
		this.queryAtomsDefinedByIds = queryAtomsDefinedByIds;
		this.algoParameters = algoParameters;

	}




	//-------------------------------------------------------------
	// Interface & Public methods
	//-------------------------------------------------------------
	public void compute() throws ShapeBuildingException{

		List<MyMonomerIfc> monomersContainingAtomsDefinedByIds = findMyMonomersOnlyInAminoChainsContainingAtomsDefinedByIds(myStructureGlobalBrut, queryAtomsDefinedByIds);
		MyChainIfc correspondingChain = new MyChain(monomersContainingAtomsDefinedByIds);
		myStructureLocal = makeStructureLocalAroundAndWithChain(correspondingChain, chainToIgnore);
	}




	//-------------------------------------------------------------
	// Implementation
	//-------------------------------------------------------------
	private MyStructureIfc makeStructureLocalAroundAndWithChain(MyChainIfc myChain, List<String> chainToIgnore) {

		Set<MyMonomerIfc> queryMonomers = StructureLocalTools.makeMyMonomersLocalAroundAndWithChain(myChain);
		MyStructureIfc myStructureLocal;
		try {
			myStructureLocal = myStructureGlobalBrut.cloneWithSameObjectsWhileKeepingOnlyMyMonomerInThisSet(queryMonomers);
		} catch (ExceptionInMyStructurePackage e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		ShapeBuildingTools.deleteChains(chainToIgnore, myStructureLocal);

		return myStructureLocal;
	}



	private List<MyMonomerIfc> findMyMonomersOnlyInAminoChainsContainingAtomsDefinedByIds(MyStructureIfc myStructure, List<QueryAtomDefinedByIds> queryAtomsDefinedByIds){

		List<MyMonomerIfc> monomersFound = new ArrayList<>();

		for (QueryAtomDefinedByIds atomDefinedByIds: queryAtomsDefinedByIds){

			char[] chainIdToFind = atomDefinedByIds.getChainQuery().toCharArray();
			MyChainIfc foundMyChain = myStructure.getAminoMyChain(chainIdToFind);
			if (foundMyChain == null){
				System.out.println("chain not found : " + String.valueOf(chainIdToFind));
				continue;
			}

			int residueIdToFind = atomDefinedByIds.getResidueId();
			MyMonomerIfc foundMyMonomer = foundMyChain.getMyMonomerFromResidueId(residueIdToFind);
			if (foundMyMonomer == null){
				System.out.println("monomer not found : " + residueIdToFind);
				continue;
			}

			char[] atomNameToFind = atomDefinedByIds.getAtomName().toCharArray();
			MyAtomIfc foundMyAtom = foundMyMonomer.getMyAtomFromMyAtomName(atomNameToFind);
			if (foundMyAtom == null){
				System.out.println("atom not found : " + String.valueOf(atomNameToFind));
				continue;
			}
			monomersFound.add(foundMyMonomer);
		}
		return monomersFound;
	}




	//-------------------------------------------------------------
	// Getters & Setters
	//-------------------------------------------------------------
	@Override
	public MyStructureIfc getMyStructureLocal() {
		return myStructureLocal;
	}
}
