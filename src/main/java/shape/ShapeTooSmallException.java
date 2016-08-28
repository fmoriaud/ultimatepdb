package shape;

public class ShapeTooSmallException extends Exception {

	private static final long serialVersionUID = -3648457504802011509L;

	
	public ShapeTooSmallException(){
		super();
	}
	
	
	public ShapeTooSmallException(String message){
		super(message);
	}
	
}
