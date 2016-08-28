package parameters;

public class TargetDefinedByWholeChain implements TargetsIfc{
	//------------------------
	// Class variables
	//------------------------
	private String fileName;
	private int minLength;
	private int maxLength;




	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public TargetDefinedByWholeChain(String fileName, int minLength, int maxLength){
		this.fileName = fileName;
		this.minLength = minLength;
		this.maxLength = maxLength;
	}




	// -------------------------------------------------------------------
	// Interface
	// -------------------------------------------------------------------
	@Override
	public String getFileName() {
		return fileName;
	}




	// -------------------------------------------------------------------
	// Override
	// -------------------------------------------------------------------
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(minLength + " " + maxLength + "  " + fileName);
		return sb.toString();
	}




	// -------------------------------------------------------------------
	// Getters & Setters
	// -------------------------------------------------------------------
	public int getMinLength() {
		return minLength;
	}


	public int getMaxLength() {
		return maxLength;
	}
}