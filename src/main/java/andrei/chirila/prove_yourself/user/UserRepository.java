package andrei.chirila.prove_yourself.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    User findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    @NativeQuery("SELECT u.email_verified FROM Users u WHERE u.email = :email")
    boolean checkActiveStatusByEmail(String email);
    User findByEmail(String email);
}
