package andrei.chirila.prove_yourself.domain.exceptions;

public class AccountActivationTokenNotFoundException extends RuntimeException {
    public AccountActivationTokenNotFoundException(String message) {
        super(message);
    }
}
