package structure;

public interface MyChainIfc {

	MyMonomerIfc[] getMyMonomers();
	MyMonomerIfc getMyMonomerByRank(int i);
	char[] getChainId();
	MyMonomerIfc getMyMonomerFromResidueId(int residueID);
	void removeMyMonomer(MyMonomerIfc myMonomer);
	void setChainId(char[] chainId);
	MyChainIfc makeSubchain(int startRankId, int length);
	void setMyMonomers(MyMonomerIfc[] myMonomers);
	void replaceMonomer(MyMonomerIfc oldMonomer, MyMonomerIfc newMonomer);
}
