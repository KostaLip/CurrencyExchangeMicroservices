package api.services;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import api.dtos.CryptoWalletDto;

@Service
public interface CryptoWalletService {

	@GetMapping("/crypto-wallet")
	List<CryptoWalletDto> getAllCryptoWallets();
	
	@GetMapping("/crypto-wallet/email")
	ResponseEntity<?> getCryptoWalletByEmail(@RequestParam String email, @RequestHeader(value = "X-Auth-Role") String role, 
			@RequestHeader(value = "X-Auth-Email") String currentEmail);
	
	@GetMapping("/crypto-wallet/email/update")
	CryptoWalletDto getCryptoWallet(@RequestParam String email);
	
	@PostMapping("/crypto-wallet")
	ResponseEntity<?> createCryptoWallet(@RequestParam String email);
	
	@PutMapping("/crypto-wallet")
	ResponseEntity<?> updateCryptoWallet(@RequestBody CryptoWalletDto dto);
	
	@DeleteMapping("/crypto-wallet")
	ResponseEntity<?> deleteCryptoWallet(@RequestParam String email);
	
}
