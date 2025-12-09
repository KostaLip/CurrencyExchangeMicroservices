package util.exceptions;

import java.math.BigDecimal;

public class CryptoAmountException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private String crypto;
	private BigDecimal amount;
	
	public CryptoAmountException() {
		
	}
	
	public CryptoAmountException(String message) {
		super(message);
	}
	
	public CryptoAmountException(String message, String crypto, BigDecimal amount) {
		super(message);
		this.crypto = crypto;
		this.amount = amount;
	}
	
	public String getCrypto() {
		return crypto;
	}
	
	public void setCrypto(String crypto) {
		this.crypto = crypto;
	}
	
	public BigDecimal getAmount() {
		return amount;
	}
	
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

}
