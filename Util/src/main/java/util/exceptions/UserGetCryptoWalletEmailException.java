package util.exceptions;

public class UserGetCryptoWalletEmailException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public UserGetCryptoWalletEmailException( ) {
		
	}
	
	public UserGetCryptoWalletEmailException(String message) {
		super(message);
	}

}
