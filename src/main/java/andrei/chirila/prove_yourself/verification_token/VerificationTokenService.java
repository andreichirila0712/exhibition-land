package andrei.chirila.prove_yourself.verification_token;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class VerificationTokenService {
    private final VerificationTokenRepository repository;

    public VerificationTokenService(VerificationTokenRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void generateTokenForValidation(final String userEmail) {
        VerificationToken token = new VerificationToken();
        LocalDateTime currentTime = LocalDateTime.now();

        token.setUserEmail(userEmail);
        token.setTokenValue(UUID.randomUUID());
        token.setCreationDate(currentTime);
        token.setExpirationDate(currentTime.plusHours(1));

        this.repository.save(token);
    }

    public UUID getVerificationToken(final String userEmail) {
        return this.repository.findTokenValueByUserEmail(userEmail);
    }

    public String getUserEmailFromToken(final UUID token) {
        return this.repository.findUserEmailByTokenValue(token);
    }

    @Transactional
    public void removeToken(final String email) {
        this.repository.removeVerificationTokenByUserEmail(email);
    }
}
