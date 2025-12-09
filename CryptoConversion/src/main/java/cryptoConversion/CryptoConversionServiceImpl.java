package cryptoConversion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import api.dtos.CryptoConversionDto;
import api.dtos.CryptoExchangeDto;
import api.dtos.CryptoWalletDto;
import api.proxies.CryptoExchangeProxy;
import api.proxies.CryptoWalletProxy;
import api.services.CryptoConversionService;
import util.exceptions.CryptoAmountException;
import util.exceptions.CryptoDoesNotExistException;
import util.exceptions.InvalidQuantityException;

@RestController
public class CryptoConversionServiceImpl implements CryptoConversionService{

	@Autowired
	private CryptoExchangeProxy proxy;
	
	@Autowired
	private CryptoWalletProxy walletProxy;
	
	@Override
	public ResponseEntity<?> getConversionFeign(String from, String to, BigDecimal quantity, String email) {
		if (quantity.compareTo(BigDecimal.ZERO) < 0) {
			throw new InvalidQuantityException("Quantity can not be negative number");
		}
		if(from.equalsIgnoreCase(to)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cryptos must be different");
		}
		if(quantity.compareTo(BigDecimal.valueOf(300.0)) >= 1) {
			throw new InvalidQuantityException(String.format("Quantity of %s is too large", quantity));
		}
		checkCryptoExists(from, to);
		CryptoWalletDto wallet = walletProxy.getCryptoWallet(email);
		System.out.println(wallet);
		if(checkCryptoAmount(quantity, wallet, from)) {
			CryptoExchangeDto response = proxy.getExchangeFeign(from, to).getBody();
			CryptoConversionDto dto = new CryptoConversionDto(response, quantity);
			BigDecimal convertedAmount = dto.getConversionResault().getConvertedAmount();
			
			setCryptoAmount(wallet, getCryptoAmount(wallet, from).subtract(quantity), from);
			setCryptoAmount(wallet, getCryptoAmount(wallet, to).add(convertedAmount), to);
			
			System.out.println(wallet);
			
			walletProxy.updateCryptoWallet(wallet);
			
			Map<String, Object> backResponse = new HashMap<>();
			backResponse.put("message", "Conversion successful! " + from + ": " + quantity + " -> " + to + " " + convertedAmount);
			backResponse.put("data", wallet);

			return ResponseEntity.status(HttpStatus.OK).body(backResponse);
		}
		
		throw new CryptoAmountException("You do not have enough crypto amount", from,
				getCryptoAmount(wallet, from));
	}
	
	private BigDecimal getCryptoAmount(CryptoWalletDto wallet, String crypto) {
		switch(crypto) {
		case "BTC": return wallet.getBtcAmount();
		case "ETH": return wallet.getEthAmount();
		case "SOL": return wallet.getSolAmount();
		default: return BigDecimal.ZERO;
		}
	}
	
	private boolean checkCryptoAmount(BigDecimal quantity, CryptoWalletDto wallet, String crypto) {
		switch(crypto) {
		case "BTC":
			return quantity.compareTo(wallet.getBtcAmount()) <= 0;
		case "ETH":
			return quantity.compareTo(wallet.getEthAmount()) <= 0;
		case "SOL":
			return quantity.compareTo(wallet.getSolAmount()) <= 0;
		default:
			List<String> validCryptos = new ArrayList<String>();
			validCryptos.add("BTC");
			validCryptos.add("ETH");
			validCryptos.add("SOL");
			throw new CryptoDoesNotExistException("Crypto " + crypto + " does not exist", validCryptos);
		}
	}
	
	private void setCryptoAmount(CryptoWalletDto wallet, BigDecimal ammount, String crypto) {
		switch(crypto) {
		case "BTC":
			wallet.setBtcAmount(ammount);
			break;
		case "ETH":
			wallet.setEthAmount(ammount);
			break;
		case "SOL":
			wallet.setSolAmount(ammount);
			break;
		default:
			List<String> validCryptos = new ArrayList<String>();
			validCryptos.add("BTC");
			validCryptos.add("ETH");
			validCryptos.add("SOL");
			throw new CryptoDoesNotExistException("Crypto " + wallet + " does not exist", validCryptos);
		}
	}
	
	private void checkCryptoExists(String from, String to) {
		List<String> validCryptos = new ArrayList<String>();
		validCryptos.add("BTC");
		validCryptos.add("ETH");
		validCryptos.add("SOL");
		if(!validCryptos.contains(from)) {
			throw new CryptoDoesNotExistException("Crypto " + from + " does not exist", validCryptos);
		} else if(!validCryptos.contains(to)) {
			throw new CryptoDoesNotExistException("Crypto " + to + " does not exist", validCryptos);
		}
	}

}
