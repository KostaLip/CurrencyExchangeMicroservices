package util.exceptions;

public class AdminCreateNonUserBankAccountException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public AdminCreateNonUserBankAccountException() {
		
	}
	
	public AdminCreateNonUserBankAccountException(String message) {
		super(message);
	}

}
