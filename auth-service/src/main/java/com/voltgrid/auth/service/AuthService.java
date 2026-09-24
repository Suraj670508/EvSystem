package com.voltgrid.auth.service;

import com.voltgrid.auth.dto.AuthRequest;
import com.voltgrid.auth.dto.AuthResponse;
import com.voltgrid.auth.dto.RegisterRequest;
import com.voltgrid.auth.entity.UserCredential;
import com.voltgrid.auth.exception.UserAlreadyExistsException;
import com.voltgrid.auth.repository.UserCredentialRepository;
import com.voltgrid.auth.util.JwtUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserCredentialRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserCredentialRepository repository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public String register(RegisterRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (repository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("User with username '" + request.getUsername() + "' already exists");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        String role = (request.getRole() != null && !request.getRole().trim().isEmpty())
                ? request.getRole()
                : "ROLE_USER";

        UserCredential credential = new UserCredential(request.getUsername(), encodedPassword, role);
        repository.save(credential);
        return "User registered successfully";
    }

    public AuthResponse login(AuthRequest request) {
        if (request.getUsername() == null || request.getPassword() == null) {
            throw new BadCredentialsException("Invalid username or password");
        }

        UserCredential credential = repository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), credential.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(credential.getUsername(), credential.getRole());
        return new AuthResponse(token, jwtUtil.getExpirationMs(), credential.getUsername(), credential.getRole());
    }
}
