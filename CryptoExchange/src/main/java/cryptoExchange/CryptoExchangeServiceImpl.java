package cryptoExchange;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import api.dtos.CryptoExchangeDto;
import api.services.CryptoExchangeService;
import util.exceptions.CryptoDoesNotExistException;
import util.exceptions.NoDataFoundException;

@RestController
public class CryptoExchangeServiceImpl implements CryptoExchangeService{

	@Autowired
	private CryptoExchangeRepository repo;
	
	@Override
	public ResponseEntity<?> getCryptoExchange(String from, String to) {
		String missingCrypto = null;
		List<String> validCryptos = repo.findAllDistinctCryptos();
		
		if (!isValidCrypto(from)) {
			missingCrypto = from;
		} else if (!isValidCrypto(to)) {
			missingCrypto = to;
		}
		
		if(missingCrypto != null) {
			throw new CryptoDoesNotExistException(
					String.format("Crypto %s does not exist in the database", missingCrypto),
					validCryptos);
		}
		
		CryptoExchangeModel dbResponse = repo.findByFromAndTo(from, to);
		
		if(dbResponse == null) {
			throw new NoDataFoundException(
					String.format("Requested exchange rate from: [%s to %s] does not exist", from, to),
					validCryptos);
		}
		CryptoExchangeDto dto = new CryptoExchangeDto(dbResponse.getFrom(),dbResponse.getTo(),dbResponse.getExchangeRate());
		
		return ResponseEntity.ok(dto);
	}
	
	public boolean isValidCrypto(String crypto) {
		List<String> cryptos = repo.findAllDistinctCryptos();
		for (String c: cryptos) {
			if (c.equalsIgnoreCase(crypto)) {
				return true;
			}
		}
		return false;
	}

}
