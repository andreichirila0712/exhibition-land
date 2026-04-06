package andrei.chirila.prove_yourself.infrastructure.persistence;

import andrei.chirila.prove_yourself.domain.AccountActivationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PostgresAccountActivationTokenRepository extends JpaRepository<AccountActivationToken, Integer> {
    Optional<AccountActivationToken> findByUserEmail(String email);
    Optional<AccountActivationToken> findByToken(UUID token);
    void deleteByToken(UUID token);
}
