package andrei.chirila.prove_yourself.verification_token;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface VerificationTokenRepository extends CrudRepository<VerificationToken, Long> {
    @NativeQuery("SELECT t.token_value FROM Verification_Token t WHERE t.user_email = ?1")
    UUID findTokenValueByUserEmail(String email);
    @NativeQuery("SELECT t.user_email FROM Verification_Token t WHERE t.token_value = ?1")
    String findUserEmailByTokenValue(UUID token);
    void removeVerificationTokenByUserEmail(String email);
}
