package andrei.chirila.prove_yourself.domain;

public class UserDeletedAccountEvent {
    private String userEmail;

    public UserDeletedAccountEvent(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserEmail() {
        return userEmail;
    }
}
