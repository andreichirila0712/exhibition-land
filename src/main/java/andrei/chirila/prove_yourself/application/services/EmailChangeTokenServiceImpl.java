package andrei.chirila.prove_yourself.application.services;

import andrei.chirila.prove_yourself.domain.EmailChangeToken;
import andrei.chirila.prove_yourself.domain.EmailChangeTokenRepository;
import andrei.chirila.prove_yourself.domain.exceptions.ElErrorMessage;
import andrei.chirila.prove_yourself.domain.exceptions.ElException;
import andrei.chirila.prove_yourself.domain.services.EmailChangeTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmailChangeTokenServiceImpl implements EmailChangeTokenService {
    private static final Logger logger = LoggerFactory.getLogger(EmailChangeTokenServiceImpl.class);
    private final EmailChangeTokenRepository repository;

    public EmailChangeTokenServiceImpl(EmailChangeTokenRepository repository) {
        this.repository = repository;
    }

    @Override
    public UUID generateToken(String email, String newEmail) {
        Optional<EmailChangeToken> emailChangeToken = this.repository.findByCurrentEmail(email);

        if (emailChangeToken.isPresent() && !emailChangeToken.get().getExpirationDate().isBefore(LocalDateTime.now(ZoneOffset.UTC))) {
            logger.error("[EMAIL CHANGE TOKEN] : An email change token already exists for email {}", email);
            throw new ElException(ElErrorMessage.EMAIL_CHANGE_TOKEN_ALREADY_EXISTS);
        }

        EmailChangeToken newEmailChangeToken = new EmailChangeToken();

        newEmailChangeToken.setToken(UUID.randomUUID());
        newEmailChangeToken.setCurrentEmail(email);
        newEmailChangeToken.setPendingEmail(newEmail);
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        newEmailChangeToken.setCreationDate(now);
        newEmailChangeToken.setExpirationDate(now.plusMinutes(30));

        this.repository.save(newEmailChangeToken);
        logger.info("[EMAIL CHANGE TOKEN] : Email change token successfully generated for email {}", email);
        return newEmailChangeToken.getToken();
    }

    @Override
    public UUID findByEmail(String email) {
        return this.repository.findByPendingEmail(email).map(EmailChangeToken::getToken).orElseThrow(() -> {
                    logger.error("[EMAIL CHANGE TOKEN] : No email change token found for email {}", email);
                    return new ElException(ElErrorMessage.EMAIL_CHANGE_TOKEN_NOT_FOUND);
                });
    }

    @Override
    public EmailChangeToken findByToken(UUID token) {
        return this.repository.findByToken(token).orElseThrow(() -> {
           logger.error("[EMAIL CHANGE TOKEN] : No email change token found for token {}", token);
           return new ElException(ElErrorMessage.EMAIL_CHANGE_TOKEN_NOT_FOUND);
        });
    }

    @Override
    public void deleteToken(UUID token) {
        this.repository.removeEmailChangeToken(token);
        logger.info("[EMAIL CHANGE TOKEN] : Email change token {} successfully deleted", token);
    }

    @Override
    public void deleteByUserEmail(String email) {
        this.repository.deleteByCurrentEmail(email);
        logger.info("[EMAIL CHANGE TOKEN] : Password change token successfully deleted for user {}", email);
    }
}
