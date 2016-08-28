package structure;

import java.util.Arrays;

public enum MyMonomerType {
	AMINOACID("amino".toCharArray()), 
	HETATM("hetatm".toCharArray()), 
	NUCLEOTIDE("nucleotide".toCharArray());

	private char[] type;
	MyMonomerType(char[] type){
		this.type = type;
	}


	public char[] getType() {
		return type;
	}


	public static MyMonomerType getEnumType(char[] type){

		for (MyMonomerType enumType: MyMonomerType.values()){
			if (Arrays.equals(enumType.getType(), type)){
				return enumType;
			}
		}
		return null;
	}
}
