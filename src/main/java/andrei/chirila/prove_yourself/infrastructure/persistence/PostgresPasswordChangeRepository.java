package andrei.chirila.prove_yourself.infrastructure.persistence;

import andrei.chirila.prove_yourself.domain.PasswordChangeToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PostgresPasswordChangeRepository extends JpaRepository<PasswordChangeToken, Integer> {
    Optional<PasswordChangeToken> findByToken(UUID token);
    Optional<PasswordChangeToken> findByUserEmail(String userEmail);
    void deleteByToken(UUID token);
    void deleteByUserEmail(String email);
}
