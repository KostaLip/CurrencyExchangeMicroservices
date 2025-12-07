package util.exceptions;

public class AdminUpdateException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public AdminUpdateException() {
		
	}
	
	public AdminUpdateException(String message) {
		super(message);
	}
	
}
