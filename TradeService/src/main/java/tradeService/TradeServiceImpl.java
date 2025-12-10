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
import api.dtos.CryptoExchangeDto;
import api.dtos.CryptoWalletDto;
import api.proxies.BankAccountProxy;
import api.proxies.CryptoWalletProxy;
import api.proxies.CurrencyExchangeProxy;
import api.services.TradeService;
import jakarta.transaction.Transactional;
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
	@Transactional
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
	
	private ResponseEntity<?> tradeFromFiatToCrypto(String from, String to, BigDecimal quantity, String email) {
		BankAccountDto bankAccount = bankProxy.getBankAccount(email);
		CryptoWalletDto cryptoWallet = walletProxy.getCryptoWallet(email);
		
		BigDecimal currentCurrencyAmount = getCurrencyAmount(from, bankAccount);
		BigDecimal exchangeRate;
		BigDecimal totalToAdd;
		Map<String, Object> backResponse = new HashMap<>();
		
		if(currentCurrencyAmount.compareTo(quantity) < 0) {
			throw new CurrencyAmountException("You do not have enough currency for trade", from, currentCurrencyAmount);
		}
		
		if(from.equalsIgnoreCase("USD") || from.equalsIgnoreCase("EUR")) {
			exchangeRate = repo.findByFromAndTo(from, to).getExchangeRate();
			
			if(from.equalsIgnoreCase("USD")) {
				setCurrencyAmount(from, bankAccount, bankAccount.getUsdAmount().subtract(quantity));
			} else {
				setCurrencyAmount(from, bankAccount, bankAccount.getEurAmount().subtract(quantity));
			}
			
			totalToAdd = quantity.multiply(exchangeRate);
			setCryptoAmount(to, cryptoWallet, getCryptoAmount(to, cryptoWallet).add(totalToAdd));
			
			bankProxy.updateBankAccount(bankAccount);
			walletProxy.updateCryptoWallet(cryptoWallet);
			
			backResponse.put("data", cryptoWallet);
			backResponse.put("message", String.format("Successfully exchanged! %s %s --> %s %s", 
					quantity, from, totalToAdd, to));
			return ResponseEntity.status(HttpStatus.OK).body(backResponse);
		} else {
			BigDecimal currencyExchangeRate = currencyProxy.getExchangeFeign(from, "USD").getBody().getExchangeRate();
			BigDecimal baseQuantity = quantity;
			quantity = quantity.multiply(currencyExchangeRate);
			
			exchangeRate = repo.findByFromAndTo("USD", to).getExchangeRate();
			totalToAdd = quantity.multiply(exchangeRate);
			
			setCurrencyAmount(from, bankAccount, getCurrencyAmount(from, bankAccount).subtract(baseQuantity));
			setCryptoAmount(to, cryptoWallet, getCryptoAmount(to, cryptoWallet).add(totalToAdd));
			
			bankProxy.updateBankAccount(bankAccount);
			walletProxy.updateCryptoWallet(cryptoWallet);
			
			backResponse.put("data", cryptoWallet);
			backResponse.put("message", String.format(String.format("Successfully exchanged! %s %s --> %s %s --> %s %s", 
					baseQuantity, from, quantity, "USD", totalToAdd, to)));
			return ResponseEntity.status(HttpStatus.OK).body(backResponse);
		}
	}
	
	private ResponseEntity<?> tradeFromCryptoToFiat(String from, String to, BigDecimal quantity, String email) {
		BankAccountDto bankAccount = bankProxy.getBankAccount(email);
		CryptoWalletDto cryptoWallet = walletProxy.getCryptoWallet(email);
		
		BigDecimal currentCryptoAmount = getCryptoAmount(from, cryptoWallet);
		BigDecimal exchangeRate;
		BigDecimal totalToAdd;
		Map<String, Object> backResponse = new HashMap<>();
		
		if(currentCryptoAmount.compareTo(quantity) < 0) {
			throw new CryptoAmountException("You do not have enough crypto for trade", from, currentCryptoAmount);
		}
		
		if(to.equalsIgnoreCase("USD") || to.equalsIgnoreCase("EUR")) {
			exchangeRate = repo.findByFromAndTo(from, to).getExchangeRate();
			totalToAdd = quantity.multiply(exchangeRate);
			
			if(to.equalsIgnoreCase("USD")) {
				setCurrencyAmount(to, bankAccount, bankAccount.getUsdAmount().add(totalToAdd));
			} else {
				setCurrencyAmount(to, bankAccount, bankAccount.getEurAmount().add(totalToAdd));
			}
			
			setCryptoAmount(from, cryptoWallet, getCryptoAmount(from, cryptoWallet).subtract(quantity));
			
			bankProxy.updateBankAccount(bankAccount);
			walletProxy.updateCryptoWallet(cryptoWallet);
			
			backResponse.put("data", bankAccount);
			backResponse.put("message", String.format("Successfully exchanged! %s %s --> %s %s", 
					quantity, from, totalToAdd, to));
			return ResponseEntity.status(HttpStatus.OK).body(backResponse);
		} else {
			BigDecimal currencyExchangeRate = currencyProxy.getExchangeFeign("USD", to).getBody().getExchangeRate();
			BigDecimal baseQuantity = quantity;
			quantity = quantity.multiply(currencyExchangeRate);
			
			exchangeRate = repo.findByFromAndTo(from, "USD").getExchangeRate();
			totalToAdd = quantity.multiply(exchangeRate);
			
			setCurrencyAmount(to, bankAccount, getCurrencyAmount(to, bankAccount).add(totalToAdd));
			setCryptoAmount(from, cryptoWallet, getCryptoAmount(from, cryptoWallet).subtract(baseQuantity));
			
			bankProxy.updateBankAccount(bankAccount);
			walletProxy.updateCryptoWallet(cryptoWallet);
			
			backResponse.put("data", bankAccount);
			backResponse.put("message", String.format(String.format("Successfully exchanged! %s %s --> %s %s --> %s %s", 
					baseQuantity, from, baseQuantity.multiply(exchangeRate), "USD", totalToAdd, to)));
			return ResponseEntity.status(HttpStatus.OK).body(backResponse);
		}
	}
	
	private void setCryptoAmount(String crypto, CryptoWalletDto wallet, BigDecimal quantity) {
		switch(crypto) {
		case "BTC": wallet.setBtcAmount(quantity); break;
		case "ETH": wallet.setEthAmount(quantity); break;
		case "SOL": wallet.setSolAmount(quantity); break;
		}
	}
	
	private void setCurrencyAmount(String currency, BankAccountDto account, BigDecimal quantity) {
		switch(currency) {
		case "USD": account.setUsdAmount(quantity); break;
		case "EUR": account.setEurAmount(quantity); break;
		case "RSD": account.setRsdAmount(quantity); break;
		case "GBP": account.setGbpAmount(quantity); break;
		case "CHF": account.setChfAmount(quantity); break;
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
