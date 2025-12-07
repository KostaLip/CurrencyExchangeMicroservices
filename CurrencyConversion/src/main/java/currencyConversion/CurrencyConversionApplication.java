package currencyConversion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {
	    DataSourceAutoConfiguration.class,
	    DataSourceTransactionManagerAutoConfiguration.class,
	    HibernateJpaAutoConfiguration.class
	})
@ComponentScan(basePackages = {"util.exceptions", "currencyConversion", "api.services"})
@EnableFeignClients(basePackages = {"api.proxies"})
public class CurrencyConversionApplication {

	public static void main(String[] args) {
		SpringApplication.run(CurrencyConversionApplication.class, args);
	}

}
