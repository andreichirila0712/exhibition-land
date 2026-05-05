package andrei.chirila.prove_yourself.application.services;

import andrei.chirila.prove_yourself.domain.PasswordChangeToken;
import andrei.chirila.prove_yourself.domain.PasswordChangeTokenRepository;
import andrei.chirila.prove_yourself.domain.exceptions.ElException;
import andrei.chirila.prove_yourself.domain.services.PasswordChangeTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PasswordChangeTokenServiceImplTest {
    private static final String PENDING_PASSWORD = "test1234";
    private static final String USER_EMAIL = "test@gmail.com";
    @Mock
    private PasswordChangeTokenRepository repository;
    @Mock
    private PasswordEncoder passwordEncoder;
    private PasswordChangeTokenService service;

    @BeforeEach
    void init() {
        service = new PasswordChangeTokenServiceImpl(repository, passwordEncoder);
    }

    @Test
    void shouldReturnTokenWhenGivenDataIsValid() {
        UUID result = service.generateToken(USER_EMAIL, PENDING_PASSWORD);

        verify(repository).save(any());
        assertNotNull(result);
    }

    @Test
    void shouldThrowErrorWhenTokenExistsButIsNotUsed() {
        PasswordChangeToken token = new PasswordChangeToken();
        token.setExpirationDate(LocalDateTime.now(ZoneOffset.UTC).plusMinutes(30));
        token.setToken(UUID.randomUUID());

        when(repository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(token));

        assertThrows(ElException.class, () -> service.generateToken(USER_EMAIL, PENDING_PASSWORD));
    }

    @Test
    void shouldReturnTokenWhenFound() {
        PasswordChangeToken token = new PasswordChangeToken();
        UUID passToken = UUID.randomUUID();
        token.setToken(passToken);
        token.setUserEmail(USER_EMAIL);
        token.setPendingPassword(PENDING_PASSWORD);

        when(repository.findByToken(passToken)).thenReturn(Optional.of(token));

        PasswordChangeToken result = service.findByToken(passToken);
        verify(repository).findByToken(passToken);
        assertEquals(token, result);
    }

    @Test
    void shouldThrowErrorWhenNotFoundByToken() {
        assertThrows(ElException.class, () -> service.findByToken(any()));
    }

    @Test
    void shouldReturnTokenWhenFoundByEmail() {
        PasswordChangeToken token = new PasswordChangeToken();
        token.setUserEmail(USER_EMAIL);
        token.setToken(UUID.randomUUID());

        when(repository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(token));

        UUID result = service.findByEmail(USER_EMAIL);

        verify(repository).findByEmail(USER_EMAIL);
        assertEquals(token.getToken(), result);
    }

    @Test
    void shouldThrowErrorWhenNotFoundByEmail() {
        assertThrows(ElException.class, () -> service.findByEmail(USER_EMAIL));
    }

    @Test
    void shouldDeleteToken() {
        service.deleteToken(any());

        verify(repository).removePasswordChangeToken(any());
    }

    @Test
    void shouldDeleteTokenByUserEmail() {
        service.deleteByUserEmail(USER_EMAIL);

        verify(repository).deleteByUserEmail(USER_EMAIL);
    }
}
