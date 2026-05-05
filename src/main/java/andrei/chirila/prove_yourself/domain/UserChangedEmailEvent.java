package andrei.chirila.prove_yourself.domain;

import java.util.UUID;

public class UserChangedEmailEvent {
    private String userNewEmail;
    private UUID newEmailConfirmationToken;

    public UserChangedEmailEvent(String userNewEmail, UUID newEmailConfirmationToken) {
        this.userNewEmail = userNewEmail;
        this.newEmailConfirmationToken = newEmailConfirmationToken;
    }

    public String getUserNewEmail() {
        return userNewEmail;
    }

    public UUID getNewEmailConfirmationToken() {
        return newEmailConfirmationToken;
    }
}
