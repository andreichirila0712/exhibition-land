package andrei.chirila.prove_yourself.domain;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findByEmail(String email);
    User save(User user);
    Optional<User> findById(UUID id);
    boolean hasVerifiedEmail(String email);

//    User findByUsername(String username);
//    boolean existsByUsername(String username);
//    boolean existsByEmail(String email);
//    @NativeQuery("SELECT u.email_verified FROM Users u WHERE u.email = :email")
//    boolean checkActiveStatusByEmail(String email);
}
