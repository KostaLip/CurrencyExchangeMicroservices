package api.services;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import api.dtos.BankAccountDto;

public interface BankAccountService {

	@GetMapping("/bankAccounts")
	List<BankAccountDto> getAllBankAccounts();
	
	@GetMapping("/bankAccounts/email")
	ResponseEntity<?> getBankAccountByEmail(@RequestParam String email, @RequestHeader(value = "X-Auth-Role") String role, 
			@RequestHeader(value = "X-Auth-Email") String currentEmail);
	
	@PostMapping("/bankAccounts")
	ResponseEntity<?> createBankAccount(@RequestParam String email);
	
	@PutMapping("/bankAccounts")
	ResponseEntity<?> updateBankAccount(@RequestBody BankAccountDto dto);
	
	@DeleteMapping("/bankAccounts")
	ResponseEntity<?> deleteBankAccount(@RequestParam String email);
	
}
