package andrei.chirila.prove_yourself.domain.services;

import andrei.chirila.prove_yourself.domain.AccountActivationToken;

import java.util.UUID;

public interface AccountActivationTokenService {
    UUID generateToken(String email);
    UUID findByEmail(String email);
    String findByToken(UUID token);
    void deleteToken(UUID token);
}
