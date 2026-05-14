package andrei.chirila.prove_yourself.infrastructure.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserDto(
        @Email @NotBlank String email,
        @NotBlank String name,
        @NotBlank String password,
        @NotBlank String username
        ) {
}
