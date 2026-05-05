package andrei.chirila.prove_yourself.domain;

import java.util.Optional;
import java.util.UUID;

public interface AccountActivationTokenRepository {
    Optional<AccountActivationToken> findByUserEmail(String email);
    void save(AccountActivationToken activationToken);
    void removeActivationToken(UUID token);
    Optional<String> findUserEmailByToken(UUID token);
    void deleteByUserEmail(String email);
}
