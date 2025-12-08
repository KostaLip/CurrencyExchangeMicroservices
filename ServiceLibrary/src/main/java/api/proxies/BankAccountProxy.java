package api.proxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import api.dtos.BankAccountDto;

@FeignClient("bank-account")
public interface BankAccountProxy {

	@PostMapping("/bankAccounts")
	ResponseEntity<?> createBankAccount(@RequestParam String email);
	
	@DeleteMapping("/bankAccounts")
	ResponseEntity<?> deleteBankAccount(@RequestParam String email);
	
	@GetMapping("/bankAccounts/email/update")
	BankAccountDto getBankAccount(@RequestParam String email);
	
	@PutMapping("/bankAccounts")
	ResponseEntity<?> updateBankAccount(@RequestBody BankAccountDto dto);
	
}
