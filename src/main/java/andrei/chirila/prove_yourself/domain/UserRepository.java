package andrei.chirila.prove_yourself.domain;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findByEmail(String email);
    Optional<User> findByAccountName(String username);
    User save(User user);
    Optional<User> findById(UUID id);
    boolean hasVerifiedEmail(String email);
    void deleteUser(User user);
}
