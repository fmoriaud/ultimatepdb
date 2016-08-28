package structure;

import java.nio.file.attribute.FileTime;
import java.util.List;
import java.util.Set;

import shapeCompare.ResultsFromEvaluateCost;



public interface MyStructureIfc {

	public FileTime getLastModificationTime();
	public void setLastModificationTime(FileTime lastModificationTime);
	
	MyChainIfc getAminoChain(int i);
	void setAminoChain(char[] chainId, MyChainIfc myNewChain);
	MyChainIfc[] getAllAminochains();
	MyChainIfc[] getAllHetatmchains();
	MyChainIfc[] getAllNucleosidechains();
	MyChainIfc getAminoMyChain(char[] chainId);
	MyChainIfc[] getAllChainsRelevantForShapeBuilding();
	/**
	 * Get All MyChains in the following order 	AMINOACID, HETATM, NUCLEOTIDE
	 * @return
	 */
	MyChainIfc[] getAllChains();
	int getAminoMonomercount();

	int getAminoChainCount();

	int indexOfAnAminoChain(MyChainIfc myChain);
	
	char[] getFourLetterCode();
	void setFourLetterCode(char[] fourLetterCode);
	
	HBondDefinedByAtomAndMonomer[] getHbonds();
	void setHbonds(HBondDefinedByAtomAndMonomer[] hbonds);

	PairOfMyAtomWithMyMonomerAndMychainReferences[] getDisulfideBridges();
	void setDisulfideBridges(PairOfMyAtomWithMyMonomerAndMychainReferences[] disulfideBridges);
	String toV3000();
	
	MyStructureIfc cloneWithSameObjects() throws ExceptionInMyStructurePackage;
	MyStructureIfc cloneWithSameObjectsWhileKeepingOnlyMyMonomerInThisSet(Set<MyMonomerIfc> myMonomerToKeep) throws ExceptionInMyStructurePackage;
	
	void removeChain(char[] chainId);
	MyStructureIfc cloneWithSameObjectsRotatedCoords(ResultsFromEvaluateCost result) throws ExceptionInMyStructurePackage;
}
