package mystructure;

import java.nio.file.attribute.FileTime;
import java.util.Set;

import shapeCompare.ResultsFromEvaluateCost;



public interface MyStructureIfc {

	public FileTime getLastModificationTime();
	public void setLastModificationTime(FileTime lastModificationTime);
	
	MyChainIfc getAminoChain(int i);
	MyChainIfc getNucleosideChain(int i);
	MyChainIfc getHetatmChain(int i);

	void setAminoChain(char[] chainId, MyChainIfc myNewChain);
	MyChainIfc[] getAllAminochains();
	MyChainIfc[] getAllHetatmchains();
	MyChainIfc[] getAllNucleosidechains();
	MyChainIfc getAminoMyChain(char[] chainId);
	MyChainIfc getNucleosideChain(char[] chainId);
	MyChainIfc getHeteroChain(char[] chainId);
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

	void removeChain(char[] chainId);

	ExpTechniquesEnum getExpTechnique();
}
