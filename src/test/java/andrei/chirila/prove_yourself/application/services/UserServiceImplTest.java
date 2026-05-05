package andrei.chirila.prove_yourself.application.services;

import andrei.chirila.prove_yourself.domain.User;
import andrei.chirila.prove_yourself.domain.UserChangedEmailEvent;
import andrei.chirila.prove_yourself.domain.UserChangedPasswordEvent;
import andrei.chirila.prove_yourself.domain.UserDeletedAccountEvent;
import andrei.chirila.prove_yourself.domain.UserRepository;
import andrei.chirila.prove_yourself.domain.exceptions.ElException;
import andrei.chirila.prove_yourself.domain.services.AccountActivationTokenService;
import andrei.chirila.prove_yourself.domain.services.EmailChangeTokenService;
import andrei.chirila.prove_yourself.domain.services.PasswordChangeTokenService;
import andrei.chirila.prove_yourself.domain.services.UserService;
import andrei.chirila.prove_yourself.infrastructure.dtos.UserSettingsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    private static final String USER_EMAIL = "test@gmail.com";
    @Mock
    private UserRepository repository;
    @Mock
    private EmailChangeTokenService emailChangeService;
    @Mock
    private PasswordChangeTokenService passwordChangeService;
    @Mock
    private AccountActivationTokenService accountActivationService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ApplicationEventPublisher publisher;
    private UserService service;

    @Captor
    private ArgumentCaptor<User> argumentCaptor;
    @Captor
    private ArgumentCaptor<UserChangedEmailEvent> emailChangeEventCaptor;
    @Captor
    private ArgumentCaptor<UserChangedPasswordEvent> passwordChangeEventCaptor;
    @Captor
    private ArgumentCaptor<UserDeletedAccountEvent> accountDeleteEventCaptor;

    @BeforeEach
    void init() {
        service = new UserServiceImpl(repository, emailChangeService, passwordChangeService, accountActivationService, passwordEncoder, publisher);
    }

    @Test
    void shouldReturnUserSettingsIfExisting() {
        User user = new User();
        user.setEmail(USER_EMAIL);
        user.setLanguage("English");
        user.setTheme("Light");
        user.setDateFormat("DD/MM/YYYY");
        user.setProfileVisibility("Public");
        user.setProfileDiscoverable("Yes");

        when(repository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        UserSettingsDto result = service.getUserSettings(USER_EMAIL);

        verify(repository).findByEmail(USER_EMAIL);
        assertEquals(user.getLanguage(), result.language());
        assertEquals(user.getTheme(), result.theme());
        assertEquals(user.getDateFormat(), result.dateFormat());
        assertEquals(user.getProfileVisibility(), result.profileVisibility());
        assertEquals(user.getProfileDiscoverable(), result.profileDiscoverable());
    }

    @Test
    void shouldThrowErrorWhenUserNotExisting() {
        assertThrows(ElException.class, () -> service.getUserSettings(USER_EMAIL));
    }

    @Test
    void shouldUpdateLanguage() {
        User user = new User();
        user.setEmail(USER_EMAIL);
        user.setLanguage("English");

        when(repository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        service.updateLanguage(USER_EMAIL, "Spanish");

        verify(repository).save(argumentCaptor.capture());

        assertEquals(user.getEmail(), argumentCaptor.getValue().getEmail());
        assertEquals("Spanish", argumentCaptor.getValue().getLanguage());
    }

    @Test
    void shouldUpdateTheme() {
        User user = new User();
        user.setTheme("Light");
        user.setEmail(USER_EMAIL);

        when(repository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        service.updateTheme(USER_EMAIL, "Dark");

        verify(repository).save(argumentCaptor.capture());

        assertEquals(user.getEmail(), argumentCaptor.getValue().getEmail());
        assertEquals("Dark", argumentCaptor.getValue().getTheme());
    }

    @Test
    void shouldUpdateDateFormat() {
        User user = new User();
        user.setEmail(USER_EMAIL);
        user.setDateFormat("DD/MM/YYYY");

        when(repository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        service.updateDateFormat(USER_EMAIL,"MM/DD/YYYY");

        verify(repository).save(argumentCaptor.capture());

        assertEquals(user.getEmail(), argumentCaptor.getValue().getEmail());
        assertEquals("MM/DD/YYYY", argumentCaptor.getValue().getDateFormat());
    }

    @Test
    void shouldUpdateProfileVisibility() {
        User user = new User();
        user.setEmail(USER_EMAIL);
        user.setProfileVisibility("Public");

        when(repository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        service.updateVisibility(USER_EMAIL, "Private");

        verify(repository).save(argumentCaptor.capture());

        assertEquals(user.getEmail(), argumentCaptor.getValue().getEmail());
        assertEquals("Private", argumentCaptor.getValue().getProfileVisibility());
    }

    @Test
    void shouldPublishEventForChangeEmailIfDataIsValid() {
        service.changeEmail(USER_EMAIL, "new@gmail.com");

        verify(publisher).publishEvent(emailChangeEventCaptor.capture());

        assertEquals("new@gmail.com", emailChangeEventCaptor.getValue().getUserNewEmail());
    }

    @Test
    void shouldThrowErrorWhenEmailAlreadyExists() {
        User user = new User();
        user.setEmail(USER_EMAIL);

        when(repository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));

        assertThrows(ElException.class, () -> service.changeEmail(USER_EMAIL, USER_EMAIL));
    }

    @Test
    void shouldPublishEventForChangePasswordIfDataIsValid() {
        User user = new User();
        user.setEmail(USER_EMAIL);

        when(repository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        service.changePassword(USER_EMAIL, "test1234", "testtest");

        verify(publisher).publishEvent(passwordChangeEventCaptor.capture());

        assertEquals(USER_EMAIL, passwordChangeEventCaptor.getValue().getUserEmail());
    }

    @Test
    void shouldThrowErrorWhenPasswordDoesntMatch() {
        assertThrows(ElException.class, () -> service.changePassword(USER_EMAIL, "test1234", "testtest"));
    }

    @Test
    void shouldReturnIfPasswordsMatch() {
        User user = new User();
        user.setEmail(USER_EMAIL);
        user.setPassword("encodedPass");

        when(repository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("testtest", "encodedPass")).thenReturn(true);

        boolean result = service.checkPassword(USER_EMAIL, "testtest");

        assertTrue(result);
    }

    @Test
    void shouldDeleteAccount() {
        User user = new User();
        user.setEmail(USER_EMAIL);
        when(repository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));

        service.deleteAccount(USER_EMAIL);

        verify(repository).deleteUser(any());
        verify(publisher).publishEvent(accountDeleteEventCaptor.capture());

        assertEquals(USER_EMAIL, accountDeleteEventCaptor.getValue().getUserEmail());
    }

}
