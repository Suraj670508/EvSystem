package com.voltgrid.gateway;

import com.voltgrid.gateway.util.JwtUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private final String secret = "voltgrid-super-secret-key-32-characters-minimum-secure-key-2026";
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(secret);
    }

    @Test
    void testValidToken() {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder()
                .subject("driver1")
                .claim("role", "ROLE_USER")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(key)
                .compact();

        assertTrue(jwtUtil.validateToken(token));
        assertEquals("driver1", jwtUtil.extractUsername(token));
        assertEquals("ROLE_USER", jwtUtil.extractRole(token));
    }

    @Test
    void testExpiredToken() {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder()
                .subject("driver1")
                .issuedAt(new Date(System.currentTimeMillis() - 7200000))
                .expiration(new Date(System.currentTimeMillis() - 3600000))
                .signWith(key)
                .compact();

        assertFalse(jwtUtil.validateToken(token));
    }

    @Test
    void testMalformedToken() {
        assertFalse(jwtUtil.validateToken("not-a-valid-jwt-token"));
    }
}
