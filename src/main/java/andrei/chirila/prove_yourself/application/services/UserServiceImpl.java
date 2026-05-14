package andrei.chirila.prove_yourself.application.services;

import andrei.chirila.prove_yourself.domain.User;
import andrei.chirila.prove_yourself.domain.UserChangedEmailEvent;
import andrei.chirila.prove_yourself.domain.UserChangedPasswordEvent;
import andrei.chirila.prove_yourself.domain.UserDeletedAccountEvent;
import andrei.chirila.prove_yourself.domain.UserRepository;
import andrei.chirila.prove_yourself.domain.exceptions.ElErrorMessage;
import andrei.chirila.prove_yourself.domain.exceptions.ElException;
import andrei.chirila.prove_yourself.domain.services.AccountActivationTokenService;
import andrei.chirila.prove_yourself.domain.services.EmailChangeTokenService;
import andrei.chirila.prove_yourself.domain.services.PasswordChangeTokenService;
import andrei.chirila.prove_yourself.domain.services.UserService;
import andrei.chirila.prove_yourself.infrastructure.dtos.UserSettingsDto;
import andrei.chirila.prove_yourself.infrastructure.storage.S3Utility;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final EmailChangeTokenService emailChangeTokenService;
    private final PasswordChangeTokenService passwordChangeTokenService;
    private final AccountActivationTokenService accountActivationTokenService;
    private final PasswordEncoder passwordEncoder;
    private final S3Utility s3Utility;
    private final ApplicationEventPublisher publisher;


    public UserServiceImpl(UserRepository userRepository, EmailChangeTokenService emailChangeTokenService, PasswordChangeTokenService passwordChangeTokenService, AccountActivationTokenService accountActivationTokenService, PasswordEncoder passwordEncoder, S3Utility s3Utility, ApplicationEventPublisher publisher) {
        this.userRepository = userRepository;
        this.emailChangeTokenService = emailChangeTokenService;
        this.passwordChangeTokenService = passwordChangeTokenService;
        this.accountActivationTokenService = accountActivationTokenService;
        this.passwordEncoder = passwordEncoder;
        this.s3Utility = s3Utility;
        this.publisher = publisher;
    }

    @Override
    public UserSettingsDto getUserSettings(String email) {
        Optional<User> user  = this.userRepository.findByEmail(email);

        if (user.isEmpty()) {
            logger.error("[USER] : User with email {} not found", email);
            throw new ElException(ElErrorMessage.USER_NOT_FOUND);
        }

        return new UserSettingsDto(
                user.get().getLanguage(),
                user.get().getTheme(),
                user.get().getDateFormat(),
                user.get().getProfileVisibility(),
                user.get().getProfileDiscoverable());
    }

    @Override
    public void updateLanguage(String email, String language) {
        this.userRepository.findByEmail(email).ifPresent(user -> {
            user.setLanguage(language);
            this.userRepository.save(user);
        });
        logger.info("[USER] : User with email {} successfully updated language to {}", email, language);
    }

    @Override
    public void updateTheme(String email, String theme) {
        this.userRepository.findByEmail(email).ifPresent(user -> {
            user.setTheme(theme);
            this.userRepository.save(user);
        });
        logger.info("[USER] : User with email {} successfully updated theme to {}", email, theme);
    }

    @Override
    public void updateDateFormat(String email, String dateFormat) {
        this.userRepository.findByEmail(email).ifPresent(user -> {
            user.setDateFormat(dateFormat);
            this.userRepository.save(user);
        });
        logger.info("[USER] : User with email {} successfully updated date format to {}", email, dateFormat);
    }

    @Override
    public void updateVisibility(String email, String visibility) {
        this.userRepository.findByEmail(email).ifPresent(user -> {
            user.setProfileVisibility(visibility);
            this.userRepository.save(user);
        });
        logger.info("[USER] : User with email {} successfully updated profile visibility to {}", email, visibility);
    }

    @Override
    @Transactional
    public void changeEmail(String email, String newEmail) {
        if (this.userRepository.findByEmail(newEmail).isPresent()) {
            logger.error("[USER] : User with email {} already exists", newEmail);
            throw new ElException(ElErrorMessage.EMAIL_ALREADY_EXISTS);
        }

        UUID newEmailConfirmationToken = this.emailChangeTokenService.generateToken(email, newEmail);
        logger.info("[EMAIL CHANGE TOKEN] : Email change token successfully generated for email {}", newEmail);
        this.publisher.publishEvent(new UserChangedEmailEvent(newEmail, newEmailConfirmationToken));
    }

    @Override
    @Transactional
    public void changePassword(String email, String newPassword, String inputPassword) {
        if (!checkPassword(email, inputPassword)) {
            logger.error("[PASSWORD CHANGE TOKEN] : Password change token not generated, user attempted to bypass validation");
            throw new ElException(ElErrorMessage.BAD_CREDENTIALS);
        }

        UUID newPasswordChangeToken = this.passwordChangeTokenService.generateToken(email, newPassword);
        logger.info("[PASSWORD CHANGE TOKEN] : Password change token successfully generated for user {}", email);

        this.publisher.publishEvent(new UserChangedPasswordEvent(newPasswordChangeToken, email));
    }

    @Override
    public boolean checkPassword(String email, String password) {
        return this.userRepository.findByEmail(email).filter(u -> passwordEncoder.matches(password, u.getPassword())).isPresent();
    }

    @Override
    @Transactional
    public void deleteAccount(String email) {
        this.emailChangeTokenService.deleteByUserEmail(email);
        this.passwordChangeTokenService.deleteByUserEmail(email);
        this.accountActivationTokenService.deleteByUserEmail(email);

        User user = this.userRepository.findByEmail(email).orElseThrow(() -> new ElException(ElErrorMessage.USER_NOT_FOUND));
        if (user.getImages() != null && user.getImages().get("avatar") != null) {
            this.s3Utility.deleteFile(user.getImages().get("avatar"));
        }

        this.userRepository.deleteUser(user);


        publisher.publishEvent(new UserDeletedAccountEvent(email));
        logger.info("[USER] : User with email {} successfully deleted their account", email);
    }

    @Override
    @Transactional
    public void updateProfile(String name, String username, String about, String location, String website, String email) {
        User user = this.userRepository.findByEmail(email).orElseThrow(() -> new ElException(ElErrorMessage.USER_NOT_FOUND));

        if (name.isBlank()) {
            logger.error("[USER] : User with email {} sent an invalid name", email);
            throw new ElException(ElErrorMessage.NAME_IS_EMPTY);
        }
        user.setName(name);

        if (username.isBlank()) {
            logger.error("[USER] : User with email {} sent an invalid username", email);
            throw new ElException(ElErrorMessage.USERNAME_IS_EMPTY);
        }

        boolean usernameTaken = this.userRepository.findByAccountName(username)
                .filter(u -> !Objects.equals(u.getEmail(), email))
                .isPresent();

        if (usernameTaken) {
            logger.error("[USER] : Username {} already exists", username);
            throw new ElException(ElErrorMessage.USERNAME_ALREADY_EXISTS);
        }
        user.setAccountName(username);

        if (about.length() > 500) {
            logger.error("[USER] : User's about is too lengthy");
            throw new ElException(ElErrorMessage.ABOUT_IS_TOO_LENGTHY);
        }
        user.setAbout(about);
        user.setLocation(location);
        user.setWebsite(website);
        this.userRepository.save(user);
    }

    @Override
    @Transactional
    public void uploadAvatar(MultipartFile avatar, String email) {
        User user = this.userRepository.findByEmail(email).orElseThrow(() -> new ElException(ElErrorMessage.USER_NOT_FOUND));

        if (user.getImages() != null && user.getImages().get("avatar") != null) {
            this.s3Utility.deleteFile(user.getImages().get("avatar"));
        }

        String key = this.s3Utility.uploadFile(avatar, user.getId().toString());
        logger.info("[USER] : User with email {} successfully uploaded avatar", email);
        user.setImage("avatar", key);
        this.userRepository.save(user);
    }

    @Override
    public String getUrlToAvatar(String email) {
        User user = this.userRepository.findByEmail(email).orElseThrow(() -> new ElException(ElErrorMessage.USER_NOT_FOUND));
        if (user.getImages() == null || user.getImages().isEmpty()) {
            return "https://cdn.pixabay.com/photo/2023/02/18/11/00/icon-7797704_1280.png";
        }

        return this.s3Utility.createPresignedUrl(user.getImages().get("avatar"));
    }

    @Override
    public User getUser(String email) {
        return this.userRepository.findByEmail(email).orElseThrow(() -> new ElException(ElErrorMessage.USER_NOT_FOUND));
    }
}
