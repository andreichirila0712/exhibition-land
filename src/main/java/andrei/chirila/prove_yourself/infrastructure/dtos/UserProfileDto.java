package andrei.chirila.prove_yourself.infrastructure.dtos;

import jakarta.validation.constraints.NotBlank;

public record UserProfileDto(
        @NotBlank String name,
        @NotBlank String username,
        String about,
        String location,
        String website
) {
}
