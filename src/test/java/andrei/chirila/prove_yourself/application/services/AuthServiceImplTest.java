package andrei.chirila.prove_yourself.application.services;

import andrei.chirila.prove_yourself.domain.EmailChangeToken;
import andrei.chirila.prove_yourself.domain.PasswordChangeToken;
import andrei.chirila.prove_yourself.domain.Role;
import andrei.chirila.prove_yourself.domain.User;
import andrei.chirila.prove_yourself.domain.UserRepository;
import andrei.chirila.prove_yourself.domain.exceptions.ElException;
import andrei.chirila.prove_yourself.domain.services.AccountActivationTokenService;
import andrei.chirila.prove_yourself.domain.services.AuthService;
import andrei.chirila.prove_yourself.domain.services.EmailChangeTokenService;
import andrei.chirila.prove_yourself.domain.services.PasswordChangeTokenService;
import andrei.chirila.prove_yourself.domain.services.TokenService;
import andrei.chirila.prove_yourself.infrastructure.dtos.CreateUserDto;
import andrei.chirila.prove_yourself.infrastructure.dtos.LoginRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {
    private static final String USER_EMAIL = "test@gmail.com";
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
    @Mock
    private PasswordChangeTokenService passwordChangeTokenService;
    @Mock
    private EmailChangeTokenService emailChangeTokenService;
    private AuthService authService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @BeforeEach
    void init() {
        authService = new AuthServiceImpl(userRepository, tokenService, passwordEncoder, authenticationConfiguration,
                mock(ApplicationEventPublisher.class), activationTokenService, passwordChangeTokenService, emailChangeTokenService);
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
        CreateUserDto dto = new CreateUserDto("test@gmail.com", "Test", "test123", "test");

        when(userRepository.save(any())).thenReturn(new User(dto.name(), dto.email()));
        when(passwordEncoder.encode(any())).thenReturn(any());
        authService.createUser(dto);

        verify(passwordEncoder).encode(any());
        verify(userRepository).save(any());
        verify(activationTokenService).generateToken(dto.email());
    }

    @Test
    void shouldConfirmPasswordChanged() {
        PasswordChangeToken token = new PasswordChangeToken();
        token.setToken(UUID.randomUUID());
        token.setUserEmail(USER_EMAIL);
        token.setExpirationDate(LocalDateTime.now(ZoneOffset.UTC).plusMinutes(30));
        token.setPendingPassword("encodedPass2");

        when(passwordChangeTokenService.findByToken(token.getToken())).thenReturn(token);

        User user = new User();
        user.setPassword("encodedPass");
        user.setEmail(USER_EMAIL);

        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        authService.confirmChangedPassword(token.getToken());

        verify(passwordChangeTokenService).findByToken(token.getToken());
        verify(userRepository).findByEmail(USER_EMAIL);
        verify(userRepository).save(userCaptor.capture());
        verify(passwordChangeTokenService).deleteToken(token.getToken());

        assertEquals(token.getPendingPassword(), userCaptor.getValue().getPassword());
    }

    @Test
    void shouldThrowErrorWhenPasswordTokenIsExpired() {
        PasswordChangeToken token = new PasswordChangeToken();
        token.setToken(UUID.randomUUID());
        token.setExpirationDate(LocalDateTime.now(ZoneOffset.UTC).minusMinutes(10));

        when(passwordChangeTokenService.findByToken(token.getToken())).thenReturn(token);

        assertThrows(ElException.class, () -> authService.confirmChangedPassword(token.getToken()));
    }

    @Test
    void shouldConfirmEmailChanged() {
        EmailChangeToken token = new EmailChangeToken();
        token.setToken(UUID.randomUUID());
        token.setCurrentEmail(USER_EMAIL);
        token.setPendingEmail("new@gmail.com");
        token.setExpirationDate(LocalDateTime.now(ZoneOffset.UTC).plusMinutes(30));

        when(emailChangeTokenService.findByToken(token.getToken())).thenReturn(token);

        User user = new User();
        user.setEmail(USER_EMAIL);

        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));

        authService.confirmChangedEmail(token.getToken());

        verify(emailChangeTokenService).findByToken(token.getToken());
        verify(userRepository).findByEmail(USER_EMAIL);
        verify(userRepository).save(userCaptor.capture());
        verify(emailChangeTokenService).deleteToken(token.getToken());

        assertEquals(token.getPendingEmail(), userCaptor.getValue().getEmail());
    }

    @Test
    void shouldThrowErrorWhenEmailTokenIsExpired() {
        EmailChangeToken token = new EmailChangeToken();
        token.setExpirationDate(LocalDateTime.now(ZoneOffset.UTC).minusMinutes(15));
        token.setToken(UUID.randomUUID());

        when(emailChangeTokenService.findByToken(token.getToken())).thenReturn(token);

        assertThrows(ElException.class, () -> authService.confirmChangedEmail(token.getToken()));
    }
}
