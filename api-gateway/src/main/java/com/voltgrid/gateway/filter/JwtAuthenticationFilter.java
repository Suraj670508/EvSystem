package com.voltgrid.gateway.filter;

import com.voltgrid.gateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/auth/register",
            "/auth/login",
            "/actuator/health"
    );

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        // Check if public endpoint
        if (isPublicEndpoint(path)) {
            return chain.filter(exchange);
        }

        // Check Authorization header
        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            return onError(exchange, "Missing Authorization Header", HttpStatus.UNAUTHORIZED);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return onError(exchange, "Authorization header must start with Bearer", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7).trim();
        if (token.isEmpty()) {
            return onError(exchange, "Bearer token is empty", HttpStatus.UNAUTHORIZED);
        }

        try {
            Claims claims = jwtUtil.extractAllClaims(token);
            String username = claims.getSubject();
            String role = claims.get("role", String.class);

            // Mutate request headers with user details for downstream microservices
            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-User-Name", username != null ? username : "")
                    .header("X-User-Roles", role != null ? role : "")
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        } catch (JwtException | IllegalArgumentException e) {
            return onError(exchange, "Invalid or expired JWT: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    private boolean isPublicEndpoint(String path) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = String.format("{\"timestamp\":\"%s\",\"status\":%d,\"error\":\"Unauthorized\",\"message\":\"%s\",\"path\":\"%s\"}",
                java.time.Instant.now(), httpStatus.value(), message, exchange.getRequest().getPath().value());
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
    }

    @Override
    public int getOrder() {
        return -100; // Execute before default routing filters
    }
}
