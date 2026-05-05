package andrei.chirila.prove_yourself.infrastructure.persistence;

import andrei.chirila.prove_yourself.domain.EmailChangeToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PostgresEmailChangeTokenRepository extends JpaRepository<EmailChangeToken, Integer> {
    Optional<EmailChangeToken> findByPendingEmail(String email);
    Optional<EmailChangeToken> findByToken(UUID token);
    Optional<EmailChangeToken> findByCurrentEmail(String email);
    void deleteByToken(UUID token);
    void deleteByCurrentEmail(String email);
}
