package run;

public class Pair4LetterCodeAndChain {
	//-------------------------------------------------------------
	// Class variables
	//-------------------------------------------------------------
	private char[] fourLetterCode;
	private char[] chainName;




	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public Pair4LetterCodeAndChain(char[] fourLetterCode, char[] chainName){
		this.fourLetterCode = fourLetterCode;
		this.chainName = chainName;
	}

	
	
	
	// -------------------------------------------------------------------
	// Public Interface
	// -------------------------------------------------------------------
	@Override public String toString() {
		StringBuilder result = new StringBuilder();

		result.append(String.valueOf(fourLetterCode) + " ");
		result.append(String.valueOf(chainName) + " ");
		return result.toString();
	}

	
	
	
	// -------------------------------------------------------------------
	// Getters and Setters
	// -------------------------------------------------------------------
	public char[] getFourLetterCode() {
		return fourLetterCode;
	}

	public char[] getChainName() {
		return chainName;
	}
}
