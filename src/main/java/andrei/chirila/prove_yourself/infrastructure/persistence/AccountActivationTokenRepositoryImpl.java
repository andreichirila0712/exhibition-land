package andrei.chirila.prove_yourself.infrastructure.persistence;

import andrei.chirila.prove_yourself.domain.AccountActivationToken;
import andrei.chirila.prove_yourself.domain.AccountActivationTokenRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class AccountActivationTokenRepositoryImpl implements AccountActivationTokenRepository {
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
        postgresAccountActivationTokenRepository.save(activationToken);
    }

    @Override
    public void removeActivationToken(UUID token) {
        postgresAccountActivationTokenRepository.deleteByToken(token);
    }

    @Override
    public Optional<String> findUserEmailByToken(UUID token) {
        return postgresAccountActivationTokenRepository.findByToken(token).map(AccountActivationToken::getUserEmail);
    }

    @Override
    public void deleteByUserEmail(String email) {
        this.postgresAccountActivationTokenRepository.deleteByUserEmail(email);
    }
}
