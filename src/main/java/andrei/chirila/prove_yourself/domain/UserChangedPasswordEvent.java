package andrei.chirila.prove_yourself.domain;

import java.util.UUID;

public class UserChangedPasswordEvent {
    private UUID newPasswordConfirmationToken;
    private String userEmail;

    public UserChangedPasswordEvent(UUID newPasswordConfirmationToken, String userEmail) {
        this.newPasswordConfirmationToken = newPasswordConfirmationToken;
        this.userEmail = userEmail;
    }

    public UUID getNewPasswordConfirmationToken() {
        return newPasswordConfirmationToken;
    }

    public String getUserEmail() {
        return userEmail;
    }
}
