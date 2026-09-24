package com.voltgrid.auth;

import com.voltgrid.auth.dto.AuthRequest;
import com.voltgrid.auth.dto.AuthResponse;
import com.voltgrid.auth.dto.RegisterRequest;
import com.voltgrid.auth.entity.UserCredential;
import com.voltgrid.auth.exception.UserAlreadyExistsException;
import com.voltgrid.auth.repository.UserCredentialRepository;
import com.voltgrid.auth.service.AuthService;
import com.voltgrid.auth.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserCredentialRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(repository, passwordEncoder, jwtUtil);
    }

    @Test
    void testRegisterSuccess() {
        RegisterRequest request = new RegisterRequest("suraj", "pass123", "ROLE_USER");
        when(repository.existsByUsername("suraj")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("hashedPass");

        String result = authService.register(request);
        assertEquals("User registered successfully", result);
        verify(repository, times(1)).save(any(UserCredential.class));
    }

    @Test
    void testRegisterDuplicateUsernameThrowsException() {
        RegisterRequest request = new RegisterRequest("suraj", "pass123", "ROLE_USER");
        when(repository.existsByUsername("suraj")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> authService.register(request));
        verify(repository, never()).save(any(UserCredential.class));
    }

    @Test
    void testLoginSuccess() {
        AuthRequest request = new AuthRequest("suraj", "pass123");
        UserCredential credential = new UserCredential("suraj", "hashedPass", "ROLE_USER");

        when(repository.findByUsername("suraj")).thenReturn(Optional.of(credential));
        when(passwordEncoder.matches("pass123", "hashedPass")).thenReturn(true);
        when(jwtUtil.generateToken("suraj", "ROLE_USER")).thenReturn("mock-token-xyz");
        when(jwtUtil.getExpirationMs()).thenReturn(86400000L);

        AuthResponse response = authService.login(request);
        assertNotNull(response);
        assertEquals("mock-token-xyz", response.getToken());
        assertEquals("suraj", response.getUsername());
        assertEquals("ROLE_USER", response.getRole());
    }

    @Test
    void testLoginInvalidPassword() {
        AuthRequest request = new AuthRequest("suraj", "wrongpass");
        UserCredential credential = new UserCredential("suraj", "hashedPass", "ROLE_USER");

        when(repository.findByUsername("suraj")).thenReturn(Optional.of(credential));
        when(passwordEncoder.matches("wrongpass", "hashedPass")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }
}
