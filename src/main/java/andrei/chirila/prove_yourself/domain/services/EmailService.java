package andrei.chirila.prove_yourself.domain.services;

import java.util.UUID;

public interface EmailService {
    void sendEmail(String to, UUID token);
}
