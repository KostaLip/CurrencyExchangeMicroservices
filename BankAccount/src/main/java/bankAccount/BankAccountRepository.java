package bankAccount;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccountModel, Integer>{

	BankAccountModel findByEmail(String email);
	
	@Modifying
	@Transactional
	@Query("update BankAccountModel u set u.usdAmount = ?2, u.eurAmount = ?3, "
			+ "u.rsdAmount = ?4, u.gbpAmount = ?5, u.chfAmount = ?6 where u.email = ?1")
	void updateBankAccount(String email, BigDecimal usdAmount, BigDecimal eurAmount, BigDecimal rsdAmount,
			BigDecimal gbpAmount, BigDecimal chfAmount);
	
}
