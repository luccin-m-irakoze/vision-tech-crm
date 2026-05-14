package com.visiontechcrm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.visiontechcrm.dto.LoginRequest;
import com.visiontechcrm.dto.LoginResponse;
import com.visiontechcrm.model.Role;
import com.visiontechcrm.model.User;
import com.visiontechcrm.repository.UserRepository;
import com.visiontechcrm.security.JwtUtil;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/**
 * Unit tests for {@link AuthService}.
 *
 * <p>The authentication manager and JWT utility are mocked so the test only
 * exercises the service's own orchestration logic.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserRepository userRepository;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks private AuthService authService;

    private LoginRequest validRequest;
    private User dbUser;

    @BeforeEach
    void setUp() {
        validRequest = new LoginRequest();
        validRequest.setEmail("admin@visiontech.rw");
        validRequest.setPassword("Admin@123");

        dbUser =
                User.builder()
                        .id(1L)
                        .fullName("System Administrator")
                        .email("admin@visiontech.rw")
                        .passwordHash("$2a$10$irrelevant")
                        .role(Role.ADMIN)
                        .enabled(true)
                        .build();
    }

    @Test
    @DisplayName("login() returns a LoginResponse with a JWT on success")
    void login_validCredentials_returnsToken() {
        when(userRepository.findByEmail(validRequest.getEmail())).thenReturn(Optional.of(dbUser));
        when(jwtUtil.generateToken("admin@visiontech.rw", "ADMIN")).thenReturn("signed-jwt");
        when(jwtUtil.getExpirationMs()).thenReturn(86400000L);

        LoginResponse response = authService.login(validRequest);

        assertThat(response.getToken()).isEqualTo("signed-jwt");
        assertThat(response.getEmail()).isEqualTo("admin@visiontech.rw");
        assertThat(response.getRole()).isEqualTo("ADMIN");
        assertThat(response.getFullName()).isEqualTo("System Administrator");
        assertThat(response.getExpiresInMs()).isEqualTo(86400000L);

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("login() throws BadCredentialsException when authentication fails")
    void login_invalidCredentials_throws() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("bad"));

        assertThatThrownBy(() -> authService.login(validRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Invalid email or password");
    }

    @Test
    @DisplayName("login() throws when user exists in AuthN but not in DB (defensive guard)")
    void login_userVanished_throws() {
        when(userRepository.findByEmail(validRequest.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(validRequest))
                .isInstanceOf(BadCredentialsException.class);
    }
}
