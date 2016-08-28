package run;

public class Pair3LetterCodeAndMW extends Pair4LetterCodeAndChain{
	//-------------------------------------------------------------
	// Class variables
	//-------------------------------------------------------------
	private char[] threeLetterCode;
	private float mw;




	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public Pair3LetterCodeAndMW(char[] threeLetterCode, float mw, char[] fourLetterCode, char[] chainName) {
		super(fourLetterCode, chainName);
		this.threeLetterCode = threeLetterCode;
		this.mw = mw;
	}




	// -------------------------------------------------------------------
	// Public Interface
	// -------------------------------------------------------------------
	@Override public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(super.toString());
		result.append(String.valueOf(threeLetterCode) + " ");
		result.append(mw);
		return result.toString();
	}




	// -------------------------------------------------------------------
	// Getters and Setters
	// -------------------------------------------------------------------
	public char[] getThreeLetterCode() {
		return threeLetterCode;
	}

	public float getMw() {
		return mw;
	}
}
