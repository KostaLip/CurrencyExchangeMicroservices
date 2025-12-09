package tradeService;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TradeServiceRepository extends JpaRepository<TradeServiceModel, Integer>{

	TradeServiceModel findByFromAndTo(String from,String to);
	
}
