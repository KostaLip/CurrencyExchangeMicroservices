package util.exceptions;

import java.math.BigDecimal;

public class CurrencyAmountException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private String currency;
	private BigDecimal amount;

	public CurrencyAmountException() {
		
	}
	
	public CurrencyAmountException(String message) {
		super(message);
	}
	
	public CurrencyAmountException(String message, String currency, BigDecimal amount) {
		super(message);
		this.currency = currency;
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
}
