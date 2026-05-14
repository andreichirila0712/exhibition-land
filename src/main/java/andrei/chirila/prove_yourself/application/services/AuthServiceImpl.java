package andrei.chirila.prove_yourself.application.services;

import andrei.chirila.prove_yourself.application.mappers.AuthMapper;
import andrei.chirila.prove_yourself.domain.EmailChangeToken;
import andrei.chirila.prove_yourself.domain.PasswordChangeToken;
import andrei.chirila.prove_yourself.domain.Role;
import andrei.chirila.prove_yourself.domain.User;
import andrei.chirila.prove_yourself.domain.UserRegisteredEvent;
import andrei.chirila.prove_yourself.domain.UserRepository;
import andrei.chirila.prove_yourself.domain.exceptions.ElErrorMessage;
import andrei.chirila.prove_yourself.domain.exceptions.ElException;
import andrei.chirila.prove_yourself.domain.services.AccountActivationTokenService;
import andrei.chirila.prove_yourself.domain.services.AuthService;
import andrei.chirila.prove_yourself.domain.services.EmailChangeTokenService;
import andrei.chirila.prove_yourself.domain.services.PasswordChangeTokenService;
import andrei.chirila.prove_yourself.domain.services.TokenService;
import andrei.chirila.prove_yourself.infrastructure.dtos.CreateUserDto;
import andrei.chirila.prove_yourself.infrastructure.dtos.LoginRequestDto;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService, UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final ApplicationEventPublisher publisher;
    private final AccountActivationTokenService activationTokenService;
    private final PasswordChangeTokenService passwordChangeTokenService;
    private final EmailChangeTokenService emailChangeTokenService;

    public AuthServiceImpl(UserRepository userRepository, TokenService tokenService, PasswordEncoder passwordEncoder, AuthenticationConfiguration authenticationConfiguration, ApplicationEventPublisher publisher, AccountActivationTokenService activationTokenService, PasswordChangeTokenService passwordChangeTokenService, EmailChangeTokenService emailChangeTokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationConfiguration = authenticationConfiguration;
        this.publisher = publisher;
        this.activationTokenService = activationTokenService;
        this.passwordChangeTokenService = passwordChangeTokenService;
        this.emailChangeTokenService = emailChangeTokenService;
    }

    @Override
    @Transactional
    public void createUser(final CreateUserDto createUserDto) {
        final User createUser = AuthMapper.fromDto(createUserDto);
        createUser.setAccountName(createUserDto.username());
        createUser.setPassword(passwordEncoder.encode(createUserDto.password()));
        createUser.setEmailVerified(false);
        createUser.setRole(Role.USER);
        createUser.setLanguage("english");
        createUser.setTheme("light");
        createUser.setDateFormat("MM/DD/YYYY");
        createUser.setProfileVisibility("private");
        createUser.setProfileDiscoverable("no");
        final User user = userRepository.save(createUser);
        logger.info("[USER] : User successfully created with id {}", user.getId());
        UUID activationToken = activationTokenService.generateToken(user.getEmail());
        publisher.publishEvent(new UserRegisteredEvent(user.getEmail(), activationToken));
    }

    @Override
    public User getUser(final UUID id) {
        return userRepository.findById(id).orElseThrow(() -> {
                    logger.error("[USER] : User with id {} not found", id);
                    return new ElException(ElErrorMessage.USER_NOT_FOUND);
                });
    }

    @Override
    @Transactional
    public void activateAccount(UUID activationToken) {
        Optional<User> user = userRepository.findByEmail(activationTokenService.findByToken(activationToken));
        if (user.isEmpty()) {
            logger.error("[ACCOUNT ACTIVATION TOKEN]: User with email {} doesn't have a generated activation token", activationTokenService.findByToken(activationToken));
            throw new ElException(ElErrorMessage.ACCOUNT_ACTIVATION_TOKEN_NOT_FOUND);
        }

        if (user.get().isEmailVerified()) {
            logger.error("[USER] : User with id {} is already active", user.get().getId());
            throw new ElException(ElErrorMessage.USER_ALREADY_VERIFIED);
        }

        user.get().setEmailVerified(true);
        activationTokenService.deleteToken(activationToken);
        logger.info("[USER] : User with id {} successfully activated", user.get().getId());
    }

    @Override
    public String login(final LoginRequestDto loginRequestDto) {
        Optional<User> user = userRepository.findByEmail(loginRequestDto.email());
        if (user.isEmpty()) {
            logger.error("[USER] : Error while trying to login, no account is associated with the email {}", loginRequestDto.email());
            throw new ElException(ElErrorMessage.USER_NOT_FOUND);
        }

        if (!user.get().isEmailVerified()) {
            logger.error("[USER] : Error while trying to login, user not verified", new ElException(ElErrorMessage.USER_NOT_VERIFIED));
            throw new ElException(ElErrorMessage.USER_NOT_VERIFIED);
        }

        if (!passwordEncoder.matches(loginRequestDto.password(), user.get().getPassword())) {
            logger.error("[USER] : Error while trying to login, bad credentials", new BadCredentialsException("Bad credentials"));
            throw new ElException(ElErrorMessage.BAD_REQUEST);
        }

        try {
            final AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();
            final Authentication authRequest = AuthMapper.fromDto(loginRequestDto);
            final Authentication authentication = authenticationManager.authenticate(authRequest);

            return tokenService.generateToken(authentication);
        } catch (Exception ex) {
            logger.error("[USER] : Error while trying to login", ex);
            throw new ProviderNotFoundException("Error while trying to login");
        }
    }

    @Override
    public boolean validateToken(final String token) {
        return tokenService.validateToken(token);
    }

    @Override
    public String getUserFromToken(final String token) {
        return tokenService.getUserFromToken(token);
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    logger.error("[USER] : User not found with email {}", username);
                    return new ElException(ElErrorMessage.BAD_CREDENTIALS);
                });
    }

    @Override
    @Transactional
    public void confirmChangedPassword(UUID token) {
        PasswordChangeToken passwordChangeToken = this.passwordChangeTokenService.findByToken(token);

        if (passwordChangeToken.getExpirationDate().isBefore(LocalDateTime.now(ZoneOffset.UTC))) {
            logger.error("[PASSWORD CHANGE TOKEN] : Password change token {} is expired", token);
            throw new ElException(ElErrorMessage.PASSWORD_CHANGE_TOKEN_IS_EXPIRED);
        }

        this.userRepository.findByEmail(passwordChangeToken.getUserEmail())
                        .ifPresent(u -> {
                            u.setPassword(passwordChangeToken.getPendingPassword());
                            this.userRepository.save(u);
                        });
        logger.info("[USER]: User with email {} successfully changed their password", passwordChangeToken.getUserEmail());
        this.passwordChangeTokenService.deleteToken(token);
    }

    @Override
    @Transactional
    public void confirmChangedEmail(UUID token) {
        EmailChangeToken emailChangeToken = this.emailChangeTokenService.findByToken(token);

        if (emailChangeToken.getExpirationDate().isBefore(LocalDateTime.now(ZoneOffset.UTC))) {
            logger.error("[EMAIL CHANGE TOKEN] : Email change token {} is expired", token);
            throw new ElException(ElErrorMessage.EMAIL_CHANGE_TOKEN_IS_EXPIRED);
        }

        this.userRepository.findByEmail(emailChangeToken.getCurrentEmail())
                .ifPresent(u -> {
                    u.setEmail(emailChangeToken.getPendingEmail());
                    this.userRepository.save(u);
                });
        logger.info("[USER] : User with email {} successfully changed email to {}", emailChangeToken.getCurrentEmail(), emailChangeToken.getPendingEmail());
        this.emailChangeTokenService.deleteToken(token);
    }
}
