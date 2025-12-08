package cryptoExchange;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CryptoExchangeRepository extends JpaRepository<CryptoExchangeModel, Integer>{

	CryptoExchangeModel findByFromAndTo(String from, String to);

	@Query(value = """
			SELECT DISTINCT crypto_from AS crypto FROM crypto_exchange
			UNION
			SELECT DISTINCT crypto_to AS crypto_to FROM crypto_exchange
			""", nativeQuery = true)
	List<String> findAllDistinctCryptos();
	
}
