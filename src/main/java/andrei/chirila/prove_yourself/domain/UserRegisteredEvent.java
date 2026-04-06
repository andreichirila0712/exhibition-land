package andrei.chirila.prove_yourself.domain;

import java.util.UUID;

public class UserRegisteredEvent {
    private String userEmail;
    private UUID activationToken;

    public UserRegisteredEvent(String userEmail, UUID activationToken) {
        this.userEmail = userEmail;
        this.activationToken = activationToken;
    }

    public String getUserEmail() {
        return this.userEmail;
    }

    public UUID getActivationToken() {
        return activationToken;
    }
}
