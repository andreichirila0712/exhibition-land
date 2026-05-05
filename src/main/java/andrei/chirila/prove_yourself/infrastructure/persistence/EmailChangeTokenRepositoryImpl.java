package andrei.chirila.prove_yourself.infrastructure.persistence;

import andrei.chirila.prove_yourself.domain.EmailChangeToken;
import andrei.chirila.prove_yourself.domain.EmailChangeTokenRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class EmailChangeTokenRepositoryImpl implements EmailChangeTokenRepository {
    private final PostgresEmailChangeTokenRepository repository;

    public EmailChangeTokenRepositoryImpl(PostgresEmailChangeTokenRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<EmailChangeToken> findByPendingEmail(String email) {
        return this.repository.findByPendingEmail(email);
    }

    @Override
    public void save(EmailChangeToken changeToken) {
        this.repository.save(changeToken);
    }

    @Override
    public void removeEmailChangeToken(UUID token) {
        this.repository.deleteByToken(token);
    }

    @Override
    public Optional<String> findPendingEmailByToken(UUID token) {
        return this.repository.findByToken(token).map(EmailChangeToken::getPendingEmail);
    }

    @Override
    public Optional<EmailChangeToken> findByToken(UUID token) {
        return this.repository.findByToken(token);
    }

    @Override
    public Optional<EmailChangeToken> findByCurrentEmail(String email) {
        return this.repository.findByCurrentEmail(email);
    }

    @Override
    public void deleteByCurrentEmail(String email) {
        this.repository.deleteByCurrentEmail(email);
    }
}
