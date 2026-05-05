package andrei.chirila.prove_yourself.domain;

import java.util.Optional;
import java.util.UUID;

public interface PasswordChangeTokenRepository {
    void save(PasswordChangeToken passwordChangeToken);
    void removePasswordChangeToken(UUID token);
    Optional<PasswordChangeToken> findByToken(UUID token);
    Optional<PasswordChangeToken> findByEmail(String email);
    void deleteByUserEmail(String email);
}
