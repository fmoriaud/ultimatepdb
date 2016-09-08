package mystructure;

import java.util.Arrays;

public enum BigHetatmResidues {

	HEM("HEM");

	private String threeLetterCode;

	BigHetatmResidues(String threeLetterCode){
		this.threeLetterCode = threeLetterCode;
	}

	public char[] getThreeLetterCode() {
		return threeLetterCode.toCharArray();
	}

	
	/**
	 * Return true is the myMonomer is a big residue
	 * Big residue is defined by the three letter code, currently only HEM
	 * @param myMonomer
	 * @return
	 */
	public static boolean isMyMonomerABigResidue(MyMonomerIfc myMonomer){

		for (BigHetatmResidues bigHetatmResidues: BigHetatmResidues.values()){
			if (Arrays.equals(bigHetatmResidues.threeLetterCode.toCharArray(), myMonomer.getThreeLetterCode())){
				return true;
			}
		}
		return false;
	}
}
