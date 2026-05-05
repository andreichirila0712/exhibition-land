package andrei.chirila.prove_yourself.application.services;

import andrei.chirila.prove_yourself.domain.AccountActivationToken;
import andrei.chirila.prove_yourself.domain.AccountActivationTokenRepository;
import andrei.chirila.prove_yourself.domain.exceptions.ElException;
import andrei.chirila.prove_yourself.domain.services.AccountActivationTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
public class AccountActivationTokenServiceImplTest {
    private static final String USER_EMAIL = "test@gmail.com";
    @Mock
    private AccountActivationTokenRepository repository;
    private AccountActivationTokenService service;

    @BeforeEach
    void init() {
        service = new AccountActivationTokenServiceImpl(repository);
    }

    @Test
    void shouldGenerateTokenWhenDataIsValid() {
        UUID result = service.generateToken(USER_EMAIL);

        verify(repository).save(any());
        assertNotNull(result);
    }

    @Test
    void shouldThrowErrorWhenTokenExistsAndNotExpired() {
        AccountActivationToken token = new AccountActivationToken();
        token.setToken(UUID.randomUUID());
        token.setExpirationDate(LocalDateTime.now(ZoneOffset.UTC).plusMinutes(30));

        when(repository.findByUserEmail(USER_EMAIL)).thenReturn(Optional.of(token));

        assertThrows(ElException.class, () -> service.generateToken(USER_EMAIL));
    }

    @Test
    void shouldReturnTokenWhenFoundByEmail() {
        AccountActivationToken token = new AccountActivationToken();
        token.setUserEmail(USER_EMAIL);
        token.setToken(UUID.randomUUID());

        when(repository.findByUserEmail(USER_EMAIL)).thenReturn(Optional.of(token));
        UUID result = service.findByEmail(USER_EMAIL);

        verify(repository).findByUserEmail(USER_EMAIL);
        assertEquals(token.getToken(), result);
    }

    @Test
    void shouldThrowErrorWhenTokenNotFoundByEmail() {
        assertThrows(ElException.class, () -> service.findByEmail(USER_EMAIL));
    }

    @Test
    void shouldReturnUserEmailWhenFoundByToken() {
        AccountActivationToken token = new AccountActivationToken();
        token.setToken(UUID.randomUUID());
        token.setUserEmail(USER_EMAIL);

        when(repository.findUserEmailByToken(token.getToken())).thenReturn(Optional.of(token.getUserEmail()));

        String result = service.findByToken(token.getToken());

        verify(repository).findUserEmailByToken(token.getToken());
        assertEquals(token.getUserEmail(), result);
    }

    @Test
    void shouldThrowErrorWhenUserEmailNotFoundByToken() {
        assertThrows(ElException.class, () -> service.findByToken(any()));
    }

    @Test
    void shouldDeleteToken() {
        service.deleteToken(any());
        verify(repository).removeActivationToken(any());
    }

    @Test
    void shouldDeleteTokenByUserEmail() {
        service.deleteByUserEmail(USER_EMAIL);
        verify(repository).deleteByUserEmail(USER_EMAIL);
    }
}
