package cryptoWallet;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

@Repository
public interface CryptoWalletRepository extends JpaRepository<CryptoWalletModel, Integer>{

	CryptoWalletModel findByEmail(String email);
	
	@Modifying
	@Transactional
	@Query("update CryptoWalletModel u set u.btcAmount = ?2, u.ethAmount = ?3, u.solAmount = ?4 where u.email = ?1")
	void updateCryptoWallet(String email, BigDecimal btcAmount, BigDecimal ethAmount, BigDecimal solAmount);
	
}
