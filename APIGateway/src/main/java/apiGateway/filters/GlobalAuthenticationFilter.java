package apiGateway.filters;

import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

@Component
public class GlobalAuthenticationFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication())
                .flatMap(authentication -> {
                    if (authentication != null && authentication.isAuthenticated()) {
                        String username = authentication.getName();

                        String role = authentication.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .findFirst()
                                .orElse("");

                        if (role.startsWith("ROLE_")) {
                            role = role.substring(5); 
                        }

                        var mutatedRequest = exchange.getRequest().mutate()
                                .header("X-Auth-Email", username)
                                .header("X-Auth-Role", role)
                                .build();
                        
                        System.out.println("[GlobalAuthFilter] Username: " + username + ", Role: " + role);
                        
                        return chain.filter(exchange.mutate().request(mutatedRequest).build());
                    }
                    return chain.filter(exchange);
                })
                .switchIfEmpty(chain.filter(exchange));
    }
}