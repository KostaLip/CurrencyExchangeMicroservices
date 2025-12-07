package util.exceptions;

public class UserGetBankAccountEmailException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;

	public UserGetBankAccountEmailException() {
		
	}
	
	public UserGetBankAccountEmailException(String message) {
		super(message);
	}
	
}
