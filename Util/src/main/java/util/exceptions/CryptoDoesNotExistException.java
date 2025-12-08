package util.exceptions;

import java.util.List;

public class CryptoDoesNotExistException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private List<String> cryptos;
	
	public CryptoDoesNotExistException() {
		
	}
	
	public CryptoDoesNotExistException(String message, List<String> cryptos) {
		super(message);
		this.cryptos = cryptos;
	}

	public List<String> getCryptos() {
		return cryptos;
	}

	public void setCryptos(List<String> cryptos) {
		this.cryptos = cryptos;
	}

}
