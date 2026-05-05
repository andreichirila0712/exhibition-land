package andrei.chirila.prove_yourself.application.services;

import andrei.chirila.prove_yourself.domain.EmailChangeToken;
import andrei.chirila.prove_yourself.domain.EmailChangeTokenRepository;
import andrei.chirila.prove_yourself.domain.exceptions.ElErrorMessage;
import andrei.chirila.prove_yourself.domain.exceptions.ElException;
import andrei.chirila.prove_yourself.domain.services.EmailChangeTokenService;
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
public class EmailChangeTokenServiceImplTest {
    private static final String CURRENT_EMAIL = "test@gmail.com";
    private static final String PENDING_EMAIL = "test2@gmail.com";
    @Mock
    private EmailChangeTokenRepository repository;
    private EmailChangeTokenService service;

    @BeforeEach
    void init() {
        service = new EmailChangeTokenServiceImpl(repository);
    }

    @Test
    void shouldReturnTokenWhenGivenDataIsValid() {
        UUID generatedToken = service.generateToken(CURRENT_EMAIL, PENDING_EMAIL);

        verify(repository).save(any());

        assertNotNull(generatedToken);
    }

    @Test
    void shouldThrowErrorWhenIsValidButNotUsedYet() {
        EmailChangeToken existingToken = new EmailChangeToken();
        existingToken.setExpirationDate(LocalDateTime.now(ZoneOffset.UTC).plusMinutes(30));
        existingToken.setToken(UUID.randomUUID());

        when(repository.findByCurrentEmail(CURRENT_EMAIL)).thenReturn(Optional.of(existingToken));

        assertThrows(ElException.class, () -> service.generateToken(CURRENT_EMAIL, PENDING_EMAIL));
    }

    @Test
    void shouldReturnTokenWhenFound() {
        EmailChangeToken token = new EmailChangeToken();
        token.setToken(UUID.randomUUID());

        when(repository.findByToken(any())).thenReturn(Optional.of(token));

        EmailChangeToken result = service.findByToken(any());

        verify(repository).findByToken(any());
        assertNotNull(result);
        assertEquals(token, result);
    }

    @Test
    void shouldThrowErrorWhenCannotFindByToken() {
        assertThrows(ElException.class, () -> service.findByToken(any()));
    }

    @Test
    void shouldReturnTokenWhenFoundByEmail() {
        EmailChangeToken token = new EmailChangeToken();
        token.setPendingEmail(PENDING_EMAIL);
        token.setToken(UUID.randomUUID());
        when(repository.findByPendingEmail(PENDING_EMAIL)).thenReturn(Optional.of(token));

        UUID result = service.findByEmail(PENDING_EMAIL);

        verify(repository).findByPendingEmail(PENDING_EMAIL);
        assertEquals(token.getToken(), result);
    }

    @Test
    void shouldThrowErrorWhenTokenNotFoundByEmail() {
        assertThrows(ElException.class, () -> service.findByEmail(PENDING_EMAIL));
    }

    @Test
    void shouldDeleteToken() {
        service.deleteToken(any());
        verify(repository).removeEmailChangeToken(any());
    }

    @Test
    void shouldDeleteTokenByEmail() {
        service.deleteByUserEmail(CURRENT_EMAIL);
        verify(repository).deleteByCurrentEmail(CURRENT_EMAIL);
    }
}
