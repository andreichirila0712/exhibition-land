package andrei.chirila.prove_yourself.domain.exceptions;

import org.springframework.http.HttpStatus;

import java.util.Arrays;

public enum ElErrorMessage {
    //USER
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),
    USER_ALREADY_VERIFIED(HttpStatus.CONFLICT, "User already verified"),
    USER_NOT_VERIFIED(HttpStatus.INTERNAL_SERVER_ERROR, "User not verified"),
    //EMAIL
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "Email already exists"),
    //ACCOUNT ACTIVATION TOKEN
    ACCOUNT_ACTIVATION_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "Account activation token not found"),
    ACCOUNT_ACTIVATION_TOKEN_ALREADY_EXISTS(HttpStatus.CONFLICT, "Account activation token already exists"),
    //EMAIL CHANGE TOKEN
    EMAIL_CHANGE_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "Email change token not found"),
    EMAIL_CHANGE_TOKEN_ALREADY_EXISTS(HttpStatus.CONFLICT, "Email change token already exists"),
    EMAIL_CHANGE_TOKEN_IS_EXPIRED(HttpStatus.GONE, "Email change token is expired"),
    EMAIL_CHANGE_TOKEN_NO_EMAIL_ASSOCIATED(HttpStatus.INTERNAL_SERVER_ERROR, "Email change token hs no email associated"),
    //PASSWORD CHANGE TOKEN
    PASSWORD_CHANGE_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "Password change token not found"),
    PASSWORD_CHANGE_TOKEN_ALREADY_EXISTS(HttpStatus.CONFLICT, "Password change token already exists"),
    PASSWORD_CHANGE_TOKEN_IS_EXPIRED(HttpStatus.GONE, "Password change token is expired"),
    PASSWORD_CHANGE_TOKEN_NO_USER_ASSOCIATED(HttpStatus.INTERNAL_SERVER_ERROR, "Password change token hs no user associated"),
    //GENERIC
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Something went wrong"),
    BAD_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Invalid credentials"),
    //DATABASE
    DATA_INTEGRITY_VIOLATION(HttpStatus.INTERNAL_SERVER_ERROR, "Delete operation failed");

    private final HttpStatus httpStatus;
    private final String description;

    ElErrorMessage(HttpStatus httpStatus, String description) {
        this.httpStatus = httpStatus;
        this.description = description;
    }

    public static HttpStatus getHttpStatus(ElErrorMessage errorMessage) {
        return Arrays.stream(ElErrorMessage.values())
                .filter(e -> e.equals(errorMessage))
                .findFirst()
                .map(e -> e.httpStatus)
                .orElse(null);
    }

    public static String getDescription(ElErrorMessage errorMessage) {
        return Arrays.stream(ElErrorMessage.values())
                .filter(e -> e.equals(errorMessage))
                .findFirst()
                .map(e -> e.description)
                .orElse(null);
    }

    public static ElErrorMessage getFromHttpStatus(HttpStatus code) {
        return Arrays.stream(ElErrorMessage.values())
                .filter(e -> e.httpStatus.equals(code))
                .findFirst()
                .orElse(INTERNAL_SERVER_ERROR);
    }
}
