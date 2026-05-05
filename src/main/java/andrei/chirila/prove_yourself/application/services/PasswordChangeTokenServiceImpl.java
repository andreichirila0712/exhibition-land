package andrei.chirila.prove_yourself.application.services;

import andrei.chirila.prove_yourself.domain.PasswordChangeToken;
import andrei.chirila.prove_yourself.domain.PasswordChangeTokenRepository;
import andrei.chirila.prove_yourself.domain.exceptions.ElErrorMessage;
import andrei.chirila.prove_yourself.domain.exceptions.ElException;
import andrei.chirila.prove_yourself.domain.services.PasswordChangeTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordChangeTokenServiceImpl implements PasswordChangeTokenService {
    private static final Logger logger = LoggerFactory.getLogger(PasswordChangeTokenServiceImpl.class);
    private final PasswordChangeTokenRepository repository;
    private final PasswordEncoder passwordEncoder;

    public PasswordChangeTokenServiceImpl(PasswordChangeTokenRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UUID generateToken(String email, String pendingPassword) {
        Optional<PasswordChangeToken> passwordChangeToken = this.repository.findByEmail(email);

        if (passwordChangeToken.isPresent() && !passwordChangeToken.get().getExpirationDate().isBefore(LocalDateTime.now(ZoneOffset.UTC))) {
            logger.error("[PASSWORD CHANGE TOKEN] : A password change token already exists for user with email {}", email);
            throw new ElException(ElErrorMessage.PASSWORD_CHANGE_TOKEN_ALREADY_EXISTS);
        }

        PasswordChangeToken newPasswordChangeToken = new PasswordChangeToken();

        newPasswordChangeToken.setToken(UUID.randomUUID());
        newPasswordChangeToken.setUserEmail(email);
        newPasswordChangeToken.setPendingPassword(passwordEncoder.encode(pendingPassword));
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        newPasswordChangeToken.setCreationDate(now);
        newPasswordChangeToken.setExpirationDate(now.plusMinutes(30));

        this.repository.save(newPasswordChangeToken);
        logger.info("[PASSWORD CHANGE TOKEN] : Password change token successfully generated for user {}", email);
        return newPasswordChangeToken.getToken();
    }

    @Override
    public UUID findByEmail(String email) {
        return this.repository.findByEmail(email).map(PasswordChangeToken::getToken).orElseThrow(() -> {
           logger.error("[PASSWORD CHANGE TOKEN] : No token found for user {}", email);
           return new ElException(ElErrorMessage.PASSWORD_CHANGE_TOKEN_NOT_FOUND);
        });
    }

    @Override
    public PasswordChangeToken findByToken(UUID token) {
        return this.repository.findByToken(token).orElseThrow(() -> {
            logger.error("[PASSWORD CHANGE TOKEN] : No password change token found with token {}", token);
            return new ElException(ElErrorMessage.PASSWORD_CHANGE_TOKEN_NOT_FOUND);
        });
    }

    @Override
    public void deleteToken(UUID token) {
        this.repository.removePasswordChangeToken(token);
        logger.info("[PASSWORD CHANGE TOKEN] : Password change token {} successfully deleted", token);
    }

    @Override
    public void deleteByUserEmail(String email) {
        this.repository.deleteByUserEmail(email);
        logger.info("[PASSWORD CHANGE TOKEN] : Password change token successfully delete for user {}", email);
    }
}
