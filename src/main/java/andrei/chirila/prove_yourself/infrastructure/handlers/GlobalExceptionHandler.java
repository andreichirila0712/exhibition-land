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
    public String handleElException(ElException ex, HttpServletResponse response) {
        final ElErrorMessage errorMessage = ex.getErrorMessage();

        switch (errorMessage) {
            case USER_NOT_FOUND -> {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);

                return "fragments/shared :: nothing"; // TODO: implement when switched to user branch
            }

            case USER_ALREADY_VERIFIED -> {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                return "fragment/shared :: nothing";
            }

            case USER_NOT_VERIFIED -> {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return "fragment/shared :: nothing";
            }

            case EMAIL_ALREADY_EXISTS -> {
                response.setStatus(HttpServletResponse.SC_CONFLICT);

                return "fragments/modals :: email-sent-error-modal";
            }

            case ACCOUNT_ACTIVATION_TOKEN_ALREADY_EXISTS -> {
                response.setStatus(HttpServletResponse.SC_CONFLICT);

                return "fragments/shared :: nothing";
            }

            case ACCOUNT_ACTIVATION_TOKEN_NOT_FOUND -> {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);

                return "fragments/shared :: nothing";
            }

            case EMAIL_CHANGE_TOKEN_NOT_FOUND -> {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);

                return "fragments/modals :: email-token-not-found";
            }

            case EMAIL_CHANGE_TOKEN_ALREADY_EXISTS -> {
                response.setStatus(HttpServletResponse.SC_CONFLICT);

                return "fragments/modals :: email-token-conflict";
            }

            case EMAIL_CHANGE_TOKEN_IS_EXPIRED -> {
                response.setStatus(HttpServletResponse.SC_GONE);

                return "fragments/modals :: email-token-expired";
            }

            case EMAIL_CHANGE_TOKEN_NO_EMAIL_ASSOCIATED -> {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // TODO: has to go once cascade deletion...

                return "fragments/modals :: email-token-internal";
            }

            case PASSWORD_CHANGE_TOKEN_NOT_FOUND -> {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);

                return "fragments/modals :: password-token-not-found";
            }

            case PASSWORD_CHANGE_TOKEN_ALREADY_EXISTS -> {
                response.setStatus(HttpServletResponse.SC_CONFLICT);

                return "fragments/modals :: password-token-conflict";
            }

            case PASSWORD_CHANGE_TOKEN_IS_EXPIRED -> {
                response.setStatus(HttpServletResponse.SC_GONE);

                return "fragments/modals :: password-token-expired";
            }

            case PASSWORD_CHANGE_TOKEN_NO_USER_ASSOCIATED -> {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // TODO: has to be deleted once the cascade delete is done

                return "fragments/modals :: password-token-internal";
            }

            case BAD_CREDENTIALS -> {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                return "fragments/modals :: password-token-unauthorized";
            }

            case DATA_INTEGRITY_VIOLATION -> {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

                return "fragments/modals :: db-operation-failed";
            }

            default -> {
                response.setHeader("HX-Trigger", "generalError");
                return "";
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
