package andrei.chirila.prove_yourself.infrastructure.persistence;

import andrei.chirila.prove_yourself.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;

import java.util.Optional;
import java.util.UUID;

public interface PostgresUserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
}
