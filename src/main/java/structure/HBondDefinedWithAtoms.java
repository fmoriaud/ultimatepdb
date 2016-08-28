package structure;

public class HBondDefinedWithAtoms {
	//------------------------
	// Class variables
	//------------------------
	private MyAtomIfc myAtomDonor;
	private MyAtomIfc myAtomAcceptor;
	private MyAtomIfc myAtomHydrogen;




	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public HBondDefinedWithAtoms(MyAtomIfc myAtomDonor, MyAtomIfc myAtomAcceptor, MyAtomIfc myAtomHydrogen){
		this.myAtomDonor = myAtomDonor;
		this.myAtomAcceptor = myAtomAcceptor;
		this.myAtomHydrogen = myAtomHydrogen;
	}




	// -------------------------------------------------------------------
	// Public & Interface
	// -------------------------------------------------------------------
	@Override
	public String toString(){
		return "[ " 
				+ String.valueOf(myAtomDonor.getParent().getParent().getChainId()) + " " + String.valueOf(myAtomDonor.getParent().getThreeLetterCode()) + " " + myAtomDonor.getParent().getResidueID() + " " + String.valueOf(myAtomDonor.getAtomName()) +
				" - " 
				+ String.valueOf(myAtomAcceptor.getParent().getParent().getChainId()) + " " + String.valueOf(myAtomAcceptor.getParent().getThreeLetterCode()) + " " + myAtomAcceptor.getParent().getResidueID() + " " + String.valueOf(myAtomAcceptor.getAtomName()) +
				" - " 
				+ String.valueOf(myAtomHydrogen.getParent().getParent().getChainId()) + " " + String.valueOf(myAtomHydrogen.getParent().getThreeLetterCode()) + " " + myAtomHydrogen.getParent().getResidueID() + " " + String.valueOf(myAtomHydrogen.getAtomName()) +
				" ]";
	}


	// -------------------------------------------------------------------
	// Getter and Setter
	// -------------------------------------------------------------------
	public MyAtomIfc getMyAtomDonor() {
		return myAtomDonor;
	}

	public MyAtomIfc getMyAtomAcceptor() {
		return myAtomAcceptor;
	}

	public MyAtomIfc getMyAtomHydrogen() {
		return myAtomHydrogen;
	}
}
