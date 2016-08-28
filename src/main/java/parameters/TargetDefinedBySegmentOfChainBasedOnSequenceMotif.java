package parameters;

public class TargetDefinedBySegmentOfChainBasedOnSequenceMotif implements TargetsIfc{
	//------------------------
	// Class variables
	//------------------------
	private String fileName;
	private String sequence;
	private boolean useSimilarSequences;


	private int minLength;
	private int maxLength;




	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public TargetDefinedBySegmentOfChainBasedOnSequenceMotif(String fileName, String sequence, boolean useSimilarSequences, int minLength, int maxLength){
		this.fileName = fileName;
		this.sequence = sequence;
		this.useSimilarSequences = useSimilarSequences;
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
		sb.append(sequence + " " + useSimilarSequences + "  " + minLength + "   " + maxLength + "  " + fileName);
		return sb.toString();
	}



	// -------------------------------------------------------------------
	// Getters & Setters
	// -------------------------------------------------------------------
	public String getSequence() {
		return sequence;
	}


	public boolean isUseSimilarSequences() {
		return useSimilarSequences;
	}


	public int getMinLength() {
		return minLength;
	}

	public int getMaxLength() {
		return maxLength;
	}
}