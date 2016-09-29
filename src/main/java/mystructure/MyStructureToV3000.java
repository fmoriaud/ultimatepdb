package mystructure;

import java.util.HashMap;
import java.util.Map;

public class MyStructureToV3000 {

	//-------------------------------------------------------------
	// Constants
	//-------------------------------------------------------------
	private String newline = System.getProperty("line.separator");

	private MyStructureIfc myStructure;
	private Map<MyAtomIfc, Integer> mapMyAtomToRenumbered;



	//-------------------------------------------------------------
	// Class variables
	//-------------------------------------------------------------

	//-------------------------------------------------------------
	// Constructor
	//-------------------------------------------------------------
	public MyStructureToV3000(MyStructureIfc myStructure){

		this.myStructure = myStructure;

		mapMyAtomToRenumbered = new HashMap<>();
		int atomCountHere = 0;
		for (MyChainIfc chain: myStructure.getAllChains()){
			for (MyMonomerIfc monomer: chain.getMyMonomers()){
				for (MyAtomIfc myAtom: monomer.getMyAtoms()){
					atomCountHere += 1;
					mapMyAtomToRenumbered.put(myAtom, atomCountHere);
				}
			}
		}
	}




	//-------------------------------------------------------------
	// Interface & Public methods
	//-------------------------------------------------------------
	public String getV3000(){

		StringBuilder sbWhole = new StringBuilder();

		try{
			StringBuilder sbAtom = new StringBuilder();
			StringBuilder sbBond = new StringBuilder();
			int atomCount = 0;
			int bondCount = 0;
			for (MyChainIfc chain: myStructure.getAllChains()){
				for (MyMonomerIfc monomer: chain.getMyMonomers()){
					for (MyAtomIfc myAtom: monomer.getMyAtoms()){
						addAtomLine(myAtom, sbAtom);
						if (myAtom.getBonds() != null){
							for (MyBondIfc myBond: myAtom.getBonds()){
								if (myBond.getBondedAtom().getOriginalAtomId() < myAtom.getOriginalAtomId()){
									bondCount += 1;
									addBondLine(myAtom, myBond, sbBond, bondCount);
								}
							}
						}
						atomCount += 1;
					}
				}
			}


			sbWhole.append(myStructure.getFourLetterCode());
			sbWhole.append(newline);
			sbWhole.append(newline);
			sbWhole.append(newline);
			sbWhole.append("  0  0  0  0  0  0            999 V3000");
			sbWhole.append(newline);
			sbWhole.append("M  V30 BEGIN CTAB");
			sbWhole.append(newline);
			sbWhole.append("M  V30 COUNTS ").append(atomCount)
			.append(" ").append(bondCount).append(" 0 0 0");
			sbWhole.append(newline);
			sbWhole.append("M  V30 BEGIN ATOM");
			sbWhole.append(newline);
			sbWhole.append(sbAtom.toString());
			sbWhole.append("M  V30 END ATOM");
			sbWhole.append(newline);
			if (bondCount > 0){
				sbWhole.append("M  V30 BEGIN BOND");
				sbWhole.append(newline);
				sbWhole.append(sbBond.toString());
				sbWhole.append("M  V30 END BOND");
				sbWhole.append(newline);
			}
			sbWhole.append("M  V30 END CTAB");
			sbWhole.append(newline);
			sbWhole.append("M  END");

		}catch(Exception e){
			e.printStackTrace();
			System.out.println(" weird in getV3000");
		}
		return sbWhole.toString();
	}




	// -------------------------------------------------------------------
	// Implementation
	// -------------------------------------------------------------------
	private void addAtomLine(MyAtomIfc myAtom, StringBuilder sb){

		sb.append("M  V30 ");
		sb.append(mapMyAtomToRenumbered.get(myAtom));
		sb.append(" ");
		sb.append(myAtom.getElement());
		sb.append(" ");
		sb.append(myAtom.getCoords()[0]);
		sb.append(" ");
		sb.append(myAtom.getCoords()[1]);
		sb.append(" ");
		sb.append(myAtom.getCoords()[2]);
		sb.append(" ");

		sb.append(" 0");
		//  if (myAtom.charge != 0) mol.append(" CHG=").appendI(charge);

		sb.append(newline);
	}



	private void addBondLine(MyAtomIfc myAtom, MyBondIfc myBond, StringBuilder sb, int bondCount){

		if (mapMyAtomToRenumbered.get(myBond.getBondedAtom()) == null){
			System.out.println("weird addBondLine in to V3000: getBondedAtom is not in atom list");
		}

		// if bond missing search here
		//if (mapMyAtomToRenumbered.get(myBond.getBondedAtom()) != null){
		sb.append("M  V30 ").append(bondCount).append(" ").append(myBond.getBondOrder()).append(" ")
		.append(mapMyAtomToRenumbered.get(myAtom)).append(" ").append(mapMyAtomToRenumbered.get(myBond.getBondedAtom()));
		sb.append(newline);
		//}

	}
}
