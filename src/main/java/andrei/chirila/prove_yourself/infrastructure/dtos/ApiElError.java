package andrei.chirila.prove_yourself.infrastructure.dtos;

import andrei.chirila.prove_yourself.domain.exceptions.ElErrorMessage;

public record ApiElError(ElErrorMessage code, String message) {
}
