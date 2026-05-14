package com.visiontechcrm.service;

import com.visiontechcrm.dto.LoginRequest;
import com.visiontechcrm.dto.LoginResponse;
import com.visiontechcrm.model.User;
import com.visiontechcrm.repository.UserRepository;
import com.visiontechcrm.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

/**
 * Authenticates users and issues JSON Web Tokens.
 *
 * <p>Implements the Controller layer of MVC for the auth domain — it
 * orchestrates Spring Security and the persistence model without leaking
 * security primitives into REST controllers.
 */
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthService(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    /** Validates credentials and returns a signed JWT plus user info. */
    public LoginResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getPassword()));
        } catch (Exception ex) {
            throw new BadCredentialsException("Invalid email or password");
        }

        User user =
                userRepository
                        .findByEmail(request.getEmail())
                        .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .expiresInMs(jwtUtil.getExpirationMs())
                .build();
    }
}
