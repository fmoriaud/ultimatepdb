package mystructure;

import java.util.Arrays;

import mystructure.AtomProperties.AtomGaussianDescriptors;

public class MyAtom implements MyAtomIfc{

	//-------------------------------------------------------------
	// Class variables
	//-------------------------------------------------------------
	private char[] element;
	private float[] coords;
	private char[] atomName;
	private MyMonomerIfc parent;
	private MyBondIfc[] bonds;
	private int originalAtomId;




	//-------------------------------------------------------------
	// Constructor
	//-------------------------------------------------------------
	/** Class to store an atom and its bonds to others atoms
	 * @param element is the atom symbol, e.g. C for carbon
	 * @param coords is the 3d coordinates of this atom. If null then origin is used (0,0,0)
	 * @param atomName is an atom name, must be the one from PDB file or defined in
	 * MyJmolTools TODO move the naming in myStructure package
	 * if not available for Hydrogen, according to 
	 * @param originalAtomId is usually from PDB file if available or any as they are not used
	 * @throws ExceptionInMyStructurePackage if element is not defined in myStructure.AtomGaussianDescriptors
	 * @author Fabrice MORIAUD
	 */
	public MyAtom(char[] element, float[] coords, char[] atomName, int originalAtomId) throws ExceptionInMyStructurePackage{
		if (!isElementValid(element)){
			throw new ExceptionInMyStructurePackage("MyAtom cannot be built with element name " + String.valueOf(element));
		}
		this.element = element;
		this.coords = coords;
		this.atomName = atomName;
		this.originalAtomId = originalAtomId;
		this.bonds = new MyBond[0];
	}




	//-------------------------------------------------------------
	// Interface & Public methods
	//-------------------------------------------------------------
	/**
	 * for debugging
	 */
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("atom: " +  Arrays.toString(element) + " named: " + String.valueOf(atomName));
		return sb.toString();
	}

	/**
	 * Get 3d coordinates of this atom
	 */
	@Override
	public float[] getCoords() {
		if (coords == null){
			coords = new float[3];
		}
		return coords;
	}

	/**
	 * set 3d coordinates of this atom
	 */
	@Override
	public void setCoords(float[] coords) {
		this.coords = coords;

	}

	@Override
	public char[] getElement() {
		return element;
	}

	@Override
	public char[] getAtomName() {
		return atomName;
	}

	@Override
	public MyMonomerIfc getParent() {
		return parent;
	}

	@Override
	public void setParent(MyMonomerIfc parent) {
		this.parent = parent;
	}

	@Override
	public MyBondIfc[] getBonds() {
		return bonds;
	}

	@Override
	public void setBonds(MyBondIfc[] bonds) {
		this.bonds = bonds;
	}

	@Override
	public int getOriginalAtomId() {
		return originalAtomId;
	}

	/**
	 * Add a bond which is stored in this MyAtom class
	 * If bonded atom MyAtom in added MyBond is already bonded then nothing is done
	 */
	@Override
	public void addBond(MyBondIfc bond) {
		if (bonds.length == 0){
			MyBondIfc[] newBonds = new MyBondIfc[1];
			newBonds[0] = bond;
			setBonds(newBonds);
		}else{
			// check if already done
			for (MyBondIfc bondAlreadyThere: bonds){
				if (bondAlreadyThere.getBondedAtom() == bond.getBondedAtom()){
					return; // bond already defined
				}
			}

			MyBondIfc[] newBonds = new MyBondIfc[bonds.length + 1];
			for (int i=0; i<bonds.length; i++){
				newBonds[i] = bonds[i];
			}
			newBonds[newBonds.length-1] = bond;
			setBonds(newBonds);
		}
	}

	/**
	 * Remove MyBond if same object already in bonds
	 */
	@Override
	public void removeBond(MyBondIfc bond) {

		MyBondIfc bondToRemove = null;
		for (int i=0; i<bonds.length; i++){
			if (bonds[i] == bond){
				bondToRemove = bond;
			}
		}
		if (bondToRemove == null){
			System.out.println("failed to remove bond from MyAtom");
			return;
		}
		MyBondIfc[] newBonds = new MyBondIfc[bonds.length - 1];
		int currentBondId= 0;
		for (MyBondIfc oldBond: bonds){
			if (oldBond == bond){
				continue;
			}
			newBonds[currentBondId] = oldBond;
			currentBondId += 1;
		}
		bonds = newBonds;
	}


	@Override
	public void setOriginalAtomId(int originalAtomId) {
		this.originalAtomId = originalAtomId;
	}



	//-------------------------------------------------------------
	// Implementation
	//-------------------------------------------------------------
	private boolean isElementValid(char[] element){
		for (AtomGaussianDescriptors atomGaussianDescriptors: AtomGaussianDescriptors.values()){
			String currentAtomNameFromAtomGaussianDescriptors = atomGaussianDescriptors.getAtomName();

			if (Arrays.equals(element, currentAtomNameFromAtomGaussianDescriptors.toCharArray())){
				return true;
			} 
		}
		return false;
	}
}
