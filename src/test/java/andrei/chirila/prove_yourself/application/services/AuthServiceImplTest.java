package andrei.chirila.prove_yourself.application.services;

import andrei.chirila.prove_yourself.domain.Role;
import andrei.chirila.prove_yourself.domain.User;
import andrei.chirila.prove_yourself.domain.UserRepository;
import andrei.chirila.prove_yourself.domain.services.AccountActivationTokenService;
import andrei.chirila.prove_yourself.domain.services.TokenService;
import andrei.chirila.prove_yourself.infrastructure.dtos.CreateUserDto;
import andrei.chirila.prove_yourself.infrastructure.dtos.LoginRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private TokenService tokenService;
    @Mock
    private AuthenticationConfiguration authenticationConfiguration;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private Authentication authentication;
    @Mock
    private AccountActivationTokenService activationTokenService;
    private AuthServiceImpl authService;

    @BeforeEach
    void init() {
        authService = new AuthServiceImpl(userRepository, tokenService, passwordEncoder, authenticationConfiguration, mock(ApplicationEventPublisher.class), activationTokenService);
    }

    @Test
    void shouldReturnTokenWhenCredentialsAreValid() {
        String email = "test@gmail.com";
        String password = "test123";
        String encodedPassword = "encodedTest123";
        String token = "jwt-token";

        User user = new User("Test", email);
        user.setPassword(encodedPassword);
        user.setEmailVerified(true);
        user.setRole(Role.USER);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(tokenService.generateToken(any())).thenReturn(token);
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);

        String result = authService.login(new LoginRequestDto(email, password));

        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(password, encodedPassword);
        verify(tokenService).generateToken(any());
        assertEquals(token, result);
    }

    @Test
    void shouldActivateAccountWhenNotYetActive() {
        String email = "test@gmail.com";

        User user = new User("Test", email);
        user.setEmailVerified(false);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(activationTokenService.findByToken(any())).thenReturn(email);

        assertFalse(user.isEmailVerified());

        authService.activateAccount(any());

        verify(userRepository).findByEmail(email);
        verify(activationTokenService).findByToken(any());
        verify(activationTokenService).deleteToken(any());
        assertTrue(user.isEmailVerified());
    }

    @Test
    void shouldCreateUserWhenDataIsValid() {
        CreateUserDto dto = new CreateUserDto("test@gmail.com", "Test", "test123");

        when(userRepository.save(any())).thenReturn(new User(dto.name(), dto.email()));
        when(passwordEncoder.encode(any())).thenReturn(any());
        authService.createUser(dto);

        verify(passwordEncoder).encode(any());
        verify(userRepository).save(any());
        verify(activationTokenService).generateToken(dto.email());
    }
}
