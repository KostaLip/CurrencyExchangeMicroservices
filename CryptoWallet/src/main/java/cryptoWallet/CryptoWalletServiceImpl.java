package cryptoWallet;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import api.dtos.CryptoWalletDto;
import api.services.CryptoWalletService;
import util.exceptions.UserGetCryptoWalletEmailException;

@RestController
public class CryptoWalletServiceImpl implements CryptoWalletService{

	@Autowired
	private CryptoWalletRepository repo;
	
	@Override
	public List<CryptoWalletDto> getAllCryptoWallets() {
		List<CryptoWalletModel> models = repo.findAll();
		List<CryptoWalletDto> dtos = new ArrayList<CryptoWalletDto>();
		for(CryptoWalletModel model : models) {
			dtos.add(convertFromModelToDto(model));
		}
		return dtos;
	}

	@Override
	public ResponseEntity<?> getCryptoWalletByEmail(String email, String role, String currentEmail) {
		CryptoWalletModel wallet = repo.findByEmail(email);
		if(role.equalsIgnoreCase("USER") && !email.equalsIgnoreCase(currentEmail)) {
			throw new UserGetCryptoWalletEmailException("You can only get details about your crypto wallet");
		}
		if(wallet == null) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("User with passed email does not have crypto wallet");
		}
		return ResponseEntity.status(HttpStatus.OK).body(convertFromModelToDto(wallet));
	}

	@Override
	public ResponseEntity<?> createCryptoWallet(String email) {
		CryptoWalletModel model = new CryptoWalletModel(email, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
		return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(model));
	}

	@Override
	public ResponseEntity<?> updateCryptoWallet(CryptoWalletDto dto) {
		CryptoWalletModel wallet = repo.findByEmail(dto.getEmail());
		if(wallet == null) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("User with passed email does not have crypto wallet");
		}
		if(dto.getBtcAmount().compareTo(BigDecimal.ZERO) < 0 || dto.getEthAmount().compareTo(BigDecimal.ZERO) < 0 || 
				dto.getSolAmount().compareTo(BigDecimal.ZERO) < 0) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Each crypto amount must be greater or equal to 0");
		}
		repo.updateCryptoWallet(dto.getEmail(), dto.getBtcAmount(), dto.getEthAmount(), dto.getSolAmount());
		return ResponseEntity.status(HttpStatus.OK).body(dto);
	}

	@Override
	public ResponseEntity<?> deleteCryptoWallet(String email) {
		CryptoWalletModel cryptoWallet = repo.findByEmail(email);
		repo.delete(cryptoWallet);
		return ResponseEntity.status(HttpStatus.OK)
		        .body(Map.of("message", String.format(
		            "Crypto Wallet with email: %s, has been successfully deleted", email)));
	}
	
	private CryptoWalletDto convertFromModelToDto(CryptoWalletModel model) {
		return new CryptoWalletDto(model.getEmail(), model.getBtcAmount(), model.getEthAmount(),
				model.getSolAmount());
	}

	@Override
	public CryptoWalletDto getCryptoWallet(String email) {
		return convertFromModelToDto(repo.findByEmail(email));
	}

}
