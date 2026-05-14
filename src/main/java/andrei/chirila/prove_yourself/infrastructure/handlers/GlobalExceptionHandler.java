package andrei.chirila.prove_yourself.infrastructure.handlers;

import andrei.chirila.prove_yourself.domain.exceptions.ElErrorMessage;
import andrei.chirila.prove_yourself.domain.exceptions.ElException;
import andrei.chirila.prove_yourself.infrastructure.dtos.ApiElError;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice(basePackages = "andrei.chirila.prove_yourself.infrastructure.controllers")
public class GlobalExceptionHandler {


    @ExceptionHandler(ElException.class)
    public ResponseEntity<String> handleElException(ElException ex, HttpServletResponse response) {
        final ElErrorMessage errorMessage = ex.getErrorMessage();

        switch (errorMessage) {
            case USER_NOT_FOUND -> {
                return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND)
                        .body(ElErrorMessage.getDescription(ElErrorMessage.USER_NOT_FOUND));
            }

            case USER_ALREADY_VERIFIED -> {
                return ResponseEntity.status(HttpServletResponse.SC_CONFLICT)
                        .body(ElErrorMessage.getDescription(ElErrorMessage.USER_ALREADY_VERIFIED));
            }

            case USER_NOT_VERIFIED -> {
                return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                        .body(ElErrorMessage.getDescription(ElErrorMessage.USER_NOT_VERIFIED));
            }

            case EMAIL_ALREADY_EXISTS -> {
                return ResponseEntity.status(HttpServletResponse.SC_CONFLICT)
                        .body(ElErrorMessage.getDescription(ElErrorMessage.EMAIL_ALREADY_EXISTS));
            }

            case ACCOUNT_ACTIVATION_TOKEN_ALREADY_EXISTS -> {
                return ResponseEntity.status(HttpServletResponse.SC_CONFLICT)
                        .body(ElErrorMessage.getDescription(ElErrorMessage.ACCOUNT_ACTIVATION_TOKEN_ALREADY_EXISTS));
            }

            case ACCOUNT_ACTIVATION_TOKEN_NOT_FOUND -> {
                return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND)
                        .body(ElErrorMessage.getDescription(ElErrorMessage.ACCOUNT_ACTIVATION_TOKEN_NOT_FOUND));
            }

            case EMAIL_CHANGE_TOKEN_NOT_FOUND -> {
                return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND)
                        .body(ElErrorMessage.getDescription(ElErrorMessage.EMAIL_CHANGE_TOKEN_NOT_FOUND));
            }

            case EMAIL_CHANGE_TOKEN_ALREADY_EXISTS -> {
                return ResponseEntity.status(HttpServletResponse.SC_CONFLICT)
                        .body(ElErrorMessage.getDescription(ElErrorMessage.EMAIL_CHANGE_TOKEN_ALREADY_EXISTS));
            }

            case EMAIL_CHANGE_TOKEN_IS_EXPIRED -> {
                return ResponseEntity.status(HttpServletResponse.SC_GONE)
                        .body(ElErrorMessage.getDescription(ElErrorMessage.EMAIL_CHANGE_TOKEN_IS_EXPIRED));
            }

            case PASSWORD_CHANGE_TOKEN_NOT_FOUND -> {
                return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND)
                        .body(ElErrorMessage.getDescription(ElErrorMessage.PASSWORD_CHANGE_TOKEN_NOT_FOUND));
            }

            case PASSWORD_CHANGE_TOKEN_ALREADY_EXISTS -> {
                return ResponseEntity.status(HttpServletResponse.SC_CONFLICT)
                        .body(ElErrorMessage.getDescription(ElErrorMessage.PASSWORD_CHANGE_TOKEN_ALREADY_EXISTS));
            }

            case PASSWORD_CHANGE_TOKEN_IS_EXPIRED -> {
                return ResponseEntity.status(HttpServletResponse.SC_GONE)
                        .body(ElErrorMessage.getDescription(ElErrorMessage.PASSWORD_CHANGE_TOKEN_IS_EXPIRED));
            }

            case BAD_CREDENTIALS -> {
                return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
                        .body(ElErrorMessage.getDescription(ElErrorMessage.BAD_CREDENTIALS));
            }

            case DATA_INTEGRITY_VIOLATION -> {
               return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                       .body(ElErrorMessage.getDescription(ElErrorMessage.DATA_INTEGRITY_VIOLATION));
            }

            case USERNAME_IS_EMPTY -> {
                return ResponseEntity.badRequest()
                        .body(ElErrorMessage.getDescription(ElErrorMessage.USERNAME_IS_EMPTY));
            }

            case USERNAME_ALREADY_EXISTS -> {
                return ResponseEntity.status(HttpServletResponse.SC_CONFLICT)
                        .body(ElErrorMessage.getDescription(ElErrorMessage.USERNAME_ALREADY_EXISTS));
            }

            case NAME_IS_EMPTY -> {
                return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST)
                        .body(ElErrorMessage.getDescription(ElErrorMessage.NAME_IS_EMPTY));
            }

            case ABOUT_IS_TOO_LENGTHY -> {
                return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST)
                        .body(ElErrorMessage.getDescription(ElErrorMessage.ABOUT_IS_TOO_LENGTHY));
            }

            case URL_DOES_NOT_MATCH_PATTERN -> {
                return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST)
                        .body(ElErrorMessage.getDescription(ElErrorMessage.URL_DOES_NOT_MATCH_PATTERN));
            }

            default -> {
                return ResponseEntity.internalServerError()
                        .body(ElErrorMessage.getDescription(ElErrorMessage.INTERNAL_SERVER_ERROR));
            }
        }
    }

    @ExceptionHandler(value = {MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiElError> handleExceptions(MethodArgumentTypeMismatchException ex) {
        final ElErrorMessage errorMessage = ElErrorMessage.BAD_REQUEST;
        final HttpStatus status = ElErrorMessage.getHttpStatus(errorMessage);

        final String paramName = ex.getName();

        String requiredTypeName = "unknown";
        if (ex.getRequiredType() != null) {
            requiredTypeName = ex.getRequiredType().getSimpleName();

        }

        String providerTypeName = "unknown";
        if (ex.getValue() != null) {
            providerTypeName = ex.getValue().getClass().getSimpleName();
        }

        final String message = String.format(
                "Invalid value for parameter '%s'. Expected type: '%s', but got '%s'.",
                paramName, requiredTypeName, providerTypeName);

        final ApiElError body = createApiElError(errorMessage, message);
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiElError> handleValidationExceptions(MethodArgumentNotValidException ex) {
        final Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> {
                    final String fieldName = error.getField();
                    final String errorMessage = error.getDefaultMessage();
                    errors.put(fieldName, errorMessage);
                });

        final ElErrorMessage errorMessage = ElErrorMessage.BAD_REQUEST;
        final HttpStatus status = ElErrorMessage.getHttpStatus(errorMessage);
        final ApiElError body = new ApiElError(errorMessage, errors.toString());

        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiElError> handleGenericException(Exception ex) {
        final ElErrorMessage errorMessage = ElErrorMessage.INTERNAL_SERVER_ERROR;
        final HttpStatus status = ElErrorMessage.getHttpStatus(errorMessage);
        final ApiElError body = createApiElError(errorMessage);

        return ResponseEntity.status(status).body(body);

    }

    private ApiElError createApiElError(ElErrorMessage errorMessage) {
        final String message = ElErrorMessage.getDescription(errorMessage);

        return new ApiElError(errorMessage, message);
    }

    private ApiElError createApiElError(ElErrorMessage errorMessage, String message) {
        return new ApiElError(errorMessage, message);
    }
}
