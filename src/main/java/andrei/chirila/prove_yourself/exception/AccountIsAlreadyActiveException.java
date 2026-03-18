package andrei.chirila.prove_yourself.exception;

public class AccountIsAlreadyActiveException extends RuntimeException {
    public AccountIsAlreadyActiveException(String message) {
        super(message);
    }
}
