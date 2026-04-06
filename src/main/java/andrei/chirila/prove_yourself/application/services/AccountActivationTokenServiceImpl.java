package andrei.chirila.prove_yourself.application.services;

import andrei.chirila.prove_yourself.domain.AccountActivationToken;
import andrei.chirila.prove_yourself.domain.AccountActivationTokenRepository;
import andrei.chirila.prove_yourself.domain.exceptions.AccountActivationTokenNotFoundException;
import andrei.chirila.prove_yourself.domain.services.AccountActivationTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AccountActivationTokenServiceImpl implements AccountActivationTokenService {
    private final static Logger logger = LoggerFactory.getLogger(AccountActivationTokenServiceImpl.class);
    private final AccountActivationTokenRepository accountActivationTokenRepository;

    public AccountActivationTokenServiceImpl(AccountActivationTokenRepository accountActivationTokenRepository) {
        this.accountActivationTokenRepository = accountActivationTokenRepository;
    }

    @Override
    public UUID generateToken(String email) {
        if (accountActivationTokenRepository.findByUserEmail(email).isPresent()) {
            logger.error("[ACCOUNT ACTIVATION TOKEN] : An account activation token already exists for user with email {}", email);
            throw new RuntimeException("An account activation token already exists for user with email " + email);
        }

        AccountActivationToken activationToken = new AccountActivationToken();

        activationToken.setToken(UUID.randomUUID());
        activationToken.setUserEmail(email);
        LocalDateTime now = LocalDateTime.now();
        activationToken.setCreationDate(now);
        activationToken.setExpirationDate(now.plusHours(6));

        accountActivationTokenRepository.save(activationToken);
        logger.info("[ACCOUNT ACTIVATION TOKEN] : Account activation token successfully generated for user with email {}", email);
        return activationToken.getToken();
    }

    @Override
    public UUID findByEmail(String email) {
        return accountActivationTokenRepository.findByUserEmail(email).map(AccountActivationToken::getToken).orElseThrow(() -> {
            logger.error("[ACCOUNT ACTIVATION TOKEN] : No account activation token found for user with email address {}", email);
            return new AccountActivationTokenNotFoundException("No account activation token found for user with email address " + email);
        });
    }

    @Override
    public String findByToken(UUID token) {
        return accountActivationTokenRepository.findUserEmailByToken(token).orElseThrow(() -> {
            logger.error("[ACCOUNT ACTIVATION TOKEN] : No account activation token found for token {}", token);
            return new AccountActivationTokenNotFoundException("No account activation token found for token " + token);
        });
    }

    @Override
    public void deleteToken(UUID token) {
        accountActivationTokenRepository.removeActivationToken(token);
    }


}
