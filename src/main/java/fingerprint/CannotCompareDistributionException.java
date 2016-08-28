package fingerprint;

public class CannotCompareDistributionException extends Exception {
	private static final long serialVersionUID = 1L;


	public CannotCompareDistributionException(){
		super();
	}
	
	
	public CannotCompareDistributionException(String message){
		super(message);
	}
	
}