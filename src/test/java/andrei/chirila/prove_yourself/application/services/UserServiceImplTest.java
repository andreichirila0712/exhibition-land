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
import andrei.chirila.prove_yourself.infrastructure.storage.S3Utility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

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
    private S3Utility s3Utility;
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
        service = new UserServiceImpl(repository, emailChangeService, passwordChangeService, accountActivationService, passwordEncoder, s3Utility, publisher);
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

    @Test
    void shouldUpdateProfile() {
        User user = new User();
        user.setEmail(USER_EMAIL);
        user.setName("Test");
        user.setAccountName("usertest");
        user.setAbout("");
        user.setLocation("");
        user.setWebsite("");

        when(repository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        when(repository.findByAccountName(user.getAccountName())).thenReturn(Optional.of(user));
        service.updateProfile(user.getName(), user.getAccountName(), "A new about", "New location", "www.test.com", USER_EMAIL);

        verify(repository).save(argumentCaptor.capture());
        assertEquals("A new about", argumentCaptor.getValue().getAbout());
        assertEquals("New location", argumentCaptor.getValue().getLocation());
        assertEquals("www.test.com", argumentCaptor.getValue().getWebsite());
    }

    @Test
    void shouldThrowErrorIfNameIsBlank() {
        User user = new User();
        user.setEmail(USER_EMAIL);
        when(repository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));

        assertThrows(ElException.class, () -> service.updateProfile("", "test", "test", "test", "test", USER_EMAIL));
    }

    @Test
    void shouldThrowErrorIfUsernameIsBlank() {
        User user = new User();
        user.setEmail(USER_EMAIL);
        when(repository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));

        assertThrows(ElException.class, () -> service.updateProfile("test", "", "test", "test", "test", USER_EMAIL));
    }

    @Test
    void shouldThrowErrorIfUsernameIsTaken() {
        User user = new User();
        user.setEmail(USER_EMAIL);
        when(repository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        user.setEmail("test2@gmail.com");
        when(repository.findByAccountName("existing")).thenReturn(Optional.of(user));

        assertThrows(ElException.class, () -> service.updateProfile("test", "existing", "test", "test", "test", USER_EMAIL));
    }

    @Test
    void shouldThrowErrorIfAboutIsTooLong() {
        User user = new User();
        user.setEmail(USER_EMAIL);
        when(repository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        when(repository.findByAccountName("test")).thenReturn(Optional.of(user));

        assertThrows(ElException.class, () -> service.updateProfile("test", "test", "2".repeat(501), "test", "test", USER_EMAIL));
    }

    @Test
    void shouldUploadAvatar() {
        MockMultipartFile file = new MockMultipartFile("file", new byte[] {1, 2, 3, 4});
        User user = new User();
        user.setEmail(USER_EMAIL);
        user.setId(UUID.randomUUID());
        when(repository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        when(s3Utility.uploadFile(file, user.getId().toString())).thenReturn("avatar-name.png");


        service.uploadAvatar(file, USER_EMAIL);

        verify(repository).save(argumentCaptor.capture());
        assertEquals("avatar-name.png", argumentCaptor.getValue().getImages().get("avatar"));
    }

    @Test
    void shouldReturnDefaultUrlToAvatarIfNoAvatarWasUploaded() {
        User user = new User();
        user.setEmail(USER_EMAIL);
        when(repository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));

        String result = this.service.getUrlToAvatar(USER_EMAIL);

        assertEquals("https://cdn.pixabay.com/photo/2023/02/18/11/00/icon-7797704_1280.png", result);
    }

    @Test
    void shouldReturnPresignedUrlToAvatar() {
        User user = new User();
        user.setEmail(USER_EMAIL);
        user.setImage("avatar", "object name");
        when(repository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        when(s3Utility.createPresignedUrl(user.getImages().get("avatar"))).thenReturn("https://some-presigned-url");

        String result = this.service.getUrlToAvatar(USER_EMAIL);

        assertEquals("https://some-presigned-url", result);
    }
}
