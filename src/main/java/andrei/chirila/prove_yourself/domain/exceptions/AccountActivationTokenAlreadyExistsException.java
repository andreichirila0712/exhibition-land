package andrei.chirila.prove_yourself.domain.exceptions;

public class AccountActivationTokenAlreadyExistsException extends RuntimeException {
    public AccountActivationTokenAlreadyExistsException(String message) {
        super(message);
    }
}
