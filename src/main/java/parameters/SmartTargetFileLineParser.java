package parameters;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import mystructure.EnumMyReaderBiojava;

public class SmartTargetFileLineParser {

	//------------------------
	// Class variables
	//------------------------
	private AlgoParameters algoParameters;
	private EnumMyReaderBiojava enumMyReaderBiojava;

	private String fourLettercode;
	private String chainName;
	private int chainLengthFromFile;

	private String threeLettercode;
	private float mw;




	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public SmartTargetFileLineParser(AlgoParameters algoParameters, EnumMyReaderBiojava enumMyReaderBiojava){
		this.algoParameters = algoParameters;
		this.enumMyReaderBiojava = enumMyReaderBiojava;
	}




	// -------------------------------------------------------------------
	// Public
	// -------------------------------------------------------------------
	public boolean parseLineHetAtm(String line){

		List<String> splittedLine = parse(line);

		if (splittedLine.size() >= 4){
			// try the hetatom

			try {
				fourLettercode = splittedLine.get(0);
				chainName = splittedLine.get(1);
				threeLettercode = splittedLine.get(2);
				mw = Float.valueOf(splittedLine.get(3));
				// assuming taking the first occurence but should be improved

			}catch(Exception e){
				System.out.println("Problem in parsing line as Hetatm : " + line);
				return false;
			}
			return true;
		}
		return false;
	}



	public boolean parseLineChain(String line){

		List<String> splittedLine = parse(line);	

		if (splittedLine.size() >= 3){

			try {
				fourLettercode = splittedLine.get(0);
				chainName = splittedLine.get(1);
				chainLengthFromFile = Integer.valueOf(splittedLine.get(2));

			}catch(Exception e){
				System.out.println("Problem in parsing line as chains : " + line);
				return false;
			}
			return true;
		}
		return false;
	}




	// -------------------------------------------------------------------
	// Implementation
	// -------------------------------------------------------------------
	private List<String> parse(String line) {
		StringTokenizer tok = new StringTokenizer(line, ","); 

		List<String> splittedLine = new ArrayList<>();
		while ( tok.hasMoreElements() )  
		{  
			String next = (String) tok.nextElement();
			splittedLine.add(next);
		}
		return splittedLine;
	}




	// -------------------------------------------------------------------
	// Getters & Setters
	// -------------------------------------------------------------------
	public EnumMyReaderBiojava getEnumMyReaderBiojava() {
		return enumMyReaderBiojava;
	}

	public String getFourLettercode() {
		return fourLettercode;
	}

	public String getChainName() {
		return chainName;
	}

	public int getChainLengthFromFile() {
		return chainLengthFromFile;
	}

	public String getThreeLettercode() {
		return threeLettercode;
	}

	public float getMw() {
		return mw;
	}
}
