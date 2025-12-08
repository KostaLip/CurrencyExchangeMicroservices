package util.exceptions;

import java.util.ArrayList;
import java.util.List;

public class CurrencyDoesntExistException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	List<String> currencies;
	
	public CurrencyDoesntExistException() {
		
	}
	
	public CurrencyDoesntExistException(String message) {
		super(message);
		this.currencies = new ArrayList<String>();
		currencies.add("USD");
		currencies.add("EUR");
		currencies.add("RSD");
		currencies.add("GBP");
		currencies.add("CHF");
	}
	
	public CurrencyDoesntExistException(String message, List<String> currencies) {
		super(message);
		this.currencies = currencies;
	}

	public List<String> getCurrencies() {
		return currencies;
	}

	public void setCurencies(List<String> curencies) {
		this.currencies = curencies;
	}
	
}
