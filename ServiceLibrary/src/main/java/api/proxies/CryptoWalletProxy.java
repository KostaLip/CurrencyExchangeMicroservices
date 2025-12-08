package api.proxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import api.dtos.CryptoWalletDto;

@FeignClient("crypto-wallet")
public interface CryptoWalletProxy {

	@PostMapping("/crypto-wallet")
	ResponseEntity<?> createCryptoWallet(@RequestParam String email);
	
	@DeleteMapping("/crypto-wallet")
	ResponseEntity<?> deleteCryptoWallet(@RequestParam String email);
	
	@GetMapping("/crypto-wallet/email/update")
	CryptoWalletDto getCryptoWallet(@RequestParam String email);
	
	@PutMapping("/crypto-wallet")
	ResponseEntity<?> updateCryptoWallet(@RequestBody CryptoWalletDto dto);
	
}
