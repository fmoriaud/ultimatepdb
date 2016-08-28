package shapeBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import structure.ExceptionInMyStructurePackage;
import structure.MyChainIfc;
import structure.MyMonomerIfc;
import structure.MyStructureIfc;

public class StructureLocalTools {


	public static MyStructureIfc makeStructureLocalAroundAndExcludingMyMonomersFromInputMyChainAndTips(MyStructureIfc myStructureGlobalBrut, MyChainIfc myChain, List<MyMonomerIfc> tipMyMonomersToRemove) {

		Set<MyMonomerIfc> queryMonomers = makeMyMonomersLocalAroundAndExcludingMyMonomersFromInputMyChain(myChain);
		queryMonomers.removeAll(tipMyMonomersToRemove);
		MyStructureIfc myStructureLocal;
		try {
			myStructureLocal = myStructureGlobalBrut.cloneWithSameObjectsWhileKeepingOnlyMyMonomerInThisSet(queryMonomers);
		} catch (ExceptionInMyStructurePackage e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		return myStructureLocal;
	}



	private static Set<MyMonomerIfc> makeMyMonomersLocalAroundAndExcludingMyMonomersFromInputMyChain(MyChainIfc myChain){

		Set<MyMonomerIfc> queryMyMonomer = makeMyMonomersLocalAroundAndWithChain(myChain);

		for (MyMonomerIfc monomerToRemove: myChain.getMyMonomers()){
			queryMyMonomer.remove(monomerToRemove);
		}
		return queryMyMonomer;
	}



	public static Set<MyMonomerIfc> makeMyMonomersLocalAroundAndWithChain(MyChainIfc myChain){

		Set<MyMonomerIfc> queryMyMonomer = new HashSet<>();
		for (MyMonomerIfc myMonomer: myChain.getMyMonomers()){
			queryMyMonomer.add(myMonomer);
		}

		for (MyMonomerIfc monomer: myChain.getMyMonomers()){
			MyChainIfc[] neighbors = monomer.getNeighboringAminoMyMonomerByRepresentativeAtomDistance();
			for (MyChainIfc mychain: neighbors){
				for (MyMonomerIfc neighbor: mychain.getMyMonomers()){
					queryMyMonomer.add(neighbor);
				}
			}
		}

		return queryMyMonomer;
	}
}
