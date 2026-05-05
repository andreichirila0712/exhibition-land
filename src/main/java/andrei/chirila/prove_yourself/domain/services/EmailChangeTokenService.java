package andrei.chirila.prove_yourself.domain.services;

import andrei.chirila.prove_yourself.domain.EmailChangeToken;

import java.util.UUID;

public interface EmailChangeTokenService {
    UUID generateToken(String email, String newEmail);
    UUID findByEmail(String email);
    EmailChangeToken findByToken(UUID token);
    void deleteToken(UUID token);
    void deleteByUserEmail(String email);
}
