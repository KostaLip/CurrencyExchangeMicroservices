package bankAccount;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import api.dtos.BankAccountDto;
import api.services.BankAccountService;
import util.exceptions.UserGetBankAccountEmailException;

@RestController
public class BankAccountServiceImpl implements BankAccountService{

	@Autowired
	private BankAccountRepository repo;
	
	@Override
	public List<BankAccountDto> getAllBankAccounts() {
		List<BankAccountModel> models = repo.findAll();
		List<BankAccountDto> dtos = new ArrayList<BankAccountDto>();
		for(BankAccountModel model : models) {
			dtos.add(convertFromModelToDto(model));
		}
		return dtos;
	}

	@Override
	public ResponseEntity<?> getBankAccountByEmail(String email, String role, String currentEmail) {
		BankAccountModel account = repo.findByEmail(email);
		if(role.equalsIgnoreCase("USER") && !email.equalsIgnoreCase(currentEmail)) {
			throw new UserGetBankAccountEmailException("You can only get details about your bank account");
		}
		if(account == null) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("User with passed email does not have bank account");
		}
		return ResponseEntity.status(HttpStatus.OK).body(convertFromModelToDto(account));
	}

	@Override
	public ResponseEntity<?> createBankAccount(String email) {
		BankAccountModel model = new BankAccountModel(email, BigDecimal.ZERO, BigDecimal.ZERO,
				BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
		return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(model));
	}

	@Override
	public ResponseEntity<?> updateBankAccount(BankAccountDto dto) {
		BankAccountModel account = repo.findByEmail(dto.getEmail());
		if(account == null) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("User with passed email does not have bank account");
		}
		if(dto.getUsdAmount().compareTo(BigDecimal.ZERO) < 0 || dto.getEurAmount().compareTo(BigDecimal.ZERO) < 0 || 
				dto.getRsdAmount().compareTo(BigDecimal.ZERO) < 0 || dto.getGbpAmount().compareTo(BigDecimal.ZERO) < 0 
				|| dto.getChfAmount().compareTo(BigDecimal.ZERO) < 0) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Each currency amount must be greater or equal to 0");
		}
		repo.updateBankAccount(dto.getEmail(), dto.getUsdAmount(), dto.getEurAmount(), dto.getRsdAmount(), dto.getGbpAmount(), 
				dto.getChfAmount());
		return ResponseEntity.status(HttpStatus.OK).body(dto);
	}
	
	@Override
	public ResponseEntity<?> deleteBankAccount(String email) {
		BankAccountModel bankAccount = repo.findByEmail(email);
		repo.delete(bankAccount);
		return ResponseEntity.status(HttpStatus.OK)
		        .body(Map.of("message", String.format(
		            "Bank Account with email: %s, has been successfully deleted", email)));
	}
	
	/*@Override
	public ResponseEntity<?> updateEmailBankAccount(String oldEmail, String newEmail) {
		repo.updateEmailBankAccoutn(oldEmail, newEmail);
		return ResponseEntity.status(HttpStatus.OK).body("Bank account email updated");
	}*/
	
	private BankAccountDto convertFromModelToDto(BankAccountModel model) {
		return new BankAccountDto(model.getEmail(), model.getUsdAmount(), model.getEurAmount(),
				model.getRsdAmount(), model.getGbpAmount(), model.getChfAmount());
	}

	@Override
	public BankAccountDto getBankAccount(String email) {
		return convertFromModelToDto(repo.findByEmail(email));
	}
	

}
