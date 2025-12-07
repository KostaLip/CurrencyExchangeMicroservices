package api.proxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("bank-account")
public interface BankAccountProxy {

	@PostMapping("/bankAccounts")
	ResponseEntity<?> createBankAccount(@RequestParam String email);
	
	@DeleteMapping("/bankAccounts")
	ResponseEntity<?> deleteBankAccount(@RequestParam String email);
	
}
