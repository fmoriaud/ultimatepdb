package parameters;

public class TargetDefinedByHetAtm implements TargetsIfc{
	//------------------------
	// Class variables
	//------------------------
	private String fileName;
	private int minMW;
	private int maxMW;




	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public TargetDefinedByHetAtm(String fileName, int minMW, int maxMW){
		this.fileName = fileName;
		this.minMW = minMW;
		this.maxMW = maxMW;
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
		sb.append(minMW + " " + maxMW + " " + fileName);
		return sb.toString();
	}




	// -------------------------------------------------------------------
	// Getters & Setters
	// -------------------------------------------------------------------
	public int getMinMW() {
		return minMW;
	}


	public int getMaxMW() {
		return maxMW;
	}
}