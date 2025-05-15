package com.api.reactiveGateway.filter;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class JwtValidationGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    private static final Logger log = LoggerFactory.getLogger(JwtValidationGatewayFilterFactory.class);
    private final WebClient webClient;
    private final CircuitBreaker circuitBreaker;

    public JwtValidationGatewayFilterFactory(WebClient.Builder webClientBuilder,
                                             CircuitBreakerRegistry cbRegistry,
                                             @Value("${auth.service.url}") String authServiceUrl) {
        this.circuitBreaker = cbRegistry.circuitBreaker("authService");
        this.webClient = webClientBuilder.baseUrl(authServiceUrl).build();
    }

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getPath().toString();
            log.info("In GatewayFilter, path: {}", path);

            if (path.equals("/auth/v1/signup") || path.equals("/auth/v1/login")) {
                return chain.filter(exchange);
            }

            String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (token == null || !token.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            log.info("Validating token: {}", token);
            return webClient.get()
                    .uri("/validate")
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .toBodilessEntity()
                    .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                    .flatMap(response -> chain.filter(exchange)) // If validation succeeds
                    .onErrorResume(ex -> {
                        if (ex instanceof WebClientResponseException.Unauthorized) {
                            log.warn("Token is invalid or expired");
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        } else if (ex instanceof java.net.UnknownHostException) {
                            log.error("Auth service hostname not resolved. Container may be down.");
                            exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
                        } else {
                            log.error("Auth service unavailable: {}", ex.getMessage());
                            exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
                        }
                        return exchange.getResponse().setComplete();
                    });
        };
    }
}
