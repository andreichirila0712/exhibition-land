package andrei.chirila.prove_yourself.infrastructure.persistence;

import andrei.chirila.prove_yourself.domain.PasswordChangeToken;
import andrei.chirila.prove_yourself.domain.PasswordChangeTokenRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class PasswordChangeTokenRepositoryImpl implements PasswordChangeTokenRepository {
    private final PostgresPasswordChangeRepository repository;

    public PasswordChangeTokenRepositoryImpl(PostgresPasswordChangeRepository repository) {
        this.repository = repository;
    }


    @Override
    public void save(PasswordChangeToken passwordChangeToken) {
        this.repository.save(passwordChangeToken);
    }

    @Override
    public void removePasswordChangeToken(UUID token) {
        this.repository.deleteByToken(token);
    }

    @Override
    public Optional<PasswordChangeToken> findByToken(UUID token) {
        return this.repository.findByToken(token);
    }

    @Override
    public Optional<PasswordChangeToken> findByEmail(String email) {
        return this.repository.findByUserEmail(email);
    }

    @Override
    public void deleteByUserEmail(String email) {
        this.repository.deleteByUserEmail(email);
    }
}
