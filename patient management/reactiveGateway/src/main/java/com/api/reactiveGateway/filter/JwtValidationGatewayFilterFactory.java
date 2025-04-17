package com.api.reactiveGateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class JwtValidationGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    private static final Logger log = LoggerFactory.getLogger(JwtValidationGatewayFilterFactory.class);
    private final WebClient webClient;

    public JwtValidationGatewayFilterFactory(WebClient.Builder webClientBuilder,
                                             @Value("${auth.service.url}")
                                             String authServiceUrl) {
        authServiceUrl = "http://auth-service:4005";
        this.webClient = webClientBuilder.baseUrl(authServiceUrl).build();
    }
    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getPath().toString();
            log.info("In GatewayFilter, path: {}", path);

            // Public endpoints — no auth required
            if (path.equals("/auth/v1/signup") || path.equals("/auth/v1/login")) {
                return chain.filter(exchange); // Bypass token validation
            }

            // Token required for all other endpoints
            String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (token == null || !token.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            log.info("in JwtValidationGatewayFilterFactory, token: {}", token);
            Mono<Void> client =  webClient.get()
                    .uri("/validate")
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .toBodilessEntity()
                    .then(chain.filter(exchange));
            log.info("the client: {}", client);
            return client;
        };
    }

}

