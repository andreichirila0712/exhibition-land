package andrei.chirila.prove_yourself.domain.services;

import andrei.chirila.prove_yourself.domain.PasswordChangeToken;

import java.util.UUID;

public interface PasswordChangeTokenService {
    UUID generateToken(String email, String pendingPassword);
    UUID findByEmail(String email);
    PasswordChangeToken findByToken(UUID token);
    void deleteToken(UUID token);
    void deleteByUserEmail(String email);
}
