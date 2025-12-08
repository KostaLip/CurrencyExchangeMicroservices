package currencyConversion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import api.dtos.BankAccountDto;
import api.dtos.CurrencyConversionDto;
import api.dtos.CurrencyExchangeDto;
import api.proxies.BankAccountProxy;
import api.proxies.CurrencyExchangeProxy;
import api.services.CurrencyConversionService;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import util.exceptions.CurrencyAmountException;
import util.exceptions.CurrencyDoesntExistException;
import util.exceptions.InvalidQuantityException;

@RestController
public class CurrencyConversionServiceImpl implements CurrencyConversionService{

	private RestTemplate template = new RestTemplate();
	
	@Autowired
	private CurrencyExchangeProxy proxy;
	
	@Autowired
	private BankAccountProxy bankAccountProxy;
	
	Retry retry;
	CurrencyExchangeDto response;

	private CurrencyConversionApplication currencyConversionApplication;

	public CurrencyConversionServiceImpl(RetryRegistry registry, CurrencyConversionApplication currencyConversionApplication) {
		retry = registry.retry("default");
		this.currencyConversionApplication = currencyConversionApplication;
	}

	@Override
	public ResponseEntity<?> getConversionFeign(String from, String to, BigDecimal quantity, String email) {
		if(from.equalsIgnoreCase(to)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Currencies must be different");
		}
		if(quantity.compareTo(BigDecimal.valueOf(300.0)) >= 1) {
			throw new InvalidQuantityException(String.format("Quantity of %s is too large", quantity));
		}
		checkCurrencyExists(from, to);
		BankAccountDto bankAccount = bankAccountProxy.getBankAccount(email);
		System.out.println(bankAccount);
		if(checkCurrencyAmount(quantity, bankAccount, from)) {
			retry.executeSupplier(() -> response = proxy.getExchangeFeign(from, to).getBody());
			CurrencyConversionDto dto = new CurrencyConversionDto(response, quantity);
			BigDecimal convertedAmount = dto.getConversionResault().getConvertedAmount();
			
			setCurrencyAmount(bankAccount, getCurrencyAmount(bankAccount, from).subtract(quantity), from);
			setCurrencyAmount(bankAccount, getCurrencyAmount(bankAccount, to).add(convertedAmount), to);
			
			System.out.println(bankAccount);
			
			bankAccountProxy.updateBankAccount(bankAccount);
			
			Map<String, Object> response = new HashMap<>();
			response.put("message", "Conversion successful! " + from + ": " + quantity + " -> " + to + " " + convertedAmount);
			response.put("data", bankAccount);

			return ResponseEntity.status(HttpStatus.OK).body(response);
		}
		
		throw new CurrencyAmountException("You do not have enough currency amount", from,
				getCurrencyAmount(bankAccount, from));
	}
	
	private BigDecimal getCurrencyAmount(BankAccountDto account, String currency) {
		switch(currency) {
		case "USD": return account.getUsdAmount();
		case "EUR": return account.getEurAmount();
		case "RSD": return account.getRsdAmount();
		case "GBP": return account.getGbpAmount();
		case "CHF": return account.getChfAmount();
		default: return BigDecimal.ZERO;
		}
	}
	
	private void setCurrencyAmount(BankAccountDto account, BigDecimal ammount, String currency) {
		switch(currency) {
		case "USD":
			account.setUsdAmount(ammount);
			break;
		case "EUR":
			account.setEurAmount(ammount);
			break;
		case "RSD":
			account.setRsdAmount(ammount);
			break;
		case "GBP":
			account.setGbpAmount(ammount);
			break;
		case "CHF":
			account.setChfAmount(ammount);
			break;
		default:
			throw new CurrencyDoesntExistException("Currency " + currency + " does not exist");
		}
	}
	
	private void checkCurrencyExists(String from, String to) {
		List<String> validCurrencies = new ArrayList<String>();
		validCurrencies.add("USD");
		validCurrencies.add("EUR");
		validCurrencies.add("RSD");
		validCurrencies.add("GBP");
		validCurrencies.add("CHF");
		if(!validCurrencies.contains(from)) {
			throw new CurrencyDoesntExistException("Currency " + from + " does not exist");
		} else if(!validCurrencies.contains(to)) {
			throw new CurrencyDoesntExistException("Currency " + to + " does not exist");
		}
	}
	
	private boolean checkCurrencyAmount(BigDecimal quantity, BankAccountDto account, String currency) {
		switch(currency) {
		case "USD":
			return quantity.compareTo(account.getUsdAmount()) <= 0;
		case "EUR":
			return quantity.compareTo(account.getEurAmount()) <= 0;
		case "RSD":
			return quantity.compareTo(account.getRsdAmount()) <= 0;
		case "GBP":
			return quantity.compareTo(account.getGbpAmount()) <= 0;
		case "CHF":
			return quantity.compareTo(account.getChfAmount()) <= 0;
		default:
			throw new CurrencyDoesntExistException("Currency " + currency + " does not exist");
		}
	}
	
	@Override
	public ResponseEntity<?> getConversion(String from, String to, BigDecimal quantity) {
		
		if(quantity.compareTo(BigDecimal.valueOf(300.0)) >= 1) {
			throw new InvalidQuantityException(String.format("Quantity of %s is too large", quantity));
		}
		
		String endpoint = "http://localhost:8000/currency-exchange?from=" + from + "&to=" + to;
		ResponseEntity<CurrencyExchangeDto> response = template.getForEntity(endpoint, CurrencyExchangeDto.class);
		if(response.getStatusCode() == HttpStatus.NOT_FOUND) {
			return new ResponseEntity<String>("Unable to fetch exchange", HttpStatus.BAD_REQUEST);
		}
		return ResponseEntity.ok(new CurrencyConversionDto(response.getBody(), quantity));
	}
	
	public ResponseEntity<?> fallback(CallNotPermittedException e) {
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Currency conversion service is currently unavailable, Circuit Breaker is in open state");
	}
	
}
