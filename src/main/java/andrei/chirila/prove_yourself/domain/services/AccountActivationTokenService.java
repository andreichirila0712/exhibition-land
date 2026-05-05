package andrei.chirila.prove_yourself.domain.services;

import java.util.UUID;

public interface AccountActivationTokenService {
    UUID generateToken(String email);
    UUID findByEmail(String email);
    String findByToken(UUID token);
    void deleteToken(UUID token);
    void deleteByUserEmail(String email);
}
