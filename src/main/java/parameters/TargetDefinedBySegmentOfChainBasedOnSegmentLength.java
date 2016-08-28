package parameters;

public class TargetDefinedBySegmentOfChainBasedOnSegmentLength implements TargetsIfc{
	//------------------------
	// Class variables
	//------------------------
	private String fileName;
	private int segmentLength;
	private int minLength;
	private int maxLength;




	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public TargetDefinedBySegmentOfChainBasedOnSegmentLength(String fileName, int segmentLength,  int minLength, int maxLength){
		this.fileName = fileName;
		this.segmentLength = segmentLength;
		this.minLength = minLength;
		this.maxLength = maxLength;
	}




	// -------------------------------------------------------------------
	// Override
	// -------------------------------------------------------------------
	@Override
	public String getFileName() {
		return fileName;
	}



	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(segmentLength +  "  " + minLength + "   " + maxLength + "  " + fileName);
		return sb.toString();
	}




	// -------------------------------------------------------------------
	// Getters & Setters
	// -------------------------------------------------------------------

	public int getSegmentLength() {
		return segmentLength;
	}
	public int getMinLength() {
		return minLength;
	}

	public int getMaxLength() {
		return maxLength;
	}
}