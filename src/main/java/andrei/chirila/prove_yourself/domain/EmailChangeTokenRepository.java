package andrei.chirila.prove_yourself.domain;

import java.util.Optional;
import java.util.UUID;

public interface EmailChangeTokenRepository {
    Optional<EmailChangeToken> findByPendingEmail(String email);
    void save(EmailChangeToken changeToken);
    void removeEmailChangeToken(UUID token);
    Optional<String> findPendingEmailByToken(UUID token);
    Optional<EmailChangeToken> findByToken(UUID token);
    Optional<EmailChangeToken> findByCurrentEmail(String email);
    void deleteByCurrentEmail(String email);
}
