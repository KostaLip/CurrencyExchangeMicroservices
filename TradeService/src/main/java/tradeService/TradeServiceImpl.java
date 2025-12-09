package tradeService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import api.dtos.BankAccountDto;
import api.dtos.CryptoWalletDto;
import api.proxies.BankAccountProxy;
import api.proxies.CryptoWalletProxy;
import api.proxies.CurrencyExchangeProxy;
import api.services.TradeService;
import util.exceptions.CryptoAmountException;
import util.exceptions.CryptoDoesNotExistException;
import util.exceptions.CurrencyAmountException;
import util.exceptions.CurrencyDoesntExistException;
import util.exceptions.InvalidQuantityException;
import util.exceptions.TradeCombinationException;

@RestController
public class TradeServiceImpl implements TradeService{

	@Autowired
	private TradeServiceRepository repo;
	
	@Autowired
	private BankAccountProxy bankProxy;
	
	@Autowired
	private CryptoWalletProxy walletProxy;
	
	@Autowired
	private CurrencyExchangeProxy currencyProxy;
	
	
	@Override
	public ResponseEntity<?> trade(String from, String to, BigDecimal quantity, String email) {
		if (quantity.compareTo(BigDecimal.ZERO) < 0) {
			throw new InvalidQuantityException("Quantity can not be negative number");
		}
		if(from.equalsIgnoreCase(to)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Currencies must be different");
		}
		if(isFiatCurrency(from) && isCryptoCurrency(to)) {
			return tradeFromFiatToCrypto(from, to, quantity, email);
		} else if(isCryptoCurrency(from) && isFiatCurrency(to)) {
			return tradeFromCryptoToFiat(from, to, quantity, email);
		}
		throw new TradeCombinationException(String.format("Trade combination from %s -> %s is invalid", 
				from, to));
	}
	
	private ResponseEntity<?> tradeFromCryptoToFiat(String from, String to, BigDecimal quantity, String email) {
		BankAccountDto bankAccount = bankProxy.getBankAccount(email);
		CryptoWalletDto cryptoWallet = walletProxy.getCryptoWallet(email);
		BigDecimal currentCryptoAmount = getCryptoAmount(from, cryptoWallet);
		
		if(currentCryptoAmount.compareTo(quantity) < 0) {
			throw new CryptoAmountException("You do not have enough crypto for trade", from, currentCryptoAmount);
		}
		if(to.equalsIgnoreCase("USD") || to.equalsIgnoreCase("EUR")) {
			return tradeCryptoUsdEur(from, to, quantity, bankAccount, cryptoWallet);
		} else {
			
		}
	}
	
	private ResponseEntity<?> tradeCryptoUsdEur(String from, String to, BigDecimal quantity, BankAccountDto bankAccount, CryptoWalletDto cryptoWallet) {
		BigDecimal currentCurrencyAmount = getCurrencyAmount(to, bankAccount);
		BigDecimal currentCryptoAmount = getCryptoAmount(from, cryptoWallet);
		BigDecimal exchangeRate = repo.findByFromAndTo(from, to).getExchangeRate();
		
		setCryptoAmount(from, cryptoWallet, currentCryptoAmount.subtract(quantity));
		walletProxy.updateCryptoWallet(cryptoWallet);
		
		setCurrencyAmount(to, bankAccount, currentCurrencyAmount.add(quantity.multiply(exchangeRate)));
		bankProxy.updateBankAccount(bankAccount);
		
		Map<String, Object> backResponse = new HashMap<>();
		backResponse.put("message", "Conversion successful! " + from + ": " + quantity + " -> " + to + " " + quantity.multiply(exchangeRate));
		backResponse.put("data", bankAccount);
		return ResponseEntity.status(HttpStatus.OK).body(backResponse);
	}
	
	private ResponseEntity<?> tradeFromFiatToCrypto(String from, String to, BigDecimal quantity, String email) {
		BankAccountDto bankAccount = bankProxy.getBankAccount(email);
		CryptoWalletDto cryptoWallet = walletProxy.getCryptoWallet(email);
		BigDecimal currentCurrencyAmount = getCurrencyAmount(from, bankAccount);
		
		if(currentCurrencyAmount.compareTo(quantity) < 0) {
			throw new CurrencyAmountException("You do not have enough currency for trade", from, currentCurrencyAmount);
		}
		if(from.equalsIgnoreCase("USD") || from.equalsIgnoreCase("EUR")) {
			return tradeUsdEurCrypto(from, to, quantity, bankAccount, cryptoWallet);
		} else {
			tradeNotUsdEur(from, bankAccount, currentCurrencyAmount, quantity);
			return tradeUsdEurCrypto("USD", to, quantity, bankAccount, cryptoWallet);
		}
	}
	
	private void tradeNotUsdEur(String from, BankAccountDto account, BigDecimal currentAmount, BigDecimal quantity) {
		BigDecimal exchangeRate = currencyProxy.getExchangeFeign(from, "USD").getBody().getExchangeRate();
		
		setCurrencyAmount(from, account, currentAmount.subtract(quantity));
		setCurrencyAmount("USD", account, getCurrencyAmount("USD", account).add(quantity.multiply(exchangeRate)));
	}
	
	private ResponseEntity<?> tradeUsdEurCrypto(String from, String to, BigDecimal quantity, BankAccountDto bankAccount, CryptoWalletDto cryptoWallet) {
		BigDecimal currentCurrencyAmount = getCurrencyAmount(from, bankAccount);
		BigDecimal currentCryptoAmount = getCryptoAmount(to, cryptoWallet);
		BigDecimal exchangeRate = repo.findByFromAndTo(from, to).getExchangeRate();
		
		setCurrencyAmount(from, bankAccount, currentCurrencyAmount.subtract(quantity));
		bankProxy.updateBankAccount(bankAccount);
		
		setCryptoAmount(to, cryptoWallet, currentCryptoAmount.add(quantity.multiply(exchangeRate)));
		walletProxy.updateCryptoWallet(cryptoWallet);
		
		Map<String, Object> backResponse = new HashMap<>();
		backResponse.put("message", "Conversion successful! " + from + ": " + quantity + " -> " + to + " " + quantity.multiply(exchangeRate));
		backResponse.put("data", cryptoWallet);
		return ResponseEntity.status(HttpStatus.OK).body(backResponse);
	}
	
	private void setCryptoAmount(String crypto, CryptoWalletDto wallet, BigDecimal quantity) {
		switch(crypto) {
		case "BTC": wallet.setBtcAmount(quantity);
		case "ETH": wallet.setEthAmount(quantity);
		case "SOL": wallet.setSolAmount(quantity);
		}
	}
	
	private void setCurrencyAmount(String currency, BankAccountDto account, BigDecimal quantity) {
		switch(currency) {
		case "USD": account.setUsdAmount(quantity);
		case "EUR": account.setEurAmount(quantity);
		case "RSD": account.setRsdAmount(quantity);
		case "GBP": account.setGbpAmount(quantity);
		case "CHF": account.setChfAmount(quantity);
		}
	}
	
	private BigDecimal getCryptoAmount(String crypto, CryptoWalletDto wallet) {
		switch(crypto) {
		case "BTC": return wallet.getBtcAmount();
		case "ETH": return wallet.getEthAmount();
		case "SOL": return wallet.getSolAmount();
		default: throw new CryptoDoesNotExistException("Invalid crypto name", Arrays.asList("BTC", "ETH", "SOL"));
		}
	}
	
	private BigDecimal getCurrencyAmount(String currency, BankAccountDto account) {
		switch(currency) {
		case "USD": return account.getUsdAmount();
		case "EUR": return account.getEurAmount();
		case "RSD": return account.getRsdAmount();
		case "GBP": return account.getGbpAmount();
		case "CHF": return account.getChfAmount();
		default: throw new CurrencyDoesntExistException(String.format("Currency: %s does not exist", currency));
		}
	}
	
	private boolean isFiatCurrency(String currency) {
		if(Arrays.asList("USD", "EUR", "RSD", "GBP", "CHF").contains(currency)) return true;
		return false;
	}
	
	private boolean isCryptoCurrency(String crypto) {
		if(Arrays.asList("BTC", "ETH", "SOL").contains(crypto)) return true;
		return false;
	}

}
