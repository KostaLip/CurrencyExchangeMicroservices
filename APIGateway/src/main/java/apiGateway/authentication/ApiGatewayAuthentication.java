package apiGateway.authentication;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.function.client.WebClient;

import api.dtos.UserDto;

@Configuration
@EnableWebFluxSecurity
public class ApiGatewayAuthentication {

	@Bean
	SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
		http
		.csrf(csrf -> csrf.disable())
		.authorizeExchange(exchange -> exchange
				.pathMatchers("/currency-exchange").hasAnyRole("USER", "ADMIN", "OWNER")
				.pathMatchers("/crypto-exchange").hasAnyRole("USER", "ADMIN", "OWNER")
				.pathMatchers("/currency-conversion-feign").hasRole("USER")
				
				.pathMatchers(HttpMethod.GET,"/users").hasAnyRole("ADMIN", "OWNER")
				.pathMatchers(HttpMethod.POST, "/users/newUser").hasAnyRole("ADMIN", "OWNER")
				.pathMatchers(HttpMethod.POST, "/users/**").hasRole("OWNER")
				.pathMatchers(HttpMethod.PUT, "/users/**").hasAnyRole("ADMIN", "OWNER")
				.pathMatchers(HttpMethod.DELETE, "/users/**").hasRole("OWNER")
				
				.pathMatchers(HttpMethod.GET, "/bankAccounts").hasRole("ADMIN")
				.pathMatchers(HttpMethod.GET, "/bankAccounts/email").hasAnyRole("ADMIN", "USER")
				.pathMatchers(HttpMethod.PUT, "/bankAccounts").hasRole("ADMIN")
				
				.pathMatchers(HttpMethod.GET, "/crypto-wallet").hasRole("ADMIN")
				.pathMatchers(HttpMethod.GET, "/crypto-wallet/email").hasAnyRole("ADMIN", "USER")
				.pathMatchers(HttpMethod.PUT, "/crypto-wallet").hasRole("ADMIN")
				).httpBasic(Customizer.withDefaults());
		
		return http.build();
	}
	
	@Bean
	ReactiveUserDetailsService reactiveUserDetailsService(WebClient.Builder webClientBuilder, BCryptPasswordEncoder encoder) {
		WebClient client = webClientBuilder.baseUrl("http://localhost:8770").build();
		
		//WebClient client = webClientBuilder.baseUrl("http://user-service:8770").build();
		
		return user -> client.get()
				.uri(uriBuilder -> uriBuilder
						.path("/users/email")
						.queryParam("email", user)
						.build()
				)
				.retrieve()
				.bodyToMono(UserDto.class)
				.map(dto -> User.withUsername(dto.getEmail())
						.password(encoder.encode(dto.getPassword()))
						.roles(dto.getRole())
						.build()
				);
			
		}
		
		/*ResponseEntity<List<UserDto>> response =
				new RestTemplate().exchange("http://users-service:8770/users", HttpMethod.GET,
						null, new ParameterizedTypeReference<List<UserDto>>() {});
		List<UserDetails> users = new ArrayList<UserDetails>();
		for(UserDto user : response.getBody()) {
			users.add(
					User.withUsername(user.getEmail())
					.password(encoder.encode(user.getPassword()))
					.roles(user.getRole())
					.build());
				
		}
		
		return new MapReactiveUserDetailsService(users);
				
		}*/
	
	@Bean
	BCryptPasswordEncoder getEncoder() {
		return new BCryptPasswordEncoder();
	}
	
}
