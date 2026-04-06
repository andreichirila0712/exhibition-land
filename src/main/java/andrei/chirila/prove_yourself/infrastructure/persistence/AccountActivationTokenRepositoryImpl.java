package andrei.chirila.prove_yourself.infrastructure.persistence;

import andrei.chirila.prove_yourself.domain.AccountActivationToken;
import andrei.chirila.prove_yourself.domain.AccountActivationTokenRepository;
import andrei.chirila.prove_yourself.domain.exceptions.AccountActivationTokenAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class AccountActivationTokenRepositoryImpl implements AccountActivationTokenRepository {
    private final static Logger logger = LoggerFactory.getLogger(AccountActivationTokenRepositoryImpl.class);
    private final PostgresAccountActivationTokenRepository postgresAccountActivationTokenRepository;

    public AccountActivationTokenRepositoryImpl(PostgresAccountActivationTokenRepository postgresAccountActivationTokenRepository) {
        this.postgresAccountActivationTokenRepository = postgresAccountActivationTokenRepository;
    }

    @Override
    public Optional<AccountActivationToken> findByUserEmail(String email) {
        return postgresAccountActivationTokenRepository.findByUserEmail(email);
    }

    @Override
    public void save(AccountActivationToken activationToken) {
        if (postgresAccountActivationTokenRepository.findByUserEmail(activationToken.getUserEmail()).isPresent()) {
            logger.error("[ACCOUNT ACTIVATION TOKEN] : An account activation token already exists for user with email {}", activationToken.getUserEmail());
            throw new AccountActivationTokenAlreadyExistsException("An account activation token already exists for user with email " + activationToken.getUserEmail());
        }

        postgresAccountActivationTokenRepository.save(activationToken);
    }

    @Override
    public void removeActivationToken(UUID token) {
        postgresAccountActivationTokenRepository.deleteByToken(token);
        logger.info("[ACCOUNT ACTIVATION TOKEN] : Account activation token successfully removed");
    }

    @Override
    public Optional<String> findUserEmailByToken(UUID token) {
        return postgresAccountActivationTokenRepository.findByToken(token).map(AccountActivationToken::getUserEmail);
    }
}
